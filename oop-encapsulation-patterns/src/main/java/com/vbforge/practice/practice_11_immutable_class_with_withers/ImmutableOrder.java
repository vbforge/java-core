package com.vbforge.practice.practice_11_immutable_class_with_withers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A properly immutable class (Effective Java, Item 17):
 *   1. no mutating methods (no setters)
 *   2. class is final, so it cannot be subclassed
 *   3. all fields private and final
 *   4. exclusive access to mutable components: copy IN, expose only an unmodifiable list
 *
 * "Changing" an immutable object means building a new one (the 'wither' methods).
 */
public final class ImmutableOrder {

    private final String       orderId;
    private final String       customer;
    private final List<String> items;        // mutable type: needs protection
    private final double       totalAmount;

    public ImmutableOrder(String orderId, String customer, List<String> items, double totalAmount) {
        if (orderId == null || orderId.isBlank())
            throw new IllegalArgumentException("orderId cannot be blank");
        if (totalAmount < 0)
            throw new IllegalArgumentException("totalAmount cannot be negative");

        this.orderId     = orderId;
        this.customer    = customer;
        // defensive copy: the caller may mutate their list later
        this.items       = Collections.unmodifiableList(new ArrayList<>(items));
        this.totalAmount = totalAmount;
    }

    public String       getOrderId()     { return orderId; }
    public String       getCustomer()    { return customer; }
    public double       getTotalAmount() { return totalAmount; }

    /** Already unmodifiable, so returning it directly is safe. */
    public List<String> getItems()       { return items; }

    public ImmutableOrder withTotalAmount(double newAmount) {
        return new ImmutableOrder(orderId, customer, items, newAmount);
    }

    public ImmutableOrder withCustomer(String newCustomer) {
        return new ImmutableOrder(orderId, newCustomer, items, totalAmount);
    }

    @Override
    public String toString() {
        return "ImmutableOrder{id=" + orderId + ", customer=" + customer
               + ", items=" + items + ", total=" + totalAmount + "}";
    }
}
