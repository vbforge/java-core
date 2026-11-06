package com.vbforge.practice.practice_11_programming_to_an_interface;

import com.vbforge.practice.practice_11_programming_to_an_interface.PaymentProcessor;

public class PayPalProcessor implements PaymentProcessor {

    @Override
    public boolean processPayment(String recipient, double amount) {
        // In reality: PayPal SDK call, hidden from the caller
        System.out.println("  [PayPal] Sending to " + recipient + " -> $" + amount);
        return true;
    }

    @Override
    public String getProviderName() { return "PayPal"; }
}
