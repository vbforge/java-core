package com.vbforge.practice.queue.practice_9_concurrent_linked_queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ConcurrentLinkedQueue: lock-free FIFO queue (CAS operations instead of locks).
 * Never blocks, so it gives no back-pressure; good for work queues with many producers/consumers.
 * poll() returns null on an empty queue (contrast with BlockingQueue.take(), which waits).
 */
public class ConcurrentLinkedQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        int tasks = 1000;
        int producerCount = 4;
        AtomicInteger totalProduced = new AtomicInteger(0);
        AtomicInteger totalConsumed = new AtomicInteger(0);

        // Many producer threads
        List<Thread> producers = new ArrayList<>();
        for (int i = 0; i < producerCount; i++) {
            producers.add(new Thread(() -> {
                for (int j = 0; j < tasks / producerCount; j++) {
                    queue.offer(j);
                    totalProduced.incrementAndGet();
                }
            }));
        }

        producers.forEach(Thread::start);
        for (Thread t : producers) {
            t.join();
        }

        // Drain the queue
        Integer item;
        while ((item = queue.poll()) != null) {
            totalConsumed.incrementAndGet();
        }

        System.out.println("Produced: " + totalProduced.get() + ", Consumed: " + totalConsumed.get());
        System.out.println("Queue empty after drain: " + queue.isEmpty());
    }
}
