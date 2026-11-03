package com.vbforge.completablefuture_and_async;

import java.util.concurrent.CompletableFuture;

/**
 * Waiting for <b>many</b> futures: {@code allOf}, {@code anyOf}, and the typed
 * "either of two" stages.
 *
 * <p>Sections:
 * <ol>
 *   <li>{@code allOf} — wait for everything</li>
 *   <li>Gathering results after {@code allOf}</li>
 *   <li>{@code allOf} and failures — no fail-fast</li>
 *   <li>{@code anyOf} — the first one wins</li>
 *   <li>{@code anyOf} pitfall — the first <i>completed</i>, not the first <i>successful</i></li>
 *   <li>{@code applyToEither} / {@code acceptEither} — the typed alternative for two futures</li>
 * </ol>
 */
public class AllOfAnyOfDemo {

    public static void main(String[] args) {
        allOfDemo();
        System.out.println("---");
        gatheringResultsDemo();
        System.out.println("---");
        allOfFailureDemo();
        System.out.println("---");
        anyOfDemo();
        System.out.println("---");
        anyOfPitfallDemo();
        System.out.println("---");
        applyToEitherDemo();
    }

    /** Pretend remote call that answers after {@code delayMillis}. */
    public static CompletableFuture<String> responseAfter(String value, long delayMillis) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return value;
        });
    }

    // ------------------------------------------------------------------
    // 1. allOf — wait for everything
    //
    // What:   allOf(f1, f2, ...) returns a CompletableFuture<Void> that
    //         completes when EVERY input future has completed.
    // Why:    Fan-out / fan-in: start N independent calls, continue when
    //         the last one is back. Total latency ≈ the slowest call, not
    //         the sum of all calls.
    // Cost:   - O(n) to build (it registers on each input)
    //         - no extra threads: the thread finishing the LAST input
    //           completes the allOf future
    // Gotcha: the result is Void. allOf tells you WHEN everything is done,
    //         not WHAT the values are — see section 2.
    // ------------------------------------------------------------------
    public static void allOfDemo() {
        CompletableFuture<String> a = responseAfter("A", 30);
        CompletableFuture<String> b = responseAfter("B", 60);
        CompletableFuture<String> c = responseAfter("C", 10);

        CompletableFuture<Void> all = CompletableFuture.allOf(a, b, c);

        System.out.println("allOf value     : " + all.join() + "   (Void)");
        System.out.println("all three done  : " + (a.isDone() && b.isDone() && c.isDone()));
    }

    // ------------------------------------------------------------------
    // 2. Gathering results after allOf
    //
    // What:   Once allOf has completed, every input is done, so calling
    //         join() on each one inside thenApply returns immediately.
    // Why:    It is the standard recipe for "wait for all, then use all".
    // Cost:   - O(n) joins, none of which actually blocks
    // Gotcha: join() inside thenApply is only safe BECAUSE allOf already
    //         completed. Calling join() on a still-running future inside a
    //         stage would block a pool thread.
    // ------------------------------------------------------------------
    public static CompletableFuture<Integer> totalOfThree(CompletableFuture<Integer> p1,
                                                          CompletableFuture<Integer> p2,
                                                          CompletableFuture<Integer> p3) {
        return CompletableFuture.allOf(p1, p2, p3)
                .thenApply(ignored -> p1.join() + p2.join() + p3.join());
    }

    public static void gatheringResultsDemo() {
        CompletableFuture<Integer> total = totalOfThree(
                CompletableFuture.supplyAsync(() -> 10),
                CompletableFuture.supplyAsync(() -> 20),
                CompletableFuture.supplyAsync(() -> 12));

        System.out.println("total of three : " + total.join());
    }

    // ------------------------------------------------------------------
    // 3. allOf and failures — no fail-fast
    //
    // What:   If ANY input fails, the allOf future fails too — but only
    //         after ALL inputs have completed.
    // Why:    This is a feature (you never abandon half-finished work) and
    //         a trap (one early failure does not stop you waiting for the
    //         slowest call).
    // Gotcha: if you want to react to the first failure, attach a
    //         whenComplete / exceptionally to each input yourself.
    // ------------------------------------------------------------------
    public static void allOfFailureDemo() {
        CompletableFuture<String> failing = new CompletableFuture<>();
        CompletableFuture<String> slow = new CompletableFuture<>();
        CompletableFuture<Void> all = CompletableFuture.allOf(failing, slow);

        failing.completeExceptionally(new IllegalStateException("one service down"));
        System.out.println("one failed, one pending -> allOf done? " + all.isDone());

        slow.complete("slow service finished");
        System.out.println("last one completed      -> allOf failed? " + all.isCompletedExceptionally());
    }

    // ------------------------------------------------------------------
    // 4. anyOf — the first one wins
    //
    // What:   anyOf(f1, f2, ...) completes as soon as ONE input completes,
    //         with that input's value (or failure).
    // Why:    Racing redundant sources — mirrors, replicas, caches vs
    //         origin — and taking whichever answers first.
    // Cost:   - O(n) to build; the losers keep running (anyOf does not
    //           cancel them)
    // Gotcha: the result type is CompletableFuture<Object> because the
    //         inputs may have different types. You must cast, and the
    //         compiler cannot check the cast for you.
    // ------------------------------------------------------------------
    public static void anyOfDemo() {
        CompletableFuture<String> slow = responseAfter("slow mirror", 200);
        CompletableFuture<String> fast = responseAfter("fast mirror", 20);

        CompletableFuture<Object> first = CompletableFuture.anyOf(slow, fast);
        String winner = (String) first.join();

        System.out.println("winner          : " + winner);
        System.out.println("loser still running? " + !slow.isDone());
    }

    // ------------------------------------------------------------------
    // 5. anyOf pitfall — first COMPLETED, not first SUCCESSFUL
    //
    // What:   A future that fails quickly "completes" quickly.
    // Why:    People read anyOf as "first good answer". It is really
    //         "first answer of any kind" — an early error beats a later
    //         success.
    // Gotcha: to take the first SUCCESS you must filter failures yourself
    //         (see AllOfAnyOfTasks.firstSuccessful).
    // ------------------------------------------------------------------
    public static void anyOfPitfallDemo() {
        CompletableFuture<String> failsFast = ComposingDemo.failed(new IllegalStateException("replica 1 down"));
        CompletableFuture<String> succeedsLater = new CompletableFuture<>();

        CompletableFuture<Object> first = CompletableFuture.anyOf(failsFast, succeedsLater);

        System.out.println("anyOf failed    : " + first.isCompletedExceptionally());
        succeedsLater.complete("replica 2 answer (too late)");
        System.out.println("late success    : ignored, result still failed = " + first.isCompletedExceptionally());
    }

    // ------------------------------------------------------------------
    // 6. applyToEither / acceptEither — typed "either of two"
    //
    // What:   a.applyToEither(b, fn) applies fn to whichever of the two
    //         futures completes first. Both must have the same type T.
    // Why:    For exactly two futures it is typed — no Object, no cast.
    //         It is also the building block for timeouts (see
    //         ExecutorsAndTimeoutsDemo).
    // Gotcha: like anyOf, an early FAILURE of either future wins too.
    // ------------------------------------------------------------------
    public static void applyToEitherDemo() {
        CompletableFuture<String> primary = new CompletableFuture<>();
        CompletableFuture<String> backup = new CompletableFuture<>();

        CompletableFuture<String> shouted = primary.applyToEither(backup, String::toUpperCase);
        backup.complete("backup answer");

        System.out.println("applyToEither   : " + shouted.join());
    }
}
