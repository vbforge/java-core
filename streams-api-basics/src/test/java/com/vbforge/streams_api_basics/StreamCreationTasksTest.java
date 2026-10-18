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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StreamCreationTasksTest — tests for every method in {@link StreamCreationTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code StreamCreationTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=StreamCreationTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("StreamCreationTasks — your implementations")
class StreamCreationTasksTest {

    private StreamCreationTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new StreamCreationTasks();
    }

    // ── Task 1: powersOfTwo ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement powersOfTwo()")
    @DisplayName("Task 1 — powersOfTwo()")
    class PowersOfTwoTest {

        @Test
        @DisplayName("powersOfTwo(5) → [1,2,4,8,16]")
        void basic() {
            assertThat(tasks.powersOfTwo(5)).containsExactly(1, 2, 4, 8, 16);
        }

        @Test
        @DisplayName("count 1 → only the first power")
        void single() {
            assertThat(tasks.powersOfTwo(1)).containsExactly(1);
        }

        @Test
        @DisplayName("count 0 → empty list")
        void zero() {
            assertThat(tasks.powersOfTwo(0)).isEmpty();
        }

        @Test
        @DisplayName("the 10th power of two is 512")
        void longer() {
            List<Integer> result = tasks.powersOfTwo(10);
            assertThat(result).hasSize(10);
            assertThat(result.get(9)).isEqualTo(512);
        }
    }

    // ── Task 2: sumOfRange ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sumOfRange()")
    @DisplayName("Task 2 — sumOfRange()")
    class SumOfRangeTest {

        @Test
        @DisplayName("sumOfRange(1, 5) → 15 (both ends included)")
        void basic() {
            assertThat(tasks.sumOfRange(1, 5)).isEqualTo(15);
        }

        @Test
        @DisplayName("from == to → that single number")
        void singleNumber() {
            assertThat(tasks.sumOfRange(3, 3)).isEqualTo(3);
        }

        @Test
        @DisplayName("range around zero cancels out")
        void aroundZero() {
            assertThat(tasks.sumOfRange(-2, 2)).isZero();
        }

        @Test
        @DisplayName("from > to → empty range, sum 0")
        void emptyRange() {
            assertThat(tasks.sumOfRange(5, 1)).isZero();
        }

        @ParameterizedTest(name = "sumOfRange({0},{1}) = {2}")
        @CsvSource({
                "1,  10, 55",
                "0,  0,  0",
                "-3, 3,  0",
                "10, 12, 33",
        })
        @DisplayName("parametrized ranges")
        void parametrized(int from, int to, int expected) {
            assertThat(tasks.sumOfRange(from, to)).isEqualTo(expected);
        }
    }

    // ── Task 3: repeatText ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement repeatText()")
    @DisplayName("Task 3 — repeatText()")
    class RepeatTextTest {

        @Test
        @DisplayName("repeatText(\"ab\", 3) → [ab, ab, ab]")
        void basic() {
            assertThat(tasks.repeatText("ab", 3)).containsExactly("ab", "ab", "ab");
        }

        @Test
        @DisplayName("times 1 → single element")
        void once() {
            assertThat(tasks.repeatText("x", 1)).containsExactly("x");
        }

        @Test
        @DisplayName("times 0 → empty list")
        void zeroTimes() {
            assertThat(tasks.repeatText("x", 0)).isEmpty();
        }

        @Test
        @DisplayName("result has exactly `times` elements")
        void size() {
            assertThat(tasks.repeatText("z", 25)).hasSize(25);
        }
    }

    // ── Task 4: toCharacterList ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement toCharacterList()")
    @DisplayName("Task 4 — toCharacterList()")
    class ToCharacterListTest {

        @Test
        @DisplayName("\"abc\" → ['a','b','c']")
        void basic() {
            assertThat(tasks.toCharacterList("abc")).containsExactly('a', 'b', 'c');
        }

        @Test
        @DisplayName("repeated characters are kept")
        void duplicates() {
            assertThat(tasks.toCharacterList("aab")).containsExactly('a', 'a', 'b');
        }

        @Test
        @DisplayName("empty string → empty list")
        void emptyString() {
            assertThat(tasks.toCharacterList("")).isEmpty();
        }

        @Test
        @DisplayName("digits and spaces are characters too")
        void nonLetters() {
            assertThat(tasks.toCharacterList("a 1")).containsExactly('a', ' ', '1');
        }
    }

    // ── Task 5: concatLists ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement concatLists()")
    @DisplayName("Task 5 — concatLists()")
    class ConcatListsTest {

        @Test
        @DisplayName("[a,b] + [c] → [a,b,c]")
        void basic() {
            assertThat(tasks.concatLists(Arrays.asList("a", "b"), Arrays.asList("c")))
                    .containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("empty first list → copy of second")
        void emptyFirst() {
            assertThat(tasks.concatLists(new ArrayList<String>(), Arrays.asList("c")))
                    .containsExactly("c");
        }

        @Test
        @DisplayName("both empty → empty list")
        void bothEmpty() {
            assertThat(tasks.concatLists(new ArrayList<String>(), new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("input lists are not modified")
        void inputsUntouched() {
            List<String> first = new ArrayList<>(Arrays.asList("a", "b"));
            List<String> second = new ArrayList<>(Arrays.asList("c"));

            tasks.concatLists(first, second);

            assertThat(first).containsExactly("a", "b");
            assertThat(second).containsExactly("c");
        }

        @Test
        @DisplayName("returns a new list, not one of the inputs")
        void returnsNewList() {
            List<String> first = new ArrayList<>(Arrays.asList("a"));
            List<String> second = new ArrayList<>();

            List<String> result = tasks.concatLists(first, second);

            assertThat(result).isNotSameAs(first);
            assertThat(result).isNotSameAs(second);
        }
    }

    // ── Task 6: boxArray ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement boxArray()")
    @DisplayName("Task 6 — boxArray()")
    class BoxArrayTest {

        @Test
        @DisplayName("[3,1,2] → [3,1,2] (order kept, not sorted)")
        void basic() {
            assertThat(tasks.boxArray(new int[]{3, 1, 2})).containsExactly(3, 1, 2);
        }

        @Test
        @DisplayName("single element")
        void single() {
            assertThat(tasks.boxArray(new int[]{7})).containsExactly(7);
        }

        @Test
        @DisplayName("empty array → empty list")
        void empty() {
            assertThat(tasks.boxArray(new int[]{})).isEmpty();
        }

        @Test
        @DisplayName("every number is kept, not one array element")
        void notASingleArrayElement() {
            assertThat(tasks.boxArray(new int[]{1, 2, 3, 4})).hasSize(4);
        }
    }
}
