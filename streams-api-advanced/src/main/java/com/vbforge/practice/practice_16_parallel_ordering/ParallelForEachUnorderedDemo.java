package com.vbforge.practice.practice_16_parallel_ordering;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Parallel forEach visits every element but in arbitrary order, so only the SET of elements can be relied on. */
public class ParallelForEachUnorderedDemo {

    public static void main(String[] args) {
        Queue<Integer> seen = new ConcurrentLinkedQueue<>(); // thread-safe target
        IntStream.rangeClosed(1, 1000).boxed().parallel().forEach(seen::add);

        List<Integer> sorted = seen.stream().sorted().collect(Collectors.toList());
        List<Integer> expected = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
        System.out.println("all elements visited exactly once: " + sorted.equals(expected));
        // true (the visiting ORDER is not guaranteed and is not printed)
    }
}
