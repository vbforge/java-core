package com.vbforge.streams_api_advanced;

import java.util.List;
import java.util.Map;

/**
 * GroupingTasks — implement every method below to make {@code GroupingTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code GroupingDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=GroupingTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Every task is meant to be solved with a <b>stream pipeline</b>, not a hand-written loop.
 */
public class GroupingTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Groups the words by their length. Inside each group the words keep their input order.
     *
     * <p>Examples:
     * <pre>
     *   groupByLength(["a","bb","c","dd","eee"])  → {1=["a","c"], 2=["bb","dd"], 3=["eee"]}
     *   groupByLength(["hi"])                     → {2=["hi"]}
     *   groupByLength([])                         → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The classifier is simply "what is the length of this word".</li>
     * </ul>
     *
     * @param words input words
     * @return length → words of that length (encounter order inside each list)
     */
    public Map<Integer, List<String>> groupByLength(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement groupByLength");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Counts how many words start with each letter. The letter is compared and stored
     * <b>lower-cased</b>. Words are never empty.
     *
     * <p>Examples:
     * <pre>
     *   countByFirstLetter(["Apple","avocado","Banana"])  → {'a'=2, 'b'=1}
     *   countByFirstLetter(["x"])                         → {'x'=1}
     *   countByFirstLetter([])                            → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Pick a downstream collector that counts. Check the type of the count it returns —
     *       it must match the declared return type.</li>
     * </ul>
     *
     * @param words non-empty words
     * @return lower-cased first letter → number of words starting with it
     */
    public Map<Character, Long> countByFirstLetter(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement countByFirstLetter");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Splits the numbers into even and odd ones. The key {@code true} holds the even numbers,
     * the key {@code false} holds the odd ones. <b>Both keys must always be present</b>, even when
     * a side is empty. Order inside each list is preserved.
     *
     * <p>Examples:
     * <pre>
     *   partitionEvenOdd([1,2,3,4])  → {true=[2,4], false=[1,3]}
     *   partitionEvenOdd([2,4])      → {true=[2,4], false=[]}
     *   partitionEvenOdd([])         → {true=[],    false=[]}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One of the two splitting collectors guarantees both keys — the other does not.</li>
     * </ul>
     *
     * @param numbers input numbers
     * @return {@code true} → evens, {@code false} → odds
     */
    public Map<Boolean, List<Integer>> partitionEvenOdd(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement partitionEvenOdd");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Groups the words by their lower-cased first letter and joins the words of each group into
     * one string, separated by a comma (no spaces), in input order. Words are never empty.
     *
     * <p>Examples:
     * <pre>
     *   joinByFirstLetter(["apple","avocado","banana"])  → {'a'="apple,avocado", 'b'="banana"}
     *   joinByFirstLetter(["Cat","car"])                 → {'c'="Cat,car"}
     *   joinByFirstLetter([])                            → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You want a string per group, not a list. A downstream collector can itself take a
     *       downstream collector — think "transform, then join".</li>
     * </ul>
     *
     * @param words non-empty words
     * @return lower-cased first letter → comma-joined words
     */
    public Map<Character, String> joinByFirstLetter(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement joinByFirstLetter");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Counts the "long" and the "short" words. A word is long when its length is
     * {@code >= minLength}. The key {@code true} counts the long words, {@code false} the short
     * ones. <b>Both keys must always be present</b>, with {@code 0L} when nothing falls on a side.
     *
     * <p>Assume {@code minLength = 3}.
     *
     * <p>Examples:
     * <pre>
     *   countLongWords(["a","abc","abcd"], 3)  → {true=2, false=1}
     *   countLongWords(["a","b"],          3)  → {true=0, false=2}
     *   countLongWords([],                 3)  → {true=0, false=0}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Split into two groups and count each — combine the splitting collector with a
     *       downstream one.</li>
     * </ul>
     *
     * @param words     input words
     * @param minLength the length from which a word counts as long
     * @return {@code true} → number of long words, {@code false} → number of short words
     */
    public Map<Boolean, Long> countLongWords(List<String> words, int minLength) {
        throw new UnsupportedOperationException("TODO: implement countLongWords");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Groups the words first by length and then, inside each length, by their lower-cased first
     * letter. The innermost lists keep the input order. Words are never empty.
     *
     * <p>Examples:
     * <pre>
     *   groupByLengthThenInitial(["ab","ac","b","bb"])
     *       → {1={'b'=["b"]}, 2={'a'=["ab","ac"], 'b'=["bb"]}}
     *   groupByLengthThenInitial(["Hi","he"])
     *       → {2={'h'=["Hi","he"]}}
     *   groupByLengthThenInitial([])  → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The downstream collector of the outer grouping is another grouping.</li>
     *   <li>Declare the result type first; it helps the compiler infer the nested generics.</li>
     * </ul>
     *
     * @param words non-empty words
     * @return length → (lower-cased first letter → words)
     */
    public Map<Integer, Map<Character, List<String>>> groupByLengthThenInitial(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement groupByLengthThenInitial");
    }
}
