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
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CustomCollectorsTasksTest — tests for every method in {@link CustomCollectorsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code CustomCollectorsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=CustomCollectorsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("CustomCollectorsTasks — your implementations")
class CustomCollectorsTasksTest {

    private CustomCollectorsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new CustomCollectorsTasks();
    }

    // ── Task 1: unmodifiableEvens ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement unmodifiableEvens()")
    @DisplayName("Task 1 — unmodifiableEvens()")
    class UnmodifiableEvensTest {

        @Test
        @DisplayName("[1,2,3,4] → [2,4]")
        void basic() {
            assertThat(tasks.unmodifiableEvens(Arrays.asList(1, 2, 3, 4))).containsExactly(2, 4);
        }

        @Test
        @DisplayName("no evens → empty list")
        void noEvens() {
            assertThat(tasks.unmodifiableEvens(Arrays.asList(1, 3))).isEmpty();
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.unmodifiableEvens(new ArrayList<Integer>())).isEmpty();
        }

        @Test
        @DisplayName("the result rejects add()")
        void rejectsAdd() {
            List<Integer> result = tasks.unmodifiableEvens(Arrays.asList(2, 4));
            assertThatThrownBy(() -> result.add(6)).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("the result rejects remove() and set() too")
        void rejectsRemoveAndSet() {
            List<Integer> result = tasks.unmodifiableEvens(Arrays.asList(2, 4));
            assertThatThrownBy(() -> result.remove(0)).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> result.set(0, 9)).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("an empty result is read-only as well")
        void emptyResultIsReadOnly() {
            List<Integer> result = tasks.unmodifiableEvens(Arrays.asList(1, 3));
            assertThatThrownBy(() -> result.add(2)).isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // ── Task 2: longestWordByInitial ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement longestWordByInitial()")
    @DisplayName("Task 2 — longestWordByInitial()")
    class LongestWordByInitialTest {

        @Test
        @DisplayName("[apple,avocado,fig,banana,blue] → {a=avocado, f=fig, b=banana}")
        void basic() {
            Map<Character, String> result = tasks.longestWordByInitial(
                    Arrays.asList("apple", "avocado", "fig", "banana", "blue"));
            assertThat(result).hasSize(3)
                              .containsEntry('a', "avocado")
                              .containsEntry('f', "fig")
                              .containsEntry('b', "banana");
        }

        @Test
        @DisplayName("initial is lower-cased, the word keeps its case")
        void caseHandling() {
            assertThat(tasks.longestWordByInitial(Arrays.asList("Kiwi")))
                    .hasSize(1)
                    .containsEntry('k', "Kiwi");
        }

        @Test
        @DisplayName("empty input → empty map")
        void emptyInput() {
            assertThat(tasks.longestWordByInitial(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("values are plain Strings, not Optionals")
        void valuesArePlainStrings() {
            Map<Character, String> result = tasks.longestWordByInitial(Arrays.asList("ab", "abc"));
            String longest = result.get('a');
            assertThat(longest).isEqualTo("abc");
        }

        @Test
        @DisplayName("the longest word wins regardless of position")
        void positionDoesNotMatter() {
            assertThat(tasks.longestWordByInitial(Arrays.asList("sun", "sunshine", "sky")))
                    .containsEntry('s', "sunshine");
        }
    }

    // ── Task 3: longestStringCollector ────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement longestStringCollector()")
    @DisplayName("Task 3 — longestStringCollector()")
    class LongestStringCollectorTest {

        @Test
        @DisplayName("[a,ccc,bb] → ccc")
        void basic() {
            assertThat(Arrays.asList("a", "ccc", "bb").stream().collect(tasks.longestStringCollector()))
                    .isEqualTo("ccc");
        }

        @Test
        @DisplayName("tie: the first of the longest strings wins")
        void tieFirstWins() {
            assertThat(Arrays.asList("ab", "cd", "e").stream().collect(tasks.longestStringCollector()))
                    .isEqualTo("ab");
        }

        @Test
        @DisplayName("single element")
        void single() {
            assertThat(Arrays.asList("x").stream().collect(tasks.longestStringCollector()))
                    .isEqualTo("x");
        }

        @Test
        @DisplayName("empty stream → empty string")
        void emptyStream() {
            assertThat(new ArrayList<String>().stream().collect(tasks.longestStringCollector()))
                    .isEmpty();
        }

        @Test
        @DisplayName("the collector is reusable: every collect() starts from scratch")
        void reusable() {
            assertThat(Arrays.asList("aaa").stream().collect(tasks.longestStringCollector()))
                    .isEqualTo("aaa");
            assertThat(Arrays.asList("b").stream().collect(tasks.longestStringCollector()))
                    .isEqualTo("b");
        }

        @Test
        @DisplayName("works on a parallel stream: same answer, first of the longest wins")
        void parallelStream() {
            List<String> words = IntStream.range(0, 5000)
                    .mapToObj(i -> i == 1234 ? "winner-first" : i == 4000 ? "winner-later" : "w" + (i % 7))
                    .collect(Collectors.toList());

            assertThat(words.parallelStream().collect(tasks.longestStringCollector()))
                    .isEqualTo("winner-first");
        }

        @Test
        @DisplayName("parallel: a longer string in a late chunk is not lost by the combiner")
        void parallelLongestInLastChunk() {
            List<String> words = IntStream.range(0, 5000)
                    .mapToObj(i -> i == 4900 ? "the-longest-string" : "w" + (i % 7))
                    .collect(Collectors.toList());

            assertThat(words.parallelStream().collect(tasks.longestStringCollector()))
                    .isEqualTo("the-longest-string");
        }
    }

    // ── Task 4: lengthStats ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement lengthStats()")
    @DisplayName("Task 4 — lengthStats()")
    class LengthStatsTest {

        @Test
        @DisplayName("[a,bb,ccc] → count=3, sum=6, min=1, max=3, average=2.0")
        void basic() {
            IntSummaryStatistics stats = tasks.lengthStats(Arrays.asList("a", "bb", "ccc"));
            assertThat(stats.getCount()).isEqualTo(3L);
            assertThat(stats.getSum()).isEqualTo(6L);
            assertThat(stats.getMin()).isEqualTo(1);
            assertThat(stats.getMax()).isEqualTo(3);
            assertThat(stats.getAverage()).isEqualTo(2.0);
        }

        @Test
        @DisplayName("single word")
        void single() {
            IntSummaryStatistics stats = tasks.lengthStats(Arrays.asList("abcd"));
            assertThat(stats.getCount()).isEqualTo(1L);
            assertThat(stats.getMin()).isEqualTo(4);
            assertThat(stats.getMax()).isEqualTo(4);
            assertThat(stats.getAverage()).isEqualTo(4.0);
        }

        @Test
        @DisplayName("empty input → count 0, sum 0")
        void emptyInput() {
            IntSummaryStatistics stats = tasks.lengthStats(new ArrayList<String>());
            assertThat(stats.getCount()).isZero();
            assertThat(stats.getSum()).isZero();
        }

        @Test
        @DisplayName("average is not truncated")
        void fractionalAverage() {
            assertThat(tasks.lengthStats(Arrays.asList("a", "bb")).getAverage()).isEqualTo(1.5);
        }
    }

    // ── Task 5: averageCollector ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement averageCollector()")
    @DisplayName("Task 5 — averageCollector()")
    class AverageCollectorTest {

        @Test
        @DisplayName("[2,4,9] → 5.0")
        void basic() {
            assertThat(Arrays.asList(2, 4, 9).stream().collect(tasks.averageCollector())).isEqualTo(5.0);
        }

        @Test
        @DisplayName("[1,2] → 1.5 (not truncated)")
        void fractional() {
            assertThat(Arrays.asList(1, 2).stream().collect(tasks.averageCollector())).isEqualTo(1.5);
        }

        @Test
        @DisplayName("single element")
        void single() {
            assertThat(Arrays.asList(7).stream().collect(tasks.averageCollector())).isEqualTo(7.0);
        }

        @Test
        @DisplayName("empty stream → 0.0")
        void emptyStream() {
            assertThat(new ArrayList<Integer>().stream().collect(tasks.averageCollector())).isEqualTo(0.0);
        }

        @Test
        @DisplayName("works on a parallel stream: average of 1..10000 is 5000.5")
        void parallelStream() {
            List<Integer> numbers = IntStream.rangeClosed(1, 10_000).boxed().collect(Collectors.toList());
            assertThat(numbers.parallelStream().collect(tasks.averageCollector())).isEqualTo(5000.5);
        }

        @ParameterizedTest(name = "[{0},{1},{2}] → {3}")
        @CsvSource({
                "1, 2, 3, 2.0",
                "10, 20, 60, 30.0",
                "-5, 5, 0, 0.0",
        })
        @DisplayName("parametrized triples")
        void parametrized(int a, int b, int c, double expected) {
            assertThat(Arrays.asList(a, b, c).stream().collect(tasks.averageCollector())).isEqualTo(expected);
        }
    }
}
