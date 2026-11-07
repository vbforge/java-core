package com.vbforge.practice.queue.practice_16_linked_transfer_queue_basics;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Producer-Consumer with synchronous transfer.
 * Scenario: transfer(e) hands an element over only when a consumer is ready;
 * the producer stays blocked until the consumer has taken it (put() would return immediately).
 * The consumer shows up late (300 ms), so the producer must have been blocked for that time.
 */
public class TransferBlocksUntilConsumedDemo {

    private static final long CONSUMER_DELAY_MS = 300;

    public static void main(String[] args) throws InterruptedException {

        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();
        AtomicLong producerBlockedMs = new AtomicLong();
        AtomicReference<String> received = new AtomicReference<>();

        Thread producer = new Thread(() -> {
            try {
                long start = System.nanoTime();
                queue.transfer("data-123"); // blocks until the consumer takes it
                producerBlockedMs.set(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // simulate a delay before the consumer is ready
        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(CONSUMER_DELAY_MS);
                received.set(queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        System.out.println("Consumer received: " + received.get());
        System.out.println("Producer was blocked until the consumer arrived: "
                + (producerBlockedMs.get() >= CONSUMER_DELAY_MS / 2));
        System.out.println("Nothing left buffered: " + queue.isEmpty());
    }
}
