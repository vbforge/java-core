package com.vbforge.generics_fundamentals;

import com.vbforge.generics_fundamentals.GenericClassesDemo.Box;
import com.vbforge.generics_fundamentals.GenericClassesDemo.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GenericClassesTasks — your implementations")
class GenericClassesTasksTest {

    private GenericClassesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new GenericClassesTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement swap()")
    @DisplayName("Task 1 — swap(pair)")
    class SwapTest {

        @Test
        @DisplayName("example: (age, 30) becomes (30, age)")
        void example() {
            Pair<Integer, String> swapped = tasks.swap(new Pair<>("age", 30));

            assertThat(swapped.getFirst()).isEqualTo(30);
            assertThat(swapped.getSecond()).isEqualTo("age");
            assertThat(swapped).isEqualTo(new Pair<>(30, "age"));
        }

        @Test
        @DisplayName("example: (1, 2) becomes (2, 1)")
        void sameTypes() {
            assertThat(tasks.swap(new Pair<>(1, 2))).isEqualTo(new Pair<>(2, 1));
        }

        @Test
        @DisplayName("returns a new pair and leaves the original untouched")
        void returnsNewPair() {
            Pair<String, Integer> original = new Pair<>("a", 1);

            Pair<Integer, String> swapped = tasks.swap(original);

            assertThat(swapped).isNotSameAs(original);
            assertThat(original).isEqualTo(new Pair<>("a", 1));
        }

        @Test
        @DisplayName("null values are carried over")
        void nullValues() {
            Pair<String, String> swapped = tasks.swap(new Pair<>(null, "x"));

            assertThat(swapped.getFirst()).isEqualTo("x");
            assertThat(swapped.getSecond()).isNull();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement mapBox()")
    @DisplayName("Task 2 — mapBox(box, mapper)")
    class MapBoxTest {

        @Test
        @DisplayName("example: Box[hello] mapped with String::length is Box[5]")
        void lengthExample() {
            Box<Integer> result = tasks.mapBox(Box.of("hello"), String::length);

            assertThat(result).isEqualTo(Box.of(5));
        }

        @Test
        @DisplayName("example: Box[21] doubled is Box[42]")
        void doubleExample() {
            assertThat(tasks.mapBox(Box.of(21), n -> n * 2)).isEqualTo(Box.of(42));
        }

        @Test
        @DisplayName("example: an empty box stays empty")
        void emptyStaysEmpty() {
            Box<Integer> result = tasks.mapBox(Box.<String>empty(), String::length);

            assertThat(result.isPresent()).isFalse();
        }

        @Test
        @DisplayName("the mapper is not called for an empty box")
        void mapperNotCalledForEmpty() {
            Box<Integer> result = tasks.mapBox(Box.<String>empty(), s -> {
                throw new AssertionError("mapper must not be called for an empty box");
            });

            assertThat(result.isPresent()).isFalse();
        }

        @ParameterizedTest(name = "length of \"{0}\" is {1}")
        @CsvSource({"a, 1", "hello, 5", "generics, 8"})
        @DisplayName("works for several inputs")
        void parametrized(String text, int expectedLength) {
            assertThat(tasks.mapBox(Box.of(text), String::length).get()).isEqualTo(expectedLength);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstPresent()")
    @DisplayName("Task 3 — firstPresent(boxes)")
    class FirstPresentTest {

        @Test
        @DisplayName("example: skips empty boxes and returns the first present one")
        void example() {
            Box<String> expected = Box.of("b");
            List<Box<String>> boxes = Arrays.asList(Box.<String>empty(), expected, Box.of("c"));

            Box<String> result = tasks.firstPresent(boxes);

            assertThat(result).isSameAs(expected);
        }

        @Test
        @DisplayName("example: all boxes empty gives an empty box")
        void allEmpty() {
            List<Box<String>> boxes = Arrays.asList(Box.<String>empty(), Box.<String>empty());

            assertThat(tasks.firstPresent(boxes).isPresent()).isFalse();
        }

        @Test
        @DisplayName("example: empty list gives an empty box")
        void emptyList() {
            List<Box<String>> boxes = Collections.emptyList();

            assertThat(tasks.firstPresent(boxes).isPresent()).isFalse();
        }

        @Test
        @DisplayName("a single present box is returned as is")
        void singleBox() {
            Box<Integer> only = Box.of(1);

            assertThat(tasks.firstPresent(Collections.singletonList(only))).isSameAs(only);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement zip()")
    @DisplayName("Task 4 — zip(firsts, seconds)")
    class ZipTest {

        @Test
        @DisplayName("example: equal lengths")
        void equalLengths() {
            List<Pair<String, Integer>> result = tasks.zip(Arrays.asList("a", "b"), Arrays.asList(1, 2));

            assertThat(result).containsExactly(new Pair<>("a", 1), new Pair<>("b", 2));
        }

        @Test
        @DisplayName("example: the shorter list decides the length (second shorter)")
        void secondShorter() {
            List<Pair<String, Integer>> result = tasks.zip(Arrays.asList("a", "b", "c"), Arrays.asList(1, 2));

            assertThat(result).containsExactly(new Pair<>("a", 1), new Pair<>("b", 2));
        }

        @Test
        @DisplayName("the shorter list decides the length (first shorter)")
        void firstShorter() {
            List<Pair<Integer, String>> result = tasks.zip(Arrays.asList(1), Arrays.asList("x", "y"));

            assertThat(result).containsExactly(new Pair<>(1, "x"));
        }

        @Test
        @DisplayName("example: an empty list gives an empty result")
        void emptyInput() {
            List<Pair<String, Integer>> result = tasks.zip(Collections.<String>emptyList(), Arrays.asList(1, 2));

            assertThat(result).isEmpty();
        }
    }
}
