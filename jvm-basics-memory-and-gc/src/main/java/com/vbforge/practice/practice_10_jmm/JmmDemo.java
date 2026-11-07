package com.vbforge.practice.practice_10_jmm;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java Memory Model (JMM) — the rules governing how threads see each other's writes.
 *
 * Problem: Modern CPUs and compilers reorder instructions for performance.
 * Without explicit rules, thread A's write might not be visible to thread B,
 * even if A wrote before B read (in wall-clock time).
 *
 * JMM solution: the HAPPENS-BEFORE (HB) relation.
 *
 * If action A happens-before action B:
 *   → B is guaranteed to see ALL effects of A (and everything A happened-before)
 *
 * Built-in happens-before rules (JLS §17.4.5):
 *
 *   1. Program Order Rule
 *      Each action in a thread happens-before every subsequent action in that thread.
 *      (Single-thread ordering is preserved.)
 *
 *   2. Monitor Lock Rule
 *      unlock(m) happens-before subsequent lock(m) on the same monitor.
 *
 *   3. Volatile Variable Rule
 *      A write to a volatile field happens-before every subsequent read of that field.
 *
 *   4. Thread Start Rule
 *      Thread.start() happens-before every action in the started thread.
 *
 *   5. Thread Termination Rule
 *      Every action in a thread happens-before Thread.join() returns (in another thread).
 *
 *   6. Transitivity
 *      If A hb B and B hb C, then A hb C.
 *
 *   7. Default Value Rule
 *      Writing of default values (0, null, false) to fields happens-before
 *      the first action in any thread.
 *
 * Common JMM violations:
 *   • Visibility bug     — thread reads stale value from its CPU cache
 *   • Atomicity bug      — read-modify-write isn't atomic (e.g., i++)
 *   • Ordering bug       — instructions reordered across threads
 *
 * Fixes:
 *   • volatile           — visibility + HB guarantee (not atomicity for compound ops)
 *   • synchronized       — mutual exclusion + full memory barrier (visibility + ordering)
 *   • AtomicXxx          — CAS-based, lock-free atomic operations
 *   • java.util.concurrent constructs (CountDownLatch, etc.) — implicit HB guarantees
 *
 * Double-Checked Locking — correct pattern (Java 5+):
 *   volatile instance field + synchronized initialization block
 *   Without volatile: the object reference can be published before the object
 *   is fully constructed (partial initialization visible to other threads).
 */
public class JmmDemo {

    // -----------------------------------------------------------------------
    // 1. Visibility — volatile ensures all threads see the latest write
    // -----------------------------------------------------------------------

    /**
     * Without volatile: 'running' could be cached in the reading thread's
     * CPU register and never see the write from the stopping thread.
     * The loop might run forever (observed on server JVMs with -server flag).
     *
     * With volatile: every write is flushed to main memory;
     *               every read fetches from main memory (no CPU-cache stale read).
     */
    private volatile boolean running = true;

    public void startLoop() throws InterruptedException {
        Thread worker = new Thread(() -> {
            long count = 0;
            while (running) { // reads volatile 'running' from main memory each iteration
                count++;
                if (count % 1_000_000 == 0) {
                    Thread.yield(); // relinquish CPU briefly (helps demo)
                }
            }
            System.out.println("  Worker stopped after ~" + count + " iterations");
        });
        worker.start();
        Thread.sleep(50);  // let worker run briefly
        running = false;   // volatile write — visible to worker immediately
        worker.join(2000); // wait for worker to notice
        System.out.println("  Main: worker thread finished: " + !worker.isAlive());
    }

    // -----------------------------------------------------------------------
    // 2. Atomicity — volatile does NOT make compound ops atomic
    // -----------------------------------------------------------------------

    /**
     * i++ is NOT atomic — it is three bytecode operations:
     *   1. READ  i (from memory)
     *   2. ADD   1
     *   3. WRITE back
     *
     * Two threads can interleave these steps → lost updates.
     * volatile prevents stale reads but does NOT prevent interleaving.
     *
     * Fix: use AtomicInteger.incrementAndGet() which uses CAS (Compare-And-Swap).
     */
    private volatile int unsafeCounter = 0;
    private final AtomicInteger atomicCounter = new AtomicInteger(0);

