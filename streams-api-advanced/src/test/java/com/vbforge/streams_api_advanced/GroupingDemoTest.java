package com.vbforge.streams_api_advanced;

import com.vbforge.streams_api_advanced.GroupingDemo.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * GroupingDemoTest — verifies every concept shown in {@link GroupingDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * {@code groupingBy} and {@code partitioningBy}.
 */
@DisplayName("GroupingDemo — API contracts")
class GroupingDemoTest {

    private static List<String> names(List<Employee> employees) {
        return employees.stream().map(Employee::getName).collect(Collectors.toList());
    }

    // ── 1. groupingBy basics ──────────────────────────────────────────────────

    @Nested
    @DisplayName("1. groupingBy basics")
    class GroupingBasicsTest {

        @Test
        @DisplayName("groupingBy(classifier) builds one list per key")
        void oneListPerKey() {
            Map<String, List<Employee>> byDept = GroupingDemo.sampleEmployees().stream()
                    .collect(Collectors.groupingBy(Employee::getDept));

            assertThat(byDept).hasSize(3);
            assertThat(names(byDept.get("Engineering"))).containsExactly("Alice", "Bob", "Frank");
            assertThat(names(byDept.get("HR"))).containsExactly("Eve");
            assertThat(names(byDept.get("Sales"))).containsExactly("Carol", "Dan");
        }

        @Test
        @DisplayName("a key exists only if at least one element mapped to it")
        void noEmptyBuckets() {
            Map<String, List<Employee>> byDept = GroupingDemo.sampleEmployees().stream()
                    .collect(Collectors.groupingBy(Employee::getDept));
            assertThat(byDept.get("Legal")).isNull();
            assertThat(byDept.containsKey("Legal")).isFalse();
        }

        @Test
        @DisplayName("elements keep encounter order inside each bucket")
        void encounterOrderInBucket() {
            Map<Boolean, List<Integer>> grouped = Stream.of(5, 2, 4, 1, 6)
                    .collect(Collectors.groupingBy(n -> n % 2 == 0));
            assertThat(grouped.get(true)).containsExactly(2, 4, 6);
            assertThat(grouped.get(false)).containsExactly(5, 1);
        }

        @Test
        @DisplayName("a map factory chooses the map type: LinkedHashMap keeps first-appearance key order")
        void linkedHashMapKeyOrder() {
            Map<String, List<Employee>> ordered = GroupingDemo.sampleEmployees().stream()
                    .collect(Collectors.groupingBy(Employee::getDept, LinkedHashMap::new, Collectors.toList()));
            assertThat(ordered.keySet()).containsExactly("Engineering", "Sales", "HR");
        }

        @Test
        @DisplayName("a map factory chooses the map type: TreeMap sorts the keys")
        void treeMapSortsKeys() {
            Map<String, List<Employee>> sorted = GroupingDemo.sampleEmployees().stream()
                    .collect(Collectors.groupingBy(Employee::getDept, TreeMap::new, Collectors.toList()));
            assertThat(sorted.keySet()).containsExactly("Engineering", "HR", "Sales");
        }

        @Test
        @DisplayName("a classifier that returns null throws NullPointerException")
        void nullKeyThrows() {
            assertThatThrownBy(() -> Stream.of("a", "b").collect(Collectors.groupingBy(s -> null)))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    // ── 2. Downstream collectors ──────────────────────────────────────────────

    @Nested
    @DisplayName("2. Downstream collectors")
    class DownstreamCollectorsTest {

        @Test
        @DisplayName("counting() returns Long values")
        void counting() {
            Map<String, Long> headcount = GroupingDemo.sampleEmployees().stream().collect(
                    Collectors.groupingBy(Employee::getDept, Collectors.counting()));
            assertThat(headcount).containsEntry("Engineering", 3L)
                                 .containsEntry("HR", 1L)
                                 .containsEntry("Sales", 2L);
        }

        @Test
        @DisplayName("summingInt() adds the mapped ints per group")
        void summingInt() {
            Map<String, Integer> payroll = GroupingDemo.sampleEmployees().stream().collect(
                    Collectors.groupingBy(Employee::getDept, Collectors.summingInt(Employee::getSalary)));
            assertThat(payroll).containsEntry("Engineering", 15000)
                               .containsEntry("HR", 3200)
                               .containsEntry("Sales", 6500);
        }

        @Test
        @DisplayName("averagingInt() returns Double values")
        void averagingInt() {
            Map<String, Double> average = GroupingDemo.sampleEmployees().stream().collect(
                    Collectors.groupingBy(Employee::getDept, Collectors.averagingInt(Employee::getSalary)));
            assertThat(average).containsEntry("Engineering", 5000.0)
                               .containsEntry("Sales", 3250.0);
        }

        @Test
        @DisplayName("mapping(f, toList()) collects a property instead of the whole element")
        void mappingToList() {
            Map<String, List<String>> names = GroupingDemo.sampleEmployees().stream().collect(
                    Collectors.groupingBy(Employee::getDept,
                            Collectors.mapping(Employee::getName, Collectors.toList())));
            assertThat(names.get("Engineering")).containsExactly("Alice", "Bob", "Frank");
        }

        @Test
        @DisplayName("mapping(f, joining(...)) produces one string per group")
        void mappingToJoining() {
            Map<String, String> joined = GroupingDemo.sampleEmployees().stream().collect(
                    Collectors.groupingBy(Employee::getDept,
                            Collectors.mapping(Employee::getName, Collectors.joining("/"))));
            assertThat(joined).containsEntry("Engineering", "Alice/Bob/Frank")
                              .containsEntry("Sales", "Carol/Dan");
        }

        @Test
        @DisplayName("maxBy() yields an Optional per group")
        void maxByIsOptional() {
            Map<String, Optional<Employee>> top = GroupingDemo.sampleEmployees().stream().collect(
                    Collectors.groupingBy(Employee::getDept,
                            Collectors.maxBy(Comparator.comparingInt(Employee::getSalary))));
            assertThat(top.get("Engineering").get().getName()).isEqualTo("Frank");
            assertThat(top.get("Sales").get().getName()).isEqualTo("Dan");
            assertThat(top.get("HR").get().getName()).isEqualTo("Eve");
        }
    }

    // ── 3. Multi-level grouping ───────────────────────────────────────────────

    @Nested
    @DisplayName("3. Multi-level grouping")
    class MultiLevelGroupingTest {

        private Map<String, Map<String, List<String>>> byDeptAndBand() {
            return GroupingDemo.sampleEmployees().stream().collect(
                    Collectors.groupingBy(Employee::getDept,
                            Collectors.groupingBy(e -> e.getAge() < 35 ? "junior" : "senior",
                                    Collectors.mapping(Employee::getName, Collectors.toList()))));
        }

        @Test
        @DisplayName("a downstream groupingBy produces a map of maps")
        void mapOfMaps() {
            Map<String, Map<String, List<String>>> result = byDeptAndBand();
            assertThat(result).hasSize(3);
            assertThat(result.get("Engineering").get("junior")).containsExactly("Alice", "Frank");
            assertThat(result.get("Engineering").get("senior")).containsExactly("Bob");
        }

        @Test
        @DisplayName("inner maps contain only the keys that occur in that group")
        void innerKeysOnlyWhenPresent() {
            Map<String, Map<String, List<String>>> result = byDeptAndBand();
            assertThat(result.get("HR")).hasSize(1);
            assertThat(result.get("HR").get("senior")).containsExactly("Eve");
            assertThat(result.get("HR").get("junior")).isNull();
        }

        @Test
        @DisplayName("every employee ends up in exactly one leaf list")
        void everyoneCountedOnce() {
            int total = 0;
            for (Map<String, List<String>> inner : byDeptAndBand().values()) {
                for (List<String> leaf : inner.values()) {
                    total += leaf.size();
                }
            }
            assertThat(total).isEqualTo(6);
        }
    }

    // ── 4. partitioningBy ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. partitioningBy")
    class PartitioningTest {

        @Test
        @DisplayName("partitioningBy splits into true and false lists")
        void splitsInTwo() {
            Map<Boolean, List<Employee>> wellPaid = GroupingDemo.sampleEmployees().stream()
                    .collect(Collectors.partitioningBy(e -> e.getSalary() >= 4000));
            assertThat(names(wellPaid.get(true))).containsExactly("Alice", "Bob", "Frank");
            assertThat(names(wellPaid.get(false))).containsExactly("Carol", "Dan", "Eve");
        }

        @Test
        @DisplayName("partitioningBy accepts a downstream collector")
        void withDownstream() {
            Map<Boolean, Long> counts = GroupingDemo.sampleEmployees().stream()
                    .collect(Collectors.partitioningBy(e -> e.getAge() >= 40, Collectors.counting()));
            assertThat(counts).containsEntry(true, 2L)
                              .containsEntry(false, 4L);
        }

        @Test
        @DisplayName("partitioningBy always has both keys, even for an empty stream")
        void bothKeysAlways() {
            Map<Boolean, List<Integer>> empty = Stream.<Integer>empty()
                    .collect(Collectors.partitioningBy(n -> n > 0));
            assertThat(empty).hasSize(2);
            assertThat(empty.get(true)).isEmpty();
            assertThat(empty.get(false)).isEmpty();
        }

        @Test
        @DisplayName("groupingBy on an empty stream gives an empty map — no keys at all")
        void groupingByHasNoKeys() {
            Map<Boolean, List<Integer>> grouped = Stream.<Integer>empty()
                    .collect(Collectors.groupingBy(n -> n > 0));
            assertThat(grouped).isEmpty();
        }

        @Test
        @DisplayName("when nothing matches, partitioningBy still has the true key (empty list)")
        void nothingMatches() {
            Map<Boolean, List<Integer>> result = Stream.of(1, 2, 3)
                    .collect(Collectors.partitioningBy(n -> n > 100));
            assertThat(result.get(true)).isEmpty();
            assertThat(result.get(false)).containsExactly(1, 2, 3);
        }
    }
}
