package com.vbforge.practice.queue.practice_44_priority_queue_problems;

import java.util.PriorityQueue;

public class KthLargest {
    public static void main(String[] args) {

        int[] nums = {1, 2, 3, 4, 5, 6, 7};
        int k = 2;

        int kthLargest = findKthLargest(nums, k);
        System.out.println("Kth largest: " + kthLargest); //printing Kth biggest element

    }

    private static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        if (!minHeap.isEmpty()) return minHeap.peek();
        throw new IllegalArgumentException("k is larger than array size");
    }

}
