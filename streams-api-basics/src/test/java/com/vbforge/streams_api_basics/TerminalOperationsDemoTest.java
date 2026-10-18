package com.vbforge.streams_api_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TerminalOperationsDemoTest — verifies every concept shown in {@link TerminalOperationsDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * the terminal stream operations.
 */
@DisplayName("TerminalOperationsDemo — API contracts")
class TerminalOperationsDemoTest {

    private static final List<String> WORDS = Arrays.asList("apple", "banana", "cherry", "apple");

    // ── 1. forEach ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. forEach")
    class ForEachTest {

        @Test
        @DisplayName("forEachOrdered visits elements in encounter order")
        void forEachOrdered() {
            List<String> visited = new ArrayList<>();
            Arrays.asList("a", "b", "c").stream().forEachOrdered(visited::add);
            assertThat(visited).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("forEach is terminal: the stream cannot be used afterwards")
        void forEachIsTerminal() {
            Stream<String> stream = Stream.of("a", "b");
            stream.forEach(s -> { });

            assertThatThrownBy(() -> stream.forEach(s -> { }))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("collect builds the same list as forEach + outside mutation, without the side effect")
        void collectInsteadOfForEach() {
            List<String> viaForEach = new ArrayList<>();
            WORDS.stream().forEach(viaForEach::add);

            List<String> viaCollect = WORDS.stream().collect(Collectors.toList());

            assertThat(viaCollect).isEqualTo(viaForEach);
        }

        @Test
        @DisplayName("map + forEach: the transformed elements reach the consumer")
        void mapThenForEach() {
            List<String> upper = new ArrayList<>();
            Arrays.asList("a", "b", "c").stream().map(String::toUpperCase).forEach(upper::add);
            assertThat(upper).containsExactly("A", "B", "C");
        }
    }

    // ── 2. collect ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. collect")
    class CollectTest {

        @Test
        @DisplayName("toList() keeps order and duplicates")
        void toList() {
            assertThat(WORDS.stream().collect(Collectors.toList()))
                    .containsExactly("apple", "banana", "cherry", "apple");
        }

        @Test
        @DisplayName("toSet() drops duplicates")
        void toSet() {
            assertThat(WORDS.stream().collect(Collectors.toSet()))
                    .containsExactlyInAnyOrder("apple", "banana", "cherry");
        }

        @Test
        @DisplayName("toMap(key, value) throws IllegalStateException on a duplicate key")
        void toMapDuplicateKey() {
            assertThatThrownBy(() ->
                    WORDS.stream().collect(Collectors.toMap(Function.identity(), String::length)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Duplicate key");
        }

        @Test
        @DisplayName("toMap(key, value, merge, supplier) resolves duplicates and picks the map type")
        void toMapWithMerge() {
            TreeMap<String, Integer> lengths = WORDS.stream().collect(
                    Collectors.toMap(Function.identity(), String::length, (a, b) -> a, TreeMap::new));

            assertThat(lengths).containsEntry("apple", 5)
                               .containsEntry("banana", 6)
                               .containsEntry("cherry", 6)
                               .hasSize(3);
        }

        @Test
        @DisplayName("toMap with a summing merge function adds values of equal keys")
        void toMapSummingMerge() {
            TreeMap<String, Integer> counts = WORDS.stream().collect(
                    Collectors.toMap(Function.identity(), w -> 1, Integer::sum, TreeMap::new));

            assertThat(counts).containsEntry("apple", 2)
                              .containsEntry("banana", 1);
        }

        @Test
        @DisplayName("joining() concatenates; joining(delimiter, prefix, suffix) decorates")
        void joining() {
            assertThat(WORDS.stream().collect(Collectors.joining()))
                    .isEqualTo("applebananacherryapple");
            assertThat(WORDS.stream().collect(Collectors.joining(", ", "[", "]")))
                    .isEqualTo("[apple, banana, cherry, apple]");
        }

        @Test
        @DisplayName("joining() of an empty stream is the empty string")
        void joiningEmpty() {
            assertThat(Stream.<String>empty().collect(Collectors.joining(","))).isEmpty();
        }

        @Test
        @DisplayName("toCollection(TreeSet::new) gives a sorted set")
        void toCollection() {
            TreeSet<String> sorted = WORDS.stream().collect(Collectors.toCollection(TreeSet::new));
            assertThat(sorted).containsExactly("apple", "banana", "cherry");
        }
    }

    // ── 3. reduce ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. reduce")
    class ReduceTest {

        private final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        @Test
        @DisplayName("reduce(identity, accumulator) folds to a value")
        void identityForm() {
            assertThat(numbers.stream().reduce(0, Integer::sum)).isEqualTo(15);
        }

        @Test
        @DisplayName("reduce(accumulator) returns an Optional")
        void optionalForm() {
            Optional<Integer> max = numbers.stream().reduce(Integer::max);
            assertThat(max).hasValue(5);
        }

        @Test
        @DisplayName("empty stream: identity form returns the identity, Optional form returns empty")
        void emptyStream() {
            assertThat(Stream.<Integer>empty().reduce(0, Integer::sum)).isEqualTo(0);
            assertThat(Stream.<Integer>empty().reduce(Integer::max)).isEmpty();
        }

        @Test
        @DisplayName("String::concat folds strings from left to right")
        void concat() {
            assertThat(Stream.of("a", "b", "c").reduce("", String::concat)).isEqualTo("abc");
        }

        @Test
        @DisplayName("three-argument form: result type may differ from element type")
        void threeArgumentForm() {
            int totalLength = Stream.of("aa", "bbb", "c")
                    .reduce(0, (acc, s) -> acc + s.length(), Integer::sum);
            assertThat(totalLength).isEqualTo(6);
        }

        @Test
        @DisplayName("the identity of a product is 1")
        void productIdentity() {
            assertThat(numbers.stream().reduce(1, (a, b) -> a * b)).isEqualTo(120);
            assertThat(Stream.<Integer>empty().reduce(1, (a, b) -> a * b)).isEqualTo(1);
        }

        @Test
        @DisplayName("a wrong identity leaks into the result (sequential: added once)")
        void wrongIdentityLeaks() {
            assertThat(Stream.of(1, 2, 3).reduce(10, Integer::sum)).isEqualTo(16);
        }
    }

    // ── 4. match and find ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. match and find")
    class MatchAndFindTest {

        private final List<Integer> numbers = Arrays.asList(2, 4, 6, 8);

        @Test
        @DisplayName("anyMatch / allMatch / noneMatch answer yes-no questions")
        void basicMatches() {
            assertThat(numbers.stream().anyMatch(n -> n > 5)).isTrue();
            assertThat(numbers.stream().allMatch(n -> n % 2 == 0)).isTrue();
            assertThat(numbers.stream().noneMatch(n -> n > 10)).isTrue();
            assertThat(numbers.stream().allMatch(n -> n > 2)).isFalse();
        }

        @Test
        @DisplayName("on an empty stream allMatch and noneMatch are true, anyMatch is false")
        void vacuousTruth() {
            assertThat(Stream.<Integer>empty().allMatch(n -> false)).isTrue();
            assertThat(Stream.<Integer>empty().noneMatch(n -> true)).isTrue();
            assertThat(Stream.<Integer>empty().anyMatch(n -> true)).isFalse();
        }

        @Test
        @DisplayName("anyMatch short-circuits at the first match")
        void anyMatchShortCircuits() {
            int[] calls = {0};
            numbers.stream().anyMatch(n -> { calls[0]++; return n > 5; });
            assertThat(calls[0]).isEqualTo(3);   // 2, 4, 6 — never reaches 8
        }

        @Test
        @DisplayName("findFirst returns the first element in an Optional")
        void findFirst() {
            assertThat(numbers.stream().findFirst()).hasValue(2);
        }

        @Test
        @DisplayName("findFirst on no match returns an empty Optional; orElse supplies a default")
        void findFirstEmpty() {
            Optional<Integer> none = numbers.stream().filter(n -> n > 100).findFirst();
            assertThat(none).isEmpty();
            assertThat(none.orElse(-1)).isEqualTo(-1);
        }
    }

    // ── 5. count, min, max, statistics ────────────────────────────────────────

    @Nested
    @DisplayName("5. count, min, max, statistics")
    class AggregationTest {

        private final List<String> words = Arrays.asList("pear", "fig", "apple", "kiwi");

        @Test
        @DisplayName("count() returns a long")
        void count() {
            long longWords = words.stream().filter(w -> w.length() > 3).count();
            assertThat(longWords).isEqualTo(3L);
        }

        @Test
        @DisplayName("max(comparator) and min(comparator) return Optionals")
        void minMax() {
            assertThat(words.stream().max(Comparator.comparing(String::length))).hasValue("apple");
            assertThat(words.stream().min(Comparator.comparing(String::length))).hasValue("fig");
        }

        @Test
        @DisplayName("max of an empty stream is an empty Optional")
        void maxOfEmpty() {
            assertThat(Stream.<String>empty().max(Comparator.naturalOrder())).isEmpty();
        }

        @Test
        @DisplayName("summaryStatistics() computes count, sum, min, max and average in one pass")
        void summaryStatistics() {
            IntSummaryStatistics stats = IntStream.of(4, 8, 15, 16, 23, 42).summaryStatistics();

            assertThat(stats.getCount()).isEqualTo(6L);
            assertThat(stats.getSum()).isEqualTo(108L);
            assertThat(stats.getMin()).isEqualTo(4);
            assertThat(stats.getMax()).isEqualTo(42);
            assertThat(stats.getAverage()).isEqualTo(18.0);
        }

        @Test
        @DisplayName("empty IntStream: sum() is 0, max() and average() are empty")
        void emptyPrimitiveStream() {
            assertThat(IntStream.empty().sum()).isEqualTo(0);
            assertThat(IntStream.empty().max().isPresent()).isFalse();
            assertThat(IntStream.empty().average().isPresent()).isFalse();
        }
    }
}
