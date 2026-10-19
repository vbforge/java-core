package com.vbforge.streams_api_advanced;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * FlatMapDemo — annotated reference for {@code flatMap}: turning "one element → many elements".
 *
 * <p>{@code map} is one-to-one. {@code flatMap} is one-to-many: each element is replaced by a
 * <em>stream</em> of results, and all those streams are concatenated into one flat stream.
 *
 * <p>Topics:
 * <ol>
 *   <li>{@code map} vs {@code flatMap} — flattening nested collections and splitting text</li>
 *   <li>Dropping empty values — {@code Optional} and {@code null} handling in Java 8</li>
 *   <li>Combinations — nested {@code flatMap} + {@code map} (cartesian product)</li>
 *   <li>{@code flatMapToInt} — flattening into a primitive stream</li>
 * </ol>
 */
public class FlatMapDemo {

    public static void main(String[] args) {
        mapVsFlatMapDemo();
        System.out.println("---");
        emptyValuesDemo();
        System.out.println("---");
        combinationsDemo();
        System.out.println("---");
        flatMapToIntDemo();
    }

    // ── 1. map vs flatMap ─────────────────────────────────────────────────────

    /**
     * {@code flatMap(f)} requires {@code f} to return a {@link Stream}; the returned streams are
     * merged into the pipeline.
     *
     * <p>WHY this matters: whenever {@code map} gives you a {@code Stream<List<T>>} or a
     * {@code Stream<Stream<T>>} you really wanted {@code Stream<T>}. That is the signal to use
     * {@code flatMap}.
     *
     * <ul>
     *   <li>Time: O(total number of produced elements)</li>
     *   <li>Each inner stream is closed after its contents have been placed into the outer stream</li>
     *   <li>A mapper that returns {@code null} is treated as "no elements"</li>
     * </ul>
     *
     * <p>Common gotcha: {@code "".split(" ")} returns {@code [""]} (one empty string), not an empty
     * array — an empty line produces one empty "word" unless you filter it.
     */
    static void mapVsFlatMapDemo() {
        System.out.println("=== map vs flatMap ===");
        List<String> lines = Arrays.asList("to be or", "not to be");

        // map keeps one result per element -> a stream of arrays
        long arrays = lines.stream().map(line -> line.split(" ")).count();
        System.out.println("map -> arrays     : " + arrays);   // 2 (one array per line)

        // flatMap flattens them into one stream of words
        List<String> words = lines.stream()
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .collect(Collectors.toList());
        System.out.println("flatMap words     : " + words);   // [to, be, or, not, to, be]

        // Flatten a list of lists
        List<List<Integer>> nested = Arrays.asList(
                Arrays.asList(1, 2), Arrays.<Integer>asList(), Arrays.asList(3, 4, 5));
        List<Integer> flat = nested.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println("flattened         : " + flat);   // [1, 2, 3, 4, 5]

        // Gotcha: splitting an empty line yields ONE empty string
        System.out.println("\"\".split(\" \")     : " + "".split(" ").length);   // 1
        List<String> cleaned = Stream.of("a b", "")
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .filter(w -> !w.isEmpty())
                .collect(Collectors.toList());
        System.out.println("filtered words    : " + cleaned);   // [a, b]
    }

    // ── 2. Dropping empty values ──────────────────────────────────────────────

    /**
     * {@code flatMap} can also <em>remove</em> elements: map each one to either a one-element
     * stream or {@link Stream#empty()}.
     *
     * <p>WHY this matters: it combines "filter + unwrap" in one step. In Java 9+ you would write
     * {@code flatMap(Optional::stream)}; Java 8 has no {@code Optional.stream()}, so the idiom is
     * {@code filter(Optional::isPresent).map(Optional::get)} or an explicit conditional.
     *
     * <p>Common gotcha: calling {@code Optional.get()} without checking {@code isPresent()}
     * throws {@link java.util.NoSuchElementException}.
     */
    static void emptyValuesDemo() {
        System.out.println("=== Dropping empty values ===");
        List<Optional<String>> optionals = Arrays.asList(
                Optional.of("a"), Optional.<String>empty(), Optional.of("c"));

        List<String> viaFilter = optionals.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        System.out.println("filter + get      : " + viaFilter);   // [a, c]

        List<String> viaFlatMap = optionals.stream()
                .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.<String>empty())
                .collect(Collectors.toList());
        System.out.println("flatMap           : " + viaFlatMap);   // [a, c]

        // A mapper that returns null counts as an empty stream
        List<Integer> withNull = Stream.of(1, 2, 3)
                .flatMap(n -> n == 2 ? null : Stream.of(n))
                .collect(Collectors.toList());
        System.out.println("null -> empty     : " + withNull);   // [1, 3]
    }

    // ── 3. Combinations ───────────────────────────────────────────────────────

    /**
     * Nesting {@code flatMap} (outer loop) with {@code map} (inner loop) builds every combination
     * of two sources — a cartesian product.
     *
     * <p>WHY this matters: it replaces two nested {@code for} loops that add to a list.
     * The outer lambda owns the outer variable, so the inner {@code map} can still use it.
     *
     * <ul>
     *   <li>Result size is {@code outer.size() * inner.size()}</li>
     *   <li>Use {@code flatMap} for the outer level and {@code map} for the innermost level</li>
     * </ul>
     *
     * <p>Common gotcha: using {@code map} at the outer level gives a {@code Stream<Stream<String>>}
     * — nothing is flattened and the result is not what you wanted.
     */
    static void combinationsDemo() {
        System.out.println("=== Combinations ===");
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        List<String> letters = Arrays.asList("x", "y");

        List<String> pairs = numbers.stream()
                .flatMap(n -> letters.stream().map(l -> n + l))
                .collect(Collectors.toList());
        System.out.println("pairs             : " + pairs);   // [1x, 1y, 2x, 2y, 3x, 3y]
        System.out.println("pair count        : " + pairs.size());   // 6
    }

    // ── 4. flatMapToInt ───────────────────────────────────────────────────────

    /**
     * {@code flatMapToInt}, {@code flatMapToLong}, {@code flatMapToDouble} flatten into a
     * <em>primitive</em> stream.
     *
     * <p>WHY this matters: {@code String.chars()} returns an {@link IntStream}, so
     * {@code words.stream().map(String::chars)} is a {@code Stream<IntStream>} — useless.
     * {@code flatMapToInt(String::chars)} merges every word's characters into one {@code IntStream}.
     *
     * <ul>
     *   <li>No boxing; {@code sum()}, {@code count()}, {@code distinct()}, {@code sorted()} are available directly</li>
     * </ul>
     *
     * <p>Common gotcha: the elements are {@code int} code units; cast with {@code (char)} to print letters.
     */
    static void flatMapToIntDemo() {
        System.out.println("=== flatMapToInt ===");
        List<String> words = Arrays.asList("hello", "world");

        long totalChars = words.stream().flatMapToInt(String::chars).count();
        System.out.println("total chars       : " + totalChars);   // 10

        String distinctLetters = words.stream()
                .flatMapToInt(String::chars)
                .distinct()
                .sorted()
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
        System.out.println("distinct letters  : " + distinctLetters);   // dehlorw

        // Flatten int arrays
        int[][] grid = {{1, 2}, {3}, {4, 5, 6}};
        System.out.println("grid sum          : " + Arrays.stream(grid).flatMapToInt(Arrays::stream).sum());   // 21
    }
}
