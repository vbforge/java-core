package com.vbforge.practice.practice_18_double_checked_locking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Correct double-checked locking: the field is volatile, and it is read into a LOCAL variable
 * once, so the fast path costs one volatile read instead of two.
 *
 * <p>The volatile write happens-before every volatile read that sees the reference, so a
 * thread that sees non-null also sees the constructor's writes. Guaranteed, so asserted:
 * the factory runs exactly once per holder and nobody sees a half-built resource.
 */
public class VolatileDoubleCheckedLockingDemo {

    static final AtomicInteger CREATED = new AtomicInteger();

    static class Resource {
        int a;
        int b;

        Resource() {
            CREATED.incrementAndGet();
            a = 1;
            b = 2;
        }
    }

    static class Lazy<T> {
        private final Supplier<T> factory;
        private volatile T value;                  // the fix

        Lazy(Supplier<T> factory) {
            this.factory = factory;
        }

        T get() {
            T local = value;                       // one volatile read on the fast path
            if (local == null) {
                synchronized (this) {
                    local = value;
                    if (local == null) {
                        local = factory.get();
                        value = local;             // volatile write publishes it
                    }
                }
            }
            return local;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int rounds = 5_000;
        List<Lazy<Resource>> lazies = new ArrayList<>();   // filled before the threads start
        for (int i = 0; i < rounds; i++) {
            lazies.add(new Lazy<>(Resource::new));
        }
        int[] halfBuilt = new int[2];
        CyclicBarrier start = new CyclicBarrier(2);

        Thread[] workers = new Thread[2];
        for (int t = 0; t < workers.length; t++) {
            final int slot = t;
            workers[t] = new Thread(() -> {
                try {
                    for (int i = 0; i < rounds; i++) {
                        start.await();
                        Resource r = lazies.get(i).get();
                        if (r.a != 1 || r.b != 2) {
                            halfBuilt[slot]++;
                        }
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new IllegalStateException(e);
                }
            });
            workers[t].start();
        }
        for (Thread t : workers) {
            t.join();
        }

        System.out.println("rounds:                       " + rounds);
        System.out.println("resources created == rounds:  " + (CREATED.get() == rounds));
        System.out.println("half-built sightings:         " + (halfBuilt[0] + halfBuilt[1]) + "  (impossible with volatile)");
    }
}
