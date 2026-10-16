package com.vbforge.sealed_classes_pattern_matching;

import java.util.List;

/**
 * 🎯 TASKS — Pattern matching (instanceof + switch)
 *
 * <p>Implement every method so that {@code PatternMatchingTasksTest} passes.
 * Remove the {@code @Disabled} annotation from one nested test class at a time.
 * Do NOT change method signatures.
 */
public class PatternMatchingTasks {

    /**
     * Convert a loosely typed value to an {@code int}.
     *
     * <p>Rules:
     * <ul>
     *   <li>{@code Integer} → the same value</li>
     *   <li>{@code Long} → its int value; throw {@link ArithmeticException} if it does not fit</li>
     *   <li>{@code String} → parsed after trimming (invalid text throws {@link NumberFormatException})</li>
     *   <li>anything else, including {@code null} → {@link IllegalArgumentException}</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   parseToInt(42)          → 42
     *   parseToInt(7L)          → 7
     *   parseToInt(" 15 ")      → 15
     *   parseToInt(3_000_000_000L) → throws ArithmeticException
     *   parseToInt(2.5)         → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code instanceof} patterns or a pattern {@code switch} — your choice</li>
     *   <li>{@code Math.toIntExact}</li>
     * </ul>
     *
     * @param value an Integer, Long, String or anything else
     * @return the int value
     */
    public int parseToInt(Object value) {
        throw new UnsupportedOperationException("TODO: implement parseToInt");
    }

    /**
     * Produce a short label describing the "size" of a value.
     *
     * <p>Rules:
     * <pre>
     *   null                → "none"
     *   empty String        → "empty text"
     *   other String        → "text of N"
     *   empty Collection    → "empty collection"
     *   other Collection    → "collection of N"
     *   int[]               → "int array of N"      (also for length 0)
     *   anything else       → "scalar"
     * </pre>
     *
     * <p>Examples:
     * <pre>
     *   sizeLabel(null)                → "none"
     *   sizeLabel("hey")               → "text of 3"
     *   sizeLabel(List.of(1, 2))       → "collection of 2"
     *   sizeLabel(new int[]{1, 2, 3})  → "int array of 3"
     *   sizeLabel(42)                  → "scalar"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A pattern {@code switch} with {@code when} guards</li>
     *   <li>How does a switch treat {@code null}?</li>
     * </ul>
     *
     * @param obj any object, possibly null
     * @return the label
     */
    public String sizeLabel(Object obj) {
        throw new UnsupportedOperationException("TODO: implement sizeLabel");
    }

    /**
     * Compare two values loosely: they are equal when both are Strings that match
     * ignoring case, or both are Numbers with the same {@code doubleValue()}.
     * Mixed types and nulls are never equal.
     *
     * <p>Examples:
     * <pre>
     *   looselyEquals("Java", "JAVA") → true
     *   looselyEquals(1, 1.0)         → true
     *   looselyEquals(1, "1")         → false
     *   looselyEquals(null, null)     → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Chain two {@code instanceof} patterns with {@code &&} and use both bindings</li>
     * </ul>
     *
     * @param a first value
     * @param b second value
     * @return true if loosely equal
     */
    public boolean looselyEquals(Object a, Object b) {
        throw new UnsupportedOperationException("TODO: implement looselyEquals");
    }

    /**
     * Collect the non-blank Strings from a mixed list, trimmed, in their original order.
     * Non-String elements and nulls are ignored.
     *
     * <p>Examples:
     * <pre>
     *   collectStrings(List.of("a", 1, "  ", " b ")) → ["a", "b"]
     *   collectStrings(List.of(1, 2, 3))             → []
     *   collectStrings(List.of())                    → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A pattern binding can be used in the rest of the same condition</li>
     * </ul>
     *
     * @param items a list of arbitrary objects
     * @return a new list of trimmed, non-blank strings
     */
    public List<String> collectStrings(List<?> items) {
        throw new UnsupportedOperationException("TODO: implement collectStrings");
    }
}
