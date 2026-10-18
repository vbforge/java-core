package com.vbforge.streams_api_basics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * IntermediateOperationsDemo — annotated reference for the operations that sit
 * <em>between</em> the source and the terminal operation of a stream pipeline.
 *
 * <p>An intermediate operation always returns another stream and does <b>no work by itself</b>.
 * It only describes a step; the work starts when a terminal operation pulls elements through.
 *
 * <p>Topics:
 * <ol>
 *   <li>{@code filter} and {@code map} — keep some elements, transform each one</li>
 *   <li>{@code mapToInt} and {@code boxed} — switching between object and primitive streams</li>
 *   <li>{@code sorted} — natural order, {@link Comparator}s, stability</li>
 *   <li>{@code distinct}, {@code skip}, {@code limit} — trimming a stream</li>
 *   <li>Laziness — nothing runs without a terminal operation; short-circuiting; {@code peek}</li>
 * </ol>
 */
public class IntermediateOperationsDemo {

    /** Small immutable-ish value class used across the demo. Deliberately has no equals/hashCode. */
    static class Person {
        private final String name;
        private final int age;
        private final String city;

        Person(String name, int age, String city) {
            this.name = name;
            this.age = age;
            this.city = city;
        }

        String getName() { return name; }
        int getAge()     { return age; }
        String getCity() { return city; }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    /** Same five people every time — keeps the demo output and the tests deterministic. */
    static List<Person> samplePeople() {
        return Arrays.asList(
                new Person("Alice", 30, "Kyiv"),
                new Person("Bob",   17, "Lviv"),
                new Person("Carol", 45, "Kyiv"),
                new Person("Dan",   22, "Odesa"),
                new Person("Eve",   17, "Kyiv"));
    }

    public static void main(String[] args) {
        filterAndMapDemo();
        System.out.println("---");
        primitiveMappingDemo();
        System.out.println("---");
        sortedDemo();
        System.out.println("---");
        distinctSkipLimitDemo();
        System.out.println("---");
        lazinessDemo();
    }

    // ── 1. filter and map ─────────────────────────────────────────────────────

    /**
     * {@code filter(predicate)} keeps the elements for which the predicate is true.
     * {@code map(function)} replaces every element with the function's result.
     *
     * <p>WHY this matters: together they replace the classic "loop + if + add to a new list"
     * pattern with two named steps that read top to bottom.
     *
     * <ul>
     *   <li>{@code filter} never changes the element type; {@code map} usually does
     *       ({@code Stream<Person>} → {@code Stream<String>})</li>
     *   <li>Both are O(n) and stateless — each element is handled independently</li>
     * </ul>
     *
     * <p>Common gotcha: the order matters for performance. Put {@code filter} <em>before</em>
     * an expensive {@code map} so you transform fewer elements.
     */
    static void filterAndMapDemo() {
        System.out.println("=== filter and map ===");
        List<Person> people = samplePeople();

        List<String> adultNames = people.stream()
                .filter(p -> p.getAge() >= 18)
                .map(Person::getName)
                .collect(Collectors.toList());
        System.out.println("adult names      : " + adultNames);   // [Alice, Carol, Dan]

        // Two filters in a row behave like one filter with &&
        List<String> kyivAdults = people.stream()
                .filter(p -> p.getCity().equals("Kyiv"))
                .filter(p -> p.getAge() >= 18)
                .map(Person::getName)
                .collect(Collectors.toList());
        System.out.println("Kyiv adults      : " + kyivAdults);   // [Alice, Carol]

        // map can change the element type more than once
        List<Integer> nameLengths = people.stream()
                .map(Person::getName)
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println("name lengths     : " + nameLengths);   // [5, 3, 5, 3, 3]

        System.out.println("source untouched : " + people);        // all five, original order
    }

    // ── 2. mapToInt and boxed ─────────────────────────────────────────────────

