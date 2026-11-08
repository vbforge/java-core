package com.vbforge.practice.practice_8_monitor_edges_and_wait_notify;

/**
 * Monitor hand-off with wait / notifyAll: two threads alternate strictly. The {@code turn}
 * field is only read and written under the lock, and each thread waits in a while loop.
 * The log is built under the lock and printed by main, so the output is the same every run.
 */
public class PingPongDemo {

    private static final Object LOCK = new Object();
    private static final int ROUNDS = 5;
    private static boolean pingsTurn = true;                   // guarded by LOCK
    private static final StringBuilder log = new StringBuilder(); // guarded by LOCK

    private static void play(String name, boolean myTurnIsPing) {
        for (int i = 1; i <= ROUNDS; i++) {
            synchronized (LOCK) {
                while (pingsTurn != myTurnIsPing) {
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                log.append(name).append(' ').append(i).append('\n');
                pingsTurn = !pingsTurn;
                LOCK.notifyAll();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread ping = new Thread(() -> play("ping", true));
        Thread pong = new Thread(() -> play("pong", false));
        ping.setDaemon(true);
        pong.setDaemon(true);
        pong.start();                 // start order does not matter: pong waits for its turn
        ping.start();
        ping.join(5_000);
        pong.join(5_000);

        synchronized (LOCK) {
            System.out.print(log);
        }
    }
}
