package com.vbforge.practice.practice_8_dynamic_proxies;

import java.lang.reflect.Proxy;
import java.util.Arrays;

/** Proxy.isProxyClass, Proxy.getInvocationHandler and the interfaces a proxy class implements. */
public class ProxyIntrospectionDemo {

    public static void main(String[] args) {
        PaymentService logged = LoggingProxyFactory.wrap(new PaymentServiceImpl());

        System.out.println("Is proxy class?     " + Proxy.isProxyClass(logged.getClass()));
        System.out.println("Handler type:       "
                + Proxy.getInvocationHandler(logged).getClass().getSimpleName());
        System.out.println("Implemented ifaces: " + Arrays.toString(logged.getClass().getInterfaces()));
    }
}
