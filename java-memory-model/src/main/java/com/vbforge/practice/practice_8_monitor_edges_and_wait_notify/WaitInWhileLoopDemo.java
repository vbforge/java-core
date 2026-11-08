package com.vbforge.practice.practice_8_monitor_edges_and_wait_notify;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code wait()} belongs in a {@code while} loop that re-checks the condition:
 * <ul>
 *   <li>the condition is checked BEFORE waiting, so a signal that already happened is not
 *       missed (a notify is not remembered; waiting unconditionally could hang forever);</li>
 *   <li>the condition is checked again after EVERY wake-up, because spurious wake-ups exist
 *       and another thread may have consumed the state in the meantime.</li>
 * </ul>
 * With {@code if (!ready) wait()} the second check is lost.
 */
public class WaitInWhileLoopDemo {

    private static final Object LOCK = new Object();
    private static boolean ready;

    static void awaitReady() throws InterruptedException {
        synchronized (LOCK) {
            while (!ready) {          // while, not if
                LOCK.wait();
            }
        }
    }

    static void signalReady() {
        synchronized (LOCK) {
            ready = true;
            LOCK.notifyAll();         // wake everybody, each re-checks the condition
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger passed = new AtomicInteger();

        // Part 1: three threads wait, then the condition becomes true.
        Thread[] waiters = new Thread[3];
        for (int i = 0; i < waiters.length; i++) {
            waiters[i] = new Thread(() -> {
                try {
                    awaitReady();
                    passed.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            waiters[i].setDaemon(true);
            waiters[i].start();
        }
        signalReady();
        for (Thread t : waiters) {
            t.join(5_000);
        }
        System.out.println("waiters released: " + passed.get() + " of 3");

        // Part 2: the condition was set (and notified) BEFORE this thread starts waiting.
        // The while check sees ready == true and does not block.
        Thread late = new Thread(() -> {
            try {
                awaitReady();
                passed.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        late.setDaemon(true);
        late.start();
        late.join(5_000);
        System.out.println("late waiter passed without blocking: " + !late.isAlive());
    }
}
