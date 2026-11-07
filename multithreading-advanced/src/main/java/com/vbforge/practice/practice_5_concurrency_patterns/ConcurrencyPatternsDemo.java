// IMPLEMENTATION
package com.vbforge.practice.practice_5_concurrency_patterns;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates production-grade concurrency patterns — reusable solutions to
 * recurring thread-safety problems. Each pattern addresses a distinct category.
 *
 * Patterns covered:
 *
 *   1. Thread-safe Singleton (Initialization-on-Demand Holder)
 *      Lazy, thread-safe, no synchronization overhead on the hot path.
 *      Preferred over DCL (covered in multithreading_basics/practice_5_volatile) for simplicity.
 *
 *   2. Producer-Consumer with BlockingQueue
 *      Decouples producers from consumers via a bounded queue.
 *      BlockingQueue handles all synchronization — no wait/notify needed.
 *      Poison pill for clean shutdown.
 *
 *   3. Thread-per-Message (virtual threads, Java 21+, or pooled)
 *      Each incoming request gets its own thread context.
 *      With virtual threads: near-zero cost; millions of concurrent tasks.
 *
 *   4. Read-Write Guard (optimistic pattern)
 *      Readers proceed without locking; validate before using result.
 *      Writers acquire exclusive lock. Pattern seen in CopyOnWriteArrayList,
 *      StampedLock, and optimistic database locking.
 *
 *   5. Guarded Suspension
 *      A thread waits (suspends) until a precondition is true, then proceeds.
 *      Implemented with ReentrantLock + Condition (cleaner than wait/notify).
 *
 *   6. Two-Phase Termination
 *      Requests a running thread to stop cleanly by setting a flag,
 *      then joining. Avoids stop() (deprecated) and abrupt interruption of
 *      business logic. Guarantees cleanup code (finally blocks) always runs.
 */
public class ConcurrencyPatternsDemo {

    // ── 1. Initialization-on-Demand Holder (IODH) Singleton ──────────────────

    /**
     * The safest lazy singleton in Java.
     *
     * How it works:
     *   The JVM loads inner classes lazily. HeavyServiceHolder is not loaded until
     *   the first call to getInstance(). Class loading is inherently thread-safe —
     *   the JVM guarantees static field initialization is atomic.
     *   No synchronized, no volatile, no overhead on the hot path.
     *
     * When to use:
     *   Whenever you need lazy initialization of a heavyweight singleton.
     *   Prefer this over DCL for clarity.
     */
    static class HeavyService {
        private final String id;

        private HeavyService() {
            // Simulate expensive initialization
            id = "HeavyService#" + System.nanoTime();
            System.out.println("  [HeavyService] initialized: " + id);
        }

        // Static inner class — loaded only on first access to getInstance()
        private static final class Holder {
            static final HeavyService INSTANCE = new HeavyService();
        }

        public static HeavyService getInstance() {
            return Holder.INSTANCE;
        }

        public String getId() { return id; }
    }

    // ── 2. Producer-Consumer with BlockingQueue + Poison Pill ─────────────────

    /**
     * Classic producer-consumer built on ArrayBlockingQueue.
     *
     * BlockingQueue.put()  — blocks if full   (backpressure)
     * BlockingQueue.take() — blocks if empty  (consumer waits for work)
     *
     * Poison pill:
     *   A sentinel value inserted by the producer to signal "no more work".
     *   Each consumer takes one pill and stops. Works cleanly when the
     *   number of consumers is known at shutdown time.
     */
    static class PipelineDemo {

        static final String POISON = "__STOP__";

