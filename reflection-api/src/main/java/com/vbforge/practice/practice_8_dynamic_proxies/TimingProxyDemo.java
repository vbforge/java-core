package com.vbforge.practice.practice_8_dynamic_proxies;

import com.vbforge.practice.practice_8_dynamic_proxies.PaymentServiceImpl;
import com.vbforge.practice.practice_8_dynamic_proxies.TimingProxyFactory;

/** Timing proxy built with a lambda handler. Elapsed time is only checked, never printed. */
public class TimingProxyDemo {

    public static void main(String[] args) {
        PaymentService timed = TimingProxyFactory.wrap(new PaymentServiceImpl(),
                (method, ns) -> System.out.println("[TIMER] " + method + " measured, elapsed >= 0: " + (ns >= 0)));
        System.out.println(timed.processPayment("TXN-002", 150.0));
        System.out.println(timed.refund("TXN-002"));
    }
}
