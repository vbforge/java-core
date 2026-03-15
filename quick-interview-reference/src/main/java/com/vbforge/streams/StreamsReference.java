package com.vbforge.streams;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * ═══════════════════════════════════════════════════════════════
 *  STREAMS INTERVIEW REFERENCE
 *  Topics: laziness · intermediate vs terminal · common ops ·
 *          collectors · flatMap · parallel pitfalls · gotchas
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class StreamsReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. STREAM PIPELINE ANATOMY
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Source → Intermediate ops (lazy) → Terminal op (triggers execution)
     *
     *  INTERMEDIATE (lazy — return Stream):
     *  filter, map, flatMap, distinct, sorted, peek, limit, skip, mapToInt/Long/Double
     *
     *  TERMINAL (eager — triggers pipeline, consumes stream):
     *  forEach, collect, reduce, count, findFirst, findAny,
     *  anyMatch, allMatch, noneMatch, min, max, toArray, sum/average (on primitive streams)
     *
     *  KEY RULES:
     *  1. A Stream can only be consumed ONCE — second terminal op throws IllegalStateException
     *  2. Intermediate ops are NOT executed until a terminal op is called
     *  3. Short-circuit ops (findFirst, anyMatch, limit) may not process all elements
     */
    static void pipelineBasics() {
        List<String> words = List.of("banana", "apple", "cherry", "avocado", "blueberry");

        // Lazy evaluation demo — filter runs only until findFirst finds a match
        Optional<String> first = words.stream()
            .filter(w -> { System.out.println("filter: " + w); return w.startsWith("a"); })
            .findFirst();
        // Prints: "filter: banana", "filter: apple" — stops there, never processes rest

        // ⚠️ Reusing a stream throws IllegalStateException
        Stream<String> stream = words.stream().filter(w -> w.length() > 5);
        long count1 = stream.count();
        // long count2 = stream.count();  // IllegalStateException: stream already operated upon!
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. COMMON OPERATIONS CHEAT SHEET
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void commonOperations() {
        List<Integer> nums = List.of(5, 3, 8, 1, 9, 2, 7, 4, 6);

        // Collecting
        List<Integer> sorted    = nums.stream().sorted().collect(Collectors.toList());
        List<Integer> evens     = nums.stream().filter(n -> n % 2 == 0).toList();  // Java 16+
        Set<Integer>  asSet     = nums.stream().collect(Collectors.toSet());
        String        joined    = Stream.of("a","b","c").collect(Collectors.joining(", ", "[", "]")); // [a, b, c]

        // Reducing
        int   sum      = nums.stream().mapToInt(Integer::intValue).sum();
        OptionalInt max = nums.stream().mapToInt(Integer::intValue).max();
        int   product  = nums.stream().reduce(1, (a, b) -> a * b);

        // Matching
        boolean anyEven  = nums.stream().anyMatch(n -> n % 2 == 0);  // true if ANY match
        boolean allPos   = nums.stream().allMatch(n -> n > 0);        // true if ALL match
        boolean noneNeg  = nums.stream().noneMatch(n -> n < 0);       // true if NONE match

        // Grouping & partitioning
        Map<Boolean, List<Integer>> partitioned = nums.stream()
            .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        // {false=[5,3,1,9,7], true=[8,2,4,6]}

        Map<Integer, List<String>> byLength = Stream.of("a","bb","cc","ddd")
            .collect(Collectors.groupingBy(String::length));
        // {1=["a"], 2=["bb","cc"], 3=["ddd"]}

        // Counting per group
        Map<Integer, Long> countByLength = Stream.of("a","bb","cc","ddd")
            .collect(Collectors.groupingBy(String::length, Collectors.counting()));
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. flatMap — the most misunderstood operation ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  map()     → Stream<Stream<T>>   (nested — usually NOT what you want)
     *  flatMap() → Stream<T>           (flattened — merges all inner streams)
     *
     *  USE flatMap WHEN: each element maps to 0, 1, or many results
     */
    static void flatMapExamples() {
        List<List<Integer>> nested = List.of(
            List.of(1, 2, 3),
            List.of(4, 5),
            List.of(6, 7, 8, 9)
        );

        // ❌ map produces Stream<Stream<Integer>>
        Stream<Stream<Integer>> wrong = nested.stream().map(Collection::stream);

        // ✅ flatMap flattens into Stream<Integer>
        List<Integer> flat = nested.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        // [1, 2, 3, 4, 5, 6, 7, 8, 9]

        // Real-world: split sentences into words
        List<String> sentences = List.of("hello world", "java streams");
        List<String> words = sentences.stream()
            .flatMap(s -> Arrays.stream(s.split(" ")))
            .collect(Collectors.toList());
        // [hello, world, java, streams]

        // flatMap to filter+transform optionals
        List<Optional<String>> opts = List.of(Optional.of("a"), Optional.empty(), Optional.of("b"));
        List<String> present = opts.stream()
            .flatMap(Optional::stream)   // Java 9+
            .collect(Collectors.toList());
        // [a, b]
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. PARALLEL STREAMS — when to use and when NOT to ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  parallelStream() uses the common ForkJoinPool (shared across JVM).
     *  Default parallelism = number of CPU cores - 1.
     *
     *  ✅ GOOD CANDIDATES for parallel:
     *  - CPU-bound operations on large datasets (10,000+ elements)
     *  - Stateless, independent operations (no shared mutable state)
     *  - Operations with significant per-element cost
     *
     *  ❌ BAD CANDIDATES (parallel may be SLOWER):
     *  - Small collections (thread overhead dominates)
     *  - I/O-bound operations (use virtual threads instead)
     *  - Operations with shared mutable state (race conditions)
     *  - Ordered operations (findFirst, sorted) — lose parallel benefit
     *  - When order matters — parallel does not guarantee order
     */
    static void parallelPitfalls() {
        // ⚠️ Race condition with shared mutable state
        List<Integer> shared = new ArrayList<>();
        // NEVER do this:
        // IntStream.range(0, 1000).parallel().forEach(shared::add);  // ConcurrentModificationException or data loss!

        // ✅ Collect into thread-safe structure instead
        List<Integer> safe = IntStream.range(0, 1000)
            .parallel()
            .boxed()
            .collect(Collectors.toList());  // Collectors.toList() is thread-safe

        // ⚠️ forEach order is non-deterministic in parallel
        // Use forEachOrdered() if order matters (but loses parallelism benefit)
        IntStream.range(0, 5).parallel().forEachOrdered(System.out::println); // 0,1,2,3,4 ordered

        // ⚠️ Custom ForkJoinPool for parallel streams (avoid starving common pool)
        // ForkJoinPool pool = new ForkJoinPool(4);
        // pool.submit(() -> list.parallelStream().filter(...).collect(...)).get();
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 5. STREAM GOTCHAS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void streamGotchas() {
        // ⚠️ peek() is for debugging only — it's lazy, may not run in some pipelines
        long count = Stream.of(1, 2, 3)
            .peek(n -> System.out.println("peek: " + n))  // may not print without terminal op
            .count();                                       // count() may skip peek in some JVMs

        // ⚠️ sorted() on parallel stream is expensive — forces merge sort across threads

        // ⚠️ Infinite streams need limit() or a short-circuit terminal op
        Stream<Integer> infinite = Stream.iterate(0, n -> n + 1);
        List<Integer> first10 = infinite.limit(10).collect(Collectors.toList());

        // ⚠️ Optional.get() without isPresent() → NoSuchElementException
        Optional<String> opt = Optional.empty();
        // opt.get();  // NoSuchElementException!
        String val = opt.orElse("default");       // ✅ safe
        String val2 = opt.orElseGet(() -> computeDefault());  // ✅ lazy evaluation

        // ⚠️ Collectors.toUnmodifiableList() vs toList()
        // toList() (Java 16+) returns unmodifiable — same as toUnmodifiableList()
        List<Integer> unmod = Stream.of(1,2,3).toList();
        // unmod.add(4);  // UnsupportedOperationException

        // ⚠️ distinct() uses equals()/hashCode() — ensure they're properly overridden
    }

    static String computeDefault() { return "computed"; }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Reusing a consumed Stream                  → IllegalStateException
     *  ❌ Mutating shared state in parallel streams   → race conditions, data loss
     *  ❌ Using parallelStream() on small collections → slower than sequential
     *  ❌ Using peek() for logic (not debugging)      → may not execute
     *  ❌ Calling Optional.get() without check        → NoSuchElementException
     *  ❌ map() when flatMap() is needed              → Stream<Stream<T>> mess
     *  ❌ Chaining too many operations without collect → debugging nightmare
     */

    public static void main(String[] args) {
        pipelineBasics();
        commonOperations();
        flatMapExamples();
        parallelPitfalls();
        streamGotchas();
    }
}
