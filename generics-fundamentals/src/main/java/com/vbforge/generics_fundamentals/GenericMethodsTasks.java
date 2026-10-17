package com.vbforge.generics_fundamentals;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 🎯 TASKS — Generic Methods
 *
 * <p>Implement every method so that {@code GenericMethodsTasksTest} passes.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>Do NOT use raw types or unchecked casts</li>
 * </ul>
 */
public class GenericMethodsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Swap the elements at positions {@code i} and {@code j} of the array, IN PLACE.
     *
     * <p>Examples:
     * <pre>
     *   swap(["a", "b", "c"], 0, 2)  → array becomes ["c", "b", "a"]
     *   swap([1, 2, 3], 1, 1)        → array unchanged
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You need one temporary variable of type {@code T}</li>
     *   <li>Invalid indexes may simply raise the usual {@code ArrayIndexOutOfBoundsException}</li>
     * </ul>
     *
     * @param array the array to modify (never null)
     * @param i     first index
     * @param j     second index
     * @param <T>   element type
     */
    public <T> void swap(T[] array, int i, int j) {
        throw new UnsupportedOperationException("TODO: implement swap");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Count how many elements of the collection are equal to {@code target}.
     * Both the collection elements and the target may be {@code null}.
     *
     * <p>Examples:
     * <pre>
     *   countOccurrences(["a", "b", "a"], "a")   → 2
     *   countOccurrences(["a", "b"], "z")        → 0
     *   countOccurrences(["a", null, null], null) → 2
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code java.util.Objects.equals} is null-safe</li>
     * </ul>
     *
     * @param collection the elements to scan (never null)
     * @param target     the value to look for (may be null)
     * @param <T>        element type
     * @return number of elements equal to {@code target}
     */
    public <T> int countOccurrences(Collection<T> collection, T target) {
        throw new UnsupportedOperationException("TODO: implement countOccurrences");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Build a NEW list containing {@code item} repeated {@code times} times.
     * {@code times == 0} gives an empty list; a negative value must throw
     * {@link IllegalArgumentException}.
     *
     * <p>Examples:
     * <pre>
     *   repeat("ab", 3)  → [ab, ab, ab]
     *   repeat(7, 1)     → [7]
     *   repeat("x", 0)   → []
     *   repeat("x", -1)  → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The returned list must be modifiable by the caller</li>
     * </ul>
     *
     * @param item  the value to repeat (may be null)
     * @param times how many copies
     * @param <T>   element type
     * @return a new list of size {@code times}
     */
    public <T> List<T> repeat(T item, int times) {
        throw new UnsupportedOperationException("TODO: implement repeat");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Swap keys and values of a map. If two keys share the same value the
     * inverse would lose information, so throw {@link IllegalArgumentException}.
     *
     * <p>Examples:
     * <pre>
     *   invertMap({"one"=1, "two"=2})  → {1="one", 2="two"}   // Map&lt;Integer, String&gt;
     *   invertMap({})                  → {}
     *   invertMap({"a"=1, "b"=1})      → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Map.put} returns the previous value for that key</li>
     *   <li>Note how the type parameters trade places in the return type</li>
     * </ul>
     *
     * @param map the map to invert (never null)
     * @param <K> key type of the input map
     * @param <V> value type of the input map
     * @return a new map from each value to its key
     */
    public <K, V> Map<V, K> invertMap(Map<K, V> map) {
        throw new UnsupportedOperationException("TODO: implement invertMap");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Return a NEW list with duplicates removed, keeping the FIRST occurrence of
     * each element and preserving the original order. The input is not modified.
     *
     * <p>Examples:
     * <pre>
     *   removeDuplicates([3, 1, 3, 2, 1])  → [3, 1, 2]
     *   removeDuplicates(["a", "a"])       → [a]
     *   removeDuplicates([])               → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Pick a {@code Set} implementation that remembers insertion order, or track what you have seen</li>
     * </ul>
     *
     * @param list the source list (never null)
     * @param <T>  element type
     * @return a new list without duplicates
     */
    public <T> List<T> removeDuplicates(List<T> list) {
        throw new UnsupportedOperationException("TODO: implement removeDuplicates");
    }
}
