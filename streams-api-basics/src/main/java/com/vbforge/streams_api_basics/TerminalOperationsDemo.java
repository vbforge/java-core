package com.vbforge.streams_api_basics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * TerminalOperationsDemo — annotated reference for the operations that <em>end</em> a
 * stream pipeline and produce a result (or a side effect).
 *
 * <p>A terminal operation is what actually starts the work. After it returns the
 * stream is consumed and cannot be reused.
 *
 * <p>Topics:
 * <ol>
 *   <li>{@code forEach} / {@code forEachOrdered} — side effects at the end of a pipeline</li>
 *   <li>{@code collect} — {@code toList}, {@code toSet}, {@code toMap}, {@code joining}, {@code toCollection}</li>
 *   <li>{@code reduce} — folding many elements into one value</li>
 *   <li>{@code anyMatch} / {@code allMatch} / {@code noneMatch} / {@code findFirst} — short-circuiting terminals</li>
 *   <li>{@code count}, {@code min}, {@code max} and primitive statistics</li>
 * </ol>
 *
 * <p>Note: {@code Stream.toList()} only exists since Java 16, so this Java 8 module
 * uses {@code collect(Collectors.toList())} everywhere.
 */
public class TerminalOperationsDemo {

    public static void main(String[] args) {
        forEachDemo();
        System.out.println("---");
        collectDemo();
        System.out.println("---");
        reduceDemo();
        System.out.println("---");
        matchAndFindDemo();
        System.out.println("---");
        aggregationDemo();
    }

    // ── 1. forEach ────────────────────────────────────────────────────────────

    /**
     * {@code forEach(consumer)} runs an action for every element and returns nothing.
     * {@code forEachOrdered(consumer)} does the same but guarantees encounter order
     * even on a parallel stream.
     *
     * <p>WHY it exists: printing, logging, sending each element somewhere — things that are
     * <em>about</em> the side effect, not about producing a value.
     *
     * <p>Common gotchas:
     * <ul>
     *   <li>Do not use {@code forEach} to build a result by mutating an outside list.
     *       That is what {@code collect} is for (and it is safe with parallel streams)</li>
     *   <li>{@code forEach} cannot {@code break} or {@code return} early — use {@code limit},
     *       {@code findFirst} or {@code anyMatch} when you need to stop</li>
     *   <li>It is a terminal operation: the stream is closed afterwards</li>
     * </ul>
     */
    static void forEachDemo() {
        System.out.println("=== forEach ===");
        List<String> words = Arrays.asList("a", "b", "c");

        System.out.print("forEach          : ");
        words.stream().map(String::toUpperCase).forEach(s -> System.out.print(s + " "));   // A B C
        System.out.println();

        // forEachOrdered — same output, but the order is guaranteed
        List<String> visited = new ArrayList<>();
        words.stream().forEachOrdered(visited::add);
        System.out.println("forEachOrdered   : " + visited);   // [a, b, c]

        // Better: let collect build the list — no outside mutation
        List<String> collected = words.stream().collect(Collectors.toList());
        System.out.println("collect instead  : " + collected);   // [a, b, c]
    }

    // ── 2. collect ────────────────────────────────────────────────────────────

