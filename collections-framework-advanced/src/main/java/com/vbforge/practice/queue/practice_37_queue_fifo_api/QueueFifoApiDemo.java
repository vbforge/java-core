package com.vbforge.practice.queue.practice_37_queue_fifo_api;

import java.util.*;

/**
 * Queue as FIFO: offer/peek/poll (special value) vs element/remove (exception), on an ArrayDeque.
 */
public class QueueFifoApiDemo {

    // ─────────────────────────────────────────────────────────────
    // 1. Queue as FIFO (ArrayDeque)
    // ─────────────────────────────────────────────────────────────

    /**
     * ArrayDeque is the recommended Queue implementation.
     * Backed by a resizable circular array — no node overhead unlike LinkedList.
     * Faster than LinkedList for most queue operations.
     */
    public static void queueFIFO() {
        System.out.println("=== Queue FIFO (ArrayDeque) ===");

        Queue<String> queue = new ArrayDeque<>();

        // offer — adds to tail; returns true (bounded queues return false when full)
        queue.offer("task-1");
        queue.offer("task-2");
        queue.offer("task-3");
        System.out.println("Queue: " + queue);  // [task-1, task-2, task-3]

        // peek — inspect head WITHOUT removing; null if empty
        System.out.println("peek():  " + queue.peek());    // task-1
        System.out.println("size still: " + queue.size()); // 3

        // poll — remove and return head; null if empty
        System.out.println("poll(): " + queue.poll());     // task-1
        System.out.println("poll(): " + queue.poll());     // task-2
        System.out.println("Queue after polls: " + queue); // [task-3]

        // Empty queue behavior — safe methods return null, NOT exception
        queue.poll(); // drains last element
        System.out.println("poll() on empty: " + queue.poll()); // null (no exception)
        System.out.println("peek() on empty: " + queue.peek()); // null

        // Throwing variants — avoid in production unless truly exceptional
        Queue<String> q2 = new ArrayDeque<>(List.of("x"));
        System.out.println("element(): " + q2.element()); // x (throws if empty)
        try {
            q2.poll();
            q2.remove(); // throws NoSuchElementException on empty queue
        } catch (NoSuchElementException e) {
            System.out.println("remove() on empty: NoSuchElementException — use poll()");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. Deque as Stack (LIFO)
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 3. Deque — both-ends operations
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 4. PriorityQueue — min-heap, ordered retrieval
    // ─────────────────────────────────────────────────────────────



    // ─────────────────────────────────────────────────────────────
    // 5. Real-world patterns
    // ─────────────────────────────────────────────────────────────




    // ─────────────────────────────────────────────────────────────
    // main
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        queueFIFO();
    }
}
