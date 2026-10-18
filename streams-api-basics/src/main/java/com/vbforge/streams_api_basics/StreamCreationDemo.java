package com.vbforge.streams_api_basics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * StreamCreationDemo — annotated reference for the different ways to <em>obtain</em> a stream.
 *
 * <p>A {@link Stream} is not a data structure. It is a one-shot <em>pipeline</em> that pulls
 * elements from a source (collection, array, generator, range…), pushes them through
 * zero or more intermediate operations, and ends in exactly one terminal operation.
 * This demo is about the very first step: where the elements come from.
 *
 * <p>Topics:
 * <ol>
 *   <li>Sources — collections, arrays, {@code Stream.of}, {@code Stream.empty}, {@code Stream.concat}</li>
 *   <li>Infinite streams — {@code Stream.iterate}, {@code Stream.generate}, {@code limit}</li>
 *   <li>Primitive streams — {@code IntStream.range}, {@code String.chars}, {@code boxed}</li>
 *   <li>Single use — a stream can be consumed only once; the source is never modified</li>
 * </ol>
 */
public class StreamCreationDemo {

    public static void main(String[] args) {
        sourcesDemo();
        System.out.println("---");
        infiniteStreamsDemo();
        System.out.println("---");
        primitiveStreamsDemo();
        System.out.println("---");
        singleUseDemo();
    }

    // ── 1. Sources ────────────────────────────────────────────────────────────

    /**
     * Almost everything you already use can produce a stream.
     *
     * <p>WHY this matters: streams replace hand-written loops with a declarative
     * description of <em>what</em> to compute. Every pipeline starts with a source, and
     * choosing the right factory method avoids boxing and surprises.
     *
     * <p>Common sources:
     * <ul>
     *   <li>{@code collection.stream()} — {@link List}, {@link Set}, any {@code Collection}</li>
     *   <li>{@code Stream.of(a, b, c)} — a handful of values</li>
     *   <li>{@code Arrays.stream(array)} / {@code Arrays.stream(array, from, to)} — an array or a slice</li>
     *   <li>{@code Stream.empty()} — a stream with no elements</li>
     *   <li>{@code Stream.concat(s1, s2)} — two streams glued together</li>
     * </ul>
     *
     * <p>Common gotcha: {@code Stream.of(int[])} gives a {@code Stream<int[]>} with
     * <em>one</em> element (the array itself). Use {@code Arrays.stream(int[])} to get an
     * {@code IntStream} of the numbers.
     *
     * <p>A {@link Map} has no {@code stream()} — stream one of its views:
     * {@code keySet()}, {@code values()} or {@code entrySet()}.
     */
    static void sourcesDemo() {
        System.out.println("=== Stream sources ===");

        // From a List — the most common source
        List<String> names = Arrays.asList("Alice", "Bob", "Carol");
        System.out.println("list.stream()    : " + names.stream().collect(Collectors.toList()));

        // From a Set — encounter order follows the set's own iteration order
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
        System.out.println("set.stream() size: " + set.stream().count());   // 3

        // From a Map — stream a view, not the map itself
        Map<String, Integer> ages = new HashMap<>();
        ages.put("Alice", 30);
        ages.put("Bob", 25);
        System.out.println("map keys sorted  : " + ages.keySet().stream().sorted().collect(Collectors.toList())); // [Alice, Bob]
        System.out.println("map entries      : " + ages.entrySet().stream().count());                            // 2

        // From values
        System.out.println("Stream.of        : " + Stream.of("x", "y", "z").collect(Collectors.toList()));

        // From an array — whole array or a [from, to) slice
        String[] letters = {"a", "b", "c", "d"};
        System.out.println("Arrays.stream    : " + Arrays.stream(letters).collect(Collectors.toList()));          // [a, b, c, d]
        System.out.println("Arrays.stream 1-3: " + Arrays.stream(letters, 1, 3).collect(Collectors.toList()));    // [b, c]

        // Empty stream — handy as a neutral value instead of null
        System.out.println("Stream.empty     : " + Stream.empty().count());   // 0

        // Concatenation
        Stream<Integer> glued = Stream.concat(Stream.of(1, 2), Stream.of(3, 4));
        System.out.println("Stream.concat    : " + glued.collect(Collectors.toList()));   // [1, 2, 3, 4]

        // GOTCHA — Stream.of(int[]) is a Stream with ONE element: the array itself
        int[] numbers = {10, 20, 30};
        Stream<int[]> wrong = Stream.of(numbers);
        System.out.println("Stream.of(int[]) : " + wrong.count());                 // 1  (!)
        System.out.println("Arrays.stream    : " + Arrays.stream(numbers).count()); // 3
    }

    // ── 2. Infinite streams ───────────────────────────────────────────────────

