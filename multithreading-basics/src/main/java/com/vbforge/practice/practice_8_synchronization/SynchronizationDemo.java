// IMPLEMENTATION
package com.vbforge.practice.practice_8_synchronization;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the Java synchronization model — the primary tool for mutual exclusion
 * and memory visibility in multithreaded programs.
 *
 * The core problem — race condition:
 *   When two threads read-modify-write shared state without coordination, the result
 *   depends on thread scheduling order. This is non-deterministic and a bug.
 *
 *   Example: counter++ is NOT atomic. It compiles to three steps:
 *     1. READ  counter from memory
 *     2. ADD   1
 *     3. WRITE result back
 *   Two threads can interleave these steps and lose increments.
 *
 * The synchronized keyword provides TWO guarantees:
 *   1. Mutual exclusion  — only one thread executes the block at a time (atomicity).
 *   2. Memory visibility — on exit, all writes are flushed to main memory;
 *                          on entry, the thread reads fresh values (happens-before).
 *
 * Forms of synchronized:
 *   a) synchronized instance method   → locks on `this`
 *   b) synchronized static method     → locks on the Class object
 *   c) synchronized(object) block     → locks on the specified object (most flexible)
 *
 * Intrinsic lock (monitor):
 *   Every Java object has a built-in lock (monitor).
 *   Only one thread holds the lock at a time; others block in BLOCKED state.
 *   The lock is reentrant — the same thread can re-acquire it without deadlocking.
 *
 * Pitfalls:
 *   - Locking on different objects gives NO mutual exclusion.
 *   - Locking on a publicly accessible object lets external code lock it too.
 *   - Synchronizing every method on `this` coarsens granularity unnecessarily.
 *   - Deadlock: thread A holds lock-1 and waits for lock-2;
 *               thread B holds lock-2 and waits for lock-1.
 *     Prevention: always acquire locks in a consistent, global order.
 */
public class SynchronizationDemo {

    // ── 1. Unsafe counter — race condition ────────────────────────────────────

    /**
     * NOT thread-safe. counter++ is read-modify-write, which is not atomic.
     * Multiple threads will lose increments.
     */
    static class UnsafeCounter {
        int count = 0;                      // package-private for test visibility

        void increment() { count++; }       // race condition here
        int get()        { return count; }
    }

    // ── 2. Synchronized method — locks on `this` ──────────────────────────────

    /**
     * Thread-safe via synchronized instance method.
     * The lock object is `this`, so all callers of the same instance serialize.
     */
    public static class SynchronizedCounter {
        private int count = 0;

        // The entire method is the critical section — only one thread at a time.
        public synchronized void increment() { count++; }
        public synchronized int  get()       { return count; }
    }

    // ── 3. Synchronized block — fine-grained lock ─────────────────────────────

    /**
     * Uses a dedicated private lock object instead of locking on `this`.
     * Benefits:
     *   - External code cannot accidentally lock on the same monitor.
     *   - Multiple independent locks can coexist within one class.
     */
    static class FineGrainedCounter {
        private int count = 0;
        private final Object lock = new Object();   // private, dedicated lock

        public void increment() {
            synchronized (lock) {
                count++;
            }
        }

        public int get() {
            synchronized (lock) {
                return count;
            }
        }
    }

    // ── 4. Synchronized static method — locks on Class object ─────────────────

    /**
     * Static synchronized methods lock on {@code SynchronizationDemo.class},
     * NOT on any instance. All threads share one class-level lock.
     */
    static class StaticCounter {
        private static int instancesCreated = 0;

        StaticCounter() {
            register();
        }

        // Locks on StaticCounter.class — shared across all instances
        public static synchronized void register()     { instancesCreated++; }
        public static synchronized int  getTotal()     { return instancesCreated; }
        public static synchronized void reset()        { instancesCreated = 0; }
    }

    // ── 5. Reentrant lock demonstration ──────────────────────────────────────

    /**
     * A thread that already holds the intrinsic lock can re-acquire it (reentrant).
     * Without reentrancy, calling a synchronized method from another
     * synchronized method on the same instance would deadlock.
     */
    static class ReentrantDemo {
        public synchronized String outer() {
            return "outer[" + inner() + "]";   // calls synchronized inner() — no deadlock
        }

