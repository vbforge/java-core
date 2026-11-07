package com.vbforge.practice.practice_8_dynamic_proxies;

import com.vbforge.practice.practice_8_dynamic_proxies.LoggingProxyFactory;
import com.vbforge.practice.practice_8_dynamic_proxies.PaymentServiceImpl;

/** Logging proxy: every call is logged; a business exception passes through unwrapped. */
public class LoggingProxyDemo {

    public static void main(String[] args) {
        PaymentService logged = LoggingProxyFactory.wrap(new PaymentServiceImpl());
        logged.processPayment("TXN-001", 99.99);
        logged.refund("TXN-001");
        logged.healthCheck();

        // addition: the caller catches the real exception, not UndeclaredThrowableException
        try {
            logged.processPayment("TXN-BAD", -5);
        } catch (IllegalArgumentException e) {
            System.out.println("caller caught: " + e.getMessage());
        }
    }
}
