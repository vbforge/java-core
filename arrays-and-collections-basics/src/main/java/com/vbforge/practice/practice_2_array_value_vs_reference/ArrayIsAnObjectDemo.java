package com.vbforge.practice.practice_2_array_value_vs_reference;

/**
 * Arrays are objects: a reference to any array can be assigned to {@code Object}
 * and cast back. Each row of a 2D array is itself a reference to a 1D array.
 */
public class ArrayIsAnObjectDemo {

    public static void main(String[] args) {
        int[] arr = {2, 10};
        Object ref = arr;                    // int[] IS-A Object
        int x = ((int[]) ref)[0];            // cast back to use it
        System.out.println("x = " + x);      // 2

        System.out.println("\n--- 2D array: outer and per-row references ---");
        int[][] multi = {{10, 20}, {1, 2, 3}};
        Object outer = multi;                // the whole 2D array
        Object firstRow = multi[0];          // just the first row

        System.out.println("outer instanceof int[][]  : " + (outer instanceof int[][]));
        System.out.println("firstRow instanceof int[] : " + (firstRow instanceof int[]));
        System.out.println("row lengths               : " + multi[0].length + ", " + multi[1].length);

        System.out.println("\n--- no covariance between primitive and wrapper arrays ---");
        try {
            Integer[] wrong = (Integer[]) ref;   // compiles through Object, fails at runtime
            System.out.println(wrong.length);
        } catch (ClassCastException e) {
            System.out.println("int[] cannot be cast to Integer[] -> ClassCastException");
        }
    }
}
