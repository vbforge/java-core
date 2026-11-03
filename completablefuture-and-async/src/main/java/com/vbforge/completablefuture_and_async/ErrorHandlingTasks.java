package com.vbforge.completablefuture_and_async;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Practice: error handling in {@link CompletableFuture} pipelines (see {@link ErrorHandlingDemo}).
 */
public class ErrorHandlingTasks {

    /**
     * Peel the wrapper exceptions that the concurrency API adds and return the real error.
     * Only {@link java.util.concurrent.CompletionException} and
     * {@link java.util.concurrent.ExecutionException} count as wrappers, and they may be nested.
     * Any other exception is returned as it is — even if it has a cause. A wrapper that has
     * no cause is returned as it is.
     *
     * <p>Examples:
     * <pre>
     * unwrap(CompletionException(IllegalStateException("x")))             →  the IllegalStateException
     * unwrap(CompletionException(ExecutionException(IOException("io"))))  →  the IOException
     * unwrap(new IllegalArgumentException("plain"))                       →  the same exception
     * unwrap(RuntimeException("outer", cause))                            →  the RuntimeException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Keep peeling while the current exception is a wrapper <i>and</i> has a cause</li>
     * </ul>
     *
     * @param error the exception received by a handler (non-null)
     * @return the innermost non-wrapper exception
     */
    public Throwable unwrap(Throwable error) {
        throw new UnsupportedOperationException("TODO: implement unwrap");
    }

    /**
     * Parse {@code text} as an {@code int} on another thread. If parsing fails for any reason
     * (not a number, out of range, empty, {@code null}), the future completes normally with
     * {@code defaultValue}.
     *
     * <p>Examples:
     * <pre>
     * parseOrDefault("42", 0)    →  future of 42
     * parseOrDefault("abc", -1)  →  future of -1
     * parseOrDefault(null, 3)    →  future of 3
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Start the parsing asynchronously, then add a stage that runs only on failure</li>
     * </ul>
     *
     * @param text         text to parse, may be {@code null}
     * @param defaultValue value used when parsing fails
     * @return a future that never fails
     */
    public CompletableFuture<Integer> parseOrDefault(String text, int defaultValue) {
        throw new UnsupportedOperationException("TODO: implement parseOrDefault");
    }

    /**
     * Describe how {@code source} ended, as text. On success the text is {@code "ok:" + value}.
     * On failure it is {@code "failed:" + message}, where {@code message} is the message of the
     * <b>real</b> exception — not of a {@code CompletionException} wrapper. The returned future
     * itself never fails.
     *
     * <p>Examples:
     * <pre>
     * source completed with 5                                      →  "ok:5"
     * source completed with IllegalStateException("boom")          →  "failed:boom"
     * a stage depending on that failed source                      →  "failed:boom"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You need one callback that runs on both outcomes</li>
     *   <li>Depending on where a future failed, the handler may receive the real exception or a wrapper</li>
     * </ul>
     *
     * @param source the future to describe
     * @return a future of the description
     */
    public CompletableFuture<String> describeOutcome(CompletableFuture<Integer> source) {
        throw new UnsupportedOperationException("TODO: implement describeOutcome");
    }

    /**
     * Try the suppliers <b>in order</b>, each one asynchronously, and return the result of the
     * first one that does not throw. A supplier is only called if every supplier before it
     * threw — suppliers after the first success are never called. If all of them throw, the
     * result fails with the failure of the last supplier. If the list is empty, the result
     * fails with an {@link IllegalArgumentException}.
     *
     * <p>Examples:
     * <pre>
     * fallbackChain([throws, () -> "b", () -> "c"])  →  future of "b"   (third supplier never called)
     * fallbackChain([throws "x", throws "last"])     →  failed future, real cause message "last"
     * fallbackChain([])                              →  failed future (IllegalArgumentException)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Java 8 has no "recover with another future" stage — combine a two-outcome stage with a flattening one</li>
     *   <li>Folding the list into one growing future keeps the order and the laziness</li>
     * </ul>
     *
     * @param suppliers alternatives, tried left to right
     * @return a future of the first successful value
     */
    public CompletableFuture<String> fallbackChain(List<Supplier<String>> suppliers) {
        throw new UnsupportedOperationException("TODO: implement fallbackChain");
    }

    /**
     * Guarantee cleanup: whatever the outcome of {@code source}, append {@code "cleanup"} to
     * {@code log}. The returned future must end <b>exactly like {@code source}</b> — with the same
     * value on success, or failing on failure.
     *
     * <p>Examples:
     * <pre>
     * source completes with "v"  →  log = ["cleanup"], result "v"
     * source fails               →  log = ["cleanup"], result fails with the same error
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Pick the stage that observes both outcomes but does <i>not</i> replace the result</li>
     * </ul>
     *
     * @param source future to observe
     * @param log    thread-safe list that receives the cleanup marker
     * @return a future with the same outcome as {@code source}
     */
    public CompletableFuture<String> alwaysCleanup(CompletableFuture<String> source, List<String> log) {
        throw new UnsupportedOperationException("TODO: implement alwaysCleanup");
    }
}
