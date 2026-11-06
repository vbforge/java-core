package com.vbforge.practice.practice_11_count_min_max_sum;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;

/** mapToInt switches to an IntStream, which has sum() and average() (an OptionalDouble). */
public class SumAndAverageDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);

        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        OptionalDouble avg = numbers.stream().mapToInt(Integer::intValue).average();
        System.out.println("sum: " + sum);
        System.out.printf(Locale.ROOT, "avg: %.2f%n", avg.orElse(0));
        // sum: 39
        // avg: 3.90
    }
}
