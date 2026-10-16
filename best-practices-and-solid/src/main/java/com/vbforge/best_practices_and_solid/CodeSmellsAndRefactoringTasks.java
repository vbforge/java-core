package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.CodeSmellsAndRefactoringDemo.IntRange;
import com.vbforge.best_practices_and_solid.CodeSmellsAndRefactoringDemo.Money;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntUnaryOperator;

/**
 * 🎯 TASKS — Code Smells and Refactoring
 *
 * <p>Implement every method so that all tests in {@link CodeSmellsAndRefactoringTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 * </ul>
 */
public class CodeSmellsAndRefactoringTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Build a refactoring <b>safety net</b>: run the old and the new implementation
     * for every integer from {@code from} to {@code to} (both inclusive) and return the
     * <b>first</b> input where their results differ, or an empty {@link OptionalInt}
     * when they agree everywhere. When {@code from > to} there is nothing to compare,
     * so the result is empty.
     *
     * <p>Examples:
     * <pre>
     *   firstDifference(x -> x * 2, x -> x + x, 0, 100)  → empty
     *   firstDifference(x -> x * x, x -> x + x, 0, 5)    → 1      (1 vs 2; at 0 both give 0)
     *   firstDifference(x -> x * x, x -> x + x, 2, 5)    → 3      (at 2 both give 4)
     *   firstDifference(x -> x, x -> x + 1, 5, 1)        → empty  (empty range)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link IntUnaryOperator#applyAsInt} runs a function</li>
     *   <li>{@code OptionalInt.of(...)} and {@code OptionalInt.empty()}</li>
     *   <li>Stop at the first mismatch — do not keep comparing</li>
     * </ul>
     *
     * @param legacy     the old implementation
     * @param refactored the new implementation
     * @param from       first input to check (inclusive)
     * @param to         last input to check (inclusive)
     * @return the first differing input, if any
     */
    public OptionalInt firstDifference(IntUnaryOperator legacy, IntUnaryOperator refactored, int from, int to) {
        throw new UnsupportedOperationException("TODO: implement firstDifference");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Add up a list of {@link Money} values in the given currency. An empty list gives
     * zero in that currency. Mixing currencies must fail loudly with an
     * {@link IllegalArgumentException} — a value object protects you from that mistake.
     *
     * <p>Examples:
     * <pre>
     *   sum([USD 1.50, USD 2.25], "USD")  → USD 3.75   (375 cents)
     *   sum([], "EUR")                     → EUR 0.00
     *   sum([USD 1.00, EUR 1.00], "USD")   → throws IllegalArgumentException
     *   sum([EUR 1.00], "USD")             → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Start from {@code Money.of(currency, 0)} and fold with {@code plus}</li>
     *   <li>The currency check already lives inside {@code Money} — do not repeat it</li>
     * </ul>
     *
     * @param amounts  the amounts to add
     * @param currency the expected currency
     * @return the total
     */
    public Money sum(List<Money> amounts, String currency) {
        throw new UnsupportedOperationException("TODO: implement sum");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Decide whether two inclusive ranges share at least one integer. Ranges that merely
     * touch at one end overlap. The result must not depend on argument order.
     *
     * <p>Examples:
     * <pre>
     *   overlaps([1, 5], [5, 9])    → true    (they share 5)
     *   overlaps([1, 4], [5, 9])    → false
     *   overlaps([1, 10], [3, 4])   → true    (one contains the other)
     *   overlaps([5, 9], [1, 5])    → true    (same as the first example, swapped)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Think about when two ranges <i>cannot</i> overlap, then negate it</li>
     *   <li>Use {@code getStart()} and {@code getEnd()}</li>
     * </ul>
     *
     * @param a the first range
     * @param b the second range
     * @return {@code true} if the ranges share a value
     */
    public boolean overlaps(IntRange a, IntRange b) {
        throw new UnsupportedOperationException("TODO: implement overlaps");
    }
}
