package com.vbforge.practice.practice_18_concurrent_collectors;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** groupingByConcurrent builds one shared ConcurrentMap (no merging of maps); the result is a ConcurrentMap. */
public class GroupingByConcurrentDemo {

    public static void main(String[] args) {
        ConcurrentMap<Integer, Long> countByRemainder = IntStream.rangeClosed(1, 100).boxed().parallel()
                .collect(Collectors.groupingByConcurrent(n -> n % 3, Collectors.counting()));

        Map<Integer, Long> sorted = new TreeMap<>(countByRemainder); // sorted for a stable print
        System.out.println(sorted);
        // {0=33, 1=34, 2=33}
    }
}
