package com.vbforge.multithreading_advanced;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Callable, Future and friends.
 *
 * <p>{@code Runnable.run()} returns nothing and cannot throw a checked
 * exception, so a background computation has no clean way to hand back a
 * result or a failure. {@code Callable<V>} returns a value and may throw;
 * {@code Future<V>} is the receipt you get for a submitted task — a handle to
 * wait for, inspect or cancel it.
 *
 * <p>Run {@link #main(String[])} to see every section's output.
 */
public class CallableFutureDemo {

    public static void main(String[] args) throws Exception {
        callableVsRunnableDemo();
        System.out.println("---");
        futureGetDemo();
        System.out.println("---");
        exceptionHandlingDemo();
        System.out.println("---");
        cancellationDemo();
        System.out.println("---");
        invokeAllAndAnyDemo();
        System.out.println("---");
        futureTaskDemo();
    }

    // ------------------------------------------------------------------
    // 1. Callable vs Runnable
    // ------------------------------------------------------------------

    /**
     * {@code Callable<V>} vs {@code Runnable}.
     *
     * <ul>
     *   <li>{@code Callable.call()} returns {@code V} and may
     *       {@code throws Exception}.</li>
     *   <li>{@code Runnable.run()} returns {@code void} and throws nothing
     *       checked.</li>
     *   <li>{@code submit(Callable)} → {@code Future<V>} holding the value.</li>
     *   <li>{@code submit(Runnable)} → {@code Future<?>} whose {@code get()}
     *       is {@code null}; {@code submit(Runnable, T)} returns the fixed
     *       result you passed in.</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> {@code submit(() -> doSomething())} is a
     * {@code Callable} when the lambda body produces a value and a
     * {@code Runnable} when it does not — the compiler picks by the lambda's
     * shape.
     */
    static void callableVsRunnableDemo() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            Callable<Integer> answer = () -> 6 * 7;
            Runnable sideEffect = () -> System.out.println("a Runnable has no return value");

            Future<Integer> fromCallable = pool.submit(answer);
            Future<?> fromRunnable = pool.submit(sideEffect);
            Future<String> fromRunnableWithResult = pool.submit(sideEffect, "fixed result");

            System.out.println("Callable result: " + fromCallable.get());
            System.out.println("Runnable result: " + fromRunnable.get());
            System.out.println("Runnable with fixed result: " + fromRunnableWithResult.get());
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 2. Future.get(): blocking, timeouts
    // ------------------------------------------------------------------

    /**
     * {@code Future.get()} blocks the <em>calling</em> thread until the task
     * finishes.
     *
     * <ul>
     *   <li>{@code isDone()} — true when the task completed normally, failed
     *       or was cancelled; never blocks.</li>
     *   <li>{@code get()} — wait as long as it takes.</li>
     *   <li>{@code get(timeout, unit)} — wait at most that long, otherwise
     *       throw {@code TimeoutException}.</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> a {@code TimeoutException} only means <em>you</em>
     * stopped waiting — the task keeps running. Call
     * {@code future.cancel(true)} if you want it stopped. And a plain
     * {@code get()} with no timeout on a task that never finishes hangs
     * forever.
     */
    static void futureGetDemo() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            CountDownLatch gate = new CountDownLatch(1);
            Future<String> future = pool.submit(() -> {
                gate.await();
                return "finally done";
            });

            System.out.println("isDone before the gate opens: " + future.isDone());
            try {
                future.get(100, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                System.out.println("get(100ms) timed out; the task is still running");
            }

            gate.countDown();
            System.out.println("get() returned: " + future.get());
            System.out.println("isDone after get: " + future.isDone());
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 3. Exceptions: ExecutionException, submit vs execute
    // ------------------------------------------------------------------

    /**
     * Where do exceptions thrown inside a task go?
     *
     * <ul>
     *   <li>{@code submit(...)} — the exception is <em>captured</em> in the
     *       {@code Future}. Nothing is logged; {@code get()} rethrows it
     *       wrapped in an {@code ExecutionException}. The real error is
     *       {@code getCause()}.</li>
     *   <li>{@code execute(...)} — the exception propagates out of the worker
     *       thread to its {@code UncaughtExceptionHandler}.</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> fire-and-forget {@code submit(...)} <em>swallows</em>
     * failures. If you never call {@code get()}, you never learn the task
     * failed.
     */
    static void exceptionHandlingDemo() throws InterruptedException {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            Callable<String> failing = () -> {
                throw new IllegalStateException("boom");
            };
            Future<String> future = pool.submit(failing);
            try {
                future.get();
            } catch (ExecutionException e) {
                System.out.println("ExecutionException cause: " + e.getCause());
            }
            System.out.println("failed future isDone: " + future.isDone());
        } finally {
            pool.shutdown();
        }

        CountDownLatch handled = new CountDownLatch(1);
        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setUncaughtExceptionHandler((t, ex) -> {
                System.out.println("uncaught handler saw: " + ex);
                handled.countDown();
            });
            return thread;
        };
        ExecutorService executePool = Executors.newSingleThreadExecutor(factory);
        try {
            executePool.execute(() -> {
                throw new IllegalArgumentException("thrown from execute()");
            });
            handled.await();
        } finally {
            executePool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 4. Cancellation
    // ------------------------------------------------------------------

    /**
     * {@code Future.cancel(mayInterruptIfRunning)}.
     *
     * <ul>
     *   <li>Not started yet → it is simply never run.</li>
     *   <li>Running and {@code cancel(true)} → the worker thread is
     *       <em>interrupted</em>.</li>
     *   <li>Running and {@code cancel(false)} → the task is left to finish,
     *       but the future is already marked cancelled.</li>
     *   <li>After a successful cancel: {@code isCancelled()} and
     *       {@code isDone()} are both true and {@code get()} throws
     *       {@code CancellationException}.</li>
     *   <li>Already finished → {@code cancel} returns false.</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> cancellation is cooperative. The task only stops if it
     * reacts to the interrupt (blocking calls like {@code sleep} or
     * {@code await} do; a tight CPU loop does not unless it checks
     * {@code Thread.currentThread().isInterrupted()}).
     */
    static void cancellationDemo() throws InterruptedException {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch interrupted = new CountDownLatch(1);
            Future<?> future = pool.submit(() -> {
                started.countDown();
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException e) {
                    interrupted.countDown();
                }
            });

            started.await();
            boolean cancelled = future.cancel(true);
            interrupted.await();
            System.out.println("cancel(true) returned " + cancelled + ", task was interrupted");
            System.out.println("isCancelled=" + future.isCancelled() + ", isDone=" + future.isDone());
            try {
                future.get();
            } catch (CancellationException e) {
                System.out.println("get() on a cancelled future throws CancellationException");
            } catch (ExecutionException e) {
                System.out.println("unexpected: " + e);
            }
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 5. invokeAll / invokeAny
    // ------------------------------------------------------------------

    /**
     * {@code invokeAll} and {@code invokeAny} — batch submission.
     *
     * <ul>
     *   <li>{@code invokeAll(tasks)} — blocks until <em>every</em> task is
     *       done and returns the futures in the <em>same order as the input</em>,
     *       regardless of which finished first.</li>
     *   <li>{@code invokeAny(tasks)} — returns the result of one task that
     *       completed successfully and cancels the rest. If every task fails
     *       it throws {@code ExecutionException}.</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> {@code invokeAny} is the classic "ask three
     * mirrors, take the first good answer" pattern. {@code invokeAll}
     * replaces a hand-written loop of {@code submit} + {@code get}.
     *
     * <p><b>Gotcha:</b> {@code invokeAll} returns futures that may be
     * <em>failed</em>; calling {@code get()} on each still throws.
     */
    static void invokeAllAndAnyDemo() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            List<Callable<String>> jobs = Arrays.asList(
                    after(100, "slow"),
                    after(0, "fast"),
                    after(50, "medium"));
            List<Future<String>> futures = pool.invokeAll(jobs);
            for (Future<String> future : futures) {
                System.out.println("invokeAll in input order: " + future.get());
            }

            Callable<String> failing = () -> {
                throw new IllegalStateException("this mirror is down");
            };
            String winner = pool.invokeAny(Arrays.asList(failing, after(50, "healthy mirror")));
            System.out.println("invokeAny skipped the failure and returned: " + winner);
        } finally {
            pool.shutdown();
        }
    }

    // ------------------------------------------------------------------
    // 6. FutureTask
    // ------------------------------------------------------------------

    /**
     * {@code FutureTask<V>} is both a {@code Runnable} <em>and</em> a
     * {@code Future<V>}. It is what {@code submit(Callable)} creates internally
     * — you can use it directly to get a result out of a plain {@code Thread}.
     *
     * <p><b>Gotcha:</b> a {@code FutureTask} runs at most once. Calling
     * {@code run()} a second time does nothing.
     */
    static void futureTaskDemo() throws InterruptedException, ExecutionException {
        FutureTask<Integer> task = new FutureTask<Integer>(() -> 1 + 2);
        Thread thread = new Thread(task, "future-task-thread");
        thread.start();
        System.out.println("FutureTask result: " + task.get());
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private static Callable<String> after(final long millis, final String value) {
        return () -> {
            Thread.sleep(millis);
            return value;
        };
    }
}
