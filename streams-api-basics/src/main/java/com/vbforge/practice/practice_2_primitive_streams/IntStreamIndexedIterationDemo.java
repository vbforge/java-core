package com.vbforge.practice.practice_2_primitive_streams;

import java.util.Arrays;
import java.util.stream.IntStream;

/** IntStream.range over the indexes replaces an index-based for loop. */
public class IntStreamIndexedIterationDemo {

    public static void main(String[] args) {
        int[] values = {5, 3, 8, 1, 7};

        int[] squared = IntStream.range(0, values.length)
                .map(i -> values[i] * values[i])
                .toArray();
        System.out.println("squared: " + Arrays.toString(squared));
    }
}
