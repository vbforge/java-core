package com.vbforge.oop_interfaces_and_abstraction.abstraction;

/** Takes a fixed percentage off every order. Package-private implementation detail. */
class PercentDiscount implements DiscountPolicy {

    private final double percent;

    PercentDiscount(double percent) {
        this.percent = percent;
    }

    @Override
    public double apply(double subtotal) {
        return subtotal * (100 - percent) / 100;
    }
}
