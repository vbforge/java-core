package com.vbforge.functional_interfaces;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 🎯 TASKS — Lambdas
 *
 * <p>Implement every method so that {@code LambdasTasksTest} passes.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>Return lambdas — do not create named or anonymous classes</li>
 * </ul>
 */
public class LambdasTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return a function that multiplies its argument by {@code factor}.
     *
     * <p>Examples:
     * <pre>
     *   multiplier(3).apply(5)   → 15
     *   multiplier(0).apply(99)  → 0
     *   multiplier(-2).apply(4)  → -8
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The lambda captures {@code factor}</li>
     * </ul>
     *
     * @param factor the number to multiply by
     * @return a function that multiplies by {@code factor}
     */
    public Function<Integer, Integer> multiplier(int factor) {
        throw new UnsupportedOperationException("TODO: implement multiplier");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Return {@code count} suppliers; the supplier at position {@code k}
     * (0-based) returns {@code k * k} every time it is called.
     *
     * <p>Examples:
     * <pre>
     *   squareSuppliers(4)  → suppliers returning 0, 1, 4, 9
     *   squareSuppliers(1)  → a supplier returning 0
     *   squareSuppliers(0)  → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The loop variable of a classic {@code for} cannot be captured — copy it first</li>
     * </ul>
     *
     * @param count how many suppliers to create (never negative)
     * @return a new list of suppliers
     */
    public List<Supplier<Integer>> squareSuppliers(int count) {
        throw new UnsupportedOperationException("TODO: implement squareSuppliers");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Wrap a supplier so the original is called AT MOST ONCE: the first call asks
     * {@code source} and remembers the result; later calls return the remembered result.
     * The source must NOT be called at all until the wrapper is called.
     *
     * <p>Examples:
     * <pre>
     *   Supplier&lt;String&gt; s = once(() -&gt; expensiveLookup());
     *   s.get();   // runs expensiveLookup()
     *   s.get();   // returns the same result, expensiveLookup() is not run again
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A local variable cannot be reassigned inside a lambda — keep the state in an object it can modify</li>
     *   <li>A {@code null} result is a valid result and must be remembered too</li>
     * </ul>
     *
     * @param source the supplier to call lazily (never null)
     * @param <T>    result type
     * @return a supplier that calls {@code source} at most once
     */
    public <T> Supplier<T> once(Supplier<T> source) {
        throw new UnsupportedOperationException("TODO: implement once");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Return the integer operation for the given symbol: {@code "+"}, {@code "-"},
     * {@code "*"} or {@code "/"} (integer division). Any other symbol throws
     * {@link IllegalArgumentException}.
     *
     * <p>Examples:
     * <pre>
     *   operation("+").apply(2, 3)  → 5
     *   operation("-").apply(2, 3)  → -1
     *   operation("*").apply(2, 3)  → 6
     *   operation("/").apply(7, 2)  → 3
     *   operation("%")              → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A {@code switch} on the symbol can return a different lambda in each branch</li>
     *   <li>Dividing by zero may throw the normal {@code ArithmeticException}</li>
     * </ul>
     *
     * @param symbol the operator symbol
     * @return the matching two-argument function
     */
    public BiFunction<Integer, Integer, Integer> operation(String symbol) {
        throw new UnsupportedOperationException("TODO: implement operation");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Return a comparator that orders strings by length (shortest first) and, for equal
     * lengths, alphabetically (using {@code String.compareTo}).
     *
     * <p>Examples:
     * <pre>
     *   sort ["pear", "fig", "apple", "kiwi"]  → [fig, kiwi, pear, apple]
     *   sort ["b", "a"]                        → [a, b]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Integer.compare} gives the length comparison; fall back to {@code compareTo} when it is 0</li>
     * </ul>
     *
     * @return the comparator
     */
    public Comparator<String> byLengthThenAlphabetical() {
        throw new UnsupportedOperationException("TODO: implement byLengthThenAlphabetical");
    }
}
