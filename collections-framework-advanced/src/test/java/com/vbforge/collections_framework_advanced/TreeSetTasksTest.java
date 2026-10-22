package com.vbforge.collections_framework_advanced;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TreeSetTasks — your implementations")
class TreeSetTasksTest {

    private TreeSetTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TreeSetTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement uniqueSortedWords()")
    @DisplayName("1. uniqueSortedWords(text)")
    class UniqueSortedWordsTest {

        @Test
        @DisplayName("example from Javadoc: distinct words, sorted, lower case")
        void javadocExample() {
            assertThat(tasks.uniqueSortedWords("the cat and The dog")).containsExactly("and", "cat", "dog", "the");
        }

        @Test
        @DisplayName("example from Javadoc: case and punctuation are ignored")
        void caseAndPunctuation() {
            assertThat(tasks.uniqueSortedWords("Hello, hello; HELLO!")).containsExactly("hello");
        }

        @Test
        @DisplayName("example from Javadoc: digits separate words")
        void digitsSeparate() {
            assertThat(tasks.uniqueSortedWords("a1b2")).containsExactly("a", "b");
        }

        @Test
        @DisplayName("example from Javadoc: empty text gives no words")
        void emptyText() {
            assertThat(tasks.uniqueSortedWords("")).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: text without letters gives no words")
        void noLetters() {
            assertThat(tasks.uniqueSortedWords("123 !!!")).isEmpty();
        }

        @Test
        @DisplayName("leading separators do not produce an empty word")
        void leadingSeparators() {
            assertThat(tasks.uniqueSortedWords("  ,b a")).containsExactly("a", "b");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement closest()")
    @DisplayName("2. closest(numbers, target)")
    class ClosestTest {

        private final TreeSet<Integer> numbers = new TreeSet<>(Arrays.asList(10, 20, 30));

        @ParameterizedTest(name = "closest to {0} is {1}")
        @CsvSource({
                "22, 20",
                "26, 30",
                "25, 20",
                "20, 20",
                "5,  10",
                "99, 30",
                "10, 10"
        })
        @DisplayName("examples from Javadoc and more (a tie returns the smaller element)")
        void closestValues(int target, int expected) {
            assertThat(tasks.closest(numbers, target)).contains(expected);
        }

        @Test
        @DisplayName("example from Javadoc: an empty set has no closest element")
        void emptySet() {
            assertThat(tasks.closest(new TreeSet<Integer>(), 5)).isEmpty();
        }

        @Test
        @DisplayName("a single element is always the closest one")
        void singleElement() {
            assertThat(tasks.closest(new TreeSet<>(Arrays.asList(42)), -1000)).contains(42);
        }

        @Test
        @DisplayName("negative numbers work too")
        void negatives() {
            assertThat(tasks.closest(new TreeSet<>(Arrays.asList(-10, -3, 4)), -5)).contains(-3);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countInRange()")
    @DisplayName("3. countInRange(numbers, fromInclusive, toExclusive)")
    class CountInRangeTest {

        private final TreeSet<Integer> numbers = new TreeSet<>(Arrays.asList(1, 3, 5, 7, 9));

        @ParameterizedTest(name = "[{0}, {1}) contains {2} elements")
        @CsvSource({
                "3, 8,   3",
                "3, 3,   0",
                "8, 3,   0",
                "0, 100, 5",
                "1, 2,   1",
                "9, 10,  1",
                "10, 20, 0"
        })
        @DisplayName("examples from Javadoc and more (reversed ranges must not throw)")
        void counts(int from, int to, int expected) {
            assertThat(tasks.countInRange(numbers, from, to)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example from Javadoc: an empty set contains nothing")
        void emptySet() {
            assertThat(tasks.countInRange(new TreeSet<Integer>(), 0, 10)).isZero();
        }

        @Test
        @DisplayName("the set itself is not modified")
        void setUntouched() {
            tasks.countInRange(numbers, 3, 8);

            assertThat(numbers).containsExactly(1, 3, 5, 7, 9);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement removeSmallest()")
    @DisplayName("4. removeSmallest(numbers, count)")
    class RemoveSmallestTest {

        @Test
        @DisplayName("example from Javadoc: removes and returns the two smallest")
        void javadocExample() {
            TreeSet<Integer> numbers = new TreeSet<>(Arrays.asList(5, 1, 9, 3));

            List<Integer> removed = tasks.removeSmallest(numbers, 2);

            assertThat(removed).containsExactly(1, 3);
            assertThat(numbers).containsExactly(5, 9);
        }

        @Test
        @DisplayName("example from Javadoc: asking for more than available removes everything")
        void moreThanAvailable() {
            TreeSet<Integer> numbers = new TreeSet<>(Arrays.asList(5, 1));

            List<Integer> removed = tasks.removeSmallest(numbers, 5);

            assertThat(removed).containsExactly(1, 5);
            assertThat(numbers).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: count 0 changes nothing")
        void zeroCount() {
            TreeSet<Integer> numbers = new TreeSet<>(Arrays.asList(5, 1));

            assertThat(tasks.removeSmallest(numbers, 0)).isEmpty();
            assertThat(numbers).containsExactly(1, 5);
        }

        @Test
        @DisplayName("a negative count changes nothing")
        void negativeCount() {
            TreeSet<Integer> numbers = new TreeSet<>(Arrays.asList(5, 1));

            assertThat(tasks.removeSmallest(numbers, -3)).isEmpty();
            assertThat(numbers).containsExactly(1, 5);
        }

        @Test
        @DisplayName("an empty set stays empty")
        void emptySet() {
            TreeSet<Integer> numbers = new TreeSet<>();

            assertThat(tasks.removeSmallest(numbers, 3)).isEmpty();
            assertThat(numbers).isEmpty();
        }
    }
}
