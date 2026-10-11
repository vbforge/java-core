package com.vbforge.unit_testing_junit5_assertj;

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
 * ParameterizedTasksTest — tests for every method in {@link ParameterizedTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ParameterizedTasks.java} and implement a method.</li>
 *   <li>Remove {@code @Disabled} from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ParameterizedTasksTest} — those tests should go GREEN ✅</li>
 * </ol>
 */
@DisplayName("ParameterizedTasks — your implementations")
class ParameterizedTasksTest {

    private ParameterizedTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ParameterizedTasks();
    }

    // ── Task 1: isPrime ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isPrime()")
    @DisplayName("Task 1 — isPrime()")
    class IsPrimeTest {

        @ParameterizedTest(name = "{0} is prime")
        @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 97})
        @DisplayName("prime numbers")
        void primes(int n) {
            assertThat(tasks.isPrime(n)).isTrue();
        }

        @ParameterizedTest(name = "{0} is not prime")
        @ValueSource(ints = {4, 6, 8, 9, 10, 15, 25, 49, 100})
        @DisplayName("composite numbers")
        void composites(int n) {
            assertThat(tasks.isPrime(n)).isFalse();
        }

        @ParameterizedTest(name = "{0} is not prime (edge)")
        @ValueSource(ints = {0, 1, -1, -7, -100})
        @DisplayName("edge cases below 2")
        void belowTwo(int n) {
            assertThat(tasks.isPrime(n)).isFalse();
        }
    }

    // ── Task 2: gcd ───────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement gcd()")
    @DisplayName("Task 2 — gcd()")
    class GcdTest {

        @ParameterizedTest(name = "gcd({0}, {1}) = {2}")
        @CsvSource({
                "12, 8, 4",
                "17, 5, 1",
                "48, 18, 6",
                "0, 5, 5",
                "5, 0, 5",
                "0, 0, 0",
                "-12, 8, 4",
                "100, 100, 100"
        })
        @DisplayName("gcd of various pairs")
        void gcd(int a, int b, int expected) {
            assertThat(tasks.gcd(a, b)).isEqualTo(expected);
        }
    }

    // ── Task 3: factorial ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement factorial()")
    @DisplayName("Task 3 — factorial()")
    class FactorialTest {

        @ParameterizedTest(name = "{0}! = {1}")
        @CsvSource({
                "0, 1",
                "1, 1",
                "2, 2",
                "3, 6",
                "4, 24",
                "5, 120",
                "10, 3628800"
        })
        @DisplayName("small factorials")
        void factorials(int n, long expected) {
            assertThat(tasks.factorial(n)).isEqualTo(expected);
        }

        @Test
        @DisplayName("20! fits in a long")
        void twenty() {
            assertThat(tasks.factorial(20)).isEqualTo(2432902008176640000L);
        }

        @Test
        @DisplayName("negative input throws IllegalArgumentException")
        void negativeInput() {
            assertThatThrownBy(() -> tasks.factorial(-1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("21 overflows a long and throws ArithmeticException")
        void overflow() {
            assertThatThrownBy(() -> tasks.factorial(21))
                    .isInstanceOf(ArithmeticException.class);
        }
    }

    // ── Task 4: maxOf ─────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement maxOf()")
    @DisplayName("Task 4 — maxOf()")
    class MaxOfTest {

        @Test
        @DisplayName("ascending array")
        void ascending() {
            assertThat(tasks.maxOf(new int[]{1, 2, 3})).isEqualTo(3);
        }

        @Test
        @DisplayName("descending array")
        void descending() {
            assertThat(tasks.maxOf(new int[]{3, 2, 1})).isEqualTo(3);
        }

        @Test
        @DisplayName("all negative")
        void allNegative() {
            assertThat(tasks.maxOf(new int[]{-5, -1, -10})).isEqualTo(-1);
        }

        @Test
        @DisplayName("single element")
        void single() {
            assertThat(tasks.maxOf(new int[]{42})).isEqualTo(42);
        }

        @Test
        @DisplayName("empty array throws IllegalArgumentException")
        void empty() {
            assertThatThrownBy(() -> tasks.maxOf(new int[]{}))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("null array throws IllegalArgumentException")
        void nullArray() {
            assertThatThrownBy(() -> tasks.maxOf(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}