    /**
     * {@code collect(collector)} gathers the elements into a container. The
     * {@link Collectors} class provides the ready-made collectors.
     *
     * <p>WHY it matters: this is how a pipeline turns back into a normal collection or string.
     *
     * <ul>
     *   <li>{@code toList()} — a {@code List} (the exact type and mutability are not specified)</li>
     *   <li>{@code toSet()} — a {@code Set}, duplicates dropped, no order promised</li>
     *   <li>{@code toMap(keyFn, valueFn)} — a {@code Map}; <b>throws</b> if two elements give the same key</li>
     *   <li>{@code toMap(keyFn, valueFn, mergeFn)} — resolves duplicate keys with a merge function</li>
     *   <li>{@code joining(delimiter, prefix, suffix)} — concatenates {@code CharSequence}s</li>
     *   <li>{@code toCollection(supplier)} — your own collection type, e.g. {@code TreeSet::new}</li>
     * </ul>
     *
     * <p>Common gotcha: {@code toMap} without a merge function fails with
     * {@link IllegalStateException} ("Duplicate key …") on the first repeated key.
     * Grouping many values under one key is a job for {@code groupingBy}
     * (see {@code streams-api-advanced}).
     */
    static void collectDemo() {
        System.out.println("=== collect ===");
        List<String> words = Arrays.asList("apple", "banana", "cherry", "apple");

        System.out.println("toList           : " + words.stream().collect(Collectors.toList()));   // [apple, banana, cherry, apple]
        System.out.println("toSet size       : " + words.stream().collect(Collectors.toSet()).size());   // 3

        // toMap — duplicate key "apple" blows up
        try {
            words.stream().collect(Collectors.toMap(Function.identity(), String::length));
        } catch (IllegalStateException e) {
            System.out.println("toMap duplicate  : IllegalStateException");
        }

        // ... unless you say how to merge. TreeMap::new gives a sorted, printable result.
        TreeMap<String, Integer> lengths = words.stream().collect(
                Collectors.toMap(Function.identity(), String::length, (a, b) -> a, TreeMap::new));
        System.out.println("toMap + merge    : " + lengths);   // {apple=5, banana=6, cherry=6}

        System.out.println("joining()        : " + words.stream().collect(Collectors.joining()));            // applebananacherryapple
        System.out.println("joining(, [ ])   : " + words.stream().collect(Collectors.joining(", ", "[", "]"))); // [apple, banana, cherry, apple]

        TreeSet<String> sortedUnique = words.stream().collect(Collectors.toCollection(TreeSet::new));
        System.out.println("toCollection     : " + sortedUnique);   // [apple, banana, cherry]
    }

    // ── 3. reduce ─────────────────────────────────────────────────────────────

    /**
     * {@code reduce} folds all elements into a single value by repeatedly combining two of them.
     *
     * <p>Three forms:
     * <ul>
     *   <li>{@code reduce(identity, accumulator)} → the value itself (never empty: an empty
     *       stream returns the identity)</li>
     *   <li>{@code reduce(accumulator)} → an {@link Optional} (empty when the stream is empty)</li>
     *   <li>{@code reduce(identity, accumulator, combiner)} → when the result type differs from the
     *       element type (e.g. {@code Stream<String>} → {@code int} total length)</li>
     * </ul>
     *
     * <p>WHY it matters: it is the general fold behind {@code sum}, {@code max}, {@code joining}…
     * Prefer the specialised versions ({@code mapToInt(...).sum()}, {@code max(...)}) when one exists.
     *
     * <p>Common gotchas:
     * <ul>
     *   <li>The identity must be a true neutral element ({@code 0} for +, {@code 1} for ×).
     *       {@code reduce(10, Integer::sum)} "works" sequentially but would add 10 once per
     *       chunk on a parallel stream</li>
     *   <li>The accumulator must be associative; subtraction is not</li>
     * </ul>
     */
    static void reduceDemo() {
        System.out.println("=== reduce ===");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        int sum = numbers.stream().reduce(0, Integer::sum);
        System.out.println("sum (identity)   : " + sum);   // 15

        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        System.out.println("max (Optional)   : " + max);   // Optional[5]

        // Empty streams: identity form returns the identity, Optional form returns empty
        System.out.println("empty + identity : " + Stream.<Integer>empty().reduce(0, Integer::sum));    // 0
        System.out.println("empty + Optional : " + Stream.<Integer>empty().reduce(Integer::max));       // Optional.empty

        System.out.println("concat           : " + Stream.of("a", "b", "c").reduce("", String::concat)); // abc

        // Three-argument form: result type (int) differs from element type (String)
        int totalLength = Stream.of("aa", "bbb", "c")
                .reduce(0, (acc, s) -> acc + s.length(), Integer::sum);
        System.out.println("total length     : " + totalLength);   // 6

        // Product: the identity is 1, not 0
        System.out.println("product          : " + numbers.stream().reduce(1, (a, b) -> a * b));   // 120
    }

    // ── 4. match and find ─────────────────────────────────────────────────────

