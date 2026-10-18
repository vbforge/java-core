package com.vbforge.streams_api_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * StreamCreationDemoTest — verifies every concept shown in {@link StreamCreationDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how streams are created.
 */
@DisplayName("StreamCreationDemo — API contracts")
class StreamCreationDemoTest {

    // ── 1. Sources ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Stream sources")
    class SourcesTest {

        @Test
        @DisplayName("list.stream() follows the list's encounter order")
        void fromList() {
            List<String> names = Arrays.asList("Alice", "Bob", "Carol");
            assertThat(names.stream().collect(Collectors.toList()))
                    .containsExactly("Alice", "Bob", "Carol");
        }

        @Test
        @DisplayName("set.stream() contains each unique element once")
        void fromSet() {
            Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 2, 3));
            assertThat(set.stream().count()).isEqualTo(3L);
        }

        @Test
        @DisplayName("a Map is streamed through keySet(), values() or entrySet()")
        void fromMapViews() {
            Map<String, Integer> ages = new HashMap<>();
            ages.put("Bob", 25);
            ages.put("Alice", 30);

            assertThat(ages.keySet().stream().sorted().collect(Collectors.toList()))
                    .containsExactly("Alice", "Bob");
            assertThat(ages.values().stream().mapToInt(Integer::intValue).sum()).isEqualTo(55);
            assertThat(ages.entrySet().stream().count()).isEqualTo(2L);
        }

        @Test
        @DisplayName("Stream.of(...) streams the given values")
        void streamOf() {
            assertThat(Stream.of("x", "y", "z").collect(Collectors.toList()))
                    .containsExactly("x", "y", "z");
        }

        @Test
        @DisplayName("Arrays.stream(array) streams the whole array")
        void arraysStream() {
            String[] letters = {"a", "b", "c", "d"};
            assertThat(Arrays.stream(letters).collect(Collectors.toList()))
                    .containsExactly("a", "b", "c", "d");
        }

        @Test
        @DisplayName("Arrays.stream(array, from, to) streams the [from, to) slice")
        void arraysStreamSlice() {
            String[] letters = {"a", "b", "c", "d"};
            assertThat(Arrays.stream(letters, 1, 3).collect(Collectors.toList()))
                    .containsExactly("b", "c");
        }

        @Test
        @DisplayName("Stream.empty() has no elements")
        void emptyStream() {
            assertThat(Stream.empty().count()).isZero();
        }

        @Test
        @DisplayName("Stream.concat() glues two streams together in order")
        void concat() {
            Stream<Integer> glued = Stream.concat(Stream.of(1, 2), Stream.of(3, 4));
            assertThat(glued.collect(Collectors.toList())).containsExactly(1, 2, 3, 4);
        }

        @Test
        @DisplayName("Stream.of(int[]) is ONE element; Arrays.stream(int[]) is one per number")
        void streamOfIntArrayGotcha() {
            int[] numbers = {10, 20, 30};
            assertThat(Stream.of(numbers).count()).isEqualTo(1L);
            assertThat(Arrays.stream(numbers).count()).isEqualTo(3L);
        }
    }

    // ── 2. Infinite streams ───────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Infinite streams")
    class InfiniteStreamsTest {

        @Test
        @DisplayName("iterate(seed, f) + limit builds a sequence from the previous element")
        void iterateWithLimit() {
            List<Integer> powers = Stream.iterate(1, n -> n * 2)
                    .limit(8)
                    .collect(Collectors.toList());
            assertThat(powers).containsExactly(1, 2, 4, 8, 16, 32, 64, 128);
        }

        @Test
        @DisplayName("generate(supplier) + limit calls the supplier for every element")
        void generateWithLimit() {
            List<String> greetings = Stream.generate(() -> "hi")
                    .limit(3)
                    .collect(Collectors.toList());
            assertThat(greetings).containsExactly("hi", "hi", "hi");
        }

        @Test
        @DisplayName("limit(0) yields an empty stream")
        void limitZero() {
            assertThat(Stream.iterate(1, n -> n + 1).limit(0).collect(Collectors.toList())).isEmpty();
        }

        @Test
        @DisplayName("skip + limit cut a window out of an infinite stream")
        void skipAndLimit() {
            List<Integer> window = Stream.iterate(1, n -> n + 1)
                    .skip(2)
                    .limit(3)
                    .collect(Collectors.toList());
            assertThat(window).containsExactly(3, 4, 5);
        }

        @Test
        @DisplayName("a short-circuiting terminal operation ends an infinite stream without limit")
        void shortCircuitingTerminal() {
            assertThat(Stream.iterate(1, n -> n + 1).anyMatch(n -> n % 7 == 0)).isTrue();

            Optional<Integer> firstBig = Stream.iterate(1, n -> n + 1)
                    .filter(n -> n * n > 50)
                    .findFirst();
            assertThat(firstBig).hasValue(8);
        }
    }

    // ── 3. Primitive streams ──────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Primitive streams")
    class PrimitiveStreamsTest {

        @Test
        @DisplayName("IntStream.range(a, b) excludes b")
        void rangeExcludesEnd() {
            assertThat(IntStream.range(0, 5).boxed().collect(Collectors.toList()))
                    .containsExactly(0, 1, 2, 3, 4);
        }

        @Test
        @DisplayName("IntStream.rangeClosed(a, b) includes b")
        void rangeClosedIncludesEnd() {
            assertThat(IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList()))
                    .containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("IntStream offers sum() and max() directly")
        void numericHelpers() {
            assertThat(IntStream.rangeClosed(1, 5).sum()).isEqualTo(15);
            assertThat(IntStream.of(3, 1, 2).max().getAsInt()).isEqualTo(3);
        }

        @Test
        @DisplayName("an empty range sums to zero")
        void emptyRangeSum() {
            assertThat(IntStream.rangeClosed(5, 1).sum()).isZero();
        }

        @Test
        @DisplayName("String.chars() yields int code units, not chars")
        void charsAreInts() {
            assertThat("abc".chars().boxed().collect(Collectors.toList()))
                    .containsExactly(97, 98, 99);
        }

        @Test
        @DisplayName("mapToObj((char) c) turns code units back into characters")
        void charsBackToCharacters() {
            List<Character> chars = "abc".chars()
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.toList());
            assertThat(chars).containsExactly('a', 'b', 'c');
        }

        @Test
        @DisplayName("Arrays.stream(int[]).boxed() converts int[] to List<Integer>")
        void boxedFromArray() {
            int[] raw = {3, 1, 2};
            assertThat(Arrays.stream(raw).boxed().collect(Collectors.toList()))
                    .containsExactly(3, 1, 2);
        }
    }

    // ── 4. Single use ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Single use")
    class SingleUseTest {

        @Test
        @DisplayName("a second terminal operation throws IllegalStateException")
        void secondTerminalThrows() {
            Stream<String> stream = Stream.of("a", "b", "c");
            assertThat(stream.count()).isEqualTo(3L);

            assertThatThrownBy(stream::count)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already been operated upon or closed");
        }

        @Test
        @DisplayName("adding an intermediate operation to a consumed stream also throws")
        void reusingConsumedStreamForIntermediate() {
            Stream<String> stream = Stream.of("a", "b");
            stream.count();

            assertThatThrownBy(() -> stream.map(String::toUpperCase))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("a Supplier<Stream<T>> gives a fresh stream per use")
        void supplierGivesFreshStreams() {
            Supplier<Stream<String>> supplier = () -> Stream.of("a", "b", "c");
            assertThat(supplier.get().count()).isEqualTo(3L);
            assertThat(supplier.get().count()).isEqualTo(3L);
        }

        @Test
        @DisplayName("sorted() works on a copy — the source list is not modified")
        void sourceIsNotModified() {
            List<Integer> source = Arrays.asList(3, 1, 2);
            List<Integer> sorted = source.stream().sorted().collect(Collectors.toList());

            assertThat(sorted).containsExactly(1, 2, 3);
            assertThat(source).containsExactly(3, 1, 2);
        }
    }
}
