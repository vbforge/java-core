package com.vbforge.optional_and_null_handling;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * 🎯 TASKS — Creating Optionals
 *
 * <p>Implement every method so that all tests in {@code OptionalCreationTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class OptionalCreationTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Convert text to an {@code int}, returning an empty Optional instead of
     * throwing when the text is not a valid integer.
     *
     * <p>No trimming is applied: {@code " 42"} is NOT a valid integer here.
     * {@code null}, empty text, decimals and values outside the {@code int} range are all "not valid".
     *
     * <p>Examples:
     * <pre>
     *   parseInteger("42")          → Optional[42]
     *   parseInteger("-7")          → Optional[-7]
     *   parseInteger("abc")         → Optional.empty
     *   parseInteger(null)          → Optional.empty
     *   parseInteger("2147483648")  → Optional.empty   (one more than Integer.MAX_VALUE)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Integer.parseInt} reports bad input with an exception — catch the specific one</li>
     *   <li>Decide which {@code Optional} factory fits a value you have just proven non-null</li>
     * </ul>
     *
     * @param text the text to convert, may be {@code null}
     * @return the parsed number, or empty when the text is not a valid {@code int}
     */
    public Optional<Integer> parseInteger(String text) {
        throw new UnsupportedOperationException("TODO: implement parseInteger");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Return the TRIMMED text, or empty when the text is {@code null}, empty,
     * or contains only whitespace.
     *
     * <p>Examples:
     * <pre>
     *   nonBlank("  hi  ")  → Optional[hi]
     *   nonBlank("hello")   → Optional[hello]
     *   nonBlank("   ")     → Optional.empty
     *   nonBlank("")        → Optional.empty
     *   nonBlank(null)      → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Check for {@code null} before calling any method on the text</li>
     *   <li>{@code String.trim()} gives you the cleaned value you must return</li>
     * </ul>
     *
     * @param text the text to check, may be {@code null}
     * @return the trimmed text, or empty when there is nothing but whitespace
     */
    public Optional<String> nonBlank(String text) {
        throw new UnsupportedOperationException("TODO: implement nonBlank");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Wrap the result of a {@code Map} lookup so callers never see {@code null}.
     * Return empty when the key is {@code null}, missing, or mapped to {@code null}.
     *
     * <p>Examples:
     * <pre>
     *   map = {"id": "42"}
     *   lookup(map, "id")       → Optional[42]
     *   lookup(map, "missing")  → Optional.empty
     *   lookup(map, null)       → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This is the "bridge from the null world" factory method</li>
     *   <li>Some map implementations (e.g. {@code TreeMap}) reject a {@code null} key — guard it first</li>
     * </ul>
     *
     * @param map the map to read from, never {@code null}
     * @param key the key to look up, may be {@code null}
     * @return the mapped value, or empty when there is none
     */
    public Optional<String> lookup(Map<String, String> map, String key) {
        throw new UnsupportedOperationException("TODO: implement lookup");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Find the first NEGATIVE number (strictly less than zero) in the array.
     * Return an empty {@link OptionalInt} when there is none (zero is not negative).
     *
     * <p>Examples:
     * <pre>
     *   firstNegative([3, -1, -5])  → OptionalInt[-1]
     *   firstNegative([3, 0, 5])    → OptionalInt.empty
     *   firstNegative([])           → OptionalInt.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use the primitive specialisation so no {@code Integer} boxing is needed</li>
     *   <li>Stop as soon as you have an answer</li>
     * </ul>
     *
     * @param numbers the numbers to scan, never {@code null}
     * @return the first negative number, or empty
     */
    public OptionalInt firstNegative(int[] numbers) {
        throw new UnsupportedOperationException("TODO: implement firstNegative");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Return the arithmetic mean of the numbers. An empty array has NO average —
     * model that with an empty {@link OptionalDouble}, not with {@code 0.0} or {@code NaN}.
     *
     * <p>Examples:
     * <pre>
     *   average([1, 2, 3, 4])  → OptionalDouble[2.5]
     *   average([5])           → OptionalDouble[5.0]
     *   average([-1, 1])       → OptionalDouble[0.0]
     *   average([])            → OptionalDouble.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Beware integer division — decide where the conversion to {@code double} happens</li>
     *   <li>Sum with a {@code long} if you want to be safe against overflow</li>
     * </ul>
     *
     * @param numbers the numbers to average, never {@code null}
     * @return the mean, or empty when there are no numbers
     */
    public OptionalDouble average(int[] numbers) {
        throw new UnsupportedOperationException("TODO: implement average");
    }
}
