package com.vbforge.practice.practice_8_test_data_builders;

/** Object Mother: named, ready-made objects with a business meaning. Good for the few well-known cases. */
public final class CustomerMother {

    private CustomerMother() {
    }

    public static Customer regularCustomer() {
        return new Customer("Regular Rita", "rita@example.com", false);
    }

    public static Customer vipCustomer() {
        return new Customer("Vip Victor", "victor@example.com", true);
    }
}
