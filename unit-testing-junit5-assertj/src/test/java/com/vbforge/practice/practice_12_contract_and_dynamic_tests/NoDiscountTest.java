package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NoDiscountTest implements PriceRuleContract {

    @Override
    public PriceRule createRule() {
        return new NoDiscount();
    }

    @Test
    void returnsThePriceUnchanged() {
        assertThat(new NoDiscount().apply(1_234)).isEqualTo(1_234);
    }
}
