package com.vbforge.practice.queue.practice_16_linked_transfer_queue_basics;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * LinkedTransferQueue is an unbounded, linked-node BLOCKING queue that implements TransferQueue.
 * Key feature: a producer can wait until a consumer actually receives the element (transfer).
 *   - unbounded, so put/offer never block
 *   - efficient under both high and low contention
 *   - ideal for hand-off scenarios (e.g. work distribution in a thread pool)
 *
 * Methods:
 *   put(e)                   inserts, never blocks here (unbounded)
 *   offer(e)                 inserts immediately
 *   poll()                   removes the head, null if empty
 *   take()                   removes the head, waits if necessary
 *   transfer(e)              inserts and waits until a consumer receives it
 *   tryTransfer(e)           transfers only if a consumer is already waiting, otherwise false (nothing is queued)
 *   tryTransfer(e, t, u)     waits for a consumer up to the timeout, true if transferred
 *   hasWaitingConsumer()     true if at least one consumer is waiting
 *   getWaitingConsumerCount() estimated number of waiting consumers
 */
public class LinkedTransferQueueBasicMethodsDemo {

    public static void main(String[] args) throws InterruptedException {

        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();

        // no consumer is waiting
        System.out.println("hasWaitingConsumer: " + queue.hasWaitingConsumer());
        System.out.println("waiting consumers: " + queue.getWaitingConsumerCount());
        System.out.println("tryTransfer: " + queue.tryTransfer("X"));                                // false
        System.out.println("tryTransfer with timeout: " + queue.tryTransfer("X", 100, TimeUnit.MILLISECONDS)); // false
        System.out.println("queue still empty: " + queue.isEmpty());

        // iteration: FIFO order like a regular queue
        queue.add("A");
        queue.add("B");
        queue.add("C");

        for (String item : queue) {
            System.out.println("Item: " + item);
        }

        // removing
        System.out.println("remove(\"B\"): " + queue.remove("B")); // removes a specific element
        System.out.println("poll: " + queue.poll());               // removes the head (A)
        queue.clear();                                             // removes all
        System.out.println("after clear: " + queue);
    }
}
