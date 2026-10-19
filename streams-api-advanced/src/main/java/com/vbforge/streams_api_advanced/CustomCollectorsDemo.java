package com.vbforge.streams_api_advanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * CustomCollectorsDemo — annotated reference for building, composing and writing
 * {@link Collector}s.
 *
 * <p>Topics:
 * <ol>
 *   <li>Anatomy of a collector — {@code Collector.of(supplier, accumulator, combiner, finisher)}</li>
 *   <li>{@code collectingAndThen} — post-process the result of an existing collector</li>
 *   <li>Composing built-ins — {@code summarizingInt}, {@code reducing}, {@code maxBy} + {@code Optional::get}</li>
 *   <li>Combiners — why a collector must also work on a parallel stream</li>
 * </ol>
 */
public class CustomCollectorsDemo {

    /** Small value class used across the demo. */
    static class Employee {
        private final String name;
        private final String dept;
        private final int salary;

        Employee(String name, String dept, int salary) {
            this.name = name;
            this.dept = dept;
            this.salary = salary;
        }

        String getName() { return name; }
        String getDept() { return dept; }
        int getSalary()  { return salary; }

        @Override
        public String toString() {
            return name;
        }
    }

    static List<Employee> sampleEmployees() {
        return Arrays.asList(
                new Employee("Alice", "Engineering", 5000),
                new Employee("Bob",   "Engineering", 4000),
                new Employee("Carol", "Sales",       3000),
                new Employee("Dan",   "Sales",       3500),
                new Employee("Eve",   "HR",          3200));
    }

    public static void main(String[] args) {
        collectorAnatomyDemo();
        System.out.println("---");
        collectingAndThenDemo();
        System.out.println("---");
        composingBuiltInsDemo();
        System.out.println("---");
        combinerDemo();
    }

    // ── 1. Anatomy of a collector ─────────────────────────────────────────────

    /**
     * A {@link Collector} is a recipe for a mutable reduction with four parts:
     * <ul>
     *   <li><b>supplier</b> — creates an empty accumulator ({@code ArrayList::new})</li>
     *   <li><b>accumulator</b> — folds one element into the accumulator ({@code List::add})</li>
     *   <li><b>combiner</b> — merges two accumulators (needed for parallel streams)</li>
     *   <li><b>finisher</b> — converts the accumulator into the final result (optional)</li>
     * </ul>
     *
     * <p>WHY write one: when no built-in collector produces the shape you need. {@code Collector.of}
     * is the Java 8 factory; without a finisher the accumulator <em>is</em> the result
     * (characteristic {@code IDENTITY_FINISH}).
     *
     * <p>Common gotcha: the accumulator type {@code A} is hidden ({@code Collector<T, ?, R>}) —
     * callers see only the element type and the result type.
     */
    static void collectorAnatomyDemo() {
        System.out.println("=== Collector anatomy ===");

        // Equivalent of Collectors.toList(): supplier + accumulator + combiner
        Collector<String, List<String>, List<String>> toList = Collector.of(
                ArrayList::new,
                List::add,
                (left, right) -> { left.addAll(right); return left; });
        System.out.println("custom toList     : " + Arrays.asList("a", "b", "c").stream().collect(toList));   // [a, b, c]

        // Add a finisher: the accumulator (List) becomes a different result (an unmodifiable List)
        Collector<String, List<String>, List<String>> toReadOnlyList = Collector.of(
                ArrayList::new,
                List::add,
                (left, right) -> { left.addAll(right); return left; },
                Collections::unmodifiableList);
        List<String> readOnly = Arrays.asList("a", "b").stream().collect(toReadOnlyList);
        try {
            readOnly.add("c");
        } catch (UnsupportedOperationException e) {
            System.out.println("read-only list    : add blocked");
        }

        // A collector whose accumulator type differs from its result type: average of ints
        Collector<Integer, long[], Double> average = Collector.of(
                () -> new long[2],                                 // [sum, count]
                (acc, n) -> { acc[0] += n; acc[1]++; },
                (a, b) -> { a[0] += b[0]; a[1] += b[1]; return a; },
                acc -> acc[1] == 0 ? 0.0 : (double) acc[0] / acc[1]);
        System.out.println("custom average    : " + Arrays.asList(2, 4, 9).stream().collect(average));   // 5.0
    }

    // ── 2. collectingAndThen ──────────────────────────────────────────────────

