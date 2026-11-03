package com.vbforge.virtual_threads_intro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Virtual threads — pitfalls and the habits that go with them.
 *
 * <p>Virtual threads change which habits are good. Sections:
 * <ol>
 *   <li>Limit concurrency with a {@link Semaphore}, not with a pool</li>
 *   <li>{@link ThreadLocal} is per thread — and now there can be millions of threads</li>
 *   <li>{@code synchronized} vs {@link ReentrantLock} (carrier pinning)</li>
 * </ol>
 */
public class VirtualThreadPitfallsDemo {

    public static void main(String[] args) throws Exception {
        limitConcurrencyDemo();
        System.out.println("---");
        threadLocalDemo();
        System.out.println("---");
        lockingDemo();
    }

    // =========================================================================
    // 1. LIMIT CONCURRENCY WITH A SEMAPHORE
    // =========================================================================
    /*
     * What:
     *   A Semaphore holds N permits. A task must acquire() a permit before touching a
     *   scarce resource and release() it afterwards. A virtual thread that cannot get a
     *   permit simply parks (state WAITING) — it costs almost nothing to wait.
     *
     * Why it matters:
     *   With platform threads, "a pool of 10 threads" doubled as a rate limiter for the
     *   database or a remote API. A virtual-thread-per-task executor has no such limit —
     *   100_000 tasks would all hit the database at once. The limit belongs to the
     *   resource, so express it explicitly.
     *
     * Complexity:
     *   - acquire / release: O(1)
     *   - Waiting tasks: O(waiters) cheap parked virtual threads
     *
     * Common gotcha:
     *   Always release() in a finally block — a leaked permit shrinks the limit forever.
     */
    static void limitConcurrencyDemo() {
        Semaphore permits = new Semaphore(2);
        AtomicInteger inFlight = new AtomicInteger();
        AtomicInteger peak = new AtomicInteger();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 8; i++) {
                executor.submit(() -> {
                    permits.acquire();
                    try {
                        peak.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
                        Thread.sleep(10);
                    } finally {
                        inFlight.decrementAndGet();
                        permits.release();
                    }
                    return null;
                });
            }
        }
        System.out.println("8 tasks, 2 permits → peak concurrency: " + peak.get());
    }

    // =========================================================================
    // 2. THREAD-LOCALS
    // =========================================================================
    /*
     * What:
     *   Virtual threads support ThreadLocal exactly like platform threads: every thread
     *   has its own copy, and a new thread does NOT see the value of another thread
     *   (only InheritableThreadLocal copies from the parent at creation).
     *
     * Why it matters:
     *   Per-thread caches (e.g. a reusable SimpleDateFormat or a big buffer) were fine
     *   when there were 200 threads. With one virtual thread per task there can be
     *   hundreds of thousands, each holding its own copy. Keep ThreadLocal values small
     *   and short-lived — or pass data as parameters. (ScopedValue is the planned
     *   replacement; it is a preview API in Java 21 and not used in this module.)
     *
     * Common gotcha:
     *   A thread-local is NOT a pool. Because virtual threads are never reused, a value
     *   set in one task will never be seen by the next task.
     */
    private static final ThreadLocal<String> CURRENT_USER = ThreadLocal.withInitial(() -> "anonymous");

    static void threadLocalDemo() throws InterruptedException {
        CURRENT_USER.set("main-user");

        Thread worker = Thread.ofVirtual().start(() -> {
            System.out.println("virtual thread starts with: " + CURRENT_USER.get());
            CURRENT_USER.set("virtual-user");
            System.out.println("virtual thread now has:     " + CURRENT_USER.get());
        });
        worker.join();

        System.out.println("main thread still has:      " + CURRENT_USER.get());
        CURRENT_USER.remove();
    }

    // =========================================================================
    // 3. synchronized vs ReentrantLock  (carrier pinning)
    // =========================================================================
    /*
     * What:
     *   A virtual thread runs "mounted" on a carrier thread. If it blocks, the JVM
     *   normally unmounts it so the carrier can run something else. In Java 21, blocking
     *   while holding a synchronized monitor (or inside a native frame) PINS the virtual
     *   thread to its carrier: the carrier is stuck until the block ends.
     *   ReentrantLock (java.util.concurrent.locks) does not pin.
     *
     * Why it matters:
     *   A few pinned threads are harmless. Many virtual threads blocking inside
     *   synchronized — for example around a slow remote call — can exhaust the carriers
     *   and stall the whole application. Prefer ReentrantLock around code that blocks.
     *   (Java 24 removed most synchronized pinning, JEP 491 — but this module targets
     *   Java 21, where the guidance still applies.)
     *
     * Complexity:
     *   - Both locks guard the critical section in O(1) per entry
     *   - Contention cost, not the lock type, dominates for pure CPU work
     *
     * Common gotcha:
     *   Both versions below are CORRECT. Pinning is a scalability problem, not a
     *   correctness problem — which is why you only notice it under load.
     *   To find pinning on Java 21 run with: -Djdk.tracePinnedThreads=full
     */
    static void lockingDemo() throws InterruptedException {
        System.out.println("synchronized:  " + countWithSynchronized(100, 1_000));
        System.out.println("ReentrantLock: " + countWithReentrantLock(100, 1_000));
    }

    /**
     * Increments a shared counter from many virtual threads, guarded by a monitor.
     *
     * @return the final counter value ({@code threads * incrementsPerThread})
     */
    public static int countWithSynchronized(int threads, int incrementsPerThread) throws InterruptedException {
        int[] counter = {0};
        Object monitor = new Object();
        List<Thread> started = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            started.add(Thread.ofVirtual().start(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    synchronized (monitor) {
                        counter[0]++;
                    }
                }
            }));
        }
        for (Thread t : started) {
            t.join();
        }
        return counter[0];
    }

    /**
     * Same job guarded by a {@link ReentrantLock} — the form that never pins a carrier.
     *
     * @return the final counter value ({@code threads * incrementsPerThread})
     */
    public static int countWithReentrantLock(int threads, int incrementsPerThread) throws InterruptedException {
        int[] counter = {0};
        ReentrantLock lock = new ReentrantLock();
        List<Thread> started = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            started.add(Thread.ofVirtual().start(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    lock.lock();
                    try {
                        counter[0]++;
                    } finally {
                        lock.unlock();
                    }
                }
            }));
        }
        for (Thread t : started) {
            t.join();
        }
        return counter[0];
    }
}
