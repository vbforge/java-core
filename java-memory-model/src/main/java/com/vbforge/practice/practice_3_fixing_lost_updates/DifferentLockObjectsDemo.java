package com.vbforge.practice.practice_3_fixing_lost_updates;

/**
 * A lock only protects data when every thread uses the SAME lock object. Here each thread
 * locks its own object, so there is no mutual exclusion and no happens-before edge between
 * the threads: updates can be lost again.
 *
 * <p>Only the guaranteed part is printed (never above the expected value).
 */
public class DifferentLockObjectsDemo {

    private static long count;

    public static void main(String[] args) throws InterruptedException {
        final int threads = 4;
        final int perThread = 100_000;

        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final Object ownLock = new Object();   // a different lock per thread: the bug
            workers[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    synchronized (ownLock) {
                        count++;
                    }
                }
            });
            workers[i].start();
        }
        for (Thread t : workers) {
            t.join();
        }

        long expected = (long) threads * perThread;
        System.out.println("expected:             " + expected);
        System.out.println("never above expected: " + (count <= expected));
        System.out.println("equal to expected:    not guaranteed (timing dependent, not printed)");
    }
}
