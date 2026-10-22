package com.vbforge.collections_framework_advanced;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

/**
 * 🎯 TASKS — TreeSet
 *
 * <p>Implement every method so that all tests in {@code TreeSetTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class TreeSetTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return the distinct words of a text in alphabetical order. A word is a maximal run of letters
     * {@code a-z}; everything else (spaces, digits, punctuation) separates words. Case is ignored and
     * all words are returned in lower case.
     *
     * <p>Examples:
     * <pre>
     *   uniqueSortedWords("the cat and The dog")   → [and, cat, dog, the]
     *   uniqueSortedWords("Hello, hello; HELLO!")  → [hello]
     *   uniqueSortedWords("a1b2")                  → [a, b]
     *   uniqueSortedWords("")                      → []
     *   uniqueSortedWords("123 !!!")               → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One collection gives you both "no duplicates" and "sorted"</li>
     *   <li>Watch out for empty strings produced by {@code split} at the start of the text</li>
     * </ul>
     *
     * @param text the text, never {@code null}
     * @return the distinct lower-case words in alphabetical order
     */
    public List<String> uniqueSortedWords(String text) {
        throw new UnsupportedOperationException("TODO: implement uniqueSortedWords");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Find the element of the set that is CLOSEST to {@code target}. When two elements are equally
     * close, return the SMALLER one. An empty set has no closest element.
     *
     * <p>Examples:
     * <pre>
     *   {10, 20, 30}, target 22  → Optional[20]
     *   {10, 20, 30}, target 26  → Optional[30]
     *   {10, 20, 30}, target 25  → Optional[20]    (tie: smaller wins)
     *   {10, 20, 30}, target 20  → Optional[20]
     *   {10, 20, 30}, target 5   → Optional[10]
     *   {}, target 5             → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The closest element is either the nearest one at-or-below the target or the nearest one at-or-above it</li>
     *   <li>Either neighbour may not exist</li>
     * </ul>
     *
     * @param numbers the sorted set, never {@code null}
     * @param target  the value to approximate
     * @return the closest element, if the set is not empty
     */
    public Optional<Integer> closest(TreeSet<Integer> numbers, int target) {
        throw new UnsupportedOperationException("TODO: implement closest");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Count the elements {@code e} with {@code fromInclusive <= e < toExclusive}. When the range is
     * empty or reversed ({@code fromInclusive >= toExclusive}) the answer is 0 — the method must not throw.
     *
     * <p>Examples:
     * <pre>
     *   {1, 3, 5, 7, 9}, from 3, to 8   → 3     (3, 5, 7)
     *   {1, 3, 5, 7, 9}, from 3, to 3   → 0
     *   {1, 3, 5, 7, 9}, from 8, to 3   → 0     (reversed range)
     *   {1, 3, 5, 7, 9}, from 0, to 100 → 5
     *   {}, from 0, to 10               → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A range view of the set already contains exactly the elements you need</li>
     *   <li>Creating a view with a reversed range throws {@code IllegalArgumentException}</li>
     * </ul>
     *
     * @param numbers       the sorted set, never {@code null}
     * @param fromInclusive the lower bound, inclusive
     * @param toExclusive   the upper bound, exclusive
     * @return how many elements lie in the range
     */
    public int countInRange(TreeSet<Integer> numbers, int fromInclusive, int toExclusive) {
        throw new UnsupportedOperationException("TODO: implement countInRange");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * REMOVE the {@code count} smallest elements from the set and return them in ascending order.
     * If the set has fewer than {@code count} elements, remove and return all of them.
     * A {@code count} of 0 (or less) changes nothing and returns an empty list.
     *
     * <p>Examples:
     * <pre>
     *   {5, 1, 9, 3}, count 2   → returns [1, 3],       set is now {5, 9}
     *   {5, 1},       count 5   → returns [1, 5],       set is now {}
     *   {5, 1},       count 0   → returns [],           set is still {1, 5}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One method both reads and removes the smallest element — and tells you when the set is empty</li>
     * </ul>
     *
     * @param numbers the set to take elements from (it is modified), never {@code null}
     * @param count   how many elements to remove
     * @return the removed elements in ascending order
     */
    public List<Integer> removeSmallest(TreeSet<Integer> numbers, int count) {
        throw new UnsupportedOperationException("TODO: implement removeSmallest");
    }
}
