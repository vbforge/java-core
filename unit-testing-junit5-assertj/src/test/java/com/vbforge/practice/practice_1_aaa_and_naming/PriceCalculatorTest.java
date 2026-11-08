package com.vbforge.practice.practice_1_aaa_and_naming;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Technique: Arrange-Act-Assert + naming.
 * - one behaviour per test, name says WHAT happens and under WHICH condition
 * - blank lines (or comments) separate the three phases
 * - exactly one "Act" line per test
 */
@DisplayName("PriceCalculator")
class PriceCalculatorTest {

    private final PriceCalculator calculator = new PriceCalculator();

    @Test
    @DisplayName("15% discount on 19.99 is rounded to cents (HALF_UP)")
    void applyDiscount_roundsResultToCents() {
        // Arrange
        BigDecimal price = new BigDecimal("19.99");

        // Act
        BigDecimal result = calculator.applyDiscount(price, 15);

        // Assert
        assertThat(result).isEqualByComparingTo("16.99");
    }

    @Test
    void applyDiscount_zeroPercent_returnsSamePrice() {
        BigDecimal result = calculator.applyDiscount(new BigDecimal("50.00"), 0);

        assertThat(result).isEqualByComparingTo("50.00");
    }

    @Test
    void applyDiscount_hundredPercent_returnsZero() {
        BigDecimal result = calculator.applyDiscount(new BigDecimal("50.00"), 100);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void applyDiscount_negativePercent_throwsWithValueInMessage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.applyDiscount(BigDecimal.TEN, -1))
                .withMessageContaining("-1");
    }

    @Test
    void addTax_twentyPercentOnHundred_returnsOneHundredTwenty() {
        BigDecimal result = calculator.addTax(new BigDecimal("100.00"), new BigDecimal("20"));

        // isEqualTo would fail on scale (120.00 vs 120), compareTo-based assertion is the right tool for BigDecimal
        assertThat(result).isEqualByComparingTo("120");
    }

    @Test
    void addTax_nullPrice_throws() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.addTax(null, BigDecimal.ONE))
                .withMessageContaining("null");
    }

    @Test
    @DisplayName("BigDecimal equals is scale-sensitive: why isEqualByComparingTo is used above")
    void bigDecimalEquals_isScaleSensitive() {
        assertThat(new BigDecimal("2.0")).isNotEqualTo(new BigDecimal("2.00"));
        assertThat(new BigDecimal("2.0")).isEqualByComparingTo(new BigDecimal("2.00"));
    }

    @Test
    void applyDiscount_negativePrice_throws() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.applyDiscount(new BigDecimal("-0.01"), 10))
                .withMessageContaining("negative");
    }

    @Test
    void addTax_negativeTax_throws() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.addTax(BigDecimal.TEN, new BigDecimal("-1")));
    }

    @Test
    void applyDiscount_nullPrice_throws() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.applyDiscount(null, 10))
                .withMessageContaining("null");
    }

    @Test
    void applyDiscount_percentAboveHundred_throwsWithValueInMessage() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> calculator.applyDiscount(BigDecimal.TEN, 101))
                .withMessageContaining("101");
    }
}
