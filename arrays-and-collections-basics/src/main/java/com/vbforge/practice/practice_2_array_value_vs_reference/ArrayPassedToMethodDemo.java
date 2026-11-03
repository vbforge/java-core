package com.vbforge.practice.practice_2_array_value_vs_reference;

import java.util.Arrays;

/**
 * Java passes the array REFERENCE by value.
 * A method can change the caller's array contents,
 * but re-pointing its own parameter does not affect the caller.
 */
public class ArrayPassedToMethodDemo {

    public static void main(String[] args) {
        int[] values = {10, 20, 30};

        addInPlace(values, 7);
        System.out.println("after addInPlace    : " + Arrays.toString(values)); // [17, 27, 37]

        replaceReference(values);
        System.out.println("after replaceRef    : " + Arrays.toString(values)); // still [17, 27, 37]
    }

    /** Mutates the caller's array through the shared reference. */
    static void addInPlace(int[] arr, int delta) {
        for (int i = 0; i < arr.length; i++) arr[i] += delta;
    }

    /** Only the local parameter is re-pointed; the caller's variable is untouched. */
    static void replaceReference(int[] arr) {
        arr = new int[]{0, 0, 0};
    }
}
