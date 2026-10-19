package com.vbforge.streams_api_advanced;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GroupingDemo — annotated reference for {@code Collectors.groupingBy} and
 * {@code Collectors.partitioningBy}: the stream equivalent of SQL {@code GROUP BY}.
 *
 * <p>Topics:
 * <ol>
 *   <li>{@code groupingBy} basics — classifier, result map type, null keys</li>
 *   <li>Downstream collectors — {@code counting}, {@code summingInt}, {@code averagingInt},
 *       {@code mapping}, {@code maxBy}</li>
 *   <li>Multi-level grouping — a map of maps</li>
 *   <li>{@code partitioningBy} — a yes/no split that always has both keys</li>
 * </ol>
 */
public class GroupingDemo {

    /** Small value class used across the demo. {@code toString()} prints only the name. */
    static class Employee {
        private final String name;
        private final String dept;
        private final int salary;
        private final int age;

        Employee(String name, String dept, int salary, int age) {
            this.name = name;
            this.dept = dept;
            this.salary = salary;
            this.age = age;
        }

        String getName() { return name; }
        String getDept() { return dept; }
        int getSalary()  { return salary; }
        int getAge()     { return age; }

        @Override
        public String toString() {
            return name;
        }
    }

    /** Same six employees every time — keeps the demo output and the tests deterministic. */
    static List<Employee> sampleEmployees() {
        return Arrays.asList(
                new Employee("Alice", "Engineering", 5000, 30),
                new Employee("Bob",   "Engineering", 4000, 45),
                new Employee("Carol", "Sales",       3000, 28),
                new Employee("Dan",   "Sales",       3500, 52),
                new Employee("Eve",   "HR",          3200, 39),
                new Employee("Frank", "Engineering", 6000, 24));
    }

    public static void main(String[] args) {
        groupingBasicsDemo();
        System.out.println("---");
        downstreamCollectorsDemo();
        System.out.println("---");
        multiLevelGroupingDemo();
        System.out.println("---");
        partitioningDemo();
    }

    // ── 1. groupingBy basics ──────────────────────────────────────────────────

