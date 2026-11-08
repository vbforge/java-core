package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

/**
 * Contract: for a price in cents (>= 0) returns the price after the rule.
 * Every implementation must: reject negative input, never return a negative value,
 * never return more than the input, and never decrease when the input grows.
 */
public interface PriceRule {

    long apply(long priceCents);
}
