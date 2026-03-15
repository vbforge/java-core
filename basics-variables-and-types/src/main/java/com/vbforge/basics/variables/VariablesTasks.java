package com.vbforge.basics.variables;

/**
 * 🎯 TASKS — Variables and Types
 *
 * <p>Your goal: implement every method below so that all tests
 * in {@link VariablesTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>You may add private helper methods if needed</li>
 * </ul>
 */
public class VariablesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return the number of bytes that a value of the given primitive type occupies.
     *
     * <p>Supported type names (case-insensitive): "byte", "short", "int", "long",
     * "float", "double", "char", "boolean".
     *
     * <p>For "boolean" return 1 (JVM-convention approximation).
     * Throw {@link IllegalArgumentException} for any unknown type name.
     *
     * <p>Examples:
     * <pre>
     *   primitiveSize("int")    → 4
     *   primitiveSize("double") → 8
     *   primitiveSize("byte")   → 1
     * </pre>
     */
    public int primitiveSize(String typeName) {
        throw new UnsupportedOperationException("Task 1 not implemented yet");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Perform a safe narrowing cast from {@code double} to {@code int}.
     *
     * <p>If the value is outside the range of {@code int}
     * ({@link Integer#MIN_VALUE} to {@link Integer#MAX_VALUE}),
     * return the nearest boundary value instead of overflowing.
     *
     * <p>Examples:
     * <pre>
     *   safeNarrow(3.99)               → 3      (truncation, not rounding)
     *   safeNarrow(-3.99)              → -3
     *   safeNarrow(3.0e10)             → Integer.MAX_VALUE
     *   safeNarrow(-3.0e10)            → Integer.MIN_VALUE
     * </pre>
     */
    public int safeNarrow(double value) {
        throw new UnsupportedOperationException("Task 2 not implemented yet");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Return {@code true} if the given {@code Integer} object is inside
     * the JVM's Integer cache range (−128 to 127 inclusive), {@code false} otherwise.
     *
     * <p>Hint: you do NOT need to know the cache internals —
     * just check whether the value fits in the documented range.
     *
     * <p>Examples:
     * <pre>
     *   isInIntegerCacheRange(0)    → true
     *   isInIntegerCacheRange(127)  → true
     *   isInIntegerCacheRange(128)  → false
     *   isInIntegerCacheRange(-128) → true
     *   isInIntegerCacheRange(-129) → false
     * </pre>
     */
    public boolean isInIntegerCacheRange(Integer value) {
        throw new UnsupportedOperationException("Task 3 not implemented yet");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Convert a numeric string to {@code int}, returning a provided default
     * value if the string is {@code null}, blank, or not a valid integer.
     *
     * <p>Examples:
     * <pre>
     *   parseOrDefault("42",  0) → 42
     *   parseOrDefault("-7",  0) → -7
     *   parseOrDefault("abc", 0) → 0
     *   parseOrDefault(null,  5) → 5
     *   parseOrDefault("  ", -1) → -1
     * </pre>
     */
    public int parseOrDefault(String value, int defaultValue) {
        throw new UnsupportedOperationException("Task 4 not implemented yet");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Given a {@code char}, return its value shifted by {@code shift} positions
     * in the alphabet, wrapping around within the same case group.
     *
     * <p>Only alphabetic characters are shifted; non-alphabetic characters
     * are returned unchanged. Case is preserved.
     *
     * <p>Examples:
     * <pre>
     *   shiftChar('a', 1)  → 'b'
     *   shiftChar('z', 1)  → 'a'   (wraps)
     *   shiftChar('A', 3)  → 'D'
     *   shiftChar('Y', 3)  → 'B'   (wraps)
     *   shiftChar('5', 2)  → '5'   (non-alpha, unchanged)
     * </pre>
     */
    public char shiftChar(char c, int shift) {
        throw new UnsupportedOperationException("Task 5 not implemented yet");
    }
}
