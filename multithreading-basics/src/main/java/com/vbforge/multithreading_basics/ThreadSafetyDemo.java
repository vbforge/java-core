package com.vbforge.multithreading_basics;

import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * What "thread-safe" means and the main strategies to get there: statelessness,
 * immutability, thread confinement, atomic variables, and atomic compound actions.
 *
 * Run: mvn exec:java -Dexec.mainClass="com.vbforge.multithreading_basics.ThreadSafetyDemo"
 */
public class ThreadSafetyDemo {

    public static void main(String[] args) throws Exception {
        statelessDemo();
        System.out.println("---");
        immutabilityDemo();
        System.out.println("---");
        threadConfinementDemo();
        System.out.println("---");
        atomicVariablesDemo();
        System.out.println("---");
        compoundActionDemo();
        System.out.println("---");
        lazyInitDemo();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 0. Definition
    //
    // A class is thread-safe if it behaves correctly when used from several threads,
    // WITHOUT extra synchronization from the callers. Shared mutable state is the
    // only source of trouble, so there are four ways out:
    //   1) do not share it (stateless / thread confinement)
    //   2) do not mutate it (immutability)
    //   3) guard every access with ONE lock (synchronized)
    //   4) use atomic variables for single-variable updates
    // ─────────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────────
    // 1. Stateless code
    //
    // What:  a method that only uses its parameters and local variables.
    // Why:   locals live on the thread's own stack, so no two threads ever share them.
    // ─────────────────────────────────────────────────────────────────────
    public static long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    static void statelessDemo() throws InterruptedException {
        long[] results = new long[3];
        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            final int slot = i;
            threads[i] = new Thread(() -> results[slot] = factorial(10));
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("factorial(10) on 3 threads: " + results[0] + ", " + results[1] + ", " + results[2]);
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. Immutability
    //
    // What:  all fields final, the class is final, no setters; "changes" return a new object.
    // Why:   an object that cannot change cannot be seen in an inconsistent state, so it can
    //        be shared freely with no locks. (Why final fields are also safely visible to
    //        other threads is explained in the java-memory-model module.)
    // Gotcha: a final field that points to a mutable object (e.g. a List) is NOT immutable.
    // ─────────────────────────────────────────────────────────────────────
    public static final class ImmutablePoint {
        private final int x;
        private final int y;

        public ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public ImmutablePoint withX(int newX) {
            return new ImmutablePoint(newX, y);
        }
    }

    static void immutabilityDemo() {
        ImmutablePoint p = new ImmutablePoint(1, 2);
        ImmutablePoint moved = p.withX(10);
        System.out.println("original x=" + p.getX() + ", moved x=" + moved.getX());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. Thread confinement with ThreadLocal
    //
    // What:  ThreadLocal gives every thread its own copy of a variable.
    // Why:   SimpleDateFormat keeps mutable internal state and its Javadoc says it is not
    //        synchronized. Instead of locking one shared instance, give each thread its own.
    // Gotcha: in thread pools threads live long; call remove() when a value must not
    //        outlive the task, otherwise you leak memory or leak data between tasks.
    // ─────────────────────────────────────────────────────────────────────
    public static final ThreadLocal<SimpleDateFormat> FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    static void threadConfinementDemo() throws InterruptedException {
        SimpleDateFormat mine = FORMAT.get();
        final SimpleDateFormat[] other = new SimpleDateFormat[1];
        Thread t = new Thread(() -> other[0] = FORMAT.get());
        t.start();
        t.join();
        System.out.println("same instance on another thread? " + (mine == other[0]));
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. Atomic variables
    //
    // What:  AtomicInteger/AtomicLong/AtomicReference perform read-modify-write as ONE
    //        indivisible step using a hardware compare-and-swap (CAS), without a lock.
    // Why:   fixes the lost-update race for single variables and never blocks.
    // Complexity: O(1) per operation; under heavy contention CAS may retry.
    // ─────────────────────────────────────────────────────────────────────
    static void atomicVariablesDemo() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        SynchronizationDemo.hammer(4, 100_000, counter::incrementAndGet);
        System.out.println("atomic counter = " + counter.get());
        System.out.println("compareAndSet(400000, 0) = " + counter.compareAndSet(400_000, 0));
        System.out.println("compareAndSet(400000, 0) again = " + counter.compareAndSet(400_000, 0));
    }

    // ─────────────────────────────────────────────────────────────────────
    // 5. Compound actions (check-then-act)
    //
    // What:  "if the counter is below the limit, increment it" is two operations. Each one
    //        is thread-safe on its own, but between them another thread can sneak in.
    // Why:   composing thread-safe operations does NOT give a thread-safe operation.
    // Fix:   make the whole check-and-update atomic: a compareAndSet retry loop (below),
    //        or one synchronized block.
    // ─────────────────────────────────────────────────────────────────────
    /** Racy: the limit can be exceeded. */
    public static boolean incrementIfBelowUnsafe(AtomicInteger counter, int limit) {
        if (counter.get() < limit) {
            counter.incrementAndGet();
            return true;
        }
        return false;
    }

    /** Safe: CAS retry loop; the limit is never exceeded. */
    public static boolean incrementIfBelowSafe(AtomicInteger counter, int limit) {
        while (true) {
            int current = counter.get();
            if (current >= limit) {
                return false;
            }
            if (counter.compareAndSet(current, current + 1)) {
                return true;
            }
        }
    }

    static void compoundActionDemo() throws InterruptedException {
        AtomicInteger unsafe = new AtomicInteger();
        AtomicInteger safe = new AtomicInteger();
        SynchronizationDemo.hammer(8, 1_000, () -> incrementIfBelowUnsafe(unsafe, 1_000));
        SynchronizationDemo.hammer(8, 1_000, () -> incrementIfBelowSafe(safe, 1_000));
        System.out.println("limit 1000 -> unsafe: " + unsafe.get() + ", safe: " + safe.get());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 6. Lazy initialization is a check-then-act too
    //
    // What:  `if (instance == null) instance = new X();` lets two threads both see null.
    // Fix:   synchronize the check and the creation together.
    // Note:  the double-checked locking optimisation needs volatile and is covered in the
    //        java-memory-model module.
    // ─────────────────────────────────────────────────────────────────────
    public static class LazyHolder {
        private final AtomicInteger creations = new AtomicInteger();
        private Object instance;

        public synchronized Object get() {
            if (instance == null) {
                creations.incrementAndGet();
                instance = new Object();
            }
            return instance;
        }

        public int getCreations() {
            return creations.get();
        }
    }

    static void lazyInitDemo() throws InterruptedException {
        LazyHolder holder = new LazyHolder();
        SynchronizationDemo.hammer(8, 1_000, holder::get);
        System.out.println("instances created: " + holder.getCreations());
    }
}
