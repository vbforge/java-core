package com.vbforge.streams_api_advanced;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ParallelStreamsTasks — implement every method below to make {@code ParallelStreamsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ParallelStreamsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ParallelStreamsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Every task is meant to be solved with a <b>parallel stream pipeline</b>, not a hand-written loop,
 * and <b>without shared mutable state</b>.
 */
public class ParallelStreamsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the sum of all integers from {@code 1} to {@code n} (inclusive), computed with a
     * <b>parallel</b> primitive stream. For {@code n = 0} the sum is {@code 0}.
     * The result is a {@code long} — the sum of a million numbers does not fit into an {@code int}.
     *
     * <p>Examples:
     * <pre>
     *   sumUpTo(10)         → 55
     *   sumUpTo(1)          → 1
     *   sumUpTo(0)          → 0
     *   sumUpTo(1000000)    → 500000500000
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use the {@code long} flavour of the primitive streams so nothing overflows.</li>
     *   <li>A range factory plus the switch that makes the pipeline parallel is all you need.</li>
     * </ul>
     *
     * @param n the upper bound (≥ 0)
     * @return 1 + 2 + … + n
     */
    public long sumUpTo(long n) {
        throw new UnsupportedOperationException("TODO: implement sumUpTo");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the square of every number, computed on a <b>parallel</b> stream, <b>in the original
     * order</b>.
     *
     * <p>Examples:
     * <pre>
     *   squaresInOrder([1,2,3,4])  → [1,4,9,16]
     *   squaresInOrder([5])        → [25]
     *   squaresInOrder([])         → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Do <em>not</em> add to an outside list from a parallel {@code forEach} — the result
     *       would be incomplete or shuffled. Let a collector build the list.</li>
     * </ul>
     *
     * @param numbers input numbers
     * @return their squares, same order as the input
     */
    public List<Integer> squaresInOrder(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement squaresInOrder");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Counts how many numbers leave each remainder when divided by {@code divisor}, using a
     * <b>parallel</b> stream and a collector designed for concurrent use.
     *
     * <p>Examples:
     * <pre>
     *   countByRemainder([1,2,3,4,5,6], 3)  → {0=2, 1=2, 2=2}
     *   countByRemainder([3,6,9],       3)  → {0=3}
     *   countByRemainder([],            3)  → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Collectors} has a parallel-friendly variant of the grouping collector.</li>
     *   <li>Pick the downstream collector that counts — and check its result type.</li>
     * </ul>
     *
     * @param numbers non-negative input numbers
     * @param divisor the divisor ({@code > 0})
     * @return remainder → how many numbers have it
     */
    public Map<Integer, Long> countByRemainder(List<Integer> numbers, int divisor) {
        throw new UnsupportedOperationException("TODO: implement countByRemainder");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the sum of the lengths of all words, computed on a <b>parallel</b> stream
     * <b>without</b> any shared mutable variable (no external counter, no external list).
     *
     * <p>Examples:
     * <pre>
     *   totalLength(["alpha","beta","gamma","delta"])  → 19
     *   totalLength(["a"])                             → 1
     *   totalLength([])                                → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Transform to numbers first, then reduce. A primitive stream makes this trivial.</li>
     *   <li>If you use the explicit {@code reduce}, the identity must be neutral for the operation.</li>
     * </ul>
     *
     * @param words input words
     * @return the sum of {@code word.length()}
     */
    public int totalLength(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement totalLength");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns <em>any</em> even number of the list, or an empty {@link Optional} if there is none,
     * searching on a <b>parallel</b> stream. Which even number is returned is not specified —
     * only that it is one of the even numbers in the list.
     *
     * <p>Examples:
     * <pre>
     *   anyEven([1,3,4,7,8])  → Optional[4] or Optional[8]
     *   anyEven([2])          → Optional[2]
     *   anyEven([1,3,5])      → Optional.empty
     *   anyEven([])           → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One of the two "find" operations explicitly does not care about encounter order —
     *       which makes it the cheaper one on a parallel stream.</li>
     * </ul>
     *
     * @param numbers input numbers
     * @return some even element, if any
     */
    public Optional<Integer> anyEven(List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement anyEven");
    }
}