    /**
     * {@code Stream.iterate} and {@code Stream.generate} create <em>unbounded</em> streams.
     *
     * <p>WHY this works: streams are lazy. Nothing is produced until a terminal operation
     * asks for elements, so an "infinite" source costs nothing until you cut it with a
     * short-circuiting operation such as {@code limit(n)}.
     *
     * <ul>
     *   <li>{@code Stream.iterate(seed, f)} — seed, f(seed), f(f(seed)), … (each element depends on the previous one)</li>
     *   <li>{@code Stream.generate(supplier)} — every element comes from calling the supplier (independent values)</li>
     * </ul>
     *
     * <p>Common gotcha: forgetting {@code limit(...)}. {@code collect}, {@code sorted},
     * {@code distinct} or {@code count} on an infinite stream never returns (until
     * {@link OutOfMemoryError}). In Java 8 {@code iterate} has no stop condition —
     * the three-argument version only arrived in Java 9.
     */
    static void infiniteStreamsDemo() {
        System.out.println("=== Infinite streams ===");

        List<Integer> powers = Stream.iterate(1, n -> n * 2)
                .limit(8)
                .collect(Collectors.toList());
        System.out.println("iterate powers   : " + powers);   // [1, 2, 4, 8, 16, 32, 64, 128]

        List<String> greetings = Stream.generate(() -> "hi")
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("generate hi      : " + greetings);   // [hi, hi, hi]

        // skip + limit cut a window out of an infinite stream
        List<Integer> window = Stream.iterate(1, n -> n + 1)
                .skip(2)
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("window           : " + window);   // [3, 4, 5]

        // A short-circuiting TERMINAL operation also stops the stream — no limit needed
        boolean found = Stream.iterate(1, n -> n + 1).anyMatch(n -> n % 7 == 0);
        System.out.println("anyMatch %7      : " + found);   // true — stopped at 7
    }

    // ── 3. Primitive streams ──────────────────────────────────────────────────

    /**
     * {@link IntStream} (and {@code LongStream}, {@code DoubleStream}) are streams of
     * <em>primitives</em>.
     *
     * <p>WHY use them: {@code Stream<Integer>} boxes every number into an object.
     * {@code IntStream} stores plain {@code int}s and adds numeric helpers such as
     * {@code sum()}, {@code average()}, {@code max()} that a generic stream does not have.
     *
     * <p>Ways to create one:
     * <ul>
     *   <li>{@code IntStream.range(a, b)} — a inclusive, b <em>exclusive</em></li>
     *   <li>{@code IntStream.rangeClosed(a, b)} — both ends inclusive</li>
     *   <li>{@code IntStream.of(...)} / {@code Arrays.stream(int[])}</li>
     *   <li>{@code "text".chars()} — the UTF-16 code units as {@code int}s</li>
     * </ul>
     *
     * <p>Convert back to objects with {@code boxed()} (→ {@code Stream<Integer>}) or
     * {@code mapToObj(...)} (→ any type).
     *
     * <p>Common gotcha: {@code "abc".chars()} yields {@code 97, 98, 99}, <em>not</em>
     * {@code 'a', 'b', 'c'}. Cast with {@code (char) c} to get characters back.
     */
    static void primitiveStreamsDemo() {
        System.out.println("=== Primitive streams ===");

        System.out.println("range(0,5)       : " + IntStream.range(0, 5).boxed().collect(Collectors.toList()));        // [0, 1, 2, 3, 4]
        System.out.println("rangeClosed(1,5) : " + IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList()));  // [1, 2, 3, 4, 5]
        System.out.println("rangeClosed sum  : " + IntStream.rangeClosed(1, 5).sum());                                 // 15
        System.out.println("IntStream.of max : " + IntStream.of(3, 1, 2).max().getAsInt());                            // 3

        // chars() — ints, not chars
        System.out.println("chars() as ints  : " + "abc".chars().boxed().collect(Collectors.toList()));   // [97, 98, 99]
        System.out.println("chars() as chars : " + "abc".chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList()));                                                           // [a, b, c]

        // int[] -> List<Integer>: IntStream.boxed() bridges primitives and objects
        int[] raw = {3, 1, 2};
        List<Integer> boxed = Arrays.stream(raw).boxed().collect(Collectors.toList());
        System.out.println("boxed            : " + boxed);   // [3, 1, 2]
    }

    // ── 4. Single use ─────────────────────────────────────────────────────────

    /**
     * A stream can be traversed exactly <em>once</em>. After a terminal operation it is
     * closed; touching it again throws {@link IllegalStateException}.
     *
     * <p>WHY: a stream is a view over a one-way pipeline, not a container. It holds no
     * elements of its own, so there is nothing to "rewind". If you need the same data
     * twice, create a new stream from the source — typically through a
     * {@link Supplier}{@code <Stream<T>>}.
     *
     * <p>The other half of the contract: a stream <em>never modifies its source</em>.
     * {@code sorted()} on a list sorts a copy inside the pipeline; the list itself is untouched.
     */
    static void singleUseDemo() {
        System.out.println("=== Single use ===");

        Stream<String> stream = Stream.of("a", "b", "c");
        System.out.println("first count()    : " + stream.count());   // 3
        try {
            stream.count();   // second terminal operation on the same stream
        } catch (IllegalStateException e) {
            System.out.println("second count()   : " + e.getMessage());
            // stream has already been operated upon or closed
        }

        // Fix: build a new stream each time
        Supplier<Stream<String>> supplier = () -> Stream.of("a", "b", "c");
        System.out.println("supplier #1      : " + supplier.get().count());   // 3
        System.out.println("supplier #2      : " + supplier.get().count());   // 3

        // The source is never modified
        List<Integer> source = Arrays.asList(3, 1, 2);
        List<Integer> sorted = source.stream().sorted().collect(Collectors.toList());
        System.out.println("sorted copy      : " + sorted);   // [1, 2, 3]
        System.out.println("source untouched : " + source);   // [3, 1, 2]
    }
}
