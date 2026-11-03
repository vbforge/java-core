package com.vbforge.completablefuture_and_async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Error handling in {@link CompletableFuture} pipelines.
 *
 * <p>Sections:
 * <ol>
 *   <li>A failure skips every dependent stage</li>
 *   <li>{@code exceptionally} — recover with a fallback value</li>
 *   <li>{@code CompletionException} wrapping — what the handler actually receives</li>
 *   <li>{@code handle} — one callback for success and failure</li>
 *   <li>{@code whenComplete} — observe without changing the outcome</li>
 *   <li>Recovering with another async call (the Java 8 way)</li>
 *   <li>{@code cancel} — it does not stop running code</li>
 * </ol>
 */
public class ErrorHandlingDemo {

    public static void main(String[] args) {
        failureSkipsDependentsDemo();
        System.out.println("---");
        exceptionallyDemo();
        System.out.println("---");
        wrappingDemo();
        System.out.println("---");
        handleDemo();
        System.out.println("---");
        whenCompleteDemo();
        System.out.println("---");
        recoverWithBackupDemo();
        System.out.println("---");
        cancelDemo();
    }

    // ------------------------------------------------------------------
    // 1. A failure skips every dependent stage
    //
    // What:   When a stage fails, thenApply / thenAccept / thenCompose /
    //         thenCombine ... downstream are NOT executed. The failure
    //         travels down the chain until a handler (exceptionally,
    //         handle, whenComplete) deals with it.
    // Why:    Same idea as an exception unwinding the call stack: you
    //         write the happy path once and handle errors at the end.
    // Gotcha: nobody throws into your thread. If you never attach a
    //         handler and never join(), the failure is silently lost.
    // ------------------------------------------------------------------
    public static void failureSkipsDependentsDemo() {
        AtomicInteger executed = new AtomicInteger();

        CompletableFuture<Integer> chain = ComposingDemo.<String>failed(new IllegalStateException("upstream failed"))
                .thenApply(s -> {
                    executed.incrementAndGet();
                    return s.length();
                })
                .thenApply(n -> {
                    executed.incrementAndGet();
                    return n * 2;
                });

        System.out.println("chain failed         : " + chain.isCompletedExceptionally());
        System.out.println("dependents executed  : " + executed.get());
    }

    // ------------------------------------------------------------------
    // 2. exceptionally — recover with a fallback value
    //
    // What:   exceptionally(Function<Throwable, T>) runs ONLY on failure
    //         and supplies a replacement value of the same type T.
    // Why:    It is the async catch block: "if the price service is down,
    //         use the cached price".
    // Gotcha: on success the function is skipped and the value passes
    //         through unchanged. It cannot change the result TYPE — use
    //         handle (section 4) for that.
    // ------------------------------------------------------------------
    public static void exceptionallyDemo() {
        CompletableFuture<Integer> price = ComposingDemo.<Integer>failed(new IllegalStateException("price service down"))
                .exceptionally(error -> -1);
        CompletableFuture<Integer> healthy = CompletableFuture.completedFuture(99)
                .exceptionally(error -> -1);

        System.out.println("failed + fallback    : " + price.join());
        System.out.println("healthy (untouched)  : " + healthy.join());
    }

    // ------------------------------------------------------------------
    // 3. CompletionException wrapping — what the handler actually receives
    //
    // What:   The Throwable your handler sees depends on WHERE the future
    //         failed:
    //           - future completed with completeExceptionally(ex), handler
    //             attached to that same future  -> the raw ex
    //           - a DEPENDENT stage of that future -> CompletionException(ex)
    //           - supplyAsync whose supplier threw -> CompletionException(ex)
    // Why:    Exceptions crossing a thread boundary are wrapped so that
    //         they can be rethrown unchecked from join().
    // Gotcha: `error instanceof IllegalStateException` can be false even
    //         though that is the real problem. Unwrap getCause() first
    //         (see ErrorHandlingTasks.unwrap).
    // ------------------------------------------------------------------

    /** Returns the exact class of the Throwable that {@code exceptionally} receives for {@code future}. */
    public static <T> Class<? extends Throwable> exceptionSeenBy(CompletableFuture<T> future) {
        AtomicReference<Throwable> seen = new AtomicReference<>();
        future.exceptionally(error -> {
            seen.set(error);
            return null;
        }).join();
        return seen.get().getClass();
    }

