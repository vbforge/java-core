package com.vbforge.practice.practice_2_primitive_streams;

import java.util.Arrays;

/** Built-in sum / average / max on an IntStream (no boxing); average and max return optionals. */
public class IntStreamAggregatesDemo {

    public static void main(String[] args) {
        int[] values = {5, 3, 8, 1, 7};

        System.out.println("IntStream sum:     " + Arrays.stream(values).sum());
        System.out.println("IntStream average: " + Arrays.stream(values).average().orElse(0));
        System.out.println("IntStream max:     " + Arrays.stream(values).max().orElse(0));
    }
}
