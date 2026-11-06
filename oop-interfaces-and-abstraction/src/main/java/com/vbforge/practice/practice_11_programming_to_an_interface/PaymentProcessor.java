package com.vbforge.practice.practice_11_programming_to_an_interface;

/** The contract: WHAT a payment provider does, not HOW. */
public interface PaymentProcessor {
    boolean processPayment(String recipient, double amount);

    String getProviderName();
}
