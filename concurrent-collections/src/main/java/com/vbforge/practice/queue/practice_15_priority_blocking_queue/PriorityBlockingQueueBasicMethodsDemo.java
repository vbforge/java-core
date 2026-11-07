package com.vbforge.practice.queue.practice_15_priority_blocking_queue;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * PriorityBlockingQueue: unbounded, thread-safe queue ordered by natural order or a Comparator (not FIFO).
 *   - backed by a heap, grows as needed
 *   - insertion never blocks (put() == add() == offer())
 *   - retrieval (take()) blocks only when the queue is EMPTY; it can never be "full"
 *   - iteration order is NOT priority order: use poll()/take() to get elements in priority order
 */
public class PriorityBlockingQueueBasicMethodsDemo {

    public static void main(String[] args) throws InterruptedException {

        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();

        // Add elements
        queue.add(4);
        queue.add(9);
        queue.put(2);
        queue.offer(3);

        // Remove elements
        System.out.println("take: " + queue.take()); // 2 (blocks if empty)
        System.out.println("poll: " + queue.poll()); // 3 (null if empty)

        // Peek
        System.out.println("peek: " + queue.peek()); // 4, the smallest (highest priority) element

        // Custom Comparator example: shortest string first
        PriorityBlockingQueue<String> queueWithComparator =
                new PriorityBlockingQueue<>(10, Comparator.comparingInt(String::length));
        queueWithComparator.add("apple");
        queueWithComparator.add("kiwi");
        queueWithComparator.add("banana");

        while (!queueWithComparator.isEmpty()) {
            System.out.println(queueWithComparator.poll()); // kiwi, apple, banana
        }

        // Iteration: not guaranteed to be in priority order
        for (Integer item : queue) {
            System.out.println("item: " + item);
        }

        // Removing elements
        System.out.println("remove(9): " + queue.remove(9)); // removes a specific object (true if present)
        queue.clear();                                       // clears all elements
    }
}
