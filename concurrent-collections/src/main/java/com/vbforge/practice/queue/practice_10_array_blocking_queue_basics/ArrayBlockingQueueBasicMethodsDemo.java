package com.vbforge.practice.queue.practice_10_array_blocking_queue_basics;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * ArrayBlockingQueue is a bounded, FIFO, array-backed blocking queue.
 * Thread-safe; the capacity is fixed at construction.
 * put() blocks when full, take() blocks when empty.
 */
public class ArrayBlockingQueueBasicMethodsDemo {

    public static void main(String[] args) throws InterruptedException {

        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(5);

        // Add elements
        queue.put("A1");                              // Blocks if full
        queue.put("A2");
        queue.put("A3");
        System.out.println("offer B: " + queue.offer("B")); // Returns false if full
        System.out.println("add C: " + queue.add("C"));     // Throws IllegalStateException if full
        System.out.println("FIFO order: " + queue + ", remaining capacity: " + queue.remainingCapacity());

        // Remove elements
        System.out.println("take: " + queue.take());        // Blocks if empty
        System.out.println("poll: " + queue.poll());        // Returns null if empty
        System.out.println("remove: " + queue.remove());    // Throws NoSuchElementException if empty

        // Peek
        System.out.println("peek: " + queue.peek());        // Returns head or null
        System.out.println("element: " + queue.element());  // Returns head or throws exception

        // Iteration is weakly consistent (reflects the queue at some point, not necessarily every later update)
        for (String item : queue) {
            System.out.println("item: " + item);
        }

        // Removing elements
        System.out.println("remove(\"B\"): " + queue.remove("B")); // Removes a specific object, true if it was there
        queue.clear();                                             // Removes all elements

        // On an EMPTY queue
        System.out.println("poll on empty: " + queue.poll());
        System.out.println("peek on empty: " + queue.peek());
        try {
            queue.remove();
        } catch (NoSuchElementException e) {
            System.out.println("remove on empty: NoSuchElementException");
        }
    }
}
