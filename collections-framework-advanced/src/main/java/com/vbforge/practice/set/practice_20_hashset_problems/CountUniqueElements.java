package com.vbforge.practice.set.practice_20_hashset_problems;

import java.util.HashSet;
import java.util.Set;

public class CountUniqueElements {
    public static void main(String[] args) {

        int [] arr = {1,2,3,4,4,5,5,7,7};

        countUnique(arr);

    }

    private static void countUnique(int[] arr) {
        Set<Integer> set = new HashSet<>();
        for (int i : arr) {
            set.add(i);
        }
        System.out.println("Unique elements: " + set);
        System.out.println("Size of set: " + set.size());
    }
}
