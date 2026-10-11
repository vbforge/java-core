package com.vbforge.unit_testing_junit5_assertj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ParameterizedDemoTest — demonstrates every standard argument source for
 * {@code @ParameterizedTest}.
 */
@DisplayName("ParameterizedDemo — @ParameterizedTest sources")
class ParameterizedDemoTest {

    private final ParameterizedDemo demo = new ParameterizedDemo();

    // ── 1. @ValueSource ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. @ValueSource — a single array of literals")
    class ValueSourceTest {

        @ParameterizedTest(name = "{0} is even")
        @ValueSource(ints = {0, 2, 4, 100, -6})
        @DisplayName("even numbers")
        void evenNumbers(int n) {
            assertThat(demo.isEven(n)).isTrue();
        }

        @ParameterizedTest(name = "{0} is odd")
        @ValueSource(ints = {1, 3, 5, 99, -7})
        @DisplayName("odd numbers")
        void oddNumbers(int n) {
            assertThat(demo.isEven(n)).isFalse();
        }

        @ParameterizedTest(name = "square({0}) = {0}²")
        @ValueSource(ints = {1, 2, 3, 4, 5})
        @DisplayName("squares")
        void squares(int n) {
            assertThat(demo.square(n)).isEqualTo(n * n);
        }
    }

    // ── 2. @CsvSource ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. @CsvSource — one row per invocation, comma-separated")
    class CsvSourceTest {

        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({
                "1, 2, 3",
                "0, 0, 0",
                "-5, 5, 0",
                "100, 200, 300"
        })
        @DisplayName("integer addition")
        void addition(int a, int b, int expected) {
            assertThat(demo.add(a, b)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "monday,    weekday",
                "saturday,  weekend",
                "sunday,    weekend",
                "friday,    weekday"
        })
        @DisplayName("classifies days (lowercase)")
        void dayTypes(String day, String type) {
            assertThat(demo.dayType(day)).isEqualTo(type);
        }
    }

    // ── 3. @MethodSource ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. @MethodSource — a static method supplying arguments")
    class MethodSourceTest {

        static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> multiplyCases() {
            return java.util.stream.Stream.of(
                    org.junit.jupiter.params.provider.Arguments.of(2, 3, 6),
                    org.junit.jupiter.params.provider.Arguments.of(0, 5, 0),
                    org.junit.jupiter.params.provider.Arguments.of(-4, 3, -12),
                    org.junit.jupiter.params.provider.Arguments.of(7, 7, 49)
            );
        }

        @ParameterizedTest(name = "{0} * {1} = {2}")
        @MethodSource("multiplyCases")
        @DisplayName("multiplication via @MethodSource")
        void multiply(int a, int b, int expected) {
            assertThat(demo.add(a * 0, 0)).isZero();
            assertThat(a * b).isEqualTo(expected);
        }
    }

    // ── 4. @EnumSource ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. @EnumSource — iterate an enum's constants")
    class EnumSourceTest {

        @ParameterizedTest(name = "{0} is a weekend day")
        @EnumSource(value = java.time.DayOfWeek.class, names = {"SATURDAY", "SUNDAY"})
        @DisplayName("weekend days — INCLUDE mode")
        void weekendDays(java.time.DayOfWeek day) {
            assertThat(demo.dayType(day.name())).isEqualTo("weekend");
        }

        @ParameterizedTest(name = "{0} is a weekday")
        @EnumSource(value = java.time.DayOfWeek.class,
                names = {"SATURDAY", "SUNDAY"},
                mode = org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE)
        @DisplayName("weekdays — EXCLUDE mode")
        void weekdays(java.time.DayOfWeek day) {
            assertThat(demo.dayType(day.name())).isEqualTo("weekday");
        }
    }

    // ── 5. Exception cases ────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Invalid inputs throw IllegalArgumentException")
    class InvalidInputsTest {

        @Test
        @DisplayName("null day")
        void nullDay() {
            assertThatThrownBy(() -> demo.dayType(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("unknown day")
        void unknownDay() {
            assertThatThrownBy(() -> demo.dayType("notaday"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