        static void run(int producerItems, int consumers) throws InterruptedException {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

            // Producer thread
            Thread producer = new Thread(() -> {
                try {
                    for (int i = 1; i <= producerItems; i++) {
                        String item = "item-" + i;
                        queue.put(item);
                        System.out.println("  [producer] put: " + item);
                        Thread.sleep(10);
                    }
                    // One poison pill per consumer
                    for (int i = 0; i < consumers; i++) queue.put(POISON);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Producer");

            // Consumer threads
            Thread[] consumerThreads = new Thread[consumers];
            for (int c = 0; c < consumers; c++) {
                final int id = c + 1;
                consumerThreads[c] = new Thread(() -> {
                    try {
                        while (true) {
                            String item = queue.take();
                            if (POISON.equals(item)) break;   // stop signal received
                            System.out.printf("  [consumer-%d] took: %s%n", id, item);
                        }
                        System.out.println("  [consumer-" + id + "] stopped");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, "Consumer-" + id);
            }

            producer.start();
            for (Thread t : consumerThreads) t.start();
            producer.join();
            for (Thread t : consumerThreads) t.join();
        }
    }

    // ── 3. Guarded Suspension ─────────────────────────────────────────────────

    /**
     * A request-reply channel where the caller submits a request and blocks
     * until a result is available. The worker fills the result asynchronously.
     *
     * Use case: async handlers, request-reply over message queues, futures.
     * This is essentially a single-slot, one-shot CompletableFuture built
     * from primitives to make the pattern explicit.
     */
    static class GuardedRequest<T> {
        private T       result;
        private boolean fulfilled = false;
        private final ReentrantLock lock      = new ReentrantLock();
        private final Condition fulfilled_ = lock.newCondition();

        /** Worker calls this to provide the result. */
        public void fulfill(T value) {
            lock.lock();
            try {
                this.result    = value;
                this.fulfilled = true;
                fulfilled_.signalAll();
            } finally {
                lock.unlock();
            }
        }

        /** Caller blocks here until worker calls fulfill(). */
        public T await() throws InterruptedException {
            lock.lock();
            try {
                while (!fulfilled) fulfilled_.await();
                return result;
            } finally {
                lock.unlock();
            }
        }

        public boolean isFulfilled() {
            lock.lock();
            try { return fulfilled; } finally { lock.unlock(); }
        }
    }

    // ── 4. Two-Phase Termination ──────────────────────────────────────────────

    /**
     * A background worker that can be stopped cleanly.
     *
     * Phase 1 — request termination: set flag, interrupt sleeping thread.
     * Phase 2 — wait for termination: join the thread.
     *
     * This pattern guarantees:
     *   - The worker finishes its current unit of work before stopping.
     *   - Finally blocks (cleanup, resource release) always execute.
     *   - The caller knows the worker has fully stopped after stop() returns.
     */
    static class BackgroundWorker {
        private final AtomicBoolean running = new AtomicBoolean(false);
        private volatile Thread     workerThread;
        private int                 workDone = 0;

        public void start() {
            running.set(true);
            workerThread = new Thread(() -> {
                System.out.println("  [worker] started");
                try {
                    while (running.get()) {
                        doUnitOfWork();
                        Thread.sleep(30);    // interruptible wait between units
                    }
                } catch (InterruptedException e) {
                    // Phase 1 interrupted us out of sleep — re-check running flag
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println("  [worker] cleanup done. units=" + workDone);
                }
            }, "BackgroundWorker");
            workerThread.setDaemon(false);
            workerThread.start();
        }

        /** Phase 1: signal stop. Phase 2: wait for full termination. */
        public void stop() throws InterruptedException {
            running.set(false);           // phase 1a: set stop flag
            if (workerThread != null) {
                workerThread.interrupt(); // phase 1b: wake from sleep immediately
                workerThread.join();      // phase 2:  wait for clean exit
            }
        }

        private void doUnitOfWork() {
            workDone++;
            // Simulate work (non-interruptible section)
        }

        public int getWorkDone() { return workDone; }
    }

    // ── 5. Copy-On-Write snapshot sharing ────────────────────────────────────

    /**
     * Readers always get a consistent snapshot; writers atomically swap the reference.
     * No locking required for reads — useful for rarely-updated config / allow-lists.
     *
     * This is the pattern behind CopyOnWriteArrayList, with a simpler wrapper.
     */
    static class CopyOnWriteConfig {
        // Immutable snapshot — once published, never mutated
        record Config(String endpoint, int timeout) {}

        private final AtomicReference<Config> ref =
                new AtomicReference<>(new Config("localhost", 5000));

        /** Always returns a consistent, immutable snapshot — no lock needed. */
        public Config get() { return ref.get(); }

        /** Creates a new snapshot and atomically publishes it. */
        public void update(String endpoint, int timeout) {
            ref.set(new Config(endpoint, timeout));
        }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Concurrency Patterns Demo ===\n");

        // --- 1. IODH Singleton ---
        System.out.println("-- IODH Singleton --");
        HeavyService s1 = HeavyService.getInstance();
        HeavyService s2 = HeavyService.getInstance();
        System.out.println("  same instance: " + (s1 == s2));

        // --- 2. Producer-Consumer ---
        System.out.println("\n-- Producer-Consumer + Poison Pill --");
        PipelineDemo.run(5, 2);

        // --- 3. Guarded Suspension ---
        System.out.println("\n-- Guarded Suspension --");
        GuardedRequest<String> request = new GuardedRequest<>();

        Thread worker = new Thread(() -> {
            try { Thread.sleep(80); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            request.fulfill("result-from-worker");
        }, "Worker");
        worker.start();

        System.out.println("  caller blocking...");
        String reply = request.await();
        System.out.println("  caller received: " + reply);
        worker.join();

        // --- 4. Two-Phase Termination ---
        System.out.println("\n-- Two-Phase Termination --");
        BackgroundWorker bgWorker = new BackgroundWorker();
        bgWorker.start();
        Thread.sleep(200);
        bgWorker.stop();
        System.out.println("  work done: " + bgWorker.getWorkDone());

        // --- 5. CopyOnWrite config ---
        System.out.println("\n-- CopyOnWrite config snapshot --");
        CopyOnWriteConfig cfg = new CopyOnWriteConfig();
        System.out.println("  initial: " + cfg.get());
        cfg.update("prod-server", 3000);
        System.out.println("  updated: " + cfg.get());

        System.out.println("\nMain done.");
    }
}