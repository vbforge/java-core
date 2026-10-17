package com.vbforge.functional_interfaces;

import com.vbforge.functional_interfaces.FunctionalContractDemo.TriFunction;
import com.vbforge.functional_interfaces.FunctionalContractDemo.Validator;
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
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FunctionalContractTasks — your implementations")
class FunctionalContractTasksTest {

    private FunctionalContractTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new FunctionalContractTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement allOf()")
    @DisplayName("Task 1 — allOf(validators)")
    class AllOfTest {

        private final Validator<String> notEmpty = text -> !text.isEmpty();
        private final Validator<String> shortText = text -> text.length() <= 5;

        @ParameterizedTest(name = "allOf([notEmpty, shortText]).validate(\"{0}\") = {1}")
        @CsvSource({"abc, true", "toolong, false", "a, true"})
        @DisplayName("example: every validator must accept")
        void example(String value, boolean expected) {
            Validator<String> combined = tasks.allOf(Arrays.asList(notEmpty, shortText));

            assertThat(combined.validate(value)).isEqualTo(expected);
        }

        @Test
        @DisplayName("an empty string fails the notEmpty validator")
        void emptyStringRejected() {
            assertThat(tasks.allOf(Arrays.asList(notEmpty, shortText)).validate("")).isFalse();
        }

        @Test
        @DisplayName("example: an empty list accepts everything")
        void emptyList() {
            List<Validator<String>> none = Collections.emptyList();

            assertThat(tasks.allOf(none).validate("anything")).isTrue();
        }

        @Test
        @DisplayName("a single validator behaves like itself")
        void single() {
            Validator<Integer> positive = n -> n > 0;

            assertThat(tasks.allOf(Collections.singletonList(positive)).validate(5)).isTrue();
            assertThat(tasks.allOf(Collections.singletonList(positive)).validate(-5)).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement anyOf()")
    @DisplayName("Task 2 — anyOf(validators)")
    class AnyOfTest {

        private final Validator<String> isNumber = text -> text.matches("\\d+");
        private final Validator<String> isBlank = text -> text.trim().isEmpty();

        @Test
        @DisplayName("example: accepts when one validator accepts")
        void oneAccepts() {
            assertThat(tasks.anyOf(Arrays.asList(isNumber, isBlank)).validate("42")).isTrue();
            assertThat(tasks.anyOf(Arrays.asList(isNumber, isBlank)).validate("   ")).isTrue();
        }

        @Test
        @DisplayName("example: rejects when none accepts")
        void noneAccepts() {
            assertThat(tasks.anyOf(Arrays.asList(isNumber, isBlank)).validate("abc")).isFalse();
        }

        @Test
        @DisplayName("example: an empty list accepts nothing")
        void emptyList() {
            List<Validator<String>> none = Collections.emptyList();

            assertThat(tasks.anyOf(none).validate("anything")).isFalse();
        }

        @Test
        @DisplayName("stops at the first accepting validator")
        void shortCircuits() {
            Validator<String> accepts = text -> true;
            Validator<String> mustNotRun = text -> {
                throw new AssertionError("validator after the winner must not be called");
            };

            assertThat(tasks.anyOf(Arrays.asList(accepts, mustNotRun)).validate("x")).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement curry()")
    @DisplayName("Task 3 — curry(function)")
    class CurryTest {

        @Test
        @DisplayName("example: curried addition")
        void addition() {
            Function<Integer, Function<Integer, Function<Integer, Integer>>> curried =
                    tasks.curry((a, b, c) -> a + b + c);

            assertThat(curried.apply(1).apply(2).apply(3)).isEqualTo(6);
        }

        @Test
        @DisplayName("example: curried string joining keeps argument order")
        void joining() {
            Function<String, Function<String, Function<String, String>>> curried =
                    tasks.curry((a, b, c) -> a + "-" + b + "-" + c);

            assertThat(curried.apply("x").apply("y").apply("z")).isEqualTo("x-y-z");
        }

        @Test
        @DisplayName("a partially applied function can be reused")
        void partialApplication() {
            Function<Integer, Function<Integer, Function<Integer, Integer>>> curried =
                    tasks.curry((a, b, c) -> a * b + c);
            Function<Integer, Function<Integer, Integer>> timesTen = curried.apply(10);

            assertThat(timesTen.apply(2).apply(1)).isEqualTo(21);
            assertThat(timesTen.apply(3).apply(0)).isEqualTo(30);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement onError()")
    @DisplayName("Task 4 — onError(function, fallback)")
    class OnErrorTest {

        @Test
        @DisplayName("example: normal results pass through")
        void passThrough() {
            TriFunction<Integer, Integer, Integer, Integer> safe = tasks.onError((a, b, c) -> a / b / c, -1);

            assertThat(safe.apply(8, 2, 2)).isEqualTo(2);
        }

        @Test
        @DisplayName("example: a RuntimeException becomes the fallback")
        void exceptionBecomesFallback() {
            TriFunction<Integer, Integer, Integer, Integer> safe = tasks.onError((a, b, c) -> a / b / c, -1);

            assertThat(safe.apply(8, 0, 2)).isEqualTo(-1);
        }

        @Test
        @DisplayName("the wrapper can be used repeatedly")
        void reusable() {
            TriFunction<String, String, String, Integer> safe =
                    tasks.onError((a, b, c) -> Integer.parseInt(a + b + c), 0);

            assertThat(safe.apply("1", "2", "3")).isEqualTo(123);
            assertThat(safe.apply("x", "y", "z")).isEqualTo(0);
            assertThat(safe.apply("4", "5", "6")).isEqualTo(456);
        }
    }
}
