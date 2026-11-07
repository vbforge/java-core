package com.vbforge.practice.queue.practice_19_synchronous_queue_problems;

import java.util.concurrent.SynchronousQueue;

/**
 * Ping-Pong synchronization.
 * Goal: two threads print alternately: ping, pong, ping, pong...
 * Two SynchronousQueues, one per direction: a thread can only continue after the other thread has taken its message.
 */
public class PingPongDemo {

    private static final int ROUNDS = 3;

    public static void main(String[] args) throws InterruptedException {

        SynchronousQueue<String> toPong = new SynchronousQueue<>();
        SynchronousQueue<String> toPing = new SynchronousQueue<>();

        Thread ping = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    System.out.println("ping");
                    toPong.put("ping");
                    toPing.take();      // wait for the pong
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread pong = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    toPong.take();      // wait for the ping
                    System.out.println("pong");
                    toPing.put("pong");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        ping.start();
        pong.start();
        ping.join();
        pong.join();
    }
}
