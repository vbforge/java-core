package com.vbforge.streams_api_advanced;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ParallelStreamsDemoTest — verifies every concept shown in {@link ParallelStreamsDemo}.
 *
 * <p>These tests are always GREEN. Every assertion is machine-independent: none of them
 * depends on the number of cores or on thread scheduling.
 */
@DisplayName("ParallelStreamsDemo — API contracts")
class ParallelStreamsDemoTest {

    // ── 1. Creating and switching ─────────────────────────────────────────────

    @Nested
    @DisplayName("1. Creating parallel streams")
    class CreatingTest {

        private final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        @Test
        @DisplayName("stream() is sequential by default")
        void sequentialByDefault() {
            assertThat(numbers.stream().isParallel()).isFalse();
        }

        @Test
        @DisplayName("parallelStream() is parallel")
        void parallelStream() {
            assertThat(numbers.parallelStream().isParallel()).isTrue();
        }

        @Test
        @DisplayName("stream().parallel() switches to parallel")
        void parallelSwitch() {
            assertThat(numbers.stream().parallel().isParallel()).isTrue();
        }

        @Test
        @DisplayName("sequential() switches back")
        void sequentialSwitch() {
            assertThat(numbers.parallelStream().sequential().isParallel()).isFalse();
        }

        @Test
        @DisplayName("the last call wins: sequential().parallel() is parallel, parallel().sequential() is not")
        void lastCallWins() {
            assertThat(numbers.stream().sequential().parallel().isParallel()).isTrue();
            assertThat(numbers.stream().parallel().sequential().isParallel()).isFalse();
        }

        @Test
        @DisplayName("the mode belongs to the whole pipeline, even to steps written before the switch")
        void wholePipeline() {
            boolean mixed = numbers.stream().parallel().map(n -> n * 2).sequential().isParallel();
            assertThat(mixed).isFalse();
        }
    }

    // ── 2. Deterministic results ──────────────────────────────────────────────

    @Nested
    @DisplayName("2. Deterministic results")
    class DeterministicResultsTest {

        @Test
        @DisplayName("a parallel sum equals the sequential sum")
        void sumIsDeterministic() {
            long parallel = LongStream.rangeClosed(1, 1_000_000).parallel().sum();
            long sequential = LongStream.rangeClosed(1, 1_000_000).sum();
            assertThat(parallel).isEqualTo(500_000_500_000L);
            assertThat(parallel).isEqualTo(sequential);
        }

        @Test
        @DisplayName("collect(toList()) keeps the encounter order on a parallel stream")
        void collectKeepsOrder() {
            List<Integer> squares = IntStream.rangeClosed(1, 10).boxed().parallel()
                    .map(n -> n * n)
                    .collect(Collectors.toList());
            assertThat(squares).containsExactly(1, 4, 9, 16, 25, 36, 49, 64, 81, 100);
        }

        @Test
        @DisplayName("order is kept for a large list too")
        void orderForLargeList() {
            List<Integer> source = IntStream.range(0, 20_000).boxed().collect(Collectors.toList());
            List<Integer> result = source.parallelStream().map(n -> n + 1).collect(Collectors.toList());

            assertThat(result).hasSize(20_000);
            assertThat(result.get(0)).isEqualTo(1);
            assertThat(result.get(19_999)).isEqualTo(20_000);
            assertThat(result.get(10_000)).isEqualTo(10_001);
        }

        @Test
        @DisplayName("forEachOrdered visits the elements in encounter order, one at a time")
        void forEachOrdered() {
            List<Integer> visited = new ArrayList<>();
            IntStream.rangeClosed(1, 10).boxed().parallel().forEachOrdered(visited::add);
            assertThat(visited).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        }

        @Test
        @DisplayName("count() and filter() give the same answer as sequential")
        void countAndFilter() {
            long parallel = IntStream.rangeClosed(1, 100_000).parallel().filter(n -> n % 2 == 0).count();
            assertThat(parallel).isEqualTo(50_000L);
        }
    }

    // ── 3. Safe accumulation ──────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Safe accumulation")
    class SafeAccumulationTest {

        @Test
        @DisplayName("collect() builds a complete list from a parallel stream")
        void collectIsComplete() {
            List<Integer> collected = IntStream.range(0, 10_000).boxed().parallel()
                    .collect(Collectors.toList());
            assertThat(collected).hasSize(10_000);
            assertThat(collected.get(0)).isEqualTo(0);
            assertThat(collected.get(9_999)).isEqualTo(9_999);
        }

        @Test
        @DisplayName("reduce(identity, accumulator, combiner) works with a neutral identity")
        void reduceWithNeutralIdentity() {
            List<String> words = Arrays.asList("alpha", "beta", "gamma", "delta");
            int total = words.parallelStream().reduce(0, (acc, w) -> acc + w.length(), Integer::sum);
            assertThat(total).isEqualTo(19);
        }

        @Test
        @DisplayName("reduce on an empty parallel stream returns the identity")
        void reduceEmptyReturnsIdentity() {
            int total = new ArrayList<String>().parallelStream()
                    .reduce(0, (acc, w) -> acc + w.length(), Integer::sum);
            assertThat(total).isZero();
        }

        @Test
        @DisplayName("a thread-safe accumulator (LongAdder) gives the right total")
        void longAdder() {
            LongAdder adder = new LongAdder();
            IntStream.rangeClosed(1, 1000).parallel().forEach(adder::add);
            assertThat(adder.sum()).isEqualTo(500_500L);
        }
    }

    // ── 4. Concurrent collectors and findAny ──────────────────────────────────

    @Nested
    @DisplayName("4. Concurrent collectors and findAny")
    class ConcurrentCollectorsTest {

        private final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        @Test
        @DisplayName("groupingByConcurrent + counting gives exact counts")
        void groupingByConcurrentCounts() {
            Map<Integer, Long> counts = IntStream.rangeClosed(1, 30_000).boxed().parallel()
                    .collect(Collectors.groupingByConcurrent(n -> n % 3, Collectors.counting()));

            assertThat(counts).hasSize(3)
                              .containsEntry(0, 10_000L)
                              .containsEntry(1, 10_000L)
                              .containsEntry(2, 10_000L);
        }

        @Test
        @DisplayName("groupingByConcurrent gives the same counts as sequential groupingBy")
        void sameAsSequentialGrouping() {
            Map<Integer, Long> sequential = IntStream.rangeClosed(1, 1000).boxed()
                    .collect(Collectors.groupingBy(n -> n % 4, Collectors.counting()));
            Map<Integer, Long> concurrent = IntStream.rangeClosed(1, 1000).boxed().parallel()
                    .collect(Collectors.groupingByConcurrent(n -> n % 4, Collectors.counting()));

            assertThat(concurrent).isEqualTo(sequential);
        }

        @Test
        @DisplayName("findFirst on a parallel stream is still deterministic")
        void findFirstIsDeterministic() {
            Optional<Integer> first = numbers.parallelStream().filter(n -> n % 2 == 0).findFirst();
            assertThat(first).hasValue(2);
        }

        @Test
        @DisplayName("findAny returns one of the matching elements")
        void findAnyReturnsAMatch() {
            Optional<Integer> any = numbers.parallelStream().filter(n -> n % 2 == 0).findAny();
            assertThat(any.isPresent()).isTrue();
            assertThat(any.get() % 2).isEqualTo(0);
        }

        @Test
        @DisplayName("findAny with no match is empty")
        void findAnyNoMatch() {
            assertThat(numbers.parallelStream().filter(n -> n > 100).findAny()).isEmpty();
        }
    }
}
