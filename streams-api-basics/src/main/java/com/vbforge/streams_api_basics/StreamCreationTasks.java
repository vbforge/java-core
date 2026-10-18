package com.vbforge.streams_api_basics;

import java.util.List;

/**
 * StreamCreationTasks — implement every method below to make {@code StreamCreationTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code StreamCreationDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=StreamCreationTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Every task is meant to be solved with a <b>stream pipeline</b>, not a hand-written loop.
 */
public class StreamCreationTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the first {@code count} powers of two, starting with {@code 1}
     * (that is 2<sup>0</sup>, 2<sup>1</sup>, 2<sup>2</sup>, …).
     *
     * <p>Examples:
     * <pre>
     *   powersOfTwo(5)  → [1,2,4,8,16]
     *   powersOfTwo(1)  → [1]
     *   powersOfTwo(0)  → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You need a stream where each element is computed from the previous one —
     *       look at {@code Stream.iterate(seed, f)}.</li>
     *   <li>That stream is infinite. Something has to cut it to {@code count} elements.</li>
     * </ul>
     *
     * @param count how many powers to produce (0 ≤ count ≤ 30)
     * @return the first {@code count} powers of two, in ascending order
     */
    public List<Integer> powersOfTwo(int count) {
        throw new UnsupportedOperationException("TODO: implement powersOfTwo");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the sum of all integers from {@code fromInclusive} to {@code toInclusive},
     * both ends included. If {@code fromInclusive > toInclusive} the range is empty
     * and the sum is {@code 0}.
     *
     * <p>Examples:
     * <pre>
     *   sumOfRange(1, 5)   → 15    (1+2+3+4+5)
     *   sumOfRange(3, 3)   → 3
     *   sumOfRange(-2, 2)  → 0
     *   sumOfRange(5, 1)   → 0     (empty range)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code IntStream} has two range factories — one includes the end, one does not.
     *       Pick the right one.</li>
     *   <li>What does an <em>empty</em> {@code IntStream} sum to?</li>
     * </ul>
     *
     * @param fromInclusive first number of the range
     * @param toInclusive   last number of the range
     * @return the sum of every integer in the range
     */
    public int sumOfRange(int fromInclusive, int toInclusive) {
        throw new UnsupportedOperationException("TODO: implement sumOfRange");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a list that contains {@code text} repeated {@code times} times.
     *
     * <p>Examples:
     * <pre>
     *   repeatText("ab", 3)  → ["ab","ab","ab"]
     *   repeatText("x",  1)  → ["x"]
     *   repeatText("x",  0)  → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The elements are all the same value and do not depend on each other —
     *       think {@code Stream.generate(supplier)} rather than {@code iterate}.</li>
     *   <li>Remember to bound the stream.</li>
     * </ul>
     *
     * @param text  the text to repeat
     * @param times how many copies (≥ 0)
     * @return a list with {@code times} copies of {@code text}
     */
    public List<String> repeatText(String text, int times) {
        throw new UnsupportedOperationException("TODO: implement repeatText");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Converts a string into a list of its characters, in order.
     *
     * <p>Examples:
     * <pre>
     *   toCharacterList("abc")  → ['a','b','c']
     *   toCharacterList("aab")  → ['a','a','b']
     *   toCharacterList("")     → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code String.chars()} is the natural source — but check the type of the
     *       stream it returns.</li>
     *   <li>The elements must become {@code Character} objects. Which method turns
     *       an {@code IntStream} into a {@code Stream} of objects?</li>
     * </ul>
     *
     * @param text any string (may be empty)
     * @return the characters of {@code text} as a list
     */
    public List<Character> toCharacterList(String text) {
        throw new UnsupportedOperationException("TODO: implement toCharacterList");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a <em>new</em> list containing every element of {@code first} followed by
     * every element of {@code second}. Neither input list is modified.
     *
     * <p>Examples:
     * <pre>
     *   concatLists(["a","b"], ["c"])  → ["a","b","c"]
     *   concatLists([],        ["c"])  → ["c"]
     *   concatLists([],        [])     → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Stream.concat} takes two <em>streams</em>, not two lists.</li>
     * </ul>
     *
     * @param first  the elements that come first
     * @param second the elements that come last
     * @return a new list: all of {@code first}, then all of {@code second}
     */
    public List<String> concatLists(List<String> first, List<String> second) {
        throw new UnsupportedOperationException("TODO: implement concatLists");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Converts a primitive {@code int[]} into a {@code List<Integer>} with the same
     * elements in the same order.
     *
     * <p>Examples:
     * <pre>
     *   boxArray([3,1,2])  → [3,1,2]
     *   boxArray([7])      → [7]
     *   boxArray([])       → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Stream.of(int[])} does <em>not</em> do what you might expect
     *       (see the first section of the demo).</li>
     *   <li>You need a stream of primitives first, then a way to turn them into objects.</li>
     * </ul>
     *
     * @param numbers the primitive array (may be empty)
     * @return a list holding the same numbers, boxed
     */
    public List<Integer> boxArray(int[] numbers) {
        throw new UnsupportedOperationException("TODO: implement boxArray");
    }
}
