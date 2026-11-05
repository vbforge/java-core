package com.vbforge.practice.practice_8_autoboxing_unboxing;

/**
 * Primitive arrays and wrapper arrays are NOT interchangeable,
 * even though int <-> Integer autoboxing is allowed for single values.
 *
 * int[] cannot be assigned to Integer[] and vice versa - this is a compile-time error,
 * not a runtime one. See the commented lines below.
 */
public class PrimitiveVsWrapperArraysDemo {

    public static void main(String[] args) {
        int[] primitives = {1, 2, 3, 4, 5};
        Integer[] wrappers = {1, 2, 3, 4, 5};

        printPrimitiveSum(primitives);
        printWrapperSum(wrappers);

        // Compile-time errors (kept here as documentation):
        // printWrapperSum(primitives); // int[] cannot be converted to Integer[]
        // printPrimitiveSum(wrappers); // Integer[] cannot be converted to int[]
    }

    private static void printPrimitiveSum(int[] arr) {
        int sum = 0;
        for (int v : arr) sum += v;
        System.out.println("int[] sum = " + sum);
    }

    private static void printWrapperSum(Integer[] arr) {
        int sum = 0;
        for (Integer v : arr) sum += v; // unboxing
        System.out.println("Integer[] sum = " + sum);
    }
}
