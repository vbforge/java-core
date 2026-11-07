package com.vbforge.practice.practice_2_blocking_queue_family;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class LinkedTransferQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── WHAT IT IS ────────────────────────────────────────────────────────────
        // An UNBOUNDED queue (CAS-based, no locks on the hot path).
        // Its killer feature is DIRECT HAND-OFF:
        // • transfer(e)    – blocks until a consumer actually RECEIVES e.
        // • tryTransfer(e) – non-blocking: enqueues ONLY if a consumer is waiting.
        // • tryTransfer(e, timeout, unit) – waits up to the timeout.
        // If you only use put()/take() it behaves exactly like a normal unbounded queue.
        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();

        // ---- 1. transfer() blocks until the element is consumed ------------------
        Thread producer = Thread.ofVirtual().name("producer").start(() -> {
            try {
                System.out.println("Producer: calling transfer(\"hello\")");
                queue.transfer("hello");       // returns only after the consumer takes it
                System.out.println("Producer: transfer() returned (consumer got it)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread.sleep(500);
        System.out.println("hasWaitingConsumer = " + queue.hasWaitingConsumer());
        System.out.println("Consumer: take() -> " + queue.take());   // releases producer
        producer.join();

        // ---- 2. tryTransfer without a waiting consumer -> element NOT enqueued ---
        System.out.println("tryTransfer(no consumer) = " + queue.tryTransfer("dropped")); // false
        System.out.println("queue size after that       = " + queue.size());             // 0

        // ---- 3. tryTransfer with a timeout --------------------------------------
        System.out.println("tryTransfer(100ms) = " + queue.tryTransfer("dropped2", 100, TimeUnit.MILLISECONDS));

        // ---- 4. put()/take() behave like a normal unbounded queue ---------------
        queue.put("buffered");
        System.out.println("take() -> " + queue.take()); // "buffered"
    }
}
