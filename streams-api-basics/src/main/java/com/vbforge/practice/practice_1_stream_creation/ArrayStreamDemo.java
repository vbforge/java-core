package com.vbforge.practice.practice_1_stream_creation;

import java.util.Arrays;

/** Arrays.stream(int[]) gives an IntStream over the array elements. */
public class ArrayStreamDemo {

    public static void main(String[] args) {
        int[] arr = {10, 20, 30};
        System.out.print("from array: ");
        Arrays.stream(arr).forEach(n -> System.out.print(n + " "));
        System.out.println();
    }
}
