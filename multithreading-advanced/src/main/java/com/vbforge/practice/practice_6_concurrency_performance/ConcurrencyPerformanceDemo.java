package com.vbforge.practice.practice_6_concurrency_performance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Demonstrates performance-aware concurrency patterns in Java.
 *
 * Core tension in concurrent code:
 *   Safety (correctness)  ←→  Throughput (performance)
 *
 * Every synchronization mechanism has a cost:
 *   - synchronized block:  kernel mutex, thread blocking, cache coherence flush
 *   - volatile:            guarantees visibility, NO atomicity, cheaper than mutex
 *   - AtomicXxx:           lock-free CAS (Compare-And-Swap), best for single counters
 *   - ReentrantLock:       explicit lock with tryLock/timeout, fairer scheduling option
 *   - ReadWriteLock:       multiple concurrent readers, exclusive writer — good for read-heavy
 *   - StampedLock:         optimistic reads, fastest for read-heavy with rare writes
 *
 * Thread pool sizing rule of thumb:
 *   CPU-bound tasks:  nThreads ≈ availableProcessors (no idle waiting)
 *   IO-bound tasks:   nThreads ≈ availableProcessors * (1 + wait/compute ratio)
 */
public class ConcurrencyPerformanceDemo {

    // -------------------------------------------------------------------------
    // 1. synchronized counter vs AtomicLong — contention cost
    // -------------------------------------------------------------------------

    /**
     * synchronized-based counter.
     * Correct but every increment acquires the intrinsic lock.
     * Under high contention threads queue up → throughput drops.
     */
    public static class SynchronizedCounter {
        private long count = 0;

        public synchronized void increment() {
            count++;              // exclusive lock per call
        }

        public synchronized long get() {
            return count;
        }
    }

    /**
     * Lock-free counter using CAS (Compare-And-Swap) hardware instruction.
     * AtomicLong internally calls Unsafe.compareAndSetLong.
     * No OS-level mutex, no thread blocking — spinning retry on CAS failure.
     * Faster under moderate contention; at extreme contention LongAdder wins.
     */
    public static class AtomicCounter {
        private final AtomicLong count = new AtomicLong(0);

        public void increment() {
            count.incrementAndGet();  // lock-free CAS
        }

        public long get() {
            return count.get();
        }
    }

    /**
     * LongAdder — distributes the counter across per-thread cells.
     * Reduces contention further when many threads write concurrently.
     * sum() is slightly more expensive (aggregates cells) — use when writes >> reads.
     */
    public static class AdderCounter {
        private final LongAdder adder = new LongAdder();

        public void increment() {
            adder.increment();        // near-zero contention per thread
        }

        public long get() {
            return adder.sum();       // aggregates all cells
        }
    }

    // -------------------------------------------------------------------------
    // 2. ReadWriteLock — optimize read-heavy workloads
    // -------------------------------------------------------------------------

    /**
     * Plain synchronized gives exclusive access even for reads.
     * If 90% of operations are reads, readers block each other unnecessarily.
     *
     * ReadWriteLock contract:
     *   - Multiple threads can hold the read lock simultaneously (shared)
     *   - Write lock is exclusive — blocks all readers and other writers
     *
     * Result: read throughput scales with reader thread count.
     */
    public static class ReadWriteCache {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock readLock  = lock.readLock();
        private final Lock writeLock = lock.writeLock();

        private String cachedValue = "";

        public String read() {
            readLock.lock();
            try {
                return cachedValue;           // concurrent readers allowed
            } finally {
                readLock.unlock();            // always in finally to avoid deadlock
            }
        }

        public void write(String value) {
            writeLock.lock();
            try {
                cachedValue = value;          // exclusive — blocks all readers
            } finally {
                writeLock.unlock();
            }
        }
    }

    // -------------------------------------------------------------------------
    // 3. Thread pool sizing — CPU-bound vs IO-bound
    // -------------------------------------------------------------------------

    /**
     * CPU-bound: work keeps the CPU busy (no waiting).
     * Optimal threads = # of logical processors.
     * More threads → context-switch overhead, no throughput gain.
     */
    public static ExecutorService cpuBoundPool() {
        int cores = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(cores);
    }

