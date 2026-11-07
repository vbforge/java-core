package com.vbforge.practice.set.practice_20_hashset_problems;

import java.util.HashSet;
import java.util.Set;

public class CommonElementsInTwoArrays {
    public static void main(String[] args) {

        int [] arr1 = {1,2,3,4,5,6,7,8,9};
        int [] arr2 = {10,20,30,4,50,6,70,80,90};

        haveCommonElements(arr1, arr2);

    }

    private static void haveCommonElements(int[] arr1, int[] arr2) {

        Set<Integer> set = new HashSet<>();

        for (int i : arr1) {
            set.add(i);
        }

        for (int i : arr2) {
            if (set.contains(i)) {
                System.out.println("Common element: " + i);
            } else {
                set.add(i);
                System.out.println("No common element: " + i);
            }
        }

        System.out.println("Merged two arrays without duplicates: " + set);



    }
}
