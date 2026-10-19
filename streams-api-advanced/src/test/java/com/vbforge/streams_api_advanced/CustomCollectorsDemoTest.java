package com.vbforge.streams_api_advanced;

import com.vbforge.streams_api_advanced.CustomCollectorsDemo.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CustomCollectorsDemoTest — verifies every concept shown in {@link CustomCollectorsDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how collectors are built, composed and combined.
 */
@DisplayName("CustomCollectorsDemo — API contracts")
class CustomCollectorsDemoTest {

    // ── 1. Anatomy of a collector ─────────────────────────────────────────────

    @Nested
    @DisplayName("1. Collector anatomy")
    class AnatomyTest {

        private Collector<String, List<String>, List<String>> newToListCollector() {
            return Collector.of(
                    ArrayList::new,
                    List::add,
                    (left, right) -> { left.addAll(right); return left; });
        }

        @Test
        @DisplayName("supplier + accumulator + combiner is enough to build toList()")
        void customToList() {
            assertThat(Stream.of("a", "b", "c").collect(newToListCollector()))
                    .containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("a finisher changes the result type: here a read-only list")
        void finisherMakesListReadOnly() {
            Collector<String, List<String>, List<String>> readOnly = Collector.of(
                    ArrayList::new,
                    List::add,
                    (left, right) -> { left.addAll(right); return left; },
                    Collections::unmodifiableList);

            List<String> result = Stream.of("a", "b").collect(readOnly);

            assertThat(result).containsExactly("a", "b");
            assertThatThrownBy(() -> result.add("c")).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("the accumulator type may differ from the result type (long[] → Double)")
        void accumulatorDiffersFromResult() {
            Collector<Integer, long[], Double> average = Collector.of(
                    () -> new long[2],
                    (acc, n) -> { acc[0] += n; acc[1]++; },
                    (a, b) -> { a[0] += b[0]; a[1] += b[1]; return a; },
                    acc -> acc[1] == 0 ? 0.0 : (double) acc[0] / acc[1]);

            assertThat(Stream.of(2, 4, 9).collect(average)).isEqualTo(5.0);
            assertThat(Stream.<Integer>empty().collect(average)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("the supplier is called to create a fresh accumulator per collect() call")
        void freshAccumulatorPerCollect() {
            Collector<String, List<String>, List<String>> collector = newToListCollector();
            List<String> first = Stream.of("a").collect(collector);
            List<String> second = Stream.of("b").collect(collector);

            assertThat(first).containsExactly("a");
            assertThat(second).containsExactly("b");
        }
    }

    // ── 2. collectingAndThen ──────────────────────────────────────────────────

    @Nested
    @DisplayName("2. collectingAndThen")
    class CollectingAndThenTest {

        @Test
        @DisplayName("collectingAndThen(toList(), unmodifiableList) returns a read-only list")
        void unmodifiableResult() {
            List<Integer> result = IntStream.rangeClosed(1, 5).boxed().collect(
                    Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

            assertThat(result).containsExactly(1, 2, 3, 4, 5);
            assertThatThrownBy(() -> result.add(6)).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("collectingAndThen(counting(), Long::intValue) turns the Long count into an int")
        void countAsInt() {
            int evenCount = IntStream.rangeClosed(1, 10).boxed()
                    .filter(n -> n % 2 == 0)
                    .collect(Collectors.collectingAndThen(Collectors.counting(), Long::intValue));
            assertThat(evenCount).isEqualTo(5);
        }

        @Test
        @DisplayName("collectingAndThen(maxBy(...), Optional::get) gives Map<K, T> instead of Map<K, Optional<T>>")
        void unwrapOptionalInsideGrouping() {
            Map<String, Employee> top = CustomCollectorsDemo.sampleEmployees().stream().collect(
                    Collectors.groupingBy(Employee::getDept, TreeMap::new,
                            Collectors.collectingAndThen(
                                    Collectors.maxBy(Comparator.comparingInt(Employee::getSalary)),
                                    Optional::get)));

            assertThat(top.get("Engineering").getName()).isEqualTo("Alice");
            assertThat(top.get("Sales").getName()).isEqualTo("Dan");
            assertThat(top.get("HR").getName()).isEqualTo("Eve");
        }

        @Test
        @DisplayName("without the wrapper, maxBy leaves an Optional in every value")
        void maxByAloneGivesOptional() {
            Map<String, Optional<Employee>> top = CustomCollectorsDemo.sampleEmployees().stream().collect(
                    Collectors.groupingBy(Employee::getDept,
                            Collectors.maxBy(Comparator.comparingInt(Employee::getSalary))));
            assertThat(top.get("Engineering").isPresent()).isTrue();
        }
    }

    // ── 3. Composing built-ins ────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Composing built-ins")
    class ComposingBuiltInsTest {

        @Test
        @DisplayName("summarizingInt() computes count, sum, min, max and average in one pass")
        void summarizingInt() {
            IntSummaryStatistics stats = CustomCollectorsDemo.sampleEmployees().stream()
                    .collect(Collectors.summarizingInt(Employee::getSalary));

            assertThat(stats.getCount()).isEqualTo(5L);
            assertThat(stats.getSum()).isEqualTo(18700L);
            assertThat(stats.getMin()).isEqualTo(3000);
            assertThat(stats.getMax()).isEqualTo(5000);
            assertThat(stats.getAverage()).isEqualTo(3740.0);
        }

        @Test
        @DisplayName("reducing(identity, mapper, op) is a map-then-reduce collector")
        void reducingWithIdentity() {
            int total = CustomCollectorsDemo.sampleEmployees().stream()
                    .collect(Collectors.reducing(0, Employee::getSalary, Integer::sum));
            assertThat(total).isEqualTo(18700);
        }

        @Test
        @DisplayName("reducing with an identity returns the identity for an empty stream")
        void reducingEmptyReturnsIdentity() {
            int total = Stream.<Employee>empty()
                    .collect(Collectors.reducing(0, Employee::getSalary, Integer::sum));
            assertThat(total).isZero();
        }

        @Test
        @DisplayName("the one-argument reducing form returns an Optional")
        void reducingWithoutIdentityIsOptional() {
            Optional<Integer> max = Stream.of(3, 9, 4).collect(Collectors.reducing(Integer::max));
            assertThat(max).hasValue(9);
            assertThat(Stream.<Integer>empty().collect(Collectors.reducing(Integer::max))).isEmpty();
        }

        @Test
        @DisplayName("toMap(key, value, merge, supplier) merges duplicate keys into a TreeMap")
        void toMapWithMerge() {
            Map<String, Integer> payroll = CustomCollectorsDemo.sampleEmployees().stream().collect(
                    Collectors.toMap(Employee::getDept, Employee::getSalary, Integer::sum, TreeMap::new));

            assertThat(payroll.keySet()).containsExactly("Engineering", "HR", "Sales");
            assertThat(payroll).containsEntry("Engineering", 9000)
                               .containsEntry("HR", 3200)
                               .containsEntry("Sales", 6500);
        }

        @Test
        @DisplayName("summarizingInt on an empty stream: count 0, sum 0")
        void summarizingEmpty() {
            IntSummaryStatistics stats = Stream.<Employee>empty()
                    .collect(Collectors.summarizingInt(Employee::getSalary));
            assertThat(stats.getCount()).isZero();
            assertThat(stats.getSum()).isZero();
        }
    }

    // ── 4. Combiners ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Combiners")
    class CombinersTest {

        private Collector<Integer, long[], Long> sumOfSquares() {
            return Collector.of(
                    () -> new long[1],
                    (acc, n) -> acc[0] += (long) n * n,
                    (a, b) -> { a[0] += b[0]; return a; },
                    acc -> acc[0]);
        }

        private List<Integer> numbers() {
            List<Integer> numbers = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                numbers.add(i);
            }
            return numbers;
        }

        @Test
        @DisplayName("sequential result: 1² + 2² + … + 1000² = 333833500")
        void sequentialResult() {
            assertThat(numbers().stream().collect(sumOfSquares())).isEqualTo(333_833_500L);
        }

        @Test
        @DisplayName("a correct combiner gives the same result on a parallel stream")
        void parallelEqualsSequential() {
            long sequential = numbers().stream().collect(sumOfSquares());
            long parallel = numbers().parallelStream().collect(sumOfSquares());
            assertThat(parallel).isEqualTo(sequential);
        }

        @Test
        @DisplayName("a combiner that drops the right-hand side is wrong on parallel streams")
        void brokenCombinerLosesData() {
            Collector<Integer, long[], Long> broken = Collector.of(
                    () -> new long[1],
                    (acc, n) -> acc[0] += (long) n * n,
                    (a, b) -> a,                       // forgets b!
                    acc -> acc[0]);

            long sequential = numbers().stream().collect(broken);
            long parallel = numbers().parallelStream().collect(broken);

            assertThat(sequential).isEqualTo(333_833_500L);   // the combiner is never called here
            assertThat(parallel).isNotEqualTo(333_833_500L);  // parts were thrown away
        }

        @Test
        @DisplayName("built-in collectors already have correct combiners")
        void builtInsAreParallelSafe() {
            List<Integer> parallel = numbers().parallelStream().collect(Collectors.toList());
            assertThat(parallel).isEqualTo(numbers());
        }
    }
}
