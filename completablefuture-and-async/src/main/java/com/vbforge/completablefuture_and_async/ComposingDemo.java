package com.vbforge.completablefuture_and_async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Composing asynchronous work with {@link CompletableFuture}.
 *
 * <p>A {@code CompletableFuture<T>} is a {@code Future<T>} that you can
 * <b>complete yourself</b> and <b>attach follow-up steps to</b>. Instead of
 * blocking a thread on {@code get()}, you describe a pipeline of stages and let
 * whichever thread finishes a stage run the next one.
 *
 * <p>Sections:
 * <ol>
 *   <li>{@code supplyAsync} / {@code runAsync} / {@code completedFuture} / manual completion</li>
 *   <li>{@code thenApply} — transform a result</li>
 *   <li>{@code thenAccept} / {@code thenRun} — consume a result</li>
 *   <li>{@code thenCompose} — chain a step that is itself asynchronous</li>
 *   <li>{@code thenCombine} — merge two independent futures</li>
 *   <li>{@code join} vs {@code get}</li>
 * </ol>
 */
public class ComposingDemo {

    public static void main(String[] args) {
        supplyAsyncDemo();
        System.out.println("---");
        thenApplyDemo();
        System.out.println("---");
        thenAcceptAndRunDemo();
        System.out.println("---");
        thenComposeDemo();
        System.out.println("---");
        thenCombineDemo();
        System.out.println("---");
        joinVsGetDemo();
    }

    // ------------------------------------------------------------------
    // Small "services" shared by the demo and its tests. Each one returns
    // a CompletableFuture, exactly like a real async client would.
    // ------------------------------------------------------------------

    /** Pretend remote call: user id → user name. */
    public static CompletableFuture<String> findUserName(int userId) {
        return CompletableFuture.supplyAsync(() -> "user-" + userId);
    }

    /** Pretend remote call: user name → e-mail address. */
    public static CompletableFuture<String> findEmail(String userName) {
        return CompletableFuture.supplyAsync(() -> userName + "@example.com");
    }

    /** Pretend remote call: user id → department. */
    public static CompletableFuture<String> findDepartment(int userId) {
        return CompletableFuture.supplyAsync(() -> "dept-" + (userId % 3));
    }

    /** Two independent calls merged with thenCombine. */
    public static CompletableFuture<String> userCard(int userId) {
        CompletableFuture<String> name = findUserName(userId);
        CompletableFuture<String> department = findDepartment(userId);
        return name.thenCombine(department, (n, d) -> n + " @ " + d);
    }

