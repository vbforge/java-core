package com.vbforge.practice.practice_8_dynamic_proxies;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.function.BiConsumer;

/**
 * Timing proxy: measures the nanoseconds of every call.
 * The InvocationHandler is a lambda (it is a functional interface), fine for a stateless handler.
 * The measurement goes to a reporter instead of being printed, so the demo output stays deterministic.
 */
class TimingProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T wrap(T target, BiConsumer<String, Long> reporter) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    long start = System.nanoTime();
                    try {
                        return method.invoke(target, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    } finally {
                        reporter.accept(method.getName(), System.nanoTime() - start);
                    }
                }
        );
    }
}
