package com.vbforge.practice.set.practice_20_hashset_problems;

import java.util.HashSet;
import java.util.Set;

public class FindDuplicatesInArray {
    public static void main(String[] args) {

        int [] arr = {1,2,3,4,4,5,5,7,7};

        findDuplicatesInArray(arr);

    }

    private static void findDuplicatesInArray(int[] arr) {
        Set<Integer> seen = new HashSet<>();
        for (int i : arr) {
            if (seen.contains(i)) {
                System.out.println("Duplicate: " + i);
            } else {
                seen.add(i);
            }
        }
        System.out.println(seen);
    }
}
