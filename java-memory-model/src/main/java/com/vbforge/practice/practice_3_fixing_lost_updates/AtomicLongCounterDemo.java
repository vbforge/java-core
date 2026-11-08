package com.vbforge.practice.practice_3_fixing_lost_updates;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Fix 2: a lock-free counter. {@code incrementAndGet} is one atomic step (a hardware
 * compare-and-swap loop) and also has volatile read/write semantics.
 */
public class AtomicLongCounterDemo {

    public static void main(String[] args) throws InterruptedException {
        final int threads = 4;
        final int perThread = 100_000;
        AtomicLong counter = new AtomicLong();

        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    counter.incrementAndGet();
                }
            });
            workers[i].start();
        }
        for (Thread t : workers) {
            t.join();
        }

        long expected = (long) threads * perThread;
        System.out.println("expected: " + expected + ", got: " + counter.get());
        System.out.println("exact:    " + (counter.get() == expected));

        // The rest of the small API, single-threaded so the output is fixed.
        AtomicLong value = new AtomicLong(10);
        System.out.println("addAndGet(5)         -> " + value.addAndGet(5));          // 15
        System.out.println("updateAndGet(v -> *2) -> " + value.updateAndGet(v -> v * 2)); // 30
        System.out.println("getAndSet(1)         -> " + value.getAndSet(1));          // old value 30
        System.out.println("now                  -> " + value.get());                 // 1
    }
}
