package com.vbforge.exception_handling;

import java.util.List;

/**
 * 🎯 TASKS — try-with-resources
 *
 * <p>Implement every method so that all tests in {@code TryWithResourcesTasksTest} pass.
 * Use {@link TrackedResource}: {@code new TrackedResource(name, log)} logs {@code open:name},
 * {@code use()} logs {@code use:name}, {@code close()} logs {@code close:name}.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class TryWithResourcesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Open a resource with the given name, call {@code use()} on it, and make sure it is
     * closed. Return {@code "done"}. Do NOT call {@code close()} yourself.
     *
     * <p>Example:
     * <pre>
     *   useAndClose("db", log)  → "done",  log = [open:db, use:db, close:db]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The resource goes inside the parentheses after {@code try}</li>
     * </ul>
     *
     * @param name the resource name
     * @param log  receives the events, never {@code null}
     * @return "done"
     */
    public String useAndClose(String name, List<String> log) {
        throw new UnsupportedOperationException("TODO: implement useAndClose");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Open {@code first}, then {@code second}; use {@code first}, then {@code second};
     * and let both be closed automatically — in the correct (reverse) order — using a
     * SINGLE try-with-resources statement.
     *
     * <p>Example:
     * <pre>
     *   useBoth("a", "b", log)  → log = [open:a, open:b, use:a, use:b, close:b, close:a]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Multiple resources are separated by a semicolon</li>
     * </ul>
     *
     * @param first  the name of the resource opened first
     * @param second the name of the resource opened second
     * @param log    receives the events, never {@code null}
     */
    public void useBoth(String first, String second, List<String> log) {
        throw new UnsupportedOperationException("TODO: implement useBoth");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Open a resource named {@code name} and run {@code body} while it is open.
     * <ul>
     *   <li>Body completes normally → return {@code "ok"}.</li>
     *   <li>Body throws a {@link RuntimeException} → add {@code "handled:" + message} to the log
     *       and return {@code "failed:" + message}. The resource must already be closed by then.</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   body ok                        → "ok",           log = [open:r, close:r]
     *   body throws IllegalStateException("boom") → "failed:boom", log = [open:r, close:r, handled:boom]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Where does a catch block run relative to the automatic close?</li>
     * </ul>
     *
     * @param name the resource name
     * @param log  receives the events, never {@code null}
     * @param body the work to run while the resource is open
     * @return "ok" or "failed:" + the exception message
     */
    public String runCatching(String name, List<String> log, Runnable body) {
        throw new UnsupportedOperationException("TODO: implement runCatching");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Return the messages of all exceptions suppressed by {@code exception}, in order.
     * Return an empty list when nothing was suppressed.
     *
     * <p>Examples:
     * <pre>
     *   exception with suppressed IllegalStateException("x") and IllegalStateException("y")  → ["x", "y"]
     *   exception with no suppressed exceptions                                              → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Throwable} has a method that returns an array of the suppressed exceptions</li>
     * </ul>
     *
     * @param exception the exception to inspect, never {@code null}
     * @return the suppressed messages
     */
    public List<String> suppressedMessages(Exception exception) {
        throw new UnsupportedOperationException("TODO: implement suppressedMessages");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Close EVERY resource in the list, in list order, even if some of them fail to close.
     * Skip {@code null} elements. Return how many resources failed to close.
     *
     * <p>Examples:
     * <pre>
     *   [ok, failing, ok]   → 1     (all three were closed)
     *   [failing, failing]  → 2
     *   []                  → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code AutoCloseable.close()} declares {@code throws Exception}</li>
     *   <li>One failure must not prevent the remaining resources from being closed</li>
     * </ul>
     *
     * @param resources the resources to close, never {@code null} (elements may be)
     * @return the number of resources whose {@code close()} threw
     */
    public int closeAllQuietly(List<? extends AutoCloseable> resources) {
        throw new UnsupportedOperationException("TODO: implement closeAllQuietly");
    }
}
