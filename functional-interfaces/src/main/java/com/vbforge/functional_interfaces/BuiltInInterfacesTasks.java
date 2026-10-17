package com.vbforge.functional_interfaces;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 🎯 TASKS — Built-in Functional Interfaces
 *
 * <p>Implement every method so that {@code BuiltInInterfacesTasksTest} passes.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>Do NOT use streams — loops and the interfaces' own methods are enough</li>
 * </ul>
 */
public class BuiltInInterfacesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Combine predicates so the result is true only if NONE of them matches.
     * An empty list matches nothing, so the result is always true.
     *
     * <p>Examples:
     * <pre>
     *   noneOf([isEmpty, isBlank]).test("abc")  → true
     *   noneOf([isEmpty, isBlank]).test("")     → false
     *   noneOf([]).test("anything")             → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Stop as soon as one predicate matches</li>
     *   <li>{@code negate()} can flip a combined predicate</li>
     * </ul>
     *
     * @param predicates the predicates (never null)
     * @param <T>        tested type
     * @return a predicate that is true when no predicate matches
     */
    public <T> Predicate<T> noneOf(List<Predicate<T>> predicates) {
        throw new UnsupportedOperationException("TODO: implement noneOf");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Chain the steps into one function: the first step runs first, the output of each step is
     * the input of the next. An empty list gives a function that returns its argument unchanged.
     *
     * <p>Examples:
     * <pre>
     *   pipeline([trim, upper]).apply("  ab ")      → "AB"
     *   pipeline([plusOne, timesTwo]).apply(5)       → 12
     *   pipeline([]).apply("same")                   → "same"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Function.identity()} is the neutral starting point</li>
     *   <li>Pay attention to the order: {@code andThen} versus {@code compose}</li>
     * </ul>
     *
     * @param steps the transformations, in execution order (never null)
     * @param <T>   the value type
     * @return the combined function
     */
    public <T> Function<T, T> pipeline(List<Function<T, T>> steps) {
        throw new UnsupportedOperationException("TODO: implement pipeline");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Return a consumer that passes its argument to every consumer in the list,
     * in list order. An empty list gives a consumer that does nothing.
     *
     * <p>Examples:
     * <pre>
     *   broadcast([addToLogA, addToLogB]).accept("x")  → logA gets "x", then logB gets "x"
     *   broadcast([]).accept("x")                      → nothing happens
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The returned consumer must not call anything until it is used</li>
     * </ul>
     *
     * @param consumers the consumers to notify (never null)
     * @param <T>       consumed type
     * @return a consumer that notifies all of them
     */
    public <T> Consumer<T> broadcast(List<Consumer<T>> consumers) {
        throw new UnsupportedOperationException("TODO: implement broadcast");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Ask the suppliers in order and return the first result that is not {@code null}.
     * Suppliers after the winner must NOT be called. If every supplier returns {@code null}
     * (or the list is empty), return {@code null}.
     *
     * <p>Examples:
     * <pre>
     *   firstNonNull([() -&gt; null, () -&gt; "b", () -&gt; "c"])  → "b"      // third supplier never runs
     *   firstNonNull([() -&gt; null, () -&gt; null])             → null
     *   firstNonNull([])                                  → null
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Call {@code get()} once per supplier and keep the result in a local variable</li>
     * </ul>
     *
     * @param suppliers lazy sources of values (never null)
     * @param <T>       result type
     * @return the first non-null value, or {@code null}
     */
    public <T> T firstNonNull(List<Supplier<T>> suppliers) {
        throw new UnsupportedOperationException("TODO: implement firstNonNull");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Return the sum of the values for which {@code filter} is true. No boxing: work with
     * {@code int} and the primitive specialisation {@link IntPredicate}.
     *
     * <p>Examples:
     * <pre>
     *   sumIf([1, 2, 3, 4], n -&gt; n % 2 == 0)   → 6
     *   sumIf([1, 2, 3], n -&gt; n &gt; 10)          → 0
     *   sumIf([], n -&gt; true)                   → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code IntPredicate} names its method {@code test}</li>
     * </ul>
     *
     * @param values the numbers (never null)
     * @param filter decides which numbers are summed
     * @return the sum of the accepted numbers
     */
    public int sumIf(int[] values, IntPredicate filter) {
        throw new UnsupportedOperationException("TODO: implement sumIf");
    }


    // ── Task 6 ─────────────────────────────────────────────────────────────────
    /**
     * Convert every element with {@code mapper}, then keep only the CONVERTED values that
     * satisfy {@code filter}. Return a new list in the original order.
     *
     * <p>Examples:
     * <pre>
     *   mapAndFilter(["a", "bb", "ccc"], String::length, n -&gt; n &gt; 1)  → [2, 3]
     *   mapAndFilter([1, 2, 3], n -&gt; n * n, n -&gt; n &lt; 5)               → [1, 4]
     *   mapAndFilter([], x -&gt; x, x -&gt; true)                           → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The predicate tests the mapped value, not the original element</li>
     *   <li>Wildcards in the signature let callers pass more general functions</li>
     * </ul>
     *
     * @param input  the source list (never null)
     * @param mapper converts each element
     * @param filter tests each converted value
     * @param <T>    source element type
     * @param <R>    converted element type
     * @return a new list of the accepted converted values
     */
    public <T, R> List<R> mapAndFilter(List<T> input,
                                       Function<? super T, ? extends R> mapper,
                                       Predicate<? super R> filter) {
        throw new UnsupportedOperationException("TODO: implement mapAndFilter");
    }
}
