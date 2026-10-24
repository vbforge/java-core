package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.TddCycleDemo.Calculator;
import com.vbforge.mockito_and_tdd.TddCycleDemo.CalculatorFakeIt;
import com.vbforge.mockito_and_tdd.TddCycleDemo.CalculatorV1;
import com.vbforge.mockito_and_tdd.TddCycleDemo.CalculatorV2;
import com.vbforge.mockito_and_tdd.TddCycleDemo.CalculatorV3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TddCycleDemoTest — verifies every stage of the red → green → refactor story in
 * {@link TddCycleDemo}.
 *
 * <p>These tests are always GREEN: the "red" stage is proven by asserting that the
 * old implementation <em>does</em> fail the new requirement.
 */
@DisplayName("TddCycleDemo — API contracts")
class TddCycleDemoTest {

    // ── 1. Red ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Red")
    class RedTest {

        private final Calculator v1 = new CalculatorV1();

        @Test
        @DisplayName("requirement 1 is already green: \"\" → 0 and \"7\" → 7")
        void firstRequirementGreen() {
            assertThat(v1.add("")).isZero();
            assertThat(v1.add("7")).isEqualTo(7);
        }

        @Test
        @DisplayName("requirement 2 is red for the right reason: \"1,2\" gives 1, not 3")
        void secondRequirementRed() {
            assertThat(v1.add("1,2")).isEqualTo(1);       // the wrong VALUE — a meaningful red
            assertThat(v1.add("1,2")).isNotEqualTo(3);
        }

        @Test
        @DisplayName("the whole spec has failures against V1")
        void specFails() {
            assertThat(TddCycleDemo.failures(v1)).isGreaterThan(0);
        }
    }

    // ── 2. Fake it, then triangulate ──────────────────────────────────────────

    @Nested
    @DisplayName("2. Fake it, then triangulate")
    class FakeItTest {

        private final Calculator fake = new CalculatorFakeIt();

        @Test
        @DisplayName("a hard-coded 3 is enough to pass the first example")
        void firstExamplePasses() {
            assertThat(fake.add("1,2")).isEqualTo(3);
        }

        @Test
        @DisplayName("a second example (\"4,5\") exposes the fake")
        void secondExampleExposesFake() {
            assertThat(fake.add("4,5")).isNotEqualTo(9);
        }

        @Test
        @DisplayName("the real implementation passes both examples")
        void realImplementationPasses() {
            Calculator real = new CalculatorV2();

            assertThat(real.add("1,2")).isEqualTo(3);
            assertThat(real.add("4,5")).isEqualTo(9);
        }
    }

    // ── 3. Green ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Green")
    class GreenTest {

        private final Calculator v2 = new CalculatorV2();

        @Test
        @DisplayName("passes every row of the spec")
        void passesSpec() {
            assertThat(TddCycleDemo.failures(v2)).isZero();
        }

        @Test
        @DisplayName("newlines and commas both separate numbers")
        void newlineSeparator() {
            assertThat(v2.add("1\n2,3")).isEqualTo(6);
        }

        @Test
        @DisplayName("numbers above 1000 are ignored; 1000 itself counts")
        void bigNumbers() {
            assertThat(v2.add("2,1001")).isEqualTo(2);
            assertThat(v2.add("1000,1")).isEqualTo(1001);
        }

        @Test
        @DisplayName("negatives are rejected and ALL of them are listed")
        void negatives() {
            assertThatThrownBy(() -> v2.add("1,-2,-3"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("negatives not allowed: -2,-3");
        }
    }

    // ── 4. Refactor ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Refactor")
    class RefactorTest {

        private final Calculator v2 = new CalculatorV2();
        private final Calculator v3 = new CalculatorV3();

        @Test
        @DisplayName("the refactored version still passes the spec")
        void passesSpec() {
            assertThat(TddCycleDemo.failures(v3)).isZero();
        }

        @ParameterizedTest(name = "V2 and V3 agree on \"{0}\"")
        @ValueSource(strings = {"", "5", "1,2", "1\n2\n3", "1000,1000", "5,2000,6"})
        @DisplayName("same result as before the refactoring")
        void sameBehaviour(String input) {
            assertThat(v3.add(input)).isEqualTo(v2.add(input));
        }

        @Test
        @DisplayName("same error message for negatives")
        void sameNegativesMessage() {
            assertThatThrownBy(() -> v3.add("4,-1,-9"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("negatives not allowed: -1,-9");
        }
    }
}
