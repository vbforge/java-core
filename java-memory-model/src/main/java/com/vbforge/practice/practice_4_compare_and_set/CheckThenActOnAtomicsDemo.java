package com.vbforge.practice.practice_4_compare_and_set;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Check-then-act is two operations, even when each one is atomic. Eight threads try to
 * increment up to a limit of 100; between the check and the increment other threads may
 * also pass the check, so the counter can end ABOVE the limit.
 *
 * <p>Guaranteed and printed: the final value is at least the limit. Whether it overshot in
 * this run is timing-dependent and is not printed.
 */
public class CheckThenActOnAtomicsDemo {

    private static final int LIMIT = 100;
    private static final AtomicInteger counter = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        Thread[] workers = new Thread[8];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < LIMIT; j++) {
                    if (counter.get() < LIMIT) {          // check ...
                        counter.incrementAndGet();        // ... then act: the pair is not atomic
                    }
                }
            });
            workers[i].start();
        }
        for (Thread t : workers) {
            t.join();
        }

        System.out.println("limit:                " + LIMIT);
        System.out.println("final >= limit:       " + (counter.get() >= LIMIT));
        System.out.println("final == limit:       not guaranteed (timing dependent, not printed)");
    }
}
