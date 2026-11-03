package com.vbforge.practice.practice_3_array_utilities;

import java.util.Arrays;

/**
 * Comparing array contents: {@code Arrays.equals} for one dimension,
 * {@code Arrays.deepEquals} for nested arrays.
 */
public class ArraysEqualsDemo {

    public static void main(String[] args) {
        int[] a = {5, 2, 8, 3, 1};
        int[] copy = Arrays.copyOf(a, a.length);
        System.out.println("equals     : " + Arrays.equals(a, copy));                                  // true

        int[][] m = {{1, 2}, {3, 4}};
        System.out.println("deepEquals : " + Arrays.deepEquals(m, new int[][]{{1, 2}, {3, 4}}));        // true
    }
}
