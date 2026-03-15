package com.vbforge.basics.operators;

/**
 * 🎯 TASKS — Operators and Expressions
 *
 * <p>Your goal: implement every method below so that all tests
 * in {@link OperatorsTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>You may add private helper methods if needed</li>
 * </ul>
 */
public class OperatorsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return {@code true} if {@code n} is divisible by both {@code a} and {@code b}.
     *
     * <p>Examples:
     * <pre>
     *   isDivisibleByBoth(12, 3, 4) → true   (12 % 3 == 0 AND 12 % 4 == 0)
     *   isDivisibleByBoth(12, 3, 5) → false
     *   isDivisibleByBoth( 0, 3, 4) → true   (0 is divisible by anything non-zero)
     * </pre>
     */
    public boolean isDivisibleByBoth(int n, int a, int b) {
        throw new UnsupportedOperationException("Task 1 not implemented yet");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Calculate the integer average of three numbers using only integer arithmetic.
     * Truncate toward zero — do NOT use floating-point at any point.
     *
     * <p>Examples:
     * <pre>
     *   intAverage(1, 2, 3) → 2
     *   intAverage(1, 2, 4) → 2   (7 / 3 = 2, truncated)
     *   intAverage(0, 0, 1) → 0
     * </pre>
     */
    public int intAverage(int a, int b, int c) {
        throw new UnsupportedOperationException("Task 2 not implemented yet");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Return the absolute value of {@code n} using only the ternary operator
     * — do NOT use {@link Math#abs} or any branching ({@code if/else}).
     *
     * <p>Examples:
     * <pre>
     *   absoluteValue(-7) → 7
     *   absoluteValue( 7) → 7
     *   absoluteValue( 0) → 0
     * </pre>
     */
    public int absoluteValue(int n) {
        throw new UnsupportedOperationException("Task 3 not implemented yet");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Using only bitwise operators, determine whether {@code n} is a power of two.
     * Non-positive numbers are never powers of two.
     *
     * <p>Hint: a power of two has exactly one bit set.
     * Think about what {@code n & (n - 1)} gives you.
     *
     * <p>Examples:
     * <pre>
     *   isPowerOfTwo(1)  → true    (2^0)
     *   isPowerOfTwo(2)  → true    (2^1)
     *   isPowerOfTwo(3)  → false
     *   isPowerOfTwo(16) → true    (2^4)
     *   isPowerOfTwo(0)  → false
     *   isPowerOfTwo(-4) → false
     * </pre>
     */
    public boolean isPowerOfTwo(int n) {
        throw new UnsupportedOperationException("Task 4 not implemented yet");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Swap the values of two integers and return them as a two-element array
     * {@code [newA, newB]} — using only arithmetic operators, no temporary variable.
     *
     * <p>Hint: you can use addition/subtraction OR XOR for a classic temp-free swap.
     *
     * <p>Examples:
     * <pre>
     *   swapWithoutTemp(3, 7) → [7, 3]
     *   swapWithoutTemp(0, 5) → [5, 0]
     *   swapWithoutTemp(4, 4) → [4, 4]
     * </pre>
     */
    public int[] swapWithoutTemp(int a, int b) {
        throw new UnsupportedOperationException("Task 5 not implemented yet");
    }


    // ── Task 6 ─────────────────────────────────────────────────────────────────
    /**
     * Using only the left-shift operator ({@code <<}), compute {@code 2} raised
     * to the power {@code exponent} for values {@code 0 ≤ exponent ≤ 30}.
     *
     * <p>Examples:
     * <pre>
     *   powerOfTwoShift(0)  → 1
     *   powerOfTwoShift(1)  → 2
     *   powerOfTwoShift(4)  → 16
     *   powerOfTwoShift(10) → 1024
     * </pre>
     */
    public int powerOfTwoShift(int exponent) {
        throw new UnsupportedOperationException("Task 6 not implemented yet");
    }
}
