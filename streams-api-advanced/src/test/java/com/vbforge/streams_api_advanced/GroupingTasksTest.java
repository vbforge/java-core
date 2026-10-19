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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GroupingTasksTest — tests for every method in {@link GroupingTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code GroupingTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=GroupingTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("GroupingTasks — your implementations")
class GroupingTasksTest {

    private GroupingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new GroupingTasks();
    }

    // ── Task 1: groupByLength ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement groupByLength()")
    @DisplayName("Task 1 — groupByLength()")
    class GroupByLengthTest {

        @Test
        @DisplayName("[a,bb,c,dd,eee] → {1=[a,c], 2=[bb,dd], 3=[eee]}")
        void basic() {
            Map<Integer, List<String>> result =
                    tasks.groupByLength(Arrays.asList("a", "bb", "c", "dd", "eee"));
            assertThat(result).hasSize(3);
            assertThat(result.get(1)).containsExactly("a", "c");
            assertThat(result.get(2)).containsExactly("bb", "dd");
            assertThat(result.get(3)).containsExactly("eee");
        }

        @Test
        @DisplayName("single word")
        void single() {
            Map<Integer, List<String>> result = tasks.groupByLength(Arrays.asList("hi"));
            assertThat(result).hasSize(1);
            assertThat(result.get(2)).containsExactly("hi");
        }

        @Test
        @DisplayName("empty input → empty map")
        void emptyInput() {
            assertThat(tasks.groupByLength(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("duplicates stay in the group, in input order")
        void duplicatesKept() {
            Map<Integer, List<String>> result = tasks.groupByLength(Arrays.asList("ab", "cd", "ab"));
            assertThat(result.get(2)).containsExactly("ab", "cd", "ab");
        }
    }

    // ── Task 2: countByFirstLetter ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countByFirstLetter()")
    @DisplayName("Task 2 — countByFirstLetter()")
    class CountByFirstLetterTest {

        @Test
        @DisplayName("[Apple,avocado,Banana] → {a=2, b=1}")
        void basic() {
            Map<Character, Long> result =
                    tasks.countByFirstLetter(Arrays.asList("Apple", "avocado", "Banana"));
            assertThat(result).hasSize(2)
                              .containsEntry('a', 2L)
                              .containsEntry('b', 1L);
        }

        @Test
        @DisplayName("single word")
        void single() {
            assertThat(tasks.countByFirstLetter(Arrays.asList("x"))).containsEntry('x', 1L);
        }

        @Test
        @DisplayName("empty input → empty map")
        void emptyInput() {
            assertThat(tasks.countByFirstLetter(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("upper- and lower-case initials share one key")
        void caseInsensitive() {
            assertThat(tasks.countByFirstLetter(Arrays.asList("Cat", "car", "COW")))
                    .hasSize(1)
                    .containsEntry('c', 3L);
        }
    }

    // ── Task 3: partitionEvenOdd ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement partitionEvenOdd()")
    @DisplayName("Task 3 — partitionEvenOdd()")
    class PartitionEvenOddTest {

        @Test
        @DisplayName("[1,2,3,4] → {true=[2,4], false=[1,3]}")
        void basic() {
            Map<Boolean, List<Integer>> result = tasks.partitionEvenOdd(Arrays.asList(1, 2, 3, 4));
            assertThat(result.get(true)).containsExactly(2, 4);
            assertThat(result.get(false)).containsExactly(1, 3);
        }

        @Test
        @DisplayName("only evens: the false key is still present (empty list)")
        void onlyEvens() {
            Map<Boolean, List<Integer>> result = tasks.partitionEvenOdd(Arrays.asList(2, 4));
            assertThat(result).hasSize(2);
            assertThat(result.get(true)).containsExactly(2, 4);
            assertThat(result.get(false)).isEmpty();
        }

        @Test
        @DisplayName("empty input: both keys present with empty lists")
        void emptyInput() {
            Map<Boolean, List<Integer>> result = tasks.partitionEvenOdd(new ArrayList<Integer>());
            assertThat(result).hasSize(2);
            assertThat(result.get(true)).isEmpty();
            assertThat(result.get(false)).isEmpty();
        }

        @Test
        @DisplayName("negative numbers: -3 is odd, -4 is even")
        void negatives() {
            Map<Boolean, List<Integer>> result = tasks.partitionEvenOdd(Arrays.asList(-3, -4, 0));
            assertThat(result.get(true)).containsExactly(-4, 0);
            assertThat(result.get(false)).containsExactly(-3);
        }
    }

    // ── Task 4: joinByFirstLetter ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement joinByFirstLetter()")
    @DisplayName("Task 4 — joinByFirstLetter()")
    class JoinByFirstLetterTest {

        @Test
        @DisplayName("[apple,avocado,banana] → {a=\"apple,avocado\", b=\"banana\"}")
        void basic() {
            Map<Character, String> result =
                    tasks.joinByFirstLetter(Arrays.asList("apple", "avocado", "banana"));
            assertThat(result).hasSize(2)
                              .containsEntry('a', "apple,avocado")
                              .containsEntry('b', "banana");
        }

        @Test
        @DisplayName("initials are lower-cased, words keep their original case")
        void caseHandling() {
            assertThat(tasks.joinByFirstLetter(Arrays.asList("Cat", "car")))
                    .hasSize(1)
                    .containsEntry('c', "Cat,car");
        }

        @Test
        @DisplayName("empty input → empty map")
        void emptyInput() {
            assertThat(tasks.joinByFirstLetter(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("a group with one word has no comma")
        void singleWordGroup() {
            assertThat(tasks.joinByFirstLetter(Arrays.asList("zebra"))).containsEntry('z', "zebra");
        }
    }

    // ── Task 5: countLongWords ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countLongWords()")
    @DisplayName("Task 5 — countLongWords()")
    class CountLongWordsTest {

        @Test
        @DisplayName("[a,abc,abcd] with min 3 → {true=2, false=1}")
        void basic() {
            Map<Boolean, Long> result = tasks.countLongWords(Arrays.asList("a", "abc", "abcd"), 3);
            assertThat(result).containsEntry(true, 2L)
                              .containsEntry(false, 1L);
        }

        @Test
        @DisplayName("no long words: the true key is present with 0")
        void noLongWords() {
            Map<Boolean, Long> result = tasks.countLongWords(Arrays.asList("a", "b"), 3);
            assertThat(result).hasSize(2)
                              .containsEntry(true, 0L)
                              .containsEntry(false, 2L);
        }

        @Test
        @DisplayName("empty input: both keys present with 0")
        void emptyInput() {
            Map<Boolean, Long> result = tasks.countLongWords(new ArrayList<String>(), 3);
            assertThat(result).hasSize(2)
                              .containsEntry(true, 0L)
                              .containsEntry(false, 0L);
        }

        @ParameterizedTest(name = "min length {0} → {1} long, {2} short")
        @CsvSource({
                "1, 4, 0",
                "2, 3, 1",
                "4, 1, 3",
                "9, 0, 4",
        })
        @DisplayName("parametrized thresholds on [a,bb,ccc,dddd]")
        void parametrized(int minLength, long expectedLong, long expectedShort) {
            Map<Boolean, Long> result =
                    tasks.countLongWords(Arrays.asList("a", "bb", "ccc", "dddd"), minLength);
            assertThat(result).containsEntry(true, expectedLong)
                              .containsEntry(false, expectedShort);
        }
    }

    // ── Task 6: groupByLengthThenInitial ──────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement groupByLengthThenInitial()")
    @DisplayName("Task 6 — groupByLengthThenInitial()")
    class GroupByLengthThenInitialTest {

        @Test
        @DisplayName("[ab,ac,b,bb] → {1={b=[b]}, 2={a=[ab,ac], b=[bb]}}")
        void basic() {
            Map<Integer, Map<Character, List<String>>> result =
                    tasks.groupByLengthThenInitial(Arrays.asList("ab", "ac", "b", "bb"));

            assertThat(result).hasSize(2);
            assertThat(result.get(1)).hasSize(1);
            assertThat(result.get(1).get('b')).containsExactly("b");
            assertThat(result.get(2)).hasSize(2);
            assertThat(result.get(2).get('a')).containsExactly("ab", "ac");
            assertThat(result.get(2).get('b')).containsExactly("bb");
        }

        @Test
        @DisplayName("initials are lower-cased, words keep their case")
        void caseHandling() {
            Map<Integer, Map<Character, List<String>>> result =
                    tasks.groupByLengthThenInitial(Arrays.asList("Hi", "he"));
            assertThat(result).hasSize(1);
            assertThat(result.get(2).get('h')).containsExactly("Hi", "he");
        }

        @Test
        @DisplayName("empty input → empty map")
        void emptyInput() {
            assertThat(tasks.groupByLengthThenInitial(new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("an inner key exists only when a word needs it")
        void noEmptyInnerBuckets() {
            Map<Integer, Map<Character, List<String>>> result =
                    tasks.groupByLengthThenInitial(Arrays.asList("xy"));
            assertThat(result.get(2)).hasSize(1);
            assertThat(result.get(2).get('z')).isNull();
        }
    }
}
