package com.vbforge.practice.practice_20_lazy_nullable_value;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * When the factory may legitimately return null, `value == null` can no longer mean "not
 * initialised yet". The naive lazy runs the factory again on every call. Single-threaded on
 * purpose: this is a logic bug, not a race, so the output is fixed.
 */
public class NaiveNullCheckLazyDemo {

    static class NaiveLazy<T> {
        private final Supplier<T> factory;
        private T value;

        NaiveLazy(Supplier<T> factory) {
            this.factory = factory;
        }

        synchronized T get() {
            if (value == null) {              // null result looks like "not initialised"
                value = factory.get();
            }
            return value;
        }
    }

    public static void main(String[] args) {
        AtomicInteger calls = new AtomicInteger();
        NaiveLazy<String> lazy = new NaiveLazy<>(() -> {
            calls.incrementAndGet();
            return null;                      // a legitimate "no value"
        });

        lazy.get();
        lazy.get();
        lazy.get();
        System.out.println("3 get() calls, factory ran " + calls.get() + " times  (expected 1)");
    }
}
