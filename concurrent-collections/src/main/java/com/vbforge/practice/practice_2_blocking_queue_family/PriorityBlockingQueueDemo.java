package com.vbforge.practice.practice_2_blocking_queue_family;

import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueDemo {

    // ── WHAT IT IS ────────────────────────────────────────────────────────────────
    // An UNBOUNDED, thread-safe min-heap (like PriorityQueue, but blocking + safe).
    // • put() NEVER blocks (unbounded) -> only take() can block, when empty.
    // • Elements must be Comparable, or you must supply a Comparator in the ctor.
    // • null is NOT allowed.
    // • Only the HEAD is guaranteed to be the smallest; iteration order is NOT sorted.
    // • Elements with equal priority have NO guaranteed ordering (not stable).
    record Job(int priority, String name) implements Comparable<Job> {
        @Override
        public int compareTo(Job other) {
            // lower number = higher priority
            return Integer.compare(this.priority, other.priority);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        PriorityBlockingQueue<Job> queue = new PriorityBlockingQueue<>();

        queue.add(new Job(3, "low"));
        queue.add(new Job(1, "urgent"));
        queue.add(new Job(2, "medium"));
        queue.add(new Job(1, "urgent-2"));

        System.out.println("iteration (NOT sorted): " + queue);
        System.out.println("peek()  (head)         : " + queue.peek());

        while (!queue.isEmpty()) {
            // take() always returns the current highest-priority element
            System.out.println("take -> " + queue.take());
        }
        // Order: priority 1, 1, 2, 3  (order between the two 1s is undefined)

        // You can also build one with an explicit comparator:
        PriorityBlockingQueue<String> byLength =
                new PriorityBlockingQueue<>(11, java.util.Comparator.comparingInt(String::length));
        byLength.addAll(java.util.List.of("aaaa", "b", "ccc"));
        System.out.println("by length: " + byLength.take() + ", " + byLength.take() + ", " + byLength.take());
    }
}
