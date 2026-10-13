package com.vbforge.oop_interfaces_and_abstraction.abstraction;

/** Package-private on purpose: code outside this package cannot even name the class. */
class NoDiscount implements DiscountPolicy {

    @Override
    public double apply(double subtotal) {
        return subtotal;
    }
}