    /** Java 8 has no CompletableFuture.failedFuture (that arrived in 9) — build one by hand. */
    public static <T> CompletableFuture<T> failed(Throwable error) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(error);
        return future;
    }

    // ------------------------------------------------------------------
    // 1. supplyAsync / runAsync / completedFuture / manual completion
    //
    // What:   supplyAsync(Supplier) runs a computation on another thread
    //         (by default ForkJoinPool.commonPool()) and returns at once.
    //         runAsync(Runnable) is the same without a result
    //         (CompletableFuture<Void>). completedFuture(v) wraps a value
    //         that is already known, and `new CompletableFuture<>()` is an
    //         empty box that YOU fill later with complete(...).
    // Why:    Future.get() can only block. CompletableFuture lets you say
    //         "when this finishes, do that" without parking a thread.
    // Cost:   - creating a future: O(1), the work itself runs elsewhere
    //         - one small object per stage you add
    // Gotcha: the default pool's threads are daemon threads. If main()
    //         returns before the async work finishes, the JVM can exit and
    //         the work is silently lost — join() before you leave.
    // ------------------------------------------------------------------
    public static void supplyAsyncDemo() {
        CompletableFuture<String> supplied = CompletableFuture.supplyAsync(() -> "computed elsewhere");
        System.out.println("supplyAsync     : " + supplied.join());

        CompletableFuture<Void> ran = CompletableFuture.runAsync(() -> { });
        System.out.println("runAsync        : " + ran.join() + "   (Void is always null)");

        CompletableFuture<String> ready = CompletableFuture.completedFuture("already known");
        System.out.println("completedFuture : isDone=" + ready.isDone());

        CompletableFuture<String> manual = new CompletableFuture<>();
        System.out.println("manual (before) : isDone=" + manual.isDone());
        boolean first = manual.complete("filled by hand");
        boolean second = manual.complete("too late");
        System.out.println("manual (after)  : " + manual.join()
                + ", first complete()=" + first + ", second complete()=" + second);
    }

    // ------------------------------------------------------------------
    // 2. thenApply — transform a result
    //
    // What:   thenApply(Function) returns a NEW future whose value is the
    //         function applied to this future's value. The original future
    //         is untouched.
    // Why:    It is Stream.map / Optional.map for a value that is still
    //         "on its way". No thread waits for it.
    // Cost:   - O(1) to attach; the function runs once, when the value arrives
    // Gotcha: if the source fails, the function is SKIPPED and the new
    //         future fails with the same error (see ErrorHandlingDemo).
    // ------------------------------------------------------------------
    public static void thenApplyDemo() {
        CompletableFuture<String> name = findUserName(1);
        CompletableFuture<Integer> length = name.thenApply(String::length);
        CompletableFuture<String> shout = name.thenApply(String::toUpperCase).thenApply(s -> s + "!");

        System.out.println("original : " + name.join());
        System.out.println("length   : " + length.join());
        System.out.println("shouted  : " + shout.join());
    }

    // ------------------------------------------------------------------
    // 3. thenAccept / thenRun — consume a result
    //
    // What:   thenAccept(Consumer) receives the value and produces
    //         CompletableFuture<Void>. thenRun(Runnable) ignores the value.
    // Why:    End of a pipeline: log it, store it, update the UI. The
    //         returned Void future still tells you WHEN the side effect
    //         has finished, so you can wait for it.
    // Gotcha: there is nothing to read from the Void future — keep the
    //         value in a variable earlier in the chain if you still need it.
    // ------------------------------------------------------------------
    public static void thenAcceptAndRunDemo() {
        CompletableFuture<Void> accepted = findUserName(2)
                .thenAccept(n -> System.out.println("thenAccept got : " + n));
        System.out.println("thenAccept value: " + accepted.join());

        CompletableFuture<Void> ran = findUserName(3)
                .thenRun(() -> System.out.println("thenRun ran    : (no input available)"));
        ran.join();
    }

    // ------------------------------------------------------------------
    // 4. thenCompose — chain a step that is itself asynchronous
    //
    // What:   thenCompose(Function<T, CompletionStage<U>>) is flatMap for
    //         futures: your function returns a future, and thenCompose
    //         "unwraps" it into a single CompletableFuture<U>.
    // Why:    Dependent async calls ("find the user, THEN find their e-mail")
    //         are everywhere. thenApply would give you
    //         CompletableFuture<CompletableFuture<U>> — a box in a box.
    // Cost:   - O(1) to attach; total latency is the SUM of both calls
    //           (the second cannot start before the first finishes)
    // Gotcha: if the two calls do NOT depend on each other, use
    //         thenCombine (section 5) so they run in parallel.
    // ------------------------------------------------------------------
    public static void thenComposeDemo() {
        CompletableFuture<CompletableFuture<String>> nested =
                findUserName(4).thenApply(ComposingDemo::findEmail);
        System.out.println("thenApply   : nested future, unwrap twice -> " + nested.join().join());

        CompletableFuture<String> flat = findUserName(4).thenCompose(ComposingDemo::findEmail);
        System.out.println("thenCompose : flat future                 -> " + flat.join());
    }

    // ------------------------------------------------------------------
    // 5. thenCombine — merge two independent futures
    //
    // What:   a.thenCombine(b, BiFunction) completes when BOTH a and b
    //         are done, with the function applied to both values.
    // Why:    Independent calls should overlap. Both futures below are
    //         already running by the time thenCombine is called, so total
    //         latency is max(a, b) rather than a + b.
    // Cost:   - O(1) to attach; waits for the slower of the two
    // Gotcha: thenCombine only works for TWO futures. For N futures use
    //         allOf (AllOfAnyOfDemo).
    // ------------------------------------------------------------------
    public static void thenCombineDemo() {
        System.out.println("userCard(5)     : " + userCard(5).join());

        CompletableFuture<Integer> left = new CompletableFuture<>();
        CompletableFuture<Integer> right = new CompletableFuture<>();
        CompletableFuture<Integer> sum = left.thenCombine(right, Integer::sum);

        left.complete(20);
        System.out.println("left done only  : combined isDone=" + sum.isDone());
        right.complete(22);
        System.out.println("both done       : combined = " + sum.join());
    }

    // ------------------------------------------------------------------
    // 6. join vs get
    //
    // What:   Both block until the future is done. get() throws CHECKED
    //         exceptions (InterruptedException, ExecutionException) and has
    //         a timed overload. join() throws only the UNCHECKED
    //         CompletionException.
    // Why:    join() fits lambdas and stream pipelines (no try/catch),
    //         get(timeout, unit) is the way to give up waiting.
    // Gotcha: the real error is the CAUSE of the exception you catch —
    //         never log only e.getMessage() of the wrapper.
    // ------------------------------------------------------------------
    public static void joinVsGetDemo() {
        CompletableFuture<String> broken = failed(new IllegalStateException("boom"));

        try {
            broken.join();
        } catch (CompletionException e) {
            System.out.println("join -> " + e.getClass().getSimpleName()
                    + ", cause = " + e.getCause().getMessage());
        }

        try {
            broken.get();
        } catch (ExecutionException e) {
            System.out.println("get  -> " + e.getClass().getSimpleName()
                    + ", cause = " + e.getCause().getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        CompletableFuture<String> never = new CompletableFuture<>();
        try {
            never.get(50, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.out.println("get(50ms) on a future nobody completes -> TimeoutException");
        } catch (ExecutionException e) {
            System.out.println("unexpected: " + e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
