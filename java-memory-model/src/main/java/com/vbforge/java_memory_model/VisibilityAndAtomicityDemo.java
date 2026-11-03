package com.vbforge.java_memory_model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Visibility vs atomicity — the two separate problems hiding behind "thread-safety".
 *
 * <ul>
 *   <li><b>Visibility</b>: when thread B writes a variable, will thread A ever <i>see</i> the new value?</li>
 *   <li><b>Atomicity</b>: can another thread observe (or interleave with) a half-finished compound action?</li>
 * </ul>
 *
 * {@code volatile} fixes the first problem only. {@code synchronized} and the
 * {@code java.util.concurrent.atomic} classes fix both.
 *
 * <p><b>Honesty note:</b> visibility bugs depend on the JIT, the CPU and the OS scheduler.
 * The "broken" demos below <i>may</i> misbehave on your machine — or may not. The tests therefore
 * assert only what the Java Memory Model <i>guarantees</i> (e.g. "synchronized never loses an
 * update"), never "the bug was observed".</p>
 */
public class VisibilityAndAtomicityDemo {

    public static void main(String[] args) {
        System.out.println("=== 1. Visibility: volatile vs plain stop flag ===");
        System.out.println("volatile flag -> worker stopped: " + volatileFlagStopsWorker(100));
        System.out.println("plain flag    -> worker stopped by flag (may be false!): " + plainFlagStopsWorker(300));
        System.out.println("---");

        System.out.println("=== 2. Atomicity: count++ is three steps ===");
        int threads = 4;
        int perThread = 100_000;
        long expected = (long) threads * perThread;
        System.out.println("expected              = " + expected);
        System.out.println("volatile long ++      = " + volatileIncrement(threads, perThread) + "  (may be LESS than expected; depends on cores and JIT)");
        System.out.println("synchronized ++       = " + synchronizedIncrement(threads, perThread));
        System.out.println("AtomicLong.increment  = " + atomicIncrement(threads, perThread));
        System.out.println("---");

        System.out.println("=== 3. Atomic compound actions (CAS) ===");
        System.out.println(atomicCompoundActionsDemo());
    }

    // ═════════════════════════════════════════════════════════════════════
    // Small helpers (package-private so the tests can reuse them)
    // ═════════════════════════════════════════════════════════════════════

    static void runConcurrently(int threads, Runnable body) {
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(body);
            workers[i].start();
        }
        for (Thread worker : workers) {
            joinQuietly(worker, 0);
        }
    }

    static void joinQuietly(Thread thread, long millis) {
        try {
            thread.join(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while joining " + thread.getName(), e);
        }
    }

    static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while sleeping", e);
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    // 1. VISIBILITY
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   Without synchronization, a write made by one thread is NOT guaranteed
    //         to ever become visible to another thread. Each core has caches and
    //         store buffers, and the JIT is free to keep a field in a register.
    // Why:    The classic symptom is a "stop flag" loop that never stops. The JIT may
    //         legally hoist the read of a plain field out of the loop:
    //             while (!stop) { spins++; }   ->   if (!stop) { while (true) { spins++; } }
    // Fix:    Declare the flag `volatile`. A volatile write happens-before every later
    //         volatile read of the same field, and the JIT may not hoist the read.
    // Cost:   volatile read/write are O(1) but prevent some optimisations and reordering.
    // Gotcha: "It worked on my laptop" proves nothing — an interpreter-only run
    //         (short loop, no JIT yet) often hides the bug. The worker needs time to get
    //         JIT-compiled, which is why the plain demo waits a few hundred milliseconds.

    /** Worker that spins on a PLAIN field. Data race: the loop may never see {@code stop == true}. */
    static class PlainFlagWorker implements Runnable {
        boolean stop;   // NOT volatile - deliberately broken
        long spins;

        @Override
        public void run() {
            // The interrupt check is only a safety net so this demo can always clean up after itself.
            while (!stop && !Thread.currentThread().isInterrupted()) {
                spins++;
            }
        }
    }

    /** Worker that spins on a VOLATILE field. Guaranteed to see the update. */
    static class VolatileFlagWorker implements Runnable {
        volatile boolean stop;
        long spins;

        @Override
        public void run() {
            while (!stop) {
                spins++;
            }
        }
    }

    /**
     * Starts a worker, lets it spin for {@code runMillis}, flips a volatile flag and joins.
     *
     * @return {@code true} if the worker terminated — guaranteed by the JMM
     */
    static boolean volatileFlagStopsWorker(long runMillis) {
        VolatileFlagWorker task = new VolatileFlagWorker();
        Thread worker = new Thread(task, "volatile-flag-worker");
        worker.setDaemon(true);
        worker.start();

        sleepQuietly(runMillis);
        task.stop = true;               // volatile write -> visible to the worker's next read

        joinQuietly(worker, 5_000);
        return !worker.isAlive();
    }

    /**
     * Same experiment with a PLAIN flag. The result is <b>not</b> deterministic:
     * {@code false} means "the worker never noticed the flag and had to be interrupted".
     *
     * @return {@code true} if the worker stopped by itself within 500 ms of the flag write
     */
    static boolean plainFlagStopsWorker(long waitMillis) {
        PlainFlagWorker task = new PlainFlagWorker();
        Thread worker = new Thread(task, "plain-flag-worker");
        worker.setDaemon(true);
        worker.start();

        sleepQuietly(waitMillis);       // give the JIT a chance to compile the loop
        task.stop = true;               // plain write - no visibility guarantee

        joinQuietly(worker, 500);
        boolean stoppedByFlag = !worker.isAlive();
        if (!stoppedByFlag) {
            worker.interrupt();         // rescue the thread so it does not burn a CPU forever
            joinQuietly(worker, 5_000);
        }
        return stoppedByFlag;
    }

    // ═════════════════════════════════════════════════════════════════════
    // 2. ATOMICITY
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   `value++` compiles to read -> add -> write. Two threads can both read 5
    //         and both write 6: one increment is lost.
    // Why:    `volatile` makes each individual read and write visible, but it does NOT
    //         make the three-step sequence indivisible. Visibility != atomicity.
    // Fixes:  - synchronized / Lock : one thread at a time, O(1) uncontended, blocks under contention
    //         - AtomicLong (CAS)    : lock-free, retries on conflict, no blocking
    // Gotcha: Making a field volatile and then "fixing" a race with it is a very common
    //         concurrency mistake. The loss is timing-dependent: a run that happens to print the
    //         full total proves nothing — which is why the tests only assert the guaranteed side.

    /** Volatile counter: visible, but {@code value++} is still a read-modify-write race. */
    static class VolatileCounter {
        volatile long value;

        void increment() {
            value++;    // read, add, write - NOT atomic
        }
    }

    /** Lock-protected counter: atomic and visible. */
    static class SynchronizedCounter {
        private long value;

        synchronized void increment() {
            value++;
        }

        synchronized long get() {
            return value;
        }
    }

    /**
     * @return the observed total — always between 1 and {@code threads * incrementsPerThread};
     *         can be less than the maximum when increments are lost (not guaranteed on every run)
     */
    static long volatileIncrement(int threads, int incrementsPerThread) {
        VolatileCounter counter = new VolatileCounter();
        runConcurrently(threads, () -> {
            for (int i = 0; i < incrementsPerThread; i++) {
                counter.increment();
            }
        });
        return counter.value;
    }

    /** @return exactly {@code threads * incrementsPerThread} — guaranteed */
    static long synchronizedIncrement(int threads, int incrementsPerThread) {
        SynchronizedCounter counter = new SynchronizedCounter();
        runConcurrently(threads, () -> {
            for (int i = 0; i < incrementsPerThread; i++) {
                counter.increment();
            }
        });
        return counter.get();
    }

    /** @return exactly {@code threads * incrementsPerThread} — guaranteed */
    static long atomicIncrement(int threads, int incrementsPerThread) {
        AtomicLong counter = new AtomicLong();
        runConcurrently(threads, () -> {
            for (int i = 0; i < incrementsPerThread; i++) {
                counter.incrementAndGet();
            }
        });
        return counter.get();
    }

    // ═════════════════════════════════════════════════════════════════════
    // 3. ATOMIC COMPOUND ACTIONS (compare-and-set)
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   compareAndSet(expected, update) atomically does:
    //             if (value == expected) { value = update; return true; } else return false;
    // Why:    It is the building block for every lock-free algorithm: read the current
    //         value, compute the new one, and CAS — if somebody else won, loop and retry.
    // Cost:   O(1) per attempt; under heavy contention the retry loop can spin.
    // Gotcha: A check-then-act written as `if (a.get() < limit) a.incrementAndGet()` is
    //         still a race — each call is atomic, the PAIR is not.

    /**
     * Single-threaded walk through the CAS family so every return value is predictable.
     *
     * @return an ordered map describing what each call returned
     */
    static Map<String, Object> atomicCompoundActionsDemo() {
        AtomicInteger value = new AtomicInteger(0);
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("firstCas", value.compareAndSet(0, 5));        // 0 == 0 -> swapped to 5
        result.put("secondCas", value.compareAndSet(0, 6));       // value is 5, not 0 -> refused
        result.put("updateAndGet", value.updateAndGet(v -> v * 2)); // 5 -> 10, returns NEW value
        result.put("getAndAdd", value.getAndAdd(3));              // returns OLD value (10), now 13
        result.put("final", value.get());
        return result;
    }
}
