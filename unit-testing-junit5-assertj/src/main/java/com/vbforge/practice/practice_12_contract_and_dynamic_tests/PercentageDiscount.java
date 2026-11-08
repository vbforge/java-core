package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

public class PercentageDiscount implements PriceRule {

    private final int percent;

    public PercentageDiscount(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be in 0..100");
        }
        this.percent = percent;
    }

    @Override
    public long apply(long priceCents) {
        PriceRules.requireNonNegative(priceCents);
        return (priceCents * (100 - percent) + 50) / 100;   // HALF_UP on whole cents
    }
}
