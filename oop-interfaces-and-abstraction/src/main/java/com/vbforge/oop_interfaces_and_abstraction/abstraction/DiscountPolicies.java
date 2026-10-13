package com.vbforge.oop_interfaces_and_abstraction.abstraction;

/**
 * The only way for outside code to obtain a policy. Every method RETURNS THE INTERFACE, so the
 * concrete classes can be renamed, replaced or optimised without breaking any caller.
 */
public final class DiscountPolicies {

    private DiscountPolicies() {
    }

    public static DiscountPolicy none() {
        return new NoDiscount();
    }

    public static DiscountPolicy percent(double percent) {
        return new PercentDiscount(percent);
    }

    public static DiscountPolicy threshold(double threshold, double amountOff) {
        return new ThresholdDiscount(threshold, amountOff);
    }
}