    /**
     * {@code anyMatch}, {@code allMatch}, {@code noneMatch} answer yes/no questions;
     * {@code findFirst} / {@code findAny} return an {@link Optional}.
     *
     * <p>WHY they matter: they <em>short-circuit</em> — they stop pulling elements as soon as
     * the answer is known, which is why they also work on infinite streams.
     *
     * <ul>
     *   <li>{@code anyMatch} stops at the first match; {@code allMatch}/{@code noneMatch} stop at the first counter-example</li>
     *   <li>{@code findFirst} respects encounter order; {@code findAny} is free to return any
     *       element (it only matters on parallel streams)</li>
     * </ul>
     *
     * <p>Common gotcha — vacuous truth: on an <em>empty</em> stream {@code allMatch} and
     * {@code noneMatch} return {@code true}, {@code anyMatch} returns {@code false}.
     */
    static void matchAndFindDemo() {
        System.out.println("=== match and find ===");
        List<Integer> numbers = Arrays.asList(2, 4, 6, 8);

        System.out.println("anyMatch > 5     : " + numbers.stream().anyMatch(n -> n > 5));          // true
        System.out.println("allMatch even    : " + numbers.stream().allMatch(n -> n % 2 == 0));     // true
        System.out.println("noneMatch > 10   : " + numbers.stream().noneMatch(n -> n > 10));        // true

        // Vacuous truth on an empty stream
        System.out.println("empty allMatch   : " + Stream.<Integer>empty().allMatch(n -> false));   // true (!)
        System.out.println("empty noneMatch  : " + Stream.<Integer>empty().noneMatch(n -> true));   // true (!)
        System.out.println("empty anyMatch   : " + Stream.<Integer>empty().anyMatch(n -> true));    // false

        // Short-circuit: the predicate is not called for 8
        int[] calls = {0};
        numbers.stream().anyMatch(n -> { calls[0]++; return n > 5; });
        System.out.println("predicate calls  : " + calls[0]);   // 3  (2, 4, 6)

        // find* return Optional — the stream may have no suitable element
        System.out.println("findFirst        : " + numbers.stream().findFirst());                                    // Optional[2]
        System.out.println("findFirst > 100  : " + numbers.stream().filter(n -> n > 100).findFirst());              // Optional.empty
        System.out.println("orElse           : " + numbers.stream().filter(n -> n > 100).findFirst().orElse(-1));    // -1
    }

    // ── 5. count, min, max, primitive statistics ──────────────────────────────

    /**
     * {@code count()} returns a {@code long}; {@code min}/{@code max} take a
     * {@link Comparator} and return an {@link Optional}; the primitive streams add
     * {@code sum}, {@code average}, {@code max} and {@code summaryStatistics}.
     *
     * <p>WHY it matters: one pass over the data can answer many questions —
     * {@code summaryStatistics()} computes count, sum, min, max and average together.
     *
     * <p>Common gotchas:
     * <ul>
     *   <li>{@code count()} returns {@code long}, so assign it to a {@code long}</li>
     *   <li>On an empty {@code IntStream}: {@code sum()} is {@code 0}, but {@code max()} and
     *       {@code average()} are <em>empty</em> optionals — there is no sensible default</li>
     * </ul>
     */
    static void aggregationDemo() {
        System.out.println("=== count, min, max, statistics ===");

        List<String> words = Arrays.asList("pear", "fig", "apple", "kiwi");
        long longWords = words.stream().filter(w -> w.length() > 3).count();
        System.out.println("count > 3 chars  : " + longWords);   // 3

        System.out.println("longest          : " + words.stream().max(Comparator.comparing(String::length)));   // Optional[apple]
        System.out.println("shortest         : " + words.stream().min(Comparator.comparing(String::length)));   // Optional[fig]

        IntStream scores = IntStream.of(4, 8, 15, 16, 23, 42);
        IntSummaryStatistics stats = scores.summaryStatistics();
        System.out.println("count/sum        : " + stats.getCount() + " / " + stats.getSum());   // 6 / 108
        System.out.println("min/max          : " + stats.getMin() + " / " + stats.getMax());     // 4 / 42
        System.out.println("average          : " + stats.getAverage());                           // 18.0

        // Empty primitive stream
        System.out.println("empty sum        : " + IntStream.empty().sum());       // 0
        System.out.println("empty max        : " + IntStream.empty().max());       // OptionalInt.empty
        System.out.println("empty average    : " + IntStream.empty().average());   // OptionalDouble.empty
    }
}
