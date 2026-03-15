package com.vbforge.basics.variables;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 🧪 Tests for {@link VariablesTasks}.
 *
 * <p>All tests are RED until you implement the corresponding method.
 * Your goal: make every test GREEN without modifying this file.
 */
@Disabled("Tasks not implemented yet — remove this annotation when ready to solve")
@DisplayName("Variables Tasks — implement to make tests pass")
class VariablesTasksTest {

    private final VariablesTasks tasks = new VariablesTasks();


    // ── Task 1 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 1 — primitiveSize(typeName)")
    class Task1 {

        @ParameterizedTest(name = "\"{0}\" → {1} byte(s)")
        @CsvSource({
            "byte,    1",
            "short,   2",
            "int,     4",
            "long,    8",
            "float,   4",
            "double,  8",
            "char,    2",
            "boolean, 1"
        })
        @DisplayName("returns correct size for all 8 primitive types")
        void allPrimitives(String type, int expectedBytes) {
            assertThat(tasks.primitiveSize(type)).isEqualTo(expectedBytes);
        }

        @Test
        @DisplayName("type name matching is case-insensitive")
        void caseInsensitive() {
            assertThat(tasks.primitiveSize("INT")).isEqualTo(4);
            assertThat(tasks.primitiveSize("Double")).isEqualTo(8);
            assertThat(tasks.primitiveSize("LONG")).isEqualTo(8);
        }

        @Test
        @DisplayName("unknown type name throws IllegalArgumentException")
        void unknownTypeThrows() {
            assertThatThrownBy(() -> tasks.primitiveSize("string"))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.primitiveSize(""))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 2 — safeNarrow(double)")
    class Task2 {

        @Test
        @DisplayName("truncates (does not round) values within int range")
        void truncatesWithinRange() {
            assertThat(tasks.safeNarrow(3.99)).isEqualTo(3);
            assertThat(tasks.safeNarrow(-3.99)).isEqualTo(-3);
            assertThat(tasks.safeNarrow(0.0)).isEqualTo(0);
            assertThat(tasks.safeNarrow(100.0)).isEqualTo(100);
        }

