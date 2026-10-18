package com.vbforge.streams_api_basics;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * TerminalOperationsTasks — implement every method below to make
 * {@code TerminalOperationsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code TerminalOperationsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=TerminalOperationsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Every task is meant to be solved with a <b>stream pipeline</b>, not a hand-written loop.
 */
public class TerminalOperationsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Converts every word to upper case and joins them with {@code delimiter}.
     * An empty list gives an empty string.
     *
     * <p>Examples:
     * <pre>
     *   joinUpperCase(["a","b","c"], "-")  → "A-B-C"
     *   joinUpperCase(["hello"],     ", ") → "HELLO"
     *   joinUpperCase([],            ",")  → ""
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Collectors} has a collector made for exactly this.</li>
     * </ul>
     *
     * @param words     the words to join
     * @param delimiter the text placed between two words
     * @return the upper-cased words separated by {@code delimiter}
     */
    public String joinUpperCase(List<String> words, String delimiter) {
        throw new UnsupportedOperationException("TODO: implement joinUpperCase");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a map from each distinct word to its length. If a word appears several
     * times it still appears <em>once</em> in the map.
     *
     * <p>Examples:
     * <pre>
     *   wordLengths(["cat","dog","cat"])  → {"cat"=3, "dog"=3}
     *   wordLengths(["hello"])            → {"hello"=5}
     *   wordLengths([])                   → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The simplest {@code Collectors.toMap} overload throws on a repeated key.
     *       Look for the overload that accepts a merge function.</li>
     * </ul>
     *
     * @param words the words (may contain duplicates)
     * @return word → length, one entry per distinct word
     */
    public Map<String, Integer> wordLengths(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement wordLengths");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the largest number, or an empty {@link Optional} if the list is empty.
     *
     * <p>Examples:
     * <pre>
     *   largest([3,9,2])    → Optional[9]
     *   largest([-5,-1])    → Optional[-1]
     *   largest([7])        → Optional[7]
     *   largest([])         → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There are at least two good terminal operations for this
     *       ({@code reduce} with one argument, or one that takes a {@code Comparator}).</li>
     *   <li>Why can the result not be a plain {@code int}?</li>
     * </ul>
     *
     * @param numbers input numbers (may be empty)
     * @return the maximum, or {@code Optional.empty()} for an empty list
     */
    public Optional<Integer> largest(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement largest");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the product of all numbers. The product of an empty list is {@code 1}.
     * The result is a {@code long}, so products larger than {@code Integer.MAX_VALUE}
     * must come out correct.
     *
     * <p>Examples:
     * <pre>
     *   product([2,3,4])            → 24
     *   product([5])                → 5
     *   product([0,7])              → 0
     *   product([100000,100000])    → 10000000000
     *   product([])                 → 1
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This is a fold. Which value is neutral for multiplication?</li>
     *   <li>The elements are {@code Integer} but the result is {@code long} — either use the
     *       {@code reduce} form that allows a different result type, or convert the
     *       elements to a primitive stream first.</li>
     * </ul>
     *
     * @param numbers input numbers (may be empty)
     * @return the product as a {@code long}
     */
    public long product(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement product");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if <em>every</em> number is strictly greater than zero.
     * An empty list counts as {@code true}.
     *
     * <p>Examples:
     * <pre>
     *   allPositive([1,2,3])  → true
     *   allPositive([1,0,3])  → false
     *   allPositive([-1])     → false
     *   allPositive([])       → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One of the three match operations does this, and it stops at the first counter-example.</li>
     *   <li>Check what it returns for an empty stream — it matches the spec above.</li>
     * </ul>
     *
     * @param numbers input numbers (may be empty)
     * @return whether all numbers are &gt; 0
     */
    public boolean allPositive(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement allPositive");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the first word (in list order) that has at least {@code minLength}
     * characters, or an empty {@link Optional} if there is none.
     *
     * <p>Examples:
     * <pre>
     *   firstLongWord(["hi","hello","world"], 4)  → Optional["hello"]
     *   firstLongWord(["hi","yo"],            4)  → Optional.empty
     *   firstLongWord(["abcd"],               4)  → Optional["abcd"]
     *   firstLongWord([],                     4)  → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Select, then take the first one that survives. The terminal operation already
     *       returns an {@code Optional} for you.</li>
     * </ul>
     *
     * @param words     input words
     * @param minLength minimum length (inclusive)
     * @return the first word with {@code length() >= minLength}, if any
     */
    public Optional<String> firstLongWord(List<String> words, int minLength) {
        throw new UnsupportedOperationException("TODO: implement firstLongWord");
    }

    // ── Task 7 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the average length of the words. An empty list gives {@code 0.0}.
     *
     * <p>Examples:
     * <pre>
     *   averageLength(["a","bb","ccc"])  → 2.0
     *   averageLength(["a","bb"])        → 1.5
     *   averageLength(["abc"])           → 3.0
     *   averageLength([])                → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Primitive streams know how to average. What does that method return,
     *       and how do you supply a default for "no elements"?</li>
     * </ul>
     *
     * @param words input words (may be empty)
     * @return the average word length, or {@code 0.0} if there are no words
     */
    public double averageLength(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement averageLength");
    }

    // ── Task 8 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the set of distinct first letters (lower-cased, as one-character strings)
     * of all non-empty words.
     *
     * <p>Examples:
     * <pre>
     *   uniqueInitials(["Apple","avocado","Banana"])  → {"a","b"}
     *   uniqueInitials(["cat","","Car"])              → {"c"}      (empty word ignored)
     *   uniqueInitials([])                            → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Filter, then transform, then pick the collector that drops duplicates.</li>
     *   <li>{@code String.substring(0, 1)} gives the first letter as a {@code String}.</li>
     * </ul>
     *
     * @param words input words (elements never {@code null}; may be empty strings)
     * @return the distinct lower-cased initials
     */
    public Set<String> uniqueInitials(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement uniqueInitials");
    }
}
