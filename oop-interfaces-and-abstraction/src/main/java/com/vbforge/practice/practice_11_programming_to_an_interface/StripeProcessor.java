package com.vbforge.practice.practice_11_programming_to_an_interface;

import com.vbforge.practice.practice_11_programming_to_an_interface.PaymentProcessor;

public class StripeProcessor implements PaymentProcessor {

    @Override
    public boolean processPayment(String recipient, double amount) {
        // In reality: HTTP call to the Stripe API, hidden from the caller
        System.out.println("  [Stripe] Charging " + recipient + " -> $" + amount);
        return true;
    }

    @Override
    public String getProviderName() { return "Stripe"; }
}
