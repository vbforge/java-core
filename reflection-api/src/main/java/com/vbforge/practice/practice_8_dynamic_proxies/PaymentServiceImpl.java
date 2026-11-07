package com.vbforge.practice.practice_8_dynamic_proxies;

/**
 * The "real subject": Client -> Proxy -> (delegates to) -> RealSubject.
 * The proxies add logging, timing and access control without touching this class.
 */
public class PaymentServiceImpl implements PaymentService {

    @Override
    public String processPayment(String txnId, double amount) {
        // addition: a business exception, so the demos can show how handlers must unwrap it
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        return "Processed txn=" + txnId + " amount=$" + amount;
    }

    @Override
    public String refund(String txnId) {
        return "Refunded txn=" + txnId;
    }

    @Override
    public String healthCheck() {
        return "OK";
    }
}
