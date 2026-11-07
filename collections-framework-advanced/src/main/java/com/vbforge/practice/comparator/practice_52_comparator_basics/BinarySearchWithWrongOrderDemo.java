package com.vbforge.practice.comparator.practice_52_comparator_basics;

import java.util.Arrays;
import java.util.Comparator;

public class BinarySearchWithWrongOrderDemo {
    public static void main(String[] args) {

        String[] s = {"map", "pen", "marble", "key"};
        DescendingStringComparator o = new DescendingStringComparator();
        Arrays.sort(s, o);

        for (String s2 : s) System.out.print(s2 + " ");
        System.out.println(Arrays.binarySearch(s, "map"));

//        output: pen marble map key -1  (binarySearch assumes ASCENDING order, so "map" is not found)
    }

    static class DescendingStringComparator implements Comparator<String> {
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
    }
}

/**
 * Arrays.binarySearch(s, "map") assumes the array is sorted by NATURAL (ascending) order, but s was sorted with a
 * descending comparator. The search probes the middle, sees the wrong order and ends up at "insertion point 0",
 * which is reported as -(0) - 1 = -1 (not found), although "map" is in the array.
 * Fix: pass the same comparator, Arrays.binarySearch(s, "map", comparator).
 */
