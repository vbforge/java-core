package com.vbforge.practice.practice_17_parallel_safe_accumulation;

import java.util.stream.IntStream;

/** On a parallel stream the reduce identity is applied once per chunk, so it must be neutral (0 for sum). */
public class ParallelReduceNeutralIdentityDemo {

    public static void main(String[] args) {
        int expected = 500_500; // 1 + 2 + ... + 1000

        int neutral = IntStream.rangeClosed(1, 1000).boxed().parallel().reduce(0, Integer::sum);
        int notNeutral = IntStream.rangeClosed(1, 1000).boxed().parallel().reduce(10, Integer::sum);

        System.out.println("identity 0  correct: " + (neutral == expected));
        System.out.println("identity 10 correct: " + (notNeutral == expected)); // 10 is added once per chunk
        // true / false
    }
}
