package com.vbforge.exception_handling;

import java.util.List;
import java.util.function.Supplier;

/**
 * 🎯 TASKS — try / catch / finally
 *
 * <p>Implement every method so that all tests in {@code TryCatchFinallyTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class TryCatchFinallyTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Divide {@code a} by {@code b}. When the division is impossible, return
     * {@code fallback} instead of letting the exception escape.
     *
     * <p>Examples:
     * <pre>
     *   safeDivide(10, 2, -1)  → 5
     *   safeDivide(7, 2, -1)   → 3     (integer division)
     *   safeDivide(10, 0, -1)  → -1
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Integer division by zero throws a very specific exception type — catch only that</li>
     * </ul>
     *
     * @param a        the dividend
     * @param b        the divisor
     * @param fallback the value to return when the division fails
     * @return {@code a / b}, or {@code fallback}
     */
    public int safeDivide(int a, int b, int fallback) {
        throw new UnsupportedOperationException("TODO: implement safeDivide");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Sum all valid integers in a comma-separated string. Tokens that are not valid
     * integers are skipped; whitespace around a token is ignored. {@code null} or
     * empty input gives 0.
     *
     * <p>Examples:
     * <pre>
     *   sumValidNumbers("1,2,3")      → 6
     *   sumValidNumbers("1, x, 3")    → 4
     *   sumValidNumbers("a,b")        → 0
     *   sumValidNumbers("")           → 0
     *   sumValidNumbers(null)         → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One bad token must not stop the processing of the next one — where does the try go?</li>
     *   <li>{@code String.split} and {@code String.trim} help with the tokens</li>
     * </ul>
     *
     * @param csv comma-separated text, may be {@code null}
     * @return the sum of all valid integers
     */
    public int sumValidNumbers(String csv) {
        throw new UnsupportedOperationException("TODO: implement sumValidNumbers");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Return the first element as text, or {@code "none"} when there is no first
     * element — because the array is empty OR the array itself is {@code null}.
     * Handle both situations with ONE catch block.
     *
     * <p>Examples:
     * <pre>
     *   firstOrNone(new int[]{7, 8})  → "7"
     *   firstOrNone(new int[]{})      → "none"
     *   firstOrNone(null)             → "none"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Reading index 0 of an empty array and of a null array fails with two different exception types</li>
     *   <li>There is a catch syntax that lists several types at once</li>
     * </ul>
     *
     * @param numbers the array, may be {@code null} or empty
     * @return the first element as a string, or "none"
     */
    public String firstOrNone(int[] numbers) {
        throw new UnsupportedOperationException("TODO: implement firstOrNone");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Call {@code action} until it succeeds, at most {@code maxAttempts} times, and
     * return its result. If EVERY attempt throws a {@link RuntimeException}, rethrow
     * the exception from the LAST attempt.
     *
     * <p>Examples:
     * <pre>
     *   retry(() -> 5, 3)                          → 5            (1 call)
     *   action fails twice, then returns 9, max 3  → 9            (3 calls)
     *   action always throws, max 3                → throws the third exception (3 calls)
     *   retry(() -> 5, 0)                          → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Remember the last failure somewhere that is visible after the loop</li>
     *   <li>Validate {@code maxAttempts} before doing any work</li>
     * </ul>
     *
     * @param action      the action to attempt
     * @param maxAttempts the maximum number of calls, must be at least 1
     * @return the first successful result
     * @throws IllegalArgumentException if {@code maxAttempts} is less than 1
     */
    public int retry(Supplier<Integer> action, int maxAttempts) {
        throw new UnsupportedOperationException("TODO: implement retry");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Run {@code action} and return its result. Whatever happens, add exactly one
     * entry {@code "cleanup"} to {@code log} AFTER the action has finished.
     * If the action throws a {@link RuntimeException}, do not propagate it —
     * return {@code "error:" + message} instead.
     *
     * <p>Examples:
     * <pre>
     *   action returns "ok"                   → "ok",           log = ["cleanup"]
     *   action throws IllegalStateException("bad") → "error:bad", log = ["cleanup"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>"Whatever happens" is the job description of one specific block</li>
     * </ul>
     *
     * @param action the action to run
     * @param log    receives the cleanup entry, never {@code null}
     * @return the action's result, or an error description
     */
    public String runWithCleanup(Supplier<String> action, List<String> log) {
        throw new UnsupportedOperationException("TODO: implement runWithCleanup");
    }
}
