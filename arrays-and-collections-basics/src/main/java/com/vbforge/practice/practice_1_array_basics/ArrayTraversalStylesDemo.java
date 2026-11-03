package com.vbforge.practice.practice_1_array_basics;

import java.util.Arrays;

/**
 * Four ways to walk through an array: classic index loop, for-each,
 * reverse index loop and a stream.
 *
 * <p>For-each cannot modify the array slots - see
 * {@code practice_2_array_value_vs_reference}.
 */
public class ArrayTraversalStylesDemo {

    public static void main(String[] args) {
        int[] nums = {10, 20, 30, 40, 50};

        System.out.print("classic : ");
        for (int i = 0; i < nums.length; i++) System.out.print(nums[i] + " ");

        System.out.print("\nfor-each: ");
        for (int n : nums) System.out.print(n + " ");

        System.out.print("\nreverse : ");
        for (int i = nums.length - 1; i >= 0; i--) System.out.print(nums[i] + " ");

        System.out.print("\nstream  : ");
        Arrays.stream(nums).forEach(n -> System.out.print(n + " "));
        System.out.println();
    }
}