    /**
     * Demonstrates that volatile int++ causes lost updates under concurrency.
     * The final count will typically be LESS than threadCount * incrementsPerThread.
     *
     * @param threadCount       number of concurrent threads
     * @param incrementsEach    number of increments per thread
     * @return actual final value of unsafeCounter (likely < expected)
     */
    public int unsafeIncrement(int threadCount, int incrementsEach) throws InterruptedException {
        unsafeCounter = 0;
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int t = 0; t < threadCount; t++) {
            new Thread(() -> {
                for (int i = 0; i < incrementsEach; i++) {
                    unsafeCounter++; // NOT atomic: read + increment + write
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        return unsafeCounter;
    }

    /**
     * AtomicInteger.incrementAndGet() is thread-safe via CAS.
     * No synchronization needed; no lost updates possible.
     *
     * @return always exactly threadCount * incrementsEach
     */
    public int atomicIncrement(int threadCount, int incrementsEach) throws InterruptedException {
        atomicCounter.set(0);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int t = 0; t < threadCount; t++) {
            new Thread(() -> {
                for (int i = 0; i < incrementsEach; i++) {
                    atomicCounter.incrementAndGet(); // CAS — atomic, no lost updates
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        return atomicCounter.get();
    }

    // -----------------------------------------------------------------------
    // 3. Safe publication — the Double-Checked Locking pattern
    // -----------------------------------------------------------------------

    /**
     * INCORRECT Double-Checked Locking (pre-Java 5 or without volatile).
     *
     * The JVM may reorder the object write and the reference assignment:
     *   1. Allocate memory for Singleton
     *   2. Assign reference to 'instance' (VISIBLE TO OTHER THREADS!)
     *   3. Run constructor (not yet done!)
     *
     * Another thread may see a non-null but partially-constructed object.
     *
     * DO NOT USE. Shown here only as a learning anti-pattern.
     */
    static class UnsafeSingleton {
        private static UnsafeSingleton instance; // missing volatile!
        private final int value;

        private UnsafeSingleton() { this.value = 42; }

        // BROKEN — another thread may read a partially-initialized instance
        public static UnsafeSingleton getInstance() {
            if (instance == null) {
                synchronized (UnsafeSingleton.class) {
                    if (instance == null) {
                        instance = new UnsafeSingleton(); // reordering possible!
                    }
                }
            }
            return instance;
        }

        public int getValue() { return value; }
    }

    /**
     * CORRECT Double-Checked Locking with volatile (Java 5+).
     *
     * volatile on 'instance' inserts a memory barrier that prevents
     * the JVM from publishing the reference before the constructor completes.
     * Subsequent reads of 'instance' are guaranteed to see a fully-constructed object.
     */
    static class SafeSingleton {
        private static volatile SafeSingleton instance; // volatile is mandatory
        private final int value;

        private SafeSingleton() { this.value = 42; }

        public static SafeSingleton getInstance() {
            if (instance == null) {                    // check 1: avoid lock overhead (fast path)
                synchronized (SafeSingleton.class) {
                    if (instance == null) {            // check 2: prevents double-init
                        instance = new SafeSingleton(); // volatile write → full barrier
                    }
                }
            }
            return instance;
        }

        public int getValue() { return value; }
    }

    // -----------------------------------------------------------------------
    // 4. Happens-Before via synchronized
    // -----------------------------------------------------------------------

    /**
     * Demonstrates Monitor Lock Rule:
     *   - write inside synchronized block A
     *   - read inside synchronized block B (same monitor)
     *   HB: unlock(A) hb lock(B) → B sees A's write
     *
     * @return value written by writer thread, visible to reader thread
     */
    private int sharedValue = 0;
    private final Object lock = new Object();

    public int synchronizedVisibilityDemo() throws InterruptedException {
        Thread writer = new Thread(() -> {
            synchronized (lock) {
                sharedValue = 99; // write inside lock
            }
        });
        Thread reader = new Thread(() -> {
            synchronized (lock) {
                // HB guaranteed: writer's unlock hb reader's lock
                // sharedValue is guaranteed to be 99 here
            }
        });
        writer.start();
        writer.join(); // Thread Termination Rule: all of writer's actions hb join() return
        reader.start();
        reader.join();

        synchronized (lock) {
            return sharedValue; // main reads after both threads joined — HB chain holds
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java Memory Model (JMM) Demo ===\n");

        // Volatile visibility
        System.out.println("--- 1. volatile visibility ---");
        JmmDemo demo = new JmmDemo();
        demo.startLoop();

        // Atomicity gap
        System.out.println("\n--- 2. atomicity: volatile i++ vs AtomicInteger ---");
        int threads = 4, incrementsEach = 10_000;
        int unsafe  = demo.unsafeIncrement(threads, incrementsEach);
        int atomic  = demo.atomicIncrement(threads, incrementsEach);
        int expected = threads * incrementsEach;
        System.out.println("Expected          : " + expected);
        System.out.println("Unsafe counter    : " + unsafe  + (unsafe  < expected ? " ← LOST UPDATES" : " (lucky run)"));
        System.out.println("Atomic counter    : " + atomic  + " ← always correct");

        // Safe singleton
        System.out.println("\n--- 3. Safe DCL Singleton ---");
        SafeSingleton s1 = SafeSingleton.getInstance();
        SafeSingleton s2 = SafeSingleton.getInstance();
        System.out.println("Same instance: " + (s1 == s2) + ", value: " + s1.getValue());

        // synchronized HB
        System.out.println("\n--- 4. Happens-before via synchronized ---");
        int val = demo.synchronizedVisibilityDemo();
        System.out.println("sharedValue after synchronized HB: " + val);
    }
}
