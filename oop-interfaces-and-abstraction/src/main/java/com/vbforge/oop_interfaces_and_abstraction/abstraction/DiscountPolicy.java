package com.vbforge.oop_interfaces_and_abstraction.abstraction;

/** The ABSTRACTION: callers know "a policy turns a subtotal into a payable amount" — nothing about how. */
public interface DiscountPolicy {

    /** Returns the amount to pay for the given subtotal. */
    double apply(double subtotal);
}
