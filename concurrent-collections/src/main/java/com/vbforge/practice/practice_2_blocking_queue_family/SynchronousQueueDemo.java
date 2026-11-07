package com.vbforge.practice.practice_2_blocking_queue_family;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── WHAT IT IS ────────────────────────────────────────────────────────────
        // A queue with ZERO capacity — a pure RENDEZVOUS point.
        // • put(e) blocks until another thread calls take() (and vice-versa).
        // • offer(e) returns false unless a consumer is ALREADY waiting.
        // • size() is ALWAYS 0; peek() is ALWAYS null; it is ALWAYS empty.
        // • fair = true gives FIFO hand-off between waiting threads (avoids starvation).
        // • Used internally by Executors.newCachedThreadPool() to hand a task directly
        //   to an idle thread, or to spawn a new thread if none is waiting.
        SynchronousQueue<String> queue = new SynchronousQueue<>(/* fair = */ true);

        Thread producer = Thread.ofVirtual().name("producer").start(() -> {
            try {
                System.out.println("Producer: put(\"ping\") - will block until consumed");
                queue.put("ping");
                System.out.println("Producer: put() returned -> hand-off complete");

                // With no waiting consumer, timed offer() fails
                boolean offered = queue.offer("nobody", 200, TimeUnit.MILLISECONDS);
                System.out.println("Producer: offer(200ms) -> " + offered); // false
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread.sleep(300);
        System.out.println("Consumer: take() ...");
        System.out.println("Consumer: got \"" + queue.take() + "\"");   // frees the producer

        producer.join();

        // These are always true for SynchronousQueue:
        System.out.println("size()  = " + queue.size());   // 0
        System.out.println("peek()  = " + queue.peek());   // null
        System.out.println("isEmpty = " + queue.isEmpty()); // true

        // Bonus: offer() with no consumer at all
        System.out.println("offer(no consumer) = " + queue.offer("x")); // false
    }
}