    /**
     * {@code collectingAndThen(downstream, finisher)} runs a collector and then applies a function
     * to its result — a one-line custom finisher without writing a whole collector.
     *
     * <p>WHY this matters: it is the standard way to make results unmodifiable, to unwrap an
     * {@link Optional}, or to convert a {@code Long} count to an {@code int}.
     *
     * <p>Common gotcha: {@code groupingBy(..., maxBy(...))} yields {@code Map<K, Optional<T>>} even though
     * no group is ever empty. Wrap {@code maxBy} in {@code collectingAndThen(..., Optional::get)} to get {@code Map<K, T>}.
     */
    static void collectingAndThenDemo() {
        System.out.println("=== collectingAndThen ===");

        List<Integer> readOnly = IntStream.rangeClosed(1, 5).boxed()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        System.out.println("unmodifiable      : " + readOnly);   // [1, 2, 3, 4, 5]

        int evenCount = IntStream.rangeClosed(1, 10).boxed()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.collectingAndThen(Collectors.counting(), Long::intValue));
        System.out.println("count as int      : " + evenCount);   // 5

        Map<String, Employee> topPerDept = sampleEmployees().stream().collect(
                Collectors.groupingBy(Employee::getDept, TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(Employee::getSalary)),
                                Optional::get)));
        System.out.println("top per dept      : " + topPerDept);   // {Engineering=Alice, HR=Eve, Sales=Dan}
    }

    // ── 3. Composing built-ins ────────────────────────────────────────────────

    /**
     * Many "custom" needs are already covered by combining built-in collectors.
     *
     * <ul>
     *   <li>{@code summarizingInt(f)} — count, sum, min, max and average in one {@link IntSummaryStatistics}</li>
     *   <li>{@code reducing(identity, mapper, op)} — a map-then-reduce collector</li>
     *   <li>{@code toMap(key, value, merge, supplier)} — control duplicates and the map type</li>
     * </ul>
     *
     * <p>WHY this matters: you get several results in one pass over the data and, because
     * collectors nest, the same building blocks work inside {@code groupingBy}.
     *
     * <p>Common gotcha: {@code reducing} with an identity returns the identity for an empty group,
     * but the one-argument form returns an {@code Optional}.
     */
    static void composingBuiltInsDemo() {
        System.out.println("=== Composing built-ins ===");
        List<Employee> employees = sampleEmployees();

        IntSummaryStatistics stats = employees.stream()
                .collect(Collectors.summarizingInt(Employee::getSalary));
        System.out.println("count/min/max     : " + stats.getCount() + "/" + stats.getMin() + "/" + stats.getMax());   // 5/3000/5000
        System.out.println("sum/average       : " + stats.getSum() + "/" + stats.getAverage());                        // 18700/3740.0

        int totalPayroll = employees.stream()
                .collect(Collectors.reducing(0, Employee::getSalary, Integer::sum));
        System.out.println("reducing payroll  : " + totalPayroll);   // 18700

        Map<String, Integer> payrollByDept = employees.stream().collect(
                Collectors.toMap(Employee::getDept, Employee::getSalary, Integer::sum, TreeMap::new));
        System.out.println("toMap + merge     : " + payrollByDept);   // {Engineering=9000, HR=3200, Sales=6500}
    }

    // ── 4. Combiners ──────────────────────────────────────────────────────────

    /**
     * On a parallel stream the data is split, each part is accumulated into its own container, and the
     * <em>combiner</em> merges the containers back together.
     *
     * <p>WHY this matters: a collector with a wrong combiner works on a sequential stream and
     * silently returns wrong results on a parallel one — the worst kind of bug.
     *
     * <ul>
     *   <li>The accumulator must only touch its own container (no shared state)</li>
     *   <li>The combiner must merge <em>left into right order</em> for ordered results</li>
     *   <li>The built-in collectors already satisfy all of this — which is a good reason to prefer them</li>
     * </ul>
     *
     * <p>Common gotcha: a combiner that returns only {@code left} (and forgets {@code right}) is
     * never called on sequential streams, so ordinary tests do not catch it.
     */
    static void combinerDemo() {
        System.out.println("=== Combiners ===");

        // Collector that sums squares; the combiner adds the partial sums
        Collector<Integer, long[], Long> sumOfSquares = Collector.of(
                () -> new long[1],
                (acc, n) -> acc[0] += (long) n * n,
                (a, b) -> { a[0] += b[0]; return a; },
                acc -> acc[0]);

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            numbers.add(i);
        }

        long sequential = numbers.stream().collect(sumOfSquares);
        long parallel = numbers.parallelStream().collect(sumOfSquares);
        System.out.println("sequential        : " + sequential);   // 333833500
        System.out.println("parallel          : " + parallel);     // 333833500
        System.out.println("equal             : " + (sequential == parallel));   // true
    }
}
