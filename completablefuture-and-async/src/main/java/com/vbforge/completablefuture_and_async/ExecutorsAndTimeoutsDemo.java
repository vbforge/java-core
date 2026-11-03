package com.vbforge.completablefuture_and_async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * Executors and timeouts for {@link CompletableFuture}.
 *
 * <p>Sections:
 * <ol>
 *   <li>The default executor: {@code ForkJoinPool.commonPool()}</li>
 *   <li>A custom executor — and why you must shut it down</li>
 *   <li>{@code thenApply} vs {@code thenApplyAsync}: which thread runs a stage?</li>
 *   <li>Timeouts in Java 8 — there is no {@code orTimeout} yet</li>
 *   <li>A timeout does not stop the source</li>
 * </ol>
 */
public class ExecutorsAndTimeoutsDemo {

    public static void main(String[] args) {
        commonPoolDemo();
        System.out.println("---");
        customExecutorDemo();
        System.out.println("---");
        asyncVariantsDemo();
        System.out.println("---");
        timeoutDemo();
        System.out.println("---");
        timeoutDoesNotStopTheSourceDemo();
    }

    // ------------------------------------------------------------------
    // 1. The default executor: ForkJoinPool.commonPool()
    //
    // What:   supplyAsync(supplier) / runAsync(runnable) without an
    //         executor argument run on the JVM-wide common pool, which has
    //         about (CPU cores - 1) threads.
    // Why:    Zero setup for short, CPU-bound work.
    // Cost:   - the pool is SHARED with parallel streams and every other
    //           CompletableFuture in the JVM
    // Gotcha: blocking calls (JDBC, HTTP, sleep) on the common pool eat
    //         its few threads and stall unrelated work. Give blocking
    //         work its own executor (section 2). Its threads are also
    //         daemon threads: they do not keep the JVM alive.
    // ------------------------------------------------------------------
    public static void commonPoolDemo() {
        String caller = Thread.currentThread().getName();
        CompletableFuture<String> worker = CompletableFuture.supplyAsync(() -> Thread.currentThread().getName());
        System.out.println("caller thread        : " + caller);
        System.out.println("supplyAsync thread   : " + worker.join());
        System.out.println("common pool parallelism : " + ForkJoinPool.getCommonPoolParallelism());
    }

    // ------------------------------------------------------------------
    // 2. A custom executor — and why you must shut it down
    //
    // What:   Every *Async method has an overload that takes an Executor.
    //         Pass your own pool to isolate blocking work and to size the
    //         pool for the job (e.g. 20 threads for 20 parallel HTTP calls).
    // Why:    Bulkheads: slow I/O in one pool cannot starve the rest.
    // Cost:   - a fixed pool of n threads: O(n) memory, tasks beyond n wait
    //           in the pool's queue
    // Gotcha: threads of Executors.newFixedThreadPool(...) are NON-daemon.
    //         Forget shutdown() and the JVM never exits. After shutdown()
    //         the pool rejects new tasks.
    // ------------------------------------------------------------------

    /** Runs a task after the pool has been shut down; returns true if the pool rejected it. */
    public static boolean submitAfterShutdownIsRejected() {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.shutdown();
        try {
            CompletableFuture.supplyAsync(() -> "too late", pool);
            return false;
        } catch (RejectedExecutionException e) {
            return true;
        }
    }

