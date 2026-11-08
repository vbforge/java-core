package com.vbforge.practice.practice_17_synchronized_lazy_init;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Thread-safe lazy initialisation, the simple way: lock on EVERY call. Correct (the lock is
 * a mutual-exclusion and happens-before edge), but every caller pays for the lock, even long
 * after the value exists. The factory runs exactly once and all threads get the same instance.
 */
public class SynchronizedLazyDemo {

    static class SynchronizedLazy<T> {
        private final Supplier<T> factory;
        private T value;                       // plain, guarded by this

        SynchronizedLazy(Supplier<T> factory) {
            this.factory = factory;
        }

        synchronized T get() {
            if (value == null) {
                value = factory.get();
            }
            return value;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger factoryCalls = new AtomicInteger();
        SynchronizedLazy<Object> lazy = new SynchronizedLazy<>(() -> {
            factoryCalls.incrementAndGet();
            return new Object();
        });

        final int threads = 8;
        Object[] seen = new Object[threads];
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int slot = i;
            workers[i] = new Thread(() -> {
                for (int j = 0; j < 1_000; j++) {
                    seen[slot] = lazy.get();
                }
            });
            workers[i].start();
        }
        for (Thread t : workers) {
            t.join();
        }

        boolean sameInstance = true;
        for (Object o : seen) {
            sameInstance &= (o == seen[0]);
        }
        System.out.println("factory calls:            " + factoryCalls.get());
        System.out.println("all threads same instance: " + sameInstance);
    }
}
