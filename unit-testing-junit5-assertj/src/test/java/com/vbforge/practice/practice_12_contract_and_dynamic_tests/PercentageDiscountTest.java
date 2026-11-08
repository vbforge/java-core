package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** The contract tests are inherited, only behaviour SPECIFIC to this implementation is added here. */
class PercentageDiscountTest implements PriceRuleContract {

    @Override
    public PriceRule createRule() {
        return new PercentageDiscount(30);
    }

    @ParameterizedTest(name = "{0}% off {1} = {2}")
    @CsvSource({"25, 1000, 750", "100, 999, 0", "0, 999, 999", "50, 3, 2", "10, 25, 23"})
    void appliesPercentageWithHalfUpRounding(int percent, long price, long expected) {
        assertThat(new PercentageDiscount(percent).apply(price)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void percentOutsideRange_isRejected(int percent) {
        assertThatIllegalArgumentException().isThrownBy(() -> new PercentageDiscount(percent));
    }

    @Test
    void everyPercent_satisfiesTheContract() {
        for (int percent = 0; percent <= 100; percent++) {
            PriceRule rule = new PercentageDiscount(percent);
            assertThat(rule.apply(1_001)).as("percent=%d", percent).isBetween(0L, 1_001L);
        }
    }
}
