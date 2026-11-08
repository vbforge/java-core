package com.vbforge.practice.practice_4_compare_and_set;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The same bounded counter, done correctly: the check and the update are one CAS. If another
 * thread got in between, the CAS fails and we re-check against the NEW value.
 * Result is exact on every run: exactly LIMIT increments succeed.
 */
public class BoundedCounterWithCasDemo {

    private static final int LIMIT = 100;
    private static final AtomicInteger counter = new AtomicInteger();

    static boolean incrementIfBelow(AtomicInteger value, int limit) {
        int current;
        do {
            current = value.get();
            if (current >= limit) {
                return false;                  // limit reached, nothing to do
            }
        } while (!value.compareAndSet(current, current + 1));
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        final int threads = 8;
        int[] successes = new int[threads];    // each thread writes its own slot, read after join

        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int slot = i;
            workers[i] = new Thread(() -> {
                for (int j = 0; j < LIMIT; j++) {
                    if (incrementIfBelow(counter, LIMIT)) {
                        successes[slot]++;
                    }
                }
            });
            workers[i].start();
        }
        for (Thread t : workers) {
            t.join();
        }

        int totalSuccesses = 0;
        for (int s : successes) {
            totalSuccesses += s;
        }
        System.out.println("limit:             " + LIMIT);
        System.out.println("final value:       " + counter.get());
        System.out.println("successful calls:  " + totalSuccesses);
    }
}
