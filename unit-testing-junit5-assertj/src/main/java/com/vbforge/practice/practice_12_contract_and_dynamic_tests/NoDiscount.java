package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

public class NoDiscount implements PriceRule {

    @Override
    public long apply(long priceCents) {
        PriceRules.requireNonNegative(priceCents);
        return priceCents;
    }
}
