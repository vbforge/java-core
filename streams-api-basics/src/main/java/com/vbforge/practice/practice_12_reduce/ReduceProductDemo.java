package com.vbforge.practice.practice_12_reduce;

import java.util.Arrays;
import java.util.List;

/** reduce with an identity: 1 is neutral for multiplication (an empty stream would give 1). */
public class ReduceProductDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);

        int product = numbers.stream()
                .distinct()
                .reduce(1, (a, b) -> a * b);
        System.out.println("product (distinct): " + product);
        // product (distinct): 6480
    }
}
