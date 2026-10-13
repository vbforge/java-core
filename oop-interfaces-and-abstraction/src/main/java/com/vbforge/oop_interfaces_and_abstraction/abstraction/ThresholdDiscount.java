package com.vbforge.oop_interfaces_and_abstraction.abstraction;

/** Takes a fixed amount off, but only for orders that reach the threshold. Package-private implementation detail. */
class ThresholdDiscount implements DiscountPolicy {

    private final double threshold;
    private final double amountOff;

    ThresholdDiscount(double threshold, double amountOff) {
        this.threshold = threshold;
        this.amountOff = amountOff;
    }

    @Override
    public double apply(double subtotal) {
        return subtotal >= threshold ? subtotal - amountOff : subtotal;
    }
}
