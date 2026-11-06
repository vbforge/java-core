package com.vbforge.practice.practice_16_parallel_ordering;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** collect on a parallel stream keeps encounter order: same list as the sequential pipeline. */
public class ParallelCollectKeepsOrderDemo {

    public static void main(String[] args) {
        List<Integer> sequential = IntStream.rangeClosed(1, 1000).boxed()
                .map(n -> n * n).collect(Collectors.toList());
        List<Integer> parallel = IntStream.rangeClosed(1, 1000).boxed().parallel()
                .map(n -> n * n).collect(Collectors.toList());

        System.out.println("same list, same order: " + sequential.equals(parallel));
        // true
    }
}
