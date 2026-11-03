package com.vbforge.multithreading_advanced;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Synchronizers: {@code CountDownLatch} and {@code Semaphore}.
 *
 * <p>{@code synchronized} and locks answer "who may touch this data right
 * now?". Synchronizers answer different coordination questions:
 * "has everyone finished?", "may everyone start now?",
 * "how many may enter at once?".
 *
 * <p>Run {@link #main(String[])} to see every section's output.
 */
public class SynchronizersDemo {

    public static void main(String[] args) throws Exception {
        countDownLatchAwaitWorkersDemo();
        System.out.println("---");
        startGateDemo();
        System.out.println("---");
        latchGotchasDemo();
        System.out.println("---");
        semaphoreLimitDemo();
        System.out.println("---");
        semaphorePermitsDemo();
    }

    // ------------------------------------------------------------------
    // 1. CountDownLatch: wait for N workers
    // ------------------------------------------------------------------

    /**
     * {@code CountDownLatch(n)} — a one-shot "wait until n things have
     * happened" counter.
     *
     * <ul>
     *   <li>Workers call {@code countDown()} when they finish.</li>
     *   <li>The coordinator calls {@code await()} and is released when the
     *       count reaches 0.</li>
     *   <li>{@code countDown()} / {@code getCount()}: O(1); {@code await()}
     *       blocks without busy-waiting.</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> it replaces {@code Thread.join()} on every
     * worker and {@code Thread.sleep(...)} "that should be long enough".
     * It also works with pool threads, which you cannot {@code join()}.
     *
     * <p><b>Gotcha:</b> call {@code countDown()} in a {@code finally} block.
     * If a worker throws before reaching it, the coordinator waits forever.
     */
    static void countDownLatchAwaitWorkersDemo() throws InterruptedException {
        final int workers = 4;
        CountDownLatch allDone = new CountDownLatch(workers);
        AtomicInteger finished = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(workers);
        try {
            for (int i = 1; i <= workers; i++) {
                final int id = i;
                pool.execute(() -> {
                    try {
                        Thread.sleep(20L * id);
                        finished.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        allDone.countDown();
                    }
                });
            }
            allDone.await();
            System.out.println("all workers finished: " + finished.get() + "/" + workers);
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 2. CountDownLatch: start gate
    // ------------------------------------------------------------------

    /**
     * The "start gate" / "starting gun" pattern: a latch with count 1.
     *
     * <ul>
     *   <li>Every worker signals "ready" on one latch, then blocks on the
     *       gate latch.</li>
     *   <li>The coordinator waits until all workers are ready, then opens the
     *       gate with a single {@code countDown()} — all workers are released
     *       at (almost) the same instant.</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> without a gate, the first worker starts
     * before the last one has even been created, which ruins concurrency
     * tests and benchmarks.
     */
    static void startGateDemo() throws InterruptedException {
        final int workers = 3;
        CountDownLatch ready = new CountDownLatch(workers);
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(workers);
        AtomicInteger started = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(workers);
        try {
            for (int i = 0; i < workers; i++) {
                pool.execute(() -> {
                    try {
                        ready.countDown();
                        startGun.await();
                        started.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        finished.countDown();
                    }
                });
            }
            ready.await();
            System.out.println("workers started before the gun: " + started.get());
            startGun.countDown();
            finished.await();
            System.out.println("workers started after the gun: " + started.get());
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 3. CountDownLatch gotchas
    // ------------------------------------------------------------------

    /**
     * What a {@code CountDownLatch} can <em>not</em> do.
     *
     * <ul>
     *   <li>It is <b>single-use</b>: the count never goes back up. For a
     *       reusable barrier use {@code CyclicBarrier}.</li>
     *   <li>{@code countDown()} on a latch already at 0 does nothing.</li>
     *   <li>{@code await()} on a latch at 0 returns immediately.</li>
     *   <li>{@code await(timeout, unit)} returns {@code false} if the count
     *       is still above 0 when time runs out — it does <em>not</em> throw.
     *       Ignoring that boolean is a common bug.</li>
     * </ul>
     */
    static void latchGotchasDemo() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        System.out.println("await(50ms) with count 2: " + latch.await(50, TimeUnit.MILLISECONDS));

        latch.countDown();
        latch.countDown();
        latch.countDown(); // extra call: no effect, count stays at 0
        System.out.println("count after three countDown() calls on a latch of 2: " + latch.getCount());

        latch.await(); // returns immediately — the latch is open for good
        System.out.println("await(50ms) with count 0: " + latch.await(50, TimeUnit.MILLISECONDS));
    }

    // ------------------------------------------------------------------
    // 4. Semaphore: limit concurrency
    // ------------------------------------------------------------------

    /**
     * {@code Semaphore(permits)} — at most {@code permits} threads may be
     * inside a section at once.
     *
     * <ul>
     *   <li>{@code acquire()} takes a permit, blocking if none is free.</li>
     *   <li>{@code release()} returns it.</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> it protects a scarce resource — a pool of 3
     * database connections, a rate-limited API, a slow disk — without
     * limiting how many <em>threads</em> exist. A lock allows exactly one
     * thread; a semaphore allows N.
     *
     * <p><b>Gotcha:</b> always {@code release()} in a {@code finally}
     * block, and do it <em>after</em> your own bookkeeping. Here the running
     * counter is decremented <em>before</em> {@code release()}; reversing the
     * two lets a new thread enter and increment first, so the counter would
     * briefly show more than 3.
     */
    static void semaphoreLimitDemo() throws InterruptedException {
        final int tasks = 10;
        Semaphore permits = new Semaphore(3);
        AtomicInteger running = new AtomicInteger();
        AtomicInteger maxRunning = new AtomicInteger();
        CountDownLatch allDone = new CountDownLatch(tasks);
        ExecutorService pool = Executors.newFixedThreadPool(tasks);
        try {
            for (int i = 0; i < tasks; i++) {
                pool.execute(() -> {
                    try {
                        permits.acquire();
                        try {
                            maxRunning.accumulateAndGet(running.incrementAndGet(), Math::max);
                            Thread.sleep(20);
                        } finally {
                            running.decrementAndGet();
                            permits.release();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        allDone.countDown();
                    }
                });
            }
            allDone.await();
            System.out.println("10 threads, 3 permits: highest concurrency seen = " + maxRunning.get());
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 5. Semaphore: permit bookkeeping
    // ------------------------------------------------------------------

    /**
     * Permit bookkeeping with {@code Semaphore}.
     *
     * <ul>
     *   <li>{@code tryAcquire()} — non-blocking; {@code true} if a permit was
     *       taken, {@code false} immediately otherwise.</li>
     *   <li>{@code acquire(n)} / {@code release(n)} — take or return several
     *       permits at once.</li>
     *   <li>{@code drainPermits()} — takes all currently available permits
     *       and returns how many there were.</li>
     *   <li>{@code new Semaphore(n, true)} — fair: waiting threads are
     *       served in FIFO order, at some throughput cost.</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> a semaphore does <em>not</em> remember who owns a
     * permit. Calling {@code release()} without a matching {@code acquire()}
     * <em>adds</em> a permit and silently raises the limit above the value
     * you constructed it with. Only release what you actually acquired —
     * in particular, never release in a {@code finally} that also runs when
     * {@code tryAcquire()} returned {@code false}.
     */
    static void semaphorePermitsDemo() throws InterruptedException {
        Semaphore twoPermits = new Semaphore(2);
        System.out.println("tryAcquire #1: " + twoPermits.tryAcquire());
        System.out.println("tryAcquire #2: " + twoPermits.tryAcquire());
        System.out.println("tryAcquire #3: " + twoPermits.tryAcquire());
        System.out.println("available: " + twoPermits.availablePermits());

        Semaphore inflated = new Semaphore(1);
        inflated.release(); // never acquired!
        System.out.println("permits after a stray release(): " + inflated.availablePermits());

        Semaphore bulk = new Semaphore(5);
        bulk.acquire(3);
        System.out.println("after acquire(3): " + bulk.availablePermits());
        System.out.println("drainPermits() returned: " + bulk.drainPermits());
        System.out.println("fair semaphore: " + new Semaphore(1, true).isFair());
    }
}
