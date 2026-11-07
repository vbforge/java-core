package com.vbforge.practice.queue.practice_17_linked_transfer_queue_problems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * Backpressure coordination between threads.
 * Scenario: only produce data when a consumer is present. transfer() blocks the producer until
 * the (slow) consumer has taken the item, so the producer is paced by the consumer
 * and nothing ever piles up in the queue.
 */
public class BackPressureSimulatorDemo {

    private static final int ITEMS = 3;
    private static final long CONSUMER_DELAY_MS = 300;

    public static void main(String[] args) throws InterruptedException {

        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();
        List<Long> blockedMs = new ArrayList<>(); // written by the producer thread only
        List<String> consumed = new ArrayList<>(); // written by the consumer thread only

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= ITEMS; i++) {
                    long start = System.nanoTime();
                    queue.transfer("Data-" + i); // blocks until consumed
                    blockedMs.add(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= ITEMS; i++) {
                    Thread.sleep(CONSUMER_DELAY_MS);
                    consumed.add(queue.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        System.out.println("Consumed: " + consumed);
        boolean paced = blockedMs.size() == ITEMS && blockedMs.stream().allMatch(ms -> ms >= CONSUMER_DELAY_MS / 2);
        System.out.println("Every transfer waited for the slow consumer: " + paced);
        System.out.println("Items left in the queue: " + queue.size());
    }
}
