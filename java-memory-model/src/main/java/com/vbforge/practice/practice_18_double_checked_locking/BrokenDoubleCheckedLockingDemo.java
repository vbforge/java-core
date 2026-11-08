package com.vbforge.practice.practice_18_double_checked_locking;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The classic BROKEN double-checked locking: the field is not volatile, so the unlocked fast
 * path has no happens-before edge to the constructor's writes. A thread can see a non-null
 * reference to a half-built object.
 *
 * <p>The lock still guarantees the factory runs once per lazy holder; that is asserted. The
 * half-built sighting is legal but machine-dependent, so it is not counted or printed.
 */
public class BrokenDoubleCheckedLockingDemo {

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

    static class BrokenLazy {
        private Resource resource;                 // BUG: not volatile

        Resource get() {
            if (resource == null) {
                synchronized (this) {
                    if (resource == null) {
                        resource = new Resource();
                    }
                }
            }
            return resource;                       // unlocked read, no edge
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int rounds = 5_000;
        BrokenLazy[] lazies = new BrokenLazy[rounds];
        for (int i = 0; i < rounds; i++) {
            lazies[i] = new BrokenLazy();
        }
        CyclicBarrier start = new CyclicBarrier(2);

        Runnable body = () -> {
            try {
                for (int i = 0; i < rounds; i++) {
                    start.await();
                    Resource r = lazies[i].get();
                    int a = r.a;                   // may be 0 on a weakly ordered machine
                    int b = r.b;
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        };
        Thread t1 = new Thread(body);
        Thread t2 = new Thread(body);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("rounds:                       " + rounds);
        System.out.println("resources created == rounds:  " + (CREATED.get() == rounds));
        System.out.println("half-built sightings:         legal here, not counted");
    }
}
