package com.vbforge.practice.practice_3_array_utilities;

import java.util.Arrays;

/**
 * {@code Arrays.sort} and {@code Arrays.binarySearch}.
 *
 * <ul>
 *   <li>sort on primitives uses dual-pivot Quicksort; on objects it uses TimSort (stable)</li>
 *   <li>binarySearch only works on an already sorted array</li>
 * </ul>
 */
public class ArraysSortAndBinarySearchDemo {

    public static void main(String[] args) {
        int[] a = {5, 2, 8, 3, 1};
        Arrays.sort(a);
        System.out.println("sorted                = " + Arrays.toString(a));          // [1, 2, 3, 5, 8]
        System.out.println("binarySearch(8) index = " + Arrays.binarySearch(a, 8));  // 4
    }
}
