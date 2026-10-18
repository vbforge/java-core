package com.vbforge.streams_api_basics;

import com.vbforge.streams_api_basics.IntermediateOperationsDemo.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * IntermediateOperationsDemoTest — verifies every concept shown in
 * {@link IntermediateOperationsDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * the intermediate stream operations.
 */
@DisplayName("IntermediateOperationsDemo — API contracts")
class IntermediateOperationsDemoTest {

    private static final List<String> WORDS = Arrays.asList("pear", "fig", "apple", "kiwi", "plum");

    // ── 1. filter and map ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. filter and map")
    class FilterAndMapTest {

        @Test
        @DisplayName("filter keeps matching elements, map transforms them")
        void filterThenMap() {
            List<String> adultNames = IntermediateOperationsDemo.samplePeople().stream()
                    .filter(p -> p.getAge() >= 18)
                    .map(Person::getName)
                    .collect(Collectors.toList());
            assertThat(adultNames).containsExactly("Alice", "Carol", "Dan");
        }

        @Test
        @DisplayName("two filters in a row equal one filter with &&")
        void chainedFiltersEqualAnd() {
            List<Person> people = IntermediateOperationsDemo.samplePeople();

            List<String> chained = people.stream()
                    .filter(p -> p.getCity().equals("Kyiv"))
                    .filter(p -> p.getAge() >= 18)
                    .map(Person::getName)
                    .collect(Collectors.toList());
            List<String> combined = people.stream()
                    .filter(p -> p.getCity().equals("Kyiv") && p.getAge() >= 18)
                    .map(Person::getName)
                    .collect(Collectors.toList());

            assertThat(chained).containsExactly("Alice", "Carol");
            assertThat(chained).isEqualTo(combined);
        }

        @Test
        @DisplayName("map can change the element type (Person → String → Integer)")
        void mapChangesType() {
            List<Integer> lengths = IntermediateOperationsDemo.samplePeople().stream()
                    .map(Person::getName)
                    .map(String::length)
                    .collect(Collectors.toList());
            assertThat(lengths).containsExactly(5, 3, 5, 3, 3);
        }

        @Test
        @DisplayName("filter with no match gives an empty result, not an error")
        void filterNoMatch() {
            List<Person> none = IntermediateOperationsDemo.samplePeople().stream()
                    .filter(p -> p.getAge() > 100)
                    .collect(Collectors.toList());
            assertThat(none).isEmpty();
        }

        @Test
        @DisplayName("the source list is not modified")
        void sourceUntouched() {
            List<Person> people = IntermediateOperationsDemo.samplePeople();
            people.stream().filter(p -> p.getAge() >= 18).collect(Collectors.toList());
            assertThat(people).hasSize(5);
        }
    }

    // ── 2. mapToInt and boxed ─────────────────────────────────────────────────

    @Nested
    @DisplayName("2. mapToInt and boxed")
    class PrimitiveMappingTest {

        @Test
        @DisplayName("mapToInt(...).sum() adds up the ints")
        void sum() {
            int total = IntermediateOperationsDemo.samplePeople().stream()
                    .mapToInt(Person::getAge)
                    .sum();
            assertThat(total).isEqualTo(131);
        }

        @Test
        @DisplayName("average() returns an OptionalDouble")
        void average() {
            OptionalDouble average = IntermediateOperationsDemo.samplePeople().stream()
                    .mapToInt(Person::getAge)
                    .average();
            assertThat(average.isPresent()).isTrue();
            assertThat(average.getAsDouble()).isEqualTo(26.2);
        }

        @Test
        @DisplayName("average() of an empty stream is an empty OptionalDouble")
        void averageOfEmpty() {
            OptionalDouble average = IntermediateOperationsDemo.samplePeople().stream()
                    .filter(p -> p.getAge() > 100)
                    .mapToInt(Person::getAge)
                    .average();
            assertThat(average.isPresent()).isFalse();
        }

        @Test
        @DisplayName("boxed() turns an IntStream back into a Stream<Integer>")
        void boxed() {
            List<Integer> ages = IntermediateOperationsDemo.samplePeople().stream()
                    .mapToInt(Person::getAge)
                    .boxed()
                    .collect(Collectors.toList());
            assertThat(ages).containsExactly(30, 17, 45, 22, 17);
        }
    }

    // ── 3. sorted ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. sorted")
    class SortedTest {

        @Test
        @DisplayName("sorted() uses natural order")
        void naturalOrder() {
            assertThat(WORDS.stream().sorted().collect(Collectors.toList()))
                    .containsExactly("apple", "fig", "kiwi", "pear", "plum");
        }

        @Test
        @DisplayName("sorted(Comparator.comparing(...)) is stable for equal keys")
        void stableSort() {
            List<String> byLength = WORDS.stream()
                    .sorted(Comparator.comparing(String::length))
                    .collect(Collectors.toList());
            // pear, kiwi, plum all have length 4 and keep their input order
            assertThat(byLength).containsExactly("fig", "pear", "kiwi", "plum", "apple");
        }

        @Test
        @DisplayName("reversed() flips the comparator")
        void reversedComparator() {
            List<String> longestFirst = WORDS.stream()
                    .sorted(Comparator.comparing(String::length).reversed())
                    .collect(Collectors.toList());
            assertThat(longestFirst).containsExactly("apple", "pear", "kiwi", "plum", "fig");
        }

        @Test
        @DisplayName("thenComparing(...) breaks ties")
        void thenComparing() {
            List<String> result = WORDS.stream()
                    .sorted(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()))
                    .collect(Collectors.toList());
            assertThat(result).containsExactly("fig", "kiwi", "pear", "plum", "apple");
        }

        @Test
        @DisplayName("comparingInt(...).thenComparing(...) sorts objects by several fields")
        void sortObjectsByTwoFields() {
            List<String> names = IntermediateOperationsDemo.samplePeople().stream()
                    .sorted(Comparator.comparingInt(Person::getAge).thenComparing(Person::getName))
                    .map(Person::getName)
                    .collect(Collectors.toList());
            assertThat(names).containsExactly("Bob", "Eve", "Dan", "Alice", "Carol");
        }

        @Test
        @DisplayName("sorted() on non-Comparable elements fails at runtime with ClassCastException")
        void notComparableFailsAtRuntime() {
            Stream<Person> pipeline = IntermediateOperationsDemo.samplePeople().stream().sorted();
            // building the pipeline compiled and did not fail — the terminal operation does
            assertThatThrownBy(() -> pipeline.collect(Collectors.toList()))
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("sorted() leaves the source list in its original order")
        void sourceUntouched() {
            List<String> source = new ArrayList<>(WORDS);
            source.stream().sorted().collect(Collectors.toList());
            assertThat(source).isEqualTo(WORDS);
        }
    }

    // ── 4. distinct, skip, limit ──────────────────────────────────────────────

    @Nested
    @DisplayName("4. distinct, skip, limit")
    class DistinctSkipLimitTest {

        @Test
        @DisplayName("distinct() keeps the first occurrence of each element")
        void distinct() {
            assertThat(Stream.of(1, 2, 2, 3, 1).distinct().collect(Collectors.toList()))
                    .containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("distinct() compares with equals() — two equal Strings collapse")
        void distinctUsesEquals() {
            long count = Stream.of(new String("a"), new String("a")).distinct().count();
            assertThat(count).isEqualTo(1L);
        }

        @Test
        @DisplayName("distinct() removes nothing when equals() is not overridden")
        void distinctWithoutEquals() {
            long count = Stream.of(new Person("Zed", 1, "X"), new Person("Zed", 1, "X"))
                    .distinct()
                    .count();
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("skip(n) drops the first n elements")
        void skip() {
            assertThat(numbers().stream().skip(7).collect(Collectors.toList()))
                    .containsExactly(8, 9, 10);
        }

        @Test
        @DisplayName("limit(n) keeps at most the first n elements")
        void limit() {
            assertThat(numbers().stream().limit(3).collect(Collectors.toList()))
                    .containsExactly(1, 2, 3);
            assertThat(numbers().stream().limit(99).count()).isEqualTo(10L);
        }

        @Test
        @DisplayName("skip(n) past the end gives an empty stream")
        void skipPastEnd() {
            assertThat(numbers().stream().skip(50).collect(Collectors.toList())).isEmpty();
        }

        @Test
        @DisplayName("skip + limit implement pagination")
        void pagination() {
            int pageIndex = 1;
            int pageSize = 3;
            List<Integer> page = numbers().stream()
                    .skip((long) pageIndex * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());
            assertThat(page).containsExactly(4, 5, 6);
        }

        private List<Integer> numbers() {
            return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        }
    }

    // ── 5. Laziness ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Laziness")
    class LazinessTest {

        @Test
        @DisplayName("building a pipeline runs nothing until a terminal operation")
        void nothingRunsWithoutTerminal() {
            List<String> log = new ArrayList<>();
            Stream.of(1, 2, 3, 4)
                    .filter(n -> { log.add("filter " + n); return n % 2 == 0; })
                    .map(n -> { log.add("map " + n); return n * 10; });

            assertThat(log).isEmpty();
        }

        @Test
        @DisplayName("each element travels through the whole pipeline before the next one starts")
        void elementByElement() {
            List<String> log = new ArrayList<>();
            List<Integer> result = Stream.of(1, 2, 3, 4)
                    .filter(n -> { log.add("filter " + n); return n % 2 == 0; })
                    .map(n -> { log.add("map " + n); return n * 10; })
                    .collect(Collectors.toList());

            assertThat(result).containsExactly(20, 40);
            assertThat(log).containsExactly(
                    "filter 1", "filter 2", "map 2", "filter 3", "filter 4", "map 4");
        }

        @Test
        @DisplayName("limit(n) short-circuits: later elements are never pulled")
        void limitShortCircuits() {
            List<String> seen = new ArrayList<>();
            Stream.of(5, 6, 7, 8, 9)
                    .peek(n -> seen.add("seen " + n))
                    .limit(2)
                    .collect(Collectors.toList());

            assertThat(seen).containsExactly("seen 5", "seen 6");
        }

        @Test
        @DisplayName("sorted() is a barrier: it consumes every upstream element first")
        void sortedIsABarrier() {
            List<String> seen = new ArrayList<>();
            List<Integer> smallest = Stream.of(3, 1, 2)
                    .peek(n -> seen.add("seen " + n))
                    .sorted()
                    .limit(1)
                    .collect(Collectors.toList());

            assertThat(smallest).containsExactly(1);
            assertThat(seen).containsExactly("seen 3", "seen 1", "seen 2");
        }

        @Test
        @DisplayName("filter before an expensive map means fewer map calls")
        void filterBeforeMapDoesLessWork() {
            int[] mapCalls = {0};
            Stream.of(1, 2, 3, 4, 5, 6)
                    .filter(n -> n % 3 == 0)
                    .map(n -> { mapCalls[0]++; return n * n; })
                    .collect(Collectors.toList());

            assertThat(mapCalls[0]).isEqualTo(2);
        }
    }
}
