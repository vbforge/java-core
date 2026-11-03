package com.vbforge.practice.practice_5_array_algorithms;

import java.util.Arrays;

/**
 * Reversing an array two ways:
 * <ul>
 *   <li>{@code reversed} - returns a NEW array, the input is untouched</li>
 *   <li>{@code reverseInPlace} - swaps elements from both ends towards the middle</li>
 * </ul>
 */
public class ArrayReverseDemo {

    public static void main(String[] args) {
        int[] data = {1, 2, 3, 4, 5};
        System.out.println("reversed      = " + Arrays.toString(reversed(data)));  // [5, 4, 3, 2, 1]
        System.out.println("original      = " + Arrays.toString(data));            // [1, 2, 3, 4, 5]

        int[] copy = Arrays.copyOf(data, data.length);
        reverseInPlace(copy);
        System.out.println("in-place rev  = " + Arrays.toString(copy));            // [5, 4, 3, 2, 1]
    }

    static int[] reversed(int[] a) {
        int[] r = new int[a.length];
        for (int i = 0, j = a.length - 1; i < a.length; i++, j--) r[j] = a[i];
        return r;
    }

    static void reverseInPlace(int[] a) {
        for (int i = 0, j = a.length - 1; i < j; i++, j--) {
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
    }
}
