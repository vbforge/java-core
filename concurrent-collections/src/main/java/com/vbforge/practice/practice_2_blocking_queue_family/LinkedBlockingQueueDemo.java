package com.vbforge.practice.practice_2_blocking_queue_family;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class LinkedBlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── WHAT IT IS ────────────────────────────────────────────────────────────
        // Linked-node based, OPTIONALLY bounded FIFO queue.
        // • Uses TWO separate locks (putLock for producers, takeLock for consumers)
        //   => producers & consumers rarely block each other, giving HIGH throughput.
        // • Default no-arg constructor capacity = Integer.MAX_VALUE (effectively
        //   unbounded) -> ALWAYS pass an explicit capacity in production to avoid OOM.
        // • This is the work queue used by Executors.newFixedThreadPool().
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(2); // small cap for demo

        Thread producer = Thread.ofVirtual().name("producer").start(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.put(i);                  // blocks only while 2 items are buffered
                    System.out.println("put " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            // poll(timeout) is the "safe" consumer call: it never waits forever.
            Integer value = queue.poll(1, TimeUnit.SECONDS);
            System.out.println("poll -> " + value);
        }

        producer.join();
    }
}
