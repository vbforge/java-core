package com.vbforge.practice.practice_8_dynamic_proxies;

/**
 * Target interface for the dynamic proxy demos.
 * A JDK dynamic proxy implements INTERFACES only, so the proxy is assignment-compatible
 * with PaymentService and callers do not notice it.
 */
public interface PaymentService {

    /** Process a payment; @return confirmation message. */
    String processPayment(String txnId, double amount);

    /** Refund a previously processed transaction; @return confirmation message. */
    String refund(String txnId);

    /** @return "OK" if healthy */
    String healthCheck();
}
