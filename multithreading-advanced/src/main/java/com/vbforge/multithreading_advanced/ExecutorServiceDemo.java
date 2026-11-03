package com.vbforge.multithreading_advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ExecutorService and thread pools.
 *
 * <p>Creating a {@code new Thread(...)} for every piece of work is expensive
 * (each platform thread reserves stack memory and an OS thread) and unbounded
 * (10 000 requests can mean 10 000 threads). An {@link ExecutorService}
 * separates <em>what</em> to run (a task) from <em>how</em> to run it
 * (which threads, how many, what happens when they are all busy).
 *
 * <p>Run {@link #main(String[])} to see every section's output.
 */
public class ExecutorServiceDemo {

    public static void main(String[] args) throws Exception {
        fixedPoolDemo();
        System.out.println("---");
        poolFactoriesDemo();
        System.out.println("---");
        scheduledPoolDemo();
        System.out.println("---");
        shutdownDemo();
        System.out.println("---");
        threadPoolExecutorDemo();
        System.out.println("---");
        rejectionPolicyDemo();
        System.out.println("---");
        threadFactoryDemo();
        System.out.println("---");
        factoryGotchasDemo();
    }

    // ------------------------------------------------------------------
    // 1. Fixed thread pool
    // ------------------------------------------------------------------

    /**
     * {@code Executors.newFixedThreadPool(n)} — a pool that keeps at most
     * {@code n} worker threads alive and queues everything else.
     *
     * <p><b>Why it matters:</b> it caps the number of OS threads, and the
     * workers are <em>reused</em> for many tasks instead of being created and
     * destroyed per task.
     *
     * <ul>
     *   <li>Submitting a task: O(1) (queue insert)</li>
     *   <li>Memory: n worker threads + one queued object per waiting task</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> the default thread factory creates <em>non-daemon</em>
     * threads named {@code pool-N-thread-M}. If you forget to call
     * {@code shutdown()}, the JVM never exits.
     */
    static void fixedPoolDemo() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                final int taskId = i;
                futures.add(pool.submit(() ->
                        "task-" + taskId + " ran on " + Thread.currentThread().getName()));
            }
            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 2. Other factory methods: single-thread and cached
    // ------------------------------------------------------------------

    /**
     * {@code newSingleThreadExecutor()} and {@code newCachedThreadPool()}.
     *
     * <p><b>Single-thread executor:</b> exactly one worker, tasks run strictly
     * one after another in submission order (FIFO). A cheap way to get
     * "serialised" execution without {@code synchronized}.
     *
     * <p><b>Cached pool:</b> creates a new thread whenever none is idle and
     * reclaims threads that stay idle for 60 seconds. Great for many short
     * tasks; dangerous for bursts of long ones because the number of threads
     * has <em>no upper bound</em> (see {@link #factoryGotchasDemo()}).
     */
    static void poolFactoriesDemo() throws InterruptedException {
        ExecutorService single = Executors.newSingleThreadExecutor();
        for (int i = 1; i <= 3; i++) {
            final int step = i;
            single.execute(() ->
                    System.out.println("single: step " + step + " on " + Thread.currentThread().getName()));
        }
        single.shutdown();
        single.awaitTermination(5, TimeUnit.SECONDS);

        ExecutorService cached = Executors.newCachedThreadPool();
        CountDownLatch done = new CountDownLatch(3);
        for (int i = 1; i <= 3; i++) {
            final int job = i;
            cached.execute(() -> {
                System.out.println("cached: job " + job + " on " + Thread.currentThread().getName());
                done.countDown();
            });
        }
        done.await();
        cached.shutdown();
    }

    // ------------------------------------------------------------------
    // 3. Scheduled pool
    // ------------------------------------------------------------------

    /**
     * {@code ScheduledExecutorService} — run a task after a delay or
     * repeatedly. It replaces {@code java.util.Timer}, which has a single
     * thread and dies completely when one task throws.
     *
     * <ul>
     *   <li>{@code schedule(task, delay, unit)} — run once, returns a
     *       {@code ScheduledFuture}</li>
     *   <li>{@code scheduleAtFixedRate} — period measured start-to-start</li>
     *   <li>{@code scheduleWithFixedDelay} — delay measured end-to-start</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> if a periodic task throws, <em>all further runs are
     * silently cancelled</em>. The exception only becomes visible if you call
     * {@code get()} on the returned future. Wrap the body in try/catch if the
     * schedule must survive failures.
     */
    static void scheduledPoolDemo() throws InterruptedException, ExecutionException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        try {
            ScheduledFuture<String> delayed =
                    scheduler.schedule(() -> "result of a delayed task", 100, TimeUnit.MILLISECONDS);
            System.out.println(delayed.get());

            CountDownLatch threeRuns = new CountDownLatch(3);
            ScheduledFuture<?> periodic = scheduler.scheduleAtFixedRate(() -> {
                System.out.println("periodic tick " + (4 - threeRuns.getCount()));
                threeRuns.countDown();
            }, 0, 50, TimeUnit.MILLISECONDS);
            threeRuns.await();
            periodic.cancel(false);
            System.out.println("periodic cancelled: " + periodic.isCancelled());
        } finally {
            scheduler.shutdownNow();
        }
    }

    // ------------------------------------------------------------------
    // 4. Shutdown
    // ------------------------------------------------------------------

    /**
     * {@code shutdown()} vs {@code shutdownNow()}.
     *
     * <ul>
     *   <li>{@code shutdown()} — polite: no new tasks are accepted, but every
     *       task already submitted (running <em>and</em> queued) finishes.</li>
     *   <li>{@code shutdownNow()} — rude: interrupts running tasks and
     *       returns the list of queued tasks that never started.</li>
     *   <li>{@code awaitTermination(t, unit)} — blocks until the pool has
     *       fully stopped or the timeout expires; neither shutdown method
     *       waits by itself.</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> {@code shutdownNow()} only <em>interrupts</em>. A task
     * that ignores the interrupt flag keeps running.
     */
    static void shutdownDemo() throws InterruptedException {
        // --- shutdown(): queued work still completes ---
        ExecutorService polite = Executors.newFixedThreadPool(1);
        CountDownLatch release = new CountDownLatch(1);
        polite.execute(() -> awaitQuietly(release));
        polite.execute(() -> System.out.println("queued task still runs after shutdown()"));
        polite.shutdown();
        System.out.println("isShutdown=" + polite.isShutdown() + ", isTerminated=" + polite.isTerminated());
        try {
            polite.execute(() -> System.out.println("never printed"));
        } catch (RejectedExecutionException e) {
            System.out.println("new work refused after shutdown()");
        }
        release.countDown();
        System.out.println("terminated in time: " + polite.awaitTermination(5, TimeUnit.SECONDS));

        // --- shutdownNow(): running task interrupted, queued tasks handed back ---
        ExecutorService rude = Executors.newFixedThreadPool(1);
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch neverReleased = new CountDownLatch(1);
        rude.execute(() -> {
            started.countDown();
            awaitQuietly(neverReleased);
        });
        started.await();
        rude.execute(() -> System.out.println("never printed"));
        rude.execute(() -> System.out.println("never printed either"));
        List<Runnable> neverStarted = rude.shutdownNow();
        System.out.println("tasks that never started: " + neverStarted.size());
        System.out.println("terminated in time: " + rude.awaitTermination(5, TimeUnit.SECONDS));
    }

    // ------------------------------------------------------------------
    // 5. ThreadPoolExecutor: core / queue / max
    // ------------------------------------------------------------------

    /**
     * {@code ThreadPoolExecutor} — the class behind almost every factory
     * method. Its decision order for each new task is what interviewers ask:
     *
     * <ol>
     *   <li>fewer than {@code corePoolSize} threads → start a new thread</li>
     *   <li>otherwise → put the task in the queue</li>
     *   <li>queue full and fewer than {@code maximumPoolSize} threads →
     *       start an extra thread</li>
     *   <li>otherwise → the rejection handler decides (default
     *       {@code AbortPolicy} throws {@code RejectedExecutionException})</li>
     * </ol>
     *
     * <p><b>Gotcha:</b> extra threads (step 3) are only created once the queue
     * is <em>full</em>. With an unbounded queue, {@code maximumPoolSize} is
     * never reached.
     *
     * <p>Here: core 1, max 2, queue capacity 1 — so the 4th task is rejected.
     */
    static void threadPoolExecutorDemo() throws InterruptedException {
        CountDownLatch release = new CountDownLatch(1);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                1, 2, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                new ThreadPoolExecutor.AbortPolicy());
        try {
            for (int i = 1; i <= 4; i++) {
                try {
                    pool.execute(() -> awaitQuietly(release));
                    System.out.println("task " + i + " accepted: threads=" + pool.getPoolSize()
                            + ", queued=" + pool.getQueue().size());
                } catch (RejectedExecutionException e) {
                    System.out.println("task " + i + " REJECTED: pool and queue are full");
                }
            }
        } finally {
            release.countDown();
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    // ------------------------------------------------------------------
    // 6. Rejection policies
    // ------------------------------------------------------------------

    /**
     * Rejection policies — what happens when the pool and queue are saturated.
     *
     * <ul>
     *   <li>{@code AbortPolicy} (default) — throws {@code RejectedExecutionException}</li>
     *   <li>{@code CallerRunsPolicy} — the <em>submitting</em> thread runs the
     *       task itself; this slows producers down and acts as natural
     *       back-pressure</li>
     *   <li>{@code DiscardPolicy} — silently drops the task</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> {@code DiscardPolicy} loses work without any signal.
     * Use it only for work that is genuinely optional.
     */
    static void rejectionPolicyDemo() throws InterruptedException {
        CountDownLatch release = new CountDownLatch(1);

        ThreadPoolExecutor callerRuns = new ThreadPoolExecutor(
                1, 1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                new ThreadPoolExecutor.CallerRunsPolicy());
        ThreadPoolExecutor discarding = new ThreadPoolExecutor(
                1, 1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                new ThreadPoolExecutor.DiscardPolicy());
        try {
            for (int i = 0; i < 2; i++) { // fill worker + queue
                callerRuns.execute(() -> awaitQuietly(release));
                discarding.execute(() -> awaitQuietly(release));
            }
            callerRuns.execute(() ->
                    System.out.println("CallerRunsPolicy: ran on " + Thread.currentThread().getName()));
            discarding.execute(() -> System.out.println("never printed: DiscardPolicy dropped me"));
            System.out.println("DiscardPolicy: no exception, task silently dropped");
        } finally {
            release.countDown();
            callerRuns.shutdown();
            discarding.shutdown();
            callerRuns.awaitTermination(5, TimeUnit.SECONDS);
            discarding.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    // ------------------------------------------------------------------
    // 7. ThreadFactory
    // ------------------------------------------------------------------

    /**
     * A custom {@code ThreadFactory} — name your threads.
     *
     * <p><b>Why it matters:</b> in a thread dump or a log line,
     * {@code pool-3-thread-2} tells you nothing, while {@code order-worker-2}
     * tells you exactly which subsystem is stuck. A factory can also mark
     * threads as daemon or install an uncaught-exception handler.
     *
     * <p><b>Gotcha:</b> a pool creates a <em>new</em> thread for each of the
     * first {@code corePoolSize} tasks even when earlier workers are idle, so
     * with a fixed pool of 2 the first two tasks always land on two
     * different threads.
     */
    static void threadFactoryDemo() throws InterruptedException, ExecutionException {
        final AtomicInteger counter = new AtomicInteger(1);
        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable, "order-worker-" + counter.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        };
        ExecutorService pool = Executors.newFixedThreadPool(2, factory);
        try {
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                futures.add(pool.submit(() -> {
                    Thread t = Thread.currentThread();
                    return t.getName() + " (daemon=" + t.isDaemon() + ")";
                }));
            }
            for (Future<String> future : futures) {
                System.out.println(future.get());
            }
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 8. Gotchas hidden in the Executors factory methods
    // ------------------------------------------------------------------

    /**
     * The {@code Executors} factories hide unbounded resources.
     *
     * <ul>
     *   <li>{@code newFixedThreadPool} uses an <em>unbounded</em> queue
     *       ({@code LinkedBlockingQueue}, capacity {@code Integer.MAX_VALUE}).
     *       A slow consumer lets the queue grow until {@code OutOfMemoryError}.</li>
     *   <li>{@code newCachedThreadPool} has
     *       {@code maximumPoolSize = Integer.MAX_VALUE}. A burst of blocking
     *       tasks can create thousands of threads.</li>
     * </ul>
     *
     * <p>Production code usually builds a {@code ThreadPoolExecutor} with a
     * bounded queue and an explicit rejection policy (section 5).
     *
     * <p>The casts below peek at an OpenJDK implementation detail (the
     * factories return {@code ThreadPoolExecutor}) purely to show the numbers.
     */
    static void factoryGotchasDemo() {
        ThreadPoolExecutor fixed = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        ThreadPoolExecutor cached = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        try {
            System.out.println("fixed pool queue remaining capacity: " + fixed.getQueue().remainingCapacity());
            System.out.println("cached pool maximum size: " + cached.getMaximumPoolSize());
        } finally {
            fixed.shutdown();
            cached.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    /** Blocks on the latch; restores the interrupt flag if interrupted. */
    private static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
