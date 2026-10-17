package com.vbforge.generics_fundamentals;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * 🎯 TASKS — Wildcards
 *
 * <p>Implement every method so that {@code WildcardsTasksTest} passes.
 * Choose the right wildcard from the signature; the signature IS the lesson.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>You may add a private generic helper method where a task hints at it</li>
 *   <li>Do NOT use raw types or unchecked casts</li>
 * </ul>
 */
public class WildcardsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return the sum of the SQUARES of all numbers, as a {@code double}.
     * An empty collection gives {@code 0.0}.
     *
     * <p>Examples:
     * <pre>
     *   sumOfSquares([1, 2, 3])        → 14.0      // List&lt;Integer&gt;
     *   sumOfSquares([1.5, 2.0])       → 6.25      // List&lt;Double&gt;
     *   sumOfSquares([])               → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This parameter only PRODUCES values for you</li>
     * </ul>
     *
     * @param numbers any collection of numbers (never null)
     * @return the sum of squares
     */
    public double sumOfSquares(Collection<? extends Number> numbers) {
        throw new UnsupportedOperationException("TODO: implement sumOfSquares");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Append the integers {@code from} to {@code toInclusive} (both included, ascending)
     * to the end of {@code target}. If {@code from > toInclusive}, throw
     * {@link IllegalArgumentException} and leave the list untouched.
     *
     * <p>Examples:
     * <pre>
     *   addRange(listOfInteger, 1, 3)   → list gets 1, 2, 3
     *   addRange(listOfNumber, 5, 5)    → list gets 5           // List&lt;Number&gt; is accepted
     *   addRange(listOfObject, 1, 2)    → list gets 1, 2        // List&lt;Object&gt; is accepted
     *   addRange(list, 3, 1)            → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This parameter only CONSUMES values you give it</li>
     * </ul>
     *
     * @param target     list that can hold Integers (never null)
     * @param from       first value to add
     * @param toInclusive last value to add
     */
    public void addRange(List<? super Integer> target, int from, int toInclusive) {
        throw new UnsupportedOperationException("TODO: implement addRange");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Add every element of {@code source} that satisfies {@code filter} to the end of
     * {@code destination}, keeping order. Return how many elements were added.
     *
     * <p>Examples:
     * <pre>
     *   transferMatching([1, 2, 3, 4], numbers, n -&gt; n % 2 == 0)   → 2   // numbers gets 2, 4
     *   transferMatching(["a", "bb"], objects, s -&gt; s.length() &gt; 5) → 0   // nothing added
     *   transferMatching([], destination, x -&gt; true)               → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Source = producer, destination = consumer, predicate = consumer</li>
     *   <li>The source is NOT modified</li>
     * </ul>
     *
     * @param source      where elements are read from (never null)
     * @param destination where matching elements are appended (never null)
     * @param filter      decides which elements are transferred
     * @param <T>         the common element type
     * @return the number of elements added
     */
    public <T> int transferMatching(List<? extends T> source,
                                    List<? super T> destination,
                                    Predicate<? super T> filter) {
        throw new UnsupportedOperationException("TODO: implement transferMatching");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Count the elements of the list that are not {@code null}.
     *
     * <p>Examples:
     * <pre>
     *   countNonNull(["a", null, "b"])   → 2     // List&lt;String&gt;
     *   countNonNull([1, 2, 3])          → 3     // List&lt;Integer&gt;
     *   countNonNull([null, null])       → 0
     *   countNonNull([])                 → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The element type is irrelevant — which wildcard says "I do not care"?</li>
     * </ul>
     *
     * @param list a list of anything (never null)
     * @return the number of non-null elements
     */
    public int countNonNull(List<?> list) {
        throw new UnsupportedOperationException("TODO: implement countNonNull");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Reverse the order of the elements of the list IN PLACE.
     *
     * <p>Examples:
     * <pre>
     *   reverseInPlace(["a", "b", "c"])  → list becomes [c, b, a]
     *   reverseInPlace([1, 2])           → list becomes [2, 1]
     *   reverseInPlace([])               → still []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Writing into a {@code List<?>} does not compile directly — see the wildcard capture
     *       section of {@code WildcardsDemo}</li>
     *   <li>Do not call {@code java.util.Collections.reverse}; the point is to do the capture yourself</li>
     * </ul>
     *
     * @param list the list to reverse (never null, modifiable)
     */
    public void reverseInPlace(List<?> list) {
        throw new UnsupportedOperationException("TODO: implement reverseInPlace");
    }


    // ── Task 6 ─────────────────────────────────────────────────────────────────
    /**
     * Return the largest element of the collection, using the same signature style as
     * {@code java.util.Collections.max}. Throw {@link IllegalArgumentException}
     * if the collection is empty.
     *
     * <p>The element type may inherit its {@code Comparable} implementation from a
     * supertype (for example {@code Apple} compares as {@code Comparable<Fruit>}).
     *
     * <p>Examples:
     * <pre>
     *   maxOf([3, 9, 4])                       → 9
     *   maxOf([Apple(120g), Apple(180g)])      → Apple(180g)   // List&lt;Apple&gt;
     *   maxOf(["b", "a"])                      → "b"
     *   maxOf([])                              → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Do not call {@code Collections.max} — implement the comparison yourself</li>
     *   <li>The input is a producer: elements can be read as {@code T}</li>
     * </ul>
     *
     * @param items the elements (never null)
     * @param <T>   element type, comparable to itself or a supertype
     * @return the largest element
     */
    public <T extends Comparable<? super T>> T maxOf(Collection<? extends T> items) {
        throw new UnsupportedOperationException("TODO: implement maxOf");
    }
}
