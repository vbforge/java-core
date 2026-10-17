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
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LambdasTasks — your implementations")
class LambdasTasksTest {

    private LambdasTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new LambdasTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement multiplier()")
    @DisplayName("Task 1 — multiplier(factor)")
    class MultiplierTest {

        @ParameterizedTest(name = "multiplier({0}).apply({1}) = {2}")
        @CsvSource({"3, 5, 15", "0, 99, 0", "-2, 4, -8", "1, 7, 7"})
        @DisplayName("examples: multiplies by the factor")
        void examples(int factor, int value, int expected) {
            assertThat(tasks.multiplier(factor).apply(value)).isEqualTo(expected);
        }

        @Test
        @DisplayName("each call returns an independent function")
        void independentFunctions() {
            assertThat(tasks.multiplier(2).apply(5)).isEqualTo(10);
            assertThat(tasks.multiplier(3).apply(5)).isEqualTo(15);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement squareSuppliers()")
    @DisplayName("Task 2 — squareSuppliers(count)")
    class SquareSuppliersTest {

        @Test
        @DisplayName("example: four suppliers return 0, 1, 4, 9")
        void four() {
            List<Supplier<Integer>> suppliers = tasks.squareSuppliers(4);

            assertThat(suppliers).hasSize(4);
            assertThat(suppliers.get(0).get()).isEqualTo(0);
            assertThat(suppliers.get(1).get()).isEqualTo(1);
            assertThat(suppliers.get(2).get()).isEqualTo(4);
            assertThat(suppliers.get(3).get()).isEqualTo(9);
        }

        @Test
        @DisplayName("example: one supplier returns 0")
        void one() {
            assertThat(tasks.squareSuppliers(1).get(0).get()).isEqualTo(0);
        }

        @Test
        @DisplayName("example: zero suppliers gives an empty list")
        void zero() {
            assertThat(tasks.squareSuppliers(0)).isEmpty();
        }

        @Test
        @DisplayName("a supplier returns the same value every time it is called")
        void stableResult() {
            Supplier<Integer> third = tasks.squareSuppliers(5).get(3);

            assertThat(third.get()).isEqualTo(9);
            assertThat(third.get()).isEqualTo(9);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement once()")
    @DisplayName("Task 3 — once(source)")
    class OnceTest {

        @Test
        @DisplayName("the source is not called until the wrapper is called")
        void lazy() {
            AtomicInteger calls = new AtomicInteger();

            tasks.once(() -> {
                calls.incrementAndGet();
                return "value";
            });

            assertThat(calls.get()).isEqualTo(0);
        }

        @Test
        @DisplayName("example: the source runs once and the result is reused")
        void calledOnce() {
            AtomicInteger calls = new AtomicInteger();
            Supplier<Integer> wrapped = tasks.once(calls::incrementAndGet);

            assertThat(wrapped.get()).isEqualTo(1);
            assertThat(wrapped.get()).isEqualTo(1);
            assertThat(wrapped.get()).isEqualTo(1);
            assertThat(calls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("a null result is remembered too")
        void nullRemembered() {
            AtomicInteger calls = new AtomicInteger();
            Supplier<String> wrapped = tasks.once(() -> {
                calls.incrementAndGet();
                return null;
            });

            assertThat(wrapped.get()).isNull();
            assertThat(wrapped.get()).isNull();
            assertThat(calls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("two wrappers keep separate results")
        void independentWrappers() {
            AtomicInteger counter = new AtomicInteger();
            Supplier<Integer> first = tasks.once(counter::incrementAndGet);
            Supplier<Integer> second = tasks.once(counter::incrementAndGet);

            assertThat(first.get()).isEqualTo(1);
            assertThat(second.get()).isEqualTo(2);
            assertThat(first.get()).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement operation()")
    @DisplayName("Task 4 — operation(symbol)")
    class OperationTest {

        @ParameterizedTest(name = "operation(\"{0}\").apply({1}, {2}) = {3}")
        @CsvSource({"+, 2, 3, 5", "-, 2, 3, -1", "*, 2, 3, 6", "/, 7, 2, 3", "/, -7, 2, -3"})
        @DisplayName("examples: the four operators")
        void operators(String symbol, int a, int b, int expected) {
            assertThat(tasks.operation(symbol).apply(a, b)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example: an unknown symbol throws IllegalArgumentException")
        void unknownSymbol() {
            assertThatThrownBy(() -> tasks.operation("%")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.operation("")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("division by zero throws ArithmeticException when the function is applied")
        void divisionByZero() {
            assertThatThrownBy(() -> tasks.operation("/").apply(1, 0)).isInstanceOf(ArithmeticException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement byLengthThenAlphabetical()")
    @DisplayName("Task 5 — byLengthThenAlphabetical()")
    class ComparatorTest {

        @Test
        @DisplayName("example: length first, then alphabetical")
        void example() {
            List<String> words = new ArrayList<>(Arrays.asList("pear", "fig", "apple", "kiwi"));

            Collections.sort(words, tasks.byLengthThenAlphabetical());

            assertThat(words).containsExactly("fig", "kiwi", "pear", "apple");
        }

        @Test
        @DisplayName("example: equal lengths are ordered alphabetically")
        void alphabeticalTieBreak() {
            List<String> words = new ArrayList<>(Arrays.asList("b", "a"));

            Collections.sort(words, tasks.byLengthThenAlphabetical());

            assertThat(words).containsExactly("a", "b");
        }

        @Test
        @DisplayName("equal strings compare as zero")
        void equalStrings() {
            assertThat(tasks.byLengthThenAlphabetical().compare("same", "same")).isEqualTo(0);
        }

        @Test
        @DisplayName("a shorter string comes first even if it sorts later alphabetically")
        void lengthBeatsAlphabet() {
            assertThat(tasks.byLengthThenAlphabetical().compare("z", "aa")).isNegative();
        }
    }
}
