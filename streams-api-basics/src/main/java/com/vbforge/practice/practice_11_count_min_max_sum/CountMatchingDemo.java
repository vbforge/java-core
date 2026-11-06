package com.vbforge.practice.practice_11_count_min_max_sum;

import java.util.Arrays;
import java.util.List;

/** count() returns a long. */
public class CountMatchingDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);

        long count = numbers.stream().filter(n -> n > 4).count();
        System.out.println("count > 4: " + count);
        // count > 4: 4
    }
}
