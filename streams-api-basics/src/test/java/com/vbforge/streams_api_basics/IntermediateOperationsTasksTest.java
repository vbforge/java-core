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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * IntermediateOperationsTasksTest — tests for every method in {@link IntermediateOperationsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code IntermediateOperationsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=IntermediateOperationsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 7 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("IntermediateOperationsTasks — your implementations")
class IntermediateOperationsTasksTest {

    private IntermediateOperationsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new IntermediateOperationsTasks();
    }

    // ── Task 1: evenSquares ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement evenSquares()")
    @DisplayName("Task 1 — evenSquares()")
    class EvenSquaresTest {

        @Test
        @DisplayName("[1,2,3,4] → [4,16]")
        void basic() {
            assertThat(tasks.evenSquares(Arrays.asList(1, 2, 3, 4))).containsExactly(4, 16);
        }

        @Test
        @DisplayName("no even numbers → empty list")
        void noEvens() {
            assertThat(tasks.evenSquares(Arrays.asList(1, 3, 5))).isEmpty();
        }

        @Test
        @DisplayName("negative evens and zero are handled")
        void negativesAndZero() {
            assertThat(tasks.evenSquares(Arrays.asList(-2, 0, 6))).containsExactly(4, 0, 36);
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.evenSquares(new ArrayList<Integer>())).isEmpty();
        }

        @Test
        @DisplayName("original order is preserved")
        void orderPreserved() {
            assertThat(tasks.evenSquares(Arrays.asList(6, 2, 4))).containsExactly(36, 4, 16);
        }
    }

    // ── Task 2: cleanAndUpperCase ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement cleanAndUpperCase()")
    @DisplayName("Task 2 — cleanAndUpperCase()")
    class CleanAndUpperCaseTest {

        @Test
        @DisplayName("[\" a \",\"\",\"  \",\"bc\"] → [A, BC]")
        void basic() {
            assertThat(tasks.cleanAndUpperCase(Arrays.asList(" a ", "", "  ", "bc")))
                    .containsExactly("A", "BC");
        }

        @Test
        @DisplayName("single word")
        void single() {
            assertThat(tasks.cleanAndUpperCase(Arrays.asList("x"))).containsExactly("X");
        }

        @Test
        @DisplayName("only blank strings → empty list")
        void onlyBlank() {
            assertThat(tasks.cleanAndUpperCase(Arrays.asList("", " "))).isEmpty();
            assertThat(tasks.cleanAndUpperCase(Arrays.asList("\t", "  \t "))).isEmpty();
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.cleanAndUpperCase(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("inner spaces are kept, only the ends are trimmed")
        void innerSpaces() {
            assertThat(tasks.cleanAndUpperCase(Arrays.asList("  hello world ")))
                    .containsExactly("HELLO WORLD");
        }
    }

    // ── Task 3: sortByLengthThenAlphabetically ────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sortByLengthThenAlphabetically()")
    @DisplayName("Task 3 — sortByLengthThenAlphabetically()")
    class SortByLengthThenAlphabeticallyTest {

        @Test
        @DisplayName("[pear,fig,apple,kiwi,plum] → [fig,kiwi,pear,plum,apple]")
        void basic() {
            assertThat(tasks.sortByLengthThenAlphabetically(
                    Arrays.asList("pear", "fig", "apple", "kiwi", "plum")))
                    .containsExactly("fig", "kiwi", "pear", "plum", "apple");
        }

        @Test
        @DisplayName("same length → alphabetical")
        void sameLength() {
            assertThat(tasks.sortByLengthThenAlphabetically(Arrays.asList("b", "a")))
                    .containsExactly("a", "b");
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.sortByLengthThenAlphabetically(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("shorter words always come first, whatever their letters")
        void lengthBeatsAlphabet() {
            assertThat(tasks.sortByLengthThenAlphabetically(Arrays.asList("aaa", "z")))
                    .containsExactly("z", "aaa");
        }

        @Test
        @DisplayName("input list is not modified")
        void inputUntouched() {
            List<String> input = new ArrayList<>(Arrays.asList("ccc", "a", "bb"));
            tasks.sortByLengthThenAlphabetically(input);
            assertThat(input).containsExactly("ccc", "a", "bb");
        }
    }

    // ── Task 4: distinctDescending ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement distinctDescending()")
    @DisplayName("Task 4 — distinctDescending()")
    class DistinctDescendingTest {

        @Test
        @DisplayName("[3,1,3,2,1] → [3,2,1]")
        void basic() {
            assertThat(tasks.distinctDescending(Arrays.asList(3, 1, 3, 2, 1))).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("all equal → single element")
        void allSame() {
            assertThat(tasks.distinctDescending(Arrays.asList(5, 5, 5))).containsExactly(5);
        }

        @Test
        @DisplayName("negative numbers sort correctly")
        void negatives() {
            assertThat(tasks.distinctDescending(Arrays.asList(-1, -3, -1))).containsExactly(-1, -3);
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.distinctDescending(new ArrayList<Integer>())).isEmpty();
        }
    }

    // ── Task 5: page ──────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement page()")
    @DisplayName("Task 5 — page()")
    class PageTest {

        private final List<Integer> items = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        @Test
        @DisplayName("first page (index 0)")
        void firstPage() {
            assertThat(tasks.page(items, 0, 3)).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("middle page (index 1)")
        void middlePage() {
            assertThat(tasks.page(items, 1, 3)).containsExactly(4, 5, 6);
        }

        @Test
        @DisplayName("last page may be shorter")
        void lastPartialPage() {
            assertThat(tasks.page(items, 3, 3)).containsExactly(10);
        }

        @Test
        @DisplayName("page past the end → empty list")
        void pastTheEnd() {
            assertThat(tasks.page(items, 5, 3)).isEmpty();
        }

        @Test
        @DisplayName("page size larger than the list returns everything on page 0")
        void bigPageSize() {
            assertThat(tasks.page(items, 0, 100)).hasSize(10);
        }

        @Test
        @DisplayName("huge page index does not overflow int arithmetic")
        void hugePageIndex() {
            // 1_500_000_000 * 2 overflows int and wraps to a negative offset,
            // which makes skip() throw IllegalArgumentException
            assertThat(tasks.page(items, 1_500_000_000, 2)).isEmpty();
        }
    }

    // ── Task 6: sumOfOddSquares ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sumOfOddSquares()")
    @DisplayName("Task 6 — sumOfOddSquares()")
    class SumOfOddSquaresTest {

        @Test
        @DisplayName("[1,2,3,4] → 10")
        void basic() {
            assertThat(tasks.sumOfOddSquares(new int[]{1, 2, 3, 4})).isEqualTo(10);
        }

        @Test
        @DisplayName("no odd numbers → 0")
        void noOdds() {
            assertThat(tasks.sumOfOddSquares(new int[]{2, 4})).isZero();
        }

        @Test
        @DisplayName("negative odd numbers count too: [-3] → 9")
        void negativeOdd() {
            assertThat(tasks.sumOfOddSquares(new int[]{-3})).isEqualTo(9);
        }

        @Test
        @DisplayName("empty array → 0")
        void emptyArray() {
            assertThat(tasks.sumOfOddSquares(new int[]{})).isZero();
        }

        @ParameterizedTest(name = "[{0}, {1}, {2}] → {3}")
        @CsvSource({
                "1,  1,  1,  3",
                "3,  5,  7,  83",
                "-1, -2, 5,  26",
                "2,  4,  6,  0",
        })
        @DisplayName("parametrized arrays of three numbers")
        void parametrized(int a, int b, int c, int expected) {
            assertThat(tasks.sumOfOddSquares(new int[]{a, b, c})).isEqualTo(expected);
        }
    }

    // ── Task 7: firstNMatches ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement firstNMatches()")
    @DisplayName("Task 7 — firstNMatches()")
    class FirstNMatchesTest {

        private final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        private final Predicate<Integer> even = n -> n % 2 == 0;

        @Test
        @DisplayName("first 3 even numbers → [2,4,6]")
        void basic() {
            assertThat(tasks.firstNMatches(numbers, even, 3)).containsExactly(2, 4, 6);
        }

        @Test
        @DisplayName("n = 0 → empty list")
        void zero() {
            assertThat(tasks.firstNMatches(numbers, even, 0)).isEmpty();
        }

        @Test
        @DisplayName("fewer matches than n → all matches")
        void fewerThanN() {
            assertThat(tasks.firstNMatches(numbers, even, 99)).containsExactly(2, 4, 6, 8, 10);
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.firstNMatches(new ArrayList<Integer>(), even, 3)).isEmpty();
        }

        @Test
        @DisplayName("is lazy: the predicate is tested for 1..6 only, not for the whole list")
        void stopsEarly() {
            AtomicInteger calls = new AtomicInteger();
            Predicate<Integer> countingEven = n -> {
                calls.incrementAndGet();
                return n % 2 == 0;
            };

            List<Integer> result = tasks.firstNMatches(numbers, countingEven, 3);

            assertThat(result).containsExactly(2, 4, 6);
            assertThat(calls.get()).isEqualTo(6);
        }
    }
}