    /**
     * IO-bound: threads spend most time waiting (network, disk).
     * More threads can be parked while waiting without wasting CPU.
     * Heuristic: cores * (1 + waitTime / computeTime).
     * Here we use cores * 4 as a simple approximation.
     */
    public static ExecutorService ioBoundPool() {
        int cores = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(cores * 4);
    }

    // -------------------------------------------------------------------------
    // 4. tryLock — non-blocking lock attempt to avoid deadlock / long waits
    // -------------------------------------------------------------------------

    /**
     * tryLock(timeout) returns false instead of blocking indefinitely.
     * Useful for: timeouts, fallback paths, deadlock avoidance.
     *
     * Pattern:
     *   if (lock.tryLock(100, MILLISECONDS)) {
     *       try { ... } finally { lock.unlock(); }
     *   } else {
     *       // handle contention: retry, skip, or fallback
     *   }
     */
    public static class TryLockResource {
        private final ReentrantLock lock = new ReentrantLock();
        private int value = 0;

        /**
         * Attempts to update value within the timeout.
         * Returns true if the update was applied, false if timed out.
         */
        public boolean tryUpdate(int newValue, long timeoutMs) throws InterruptedException {
            if (lock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                try {
                    value = newValue;
                    return true;
                } finally {
                    lock.unlock();
                }
            }
            return false;   // timed out — caller decides what to do
        }

        public int getValue() { return value; }
    }

    // -------------------------------------------------------------------------
    // 5. volatile — visibility without atomicity
    // -------------------------------------------------------------------------

    /**
     * volatile guarantees:
     *   - Write is immediately flushed to main memory
     *   - Read always fetches from main memory (not CPU cache)
     *   - Happens-before relationship established
     *
     * Does NOT provide:
     *   - Atomicity for compound operations (check-then-act, read-modify-write)
     *
     * Safe use case: single-writer flag, stop-signal between threads.
     */
    public static class StopableWorker implements Runnable {
        private volatile boolean running = true;   // visible to all threads immediately

        public void stop() {
            running = false;                       // write flushed to main memory
        }

        @Override
        public void run() {
            while (running) {                      // always reads fresh value
                // simulate work unit
                Thread.onSpinWait();
            }
        }

        public boolean isRunning() { return running; }
    }

    // -------------------------------------------------------------------------
    // main — illustrative output
    // -------------------------------------------------------------------------

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Concurrency Performance Demo ===\n");

        // Counter comparison (single-threaded here — just correctness check)
        System.out.println("-- Counter correctness (1000 increments) --");
        SynchronizedCounter sync   = new SynchronizedCounter();
        AtomicCounter        atomic = new AtomicCounter();
        AdderCounter         adder  = new AdderCounter();

        for (int i = 0; i < 1000; i++) {
            sync.increment();
            atomic.increment();
            adder.increment();
        }
        System.out.println("Synchronized: " + sync.get());
        System.out.println("Atomic:       " + atomic.get());
        System.out.println("Adder:        " + adder.get());

        // ReadWriteCache
        System.out.println("\n-- ReadWriteCache --");
        ReadWriteCache cache = new ReadWriteCache();
        cache.write("hello");
        System.out.println("Cached value: " + cache.read());

        // TryLock
        System.out.println("\n-- TryLock update --");
        TryLockResource res = new TryLockResource();
        boolean updated = res.tryUpdate(42, 100);
        System.out.println("Update applied: " + updated + ", value: " + res.getValue());

        // StopableWorker
        System.out.println("\n-- Volatile stop signal --");
        StopableWorker worker = new StopableWorker();
        Thread t = new Thread(worker);
        t.start();
        Thread.sleep(10);
        worker.stop();
        t.join(200);
        System.out.println("Worker stopped: " + !t.isAlive());

        // Thread pool info
        System.out.println("\n-- Thread pool sizing --");
        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        ExecutorService cpu = cpuBoundPool();
        ExecutorService io  = ioBoundPool();
        cpu.shutdown();
        io.shutdown();
        System.out.println("CPU-bound pool and IO-bound pool created and shut down cleanly.");
    }
}
