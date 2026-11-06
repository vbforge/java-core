package com.vbforge.practice.practice_10_partitioning_by;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** partitioningBy(predicate) returns Map&lt;Boolean, List&lt;T&gt;&gt; with keys false and true. */
public class PartitionEvenOddDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

        Map<Boolean, List<Integer>> parts = numbers.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));

        System.out.println(parts);
        // {false=[1, 3, 5, 7], true=[2, 4, 6]}
    }
}
