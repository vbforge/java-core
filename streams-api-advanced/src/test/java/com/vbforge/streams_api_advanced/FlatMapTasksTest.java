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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FlatMapTasksTest — tests for every method in {@link FlatMapTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code FlatMapTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=FlatMapTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("FlatMapTasks — your implementations")
class FlatMapTasksTest {

    private FlatMapTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new FlatMapTasks();
    }

    // ── Task 1: allWords ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement allWords()")
    @DisplayName("Task 1 — allWords()")
    class AllWordsTest {

        @Test
        @DisplayName("[\"to be or\",\"not to be\"] → [to,be,or,not,to,be]")
        void basic() {
            assertThat(tasks.allWords(Arrays.asList("to be or", "not to be")))
                    .containsExactly("to", "be", "or", "not", "to", "be");
        }

        @Test
        @DisplayName("single one-word sentence")
        void singleWord() {
            assertThat(tasks.allWords(Arrays.asList("hello"))).containsExactly("hello");
        }

        @Test
        @DisplayName("an empty sentence contributes no words")
        void emptySentence() {
            assertThat(tasks.allWords(Arrays.asList("a b", ""))).containsExactly("a", "b");
        }

        @Test
        @DisplayName("only empty sentences → empty list")
        void onlyEmptySentences() {
            assertThat(tasks.allWords(Arrays.asList("", ""))).isEmpty();
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.allWords(new ArrayList<String>())).isEmpty();
        }
    }

    // ── Task 2: flatten ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement flatten()")
    @DisplayName("Task 2 — flatten()")
    class FlattenTest {

        @Test
        @DisplayName("[[1,2],[3],[],[4,5]] → [1,2,3,4,5]")
        void basic() {
            List<List<Integer>> nested = Arrays.asList(
                    Arrays.asList(1, 2), Arrays.asList(3), Arrays.<Integer>asList(), Arrays.asList(4, 5));
            assertThat(tasks.flatten(nested)).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("single inner list")
        void single() {
            assertThat(tasks.flatten(Arrays.asList(Arrays.asList(7)))).containsExactly(7);
        }

        @Test
        @DisplayName("only empty inner lists → empty list")
        void onlyEmptyInner() {
            List<List<Integer>> nested = Arrays.asList(Arrays.<Integer>asList(), Arrays.<Integer>asList());
            assertThat(tasks.flatten(nested)).isEmpty();
        }

        @Test
        @DisplayName("empty outer list → empty list")
        void emptyOuter() {
            assertThat(tasks.flatten(new ArrayList<List<Integer>>())).isEmpty();
        }

        @Test
        @DisplayName("duplicates are kept")
        void duplicatesKept() {
            assertThat(tasks.flatten(Arrays.asList(Arrays.asList(1, 1), Arrays.asList(1))))
                    .containsExactly(1, 1, 1);
        }
    }

    // ── Task 3: combine ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement combine()")
    @DisplayName("Task 3 — combine()")
    class CombineTest {

        @Test
        @DisplayName("[a,b] × [1,2] → [a1,a2,b1,b2]")
        void basic() {
            assertThat(tasks.combine(Arrays.asList("a", "b"), Arrays.asList("1", "2")))
                    .containsExactly("a1", "a2", "b1", "b2");
        }

        @Test
        @DisplayName("one element × two elements")
        void oneByTwo() {
            assertThat(tasks.combine(Arrays.asList("x"), Arrays.asList("1", "2")))
                    .containsExactly("x1", "x2");
        }

        @Test
        @DisplayName("empty second list → no combinations")
        void emptySecond() {
            assertThat(tasks.combine(Arrays.asList("a"), new ArrayList<String>())).isEmpty();
        }

        @Test
        @DisplayName("empty first list → no combinations")
        void emptyFirst() {
            assertThat(tasks.combine(new ArrayList<String>(), Arrays.asList("1"))).isEmpty();
        }

        @Test
        @DisplayName("result size is first.size() * second.size()")
        void resultSize() {
            assertThat(tasks.combine(Arrays.asList("a", "b", "c"), Arrays.asList("1", "2", "3", "4")))
                    .hasSize(12);
        }
    }

    // ── Task 4: presentValues ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement presentValues()")
    @DisplayName("Task 4 — presentValues()")
    class PresentValuesTest {

        @Test
        @DisplayName("[Optional[1], empty, Optional[3]] → [1,3]")
        void basic() {
            List<Optional<Integer>> input = Arrays.asList(
                    Optional.of(1), Optional.<Integer>empty(), Optional.of(3));
            assertThat(tasks.presentValues(input)).containsExactly(1, 3);
        }

        @Test
        @DisplayName("only empty optionals → empty list")
        void onlyEmpty() {
            List<Optional<Integer>> input = Arrays.asList(Optional.<Integer>empty());
            assertThat(tasks.presentValues(input)).isEmpty();
        }

        @Test
        @DisplayName("single present optional")
        void single() {
            List<Optional<Integer>> input = Arrays.asList(Optional.of(5));
            assertThat(tasks.presentValues(input)).containsExactly(5);
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.presentValues(new ArrayList<Optional<Integer>>())).isEmpty();
        }

        @Test
        @DisplayName("order of the present values is preserved")
        void orderPreserved() {
            List<Optional<Integer>> input = Arrays.asList(
                    Optional.of(9), Optional.<Integer>empty(), Optional.of(2), Optional.of(5));
            assertThat(tasks.presentValues(input)).containsExactly(9, 2, 5);
        }
    }

    // ── Task 5: distinctLetters ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement distinctLetters()")
    @DisplayName("Task 5 — distinctLetters()")
    class DistinctLettersTest {

        @Test
        @DisplayName("[hello, world] → [d,e,h,l,o,r,w]")
        void basic() {
            assertThat(tasks.distinctLetters(Arrays.asList("hello", "world")))
                    .containsExactly('d', 'e', 'h', 'l', 'o', 'r', 'w');
        }

        @Test
        @DisplayName("repeated letters across words appear once")
        void repeated() {
            assertThat(tasks.distinctLetters(Arrays.asList("aab", "b"))).containsExactly('a', 'b');
        }

        @Test
        @DisplayName("a list with only an empty word → empty list")
        void onlyEmptyWord() {
            assertThat(tasks.distinctLetters(Arrays.asList(""))).isEmpty();
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.distinctLetters(new ArrayList<String>())).isEmpty();
        }

        @ParameterizedTest(name = "[{0},{1}] → {2}")
        @CsvSource({
                "abc, cba, abc",
                "zz,  a,   az",
                "dog, god, dgo",
        })
        @DisplayName("parametrized pairs of words")
        void parametrized(String first, String second, String expectedLetters) {
            List<Character> result = tasks.distinctLetters(Arrays.asList(first, second));
            StringBuilder actual = new StringBuilder();
            for (Character c : result) {
                actual.append(c);
            }
            assertThat(actual.toString()).isEqualTo(expectedLetters);
        }
    }
}
