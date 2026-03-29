package com.vbforge.arrays_collections_basics;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CollectionsTasks — implement every method below to make {@code CollectionsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code CollectionsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=CollectionsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class CollectionsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Rotates an integer array to the right by {@code k} positions in-place.
     * After rotation, each element moves {@code k} steps to the right, wrapping
     * around from the last position back to the first.
     *
     * <p>Examples:
     * <pre>
     *   rotateRight([1,2,3,4,5], 2)  → [4,5,1,2,3]
     *   rotateRight([1,2,3],     1)  → [3,1,2]
     *   rotateRight([1,2,3],     3)  → [1,2,3]   (full rotation = no change)
     *   rotateRight([1,2,3],     4)  → [3,1,2]   (k > length is fine: 4 % 3 = 1)
     *   rotateRight([],          5)  → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Reduce {@code k} with {@code k = k % arr.length} to handle oversized k.</li>
     *   <li>Use {@link java.util.Arrays#copyOfRange} to split and re-combine the array,
     *       OR use a three-reversal trick: reverse the whole array, then reverse
     *       {@code [0..k-1]}, then reverse {@code [k..n-1]}.</li>
     * </ul>
     *
     * @param arr the array to rotate (modified in-place)
     * @param k   the number of positions to rotate right (may be ≥ arr.length)
     */
    public void rotateRight(int[] arr, int k) {
        throw new UnsupportedOperationException("TODO: implement rotateRight");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Removes all duplicate elements from {@code list}, keeping only the
     * <em>first</em> occurrence of each value. The relative order of remaining
     * elements must be preserved.
     *
     * <p>Examples:
     * <pre>
     *   removeDuplicates([1,2,3,2,1,4])  → [1,2,3,4]
     *   removeDuplicates([5,5,5,5])      → [5]
     *   removeDuplicates([1,2,3])        → [1,2,3]
     *   removeDuplicates([])             → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use a {@link java.util.HashSet} to track which values you have already seen.</li>
     *   <li>Iterate with an {@link java.util.Iterator} so you can safely call
     *       {@code it.remove()} when a duplicate is detected.</li>
     * </ul>
     *
     * @param list the list to deduplicate in-place
     */
    public void removeDuplicates(List<Integer> list) {
        throw new UnsupportedOperationException("TODO: implement removeDuplicates");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Counts the frequency of every word in {@code words} and returns a
     * {@link Map} from word → count. The comparison is case-insensitive
     * (store keys in lower-case).
     *
     * <p>Examples:
     * <pre>
     *   wordFrequency(["cat","Dog","cat","FISH","dog"])
     *       → {"cat"=2, "dog"=2, "fish"=1}
     *
     *   wordFrequency([])  → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use {@link java.util.HashMap} as the return type.</li>
     *   <li>{@code map.merge(key, 1, Integer::sum)} is the cleanest accumulation
     *       pattern — or use {@code getOrDefault} + {@code put}.</li>
     * </ul>
     *
     * @param words list of words (may contain mixed case)
     * @return a map of lower-cased word to its occurrence count
     */
    public Map<String, Integer> wordFrequency(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement wordFrequency");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a new list containing only the elements that appear in <em>both</em>
     * {@code a} and {@code b} (set intersection), with <strong>no duplicates</strong>,
     * in the order they first appear in {@code a}.
     *
     * <p>Examples:
     * <pre>
     *   intersection([1,2,3,4], [3,4,5,6])   → [3,4]
     *   intersection([1,1,2,3], [1,3,5])      → [1,3]   (1 appears once in result)
     *   intersection([1,2,3],   [4,5,6])      → []
     *   intersection([],        [1,2,3])      → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Put all elements of {@code b} into a {@link java.util.HashSet} for O(1) lookup.</li>
     *   <li>Iterate {@code a}; if an element is in the set AND not yet in a "seen" set,
     *       add it to the result list and mark it seen.</li>
     * </ul>
     *
     * @param a first list
     * @param b second list
     * @return list of common elements, first-occurrence order from {@code a}, no duplicates
     */
    public List<Integer> intersection(List<Integer> a, List<Integer> b) {
        throw new UnsupportedOperationException("TODO: implement intersection");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Groups the strings in {@code words} by their first character (lowercased)
     * and returns a {@link Map} where each key is a character and the value is
     * the {@link List} of words starting with that character, in the order they
     * appear in the input.
     *
     * <p>Examples:
     * <pre>
     *   groupByFirstChar(["apple","avocado","banana","blueberry","cherry"])
     *       → {'a': ["apple","avocado"], 'b': ["banana","blueberry"], 'c': ["cherry"]}
     *
     *   groupByFirstChar(["Apple","ant"])
     *       → {'a': ["Apple","ant"]}   (key is lower-cased first char)
     *
     *   groupByFirstChar([])  → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use a {@code HashMap<Character, List<String>>}.</li>
     *   <li>{@code map.computeIfAbsent(key, k -> new ArrayList<>()).add(word)} is
     *       the cleanest way to build each bucket.</li>
     * </ul>
     *
     * @param words list of words to group
     * @return map from first-character (lowercased) to list of matching words
     */
    public Map<Character, List<String>> groupByFirstChar(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement groupByFirstChar");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Given a list of integers, returns the two indices {@code [i, j]} (with
     * {@code i < j}) such that {@code list.get(i) + list.get(j) == target}.
     * Guaranteed that exactly one such pair exists.
     *
     * <p>Examples:
     * <pre>
     *   twoSum([2,7,11,15], 9)   → [0,1]   (2+7=9)
     *   twoSum([3,2,4],    6)   → [1,2]   (2+4=6)
     *   twoSum([3,3],      6)   → [0,1]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A naive O(n²) double loop works but aim for the O(n) approach.</li>
     *   <li>Use a {@code HashMap<Integer, Integer>} mapping each value to its index.
     *       For each element {@code v} at index {@code i}, look up
     *       {@code target - v} in the map. If found, you have your pair.</li>
     * </ul>
     *
     * @param list   list of integers
     * @param target the desired sum
     * @return int array {@code [i, j]} with {@code i < j}
     */
    public int[] twoSum(List<Integer> list, int target) {
        throw new UnsupportedOperationException("TODO: implement twoSum");
    }

    // ── Task 7 ─────────────────────────────────────────────────────────────────

    /**
     * Given a sorted integer array (ascending, may contain duplicates), returns
     * a new sorted array with all duplicates removed.
     *
     * <p>Examples:
     * <pre>
     *   deduplicateSorted([1,1,2,3,3,4])  → [1,2,3,4]
     *   deduplicateSorted([1,2,3])        → [1,2,3]
     *   deduplicateSorted([5,5,5])        → [5]
     *   deduplicateSorted([])             → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Because the array is sorted, duplicates are always adjacent —
     *       no set needed. Walk the array and collect an element only when
     *       it differs from the previous one.</li>
     *   <li>Build the result into an {@link java.util.ArrayList}, then convert
     *       to array with {@code list.stream().mapToInt(Integer::intValue).toArray()}
     *       or a simple loop.</li>
     * </ul>
     *
     * @param sorted ascending-sorted array (may be empty)
     * @return new array with duplicates removed, same ascending order
     */
    public int[] deduplicateSorted(int[] sorted) {
        throw new UnsupportedOperationException("TODO: implement deduplicateSorted");
    }
}