    /**
     * {@code mapToInt} turns a {@code Stream<T>} into an {@link java.util.stream.IntStream};
     * {@code boxed()} goes back.
     *
     * <p>WHY this matters: {@code map(Person::getAge)} produces a {@code Stream<Integer>} with
     * a boxed object per element and no {@code sum()}. {@code mapToInt(Person::getAge)}
     * keeps plain {@code int}s and unlocks {@code sum()}, {@code average()}, {@code max()}…
     *
     * <p>Common gotcha: {@code average()} returns an {@link OptionalDouble}, not a
     * {@code double} — the stream might be empty.
     */
    static void primitiveMappingDemo() {
        System.out.println("=== mapToInt and boxed ===");
        List<Person> people = samplePeople();

        int totalAge = people.stream().mapToInt(Person::getAge).sum();
        System.out.println("total age        : " + totalAge);   // 131

        OptionalDouble average = people.stream().mapToInt(Person::getAge).average();
        System.out.println("average age      : " + average.getAsDouble());   // 26.2

        List<Integer> ages = people.stream()
                .mapToInt(Person::getAge)
                .boxed()
                .collect(Collectors.toList());
        System.out.println("boxed ages       : " + ages);   // [30, 17, 45, 22, 17]
    }

    // ── 3. sorted ─────────────────────────────────────────────────────────────

    /**
     * {@code sorted()} orders elements by their natural order ({@link Comparable});
     * {@code sorted(comparator)} orders them any way you describe.
     *
     * <p>WHY this matters: you get a sorted <em>result</em> without touching the source collection.
     *
     * <ul>
     *   <li>Time: O(n log n); it must buffer <em>all</em> elements before emitting the first one</li>
     *   <li>For ordered streams the sort is <b>stable</b> — equal elements keep their original order</li>
     *   <li>Build comparators with {@code Comparator.comparing(...)}, {@code reversed()},
     *       {@code thenComparing(...)}</li>
     * </ul>
     *
     * <p>Common gotcha: {@code sorted()} on elements that are not {@code Comparable} compiles fine
     * and fails at runtime with {@link ClassCastException} when the terminal operation runs.
     */
    static void sortedDemo() {
        System.out.println("=== sorted ===");
        List<String> words = Arrays.asList("pear", "fig", "apple", "kiwi", "plum");

        System.out.println("natural          : " + words.stream().sorted().collect(Collectors.toList()));
        // [apple, fig, kiwi, pear, plum]

        System.out.println("by length        : " + words.stream()
                .sorted(Comparator.comparing(String::length))
                .collect(Collectors.toList()));
        // [fig, pear, kiwi, plum, apple]   — pear, kiwi, plum keep their input order (stable)

        System.out.println("length desc      : " + words.stream()
                .sorted(Comparator.comparing(String::length).reversed())
                .collect(Collectors.toList()));
        // [apple, pear, kiwi, plum, fig]

        System.out.println("length then A-Z  : " + words.stream()
                .sorted(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()))
                .collect(Collectors.toList()));
        // [fig, kiwi, pear, plum, apple]

        List<String> byAgeThenName = samplePeople().stream()
                .sorted(Comparator.comparingInt(Person::getAge).thenComparing(Person::getName))
                .map(Person::getName)
                .collect(Collectors.toList());
        System.out.println("age then name    : " + byAgeThenName);   // [Bob, Eve, Dan, Alice, Carol]

        // Person is not Comparable -> runtime failure, not a compile error
        try {
            samplePeople().stream().sorted().collect(Collectors.toList());
        } catch (ClassCastException e) {
            System.out.println("sorted() Person  : ClassCastException");
        }
    }

    // ── 4. distinct, skip, limit ──────────────────────────────────────────────

