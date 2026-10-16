package com.vbforge.best_practices_and_solid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SingleResponsibilityTasks — your implementations")
class SingleResponsibilityTasksTest {

    private SingleResponsibilityTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new SingleResponsibilityTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement validateUsername()")
    @DisplayName("validateUsername(username)")
    class ValidateUsernameTest {

        @Test
        @DisplayName("a valid username has no errors")
        void validName() {
            assertThat(tasks.validateUsername("alice")).isEmpty();
        }

        @ParameterizedTest(name = "\"{0}\" is valid")
        @CsvSource({"abc", "abcdefghijkl", "A1b2C3", "user42"})
        @DisplayName("lengths 3 and 12 and mixed letters and digits are valid")
        void boundaryNamesAreValid(String name) {
            assertThat(tasks.validateUsername(name)).isEmpty();
        }

        @Test
        @DisplayName("null and whitespace-only names are blank, and nothing else is reported")
        void blankNames() {
            assertThat(tasks.validateUsername(null)).containsExactly("username is blank");
            assertThat(tasks.validateUsername("")).containsExactly("username is blank");
            assertThat(tasks.validateUsername("   ")).containsExactly("username is blank");
        }

        @Test
        @DisplayName("fewer than 3 characters is too short")
        void tooShort() {
            assertThat(tasks.validateUsername("ab")).containsExactly("username is too short");
        }

        @Test
        @DisplayName("more than 12 characters is too long")
        void tooLong() {
            assertThat(tasks.validateUsername("abcdefghijklm")).containsExactly("username is too long");
        }

        @Test
        @DisplayName("a character that is not a letter or digit is invalid")
        void invalidCharacters() {
            assertThat(tasks.validateUsername("a_b")).containsExactly("username has invalid characters");
        }

        @Test
        @DisplayName("every broken rule is reported, in the documented order")
        void severalProblemsInOrder() {
            assertThat(tasks.validateUsername("a!"))
                    .containsExactly("username is too short", "username has invalid characters");
        }

        @Test
        @DisplayName("the name is not trimmed: a trailing space is an invalid character")
        void notTrimmed() {
            assertThat(tasks.validateUsername("ab ")).containsExactly("username has invalid characters");
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement calculateTotal()")
    @DisplayName("calculateTotal(prices, taxRate)")
    class CalculateTotalTest {

        @Test
        @DisplayName("sums prices and adds tax")
        void sumsAndAddsTax() {
            assertThat(tasks.calculateTotal(Arrays.asList(10.0, 20.0), 0.1)).isEqualTo(33.0);
            assertThat(tasks.calculateTotal(Arrays.asList(19.99, 5.01), 0.2)).isEqualTo(30.0);
        }

        @Test
        @DisplayName("a zero tax rate leaves the sum unchanged")
        void zeroTax() {
            assertThat(tasks.calculateTotal(Collections.singletonList(9.99), 0.0)).isEqualTo(9.99);
        }

        @Test
        @DisplayName("an empty list totals zero")
        void emptyList() {
            assertThat(tasks.calculateTotal(Collections.<Double>emptyList(), 0.2)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("floating-point noise is rounded away")
        void roundsAwayNoise() {
            assertThat(tasks.calculateTotal(Arrays.asList(0.1, 0.2), 0.0)).isEqualTo(0.3);
        }

        @Test
        @DisplayName("the result is rounded to 2 decimals")
        void roundsToTwoDecimals() {
            assertThat(tasks.calculateTotal(Collections.singletonList(10.0), 0.075)).isEqualTo(10.75);
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement renderReceipt()")
    @DisplayName("renderReceipt(names, prices, total)")
    class RenderReceiptTest {

        @Test
        @DisplayName("renders one line per item and a TOTAL line")
        void rendersReceipt() {
            assertThat(tasks.renderReceipt(Arrays.asList("Tea", "Cake"), Arrays.asList(2.5, 4.0), 6.5))
                    .isEqualTo("- Tea: 2.50\n- Cake: 4.00\nTOTAL: 6.50");
        }

        @Test
        @DisplayName("an empty receipt only has the TOTAL line")
        void emptyReceipt() {
            assertThat(tasks.renderReceipt(Collections.<String>emptyList(), Collections.<Double>emptyList(), 0.0))
                    .isEqualTo("TOTAL: 0.00");
        }

        @Test
        @DisplayName("a single item works and there is no trailing newline")
        void singleItem() {
            String receipt = tasks.renderReceipt(Collections.singletonList("Tea"), Collections.singletonList(2.5), 2.5);

            assertThat(receipt).isEqualTo("- Tea: 2.50\nTOTAL: 2.50").doesNotEndWith("\n");
        }

        @Test
        @DisplayName("it prints the total it is given instead of recomputing it")
        void usesGivenTotal() {
            assertThat(tasks.renderReceipt(Collections.singletonList("Tea"), Collections.singletonList(2.5), 99.0))
                    .endsWith("TOTAL: 99.00");
        }

        @Test
        @DisplayName("lists of different sizes are rejected")
        void sizeMismatch() {
            assertThatThrownBy(() -> tasks.renderReceipt(Arrays.asList("Tea", "Cake"), Collections.singletonList(2.5), 2.5))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
