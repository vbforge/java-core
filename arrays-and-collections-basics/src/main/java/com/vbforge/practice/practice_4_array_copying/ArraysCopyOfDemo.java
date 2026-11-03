package com.vbforge.practice.practice_4_array_copying;

import java.util.Arrays;

/**
 * {@code Arrays.copyOf} and {@code Arrays.copyOfRange} always allocate a NEW array.
 * Because the length is a parameter, {@code copyOf} is also the way to "resize" an
 * array: a longer copy is padded with default values, a shorter one is truncated.
 */
public class ArraysCopyOfDemo {

    public static void main(String[] args) {
        int[] a = {5, 2, 8, 3, 1};

        System.out.println("copyOf (same length) : " + Arrays.toString(Arrays.copyOf(a, a.length)));  // [5, 2, 8, 3, 1]
        System.out.println("copyOfRange(1, 4)    : " + Arrays.toString(Arrays.copyOfRange(a, 1, 4))); // [2, 8, 3]

        System.out.println("\n--- resizing ---");
        System.out.println("grow   to 8          : " + Arrays.toString(Arrays.copyOf(a, 8)));         // [5, 2, 8, 3, 1, 0, 0, 0]
        System.out.println("shrink to 3          : " + Arrays.toString(Arrays.copyOf(a, 3)));         // [5, 2, 8]
    }
}
