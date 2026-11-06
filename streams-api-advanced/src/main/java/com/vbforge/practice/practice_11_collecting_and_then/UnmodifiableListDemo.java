package com.vbforge.practice.practice_11_collecting_and_then;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** collectingAndThen post-processes a collector's result: here a read-only list. */
public class UnmodifiableListDemo {

    public static void main(String[] args) {
        List<Integer> evens = Arrays.asList(1, 2, 3, 4, 5, 6).stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        System.out.println("evens: " + evens);
        try {
            evens.add(8);
        } catch (UnsupportedOperationException e) {
            System.out.println("add -> UnsupportedOperationException");
        }
    }
}
