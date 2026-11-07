package com.vbforge.practice.queue.practice_44_priority_queue_problems;

import java.util.*;

/**
 * Heap-based approach — the mirror of
 * {@code TopKFrequentElements} (map layer, treemap_classic_problems) which solves
 * the SAME problem with a TreeMap grouped by frequency. Both approaches kept
 * side-by-side: min-heap (this file) trades an extra sort for O(n log k);
 * TreeMap trades the sort for O(n log n) but groups ties naturally.
 */
public class TopKFrequent {
    public static void main(String[] args) {

        int[] nums = {1,1,1,2,2,3};
        int k = 2;
        System.out.println("Top K frequent elements: " + topKFrequent(nums, k)); // [1, 2]

    }

    private static List<Integer> topKFrequent(int[] nums, int k) {

        Map<Integer, Integer> freq = new HashMap<>();

        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!minHeap.isEmpty()) result.add(minHeap.poll().getKey());
        Collections.reverse(result); // optional: descending freq order
        return result;

    }


}
