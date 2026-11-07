package com.vbforge.practice.queue.practice_41_priority_queue_basics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Min-heap vs max-heap (Comparator.reverseOrder()) and why only poll() gives sorted order.
 * Moved here from the priorityQueue() scene of the old QueueDemo deep dive.
 */
public class PriorityQueueMaxHeapDemo {
    public static void main(String[] args) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.addAll(Arrays.asList(5, 1, 4, 2, 8, 3));
        System.out.println("peek (min): " + minHeap.peek());
        System.out.print("Poll order (sorted):     ");
        while (!minHeap.isEmpty()) System.out.print(minHeap.poll() + " ");
        System.out.println();

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.addAll(Arrays.asList(5, 1, 4, 2, 8, 3));
        System.out.println("peek (max): " + maxHeap.peek());
        System.out.print("Max-heap poll order:     ");
        while (!maxHeap.isEmpty()) System.out.print(maxHeap.poll() + " ");
        System.out.println();
    }
}
