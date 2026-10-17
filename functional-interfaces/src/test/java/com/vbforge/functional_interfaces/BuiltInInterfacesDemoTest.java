package com.vbforge.functional_interfaces;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("BuiltInInterfacesDemo — API contracts")
class BuiltInInterfacesDemoTest {

    @Nested
    @DisplayName("1. Predicate<T>")
    class PredicateSection {

        @ParameterizedTest(name = "longWordStartingWithA().test(\"{0}\") = {1}")
        @CsvSource({"apple, true", "ant, false", "pear, false", "avocado, true"})
        @DisplayName("and() requires both conditions")
        void andRule(String word, boolean expected) {
            assertThat(BuiltInInterfacesDemo.longWordStartingWithA().test(word)).isEqualTo(expected);
        }

        @Test
        @DisplayName("negate() flips blank detection")
        void notBlank() {
            assertThat(BuiltInInterfacesDemo.notBlank().test("abc")).isTrue();
            assertThat(BuiltInInterfacesDemo.notBlank().test("   ")).isFalse();
        }

        @Test
        @DisplayName("or() accepts when either predicate matches")
        void orRule() {
            Predicate<String> combined = BuiltInInterfacesDemo.longWordStartingWithA().or(word -> word.equals("pear"));

            assertThat(combined.test("pear")).isTrue();
            assertThat(combined.test("apple")).isTrue();
            assertThat(combined.test("fig")).isFalse();
        }

        @Test
        @DisplayName("Predicate.isEqual matches equal values only")
        void isEqual() {
            assertThat(Predicate.isEqual("x").test("x")).isTrue();
            assertThat(Predicate.isEqual("x").test("y")).isFalse();
        }

        @Test
        @DisplayName("and() short-circuits: the second predicate does not run after a false")
        void andShortCircuits() {
            Predicate<String> first = word -> false;
            Predicate<String> second = word -> {
                throw new AssertionError("must not run");
            };

            assertThat(first.and(second).test("x")).isFalse();
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(BuiltInInterfacesDemo::predicateDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("2. Function, UnaryOperator, BinaryOperator")
    class FunctionSection {

        @Test
        @DisplayName("compose runs the argument first, andThen runs it last")
        void composeVsAndThen() {
            int[] results = BuiltInInterfacesDemo.composeVsAndThen(5);

            assertThat(results[0]).isEqualTo(11);    // plusOne(timesTwo(5))
            assertThat(results[1]).isEqualTo(12);    // timesTwo(plusOne(5))
        }

        @ParameterizedTest(name = "x = {0}: compose = {1}, andThen = {2}")
        @CsvSource({"0, 1, 2", "1, 3, 4", "10, 21, 22", "-3, -5, -4"})
        @DisplayName("the two orders always differ by exactly one")
        void orderDifference(int x, int composed, int chained) {
            int[] results = BuiltInInterfacesDemo.composeVsAndThen(x);

            assertThat(results[0]).isEqualTo(composed);
            assertThat(results[1]).isEqualTo(chained);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(BuiltInInterfacesDemo::functionDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("3. Consumer and Supplier")
    class ConsumerSupplierSection {

        @Test
        @DisplayName("Consumer.andThen runs both consumers in order")
        void consumerChain() {
            assertThat(BuiltInInterfacesDemo.consumerChain("abc")).containsExactly("ABC", "3");
        }

        @Test
        @DisplayName("the fallback supplier is not called when a value exists")
        void fallbackNotCalled() {
            assertThat(BuiltInInterfacesDemo.fallbackCalls("x")).isEqualTo(0);
        }

        @Test
        @DisplayName("the fallback supplier is called exactly once when the value is null")
        void fallbackCalledOnce() {
            assertThat(BuiltInInterfacesDemo.fallbackCalls(null)).isEqualTo(1);
        }

        @Test
        @DisplayName("valueOrElseGet returns the value or the fallback result")
        void valueOrElseGet() {
            assertThat(BuiltInInterfacesDemo.valueOrElseGet("present", () -> "fallback")).isEqualTo("present");
            assertThat(BuiltInInterfacesDemo.valueOrElseGet(null, () -> "fallback")).isEqualTo("fallback");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(BuiltInInterfacesDemo::consumerSupplierDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. Primitive specialisations")
    class PrimitiveSection {

        private final int[] numbers = {3, 8, 1, 6};

        @Test
        @DisplayName("reduceInts sums with an IntBinaryOperator")
        void sum() {
            assertThat(BuiltInInterfacesDemo.reduceInts(numbers, 0, (a, b) -> a + b)).isEqualTo(18);
        }

        @Test
        @DisplayName("reduceInts finds the maximum with Math::max")
        void max() {
            assertThat(BuiltInInterfacesDemo.reduceInts(numbers, Integer.MIN_VALUE, Math::max)).isEqualTo(8);
        }

        @Test
        @DisplayName("reduceInts of an empty array is the identity")
        void emptyArray() {
            assertThat(BuiltInInterfacesDemo.reduceInts(new int[0], 7, (a, b) -> a + b)).isEqualTo(7);
        }

        @Test
        @DisplayName("countMatching counts values accepted by an IntPredicate")
        void countMatching() {
            assertThat(BuiltInInterfacesDemo.countMatching(numbers, n -> n % 2 == 0)).isEqualTo(2);
            assertThat(BuiltInInterfacesDemo.countMatching(numbers, n -> n > 100)).isEqualTo(0);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(BuiltInInterfacesDemo::primitiveDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("5. Higher-order functions")
    class HigherOrderSection {

        @Test
        @DisplayName("applyTwice applies the operator two times")
        void applyTwice() {
            assertThat(BuiltInInterfacesDemo.applyTwice(x -> x * 3, 2)).isEqualTo(18);
            assertThat(BuiltInInterfacesDemo.applyTwice(text -> text + "!", "hi")).isEqualTo("hi!!");
        }

        @Test
        @DisplayName("filter keeps only the elements the predicate accepts, in order")
        void filter() {
            List<String> words = Arrays.asList("kiwi", "fig", "plum");

            assertThat(BuiltInInterfacesDemo.filter(words, word -> word.length() == 4)).containsExactly("kiwi", "plum");
        }

        @Test
        @DisplayName("filter of an empty list is empty")
        void filterEmpty() {
            assertThat(BuiltInInterfacesDemo.filter(Collections.<String>emptyList(), word -> true)).isEmpty();
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(BuiltInInterfacesDemo::higherOrderDemo).doesNotThrowAnyException();
        }
    }
}
