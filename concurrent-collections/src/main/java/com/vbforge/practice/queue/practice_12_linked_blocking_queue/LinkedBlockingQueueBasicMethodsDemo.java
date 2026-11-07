package com.vbforge.practice.queue.practice_12_linked_blocking_queue;

import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LinkedBlockingQueue is an optionally bounded, FIFO, linked-node blocking queue.
 *   - unbounded by default: capacity Integer.MAX_VALUE, so there is NO back-pressure
 *   - bounded when a capacity is passed to the constructor
 * Useful for producer-consumer scenarios with heavy contention.
 */
public class LinkedBlockingQueueBasicMethodsDemo {

    public static void main(String[] args) throws InterruptedException {

        // Unbounded queue (Integer.MAX_VALUE capacity by default)
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        System.out.println("Unbounded remaining capacity: " + queue.remainingCapacity());

        // Add elements
        queue.put("A");        // blocks if full
        queue.offer("B");      // returns false if full
        queue.add("C");        // throws IllegalStateException if full
        System.out.println("Queue: " + queue);

        // Peek (the head is not removed)
        System.out.println("peek: " + queue.peek());       // returns head or null
        System.out.println("element: " + queue.element()); // returns head or throws exception

        // Iterators are weakly consistent (no ConcurrentModificationException)
        for (String item : queue) {
            System.out.println("item: " + item);
        }

        // Remove elements
        System.out.println("take: " + queue.take());     // blocks if empty
        System.out.println("poll: " + queue.poll());     // returns null if empty
        System.out.println("remove: " + queue.remove()); // throws NoSuchElementException if empty

        // On an EMPTY queue
        System.out.println("peek on empty: " + queue.peek());
        try {
            queue.element();
        } catch (NoSuchElementException e) {
            System.out.println("element on empty: NoSuchElementException");
        }

        // Specific removal and clearing
        queue.add("D");
        queue.add("E");
        System.out.println("remove(\"D\"): " + queue.remove("D"));
        queue.clear();

        // Bounded queue
        LinkedBlockingQueue<String> boundedQueue = new LinkedBlockingQueue<>(2);
        System.out.println("offer 1: " + boundedQueue.offer("x"));
        System.out.println("offer 2: " + boundedQueue.offer("y"));
        System.out.println("offer 3 (full): " + boundedQueue.offer("z")); // false
        try {
            boundedQueue.add("z");
        } catch (IllegalStateException e) {
            System.out.println("add on full: IllegalStateException");
        }
    }
}
