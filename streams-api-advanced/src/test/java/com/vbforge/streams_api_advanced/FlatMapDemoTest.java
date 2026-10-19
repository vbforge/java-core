package com.vbforge.streams_api_advanced;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FlatMapDemoTest — verifies every concept shown in {@link FlatMapDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of {@code flatMap}.
 */
@DisplayName("FlatMapDemo — API contracts")
class FlatMapDemoTest {

    // ── 1. map vs flatMap ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. map vs flatMap")
    class MapVsFlatMapTest {

        private final List<String> lines = Arrays.asList("to be or", "not to be");

        @Test
        @DisplayName("map is one-to-one: one array per line")
        void mapIsOneToOne() {
            assertThat(lines.stream().map(line -> line.split(" ")).count()).isEqualTo(2L);
        }

        @Test
        @DisplayName("flatMap is one-to-many: all words end up in one flat stream")
        void flatMapSplitsLines() {
            List<String> words = lines.stream()
                    .flatMap(line -> Arrays.stream(line.split(" ")))
                    .collect(Collectors.toList());
            assertThat(words).containsExactly("to", "be", "or", "not", "to", "be");
        }

        @Test
        @DisplayName("flatMap flattens a list of lists, including empty inner lists")
        void flattensNestedLists() {
            List<List<Integer>> nested = Arrays.asList(
                    Arrays.asList(1, 2), Arrays.<Integer>asList(), Arrays.asList(3, 4, 5));
            assertThat(nested.stream().flatMap(List::stream).collect(Collectors.toList()))
                    .containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("map over lists gives a Stream of lists — nothing is flattened")
        void mapDoesNotFlatten() {
            List<List<Integer>> nested = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3));
            assertThat(nested.stream().map(l -> l).count()).isEqualTo(2L);
            assertThat(nested.stream().flatMap(List::stream).count()).isEqualTo(3L);
        }

        @Test
        @DisplayName("\"\".split(\" \") returns one empty string, not an empty array")
        void splitEmptyStringGotcha() {
            assertThat("".split(" ").length).isEqualTo(1);
            assertThat("".split(" ")[0]).isEmpty();
        }

        @Test
        @DisplayName("filtering empty tokens removes the phantom word")
        void filterEmptyTokens() {
            List<String> cleaned = Stream.of("a b", "")
                    .flatMap(line -> Arrays.stream(line.split(" ")))
                    .filter(w -> !w.isEmpty())
                    .collect(Collectors.toList());
            assertThat(cleaned).containsExactly("a", "b");
        }
    }

    // ── 2. Dropping empty values ──────────────────────────────────────────────

    @Nested
    @DisplayName("2. Dropping empty values")
    class EmptyValuesTest {

        private final List<Optional<String>> optionals = Arrays.asList(
                Optional.of("a"), Optional.<String>empty(), Optional.of("c"));

        @Test
        @DisplayName("filter(isPresent) + map(get) unwraps present optionals")
        void filterAndGet() {
            List<String> values = optionals.stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            assertThat(values).containsExactly("a", "c");
        }

        @Test
        @DisplayName("flatMap to a zero-or-one element stream does the same in one step")
        void flatMapZeroOrOne() {
            List<String> values = optionals.stream()
                    .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.<String>empty())
                    .collect(Collectors.toList());
            assertThat(values).containsExactly("a", "c");
        }

        @Test
        @DisplayName("a mapper that returns null counts as an empty stream")
        void nullMapperResultIsEmpty() {
            List<Integer> values = Stream.of(1, 2, 3)
                    .flatMap(n -> n == 2 ? null : Stream.of(n))
                    .collect(Collectors.toList());
            assertThat(values).containsExactly(1, 3);
        }

        @Test
        @DisplayName("flatMap can also expand one element into several")
        void expandsElements() {
            List<Integer> values = Stream.of(1, 2)
                    .flatMap(n -> Stream.of(n, n * 10))
                    .collect(Collectors.toList());
            assertThat(values).containsExactly(1, 10, 2, 20);
        }
    }

    // ── 3. Combinations ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Combinations")
    class CombinationsTest {

        private final List<Integer> numbers = Arrays.asList(1, 2, 3);
        private final List<String> letters = Arrays.asList("x", "y");

        @Test
        @DisplayName("flatMap + map builds the cartesian product in nested-loop order")
        void cartesianProduct() {
            List<String> pairs = numbers.stream()
                    .flatMap(n -> letters.stream().map(l -> n + l))
                    .collect(Collectors.toList());
            assertThat(pairs).containsExactly("1x", "1y", "2x", "2y", "3x", "3y");
        }

        @Test
        @DisplayName("result size is outer.size() * inner.size()")
        void productSize() {
            long count = numbers.stream()
                    .flatMap(n -> letters.stream().map(l -> n + l))
                    .count();
            assertThat(count).isEqualTo(6L);
        }

        @Test
        @DisplayName("an empty inner source yields no combinations")
        void emptyInner() {
            long count = numbers.stream()
                    .flatMap(n -> Stream.<String>empty().map(l -> n + l))
                    .count();
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("using map at the outer level leaves nested streams — one per outer element")
        void mapAtOuterLevelDoesNotFlatten() {
            long outerElements = numbers.stream()
                    .map(n -> letters.stream().map(l -> n + l))
                    .count();
            assertThat(outerElements).isEqualTo(3L);
        }
    }

    // ── 4. flatMapToInt ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. flatMapToInt")
    class FlatMapToIntTest {

        private final List<String> words = Arrays.asList("hello", "world");

        @Test
        @DisplayName("flatMapToInt(String::chars) merges all characters into one IntStream")
        void allCharacters() {
            assertThat(words.stream().flatMapToInt(String::chars).count()).isEqualTo(10L);
        }

        @Test
        @DisplayName("map(String::chars) would give a Stream of IntStreams — one per word")
        void mapGivesStreamOfStreams() {
            assertThat(words.stream().map(String::chars).count()).isEqualTo(2L);
        }

        @Test
        @DisplayName("distinct + sorted + mapToObj((char) c) gives the sorted distinct letters")
        void distinctLetters() {
            String letters = words.stream()
                    .flatMapToInt(String::chars)
                    .distinct()
                    .sorted()
                    .mapToObj(c -> String.valueOf((char) c))
                    .collect(Collectors.joining());
            assertThat(letters).isEqualTo("dehlorw");
        }

        @Test
        @DisplayName("flatMapToInt flattens int[][]")
        void flattenIntArrays() {
            int[][] grid = {{1, 2}, {3}, {4, 5, 6}};
            assertThat(Arrays.stream(grid).flatMapToInt(Arrays::stream).sum()).isEqualTo(21);
        }

        @Test
        @DisplayName("an IntStream can be expanded with flatMap too")
        void intStreamFlatMap() {
            List<Integer> result = IntStream.rangeClosed(1, 3)
                    .flatMap(n -> IntStream.rangeClosed(1, n))
                    .boxed()
                    .collect(Collectors.toList());
            assertThat(result).containsExactly(1, 1, 2, 1, 2, 3);
        }
    }
}
