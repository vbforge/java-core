package com.vbforge.practice.practice_3_array_utilities;

import java.util.Arrays;

/**
 * Printing arrays: {@code Arrays.toString} for one dimension,
 * {@code Arrays.deepToString} for nested arrays.
 * (Plain {@code System.out.println(array)} prints a type + hash, not the content.)
 */
public class ArraysToStringDemo {

    public static void main(String[] args) {
        int[] a = {5, 2, 8, 3, 1};
        System.out.println("toString     : " + Arrays.toString(a));        // [5, 2, 8, 3, 1]

        int[][] m = {{1, 2}, {3, 4}};
        System.out.println("deepToString : " + Arrays.deepToString(m));    // [[1, 2], [3, 4]]
    }
}
