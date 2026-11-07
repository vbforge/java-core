package com.vbforge.practice.queue.practice_18_synchronous_queue_basics;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SynchronousQueue is a blocking queue with NO internal capacity.
 * Every put() must wait for a matching take() and vice versa: a direct one-to-one handoff.
 *   - zero capacity, no buffering (size() is always 0, peek() is always null)
 *   - efficient where the transfer must be immediate
 *   - used internally by Executors.newCachedThreadPool()
 *
 * Methods:
 *   put(e)           blocks until another thread takes it
 *   take()           blocks until another thread puts an element
 *   offer(e)         adds the element only if a consumer is already waiting
 *   poll()           takes an element only if a producer is already waiting
 *   offer(e, t, u)   waits up to the timeout for a consumer
 *   poll(t, u)       waits up to the timeout for a producer
 */
public class SynchronousQueueBasicMethodsDemo {

    private static final long CONSUMER_DELAY_MS = 300;

    public static void main(String[] args) throws InterruptedException {

        SynchronousQueue<String> queue = new SynchronousQueue<>();

        // nobody is waiting on the other side
        System.out.println("offer without consumer: " + queue.offer("Message-0"));                    // false
        System.out.println("poll without producer: " + queue.poll());                                   // null
        System.out.println("offer with timeout: " + queue.offer("Message-0", 100, TimeUnit.MILLISECONDS)); // false
        System.out.println("size: " + queue.size() + ", peek: " + queue.peek());                        // 0, null

        AtomicLong putBlockedMs = new AtomicLong();
        AtomicReference<String> consumed = new AtomicReference<>();

        Thread producer = new Thread(() -> {
            try {
                long start = System.nanoTime();
                queue.put("Message-1"); // waits for the consumer
                putBlockedMs.set(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(CONSUMER_DELAY_MS); // simulate delay
                consumed.set(queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        System.out.println("Consumed: " + consumed.get());
        System.out.println("put() waited for the consumer: " + (putBlockedMs.get() >= CONSUMER_DELAY_MS / 2));
    }
}
