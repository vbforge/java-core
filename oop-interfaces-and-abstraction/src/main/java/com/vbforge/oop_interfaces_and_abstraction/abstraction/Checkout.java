package com.vbforge.oop_interfaces_and_abstraction.abstraction;

import java.util.List;

/**
 * Depends on the {@link DiscountPolicy} ABSTRACTION, handed in through the constructor
 * (dependency injection). It works with every present and future policy and never changes.
 */
public class Checkout {

    private final DiscountPolicy policy;

    public Checkout(DiscountPolicy policy) {
        this.policy = policy;
    }

    /** Sums the prices and lets the policy decide what is payable. */
    public double total(List<Double> prices) {
        double subtotal = 0;
        for (double price : prices) {
            subtotal += price;
        }
        return policy.apply(subtotal);
    }
}
