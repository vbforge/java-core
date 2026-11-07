package com.vbforge.practice.practice_8_dynamic_proxies;

import com.vbforge.practice.practice_8_dynamic_proxies.PaymentServiceImpl;
import com.vbforge.practice.practice_8_dynamic_proxies.ReadOnlyProxyFactory;

/** Read-only proxy: "process*" is blocked by name, the rest is delegated. */
public class ReadOnlyProxyDemo {

    public static void main(String[] args) {
        PaymentService readOnly = ReadOnlyProxyFactory.wrap(new PaymentServiceImpl());
        try {
            readOnly.processPayment("TXN-003", 200.0);
        } catch (UnsupportedOperationException e) {
            System.out.println("Blocked: " + e.getMessage());
        }
        System.out.println("Refund (allowed): " + readOnly.refund("TXN-003"));
    }
}
