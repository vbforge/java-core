package com.vbforge.practice.practice_5_array_algorithms;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Filtering an array into a new array: by element value (even / odd)
 * and by element position (even / odd index).
 */
public class ArrayFilteringDemo {

    public static void main(String[] args) {
        int[] data = {1, 2, 3, 4, 5};
        System.out.println("evens      = " + Arrays.toString(filterByParity(data, true)));   // [2, 4]
        System.out.println("odds       = " + Arrays.toString(filterByParity(data, false)));  // [1, 3, 5]

        int[] tens = {10, 20, 30, 40, 50, 60, 70};
        System.out.println("even index = " + Arrays.toString(evenIndexElements(tens)));      // [10, 30, 50, 70]
        System.out.println("odd index  = " + Arrays.toString(oddIndexElements(tens)));       // [20, 40, 60]
    }

    static int[] filterByParity(int[] a, boolean even) {
        return Arrays.stream(a).filter(v -> (v % 2 == 0) == even).toArray();
    }

    static int[] evenIndexElements(int[] a) {
        return IntStream.range(0, a.length).filter(i -> i % 2 == 0).map(i -> a[i]).toArray();
    }

    static int[] oddIndexElements(int[] a) {
        return IntStream.range(0, a.length).filter(i -> i % 2 != 0).map(i -> a[i]).toArray();
    }
}
