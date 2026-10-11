package com.vbforge.unit_testing_junit5_assertj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AssertionsAndExceptionsTasksTest — tests for every method in
 * {@link AssertionsAndExceptionsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code AssertionsAndExceptionsTasks.java} and implement a method.</li>
 *   <li>Remove {@code @Disabled} from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=AssertionsAndExceptionsTasksTest} — those tests should go GREEN ✅</li>
 * </ol>
 */
@DisplayName("AssertionsAndExceptionsTasks — your implementations")
class AssertionsAndExceptionsTasksTest {

    private AssertionsAndExceptionsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new AssertionsAndExceptionsTasks();
    }

    // ── Task 1: divide ────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement divide()")
    @DisplayName("Task 1 — divide()")
    class DivideTest {

        @ParameterizedTest(name = "{0} / {1} = {2}")
        @CsvSource({
                "10, 2, 5",
                "7, 2, 3",
                "-10, 2, -5",
                "0, 5, 0"
        })
        @DisplayName("valid divisions")
        void divides(int a, int b, int expected) {
            assertThat(tasks.divide(a, b)).isEqualTo(expected);
        }

        @Test
        @DisplayName("division by zero throws ArithmeticException")
        void divideByZero() {
            assertThatThrownBy(() -> tasks.divide(5, 0))
                    .isInstanceOf(ArithmeticException.class);
        }
    }

    // ── Task 2: parseAge ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement parseAge()")
    @DisplayName("Task 2 — parseAge()")
    class ParseAgeTest {

        @ParameterizedTest(name = "\"{0}\" → {1}")
        @CsvSource({
                "42,     42",
                "'  7  ', 7",
                "0,      0",
                "150,    150"
        })
        @DisplayName("valid ages")
        void validAges(String input, int expected) {
            assertThat(tasks.parseAge(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("null throws IllegalArgumentException")
        void nullInput() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> tasks.parseAge(null));
        }

        @Test
        @DisplayName("non-numeric throws IllegalArgumentException")
        void nonNumeric() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> tasks.parseAge("abc"));
        }

        @Test
        @DisplayName("negative age throws IllegalArgumentException")
        void negativeAge() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> tasks.parseAge("-1"));
        }

        @Test
        @DisplayName("age above 150 throws IllegalArgumentException")
        void aboveMax() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> tasks.parseAge("151"));
        }
    }

    // ── Task 3: firstElement ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement firstElement()")
    @DisplayName("Task 3 — firstElement()")
    class FirstElementTest {

        @Test
        @DisplayName("returns first of multi-element list")
        void multiElement() {
            assertThat(tasks.firstElement(List.of("a", "b", "c"))).isEqualTo("a");
        }

        @Test
        @DisplayName("returns the only element")
        void singleElement() {
            assertThat(tasks.firstElement(List.of("only"))).isEqualTo("only");
        }

        @Test
        @DisplayName("empty list throws NoSuchElementException")
        void emptyList() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> tasks.firstElement(List.of()));
        }

        @Test
        @DisplayName("null throws NoSuchElementException")
        void nullList() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> tasks.firstElement(null));
        }
    }

    // ── Task 4: clamp ─────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement clamp()")
    @DisplayName("Task 4 — clamp()")
    class ClampTest {

        @ParameterizedTest(name = "clamp({0}, {1}, {2}) = {3}")
        @CsvSource({
                "5,  0, 10,  5",
                "-3, 0, 10,  0",
                "42, 0, 10, 10",
                "0,  0, 0,   0",
                "-5, -10, -1, -5"
        })
        @DisplayName("various clamps")
        void clamps(int value, int min, int max, int expected) {
            assertThat(tasks.clamp(value, min, max)).isEqualTo(expected);
        }

        @Test
        @DisplayName("min > max throws IllegalArgumentException")
        void invalidRange() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> tasks.clamp(1, 5, 2));
        }
    }
}