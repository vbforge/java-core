package com.vbforge.basics.operators;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 🧪 Tests for {@link OperatorsTasks}.
 *
 * <p>All tests are RED until you implement the corresponding method.
 * Your goal: make every test GREEN without modifying this file.
 */
@Disabled("Tasks not implemented yet — remove this annotation when ready to solve")
@DisplayName("Operators Tasks — implement to make tests pass")
class OperatorsTasksTest {

    private final OperatorsTasks tasks = new OperatorsTasks();


    // ── Task 1 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 1 — isDivisibleByBoth(n, a, b)")
    class Task1 {

        @Test
        @DisplayName("returns true when n is divisible by both a and b")
        void divisibleByBoth() {
            assertThat(tasks.isDivisibleByBoth(12, 3, 4)).isTrue();
            assertThat(tasks.isDivisibleByBoth(60, 4, 5)).isTrue();
            assertThat(tasks.isDivisibleByBoth(0, 3, 7)).isTrue();
        }

        @Test
        @DisplayName("returns false when n is divisible by only one of the two")
        void divisibleByOneOnly() {
            assertThat(tasks.isDivisibleByBoth(12, 3, 5)).isFalse();
            assertThat(tasks.isDivisibleByBoth(10, 5, 3)).isFalse();
        }

        @Test
        @DisplayName("returns false when n is not divisible by either")
        void notDivisibleByEither() {
            assertThat(tasks.isDivisibleByBoth(7, 3, 4)).isFalse();
        }

        @ParameterizedTest(name = "isDivisibleByBoth({0}, {1}, {2}) = {3}")
        @CsvSource({
            "12,  3, 4, true",
            "12,  3, 5, false",
            "30,  5, 6, true",
            "30,  5, 7, false",
            " 0,  9, 7, true"
        })
        @DisplayName("parameterized divisibility checks")
        void parameterized(int n, int a, int b, boolean expected) {
            assertThat(tasks.isDivisibleByBoth(n, a, b)).isEqualTo(expected);
        }
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 2 — intAverage(a, b, c)")
    class Task2 {

        @Test
        @DisplayName("averages that divide evenly")
        void exact() {
            assertThat(tasks.intAverage(1, 2, 3)).isEqualTo(2);
            assertThat(tasks.intAverage(0, 0, 0)).isEqualTo(0);
            assertThat(tasks.intAverage(3, 3, 3)).isEqualTo(3);
        }

        @Test
        @DisplayName("result is truncated toward zero when not exact")
        void truncated() {
            assertThat(tasks.intAverage(1, 2, 4)).isEqualTo(2);  // 7/3 = 2
            assertThat(tasks.intAverage(0, 0, 1)).isEqualTo(0);  // 1/3 = 0
            assertThat(tasks.intAverage(1, 1, 1)).isEqualTo(1);
        }

        @Test
        @DisplayName("works with negative numbers")
        void negatives() {
            assertThat(tasks.intAverage(-3, -3, -3)).isEqualTo(-3);
            assertThat(tasks.intAverage(-1, 0, 1)).isEqualTo(0);
        }

        @ParameterizedTest(name = "intAverage({0}, {1}, {2}) = {3}")
        @CsvSource({
            " 1,  2,  3, 2",
            "10, 20, 30, 20",
            "-6, -3,  0, -3",
            " 1,  1,  2, 1"
        })
        @DisplayName("parameterized average scenarios")
        void parameterized(int a, int b, int c, int expected) {
            assertThat(tasks.intAverage(a, b, c)).isEqualTo(expected);
        }
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 3 — absoluteValue(n) — ternary only, no Math.abs or if/else")
    class Task3 {

        @Test
        @DisplayName("positive number returns itself")
        void positiveUnchanged() {
            assertThat(tasks.absoluteValue(7)).isEqualTo(7);
            assertThat(tasks.absoluteValue(100)).isEqualTo(100);
        }

        @Test
        @DisplayName("negative number returns its positive equivalent")
        void negativeFlipped() {
            assertThat(tasks.absoluteValue(-7)).isEqualTo(7);
            assertThat(tasks.absoluteValue(-100)).isEqualTo(100);
        }

