package com.vbforge.completablefuture_and_async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Practice: waiting for many futures (see {@link AllOfAnyOfDemo}).
 */
public class AllOfAnyOfTasks {

    /**
     * Turn a list of futures into one future of a list ("sequence" / "gather").
     * The result list has the <b>same order</b> as the input list, no matter in which
     * order the futures complete. The result completes after every input has completed;
     * if any input fails, the result fails.
     *
     * <p>Examples:
     * <pre>
     * allAsList([done(1), done(2), done(3)])  →  future of [1, 2, 3]
     * allAsList([])                           →  future of []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The "wait for everything" stage returns {@code Void} — you have to read the values yourself</li>
     *   <li>Once everything is done, reading a value cannot block</li>
     *   <li>A list of futures needs converting to an array to call the varargs method</li>
     * </ul>
     *
     * @param futures the futures to gather
     * @param <T>     element type
     * @return a future of all values, in input order
     */
    public <T> CompletableFuture<List<T>> allAsList(List<CompletableFuture<T>> futures) {
        throw new UnsupportedOperationException("TODO: implement allAsList");
    }

    /**
     * Return a future that completes like the <b>first</b> input future to complete —
     * with its value, or with its failure if it failed first.
     * The input list is not empty.
     *
     * <p>Examples:
     * <pre>
     * firstCompleted([pending, done("B"), pending])  →  future of "B"
     * firstCompleted([pending, pending])             →  stays pending until one of them completes
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a stage that races several futures; look at the type of its result</li>
     *   <li>The cast back to {@code T} cannot be checked by the compiler — annotate it</li>
     * </ul>
     *
     * @param futures the futures to race (non-empty)
     * @param <T>     element type
     * @return a future with the outcome of the first future to complete
     */
    public <T> CompletableFuture<T> firstCompleted(List<CompletableFuture<T>> futures) {
        throw new UnsupportedOperationException("TODO: implement firstCompleted");
    }

    /**
     * Wait until <b>every</b> future has completed — successfully or not — and count how
     * many completed <b>successfully</b>. The returned future itself never fails.
     *
     * <p>Examples:
     * <pre>
     * countSuccessful([ok, failed, ok])  →  future of 2
     * countSuccessful([failed, failed])  →  future of 0
     * countSuccessful([])                →  future of 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The "wait for everything" stage fails as soon as <i>any</i> input failed (after all
     *       are done) — neutralise that failure before continuing</li>
     *   <li>Completed futures can tell you whether they ended exceptionally</li>
     * </ul>
     *
     * @param futures the futures to inspect
     * @return a future of the number of successfully completed futures
     */
    public CompletableFuture<Long> countSuccessful(List<CompletableFuture<String>> futures) {
        throw new UnsupportedOperationException("TODO: implement countSuccessful");
    }

    /**
     * Return a future of the value of the first input future that completes
     * <b>successfully</b>. Failed futures are ignored. If <b>all</b> inputs fail, the
     * result fails. If the list is empty, the result fails with an
     * {@link IllegalArgumentException}.
     *
     * <p>Examples:
     * <pre>
     * firstSuccessful([failed, pending, done("C")])  →  future of "C"
     * firstSuccessful([failed, failed])              →  failed future
     * firstSuccessful([])                            →  failed future (IllegalArgumentException)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The plain "race" stage would let an early failure win — it is not enough here</li>
     *   <li>Create an empty future yourself and complete it from callbacks on the inputs</li>
     *   <li>You need to know when the <i>last</i> input has failed</li>
     * </ul>
     *
     * @param futures the futures to race
     * @param <T>     element type
     * @return a future of the first successful value
     */
    public <T> CompletableFuture<T> firstSuccessful(List<CompletableFuture<T>> futures) {
        throw new UnsupportedOperationException("TODO: implement firstSuccessful");
    }
}
