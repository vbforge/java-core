package com.vbforge.streams_api_advanced;

import java.util.List;
import java.util.Optional;

/**
 * FlatMapTasks — implement every method below to make {@code FlatMapTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code FlatMapDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=FlatMapTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Every task is meant to be solved with a <b>stream pipeline</b>, not a hand-written loop.
 */
public class FlatMapTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Splits every sentence into words (separated by single spaces) and returns all words
     * in order. Empty sentences contribute no words.
     *
     * <p>Examples:
     * <pre>
     *   allWords(["to be or","not to be"])  → ["to","be","or","not","to","be"]
     *   allWords(["hello"])                 → ["hello"]
     *   allWords(["a b",""])                → ["a","b"]     (empty sentence → no words)
     *   allWords([])                        → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One sentence becomes many words — which operation is "one-to-many"?</li>
     *   <li>Check what {@code "".split(" ")} returns before you trust it.</li>
     * </ul>
     *
     * @param sentences sentences whose words are separated by single spaces (may contain empty strings)
     * @return every word of every sentence, in order
     */
    public List<String> allWords(List<String> sentences) {
        throw new UnsupportedOperationException("TODO: implement allWords");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Flattens a list of lists into a single list, keeping the order. Inner lists may be empty.
     *
     * <p>Examples:
     * <pre>
     *   flatten([[1,2],[3],[],[4,5]])  → [1,2,3,4,5]
     *   flatten([[7]])                 → [7]
     *   flatten([[],[]])               → []
     *   flatten([])                    → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Each inner list can produce a stream of its own elements.</li>
     * </ul>
     *
     * @param nested the list of lists
     * @return all inner elements in a single list
     */
    public List<Integer> flatten(List<List<Integer>> nested) {
        throw new UnsupportedOperationException("TODO: implement flatten");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns every combination of an element of {@code first} followed by an element of
     * {@code second}, concatenated as text. The result is ordered by {@code first}, then by
     * {@code second}.
     *
     * <p>Examples:
     * <pre>
     *   combine(["a","b"], ["1","2"])  → ["a1","a2","b1","b2"]
     *   combine(["x"],     ["1","2"])  → ["x1","x2"]
     *   combine(["a"],     [])         → []
     *   combine([],        ["1"])      → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Think of two nested loops: which level needs "one-to-many" and which one is a plain
     *       transformation of the inner element?</li>
     * </ul>
     *
     * @param first  the left parts
     * @param second the right parts
     * @return all {@code first.size() * second.size()} combinations
     */
    public List<String> combine(List<String> first, List<String> second) {
        throw new UnsupportedOperationException("TODO: implement combine");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the values of all <em>present</em> optionals, in order, and skips the empty ones.
     *
     * <p>Examples:
     * <pre>
     *   presentValues([Optional[1], Optional.empty, Optional[3]])  → [1,3]
     *   presentValues([Optional.empty])                            → []
     *   presentValues([Optional[5]])                               → [5]
     *   presentValues([])                                          → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This module targets Java 8 — {@code Optional.stream()} does not exist there.</li>
     *   <li>Either filter-then-unwrap, or map each optional to a stream of zero or one element.</li>
     * </ul>
     *
     * @param optionals the optionals to unwrap
     * @return the contained values, empty optionals dropped
     */
    public List<Integer> presentValues(List<Optional<Integer>> optionals) {
        throw new UnsupportedOperationException("TODO: implement presentValues");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns every distinct letter that occurs in the words, as characters in ascending order.
     * All input words are lower-case.
     *
     * <p>Examples:
     * <pre>
     *   distinctLetters(["hello","world"])  → ['d','e','h','l','o','r','w']
     *   distinctLetters(["aab","b"])        → ['a','b']
     *   distinctLetters([""])               → []
     *   distinctLetters([])                 → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code String.chars()} is an {@code IntStream}; a plain {@code map} over words would
     *       give you a stream of streams. Look for the primitive flavour of the flattening operation.</li>
     *   <li>Convert the ints back to {@code Character} objects at the end.</li>
     * </ul>
     *
     * @param words lower-case words (may contain empty strings)
     * @return the distinct letters, sorted ascending
     */
    public List<Character> distinctLetters(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement distinctLetters");
    }
}