        @Test
        @DisplayName("clamps to Integer.MAX_VALUE when value is too large")
        void clampsToMax() {
            assertThat(tasks.safeNarrow(3.0e10)).isEqualTo(Integer.MAX_VALUE);
            assertThat(tasks.safeNarrow(Double.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);
        }

        @Test
        @DisplayName("clamps to Integer.MIN_VALUE when value is too small")
        void clampsToMin() {
            assertThat(tasks.safeNarrow(-3.0e10)).isEqualTo(Integer.MIN_VALUE);
            assertThat(tasks.safeNarrow(-Double.MAX_VALUE)).isEqualTo(Integer.MIN_VALUE);
        }

        @Test
        @DisplayName("exact boundary values are accepted as-is")
        void exactBoundaries() {
            assertThat(tasks.safeNarrow(Integer.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);
            assertThat(tasks.safeNarrow(Integer.MIN_VALUE)).isEqualTo(Integer.MIN_VALUE);
        }

        @ParameterizedTest(name = "safeNarrow({0}) = {1}")
        @CsvSource({
            "   1.0,  1",
            "  -1.0, -1",
            " 127.9, 127",
            "-128.9, -128"
        })
        @DisplayName("various in-range values truncate correctly")
        void variousValues(double input, int expected) {
            assertThat(tasks.safeNarrow(input)).isEqualTo(expected);
        }
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 3 — isInIntegerCacheRange(Integer)")
    class Task3 {

        @ParameterizedTest(name = "{0} → in cache range")
        @CsvSource({"-128", "-1", "0", "1", "127"})
        @DisplayName("values from -128 to 127 are inside cache range")
        void insideRange(int value) {
            assertThat(tasks.isInIntegerCacheRange(value)).isTrue();
        }

        @ParameterizedTest(name = "{0} → outside cache range")
        @CsvSource({"-129", "128", "1000", "-1000"})
        @DisplayName("values outside -128..127 are not in cache range")
        void outsideRange(int value) {
            assertThat(tasks.isInIntegerCacheRange(value)).isFalse();
        }

        @Test
        @DisplayName("exact boundary values: -128 is inside, -129 is outside")
        void lowerBoundary() {
            assertThat(tasks.isInIntegerCacheRange(-128)).isTrue();
            assertThat(tasks.isInIntegerCacheRange(-129)).isFalse();
        }

        @Test
        @DisplayName("exact boundary values: 127 is inside, 128 is outside")
        void upperBoundary() {
            assertThat(tasks.isInIntegerCacheRange(127)).isTrue();
            assertThat(tasks.isInIntegerCacheRange(128)).isFalse();
        }
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 4 — parseOrDefault(String, int)")
    class Task4 {

        @ParameterizedTest(name = "parseOrDefault(\"{0}\", 0) = {1}")
        @CsvSource({
            " 42,   42",
            " -7,   -7",
            "  0,    0",
            "999,  999"
        })
        @DisplayName("valid integer strings are parsed correctly")
        void validStrings(String input, int expected) {
            assertThat(tasks.parseOrDefault(input, 0)).isEqualTo(expected);
        }

        @Test
        @DisplayName("null input returns the default value")
        void nullInput() {
            assertThat(tasks.parseOrDefault(null, 5)).isEqualTo(5);
            assertThat(tasks.parseOrDefault(null, -1)).isEqualTo(-1);
        }

        @Test
        @DisplayName("blank/whitespace input returns the default value")
        void blankInput() {
            assertThat(tasks.parseOrDefault("   ", 9)).isEqualTo(9);
            assertThat(tasks.parseOrDefault("", 0)).isEqualTo(0);
        }

        @Test
        @DisplayName("non-numeric strings return the default value")
        void nonNumeric() {
            assertThat(tasks.parseOrDefault("abc", 0)).isEqualTo(0);
            assertThat(tasks.parseOrDefault("12.5", -1)).isEqualTo(-1); // double, not int
            assertThat(tasks.parseOrDefault("1 2", 0)).isEqualTo(0);
        }

        @Test
        @DisplayName("default value is respected — different defaults produce different results")
        void defaultValueRespected() {
            assertThat(tasks.parseOrDefault("bad", 42)).isEqualTo(42);
            assertThat(tasks.parseOrDefault("bad", -99)).isEqualTo(-99);
        }
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 5 — shiftChar(char, int)")
    class Task5 {

        @Test
        @DisplayName("lowercase letters shift correctly without wrapping")
        void lowercaseNoWrap() {
            assertThat(tasks.shiftChar('a', 1)).isEqualTo('b');
            assertThat(tasks.shiftChar('a', 3)).isEqualTo('d');
            assertThat(tasks.shiftChar('m', 5)).isEqualTo('r');
        }

        @Test
        @DisplayName("lowercase letters wrap around from z back to a")
        void lowercaseWraps() {
            assertThat(tasks.shiftChar('z', 1)).isEqualTo('a');
            assertThat(tasks.shiftChar('y', 3)).isEqualTo('b');
            assertThat(tasks.shiftChar('z', 26)).isEqualTo('z'); // full rotation
        }

        @Test
        @DisplayName("uppercase letters shift correctly without wrapping")
        void uppercaseNoWrap() {
            assertThat(tasks.shiftChar('A', 1)).isEqualTo('B');
            assertThat(tasks.shiftChar('A', 3)).isEqualTo('D');
        }

        @Test
        @DisplayName("uppercase letters wrap around from Z back to A")
        void uppercaseWraps() {
            assertThat(tasks.shiftChar('Z', 1)).isEqualTo('A');
            assertThat(tasks.shiftChar('Y', 3)).isEqualTo('B');
        }

        @Test
        @DisplayName("non-alphabetic characters are returned unchanged")
        void nonAlphaUnchanged() {
            assertThat(tasks.shiftChar('5', 3)).isEqualTo('5');
            assertThat(tasks.shiftChar(' ', 5)).isEqualTo(' ');
            assertThat(tasks.shiftChar('!', 1)).isEqualTo('!');
        }

        @Test
        @DisplayName("case is preserved — uppercase stays upper, lowercase stays lower")
        void casePreserved() {
            assertThat(tasks.shiftChar('A', 1)).isEqualTo('B');  // not 'b'
            assertThat(tasks.shiftChar('a', 1)).isEqualTo('b');  // not 'B'
        }

        @ParameterizedTest(name = "shiftChar('{0}', {1}) = '{2}'")
        @CsvSource({
            "a, 0, a",
            "a, 1, b",
            "a, 25, z",
            "a, 26, a",
            "z, 1, a",
            "A, 1, B",
            "Z, 1, A"
        })
        @DisplayName("parameterized shift scenarios covering edge cases")
        void parameterizedShift(char input, int shift, char expected) {
            assertThat(tasks.shiftChar(input, shift)).isEqualTo(expected);
        }
    }
}
