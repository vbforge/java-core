package com.vbforge.practice.practice_19_initialization_on_demand_holder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Initialization-on-demand holder: a lazy static singleton with NO volatile and NO lock in
 * your code. The JVM initialises a class lazily, on first use, exactly once, under an
 * internal lock, and class initialisation happens-before every use of the class.
 *
 * <p>The nested Holder is only initialised when getInstance() first touches it, so merely
 * loading or using the outer class does not create the instance.
 */
public class HolderIdiomDemo {

    static final AtomicInteger CREATED = new AtomicInteger();   // lives in the OUTER class on purpose

    static class Singleton {

        private Singleton() {
            CREATED.incrementAndGet();
        }

        private static class Holder {
            static final Singleton INSTANCE = new Singleton();
        }

        static Singleton getInstance() {
            return Holder.INSTANCE;          // first call triggers Holder's initialisation
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("created before first use: " + CREATED.get());   // 0

        final int threads = 8;
        Singleton[] seen = new Singleton[threads];
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int slot = i;
            workers[i] = new Thread(() -> seen[slot] = Singleton.getInstance());
            workers[i].start();
        }
        for (Thread t : workers) {
            t.join();
        }

        boolean sameInstance = true;
        for (Singleton s : seen) {
            sameInstance &= (s == seen[0]);
        }
        System.out.println("created after 8 threads:  " + CREATED.get());   // 1
        System.out.println("all threads same instance: " + sameInstance);
    }
}
