package com.vbforge.unit_testing_junit5_assertj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AssertionsAndExceptionsDemoTest — demonstrates AssertJ's fluent API and every
 * standard exception-testing pattern.
 */
@DisplayName("AssertionsAndExceptionsDemo — AssertJ & exceptions")
class AssertionsAndExceptionsDemoTest {

    private final AssertionsAndExceptionsDemo demo = new AssertionsAndExceptionsDemo();

    // ── 1. Fluent assertions ──────────────────────────────────────────────────

    @Nested
    @DisplayName("1. AssertJ fluent chaining")
    class FluentAssertionsTest {

        @Test
        @DisplayName("integer result")
        void integerResult() {
            assertThat(demo.divide(10, 2)).isEqualTo(5);
        }

        @Test
        @DisplayName("assertion chaining on a list")
        void listChaining() {
            List<String> names = List.of("Alice", "Bob", "Carol");
            assertThat(names)
                    .hasSize(3)
                    .contains("Bob")
                    .doesNotContain("Dave")
                    .startsWith("Alice")
                    .endsWith("Carol");
        }

        @Test
        @DisplayName("extracting a field from each element")
        void extracting() {
            List<String> names = List.of("alice", "bob");
            assertThat(names)
                    .extracting(String::toUpperCase)
                    .containsExactly("ALICE", "BOB");
        }

        @Test
        @DisplayName("range checks with isBetween")
        void rangeChecks() {
            assertThat(demo.parseAge("42")).isBetween(0, 150);
        }
    }

    // ── 2. assertThatThrownBy ─────────────────────────────────────────────────

    @Nested
    @DisplayName("2. assertThatThrownBy")
    class AssertThatThrownByTest {

        @Test
        @DisplayName("divide by zero throws ArithmeticException")
        void divideByZero() {
            assertThatThrownBy(() -> demo.divide(5, 0))
                    .isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("exception message is checked")
        void messageChecked() {
            assertThatThrownBy(() -> demo.divide(5, 0))
                    .isInstanceOf(ArithmeticException.class)
                    .hasMessageContaining("zero");
        }
    }

    // ── 3. assertThatExceptionOfType ──────────────────────────────────────────

    @Nested
    @DisplayName("3. assertThatExceptionOfType — typed exception assertions")
    class AssertThatExceptionOfTypeTest {

        @Test
        @DisplayName("parseAge null input")
        void nullInput() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> demo.parseAge(null))
                    .withMessageContaining("null");
        }

        @Test
        @DisplayName("parseAge non-numeric input")
        void nonNumeric() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> demo.parseAge("abc"))
                    .withMessageContaining("not a number");
        }

        @Test
        @DisplayName("parseAge out of range")
        void outOfRange() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> demo.parseAge("200"))
                    .withMessageContaining("out of range");
        }

        @Test
        @DisplayName("firstElement on empty list")
        void firstElementEmpty() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> demo.firstElement(List.of()));
        }
    }

    // ── 4. assertThatCode — "no exception" ────────────────────────────────────

    @Nested
    @DisplayName("4. assertThatCode — verify code does NOT throw")
    class AssertThatCodeTest {

        @Test
        @DisplayName("valid divide does not throw")
        void validDivide() {
            assertThatCode(() -> demo.divide(10, 2))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("valid clamp does not throw")
        void validClamp() {
            assertThatCode(() -> demo.clamp(5, 0, 10))
                    .doesNotThrowAnyException();
        }
    }

    // ── 5. clamp behaviour ────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. clamp — assertion of behaviour")
    class ClampTest {

        @Test
        @DisplayName("value within range is unchanged")
        void withinRange() {
            assertThat(demo.clamp(5, 0, 10)).isEqualTo(5);
        }

        @Test
        @DisplayName("below range clamps to min")
        void belowRange() {
            assertThat(demo.clamp(-3, 0, 10)).isEqualTo(0);
        }

        @Test
        @DisplayName("above range clamps to max")
        void aboveRange() {
            assertThat(demo.clamp(42, 0, 10)).isEqualTo(10);
        }

        @Test
        @DisplayName("invalid range throws")
        void invalidRange() {
            assertThatThrownBy(() -> demo.clamp(1, 5, 2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("min > max");
        }
    }
}