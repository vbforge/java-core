package com.vbforge.practice.practice_8_dynamic_proxies;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Read-only view: a method whose name starts with "process" counts as mutating and throws
 * UnsupportedOperationException; everything else is delegated. A simplified access-control interceptor.
 */
class ReadOnlyProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T wrap(T target) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    if (method.getName().startsWith("process")) {
                        throw new UnsupportedOperationException(
                                "Method '" + method.getName() + "' is blocked in read-only mode");
                    }
                    try {
                        return method.invoke(target, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
        );
    }
}
