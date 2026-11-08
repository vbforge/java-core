package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class FixedDiscountTest implements PriceRuleContract {

    @Override
    public PriceRule createRule() {
        return new FixedDiscount(300);
    }

    @Test
    void subtractsTheDiscount() {
        assertThat(new FixedDiscount(300).apply(1_000)).isEqualTo(700);
    }

    @Test
    void priceBelowTheDiscount_clampsToZeroInsteadOfGoingNegative() {
        assertThat(new FixedDiscount(300).apply(100)).isZero();
    }

    @Test
    void negativeDiscount_isRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> new FixedDiscount(-1));
    }
}
