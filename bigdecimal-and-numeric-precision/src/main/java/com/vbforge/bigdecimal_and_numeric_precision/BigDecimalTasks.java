package com.vbforge.bigdecimal_and_numeric_precision;

import java.math.BigDecimal;
import java.util.List;

/**
 * BigDecimalTasks — implement every method below to make {@code BigDecimalTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code BigDecimalDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=BigDecimalTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class BigDecimalTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Calculates the final price including tax, rounded to 2 decimals
     * using {@link java.math.RoundingMode#HALF_UP}.
     *
     * <p>The tax is computed as {@code price * taxRate}, and the total is
     * {@code price + tax}. Both the tax and the total are exact to the cent.
     *
     * <p>Examples:
     * <pre>
     *   calculatePriceWithTax(19.99, 0.08)  → 21.59   (tax = 1.5992 → 1.60)
     *   calculatePriceWithTax(100.00, 0.07) → 107.00
     *   calculatePriceWithTax(0.00, 0.08)   → 0.00
     *   calculatePriceWithTax(9.99, 0.00)   → 9.99
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Compute {@code price.multiply(taxRate)} — the raw product has many decimals.</li>
     *   <li>Use {@code setScale(2, RoundingMode.HALF_UP)} on the tax, then add.</li>
     *   <li>The return value must have scale 2. Use {@code setScale(2)} on the result.</li>
     * </ul>
     *
     * @param price   the base price (scale 2 is typical, but do not assume)
     * @param taxRate the tax rate as a fraction (e.g. {@code 0.08} for 8%)
     * @return the total price (price + tax) rounded to 2 decimals HALF_UP
     */
    public BigDecimal calculatePriceWithTax(BigDecimal price, BigDecimal taxRate) {
        throw new UnsupportedOperationException("TODO: implement calculatePriceWithTax");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Sums a list of prices exactly, returning the total with scale 2.
     * An empty list returns {@code BigDecimal.ZERO} with scale 2.
     *
     * <p>This exists because {@code double} summation of prices drifts.
     * {@link BigDecimal} addition is exact.
     *
     * <p>Examples:
     * <pre>
     *   sumPrices([1.10, 2.20, 3.30])  → 6.60
     *   sumPrices([])                  → 0.00
     *   sumPrices([0.01, 0.01, 0.01])  → 0.03
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Start from {@code BigDecimal.ZERO} and loop with {@code add}.</li>
     *   <li>Return {@code result.setScale(2, RoundingMode.HALF_UP)} at the end.</li>
     * </ul>
     *
     * @param prices list of prices (may be empty)
     * @return the exact sum, with scale 2
     */
    public BigDecimal sumPrices(List<BigDecimal> prices) {
        throw new UnsupportedOperationException("TODO: implement sumPrices");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Splits an amount into {@code parts} equal shares with scale 2, distributing
     * any leftover cents one at a time to the first shares. The sum of the
     * returned shares is always exactly equal to {@code amount}.
     *
     * <p>Examples:
     * <pre>
     *   splitEqually(10.00, 3)  → [3.34, 3.33, 3.33]   (1 cent left over)
     *   splitEqually(10.00, 4)  → [2.50, 2.50, 2.50, 2.50]
     *   splitEqually(0.05, 2)   → [0.03, 0.02]         (1 cent left over)
     *   splitEqually(1.00, 3)   → [0.34, 0.33, 0.33]
     *   splitEqually(0.00, 5)   → [0.00, 0.00, 0.00, 0.00, 0.00]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Compute {@code base = amount.divide(valueOf(parts), 2, RoundingMode.DOWN)}.</li>
     *   <li>Compute the remaining cents as {@code amount - base * parts}, expressed in cents.</li>
     *   <li>Give one extra cent ({@code 0.01}) to the first {@code remainderCents} shares.</li>
     *   <li>{@code parts} must be {@code > 0} — throw {@link IllegalArgumentException} otherwise.</li>
     * </ul>
     *
     * @param amount the total amount to split
     * @param parts  number of shares (must be positive)
     * @return list of {@code parts} BigDecimals, each with scale 2, summing to {@code amount}
     */
    public List<BigDecimal> splitEqually(BigDecimal amount, int parts) {
        throw new UnsupportedOperationException("TODO: implement splitEqually");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code a + b} would overflow the {@code int}
     * range, {@code false} otherwise. Never performs the overflowing addition.
     *
     * <p>Examples:
     * <pre>
     *   wouldOverflowOnAdd(Integer.MAX_VALUE, 1)   → true
     *   wouldOverflowOnAdd(Integer.MIN_VALUE, -1)  → true
     *   wouldOverflowOnAdd(1, 2)                   → false
     *   wouldOverflowOnAdd(Integer.MAX_VALUE, 0)   → false
     *   wouldOverflowOnAdd(Integer.MIN_VALUE, 0)   → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Widen to {@code long}: {@code (long) a + b} always fits and is exact.</li>
     *   <li>Overflow iff the widened sum is outside {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]}.</li>
     *   <li>Do NOT use {@code Math.addExact} in a try/catch — that performs the addition
     *       and throws; the point of this method is to detect overflow without throwing.</li>
     * </ul>
     *
     * @param a first operand
     * @param b second operand
     * @return {@code true} if {@code a + b} would overflow {@code int}
     */
    public boolean wouldOverflowOnAdd(int a, int b) {
        throw new UnsupportedOperationException("TODO: implement wouldOverflowOnAdd");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Converts an amount in cents (a {@code long}) to dollars (a
     * {@link BigDecimal} with scale 2).
     *
     * <p>Examples:
     * <pre>
     *   centsToDollars(1234L)  → 12.34
     *   centsToDollars(0L)     → 0.00
     *   centsToDollars(-500L)  → -5.00
     *   centsToDollars(5L)     → 0.05
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code BigDecimal.valueOf(long, int)} builds the value from a long
     *       and a scale. The scale you want is 2.</li>
     *   <li>{@code movePointLeft(2)} also works but requires a scale set afterwards.</li>
     * </ul>
     *
     * @param cents amount in cents
     * @return the same amount as dollars, scale 2
     */
    public BigDecimal centsToDollars(long cents) {
        throw new UnsupportedOperationException("TODO: implement centsToDollars");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code a} and {@code b} represent the same
     * numerical value, ignoring differences in scale. Uses {@code compareTo},
     * not {@code equals}.
     *
     * <p>Examples:
     * <pre>
     *   isNumericallyEqual(2.0, 2.00)    → true
     *   isNumericallyEqual(2.0, 2)       → true
     *   isNumericallyEqual(2.0, 2.001)   → false
     *   isNumericallyEqual(-0.0, 0.00)   → true   (BigDecimal has no signed zero)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code new BigDecimal("2.0").equals(new BigDecimal("2.00"))} is {@code false}
     *       because {@code equals} compares scale too.</li>
     *   <li>{@code compareTo} returns {@code 0} when the values are numerically equal.</li>
     * </ul>
     *
     * @param a first value
     * @param b second value
     * @return {@code true} if {@code a.compareTo(b) == 0}
     */
    public boolean isNumericallyEqual(BigDecimal a, BigDecimal b) {
        throw new UnsupportedOperationException("TODO: implement isNumericallyEqual");
    }
}