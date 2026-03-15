package com.vbforge.basics.controlflow;

import java.util.List;

/**
 * 🎯 TASKS — Control Flow Statements
 *
 * <p>Your goal: implement every method below so that all tests
 * in {@link ControlFlowTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>You may add private helper methods if needed</li>
 * </ul>
 */
public class ControlFlowTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Using a switch expression (arrow syntax), map an HTTP status code
     * to its standard category name.
     *
     * <p>Mapping:
     * <pre>
     *   200, 201, 204        → "success"
     *   301, 302             → "redirect"
     *   400, 401, 403, 404   → "client error"
     *   500, 502, 503        → "server error"
     *   anything else        → "unknown"
     * </pre>
     */
    public String httpCategory(int statusCode) {
        throw new UnsupportedOperationException("Task 1 not implemented yet");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Return the grade letter for a given score using if-else chains.
     *
     * <p>Scale:
     * <pre>
     *   90–100 → "A"
     *   80–89  → "B"
     *   70–79  → "C"
     *   60–69  → "D"
     *   0–59   → "F"
     * </pre>
     *
     * Throw {@link IllegalArgumentException} for scores outside 0–100.
     */
    public String letterGrade(int score) {
        throw new UnsupportedOperationException("Task 2 not implemented yet");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Using a {@code for} loop, return the sum of all integers from
     * {@code start} to {@code end} inclusive.
     * If {@code start > end}, return 0.
     *
     * <p>Examples:
     * <pre>
     *   sumRange(1, 5)  → 15   (1+2+3+4+5)
     *   sumRange(3, 3)  → 3
     *   sumRange(5, 1)  → 0
     * </pre>
     */
    public int sumRange(int start, int end) {
        throw new UnsupportedOperationException("Task 3 not implemented yet");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Using a {@code while} loop, return the largest power of 2
     * that is less than or equal to {@code n}.
     *
     * <p>Throw {@link IllegalArgumentException} for {@code n < 1}.
     *
     * <p>Examples:
     * <pre>
     *   floorPowerOfTwo(1)  → 1
     *   floorPowerOfTwo(5)  → 4
     *   floorPowerOfTwo(8)  → 8
     *   floorPowerOfTwo(9)  → 8
     *   floorPowerOfTwo(100)→ 64
     * </pre>
     */
    public int floorPowerOfTwo(int n) {
        throw new UnsupportedOperationException("Task 4 not implemented yet");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Using a loop with {@code continue}, collect all elements from the list
     * that are strictly positive (> 0) and return them as a new list,
     * preserving the original order.
     *
     * <p>Examples:
     * <pre>
     *   filterPositive([1, -2, 3, 0, 4])  → [1, 3, 4]
     *   filterPositive([-1, -2])           → []
     *   filterPositive([])                 → []
     * </pre>
     */
    public List<Integer> filterPositive(List<Integer> numbers) {
        throw new UnsupportedOperationException("Task 5 not implemented yet");
    }


    // ── Task 6 ─────────────────────────────────────────────────────────────────
    /**
     * Using pattern matching ({@code instanceof} or switch pattern matching),
     * format the given object as a string according to its type:
     *
     * <pre>
     *   Integer i  → "int(" + i + ")"
     *   Double  d  → "double(" + d + ")"
     *   String  s  → "str(" + s.length() + "):" + s
     *   Boolean b  → b ? "yes" : "no"
     *   null       → "null"
     *   other      → "?"
     * </pre>
     *
     * <p>Examples:
     * <pre>
     *   formatTyped(42)       → "int(42)"
     *   formatTyped(3.14)     → "double(3.14)"
     *   formatTyped("hi")     → "str(2):hi"
     *   formatTyped(true)     → "yes"
     *   formatTyped(null)     → "null"
     * </pre>
     */
    public String formatTyped(Object obj) {
        throw new UnsupportedOperationException("Task 6 not implemented yet");
    }
}
