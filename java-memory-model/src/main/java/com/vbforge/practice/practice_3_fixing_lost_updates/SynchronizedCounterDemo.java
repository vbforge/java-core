package com.vbforge.practice.practice_3_fixing_lost_updates;

/**
 * Fix 1: make the read-modify-write one critical section. The lock gives both mutual
 * exclusion and a happens-before edge (unlock -> next lock), so a plain field is enough.
 */
public class SynchronizedCounterDemo {

    private long count;   // plain field: always accessed under the same lock

    synchronized void increment() {
        count++;
    }

    synchronized long get() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        final int threads = 4;
        final int perThread = 100_000;
        SynchronizedCounterDemo counter = new SynchronizedCounterDemo();

        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    counter.increment();
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
    }
}
