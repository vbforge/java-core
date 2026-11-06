package com.vbforge.practice.practice_11_count_min_max_sum;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** max takes a Comparator and returns an Optional, because the stream may be empty. */
public class MaxWithOptionalDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);

        Optional<Integer> max = numbers.stream().max(Integer::compareTo);
        System.out.println("max: " + max.get()); // Java 8: get() (orElseThrow() without arguments is Java 10)
        // max: 9
    }
}