    public static void customExecutorDemo() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            CompletableFuture<String> onMyPool = CompletableFuture.supplyAsync(
                    () -> Thread.currentThread().getName(), pool);
            System.out.println("runs on custom pool  : " + onMyPool.join());
        } finally {
            pool.shutdown();
            try {
                pool.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("pool terminated      : " + pool.isTerminated());
        System.out.println("submit after shutdown rejected : " + submitAfterShutdownIsRejected());
    }

    // ------------------------------------------------------------------
    // 3. thenApply vs thenApplyAsync — which thread runs a stage?
    //
    // What:   - thenApply(fn): runs on the thread that completes the
    //           source — or, if the source is ALREADY complete when you
    //           attach the stage, right on the calling thread.
    //         - thenApplyAsync(fn): always hands fn to the common pool.
    //         - thenApplyAsync(fn, executor): hands fn to YOUR executor.
    // Why:    It decides where your code runs: keep cheap transformations
    //         on the completing thread; move heavy or blocking steps to a
    //         pool you control.
    // Gotcha: a slow thenApply callback blocks whichever thread happened
    //         to complete the source — possibly a small I/O thread.
    // ------------------------------------------------------------------
    public static void asyncVariantsDemo() {
        ExecutorService stagePool = Executors.newSingleThreadExecutor();
        try {
            String caller = Thread.currentThread().getName();
            CompletableFuture<String> ready = CompletableFuture.completedFuture("x");

            String plain = ready.thenApply(v -> Thread.currentThread().getName()).join();
            String async = ready.thenApplyAsync(v -> Thread.currentThread().getName()).join();
            String custom = ready.thenApplyAsync(v -> Thread.currentThread().getName(), stagePool).join();

            System.out.println("caller               : " + caller);
            System.out.println("thenApply            : " + plain + "   (same as caller)");
            System.out.println("thenApplyAsync       : " + async + "   (common pool)");
            System.out.println("thenApplyAsync + pool: " + custom + "   (my pool)");
        } finally {
            stagePool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 4. Timeouts in Java 8 — there is no orTimeout yet
    //
    // What:   Java 9 added orTimeout(...) and completeOnTimeout(...).
    //         On Java 8 you build the same thing: a second future that a
    //         ScheduledExecutorService completes exceptionally after the
    //         delay, raced against the real one with applyToEither.
    // Why:    Future.get(timeout) blocks a thread; this version stays
    //         non-blocking and composes with the rest of the pipeline.
    // Cost:   - one scheduled task per call, O(1) memory each
    //         - refinement (not shown): cancel the timer when the source
    //           wins, so finished calls leave nothing queued
    // Gotcha: the scheduler is a resource — create it once, share it, and
    //         shut it down when the application stops.
    // ------------------------------------------------------------------
    public static <T> CompletableFuture<T> withTimeout(CompletableFuture<T> source,
                                                       ScheduledExecutorService scheduler,
                                                       long millis) {
        CompletableFuture<T> timeout = new CompletableFuture<>();
        scheduler.schedule(
                () -> timeout.completeExceptionally(new TimeoutException("timed out after " + millis + " ms")),
                millis, TimeUnit.MILLISECONDS);
        return source.applyToEither(timeout, Function.identity());
    }

    public static void timeoutDemo() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            CompletableFuture<String> never = new CompletableFuture<>();
            try {
                withTimeout(never, scheduler, 100).join();
            } catch (CompletionException e) {
                System.out.println("slow source          : " + e.getCause().getClass().getSimpleName()
                        + " - " + e.getCause().getMessage());
            }

            CompletableFuture<String> quick = CompletableFuture.completedFuture("fast enough");
            System.out.println("fast source          : " + withTimeout(quick, scheduler, 2000).join());
        } finally {
            scheduler.shutdownNow();
        }
    }

    // ------------------------------------------------------------------
    // 5. A timeout does not stop the source
    //
    // What:   withTimeout fails the WRAPPER future. The original future —
    //         and whatever work feeds it — is untouched.
    // Why:    Knowing this prevents surprises: the slow call still runs,
    //         still holds its connection, and may complete later.
    // Gotcha: if you need the work itself to stop, you need cooperative
    //         cancellation (a flag, an interrupt, a client-level timeout);
    //         cancel() on a CompletableFuture will not interrupt anything.
    // ------------------------------------------------------------------
    public static void timeoutDoesNotStopTheSourceDemo() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            CompletableFuture<String> source = new CompletableFuture<>();
            CompletableFuture<String> guarded = withTimeout(source, scheduler, 50);

            guarded.handle((value, error) -> null).join();

            System.out.println("guarded failed       : " + guarded.isCompletedExceptionally());
            System.out.println("source still pending : " + !source.isDone());
            System.out.println("late value accepted  : " + source.complete("late value"));
        } finally {
            scheduler.shutdownNow();
        }
    }
}
