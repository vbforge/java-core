package com.vbforge.practice.map.practice_29_hashmap_classic_problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Problem:
 * Find all elements that appear more than once in an array.
 * Use of HashMap:
 * Use a HashMap<Integer, Integer> to count occurrences.
 * */
public class FindAllDuplicatesInArray {
    public static void main(String[] args) {

        int[] nums = {4, 3, 2, 7, 8, 2, 3, 1};
        List<Integer> result = findDuplicates(nums);
        System.out.println(result);

    }

    private static List<Integer> findDuplicates(int[] nums) {
        Map<Integer, Integer> countMap = new HashMap<>();
        List<Integer> result = new ArrayList<>();

        for (int num : nums) {
            countMap.put(num, countMap.getOrDefault(num, 0) + 1);
            if (countMap.get(num) == 2) {
                result.add(num);
            }
        }
        return result;
    }
}
