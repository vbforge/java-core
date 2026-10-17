package com.vbforge.generics_fundamentals;

import com.vbforge.generics_fundamentals.GenericClassesDemo.Box;
import com.vbforge.generics_fundamentals.GenericClassesDemo.Pair;

import java.util.List;
import java.util.function.Function;

/**
 * 🎯 TASKS — Generic Classes
 *
 * <p>Implement every method so that {@code GenericClassesTasksTest} passes.
 * The types {@link Box} and {@link Pair} come from {@link GenericClassesDemo}.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>Do NOT use raw types or unchecked casts</li>
 * </ul>
 */
public class GenericClassesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return a NEW pair with the two values in reversed order.
     *
     * <p>Examples:
     * <pre>
     *   swap(new Pair&lt;&gt;("age", 30))   → (30, age)    // Pair&lt;Integer, String&gt;
     *   swap(new Pair&lt;&gt;(1, 2))        → (2, 1)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Look at the return type — the type parameters trade places too</li>
     *   <li>A {@code Pair} is immutable, so there is nothing to modify in place</li>
     * </ul>
     *
     * @param pair the pair to flip (never null)
     * @param <A>  type of the first value
     * @param <B>  type of the second value
     * @return a new pair whose first value is {@code pair.getSecond()} and vice versa
     */
    public <A, B> Pair<B, A> swap(Pair<A, B> pair) {
        throw new UnsupportedOperationException("TODO: implement swap");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Apply a function to the value inside a box and wrap the result in a new box.
     * An empty box stays empty and the function must NOT be called.
     *
     * <p>Examples:
     * <pre>
     *   mapBox(Box.of("hello"), String::length)   → Box[5]        // Box&lt;Integer&gt;
     *   mapBox(Box.of(21), n -&gt; n * 2)            → Box[42]
     *   mapBox(Box.&lt;String&gt;empty(), String::length) → Box[empty]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Box.isPresent()}, {@code Box.get()}, {@code Box.of()} and {@code Box.empty()} are all you need</li>
     *   <li>{@code Box.empty()} infers its type from the return statement</li>
     * </ul>
     *
     * @param box    the source box (never null)
     * @param mapper function from the stored value to the result
     * @param <T>    type stored in the source box
     * @param <R>    type stored in the result box
     * @return a box holding {@code mapper.apply(value)}, or an empty box
     */
    public <T, R> Box<R> mapBox(Box<T> box, Function<T, R> mapper) {
        throw new UnsupportedOperationException("TODO: implement mapBox");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Return the first box in the list that holds a value, or an empty box if
     * the list is empty or every box is empty.
     *
     * <p>Examples:
     * <pre>
     *   firstPresent([empty, Box.of("b"), Box.of("c")]) → Box[b]
     *   firstPresent([empty, empty])                    → Box[empty]
     *   firstPresent([])                                → Box[empty]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Return the SAME box instance you found — do not rebuild it</li>
     * </ul>
     *
     * @param boxes the boxes to inspect (never null)
     * @param <T>   type stored in the boxes
     * @return the first present box, otherwise {@code Box.empty()}
     */
    public <T> Box<T> firstPresent(List<Box<T>> boxes) {
        throw new UnsupportedOperationException("TODO: implement firstPresent");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Combine two lists element by element into a list of pairs.
     * The result has the length of the SHORTER list; extra elements are ignored.
     *
     * <p>Examples:
     * <pre>
     *   zip(["a", "b"], [1, 2])       → [(a, 1), (b, 2)]
     *   zip(["a", "b", "c"], [1, 2])  → [(a, 1), (b, 2)]
     *   zip([], [1, 2])               → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Math.min} gives the number of pairs to build</li>
     *   <li>Use the indexed {@code get(i)} — the lists may be of different types</li>
     * </ul>
     *
     * @param firsts  source of the first values (never null)
     * @param seconds source of the second values (never null)
     * @param <A>     element type of the first list
     * @param <B>     element type of the second list
     * @return a new list of pairs, in order
     */
    public <A, B> List<Pair<A, B>> zip(List<A> firsts, List<B> seconds) {
        throw new UnsupportedOperationException("TODO: implement zip");
    }
}
