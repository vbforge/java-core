package com.vbforge.mockito_and_tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TddCycleTasksTest — tests for every method in {@link TddCycleTasks}.
 *
 * <p><b>Getting started (TDD style — one red at a time):</b>
 * <ol>
 *   <li>Remove {@code @Disabled} from <em>one</em> {@code @Nested} class and run
 *       {@code mvn test -Dtest=TddCycleTasksTest} — watch it fail (red).</li>
 *   <li>Implement just enough in {@code TddCycleTasks.java} to pass (green).</li>
 *   <li>Refactor with the tests green, then move on to the next task.</li>
 *   <li>Repeat until all 3 task groups are GREEN.</li>
 * </ol>
 * The tests inside each group are ordered from simplest to hardest.
 */
@DisplayName("TddCycleTasks — your implementations")
class TddCycleTasksTest {

    private TddCycleTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TddCycleTasks();
    }

    // ── Task 1: fizzBuzz ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fizzBuzz()")
    @DisplayName("Task 1 — fizzBuzz()")
    class FizzBuzzTest {

        @Test
        @DisplayName("1 → \"1\"")
        void one() {
            assertThat(tasks.fizzBuzz(1)).isEqualTo("1");
        }

        @Test
        @DisplayName("3 → \"Fizz\"")
        void three() {
            assertThat(tasks.fizzBuzz(3)).isEqualTo("Fizz");
        }

        @Test
        @DisplayName("5 → \"Buzz\"")
        void five() {
            assertThat(tasks.fizzBuzz(5)).isEqualTo("Buzz");
        }

        @Test
        @DisplayName("15 → \"FizzBuzz\"")
        void fifteen() {
            assertThat(tasks.fizzBuzz(15)).isEqualTo("FizzBuzz");
        }

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "2,  2",
                "6,  Fizz",
                "9,  Fizz",
                "10, Buzz",
                "20, Buzz",
                "30, FizzBuzz",
                "45, FizzBuzz",
                "98, 98",
        })
        @DisplayName("parametrized cases")
        void parametrized(int n, String expected) {
            assertThat(tasks.fizzBuzz(n)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "fizzBuzz({0}) is rejected")
        @ValueSource(ints = {0, -1, -15})
        @DisplayName("zero and negatives → IllegalArgumentException")
        void invalid(int n) {
            assertThatThrownBy(() -> tasks.fizzBuzz(n)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Task 2: isValidPassword ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isValidPassword()")
    @DisplayName("Task 2 — isValidPassword()")
    class IsValidPasswordTest {

        @Test
        @DisplayName("\"Abcdef12\" is valid")
        void valid() {
            assertThat(tasks.isValidPassword("Abcdef12")).isTrue();
        }

        @Test
        @DisplayName("null → false")
        void nullPassword() {
            assertThat(tasks.isValidPassword(null)).isFalse();
        }

        @Test
        @DisplayName("empty → false")
        void empty() {
            assertThat(tasks.isValidPassword("")).isFalse();
        }

        @Test
        @DisplayName("too short → false")
        void tooShort() {
            assertThat(tasks.isValidPassword("Ab1")).isFalse();
        }

        @Test
        @DisplayName("7 characters is too short, 8 is enough")
        void lengthBoundary() {
            assertThat(tasks.isValidPassword("Abcde12")).isFalse();
            assertThat(tasks.isValidPassword("Abcdef12")).isTrue();
        }

        @Test
        @DisplayName("no digit → false")
        void noDigit() {
            assertThat(tasks.isValidPassword("Abcdefgh")).isFalse();
        }

        @Test
        @DisplayName("no uppercase → false")
        void noUppercase() {
            assertThat(tasks.isValidPassword("abcdefg1")).isFalse();
        }

        @Test
        @DisplayName("no lowercase → false")
        void noLowercase() {
            assertThat(tasks.isValidPassword("ABCDEFG1")).isFalse();
        }

        @Test
        @DisplayName("long passwords with symbols are fine")
        void longWithSymbols() {
            assertThat(tasks.isValidPassword("Correct-Horse-Battery-9")).isTrue();
        }
    }

    // ── Task 3: toRoman ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement toRoman()")
    @DisplayName("Task 3 — toRoman()")
    class ToRomanTest {

        @Test
        @DisplayName("1 → \"I\"")
        void one() {
            assertThat(tasks.toRoman(1)).isEqualTo("I");
        }

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "2,    II",
                "3,    III",
                "4,    IV",
                "5,    V",
                "9,    IX",
                "10,   X",
                "14,   XIV",
                "40,   XL",
                "58,   LVIII",
                "90,   XC",
                "400,  CD",
                "500,  D",
                "900,  CM",
                "1994, MCMXCIV",
                "2024, MMXXIV",
                "3999, MMMCMXCIX",
        })
        @DisplayName("parametrized cases")
        void parametrized(int number, String expected) {
            assertThat(tasks.toRoman(number)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "toRoman({0}) is rejected")
        @ValueSource(ints = {0, -5, 4000, 10_000})
        @DisplayName("outside 1..3999 → IllegalArgumentException")
        void outOfRange(int number) {
            assertThatThrownBy(() -> tasks.toRoman(number)).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
