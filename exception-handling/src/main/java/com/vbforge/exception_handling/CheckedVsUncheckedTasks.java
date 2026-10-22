package com.vbforge.exception_handling;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 🎯 TASKS — Checked vs unchecked exceptions
 *
 * <p>Implement every method so that all tests in {@code CheckedVsUncheckedTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class CheckedVsUncheckedTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Classify a throwable into one of three words.
     *
     * <p>Examples:
     * <pre>
     *   classify(new java.io.IOException())          → "checked"
     *   classify(new Exception())                    → "checked"
     *   classify(new IllegalArgumentException())     → "unchecked"
     *   classify(new StackOverflowError())           → "error"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Test the two special branches of the hierarchy first; whatever is left is the third kind</li>
     * </ul>
     *
     * @param throwable the throwable to classify, never {@code null}
     * @return "checked", "unchecked" (a RuntimeException) or "error" (an Error)
     */
    public String classify(Throwable throwable) {
        throw new UnsupportedOperationException("TODO: implement classify");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Return what {@code source} produces. If the source fails with ANY
     * {@link Exception} (checked or unchecked), return {@code fallback} instead.
     * An {@link Error} (such as {@code AssertionError}) must NOT be swallowed — let it propagate.
     *
     * <p>Examples:
     * <pre>
     *   readOrDefault(() -> "data", "n/a")                           → "data"
     *   readOrDefault(() -> { throw new IOException(); }, "n/a")     → "n/a"
     *   readOrDefault(() -> { throw new IllegalStateException(); }, "n/a") → "n/a"
     *   readOrDefault(() -> { throw new AssertionError(); }, "n/a")  → throws AssertionError
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Callable.call()} declares {@code throws Exception} — you must deal with that</li>
     *   <li>Pick the narrowest catch type that still covers both checked and unchecked exceptions</li>
     * </ul>
     *
     * @param source   the operation to run
     * @param fallback the value to use when the operation throws an Exception
     * @return the operation's result, or the fallback
     */
    public String readOrDefault(Callable<String> source, String fallback) {
        throw new UnsupportedOperationException("TODO: implement readOrDefault");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Return what {@code source} produces, but never let a CHECKED exception escape:
     * wrap it in an {@link IllegalStateException} with the message {@code "could not load"}
     * and the original exception as its cause.
     * Unchecked exceptions must pass through UNCHANGED (the very same instance, not wrapped again).
     *
     * <p>Examples:
     * <pre>
     *   loadRequired(() -> "x")                               → "x"
     *   loadRequired(() -> { throw new IOException("io"); })  → throws IllegalStateException("could not load"), cause = the IOException
     *   loadRequired(() -> { throw new IllegalArgumentException("bad"); }) → throws that same IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Two catch blocks, in the right order — {@code RuntimeException} is a subclass of {@code Exception}</li>
     *   <li>Use the constructor of {@code IllegalStateException} that accepts a cause</li>
     * </ul>
     *
     * @param source the operation to run
     * @return the operation's result
     * @throws IllegalStateException when the source throws a checked exception
     */
    public String loadRequired(Callable<String> source) {
        throw new UnsupportedOperationException("TODO: implement loadRequired");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Apply {@code function} to every input, in order. Whenever it throws an
     * {@link Exception}, remember that exception's message and continue with the next input.
     * Return all collected messages in order. Inputs that succeed add nothing.
     *
     * <p>Examples:
     * <pre>
     *   function fails with "bad:" + input for inputs starting with "x"
     *   collectFailures(["a", "xb", "c", "xd"], function)  → ["bad:xb", "bad:xd"]
     *   collectFailures(["a", "b"], function)              → []
     *   collectFailures([], function)                      → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Decide whether the try block goes around the loop or inside it — one failure must not stop the others</li>
     * </ul>
     *
     * @param inputs   the values to process, never {@code null}
     * @param function the operation that may throw
     * @return the failure messages, in input order
     */
    public List<String> collectFailures(List<String> inputs, ThrowingFunction<String, String> function) {
        throw new UnsupportedOperationException("TODO: implement collectFailures");
    }
}
