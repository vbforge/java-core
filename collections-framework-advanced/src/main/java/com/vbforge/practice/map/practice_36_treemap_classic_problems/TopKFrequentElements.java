package com.vbforge.practice.map.practice_36_treemap_classic_problems;

import java.util.*;

/**
 * Problem 3: Top K Frequent Elements Using TreeMap
 * Goal: Return the K elements that appear most frequently in the array.
 *
 * Constraints: Using TreeMap to maintain sorted order of frequencies.
 *
 * Solution using TreeMap:
 * Count frequencies using a HashMap
 * Then use a TreeMap<Integer, List<Integer>> to group numbers by their frequencies
 * Traverse TreeMap in descending order
 * */
public class TopKFrequentElements {

    public static List<Integer> topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freqMap = new HashMap<>();
        TreeMap<Integer, List<Integer>> sortedMap = new TreeMap<>(Collections.reverseOrder());

        // Count frequency
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        // Group by frequency
        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            int num = entry.getKey();
            int freq = entry.getValue();
            sortedMap.computeIfAbsent(freq, x -> new ArrayList<>()).add(num);
        }

        // Collect top k frequent
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : sortedMap.entrySet()) {
            for (int num : entry.getValue()) {
                result.add(num);
                if (result.size() == k) {
                    return result;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1, 1, 1, 2, 2, 3};
        int k = 2;
        System.out.println(topKFrequent(nums, k)); // [1, 2]
    }

}