    /**
     * {@code groupingBy(classifier)} puts each element into the bucket named by the classifier's
     * result and returns a {@code Map<K, List<T>>}.
     *
     * <p>WHY this matters: it replaces the classic "{@code computeIfAbsent(...).add(...)}" loop
     * with one declarative line.
     *
     * <ul>
     *   <li>Time: O(n); buckets are created lazily, so a key exists only if at least one element mapped to it</li>
     *   <li>Within each bucket elements keep their encounter order</li>
     *   <li>The default map is a {@code HashMap} — key order is <em>not</em> guaranteed. Pass a map
     *       factory ({@code TreeMap::new}, {@code LinkedHashMap::new}) when you need an order</li>
     * </ul>
     *
     * <p>Common gotcha: the classifier must never return {@code null} —
     * {@link NullPointerException} ("element cannot be mapped to a null key").
     */
    static void groupingBasicsDemo() {
        System.out.println("=== groupingBy basics ===");
        List<Employee> employees = sampleEmployees();

        Map<String, List<Employee>> byDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDept));
        System.out.println("byDept (sorted)   : " + new TreeMap<>(byDept));
        // {Engineering=[Alice, Bob, Frank], HR=[Eve], Sales=[Carol, Dan]}
        System.out.println("unknown dept      : " + byDept.get("Legal"));   // null — no empty bucket

        // Pick the map type: LinkedHashMap keeps keys in first-appearance order
        Map<String, List<Employee>> ordered = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDept, LinkedHashMap::new, Collectors.toList()));
        System.out.println("key order         : " + ordered.keySet());   // [Engineering, Sales, HR]

        // null classifier result -> NullPointerException
        try {
            Stream.of("a", "b").collect(Collectors.groupingBy(s -> null));
        } catch (NullPointerException e) {
            System.out.println("null key          : NullPointerException");
        }
    }

    // ── 2. Downstream collectors ──────────────────────────────────────────────

    /**
     * The second argument of {@code groupingBy} is a <em>downstream collector</em> that decides what
     * each bucket contains — a count, a sum, a list of names, the maximum…
     *
     * <p>WHY this matters: it is how you compute per-group aggregates in one pass,
     * without building the intermediate lists first.
     *
     * <ul>
     *   <li>{@code counting()} → {@code Long}</li>
     *   <li>{@code summingInt(f)} → {@code Integer}; {@code averagingInt(f)} → {@code Double}</li>
     *   <li>{@code mapping(f, downstream)} → transform each element before collecting it</li>
     *   <li>{@code maxBy(comparator)} → {@code Optional<T>}</li>
     * </ul>
     *
     * <p>Common gotchas: {@code counting()} gives {@code Long}, not {@code Integer};
     * and {@code maxBy} gives an {@code Optional} even though a group can never be empty.
     */
    static void downstreamCollectorsDemo() {
        System.out.println("=== Downstream collectors ===");
        List<Employee> employees = sampleEmployees();

        Map<String, Long> headcount = employees.stream().collect(
                Collectors.groupingBy(Employee::getDept, TreeMap::new, Collectors.counting()));
        System.out.println("headcount         : " + headcount);   // {Engineering=3, HR=1, Sales=2}

        Map<String, Integer> payroll = employees.stream().collect(
                Collectors.groupingBy(Employee::getDept, TreeMap::new, Collectors.summingInt(Employee::getSalary)));
        System.out.println("payroll           : " + payroll);   // {Engineering=15000, HR=3200, Sales=6500}

        Map<String, Double> avgSalary = employees.stream().collect(
                Collectors.groupingBy(Employee::getDept, TreeMap::new, Collectors.averagingInt(Employee::getSalary)));
        System.out.println("average salary    : " + avgSalary);   // {Engineering=5000.0, HR=3200.0, Sales=3250.0}

        Map<String, List<String>> names = employees.stream().collect(
                Collectors.groupingBy(Employee::getDept, TreeMap::new,
                        Collectors.mapping(Employee::getName, Collectors.toList())));
        System.out.println("names             : " + names);   // {Engineering=[Alice, Bob, Frank], HR=[Eve], Sales=[Carol, Dan]}

        Map<String, String> nameLine = employees.stream().collect(
                Collectors.groupingBy(Employee::getDept, TreeMap::new,
                        Collectors.mapping(Employee::getName, Collectors.joining("/"))));
        System.out.println("joined names      : " + nameLine);   // {Engineering=Alice/Bob/Frank, HR=Eve, Sales=Carol/Dan}

        Map<String, Optional<Employee>> top = employees.stream().collect(
                Collectors.groupingBy(Employee::getDept, TreeMap::new,
                        Collectors.maxBy(Comparator.comparingInt(Employee::getSalary))));
        System.out.println("top earner        : " + top);   // {Engineering=Optional[Frank], HR=Optional[Eve], Sales=Optional[Dan]}
    }

    // ── 3. Multi-level grouping ───────────────────────────────────────────────

    /**
     * A downstream collector may itself be a {@code groupingBy}, giving a map of maps.
     *
     * <p>WHY this matters: it is a two-column {@code GROUP BY dept, band}.
     *
     * <ul>
     *   <li>Result type: {@code Map<K1, Map<K2, V>>}</li>
     *   <li>Each level can have its own map factory and downstream collector</li>
     * </ul>
     *
     * <p>Common gotcha: nested generics get long — declare the target type
     * ({@code Map<String, Map<String, List<String>>>}) so the compiler can infer the rest.
     */
    static void multiLevelGroupingDemo() {
        System.out.println("=== Multi-level grouping ===");

        Map<String, Map<String, List<String>>> byDeptAndBand = sampleEmployees().stream().collect(
                Collectors.groupingBy(Employee::getDept, TreeMap::new,
                        Collectors.groupingBy(e -> e.getAge() < 35 ? "junior" : "senior", TreeMap::new,
                                Collectors.mapping(Employee::getName, Collectors.toList()))));

        System.out.println("Engineering       : " + byDeptAndBand.get("Engineering"));   // {junior=[Alice, Frank], senior=[Bob]}
        System.out.println("HR                : " + byDeptAndBand.get("HR"));            // {senior=[Eve]}
        System.out.println("Sales             : " + byDeptAndBand.get("Sales"));         // {junior=[Carol], senior=[Dan]}
    }

    // ── 4. partitioningBy ─────────────────────────────────────────────────────

    /**
     * {@code partitioningBy(predicate)} splits the stream in two: a {@code Map<Boolean, List<T>>}
     * with the matches under {@code true} and the rest under {@code false}.
     *
     * <p>WHY use it over {@code groupingBy(predicate)}: the result <em>always</em> contains both
     * keys, even when one side is empty, so {@code map.get(true)} is never {@code null}.
     *
     * <ul>
     *   <li>Accepts a downstream collector too, exactly like {@code groupingBy}</li>
     *   <li>Only a predicate is allowed — for more than two buckets use {@code groupingBy}</li>
     * </ul>
     *
     * <p>Common gotcha: {@code groupingBy(predicate)} on an empty stream (or when nothing matches)
     * produces no key for the missing side.
     */
    static void partitioningDemo() {
        System.out.println("=== partitioningBy ===");
        List<Employee> employees = sampleEmployees();

        Map<Boolean, List<String>> wellPaid = employees.stream().collect(
                Collectors.partitioningBy(e -> e.getSalary() >= 4000,
                        Collectors.mapping(Employee::getName, Collectors.toList())));
        System.out.println("salary >= 4000    : " + wellPaid);   // {false=[Carol, Dan, Eve], true=[Alice, Bob, Frank]}

        Map<Boolean, Long> counts = employees.stream().collect(
                Collectors.partitioningBy(e -> e.getAge() >= 40, Collectors.counting()));
        System.out.println("age >= 40 counts  : " + counts);   // {false=4, true=2}

        // Both keys exist even for an empty stream
        Map<Boolean, List<Integer>> empty = Stream.<Integer>empty()
                .collect(Collectors.partitioningBy(n -> n > 0));
        System.out.println("partition empty   : " + empty);   // {false=[], true=[]}

        Map<Boolean, List<Integer>> grouped = Stream.<Integer>empty()
                .collect(Collectors.groupingBy(n -> n > 0));
        System.out.println("grouping empty    : " + grouped);   // {}
    }
}
