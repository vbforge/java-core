package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

import java.util.List;

public class PriceRuleDemo {
    public static void main(String[] args) {
        List<PriceRule> rules = List.of(new NoDiscount(), new PercentageDiscount(25), new FixedDiscount(300));
        for (PriceRule rule : rules) {
            System.out.println(rule.getClass().getSimpleName() + " -> " + rule.apply(1_000));
        }
    }
}
