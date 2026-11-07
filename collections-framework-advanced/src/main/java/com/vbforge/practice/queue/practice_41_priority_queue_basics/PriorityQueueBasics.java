package com.vbforge.practice.queue.practice_41_priority_queue_basics;

import java.util.PriorityQueue;

/**
 * A PriorityQueue is a heap-based data structure in Java that stores elements according to priority, not insertion order.
 * By default, it uses natural ordering (i.e., smallest first), but we can provide a custom comparator.
 * */
public class PriorityQueueBasics {
    public static void main(String[] args) {

        PriorityQueue<Integer> pq = new PriorityQueue<>();

        pq.offer(10);
        pq.offer(20);
        pq.offer(30);
        pq.offer(40);
        pq.offer(50);

        System.out.println("PriorityQueue: " + pq); // Heap order, not insertion order

        // Access smallest element
        System.out.println("Peek: " + pq.peek()); // 10

        // Remove smallest element
        System.out.println("Poll: " + pq.poll()); // 10
        System.out.println("After poll: " + pq);

        // Iterating (NOT sorted!)
        System.out.println("Iterating:");
        for (int num : pq) {
            System.out.println(num); // Order is NOT guaranteed
        }

        // Remove specific element
        pq.remove(30);
        System.out.println("After removing 30: " + pq);


    }
}