    /**
     * {@code distinct()} drops duplicates, {@code skip(n)} drops the first n elements,
     * {@code limit(n)} keeps at most the first n elements.
     *
     * <p>WHY this matters: {@code skip} + {@code limit} is the standard way to implement
     * pagination, and {@code limit} is what makes infinite streams usable.
     *
     * <ul>
     *   <li>{@code distinct} uses {@code equals}/{@code hashCode}; for ordered streams the
     *       <em>first</em> occurrence is kept</li>
     *   <li>{@code skip} and {@code limit} take a {@code long}</li>
     * </ul>
     *
     * <p>Common gotcha: {@code distinct()} on objects whose class does not override
     * {@code equals}/{@code hashCode} removes nothing — two "identical" objects are
     * different references.
     */
    static void distinctSkipLimitDemo() {
        System.out.println("=== distinct, skip, limit ===");

        System.out.println("distinct         : " + Stream.of(1, 2, 2, 3, 1)
                .distinct().collect(Collectors.toList()));   // [1, 2, 3]

        long uniquePeople = Stream.of(new Person("Zed", 1, "X"), new Person("Zed", 1, "X"))
                .distinct().count();
        System.out.println("distinct Person  : " + uniquePeople);   // 2 — no equals() override

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        System.out.println("skip 7           : " + numbers.stream().skip(7).collect(Collectors.toList()));   // [8, 9, 10]
        System.out.println("limit 3          : " + numbers.stream().limit(3).collect(Collectors.toList()));  // [1, 2, 3]

        // Pagination: page index 1 (zero-based), page size 3
        int pageIndex = 1;
        int pageSize = 3;
        List<Integer> page = numbers.stream()
                .skip((long) pageIndex * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
        System.out.println("page 1 of size 3 : " + page);   // [4, 5, 6]
    }

    // ── 5. Laziness ───────────────────────────────────────────────────────────

    /**
     * Intermediate operations are <b>lazy</b>: building a pipeline runs nothing.
     * Work happens only when a terminal operation pulls elements, and then each element
     * travels through the <em>whole</em> pipeline before the next one starts
     * (vertical processing, not "all filters, then all maps").
     *
     * <p>WHY this matters: laziness is what makes short-circuiting possible —
     * {@code limit(2)} stops pulling after two elements, so the earlier steps never see
     * the rest. It is also why a pipeline with no terminal operation silently does nothing.
     *
     * <p>Exception: <em>stateful</em> operations such as {@code sorted()} must see every
     * element before they can emit one, so they break the one-at-a-time flow.
     *
     * <p>Common gotcha: {@code peek} is meant for debugging. Putting real logic (or
     * side effects the result depends on) in {@code peek}/{@code map} is fragile, because
     * the stream may skip or reorder the calls.
     */
    static void lazinessDemo() {
        System.out.println("=== laziness ===");

        // (a) Nothing happens until a terminal operation
        List<String> log = new ArrayList<>();
        Stream<Integer> pipeline = Stream.of(1, 2, 3, 4)
                .filter(n -> { log.add("filter " + n); return n % 2 == 0; })
                .map(n -> { log.add("map " + n); return n * 10; });
        System.out.println("before terminal  : " + log);   // []

        List<Integer> result = pipeline.collect(Collectors.toList());
        System.out.println("after terminal   : " + log);
        // [filter 1, filter 2, map 2, filter 3, filter 4, map 4]   <- element by element
        System.out.println("result           : " + result);   // [20, 40]

        // (b) limit short-circuits: elements after the 2nd are never even pulled
        List<String> seen = new ArrayList<>();
        Stream.of(5, 6, 7, 8, 9)
                .peek(n -> seen.add("seen " + n))
                .limit(2)
                .collect(Collectors.toList());
        System.out.println("limit(2) saw     : " + seen);   // [seen 5, seen 6]

        // (c) sorted() is a barrier: it consumes EVERYTHING upstream first
        List<String> barrier = new ArrayList<>();
        List<Integer> smallest = Stream.of(3, 1, 2)
                .peek(n -> barrier.add("seen " + n))
                .sorted()
                .limit(1)
                .collect(Collectors.toList());
        System.out.println("sorted saw       : " + barrier);    // [seen 3, seen 1, seen 2]
        System.out.println("smallest         : " + smallest);   // [1]
    }
}
