package com.vbforge.generics_fundamentals;

import java.util.List;

/**
 * 🎯 TASKS — Bounded Types
 *
 * <p>Implement every method so that {@code BoundedTypesTasksTest} passes.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures (the bounds are part of the task)</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>Do NOT use raw types or unchecked casts</li>
 * </ul>
 */
public class BoundedTypesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return the largest element of the list. For ties return any of the equal elements.
     * Throw {@link IllegalArgumentException} if the list is empty.
     *
     * <p>Examples:
     * <pre>
     *   max([3, 9, 4])          → 9
     *   max(["pear", "apple"])  → "pear"
     *   max([5])                → 5
     *   max([])                 → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The bound on {@code T} is what makes {@code compareTo} available</li>
     * </ul>
     *
     * @param list the elements (never null)
     * @param <T>  element type, comparable to itself
     * @return the largest element
     */
    public <T extends Comparable<T>> T max(List<T> list) {
        throw new UnsupportedOperationException("TODO: implement max");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Return {@code true} if the list is sorted in ascending order. Equal
     * neighbours are allowed (non-strict). Empty and single-element lists are sorted.
     *
     * <p>Examples:
     * <pre>
     *   isSorted([1, 2, 2, 5])   → true
     *   isSorted([1, 3, 2])      → false
     *   isSorted(["a", "b"])     → true
     *   isSorted([])             → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Only neighbouring pairs matter</li>
     * </ul>
     *
     * @param list the elements (never null)
     * @param <T>  element type, comparable to itself
     * @return whether each element is &lt;= its successor
     */
    public <T extends Comparable<T>> boolean isSorted(List<T> list) {
        throw new UnsupportedOperationException("TODO: implement isSorted");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Limit {@code value} to the closed range [{@code min}, {@code max}]: return
     * {@code min} if value is below it, {@code max} if above it, otherwise {@code value}.
     * Throw {@link IllegalArgumentException} if {@code min} is greater than {@code max}.
     *
     * <p>Examples:
     * <pre>
     *   clamp(5, 1, 10)     → 5
     *   clamp(-3, 0, 10)    → 0
     *   clamp(42, 0, 10)    → 10
     *   clamp("m", "a", "f") → "f"
     *   clamp(1, 10, 0)     → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Validate the range first</li>
     *   <li>Return the original object, not a copy, when it is already inside the range</li>
     * </ul>
     *
     * @param value the value to limit
     * @param min   lower bound (inclusive)
     * @param max   upper bound (inclusive)
     * @param <T>   type comparable to itself
     * @return the limited value
     */
    public <T extends Comparable<T>> T clamp(T value, T min, T max) {
        throw new UnsupportedOperationException("TODO: implement clamp");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Return the arithmetic mean of the numbers in the array as a {@code double}.
     * An empty array has the average {@code 0.0}.
     *
     * <p>Examples:
     * <pre>
     *   average([1, 2, 3, 4])      → 2.5      // Integer[]
     *   average([1.5, 2.5])        → 2.0      // Double[]
     *   average([])                → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Number} offers a method that converts any subtype to {@code double}</li>
     *   <li>Divide as {@code double}, not as {@code int}</li>
     * </ul>
     *
     * @param numbers the numbers to average (never null)
     * @param <T>     any numeric wrapper type
     * @return the mean, or {@code 0.0} for an empty array
     */
    public <T extends Number> double average(T[] numbers) {
        throw new UnsupportedOperationException("TODO: implement average");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Sum, as a {@code double}, only the elements that are STRICTLY greater
     * than {@code threshold}. Return {@code 0.0} if none qualifies.
     *
     * <p>This needs BOTH bounds: comparing values and converting them to {@code double}.
     *
     * <p>Examples:
     * <pre>
     *   sumAbove([1, 5, 10], 4)        → 15.0     // 5 + 10
     *   sumAbove([2.5, 3.5], 3.0)      → 3.5
     *   sumAbove([1, 2, 3], 3)         → 0.0      // 3 is not strictly greater
     *   sumAbove([], 0)                → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Each bound contributes exactly one of the two operations you need</li>
     * </ul>
     *
     * @param list      the numbers (never null)
     * @param threshold exclusive lower limit
     * @param <T>       a number type that is comparable to itself
     * @return sum of the qualifying elements
     */
    public <T extends Number & Comparable<T>> double sumAbove(List<T> list, T threshold) {
        throw new UnsupportedOperationException("TODO: implement sumAbove");
    }
}
