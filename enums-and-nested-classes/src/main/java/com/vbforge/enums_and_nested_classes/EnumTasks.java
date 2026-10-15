package com.vbforge.enums_and_nested_classes;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 🎯 TASKS — Enums
 *
 * <p>Implement every method marked {@code TODO} so that the tests in
 * {@code EnumTasksTest} pass. Task 5 lives INSIDE the {@link Operation} enum.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures, enum constants or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} line with your code</li>
 *   <li>You may add private helpers if you need them</li>
 * </ul>
 */
public class EnumTasks {

    // ── Supporting types (already complete — do not edit) ─────────────────────

    public enum Level { LOW, MEDIUM, HIGH }

    public enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }


    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Convert free-form text into a {@link Level}.
     *
     * <p>Match the constant name ignoring case and surrounding whitespace.
     * Return {@link Level#LOW} when the text is {@code null}, blank or does not
     * match any constant — this method must never throw.
     *
     * <p>Examples:
     * <pre>
     *   parseLevel("high")        → HIGH
     *   parseLevel("  Medium  ")  → MEDIUM
     *   parseLevel("extreme")     → LOW
     *   parseLevel(null)          → LOW
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Enum.valueOf} is case-sensitive and throws on a miss</li>
     *   <li>Case conversion without a {@code Locale} depends on the machine's settings
     *       (think of the Turkish dotted i) — {@code Locale.ROOT} is the safe choice</li>
     * </ul>
     *
     * @param text raw user input, may be {@code null}
     * @return the matching level, or {@code LOW} as the fallback
     */
    public Level parseLevel(String text) {
        throw new UnsupportedOperationException("TODO: implement parseLevel");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Return the next level, wrapping around after the last one.
     *
     * <p>Examples:
     * <pre>
     *   nextLevel(LOW)    → MEDIUM
     *   nextLevel(MEDIUM) → HIGH
     *   nextLevel(HIGH)   → LOW
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Every enum constant knows its position — see {@code ordinal()}</li>
     *   <li>{@code values()} gives you all constants in declaration order</li>
     * </ul>
     *
     * @param current the current level (never {@code null})
     * @return the following level in declaration order, cyclically
     */
    public Level nextLevel(Level current) {
        throw new UnsupportedOperationException("TODO: implement nextLevel");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Count how often each level occurs.
     *
     * <p>The returned map must be an {@link java.util.EnumMap}, must contain
     * EVERY level (levels that never occur map to {@code 0}) and therefore
     * iterates in declaration order: LOW, MEDIUM, HIGH.
     *
     * <p>Examples:
     * <pre>
     *   countByLevel([LOW, HIGH, LOW]) → {LOW=2, MEDIUM=0, HIGH=1}
     *   countByLevel([])               → {LOW=0, MEDIUM=0, HIGH=0}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code EnumMap} needs the enum's {@code Class} object in its constructor</li>
     *   <li>Pre-fill the map first, then count</li>
     * </ul>
     *
     * @param levels the observed levels (never {@code null}, no {@code null} elements)
     * @return an {@code EnumMap} with one entry per level
     */
    public Map<Level, Integer> countByLevel(List<Level> levels) {
        throw new UnsupportedOperationException("TODO: implement countByLevel");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Return all days from {@code from} to {@code to}, both inclusive.
     *
     * <p>The result must be an {@link java.util.EnumSet} iterating in week order.
     * The range does NOT wrap around the weekend: if {@code from} comes after
     * {@code to}, return an empty set.
     *
     * <p>Examples:
     * <pre>
     *   daysBetween(WEDNESDAY, FRIDAY) → [WEDNESDAY, THURSDAY, FRIDAY]
     *   daysBetween(SUNDAY, SUNDAY)    → [SUNDAY]
     *   daysBetween(FRIDAY, MONDAY)    → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code EnumSet} has factory methods for ranges and for empty sets</li>
     *   <li>Enums are {@code Comparable} — compare positions, not names</li>
     * </ul>
     *
     * @param from first day of the range
     * @param to   last day of the range
     * @return an {@code EnumSet} with the days in the range
     */
    public Set<Day> daysBetween(Day from, Day to) {
        throw new UnsupportedOperationException("TODO: implement daysBetween");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Arithmetic operations. The constants, the {@code symbol} field and the
     * {@link #symbol()} getter are given — implement {@link #apply(int, int)}.
     */
    public enum Operation {
        ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/");

        private final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }

        /**
         * Apply this operation to two operands.
         *
         * <p>{@code DIVIDE} is integer division and must let the JVM's own
         * {@link ArithmeticException} escape when {@code b} is zero.
         *
         * <p>Examples:
         * <pre>
         *   ADD.apply(6, 3)      → 9
         *   SUBTRACT.apply(6, 3) → 3
         *   MULTIPLY.apply(6, 3) → 18
         *   DIVIDE.apply(7, 2)   → 3
         *   DIVIDE.apply(1, 0)   → throws ArithmeticException
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Either branch on {@code this} or give each constant its own body —
         *       try the second option, it is the idiomatic one for enums</li>
         *   <li>If you use constant bodies, remember the method may become {@code abstract}</li>
         * </ul>
         *
         * @param a left operand
         * @param b right operand
         * @return the result of {@code a <op> b}
         */
        public int apply(int a, int b) {
            throw new UnsupportedOperationException("TODO: implement apply");
        }
    }


    // ── Task 6 ─────────────────────────────────────────────────────────────────
    /**
     * Find the {@link Operation} that uses the given symbol.
     *
     * <p>Throw {@link IllegalArgumentException} when no operation has that symbol
     * (including {@code null}).
     *
     * <p>Examples:
     * <pre>
     *   operationFromSymbol("+")  → ADD
     *   operationFromSymbol("/")  → DIVIDE
     *   operationFromSymbol("%")  → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The symbol is a field, not the constant's name — {@code valueOf} cannot help here</li>
     *   <li>{@code Operation.values()} lets you look at every constant</li>
     * </ul>
     *
     * @param symbol the operator symbol, may be {@code null}
     * @return the matching operation
     */
    public Operation operationFromSymbol(String symbol) {
        throw new UnsupportedOperationException("TODO: implement operationFromSymbol");
    }
}
