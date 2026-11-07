// IMPLEMENTATION
package com.vbforge.practice.practice_7_runnable_callable_future;

import java.util.List;
import java.util.concurrent.*;

/**
 * Demonstrates {@link Runnable} vs {@link Callable} — task abstractions for threads.
 *
 * Runnable  — a task that produces NO result and throws NO checked exception.
 *             Signature: void run()
 *
 * Callable<V> — a task that RETURNS a result of type V and MAY throw a checked exception.
 *             Signature: V call() throws Exception
 *
 * Why Callable matters:
 *   - Runnable cannot return a value; sharing results requires shared state (error-prone).
 *   - Callable pairs naturally with Future<V>, which holds the eventual result.
 *
 * Future<V> — a handle to an asynchronous computation:
 *   get()                 — blocks until result is ready (or throws ExecutionException)
 *   get(timeout, unit)    — blocks up to a timeout; throws TimeoutException
 *   isDone()              — non-blocking readiness check
 *   cancel(mayInterrupt)  — attempts cancellation
 *   isCancelled()         — true if cancelled before completion
 *
 * ExecutionException:
 *   If call() throws, the exception is wrapped in ExecutionException.
 *   Retrieve it via getCause().
 *
 * ExecutorService (brief preview — deep-dived in
 * multithreading_advanced/practice_2_executor_framework):
 *   submit(Runnable)  → Future<?>   (result is always null)
 *   submit(Callable)  → Future<V>
 *   invokeAll(tasks)  → List<Future<V>>  (waits for ALL)
 *   invokeAny(tasks)  → V               (returns first successful result)
 */
public class RunnableCallableDemo {

    // ── Runnable examples ─────────────────────────────────────────────────────

    /**
     * A simple Runnable — fire-and-forget logging task.
     * No return value, no checked exceptions.
     */
    static class LogTask implements Runnable {
        private final String message;

        LogTask(String message) { this.message = message; }

        @Override
        public void run() {
            System.out.printf("[%s] LOG: %s%n",
                    Thread.currentThread().getName(), message);
        }
    }

    // ── Callable examples ─────────────────────────────────────────────────────

    /**
     * A Callable that computes the sum of 1..n.
     * Returns a Long — the result is available via Future.get().
     */
    static class SumCallable implements Callable<Long> {
        private final int n;

        SumCallable(int n) { this.n = n; }

        @Override
        public Long call() {
            long sum = 0;
            for (int i = 1; i <= n; i++) sum += i;
            System.out.printf("[%s] SumCallable(%d) = %d%n",
                    Thread.currentThread().getName(), n, sum);
            return sum;
        }
    }

    /**
     * A Callable that simulates a task that MAY fail.
     * Demonstrates how exceptions surface through Future.get().
     */
    static class FlakyCallable implements Callable<String> {
        private final boolean shouldFail;

        FlakyCallable(boolean shouldFail) { this.shouldFail = shouldFail; }

        @Override
        public String call() throws Exception {
            Thread.sleep(50);
            if (shouldFail) throw new IllegalStateException("Simulated failure");
            return "Success from " + Thread.currentThread().getName();
        }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Runnable & Callable Demo ===\n");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            // --- 1. Runnable via executor ---
            System.out.println("-- Runnable (submit) --");
            Future<?> rFuture = executor.submit(new LogTask("task submitted via executor"));
            rFuture.get();   // wait for completion; result is always null
            System.out.println("Runnable future result: " + rFuture.get()); // null

            // --- 2. Runnable as lambda ---
            System.out.println("\n-- Runnable lambda --");
            executor.submit(() ->
                System.out.println("[" + Thread.currentThread().getName() + "] lambda runnable")
            ).get();

            // --- 3. Callable — normal result ---
            System.out.println("\n-- Callable (normal result) --");
            Future<Long> sumFuture = executor.submit(new SumCallable(100));
            Long result = sumFuture.get();    // blocks until done
            System.out.println("Sum(1..100) = " + result);
            System.out.println("isDone: " + sumFuture.isDone());

            // --- 4. Callable as lambda ---
            System.out.println("\n-- Callable lambda --");
            Future<String> strFuture = executor.submit(() -> {
                Thread.sleep(30);
                return "Lambda result from " + Thread.currentThread().getName();
            });
            System.out.println(strFuture.get());

            // --- 5. Callable — exception wrapped in ExecutionException ---
            System.out.println("\n-- Callable exception handling --");
            Future<String> failFuture = executor.submit(new FlakyCallable(true));
            try {
                failFuture.get();
            } catch (ExecutionException ex) {
                // The real cause is nested inside ExecutionException
                System.out.println("Caught ExecutionException");
                System.out.println("Cause: " + ex.getCause().getClass().getSimpleName()
                        + " — " + ex.getCause().getMessage());
            }

            // --- 6. invokeAll — submit multiple Callables, wait for ALL ---
            System.out.println("\n-- invokeAll --");
            List<Callable<Long>> tasks = List.of(
                    new SumCallable(10),
                    new SumCallable(50),
                    new SumCallable(100)
            );
            List<Future<Long>> futures = executor.invokeAll(tasks);
            for (Future<Long> f : futures) {
                System.out.println("invokeAll result: " + f.get());
            }

            // --- 7. invokeAny — returns FIRST successful result, cancels rest ---
            System.out.println("\n-- invokeAny --");
            List<Callable<String>> racers = List.of(
                    new FlakyCallable(false),
                    new FlakyCallable(false),
                    new FlakyCallable(false)
            );
            String first = executor.invokeAny(racers);
            System.out.println("invokeAny winner: " + first);

            // --- 8. Future.cancel() ---
            System.out.println("\n-- cancel() --");
            Future<Long> slowFuture = executor.submit(new SumCallable(Integer.MAX_VALUE));
            Thread.sleep(10);
            boolean cancelled = slowFuture.cancel(true);  // mayInterruptIfRunning = true
            System.out.println("cancelled: " + cancelled);
            System.out.println("isCancelled: " + slowFuture.isCancelled());

        } catch (ExecutionException e) {
            System.err.println("Unexpected: " + e.getCause());
        } finally {
            executor.shutdown();
        }

        System.out.println("\nMain done.");
    }
}