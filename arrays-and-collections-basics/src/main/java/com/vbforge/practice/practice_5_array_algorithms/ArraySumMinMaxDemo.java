package com.vbforge.practice.practice_5_array_algorithms;

/**
 * Aggregations written by hand: sum, max, min.
 * {@code max} and {@code min} require a non-empty array.
 */
public class ArraySumMinMaxDemo {

    public static void main(String[] args) {
        int[] data = {1, 2, 3, 4, 5};
        System.out.println("sum = " + sum(data));   // 15
        System.out.println("max = " + max(data));   // 5
        System.out.println("min = " + min(data));   // 1
    }

    static int sum(int[] a) {
        int s = 0;
        for (int v : a) s += v;
        return s;
    }

    static int max(int[] a) {
        int m = a[0];
        for (int i = 1; i < a.length; i++) if (a[i] > m) m = a[i];
        return m;
    }

    static int min(int[] a) {
        int m = a[0];
        for (int i = 1; i < a.length; i++) if (a[i] < m) m = a[i];
        return m;
    }
}