        @Test
        @DisplayName("zero returns zero")
        void zeroUnchanged() {
            assertThat(tasks.absoluteValue(0)).isEqualTo(0);
        }

        @ParameterizedTest(name = "absoluteValue({0}) = {1}")
        @CsvSource({"-5, 5", "5, 5", "-1, 1", "1, 1", "0, 0", "-999, 999"})
        @DisplayName("parameterized absolute value checks")
        void parameterized(int input, int expected) {
            assertThat(tasks.absoluteValue(input)).isEqualTo(expected);
        }
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 4 — isPowerOfTwo(n) — bitwise operators only")
    class Task4 {

        @ParameterizedTest(name = "{0} IS a power of two")
        @CsvSource({"1", "2", "4", "8", "16", "32", "64", "1024", "1073741824"})
        @DisplayName("powers of two return true")
        void powersOfTwo(int n) {
            assertThat(tasks.isPowerOfTwo(n)).isTrue();
        }

        @ParameterizedTest(name = "{0} is NOT a power of two")
        @CsvSource({"0", "3", "5", "6", "7", "9", "100", "-1", "-4"})
        @DisplayName("non-powers-of-two return false")
        void notPowersOfTwo(int n) {
            assertThat(tasks.isPowerOfTwo(n)).isFalse();
        }

        @Test
        @DisplayName("edge: 0 is not a power of two")
        void zeroIsFalse() {
            assertThat(tasks.isPowerOfTwo(0)).isFalse();
        }

        @Test
        @DisplayName("edge: 1 is 2^0 — is a power of two")
        void oneIsTrue() {
            assertThat(tasks.isPowerOfTwo(1)).isTrue();
        }

        @Test
        @DisplayName("negative numbers are never powers of two")
        void negativesAreFalse() {
            assertThat(tasks.isPowerOfTwo(-2)).isFalse();
            assertThat(tasks.isPowerOfTwo(-8)).isFalse();
        }
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 5 — swapWithoutTemp(a, b) — no temporary variable")
    class Task5 {

        @Test
        @DisplayName("two different values are swapped correctly")
        void swapDifferent() {
            assertThat(tasks.swapWithoutTemp(3, 7)).containsExactly(7, 3);
            assertThat(tasks.swapWithoutTemp(1, 100)).containsExactly(100, 1);
        }

        @Test
        @DisplayName("swapping with zero works correctly")
        void swapWithZero() {
            assertThat(tasks.swapWithoutTemp(0, 5)).containsExactly(5, 0);
            assertThat(tasks.swapWithoutTemp(5, 0)).containsExactly(0, 5);
        }

        @Test
        @DisplayName("swapping equal values produces the same values")
        void swapEqual() {
            assertThat(tasks.swapWithoutTemp(4, 4)).containsExactly(4, 4);
        }

        @Test
        @DisplayName("swapping negative numbers works correctly")
        void swapNegatives() {
            assertThat(tasks.swapWithoutTemp(-3, -7)).containsExactly(-7, -3);
            assertThat(tasks.swapWithoutTemp(-5, 5)).containsExactly(5, -5);
        }

        @ParameterizedTest(name = "swap({0}, {1}) → [{2}, {3}]")
        @CsvSource({
            " 1,  2,  2,  1",
            " 0,  0,  0,  0",
            "10, 20, 20, 10",
            "-1,  1,  1, -1"
        })
        @DisplayName("parameterized swap scenarios")
        void parameterized(int a, int b, int expectedA, int expectedB) {
            assertThat(tasks.swapWithoutTemp(a, b)).containsExactly(expectedA, expectedB);
        }
    }


    // ── Task 6 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 6 — powerOfTwoShift(exponent) — left-shift only")
    class Task6 {

        @ParameterizedTest(name = "2^{0} = {1}")
        @CsvSource({
            " 0,          1",
            " 1,          2",
            " 2,          4",
            " 3,          8",
            " 4,         16",
            " 8,        256",
            "10,       1024",
            "16,      65536",
            "30, 1073741824"
        })
        @DisplayName("left shift correctly computes 2^n for all valid exponents")
        void allExponents(int exponent, int expected) {
            assertThat(tasks.powerOfTwoShift(exponent)).isEqualTo(expected);
        }
    }
}
