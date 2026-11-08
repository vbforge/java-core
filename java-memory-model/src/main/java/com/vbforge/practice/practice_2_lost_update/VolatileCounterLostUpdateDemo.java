package com.vbforge.practice.practice_2_lost_update;

/**
 * {@code volatile} gives visibility, not atomicity. Four threads run {@code count++} on a
 * volatile field; updates can be lost.
 *
 * <p>Only what is guaranteed is printed: increments can be lost, but never invented, so the
 * result is never above the expected value. Whether any update was lost in a given run is
 * timing-dependent, so it is not printed.
 */
public class VolatileCounterLostUpdateDemo {

    private static volatile long count;

    public static void main(String[] args) throws InterruptedException {
        final int threads = 4;
        final int perThread = 100_000;

        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    count++;      // read, add, write: NOT atomic, even though count is volatile
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
