package com.vbforge.unit_testing_junit5_assertj;

import java.util.List;

/**
 * AssertionsAndExceptionsTasks — implement every method below to make
 * {@code AssertionsAndExceptionsTasksTest} go GREEN.
 *
 * <p>These methods all share one property: they either return rich objects
 * (perfect for AssertJ chains) or throw exceptions (perfect for
 * {@code assertThatThrownBy}). The tests exercise AssertJ's full API.
 */
public class AssertionsAndExceptionsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Integer division. Throws {@link ArithmeticException} when {@code b == 0}.
     *
     * <p>Examples:
     * <pre>
     *   divide(10, 2)   → 5
     *   divide(7, 2)    → 3     (integer division truncates)
     *   divide(-10, 2)  → -5
     *   divide(5, 0)    → ArithmeticException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Guard {@code b == 0} and throw {@link ArithmeticException} explicitly.</li>
     *   <li>Otherwise {@code a / b} does the work.</li>
     * </ul>
     *
     * @param a dividend
     * @param b divisor (must be non-zero)
     * @return {@code a / b}
     */
    public int divide(int a, int b) {
        throw new UnsupportedOperationException("TODO: implement divide");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Parses an age from a string. Throws {@link IllegalArgumentException} for
     * any of these cases:
     * <ul>
     *   <li>{@code null} input</li>
     *   <li>Non-numeric input (e.g. {@code "abc"})</li>
     *   <li>Value outside {@code [0, 150]}</li>
     * </ul>
     * Whitespace around the input is trimmed.
     *
     * <p>Examples:
     * <pre>
     *   parseAge("42")     → 42
     *   parseAge("  7  ")  → 7
     *   parseAge("0")      → 0
     *   parseAge("150")    → 150
     *   parseAge(null)     → IllegalArgumentException
     *   parseAge("abc")    → IllegalArgumentException
     *   parseAge("-1")     → IllegalArgumentException
     *   parseAge("151")    → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Integer.parseInt} throws {@code NumberFormatException} on
     *       non-numeric — wrap it and rethrow as {@code IllegalArgumentException}.</li>
     *   <li>Guard the range after parsing.</li>
     * </ul>
     *
     * @param s the age as a string
     * @return the parsed age, 0–150
     */
    public int parseAge(String s) {
        throw new UnsupportedOperationException("TODO: implement parseAge");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the first element of a non-empty list. Throws
     * {@link java.util.NoSuchElementException} if the list is null or empty.
     *
     * <p>Examples:
     * <pre>
     *   firstElement(List.of("a", "b"))  → "a"
     *   firstElement(List.of("only"))    → "only"
     *   firstElement(List.of())          → NoSuchElementException
     *   firstElement(null)               → NoSuchElementException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Guard null/empty and throw {@code NoSuchElementException}.</li>
     *   <li>Use {@code list.get(0)} on the happy path.</li>
     * </ul>
     *
     * @param list list of strings
     * @return the first element
     */
    public String firstElement(List<String> list) {
        throw new UnsupportedOperationException("TODO: implement firstElement");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Clamps {@code value} to the inclusive range {@code [min, max]}.
     * Throws {@link IllegalArgumentException} if {@code min > max}.
     *
     * <p>Examples:
     * <pre>
     *   clamp(5, 0, 10)     → 5
     *   clamp(-3, 0, 10)    → 0
     *   clamp(42, 0, 10)    → 10
     *   clamp(0, 0, 0)      → 0
     *   clamp(1, 5, 2)      → IllegalArgumentException  (min > max)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Guard {@code min > max} first.</li>
     *   <li>Then {@code Math.max(min, Math.min(max, value))} in one line.</li>
     * </ul>
     *
     * @param value the value to clamp
     * @param min   the inclusive lower bound
     * @param max   the inclusive upper bound
     * @return the clamped value
     */
    public int clamp(int value, int min, int max) {
        throw new UnsupportedOperationException("TODO: implement clamp");
    }
}