package com.vbforge.practice.practice_3_array_utilities;

import java.util.Arrays;

/**
 * {@code Arrays.fill} - set every slot to the same value.
 */
public class ArraysFillDemo {

    public static void main(String[] args) {
        int[] filled = new int[5];
        Arrays.fill(filled, 7);
        System.out.println("filled = " + Arrays.toString(filled));   // [7, 7, 7, 7, 7]
    }
}
