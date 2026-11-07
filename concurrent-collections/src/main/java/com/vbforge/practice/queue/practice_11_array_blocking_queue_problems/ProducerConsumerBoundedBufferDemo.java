package com.vbforge.practice.queue.practice_11_array_blocking_queue_problems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Producer-Consumer with a bounded buffer.
 * Goal: the producer waits when the queue is full, the consumer waits when it is empty.
 * The producer is faster than the consumer, so it is throttled by the full buffer (back-pressure).
 */
public class ProducerConsumerBoundedBufferDemo {

    public static final int MAX_BUFFER_SIZE = 5;
    public static final int ITEMS = 10;

    public static void main(String[] args) throws InterruptedException {

        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(MAX_BUFFER_SIZE);
        List<Integer> produced = new ArrayList<>(); // written by the producer thread only
        List<Integer> consumed = new ArrayList<>(); // written by the consumer thread only

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= ITEMS; i++) {
                    queue.put(i);                       // blocks if full
                    produced.add(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= ITEMS; i++) {
                    int value = queue.take();           // blocks if empty
                    TimeUnit.MILLISECONDS.sleep(50);    // short delay to simulate consuming work
                    consumed.add(value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        // lists are read only after join, so printing is safe and the output is stable
        System.out.println("Produced: " + produced);
        System.out.println("Consumed: " + consumed);
        System.out.println("Same order: " + produced.equals(consumed));
    }
}
