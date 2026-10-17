package com.vbforge.functional_interfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BuiltInInterfacesTasks — your implementations")
class BuiltInInterfacesTasksTest {

    private BuiltInInterfacesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new BuiltInInterfacesTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement noneOf()")
    @DisplayName("Task 1 — noneOf(predicates)")
    class NoneOfTest {

        private final Predicate<String> isEmpty = String::isEmpty;
        private final Predicate<String> isBlank = text -> text.trim().isEmpty();

        @ParameterizedTest(name = "noneOf([isEmpty, isBlank]).test(\"{0}\") = {1}")
        @CsvSource({"abc, true", "x, true"})
        @DisplayName("example: true when nothing matches")
        void nothingMatches(String value, boolean expected) {
            assertThat(tasks.noneOf(Arrays.asList(isEmpty, isBlank)).test(value)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example: false when any predicate matches")
        void somethingMatches() {
            assertThat(tasks.noneOf(Arrays.asList(isEmpty, isBlank)).test("")).isFalse();
            assertThat(tasks.noneOf(Arrays.asList(isEmpty, isBlank)).test("   ")).isFalse();
        }

        @Test
        @DisplayName("example: an empty list is always true")
        void emptyList() {
            List<Predicate<String>> none = Collections.emptyList();

            assertThat(tasks.noneOf(none).test("anything")).isTrue();
        }

        @Test
        @DisplayName("stops at the first matching predicate")
        void shortCircuits() {
            Predicate<String> matches = text -> true;
            Predicate<String> mustNotRun = text -> {
                throw new AssertionError("predicate after the match must not be called");
            };

            assertThat(tasks.noneOf(Arrays.asList(matches, mustNotRun)).test("x")).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement pipeline()")
    @DisplayName("Task 2 — pipeline(steps)")
    class PipelineTest {

        @Test
        @DisplayName("example: trim then upper-case")
        void strings() {
            Function<String, String> trim = String::trim;
            Function<String, String> upper = String::toUpperCase;

            assertThat(tasks.pipeline(Arrays.asList(trim, upper)).apply("  ab ")).isEqualTo("AB");
        }

        @Test
        @DisplayName("example: the first step runs first (plusOne, then timesTwo)")
        void order() {
            Function<Integer, Integer> plusOne = n -> n + 1;
            Function<Integer, Integer> timesTwo = n -> n * 2;

            assertThat(tasks.pipeline(Arrays.asList(plusOne, timesTwo)).apply(5)).isEqualTo(12);
            assertThat(tasks.pipeline(Arrays.asList(timesTwo, plusOne)).apply(5)).isEqualTo(11);
        }

        @Test
        @DisplayName("example: an empty list returns the argument unchanged")
        void emptyList() {
            List<Function<String, String>> none = Collections.emptyList();

            assertThat(tasks.pipeline(none).apply("same")).isEqualTo("same");
        }

        @Test
        @DisplayName("a single step behaves like that step")
        void singleStep() {
            Function<Integer, Integer> square = n -> n * n;

            assertThat(tasks.pipeline(Collections.singletonList(square)).apply(6)).isEqualTo(36);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement broadcast()")
    @DisplayName("Task 3 — broadcast(consumers)")
    class BroadcastTest {

        @Test
        @DisplayName("example: every consumer receives the value, in list order")
        void example() {
            List<String> log = new ArrayList<>();
            Consumer<String> first = text -> log.add("A:" + text);
            Consumer<String> second = text -> log.add("B:" + text);

            tasks.broadcast(Arrays.asList(first, second)).accept("x");

            assertThat(log).containsExactly("A:x", "B:x");
        }

        @Test
        @DisplayName("example: an empty list does nothing")
        void emptyList() {
            List<Consumer<String>> none = Collections.emptyList();

            tasks.broadcast(none).accept("x");
        }

        @Test
        @DisplayName("nothing is called until the returned consumer is used")
        void lazy() {
            AtomicInteger calls = new AtomicInteger();
            Consumer<String> counting = text -> calls.incrementAndGet();

            Consumer<String> broadcast = tasks.broadcast(Collections.singletonList(counting));

            assertThat(calls.get()).isEqualTo(0);
            broadcast.accept("x");
            broadcast.accept("y");
            assertThat(calls.get()).isEqualTo(2);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstNonNull()")
    @DisplayName("Task 4 — firstNonNull(suppliers)")
    class FirstNonNullTest {

        @Test
        @DisplayName("example: returns the first non-null result and skips the rest")
        void example() {
            AtomicInteger thirdCalls = new AtomicInteger();
            Supplier<String> first = () -> null;
            Supplier<String> second = () -> "b";
            Supplier<String> third = () -> {
                thirdCalls.incrementAndGet();
                return "c";
            };

            String result = tasks.firstNonNull(Arrays.asList(first, second, third));

            assertThat(result).isEqualTo("b");
            assertThat(thirdCalls.get()).isEqualTo(0);
        }

        @Test
        @DisplayName("example: all null gives null")
        void allNull() {
            Supplier<String> nothing = () -> null;

            assertThat(tasks.firstNonNull(Arrays.asList(nothing, nothing))).isNull();
        }

        @Test
        @DisplayName("example: an empty list gives null")
        void emptyList() {
            List<Supplier<String>> none = Collections.emptyList();

            assertThat(tasks.firstNonNull(none)).isNull();
        }

        @Test
        @DisplayName("each supplier is called at most once")
        void calledOnce() {
            AtomicInteger calls = new AtomicInteger();
            Supplier<Integer> counting = () -> {
                calls.incrementAndGet();
                return 7;
            };

            assertThat(tasks.firstNonNull(Collections.singletonList(counting))).isEqualTo(7);
            assertThat(calls.get()).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumIf()")
    @DisplayName("Task 5 — sumIf(values, filter)")
    class SumIfTest {

        @Test
        @DisplayName("example: sums the even numbers")
        void evens() {
            assertThat(tasks.sumIf(new int[]{1, 2, 3, 4}, n -> n % 2 == 0)).isEqualTo(6);
        }

        @Test
        @DisplayName("example: no value matches")
        void noneMatch() {
            assertThat(tasks.sumIf(new int[]{1, 2, 3}, n -> n > 10)).isEqualTo(0);
        }

        @Test
        @DisplayName("example: empty array gives 0")
        void emptyArray() {
            assertThat(tasks.sumIf(new int[0], n -> true)).isEqualTo(0);
        }

        @Test
        @DisplayName("negative numbers are handled")
        void negatives() {
            assertThat(tasks.sumIf(new int[]{-5, 3, -1, 8}, n -> n < 0)).isEqualTo(-6);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement mapAndFilter()")
    @DisplayName("Task 6 — mapAndFilter(input, mapper, filter)")
    class MapAndFilterTest {

        @Test
        @DisplayName("example: lengths greater than one")
        void lengths() {
            List<Integer> result = tasks.mapAndFilter(Arrays.asList("a", "bb", "ccc"), String::length, n -> n > 1);

            assertThat(result).containsExactly(2, 3);
        }

        @Test
        @DisplayName("example: squares below five")
        void squares() {
            List<Integer> result = tasks.mapAndFilter(Arrays.asList(1, 2, 3), n -> n * n, n -> n < 5);

            assertThat(result).containsExactly(1, 4);
        }

        @Test
        @DisplayName("example: empty input gives empty output")
        void emptyInput() {
            List<Integer> result = tasks.mapAndFilter(Collections.<Integer>emptyList(), x -> x, x -> true);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("the predicate sees the converted value, not the original")
        void predicateTestsMappedValue() {
            List<String> result = tasks.mapAndFilter(Arrays.asList(1, 2, 3), n -> "n" + n, text -> text.equals("n2"));

            assertThat(result).containsExactly("n2");
        }

        @Test
        @DisplayName("returns a new list")
        void newList() {
            List<Integer> input = Arrays.asList(1, 2);

            List<Integer> result = tasks.mapAndFilter(input, n -> n, n -> true);

            assertThat(result).isNotSameAs(input);
        }
    }
}
