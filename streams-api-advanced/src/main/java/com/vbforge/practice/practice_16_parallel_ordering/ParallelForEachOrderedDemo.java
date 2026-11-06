package com.vbforge.practice.practice_16_parallel_ordering;

import java.util.Arrays;
import java.util.List;

/** forEachOrdered keeps encounter order on a parallel stream (one element at a time); plain forEach does not. */
public class ParallelForEachOrderedDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        StringBuilder out = new StringBuilder();
        numbers.parallelStream().forEachOrdered(n -> out.append(n).append(' '));
        System.out.println("forEachOrdered: " + out.toString().trim());
        // forEachOrdered: 1 2 3 4 5
    }
}
