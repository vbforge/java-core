package com.vbforge.practice.practice_8_dynamic_proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Logging proxy: logs method name + args before the call and the result (or exception) after it.
 * The handler is a NAMED class, so its type can be read with Proxy.getInvocationHandler(proxy).
 */
public class LoggingProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T wrap(T target) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new LoggingHandler(target)
        );
    }

    public static class LoggingHandler implements InvocationHandler {

        private final Object target;

        public LoggingHandler(Object target) {
            this.target = target;
        }

        /** Called for EVERY method invoked on the proxy; args is null for no-arg methods. */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("[LOG] -> " + method.getName() + "(" + Arrays.toString(args) + ")");
            try {
                Object result = method.invoke(target, args);
                System.out.println("[LOG] <- " + method.getName() + " = " + result);
                return result;
            } catch (InvocationTargetException e) {
                // unwrap: the caller must see the exception the target really threw
                System.out.println("[LOG] x " + method.getName() + " threw " + e.getCause());
                throw e.getCause();
            }
        }

        public Object getTarget() {
            return target;
        }
    }
}
