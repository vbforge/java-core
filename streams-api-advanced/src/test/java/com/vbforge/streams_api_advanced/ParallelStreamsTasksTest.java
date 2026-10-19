package com.vbforge.streams_api_advanced;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ParallelStreamsTasksTest — tests for every method in {@link ParallelStreamsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ParallelStreamsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ParallelStreamsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ParallelStreamsTasks — your implementations")
class ParallelStreamsTasksTest {

    private ParallelStreamsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ParallelStreamsTasks();
    }

    // ── Task 1: sumUpTo ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sumUpTo()")
    @DisplayName("Task 1 — sumUpTo()")
    class SumUpToTest {

        @Test
        @DisplayName("sumUpTo(10) → 55")
        void basic() {
            assertThat(tasks.sumUpTo(10)).isEqualTo(55L);
        }

        @Test
        @DisplayName("sumUpTo(1) → 1")
        void one() {
            assertThat(tasks.sumUpTo(1)).isEqualTo(1L);
        }

        @Test
        @DisplayName("sumUpTo(0) → 0")
        void zero() {
            assertThat(tasks.sumUpTo(0)).isZero();
        }

        @Test
        @DisplayName("sumUpTo(1_000_000) → 500_000_500_000 (does not fit into an int)")
        void large() {
            assertThat(tasks.sumUpTo(1_000_000)).isEqualTo(500_000_500_000L);
        }

        @ParameterizedTest(name = "sumUpTo({0}) = {1}")
        @CsvSource({
                "2,   3",
                "100, 5050",
                "1000, 500500",
        })
        @DisplayName("parametrized values")
        void parametrized(long n, long expected) {
            assertThat(tasks.sumUpTo(n)).isEqualTo(expected);
        }
    }

    // ── Task 2: squaresInOrder ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement squaresInOrder()")
    @DisplayName("Task 2 — squaresInOrder()")
    class SquaresInOrderTest {

        @Test
        @DisplayName("[1,2,3,4] → [1,4,9,16]")
        void basic() {
            assertThat(tasks.squaresInOrder(Arrays.asList(1, 2, 3, 4))).containsExactly(1, 4, 9, 16);
        }

        @Test
        @DisplayName("single element")
        void single() {
            assertThat(tasks.squaresInOrder(Arrays.asList(5))).containsExactly(25);
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.squaresInOrder(new ArrayList<Integer>())).isEmpty();
        }

        @Test
        @DisplayName("a large list keeps its size and order (20000² still fits into an int)")
        void largeListKeepsOrder() {
            List<Integer> input = IntStream.rangeClosed(1, 20_000).boxed().collect(Collectors.toList());
            List<Integer> expected = IntStream.rangeClosed(1, 20_000).boxed()
                    .map(n -> n * n)
                    .collect(Collectors.toList());

            assertThat(tasks.squaresInOrder(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("the input list is not modified")
        void inputUntouched() {
            List<Integer> input = new ArrayList<>(Arrays.asList(3, 1, 2));
            tasks.squaresInOrder(input);
            assertThat(input).containsExactly(3, 1, 2);
        }
    }

    // ── Task 3: countByRemainder ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countByRemainder()")
    @DisplayName("Task 3 — countByRemainder()")
    class CountByRemainderTest {

        @Test
        @DisplayName("[1..6] divisor 3 → {0=2, 1=2, 2=2}")
        void basic() {
            Map<Integer, Long> result = tasks.countByRemainder(Arrays.asList(1, 2, 3, 4, 5, 6), 3);
            assertThat(result).hasSize(3)
                              .containsEntry(0, 2L)
                              .containsEntry(1, 2L)
                              .containsEntry(2, 2L);
        }

        @Test
        @DisplayName("only multiples → a single key")
        void singleKey() {
            Map<Integer, Long> result = tasks.countByRemainder(Arrays.asList(3, 6, 9), 3);
            assertThat(result).hasSize(1).containsEntry(0, 3L);
        }

        @Test
        @DisplayName("empty input → empty map")
        void emptyInput() {
            assertThat(tasks.countByRemainder(new ArrayList<Integer>(), 3)).isEmpty();
        }

        @Test
        @DisplayName("a large list is counted exactly")
        void largeList() {
            List<Integer> numbers = IntStream.rangeClosed(1, 30_000).boxed().collect(Collectors.toList());
            Map<Integer, Long> result = tasks.countByRemainder(numbers, 3);
            assertThat(result).hasSize(3)
                              .containsEntry(0, 10_000L)
                              .containsEntry(1, 10_000L)
                              .containsEntry(2, 10_000L);
        }
    }

    // ── Task 4: totalLength ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement totalLength()")
    @DisplayName("Task 4 — totalLength()")
    class TotalLengthTest {

        @Test
        @DisplayName("[alpha,beta,gamma,delta] → 19")
        void basic() {
            assertThat(tasks.totalLength(Arrays.asList("alpha", "beta", "gamma", "delta"))).isEqualTo(19);
        }

        @Test
        @DisplayName("single word")
        void single() {
            assertThat(tasks.totalLength(Arrays.asList("a"))).isEqualTo(1);
        }

        @Test
        @DisplayName("empty input → 0")
        void emptyInput() {
            assertThat(tasks.totalLength(new ArrayList<String>())).isZero();
        }

        @Test
        @DisplayName("a large list gives the exact total (no lost updates)")
        void largeList() {
            List<String> words = IntStream.range(0, 50_000).mapToObj(i -> "abc").collect(Collectors.toList());
            assertThat(tasks.totalLength(words)).isEqualTo(150_000);
        }

        @Test
        @DisplayName("empty words count as 0")
        void emptyWords() {
            assertThat(tasks.totalLength(Arrays.asList("", "ab", ""))).isEqualTo(2);
        }
    }

    // ── Task 5: anyEven ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement anyEven()")
    @DisplayName("Task 5 — anyEven()")
    class AnyEvenTest {

        @Test
        @DisplayName("[1,3,4,7,8] → an even element (4 or 8)")
        void basic() {
            List<Integer> input = Arrays.asList(1, 3, 4, 7, 8);
            Optional<Integer> result = tasks.anyEven(input);

            assertThat(result.isPresent()).isTrue();
            assertThat(result.get() % 2).isEqualTo(0);
            assertThat(input.contains(result.get())).isTrue();
        }

        @Test
        @DisplayName("single even element")
        void singleEven() {
            assertThat(tasks.anyEven(Arrays.asList(2))).hasValue(2);
        }

        @Test
        @DisplayName("no even numbers → Optional.empty()")
        void noEvens() {
            assertThat(tasks.anyEven(Arrays.asList(1, 3, 5))).isEmpty();
        }

        @Test
        @DisplayName("empty input → Optional.empty()")
        void emptyInput() {
            assertThat(tasks.anyEven(new ArrayList<Integer>())).isEmpty();
        }

        @Test
        @DisplayName("a large list with a single even number finds it")
        void largeListSingleEven() {
            List<Integer> numbers = IntStream.range(0, 20_001)
                    .map(i -> 2 * i + 1)
                    .boxed()
                    .collect(Collectors.toList());
            numbers.set(12_345, 4242);

            assertThat(tasks.anyEven(numbers)).hasValue(4242);
        }
    }
}
