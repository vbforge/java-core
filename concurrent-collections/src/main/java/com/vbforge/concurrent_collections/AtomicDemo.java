package com.vbforge.concurrent_collections;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * Demo: the {@code java.util.concurrent.atomic} classes — lock-free, thread-safe
 * single variables built on compare-and-set (CAS).
 *
 * <p>Every claim in a comment is either printed by {@link #main(String[])} or
 * asserted in {@code AtomicDemoTest}. Races are forced with a
 * {@link CyclicBarrier} so the outcome is the same on every run.
 */
public class AtomicDemo {

    public static void main(String[] args) throws InterruptedException {
        atomicIntegerBasicsDemo();
        System.out.println("---");
        lostUpdateDemo();
        System.out.println("---");
        casLoopDemo();
        System.out.println("---");
        atomicBooleanDemo();
        System.out.println("---");
        atomicReferenceDemo();
        System.out.println("---");
        abaProblemDemo();
        System.out.println("---");
        longAdderDemo();
        System.out.println("---");
        atomicArrayDemo();
    }

    // =========================================================================
    // 1. AtomicInteger basics
    // =========================================================================

    /*
     * WHAT
     *   AtomicInteger (and AtomicLong) wrap one number and offer read-modify-write
     *   methods that are each ONE atomic step:
     *     incrementAndGet()   add 1, return the NEW value        (++i)
     *     getAndIncrement()   add 1, return the OLD value        (i++)
     *     addAndGet(d)        add d, return the NEW value
     *     getAndSet(v)        set v, return the OLD value
     *     compareAndSet(e, v) set v ONLY IF the current value is e; returns success
     *     updateAndGet(f)     apply f, return the NEW value
     *     getAndUpdate(f)     apply f, return the OLD value
     *     accumulateAndGet(x, f)   new = f(current, x), return the NEW value
     *
     * WHY
     *   i++ on a plain int is three steps (read, add, write). Atomics do it in
     *   one, without a lock: the CPU's compare-and-swap instruction does the work.
     *
     * COMPLEXITY
     *   - every operation: O(1), never blocks
     *
     * GOTCHA
     *   Mixing up the "...AndGet" and "getAnd..." forms is the classic
     *   off-by-one: incrementAndGet returns the new value, getAndIncrement the
     *   old one.
     */
    public static void atomicIntegerBasicsDemo() {
        AtomicInteger value = new AtomicInteger(5);

        System.out.println("incrementAndGet()        -> " + value.incrementAndGet() + " (new value)");
        System.out.println("getAndIncrement()        -> " + value.getAndIncrement() + " (old value), now " + value.get());
        System.out.println("addAndGet(3)             -> " + value.addAndGet(3));
        System.out.println("getAndSet(0)             -> " + value.getAndSet(0) + " (old value), now " + value.get());
        System.out.println("compareAndSet(0, 42)     -> " + value.compareAndSet(0, 42));
        System.out.println("compareAndSet(0, 99)     -> " + value.compareAndSet(0, 99) + ", still " + value.get());
        System.out.println("updateAndGet(x -> x * 2) -> " + value.updateAndGet(x -> x * 2));
        System.out.println("getAndUpdate(x -> x + 1) -> " + value.getAndUpdate(x -> x + 1) + " (old), now " + value.get());
        System.out.println("accumulateAndGet(5, sum) -> " + value.accumulateAndGet(5, Integer::sum));
    }

    // =========================================================================
    // 2. volatile is not enough: the lost update
    // =========================================================================

    /** A counter that is {@code volatile} - visible to every thread, but NOT atomic. */
    static class VolatileCounter {
        volatile int value;
    }

    /*
     * WHAT
     *   "volatile int count; count++" still loses updates: volatile guarantees
     *   that threads SEE the latest value, but count++ is read, add, write -
     *   and two threads can read the same old value.
     *
     * WHY
     *   This is the single most common reason to reach for an atomic class.
     *   volatile fixes visibility; atomics fix visibility AND atomicity.
     *
     * HOW THE DEMO WORKS
     *   A CyclicBarrier(2) between the read and the write makes both threads
     *   read 0 before either writes 1. The lost update happens every run.
     *   Then 4 threads x 10,000 incrementAndGet() calls always total 40,000.
     *
     * COMPLEXITY
     *   - incrementAndGet: O(1) - under heavy contention it may retry (section 3)
     *
     * GOTCHA
     *   The broken version would pass most single-threaded tests. Races show up
     *   only under concurrency - which is why this demo forces one.
     */
    public static void lostUpdateDemo() throws InterruptedException {
        VolatileCounter broken = new VolatileCounter();
        CyclicBarrier bothHaveRead = new CyclicBarrier(2);
        runInParallel(2, i -> {
            int seen = broken.value;          // read
            bothHaveRead.await();              // ...both threads now hold the old value 0
            broken.value = seen + 1;           // write
        });
        System.out.println("volatile, 2 increments   -> " + broken.value + " (one update was lost)");

        AtomicInteger safe = new AtomicInteger();
        runInParallel(4, i -> {
            for (int n = 0; n < 10_000; n++) {
                safe.incrementAndGet();
            }
        });
        System.out.println("AtomicInteger, 4 x 10000 -> " + safe.get());
    }

    // =========================================================================
    // 3. The CAS retry loop
    // =========================================================================

    /*
     * WHAT
     *   Every "update" method of an atomic is a loop around compareAndSet:
     *     1. read the current value
     *     2. compute the new value from it
     *     3. compareAndSet(current, new) - if another thread changed the value
     *        in the meantime this returns false, so go back to step 1
     *
     * WHY
     *   It is how you build ANY atomic update that has no ready-made method,
     *   without taking a lock. Understanding the loop explains the rule that
     *   the function you pass to updateAndGet must be side-effect free: it may
     *   be called more than once.
     *
     * COMPLEXITY
     *   - O(1) per attempt; under heavy contention a thread may retry many times
     *
     * GOTCHA
     *   Read the current value ONCE per attempt and use that same variable in
     *   compareAndSet. Calling get() again inside the loop body breaks the
     *   guarantee.
     */
    public static void casLoopDemo() throws InterruptedException {
        AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);
        runInParallel(4, thread -> {
            for (int i = 0; i < 100; i++) {
                atomicMaxWithCasLoop(max, thread * 100 + i);
            }
        });
        System.out.println("max of 0..399 via CAS loop -> " + max.get());

        AtomicInteger sameMax = new AtomicInteger(Integer.MIN_VALUE);
        runInParallel(4, thread -> {
            for (int i = 0; i < 100; i++) {
                sameMax.accumulateAndGet(thread * 100 + i, Math::max);
            }
        });
        System.out.println("max of 0..399 via accumulateAndGet -> " + sameMax.get());
    }

    /**
     * Raises {@code target} to {@code candidate} if (and only if) that is larger,
     * using a hand-written CAS loop. Returns the value the target holds afterwards.
     */
    public static int atomicMaxWithCasLoop(AtomicInteger target, int candidate) {
        while (true) {
            int current = target.get();                    // 1. read
            if (candidate <= current) {
                return current;                            // nothing to do
            }
            if (target.compareAndSet(current, candidate)) { // 3. publish, only if nobody changed it
                return candidate;
            }
            // somebody else won the race: loop and try again with a fresh read
        }
    }

    // =========================================================================
    // 4. AtomicBoolean: do something exactly once
    // =========================================================================

    /*
     * WHAT
     *   compareAndSet(false, true) on an AtomicBoolean succeeds for exactly ONE
     *   of any number of racing threads.
     *
     * WHY
     *   It is a lock-free "first one wins" switch: one-time initialisation,
     *   "only one thread may start the cleanup", cancel-once flags.
     *
     * COMPLEXITY
     *   - O(1)
     *
     * GOTCHA
     *   The losing threads return immediately - they do NOT wait for the winner
     *   to finish. If they need the result of the initialisation, use a latch,
     *   a Future or a lock instead.
     */
    public static void atomicBooleanDemo() throws InterruptedException {
        AtomicBoolean started = new AtomicBoolean(false);
        AtomicInteger winners = new AtomicInteger();
        runInParallel(8, i -> {
            if (started.compareAndSet(false, true)) {
                winners.incrementAndGet();
            }
        });
        System.out.println("8 threads race compareAndSet(false, true) -> winners = " + winners.get()
                + ", flag = " + started.get());
    }

    // =========================================================================
    // 5. AtomicReference: compare-and-set uses == (identity)
    // =========================================================================

    /*
     * WHAT
     *   AtomicReference<T> holds an object reference and offers the same
     *   compareAndSet / getAndSet / updateAndGet family.
     *
     * WHY
     *   Combined with IMMUTABLE objects it gives you a lock-free "current
     *   state": build the new state from the old one and swap the reference.
     *
     * COMPLEXITY
     *   - O(1) for the swap itself; copying the state is up to you
     *
     * GOTCHA
     *   compareAndSet compares references with ==, NOT with equals().
     *   Two equal Strings that are different objects do not match, and neither
     *   do boxed Integers outside the small cache (-128..127). If your code
     *   needs "equal value" semantics, read the current reference first and
     *   pass THAT reference as the expected value.
     */
    public static void atomicReferenceDemo() throws InterruptedException {
        String original = new String("state");
        String lookalike = new String("state");
        AtomicReference<String> reference = new AtomicReference<>(original);
        System.out.println("equals() but different object -> " + original.equals(lookalike));
        System.out.println("compareAndSet(lookalike, ...) -> " + reference.compareAndSet(lookalike, "next"));
        System.out.println("compareAndSet(original, ...)  -> " + reference.compareAndSet(original, "next"));

        Integer big = 1000;
        AtomicReference<Integer> boxed = new AtomicReference<>(big);
        System.out.println("boxed 1000, other instance    -> " + boxed.compareAndSet(Integer.valueOf(1000), 2000));
        System.out.println("boxed 1000, same instance     -> " + boxed.compareAndSet(big, 2000));

        AtomicReference<List<String>> snapshot = new AtomicReference<List<String>>(new ArrayList<String>());
        runInParallel(4, thread -> {
            for (int i = 0; i < 100; i++) {
                final String item = thread + "-" + i;
                snapshot.updateAndGet(old -> {
                    List<String> next = new ArrayList<>(old);   // never mutate the old state
                    next.add(item);
                    return next;
                });
            }
        });
        System.out.println("immutable-state updates       -> " + snapshot.get().size() + " items");
    }

    // =========================================================================
    // 6. The ABA problem
    // =========================================================================

    /*
     * WHAT
     *   CAS only asks "is the value still A?". If the value went A -> B -> A in
     *   the meantime, the answer is "yes" although the state changed twice.
     *   AtomicStampedReference pairs the reference with an int "stamp" that you
     *   bump on every change, so A(stamp 0) and A(stamp 2) are different.
     *
     * WHY
     *   For counters and flags ABA is harmless. For lock-free data structures
     *   (stacks, queues that recycle nodes) it can corrupt the structure.
     *
     * COMPLEXITY
     *   - O(1); each stamped reference allocates a small pair object
     *
     * GOTCHA
     *   Most application code never needs a stamped reference. Know that the
     *   problem exists; reach for the tool only when your CAS logic depends on
     *   the history of a value, not just its current content.
     */
    public static void abaProblemDemo() {
        AtomicReference<String> plain = new AtomicReference<>("A");
        String remembered = plain.get();               // the "A" we based our decision on
        plain.set("B");                                // another thread: A -> B ...
        plain.set("A");                                // ... and B -> A
        boolean plainSucceeded = plain.compareAndSet(remembered, "C");
        System.out.println("plain CAS after A->B->A    -> " + plainSucceeded + " (it cannot tell)");

        AtomicStampedReference<String> stamped = new AtomicStampedReference<>("A", 0);
        int rememberedStamp = stamped.getStamp();      // 0
        stamped.compareAndSet("A", "B", 0, 1);         // another thread: A -> B
        stamped.compareAndSet("B", "A", 1, 2);         // ... and B -> A
        boolean stampedSucceeded = stamped.compareAndSet("A", "C", rememberedStamp, rememberedStamp + 1);
        System.out.println("stamped CAS after A->B->A  -> " + stampedSucceeded
                + " (stamp is now " + stamped.getStamp() + ")");
    }

    // =========================================================================
    // 7. LongAdder / LongAccumulator
    // =========================================================================

    /*
     * WHAT
     *   LongAdder is a counter for HIGH CONTENTION: instead of one variable
     *   that every thread fights over, it keeps several cells and adds them up
     *   when you call sum(). LongAccumulator generalises it to any associative
     *   function (max, min, product, ...).
     *
     * WHY
     *   With dozens of threads hammering one AtomicLong, most CAS attempts
     *   fail and retry. Spreading the updates over several cells removes most
     *   of that contention - ideal for statistics and metrics.
     *
     * COMPLEXITY
     *   - increment / add: O(1), little contention
     *   - sum(): O(number of cells)
     *
     * GOTCHA
     *   increment() returns void - you cannot ask "what is the value after MY
     *   increment?". If you need that (ids, sequence numbers) use AtomicLong.
     *   Also, sum() is not an atomic snapshot while other threads are still
     *   updating; read it once the threads are done.
     */
    public static void longAdderDemo() throws InterruptedException {
        LongAdder hits = new LongAdder();
        LongAccumulator highest = new LongAccumulator(Math::max, Long.MIN_VALUE);
        runInParallel(8, thread -> {
            for (int i = 0; i < 5_000; i++) {
                hits.increment();
                highest.accumulate(thread * 5_000L + i);
            }
        });
        System.out.println("8 threads x 5000 increments -> sum = " + hits.sum());
        System.out.println("LongAccumulator(max)        -> " + highest.get());
        System.out.println("sumThenReset()              -> " + hits.sumThenReset() + ", sum afterwards = " + hits.sum());
    }

    // =========================================================================
    // 8. AtomicIntegerArray
    // =========================================================================

    /*
     * WHAT
     *   AtomicIntegerArray (and AtomicLongArray, AtomicReferenceArray) gives
     *   every ELEMENT of an array the atomic operations: incrementAndGet(i),
     *   compareAndSet(i, expect, update), ...
     *
     * WHY
     *   A plain int[] shared between threads has the same lost-update problem
     *   in every cell, and declaring the array volatile only makes the array
     *   REFERENCE volatile, not its elements.
     *
     * COMPLEXITY
     *   - O(1) per element operation
     *
     * GOTCHA
     *   The atomic array copies the array you give it. Writes to the original
     *   int[] afterwards are not visible through the atomic array, and vice versa.
     */
    public static void atomicArrayDemo() throws InterruptedException {
        AtomicIntegerArray buckets = new AtomicIntegerArray(3);
        runInParallel(4, thread -> {
            for (int i = 0; i < 1_000; i++) {
                for (int bucket = 0; bucket < buckets.length(); bucket++) {
                    buckets.incrementAndGet(bucket);
                }
            }
        });
        System.out.println("4 threads x 1000 per bucket -> " + buckets);

        int[] source = {1, 2, 3};
        AtomicIntegerArray copy = new AtomicIntegerArray(source);
        source[0] = 99;
        System.out.println("source changed after copy   -> source[0] = " + source[0] + ", atomic[0] = " + copy.get(0));
    }

    // =========================================================================
    // Helper: start N threads together, wait for all of them
    // =========================================================================

    @FunctionalInterface
    private interface Body {
        void run(int threadIndex) throws Exception;
    }

    private static void runInParallel(int threads, Body body) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int index = i;
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    body.run(index);
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                }
            }, "worker-" + i);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
        if (failure.get() != null) {
            throw new IllegalStateException("a worker thread failed", failure.get());
        }
    }
}
