package com.vbforge.streams_api_advanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * ParallelStreamsDemo — annotated reference for parallel streams.
 *
 * <p>A parallel stream splits its source into chunks, processes the chunks on several threads
 * (the common {@link java.util.concurrent.ForkJoinPool}) and merges the partial results.
 * The pipeline code stays the same — only the way it is executed changes.
 *
 * <p>Topics:
 * <ol>
 *   <li>Creating and switching — {@code parallelStream()}, {@code parallel()}, {@code sequential()}, {@code isParallel()}</li>
 *   <li>Deterministic results — what parallel streams guarantee</li>
 *   <li>Safe accumulation — shared mutable state vs reduction</li>
 *   <li>Concurrent collectors and {@code findAny}</li>
 * </ol>
 *
 * <p>This demo only prints values that are the same on every machine and every run.
 * Whether parallel is actually <em>faster</em> depends on data size, cost per element and
 * available cores — measure before you parallelise.
 */
public class ParallelStreamsDemo {

    public static void main(String[] args) {
        creatingParallelStreamsDemo();
        System.out.println("---");
        deterministicResultsDemo();
        System.out.println("---");
        safeAccumulationDemo();
        System.out.println("---");
        concurrentCollectorsDemo();
    }

    // ── 1. Creating and switching ─────────────────────────────────────────────

    /**
     * Two ways to ask for a parallel stream: {@code collection.parallelStream()} or
     * {@code stream.parallel()}. {@code sequential()} switches back.
     *
     * <p>WHY it matters: you opt in explicitly — streams are sequential by default.
     *
     * <ul>
     *   <li>{@code isParallel()} reports the current mode</li>
     *   <li>The mode belongs to the <em>whole pipeline</em>, not to one step</li>
     * </ul>
     *
     * <p>Common gotcha: <b>the last call wins</b>. {@code stream().parallel().map(...).sequential()}
     * runs the entire pipeline — including the {@code map} — sequentially.
     */
    static void creatingParallelStreamsDemo() {
        System.out.println("=== Creating parallel streams ===");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        System.out.println("stream()                   : " + numbers.stream().isParallel());                       // false
        System.out.println("parallelStream()           : " + numbers.parallelStream().isParallel());               // true
        System.out.println("stream().parallel()        : " + numbers.stream().parallel().isParallel());            // true
        System.out.println("parallel().sequential()    : " + numbers.stream().parallel().sequential().isParallel()); // false
        System.out.println("sequential().parallel()    : " + numbers.stream().sequential().parallel().isParallel()); // true

        // The switch applies to the whole pipeline, even steps written before it
        boolean mixed = numbers.stream().parallel().map(n -> n * 2).sequential().isParallel();
        System.out.println("parallel().map().sequential(): " + mixed);   // false
    }

    // ── 2. Deterministic results ──────────────────────────────────────────────

    /**
     * Parallel execution changes <em>who</em> does the work and <em>when</em>, not the
     * <em>result</em> — as long as the operations are stateless, non-interfering and the
     * reduction is associative.
     *
     * <ul>
     *   <li>{@code sum}, {@code count}, {@code min}, {@code max}: same value as sequential</li>
     *   <li>{@code collect(toList())} and {@code map}: the result keeps the <b>encounter order</b></li>
     *   <li>{@code forEachOrdered}: actions run one at a time, in encounter order</li>
     *   <li>{@code forEach}: arbitrary order — never rely on it</li>
     * </ul>
     *
     * <p>Common gotcha: the order of <em>side effects</em> inside {@code map}/{@code peek}/{@code forEach}
     * is not defined on a parallel stream, even though the collected result is ordered.
     */
    static void deterministicResultsDemo() {
        System.out.println("=== Deterministic results ===");

        long sum = LongStream.rangeClosed(1, 1_000_000).parallel().sum();
        System.out.println("sum 1..1_000_000           : " + sum);   // 500000500000

        List<Integer> squares = IntStream.rangeClosed(1, 10).boxed().parallel()
                .map(n -> n * n)
                .collect(Collectors.toList());
        System.out.println("squares (ordered)          : " + squares);   // [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]

        List<Integer> viaForEachOrdered = new ArrayList<>();
        IntStream.rangeClosed(1, 10).boxed().parallel().forEachOrdered(viaForEachOrdered::add);
        System.out.println("forEachOrdered             : " + viaForEachOrdered);   // [1, 2, ..., 10]

        long evens = IntStream.rangeClosed(1, 100_000).parallel().filter(n -> n % 2 == 0).count();
        System.out.println("count evens                : " + evens);   // 50000
    }