    public static void wrappingDemo() {
        CompletableFuture<String> raw = ComposingDemo.failed(new IllegalStateException("original"));
        CompletableFuture<String> dependent = raw.thenApply(s -> s);
        CompletableFuture<String> thrown = CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException("original");
        });

        System.out.println("completeExceptionally, same future : " + exceptionSeenBy(raw).getSimpleName());
        System.out.println("dependent stage                     : " + exceptionSeenBy(dependent).getSimpleName());
        System.out.println("supplyAsync that threw              : " + exceptionSeenBy(thrown).getSimpleName());
    }

    // ------------------------------------------------------------------
    // 4. handle — one callback for success AND failure
    //
    // What:   handle(BiFunction<T, Throwable, U>) always runs. Exactly one
    //         of its arguments is null: the value on success, the error on
    //         failure. It returns a NEW value, possibly of a different type.
    // Why:    Turn "value or error" into one uniform result (a label, a
    //         DTO, a default) in a single place.
    // Gotcha: on success the Throwable is null — check it before use.
    // ------------------------------------------------------------------
    public static String label(CompletableFuture<Integer> source) {
        return source.handle((value, error) ->
                error == null ? "value=" + value : "error=" + error.getClass().getSimpleName()).join();
    }

    public static void handleDemo() {
        System.out.println("handle on success   : " + label(CompletableFuture.completedFuture(21)));
        System.out.println("handle on failure   : "
                + label(ComposingDemo.<Integer>failed(new IllegalStateException("nope"))));
    }

    // ------------------------------------------------------------------
    // 5. whenComplete — observe without changing the outcome
    //
    // What:   whenComplete(BiConsumer<T, Throwable>) runs on success AND
    //         failure and returns a future with the SAME outcome as the
    //         source. It is a peek, not a transform.
    // Why:    Cleanup, logging, metrics: things that must happen but must
    //         not alter the result.
    // Gotcha: if the source succeeded but your action throws, the returned
    //         future FAILS with your exception. If the source had already
    //         failed, the ORIGINAL exception is kept.
    // ------------------------------------------------------------------
    public static void whenCompleteDemo() {
        List<String> audit = new ArrayList<>();

        CompletableFuture<String> good = CompletableFuture.completedFuture("v")
                .whenComplete((value, error) -> audit.add("success:" + value));
        CompletableFuture<String> bad = ComposingDemo.<String>failed(new IllegalStateException("x"))
                .whenComplete((value, error) -> audit.add("failure:" + error.getMessage()));

        System.out.println("good value kept     : " + good.join());
        System.out.println("bad still failed    : " + bad.isCompletedExceptionally());
        System.out.println("audit trail         : " + audit);
    }

    // ------------------------------------------------------------------
    // 6. Recovering with ANOTHER async call (the Java 8 way)
    //
    // What:   exceptionally only gives you a plain value. To recover with a
    //         new CompletableFuture, Java 8 has no exceptionallyCompose
    //         (it arrived in Java 12). The standard trick:
    //           handle((v, e) -> e == null ? completedFuture(v) : backup)
    //           .thenCompose(Function.identity())
    // Why:    "Ask the primary service; if it fails, ask the backup."
    // Cost:   - the backup is only started when the primary FAILED
    // Gotcha: backup is a Supplier on purpose — passing an already-created
    //         future would start the backup call even when it is not needed.
    // ------------------------------------------------------------------
    public static <T> CompletableFuture<T> recoverWith(CompletableFuture<T> primary,
                                                       Supplier<CompletableFuture<T>> backup) {
        return primary
                .handle((value, error) -> error == null ? CompletableFuture.completedFuture(value) : backup.get())
                .thenCompose(Function.identity());
    }

    public static void recoverWithBackupDemo() {
        AtomicInteger backupCalls = new AtomicInteger();
        Supplier<CompletableFuture<String>> backup = () -> {
            backupCalls.incrementAndGet();
            return CompletableFuture.supplyAsync(() -> "from backup");
        };

        String fromPrimary = recoverWith(CompletableFuture.completedFuture("from primary"), backup).join();
        System.out.println("primary healthy     : " + fromPrimary + ", backup calls = " + backupCalls.get());

        String fromBackup = recoverWith(ComposingDemo.<String>failed(new IllegalStateException("primary down")), backup)
                .join();
        System.out.println("primary failed      : " + fromBackup + ", backup calls = " + backupCalls.get());
    }

    // ------------------------------------------------------------------
    // 7. cancel — it does not stop running code
    //
    // What:   cancel(...) completes the future with a CancellationException
    //         (which counts as "completed exceptionally") and propagates
    //         that to dependents.
    // Why:    Callers can stop WAITING for a result they no longer need.
    // Gotcha: the boolean passed to cancel(mayInterruptIfRunning) has NO
    //         effect on CompletableFuture. A supplier that has already
    //         started keeps running to the end; only its result is dropped.
    // ------------------------------------------------------------------

    /**
     * Starts a supplier, waits until it is running, cancels the future, lets the
     * supplier continue, and reports whether its side effect still happened.
     */
    public static boolean cancelledSupplierStillRuns() {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(1);
        AtomicBoolean sideEffect = new AtomicBoolean(false);

        CompletableFuture<String> job = CompletableFuture.supplyAsync(() -> {
            started.countDown();
            awaitQuietly(release);
            sideEffect.set(true);
            finished.countDown();
            return "result";
        });

        awaitQuietly(started);
        job.cancel(true);
        release.countDown();
        awaitQuietly(finished);
        return sideEffect.get();
    }

    public static void cancelDemo() {
        CompletableFuture<String> pending = new CompletableFuture<>();
        boolean cancelled = pending.cancel(true);
        System.out.println("cancel() returned   : " + cancelled);
        System.out.println("isCancelled         : " + pending.isCancelled());
        System.out.println("isCompletedExceptionally : " + pending.isCompletedExceptionally());
        try {
            pending.join();
        } catch (CancellationException e) {
            System.out.println("join()              : CancellationException");
        }
        System.out.println("cancelled supplier still ran to the end : " + cancelledSupplierStillRuns());
    }

    private static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
