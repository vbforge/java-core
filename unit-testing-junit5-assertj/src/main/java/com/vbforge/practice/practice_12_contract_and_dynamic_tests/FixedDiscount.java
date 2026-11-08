package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

public class FixedDiscount implements PriceRule {

    private final long discountCents;

    public FixedDiscount(long discountCents) {
        if (discountCents < 0) {
            throw new IllegalArgumentException("discount must not be negative");
        }
        this.discountCents = discountCents;
    }

    @Override
    public long apply(long priceCents) {
        PriceRules.requireNonNegative(priceCents);
        return Math.max(0, priceCents - discountCents);   // the price never goes below zero
    }
}