    // ── 3. Safe accumulation ──────────────────────────────────────────────────

    /**
     * Never accumulate into a shared, non-thread-safe container from a parallel stream.
     *
     * <p>WHY: several threads call the lambda at the same time. {@code ArrayList.add} from many threads
     * can lose elements, duplicate them or throw {@link ArrayIndexOutOfBoundsException}.
     * <pre>
     *   // WRONG — do not do this
     *   List&lt;Integer&gt; bad = new ArrayList&lt;&gt;();
     *   IntStream.range(0, 10_000).parallel().forEach(bad::add);
     * </pre>
     *
     * <p>Safe alternatives, best first:
     * <ol>
     *   <li>{@code collect(...)} — every thread fills its own container, the combiner merges them</li>
     *   <li>{@code reduce(identity, accumulator, combiner)} — immutable partial results</li>
     *   <li>a thread-safe accumulator such as {@link LongAdder} — correct, but a reduction is usually clearer</li>
     * </ol>
     *
     * <p>Common gotcha: {@code reduce(10, Integer::sum)} is wrong in parallel — the identity is added once
     * <em>per chunk</em>. The identity must be neutral ({@code 0} for addition).
     */
    static void safeAccumulationDemo() {
        System.out.println("=== Safe accumulation ===");

        List<Integer> collected = IntStream.range(0, 10_000).boxed().parallel()
                .collect(Collectors.toList());
        System.out.println("collect size               : " + collected.size());   // 10000

        List<String> words = Arrays.asList("alpha", "beta", "gamma", "delta");
        int totalLength = words.parallelStream()
                .reduce(0, (acc, w) -> acc + w.length(), Integer::sum);
        System.out.println("reduce total length        : " + totalLength);   // 19

        LongAdder adder = new LongAdder();
        IntStream.rangeClosed(1, 1000).parallel().forEach(adder::add);
        System.out.println("LongAdder sum              : " + adder.sum());   // 500500
    }

    // ── 4. Concurrent collectors and findAny ──────────────────────────────────

    /**
     * {@code Collectors.groupingByConcurrent} is the parallel-friendly sibling of {@code groupingBy}:
     * all threads write into one shared {@link java.util.concurrent.ConcurrentMap} instead of
     * building separate maps that must be merged.
     *
     * <ul>
     *   <li>Use it with an order-insensitive downstream collector ({@code counting}, {@code summingInt}…)</li>
     *   <li>Elements inside a bucket are <em>not</em> guaranteed to be in encounter order</li>
     *   <li>{@code findAny()} may return any matching element on a parallel stream;
     *       {@code findFirst()} still returns the first one, at the cost of coordination</li>
     * </ul>
     *
     * <p>Common gotcha: {@code findAny()} on a parallel stream is <em>not</em> repeatable — only assert
     * that the result is one of the matching elements.
     */
    static void concurrentCollectorsDemo() {
        System.out.println("=== Concurrent collectors ===");

        Map<Integer, Long> counts = IntStream.rangeClosed(1, 30_000).boxed().parallel()
                .collect(Collectors.groupingByConcurrent(n -> n % 3, Collectors.counting()));
        System.out.println("counts by n % 3            : " + new TreeMap<>(counts));   // {0=10000, 1=10000, 2=10000}

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        Optional<Integer> first = numbers.parallelStream().filter(n -> n % 2 == 0).findFirst();
        System.out.println("findFirst even             : " + first);   // Optional[2] — always

        Optional<Integer> any = numbers.parallelStream().filter(n -> n % 2 == 0).findAny();
        System.out.println("findAny is even            : " + (any.isPresent() && any.get() % 2 == 0));   // true
    }
}
