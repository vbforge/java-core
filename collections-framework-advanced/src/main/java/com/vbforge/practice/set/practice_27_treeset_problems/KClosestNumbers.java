package com.vbforge.practice.set.practice_27_treeset_problems;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Problem 1: Find K Closest Numbers to a Target
 * Use a TreeSet to quickly find nearest neighbours using lower() and ceiling() (it is a TreeSet, not a TreeMap).
 * */
public class KClosestNumbers {
    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5, 6, 7};
        int target = 5;
        int k = 3;
        System.out.println(findKClosest(nums, target, k)); // Expected: [5, 4, 6]
    }

    private static List<Integer> findKClosest(int[] nums, int target, int k) {
        TreeSet<Integer> treeSet = new TreeSet<>();
        for (int num : nums) {
            treeSet.add(num);
        }

        List<Integer> result = new ArrayList<>();

        // Find closest element >= target
        Integer ceiling = treeSet.ceiling(target);
        // Find closest element < target
        Integer floor = treeSet.lower(target);

        // Use two pointers to pick closest numbers
        while (result.size() < k) {
            if (floor == null) {
                result.add(ceiling);
                ceiling = treeSet.higher(ceiling);
            } else if (ceiling == null) {
                result.add(floor);
                floor = treeSet.lower(floor);
            } else {
                if (Math.abs(floor - target) <= Math.abs(ceiling - target)) {
                    result.add(floor);
                    floor = treeSet.lower(floor);
                } else {
                    result.add(ceiling);
                    ceiling = treeSet.higher(ceiling);
                }
            }
        }
        // Sort the result if needed; in some cases, might return in order of closeness
        // Here, as the closest elements are added in order, you can leave it unsorted.

        return result;
    }

}
