package com.vbforge.practice.practice_20_lazy_nullable_value;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Lazy value that may be null: keep a separate volatile {@code initialized} flag. The plain
 * {@code value} is written BEFORE the volatile flag, so a thread that sees the flag set also
 * sees the value. The factory runs exactly once, null result included.
 */
public class FlagBasedNullableLazyDemo {

    static class NullableLazy<T> {
        private final Supplier<T> factory;
        private T value;                              // plain
        private volatile boolean initialized;         // the publishing edge

        NullableLazy(Supplier<T> factory) {
            this.factory = factory;
        }

        T get() {
            if (!initialized) {
                synchronized (this) {
                    if (!initialized) {
                        value = factory.get();        // may be null
                        initialized = true;           // volatile write publishes value
                    }
                }
            }
            return value;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger calls = new AtomicInteger();
        NullableLazy<String> lazy = new NullableLazy<>(() -> {
            calls.incrementAndGet();
            return null;
        });

        final int threads = 4;
        boolean[] sawNull = new boolean[threads];
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int slot = i;
            workers[i] = new Thread(() -> {
                boolean allNull = true;
                for (int j = 0; j < 1_000; j++) {
                    allNull &= (lazy.get() == null);
                }
                sawNull[slot] = allNull;
            });
            workers[i].start();
        }
        for (Thread t : workers) {
            t.join();
        }

        boolean everyThreadSawNull = true;
        for (boolean b : sawNull) {
            everyThreadSawNull &= b;
        }
        System.out.println("factory calls:             " + calls.get() + "  (expected 1)");
        System.out.println("every thread saw null:     " + everyThreadSawNull);
    }
}
