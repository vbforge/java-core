package com.vbforge.practice.queue.practice_12_linked_blocking_queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Producer-Consumer with many producers.
 * Three producers and a single consumer share one bounded LinkedBlockingQueue.
 */
public class MultiProducerSingleConsumerDemo {

    public static final int BUFFER_SIZE = 10;
    public static final int PRODUCERS = 3;
    public static final int ITEMS_PER_PRODUCER = 5;

    private static final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>(BUFFER_SIZE);

    public static void main(String[] args) throws InterruptedException {

        List<Integer> consumed = new ArrayList<>(); // written by the consumer thread only

        // producer task
        Runnable producer = () -> {
            try {
                for (int i = 1; i <= ITEMS_PER_PRODUCER; i++) {
                    queue.put(i);
                    TimeUnit.MILLISECONDS.sleep(20);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // consumer task: it knows how many items to expect
        Runnable consumer = () -> {
            try {
                for (int i = 1; i <= PRODUCERS * ITEMS_PER_PRODUCER; i++) {
                    consumed.add(queue.take());
                    TimeUnit.MILLISECONDS.sleep(30);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= PRODUCERS; i++) {
            threads.add(new Thread(producer, "Producer-" + i));
        }
        threads.add(new Thread(consumer, "Consumer-1"));

        threads.forEach(Thread::start);
        for (Thread t : threads) {
            t.join();
        }

        // interleaving between producers is up to the scheduler, so print an order-independent summary
        int sum = consumed.stream().mapToInt(Integer::intValue).sum();
        Collections.sort(consumed);
        System.out.println("Consumed " + consumed.size() + " items, sum = " + sum);
        System.out.println("Sorted: " + consumed);
    }
}
