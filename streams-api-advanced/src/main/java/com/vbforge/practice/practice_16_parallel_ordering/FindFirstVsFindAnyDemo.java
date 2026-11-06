package com.vbforge.practice.practice_16_parallel_ordering;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** findFirst is deterministic on a parallel stream; findAny may return any match, so never assert a specific one. */
public class FindFirstVsFindAnyDemo {

    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());

        System.out.println("findFirst even: " + numbers.parallelStream().filter(n -> n % 2 == 0).findFirst().get());
        // findFirst even: 2

        int any = numbers.parallelStream().filter(n -> n % 2 == 0).findAny().get();
        System.out.println("findAny is even: " + (any % 2 == 0)); // true - but which even number is not defined
    }
}
