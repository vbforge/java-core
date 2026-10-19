package com.vbforge.streams_api_advanced;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

/**
 * CustomCollectorsTasks — implement every method below to make {@code CustomCollectorsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code CustomCollectorsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=CustomCollectorsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Every task is meant to be solved with a <b>stream pipeline</b>, not a hand-written loop.
 */
public class CustomCollectorsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the even numbers (in input order) as an <b>unmodifiable</b> list: calling
     * {@code add}, {@code remove} or {@code set} on the result must throw
     * {@link UnsupportedOperationException}.
     *
     * <p>Examples:
     * <pre>
     *   unmodifiableEvens([1,2,3,4])  → [2,4]    (read-only)
     *   unmodifiableEvens([1,3])      → []       (read-only)
     *   unmodifiableEvens([])         → []       (read-only)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Collect to a list, then wrap it — a collector that applies a function to another
     *       collector's result does exactly that.</li>
     *   <li>{@code Collections} has a wrapper for read-only lists.</li>
     * </ul>
     *
     * @param numbers input numbers
     * @return the even numbers as a read-only list
     */
    public List<Integer> unmodifiableEvens(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement unmodifiableEvens");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns, for each lower-cased first letter, the <em>longest</em> word that starts with it.
     * The result holds plain words, <b>not</b> {@code Optional}s. Words are never empty.
     * (If several words tie for longest, either may be returned; the tests avoid ties.)
     *
     * <p>Examples:
     * <pre>
     *   longestWordByInitial(["apple","avocado","fig","banana","blue"])
     *       → {'a'="avocado", 'f'="fig", 'b'="banana"}
     *   longestWordByInitial(["Kiwi"])  → {'k'="Kiwi"}
     *   longestWordByInitial([])        → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Group, then use a downstream collector that finds the maximum by length.</li>
     *   <li>The maximum collector returns an {@code Optional}. Unwrap it inside the collector
     *       so that the map values are plain strings.</li>
     * </ul>
     *
     * @param words non-empty words
     * @return lower-cased first letter → longest word starting with it
     */
    public Map<Character, String> longestWordByInitial(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement longestWordByInitial");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a <b>custom collector</b> that keeps the longest string of a stream.
     * For several strings of the same maximal length the <em>first</em> one (in encounter order)
     * wins. For an empty stream the result is the empty string {@code ""}.
     * The collector must also give the same result on a <b>parallel</b> stream.
     *
     * <p>Used as {@code stream.collect(longestStringCollector())}.
     *
     * <p>Examples:
     * <pre>
     *   ["a","ccc","bb"]    → "ccc"
     *   ["ab","cd","e"]     → "ab"      (tie: first wins)
     *   ["x"]               → "x"
     *   []                  → ""
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Build it with {@code Collector.of(supplier, accumulator, combiner, finisher)}.</li>
     *   <li>The accumulator type must be mutable, e.g. a one-element array holding the best string so far.</li>
     *   <li>In the combiner, {@code left} comes <em>before</em> {@code right} in encounter order.</li>
     * </ul>
     *
     * @return a collector producing the longest string, or {@code ""} for no input
     */
    public Collector<String, ?, String> longestStringCollector() {
        throw new UnsupportedOperationException("TODO: implement longestStringCollector");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns count, sum, minimum, maximum and average of the word lengths in one object.
     *
     * <p>Examples:
     * <pre>
     *   lengthStats(["a","bb","ccc"])  → count=3, sum=6, min=1, max=3, average=2.0
     *   lengthStats(["abcd"])          → count=1, sum=4, min=4, max=4, average=4.0
     *   lengthStats([])                → count=0, sum=0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a built-in collector that produces an {@link IntSummaryStatistics}.</li>
     * </ul>
     *
     * @param words input words (may be empty)
     * @return statistics of the word lengths
     */
    public IntSummaryStatistics lengthStats(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement lengthStats");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a <b>custom collector</b> that computes the average of a stream of integers as a
     * {@code Double}. For an empty stream the result is {@code 0.0}. The collector must also give
     * the same result on a <b>parallel</b> stream.
     *
     * <p>Used as {@code stream.collect(averageCollector())}.
     *
     * <p>Examples:
     * <pre>
     *   [2,4,9]   → 5.0
     *   [1,2]     → 1.5
     *   [7]       → 7.0
     *   []        → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You need two numbers while accumulating (a running sum and a count) — but the final
     *       result is a single {@code Double}. Which of the four collector parts converts one into the other?</li>
     *   <li>The combiner must add the sums and the counts of two partial accumulators.</li>
     * </ul>
     *
     * @return a collector producing the arithmetic mean, or {@code 0.0} for no input
     */
    public Collector<Integer, ?, Double> averageCollector() {
        throw new UnsupportedOperationException("TODO: implement averageCollector");
    }
}