        public synchronized String inner() {
            return "inner";
        }
    }

    // ── 6. Collecting results from multiple threads ───────────────────────────

    /**
     * Demonstrates that synchronized on a shared list prevents data corruption
     * when multiple threads append to it concurrently.
     */
    static class SafeAccumulator {
        private final List<Integer> results = new ArrayList<>();
        private final Object        lock    = new Object();

        public void add(int value) {
            synchronized (lock) {
                results.add(value);
            }
        }

        public int size() {
            synchronized (lock) {
                return results.size();
            }
        }
    }

    // ── 7. Race condition solution — increment/decrement threads on shared data ─

    /**
     * Shared value touched by two different threads: one only increments,
     * the other only decrements. Both methods are synchronized on `this`,
     * so after equal numbers of increments and decrements the value is always 0.
     */
    static class MyData {

        private int value;

        public synchronized void increment(){
            value++;
        }

        public synchronized void decrement(){
            value--;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    static class IncrementThread extends Thread {

        private final MyData myData;

        public IncrementThread(MyData myData) {
            this.myData = myData;
        }

        @Override
        public void run() {
            System.out.println("IncrementThread started");

            for (int i = 1; i <= 100_000; i++) {
                myData.increment();
            }

            System.out.println("IncrementThread finished");
        }
    }

    static class DecrementThread extends Thread {

        private final MyData myData;

        public DecrementThread(MyData myData) {
            this.myData = myData;
        }

        @Override
        public void run() {
            System.out.println("DecrementThread started");

            for (int i = 1; i <= 100_000; i++) {
                myData.decrement();
            }

            System.out.println("DecrementThread finished");
        }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Synchronization Demo ===\n");
        final int THREADS    = 5;
        final int INCREMENTS = 1_000;

        // --- 1. Unsafe counter (race condition visible) ---
        UnsafeCounter unsafe = new UnsafeCounter();
        runIncrementThreads(THREADS, INCREMENTS, unsafe::increment);
        System.out.printf("Unsafe   count (expected %d) = %d  ← likely wrong!%n",
                THREADS * INCREMENTS, unsafe.get());

        // --- 2. Synchronized method ---
        SynchronizedCounter synced = new SynchronizedCounter();
        runIncrementThreads(THREADS, INCREMENTS, synced::increment);
        System.out.printf("Synced   count (expected %d) = %d%n",
                THREADS * INCREMENTS, synced.get());

        // --- 3. Fine-grained block ---
        FineGrainedCounter fine = new FineGrainedCounter();
        runIncrementThreads(THREADS, INCREMENTS, fine::increment);
        System.out.printf("Fine     count (expected %d) = %d%n",
                THREADS * INCREMENTS, fine.get());

        // --- 4. Static synchronized ---
        StaticCounter.reset();
        System.out.println("\nStatic counter: " + StaticCounter.getTotal());
        new StaticCounter();
        new StaticCounter();
        new StaticCounter();
        System.out.println("After 3 new instances: " + StaticCounter.getTotal());

        // --- 5. Reentrant ---
        System.out.println("\nReentrant result: " + new ReentrantDemo().outer());

        // --- 6. Safe accumulator ---
        SafeAccumulator acc = new SafeAccumulator();
        runIncrementThreads(THREADS, INCREMENTS, () -> acc.add(1));
        System.out.printf("%nAccumulator size (expected %d) = %d%n",
                THREADS * INCREMENTS, acc.size());

        // --- 7. Race condition solution: IncrementThread + DecrementThread on MyData ---
        System.out.println("\n-- MyData (increment/decrement threads) --");
        MyData myData = new MyData();

        Thread incrementThread = new IncrementThread(myData);
        Thread decrementThread = new DecrementThread(myData);

        incrementThread.start();
        decrementThread.start();

        try{
            incrementThread.join();
            decrementThread.join();
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        System.out.println("MyData value (expected 0) = " + myData);
    }

    /** Spawns {@code nThreads} threads, each calling {@code task} {@code times} times. */
    private static void runIncrementThreads(int nThreads, int times, Runnable task)
            throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < times; j++) task.run();
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();
    }
}