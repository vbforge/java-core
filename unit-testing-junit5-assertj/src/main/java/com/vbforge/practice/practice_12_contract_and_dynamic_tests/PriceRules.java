package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

final class PriceRules {

    private PriceRules() {
    }

    static void requireNonNegative(long priceCents) {
        if (priceCents < 0) {
            throw new IllegalArgumentException("price must not be negative: " + priceCents);
        }
    }
}
