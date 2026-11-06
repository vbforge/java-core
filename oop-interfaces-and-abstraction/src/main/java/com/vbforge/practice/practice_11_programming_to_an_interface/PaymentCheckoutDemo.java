package com.vbforge.practice.practice_11_programming_to_an_interface;

/**
 * Abstraction: expose WHAT, hide HOW. checkout() depends on PaymentProcessor only,
 * so implementations can be swapped without touching it.
 */
public class PaymentCheckoutDemo {

    static void checkout(PaymentProcessor processor, String customer, double amount) {
        System.out.println("Processing via " + processor.getProviderName() + "...");
        boolean success = processor.processPayment(customer, amount);
        System.out.println("Result: " + (success ? "SUCCESS" : "FAILED"));
    }

    public static void main(String[] args) {
        checkout(new StripeProcessor(), "alice@example.com", 99.99);
        checkout(new PayPalProcessor(), "bob@example.com", 149.99);
    }
}
