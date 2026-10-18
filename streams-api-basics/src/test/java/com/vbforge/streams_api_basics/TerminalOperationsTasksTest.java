package com.vbforge.streams_api_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TerminalOperationsTasksTest — tests for every method in {@link TerminalOperationsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code TerminalOperationsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=TerminalOperationsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 8 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("TerminalOperationsTasks — your implementations")
class TerminalOperationsTasksTest {

    private TerminalOperationsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TerminalOperationsTasks();
    }

    // ── Task 1: joinUpperCase ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement joinUpperCase()")
    @DisplayName("Task 1 — joinUpperCase()")
    class JoinUpperCaseTest {

        @Test
        @DisplayName("[a,b,c] with \"-\" → \"A-B-C\"")
        void basic() {
            assertThat(tasks.joinUpperCase(Arrays.asList("a", "b", "c"), "-")).isEqualTo("A-B-C");
        }

        @Test
        @DisplayName("single word has no delimiter")
        void single() {
            assertThat(tasks.joinUpperCase(Arrays.asList("hello"), ", ")).isEqualTo("HELLO");
        }

        @Test
        @DisplayName("empty list → empty string")
        void emptyList() {
            assertThat(tasks.joinUpperCase(new ArrayList<String>(), ",")).isEmpty();
        }

        @Test
        @DisplayName("multi-character delimiter is used between words only")
        void multiCharDelimiter() {
            assertThat(tasks.joinUpperCase(Arrays.asList("x", "y"), " + ")).isEqualTo("X + Y");
        }
    }

    // ── Task 2: wordLengths ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement wordLengths()")
    @DisplayName("Task 2 — wordLengths()")
    class WordLengthsTest {

        @Test
        @DisplayName("[cat,dog,cat] → {cat=3, dog=3}")
        void duplicatesCollapse() {
            Map<String, Integer> result = tasks.wordLengths(Arrays.asList("cat", "dog", "cat"));
            assertThat(result).hasSize(2)
                              .containsEntry("cat", 3)
                              .containsEntry("dog", 3);
        }

        @Test
        @DisplayName("single word")
        void single() {
            assertThat(tasks.wordLengths(Arrays.asList("hello"))).containsEntry("hello", 5);
        }

        @Test
        @DisplayName("empty list → empty map")
        void emptyList() {
            assertThat(tasks.wordLengths(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("different words keep their own lengths")
        void differentLengths() {
            Map<String, Integer> result = tasks.wordLengths(Arrays.asList("a", "bb", "ccc"));
            assertThat(result).hasSize(3)
                              .containsEntry("a", 1)
                              .containsEntry("bb", 2)
                              .containsEntry("ccc", 3);
        }
    }

    // ── Task 3: largest ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement largest()")
    @DisplayName("Task 3 — largest()")
    class LargestTest {

        @Test
        @DisplayName("[3,9,2] → Optional[9]")
        void basic() {
            assertThat(tasks.largest(Arrays.asList(3, 9, 2))).hasValue(9);
        }

        @Test
        @DisplayName("all negative numbers")
        void negatives() {
            assertThat(tasks.largest(Arrays.asList(-5, -1))).hasValue(-1);
        }

        @Test
        @DisplayName("single element")
        void single() {
            assertThat(tasks.largest(Arrays.asList(7))).hasValue(7);
        }

        @Test
        @DisplayName("empty list → Optional.empty()")
        void emptyList() {
            Optional<Integer> result = tasks.largest(new ArrayList<Integer>());
            assertThat(result).isEmpty();
        }
    }

    // ── Task 4: product ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement product()")
    @DisplayName("Task 4 — product()")
    class ProductTest {

        @Test
        @DisplayName("[2,3,4] → 24")
        void basic() {
            assertThat(tasks.product(Arrays.asList(2, 3, 4))).isEqualTo(24L);
        }

        @Test
        @DisplayName("single element")
        void single() {
            assertThat(tasks.product(Arrays.asList(5))).isEqualTo(5L);
        }

        @Test
        @DisplayName("a zero makes the product zero")
        void withZero() {
            assertThat(tasks.product(Arrays.asList(0, 7))).isEqualTo(0L);
        }

        @Test
        @DisplayName("empty list → 1 (the identity of multiplication)")
        void emptyList() {
            assertThat(tasks.product(new ArrayList<Integer>())).isEqualTo(1L);
        }

        @Test
        @DisplayName("result does not overflow int: 100000 * 100000 = 10_000_000_000")
        void largeResult() {
            assertThat(tasks.product(Arrays.asList(100_000, 100_000))).isEqualTo(10_000_000_000L);
        }
    }

    // ── Task 5: allPositive ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement allPositive()")
    @DisplayName("Task 5 — allPositive()")
    class AllPositiveTest {

        @Test
        @DisplayName("[1,2,3] → true")
        void allPositive() {
            assertThat(tasks.allPositive(Arrays.asList(1, 2, 3))).isTrue();
        }

        @Test
        @DisplayName("zero is not positive")
        void withZero() {
            assertThat(tasks.allPositive(Arrays.asList(1, 0, 3))).isFalse();
        }

        @Test
        @DisplayName("negative number → false")
        void withNegative() {
            assertThat(tasks.allPositive(Arrays.asList(-1))).isFalse();
        }

        @Test
        @DisplayName("empty list → true (vacuous truth)")
        void emptyList() {
            assertThat(tasks.allPositive(new ArrayList<Integer>())).isTrue();
        }
    }

    // ── Task 6: firstLongWord ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement firstLongWord()")
    @DisplayName("Task 6 — firstLongWord()")
    class FirstLongWordTest {

        @Test
        @DisplayName("[hi,hello,world] with min 4 → Optional[hello]")
        void basic() {
            assertThat(tasks.firstLongWord(Arrays.asList("hi", "hello", "world"), 4)).hasValue("hello");
        }

        @Test
        @DisplayName("no word long enough → Optional.empty()")
        void noneLongEnough() {
            assertThat(tasks.firstLongWord(Arrays.asList("hi", "yo"), 4)).isEmpty();
        }

        @Test
        @DisplayName("length equal to minLength qualifies")
        void boundary() {
            assertThat(tasks.firstLongWord(Arrays.asList("abcd"), 4)).hasValue("abcd");
        }

        @Test
        @DisplayName("empty list → Optional.empty()")
        void emptyList() {
            assertThat(tasks.firstLongWord(new ArrayList<String>(), 4)).isEmpty();
        }
    }

    // ── Task 7: averageLength ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement averageLength()")
    @DisplayName("Task 7 — averageLength()")
    class AverageLengthTest {

        @Test
        @DisplayName("[a,bb,ccc] → 2.0")
        void basic() {
            assertThat(tasks.averageLength(Arrays.asList("a", "bb", "ccc"))).isEqualTo(2.0);
        }

        @Test
        @DisplayName("[a,bb] → 1.5 (result is not truncated to int)")
        void fractional() {
            assertThat(tasks.averageLength(Arrays.asList("a", "bb"))).isEqualTo(1.5);
        }

        @Test
        @DisplayName("single word")
        void single() {
            assertThat(tasks.averageLength(Arrays.asList("abc"))).isEqualTo(3.0);
        }

        @Test
        @DisplayName("empty list → 0.0")
        void emptyList() {
            assertThat(tasks.averageLength(new ArrayList<String>())).isEqualTo(0.0);
        }

        @ParameterizedTest(name = "[{0},{1}] → {2}")
        @CsvSource({
                "a,    bbb,  2.0",
                "ab,   ab,   2.0",
                "abcd, a,    2.5",
        })
        @DisplayName("parametrized pairs of words")
        void parametrized(String first, String second, double expected) {
            assertThat(tasks.averageLength(Arrays.asList(first, second))).isEqualTo(expected);
        }
    }

    // ── Task 8: uniqueInitials ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement uniqueInitials()")
    @DisplayName("Task 8 — uniqueInitials()")
    class UniqueInitialsTest {

        @Test
        @DisplayName("[Apple,avocado,Banana] → {a, b}")
        void basic() {
            assertThat(tasks.uniqueInitials(Arrays.asList("Apple", "avocado", "Banana")))
                    .containsExactlyInAnyOrder("a", "b");
        }

        @Test
        @DisplayName("empty words are ignored")
        void emptyWordsIgnored() {
            assertThat(tasks.uniqueInitials(Arrays.asList("cat", "", "Car")))
                    .containsExactlyInAnyOrder("c");
        }

        @Test
        @DisplayName("empty list → empty set")
        void emptyList() {
            assertThat(tasks.uniqueInitials(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("every distinct initial appears once")
        void allDistinct() {
            assertThat(tasks.uniqueInitials(Arrays.asList("x", "y", "z", "x")))
                    .hasSize(3)
                    .containsExactlyInAnyOrder("x", "y", "z");
        }
    }
}
