package com.vbforge.functional_interfaces;

import com.vbforge.functional_interfaces.FunctionalContractDemo.TriFunction;
import com.vbforge.functional_interfaces.FunctionalContractDemo.Validator;

import java.util.List;
import java.util.function.Function;

/**
 * 🎯 TASKS — The Functional Contract
 *
 * <p>Implement every method so that {@code FunctionalContractTasksTest} passes.
 * {@link Validator} and {@link TriFunction} come from {@link FunctionalContractDemo}.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>Return lambdas — do not create named or anonymous classes</li>
 * </ul>
 */
public class FunctionalContractTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Combine validators so that a value is valid only if EVERY validator accepts it.
     * An empty list accepts everything.
     *
     * <p>Examples:
     * <pre>
     *   allOf([notEmpty, shortText]).validate("abc")     → true
     *   allOf([notEmpty, shortText]).validate("toolong") → false
     *   allOf([]).validate("anything")                   → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Validator} already has a default method for combining two validators</li>
     *   <li>{@code Validator.alwaysValid()} is a natural starting value</li>
     * </ul>
     *
     * @param validators the validators to combine (never null)
     * @param <T>        type being validated
     * @return a validator that requires all of them to pass
     */
    public <T> Validator<T> allOf(List<Validator<T>> validators) {
        throw new UnsupportedOperationException("TODO: implement allOf");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Combine validators so that a value is valid if AT LEAST ONE validator accepts it.
     * An empty list accepts nothing.
     *
     * <p>Examples:
     * <pre>
     *   anyOf([isNumber, isBlank]).validate("42")   → true
     *   anyOf([isNumber, isBlank]).validate("abc")  → false
     *   anyOf([]).validate("anything")              → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Stop asking validators as soon as one accepts</li>
     *   <li>Loop over the validators INSIDE the returned lambda; the lambda captures the list</li>
     * </ul>
     *
     * @param validators the validators to combine (never null)
     * @param <T>        type being validated
     * @return a validator that requires at least one of them to pass
     */
    public <T> Validator<T> anyOf(List<Validator<T>> validators) {
        throw new UnsupportedOperationException("TODO: implement anyOf");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Turn a three-argument function into a chain of one-argument functions (currying).
     *
     * <p>Examples:
     * <pre>
     *   curry((a, b, c) -&gt; a + b + c).apply(1).apply(2).apply(3)       → 6
     *   curry((a, b, c) -&gt; a + "-" + b + "-" + c).apply("x").apply("y").apply("z") → "x-y-z"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The return type is three nested {@code Function}s — each lambda returns the next one</li>
     *   <li>Each inner lambda can still see the earlier arguments</li>
     * </ul>
     *
     * @param function the three-argument function (never null)
     * @param <A>      type of the first argument
     * @param <B>      type of the second argument
     * @param <C>      type of the third argument
     * @param <R>      result type
     * @return the curried form of {@code function}
     */
    public <A, B, C, R> Function<A, Function<B, Function<C, R>>> curry(TriFunction<A, B, C, R> function) {
        throw new UnsupportedOperationException("TODO: implement curry");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Wrap a {@code TriFunction} so that, if it throws a {@link RuntimeException},
     * the wrapper returns {@code fallback} instead. Normal results pass through unchanged.
     *
     * <p>Examples:
     * <pre>
     *   onError((a, b, c) -&gt; a / b / c, -1).apply(8, 2, 2)   → 2
     *   onError((a, b, c) -&gt; a / b / c, -1).apply(8, 0, 2)   → -1    // ArithmeticException swallowed
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The returned object is itself a {@code TriFunction} — write it as a lambda</li>
     * </ul>
     *
     * @param function the function that may fail (never null)
     * @param fallback the value to return when it throws
     * @param <A>      type of the first argument
     * @param <B>      type of the second argument
     * @param <C>      type of the third argument
     * @param <R>      result type
     * @return a safe version of {@code function}
     */
    public <A, B, C, R> TriFunction<A, B, C, R> onError(TriFunction<A, B, C, R> function, R fallback) {
        throw new UnsupportedOperationException("TODO: implement onError");
    }
}
