package com.vbforge.practice.practice_8_monitor_edges_and_wait_notify;

/**
 * Unlock happens-before every later lock of the SAME monitor. The writer fills plain fields
 * under the lock; the reader polls under the same lock and, once it sees {@code ready}, sees
 * the fields too. Reading {@code ready} under a DIFFERENT lock object would create no edge.
 */
public class MonitorUnlockLockEdgeDemo {

    private static final Object LOCK = new Object();
    private static int a;                  // all three are plain, guarded by LOCK
    private static int b;
    private static boolean ready;

    public static void main(String[] args) throws InterruptedException {
        int[] seen = new int[2];

        Thread reader = new Thread(() -> {
            while (true) {
                synchronized (LOCK) {
                    if (ready) {
                        seen[0] = a;
                        seen[1] = b;
                        return;
                    }
                }
                Thread.yield();
            }
        });
        reader.start();

        synchronized (LOCK) {
            a = 1;
            b = 2;
            ready = true;
        }

        reader.join();
        System.out.println("reader saw a = " + seen[0] + ", b = " + seen[1] + "  (expected 1 and 2)");
    }
}
