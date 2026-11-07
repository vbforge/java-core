package com.vbforge.practice.map.practice_29_hashmap_classic_problems;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Problem:
 * Given an array of integers and a target value, return the indices of the two numbers that add up to the target.
 * Use of HashMap:
 * Store each number as a key, and its index as the value.
 * On each iteration, check if target - current number exists in the map.
 * */
public class TwoSum {
    public static void main(String[] args) {

        int[] nums = {2, 11, 15, 7, 4, 5};
        int target = 9;

        int[] result = twoSum(nums, target);
        System.out.println(Arrays.toString(result));

    }

    private static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        throw new IllegalArgumentException("No two sum solution");
    }

}
