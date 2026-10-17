package com.vbforge.generics_fundamentals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GenericMethodsTasks — your implementations")
class GenericMethodsTasksTest {

    private GenericMethodsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new GenericMethodsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement swap()")
    @DisplayName("Task 1 — swap(array, i, j)")
    class SwapTest {

        @Test
        @DisplayName("example: swaps first and last of a String array")
        void example() {
            String[] letters = {"a", "b", "c"};

            tasks.swap(letters, 0, 2);

            assertThat(letters).containsExactly("c", "b", "a");
        }

        @Test
        @DisplayName("example: swapping an index with itself changes nothing")
        void sameIndex() {
            Integer[] numbers = {1, 2, 3};

            tasks.swap(numbers, 1, 1);

            assertThat(numbers).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("works for another element type and neighbouring indexes")
        void integerArray() {
            Integer[] numbers = {10, 20, 30, 40};

            tasks.swap(numbers, 1, 2);

            assertThat(numbers).containsExactly(10, 30, 20, 40);
        }

        @Test
        @DisplayName("argument order of the indexes does not matter")
        void reversedIndexes() {
            String[] letters = {"a", "b", "c"};

            tasks.swap(letters, 2, 0);

            assertThat(letters).containsExactly("c", "b", "a");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countOccurrences()")
    @DisplayName("Task 2 — countOccurrences(collection, target)")
    class CountOccurrencesTest {

        @ParameterizedTest(name = "count of \"{0}\" in [a, b, a] is {1}")
        @CsvSource({"a, 2", "b, 1", "z, 0"})
        @DisplayName("example: counts equal elements")
        void parametrized(String target, int expected) {
            assertThat(tasks.countOccurrences(Arrays.asList("a", "b", "a"), target)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example: null elements can be counted with a null target")
        void nullTarget() {
            assertThat(tasks.countOccurrences(Arrays.asList("a", null, null), null)).isEqualTo(2);
        }

        @Test
        @DisplayName("a null target does not match non-null elements")
        void nullTargetNoMatch() {
            assertThat(tasks.countOccurrences(Arrays.asList("a", "b"), null)).isEqualTo(0);
        }

        @Test
        @DisplayName("works for any Collection, such as a Set")
        void set() {
            assertThat(tasks.countOccurrences(new HashSet<>(Arrays.asList(1, 2, 3)), 2)).isEqualTo(1);
        }

        @Test
        @DisplayName("an empty collection has zero occurrences")
        void emptyCollection() {
            assertThat(tasks.countOccurrences(Collections.<String>emptyList(), "a")).isEqualTo(0);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement repeat()")
    @DisplayName("Task 3 — repeat(item, times)")
    class RepeatTest {

        @Test
        @DisplayName("example: repeats a String three times")
        void example() {
            assertThat(tasks.repeat("ab", 3)).containsExactly("ab", "ab", "ab");
        }

        @Test
        @DisplayName("example: one copy")
        void singleCopy() {
            assertThat(tasks.repeat(7, 1)).containsExactly(7);
        }

        @Test
        @DisplayName("example: zero times gives an empty list")
        void zeroTimes() {
            assertThat(tasks.repeat("x", 0)).isEmpty();
        }

        @Test
        @DisplayName("example: negative times throws IllegalArgumentException")
        void negativeTimes() {
            assertThatThrownBy(() -> tasks.repeat("x", -1)).isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "repeat(\"z\", {0}) has size {0}")
        @CsvSource({"0", "1", "5", "20"})
        @DisplayName("size equals the requested count")
        void sizeMatches(int times) {
            assertThat(tasks.repeat("z", times)).hasSize(times);
        }

        @Test
        @DisplayName("a null item may be repeated")
        void nullItem() {
            assertThat(tasks.repeat((String) null, 2)).containsExactly(null, null);
        }

        @Test
        @DisplayName("every call returns a new, modifiable list")
        void newModifiableList() {
            List<String> first = tasks.repeat("a", 2);
            List<String> second = tasks.repeat("a", 2);

            first.add("extra");

            assertThat(first).isNotSameAs(second);
            assertThat(second).hasSize(2);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement invertMap()")
    @DisplayName("Task 4 — invertMap(map)")
    class InvertMapTest {

        @Test
        @DisplayName("example: keys and values trade places")
        void example() {
            Map<String, Integer> input = new HashMap<>();
            input.put("one", 1);
            input.put("two", 2);

            Map<Integer, String> inverted = tasks.invertMap(input);

            assertThat(inverted).hasSize(2);
            assertThat(inverted).containsEntry(1, "one");
            assertThat(inverted).containsEntry(2, "two");
        }

        @Test
        @DisplayName("example: an empty map gives an empty map")
        void emptyMap() {
            assertThat(tasks.invertMap(new HashMap<String, Integer>())).isEmpty();
        }

        @Test
        @DisplayName("example: duplicate values throw IllegalArgumentException")
        void duplicateValues() {
            Map<String, Integer> input = new HashMap<>();
            input.put("a", 1);
            input.put("b", 1);

            assertThatThrownBy(() -> tasks.invertMap(input)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("returns a new map and leaves the input unchanged")
        void newMap() {
            Map<String, Integer> input = new HashMap<>();
            input.put("x", 9);

            Map<Integer, String> inverted = tasks.invertMap(input);

            assertThat(inverted).isNotSameAs(input);
            assertThat(input).containsEntry("x", 9).hasSize(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement removeDuplicates()")
    @DisplayName("Task 5 — removeDuplicates(list)")
    class RemoveDuplicatesTest {

        @Test
        @DisplayName("example: keeps the first occurrence and the original order")
        void example() {
            assertThat(tasks.removeDuplicates(Arrays.asList(3, 1, 3, 2, 1))).containsExactly(3, 1, 2);
        }

        @Test
        @DisplayName("example: all equal elements collapse into one")
        void allEqual() {
            assertThat(tasks.removeDuplicates(Arrays.asList("a", "a"))).containsExactly("a");
        }

        @Test
        @DisplayName("example: empty list gives an empty list")
        void emptyList() {
            assertThat(tasks.removeDuplicates(Collections.<String>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("a list without duplicates is returned in the same order")
        void noDuplicates() {
            assertThat(tasks.removeDuplicates(Arrays.asList("c", "a", "b"))).containsExactly("c", "a", "b");
        }

        @Test
        @DisplayName("returns a new list and does not modify the input")
        void inputUnchanged() {
            List<Integer> input = Arrays.asList(1, 1, 2);

            List<Integer> result = tasks.removeDuplicates(input);

            assertThat(result).isNotSameAs(input);
            assertThat(input).containsExactly(1, 1, 2);
        }
    }
}
