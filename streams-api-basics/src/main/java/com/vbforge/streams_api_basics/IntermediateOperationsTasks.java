package com.vbforge.streams_api_basics;

import java.util.List;
import java.util.function.Predicate;

/**
 * IntermediateOperationsTasks — implement every method below to make
 * {@code IntermediateOperationsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code IntermediateOperationsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=IntermediateOperationsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Every task is meant to be solved with a <b>stream pipeline</b>, not a hand-written loop.
 */
public class IntermediateOperationsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Keeps only the even numbers and returns their squares, in the original order.
     *
     * <p>Examples:
     * <pre>
     *   evenSquares([1,2,3,4])  → [4,16]
     *   evenSquares([1,3,5])    → []
     *   evenSquares([-2,0,6])   → [4,0,36]
     *   evenSquares([])         → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One step selects, another step transforms — in that order.</li>
     * </ul>
     *
     * @param numbers the input numbers (may be empty)
     * @return squares of the even inputs, same relative order
     */
    public List<Integer> evenSquares(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement evenSquares");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Drops blank strings (empty, or made only of whitespace), trims the remaining ones
     * and converts them to upper case. The order is preserved.
     *
     * <p>Examples:
     * <pre>
     *   cleanAndUpperCase([" a ","","  ","bc"])  → ["A","BC"]
     *   cleanAndUpperCase(["x"])                 → ["X"]
     *   cleanAndUpperCase(["", " "])             → []
     *   cleanAndUpperCase([])                    → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This module targets Java 8 — {@code String.isBlank()} does not exist there.
     *       {@code trim()} and {@code isEmpty()} do.</li>
     *   <li>Several {@code map} steps in a row are fine and read well.</li>
     * </ul>
     *
     * @param words input strings (never {@code null}, elements never {@code null})
     * @return the cleaned, upper-cased, non-blank strings
     */
    public List<String> cleanAndUpperCase(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement cleanAndUpperCase");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the words sorted by length (shortest first). Words of the same length
     * are ordered alphabetically (natural {@code String} order).
     *
     * <p>Examples:
     * <pre>
     *   sortByLengthThenAlphabetically(["pear","fig","apple","kiwi","plum"])
     *       → ["fig","kiwi","pear","plum","apple"]
     *   sortByLengthThenAlphabetically(["b","a"])  → ["a","b"]
     *   sortByLengthThenAlphabetically([])         → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code sorted(Comparator)} plus a comparator built from {@code Comparator.comparing(...)}
     *       and a tie-breaker.</li>
     *   <li>The input list must not be modified.</li>
     * </ul>
     *
     * @param words input words
     * @return a new list sorted by length, then alphabetically
     */
    public List<String> sortByLengthThenAlphabetically(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement sortByLengthThenAlphabetically");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Removes duplicates and returns the remaining numbers from largest to smallest.
     *
     * <p>Examples:
     * <pre>
     *   distinctDescending([3,1,3,2,1])  → [3,2,1]
     *   distinctDescending([5,5,5])      → [5]
     *   distinctDescending([-1,-3,-1])   → [-1,-3]
     *   distinctDescending([])           → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Comparator} has a ready-made factory for reverse natural order.</li>
     * </ul>
     *
     * @param numbers input numbers (may contain duplicates)
     * @return unique numbers in descending order
     */
    public List<Integer> distinctDescending(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement distinctDescending");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns one "page" of {@code items}. Pages are zero-based and every page holds
     * {@code pageSize} items, except possibly the last one, which may be shorter.
     * A page that lies completely past the end of the list is empty.
     *
     * <p>Assume {@code items = [1,2,3,4,5,6,7,8,9,10]}.
     *
     * <p>Examples:
     * <pre>
     *   page(items, 0, 3)  → [1,2,3]
     *   page(items, 1, 3)  → [4,5,6]
     *   page(items, 3, 3)  → [10]      (last, shorter page)
     *   page(items, 5, 3)  → []        (past the end)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Two operations that drop elements from the front and from the back of a stream.</li>
     *   <li>Both take a {@code long}. Compute the offset in a way that cannot overflow
     *       an {@code int} for large page indexes.</li>
     * </ul>
     *
     * @param items     all items
     * @param pageIndex zero-based page number (≥ 0)
     * @param pageSize  items per page (&gt; 0)
     * @return the items of the requested page
     */
    public List<Integer> page(List<Integer> items, int pageIndex, int pageSize) {
        throw new UnsupportedOperationException("TODO: implement page");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the sum of the squares of all <em>odd</em> numbers in the array.
     * Negative odd numbers count too.
     *
     * <p>Examples:
     * <pre>
     *   sumOfOddSquares([1,2,3,4])  → 10     (1 + 9)
     *   sumOfOddSquares([2,4])      → 0
     *   sumOfOddSquares([-3])       → 9
     *   sumOfOddSquares([])         → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Stay with primitives: there is a stream factory for {@code int[]} that avoids boxing.</li>
     *   <li>In Java, {@code -3 % 2} is {@code -1}, not {@code 1}. Choose your odd-check carefully.</li>
     * </ul>
     *
     * @param numbers the input array (may be empty)
     * @return the sum of squares of the odd elements
     */
    public int sumOfOddSquares(int[] numbers) {
        throw new UnsupportedOperationException("TODO: implement sumOfOddSquares");
    }

    // ── Task 7 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the first {@code n} numbers (in list order) that satisfy {@code predicate}.
     * If fewer than {@code n} numbers match, returns all matches.
     *
     * <p><b>Laziness requirement:</b> the predicate must be evaluated only as often as
     * needed — once {@code n} matches have been found, no further element may be tested.
     *
     * <p>Assume {@code numbers = [1,2,3,4,5,6,7,8,9,10]} and {@code even} is the predicate {@code n -> n % 2 == 0}.
     *
     * <p>Examples:
     * <pre>
     *   firstNMatches(numbers, even, 3)  → [2,4,6]   (predicate called for 1..6 only)
     *   firstNMatches(numbers, even, 0)  → []
     *   firstNMatches(numbers, even, 99) → [2,4,6,8,10]
     *   firstNMatches([],      even, 3)  → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Which intermediate operation stops the pipeline early?</li>
     *   <li>Do <em>not</em> collect all matches first and cut the list afterwards.</li>
     * </ul>
     *
     * @param numbers   input numbers
     * @param predicate the condition to test
     * @param n         how many matches to return (≥ 0)
     * @return up to {@code n} matching numbers, in input order
     */
    public List<Integer> firstNMatches(List<Integer> numbers, Predicate<Integer> predicate, int n) {
        throw new UnsupportedOperationException("TODO: implement firstNMatches");
    }
}
