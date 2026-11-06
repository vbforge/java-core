package com.vbforge.practice.practice_10_collect_and_to_array;

import java.util.Arrays;
import java.util.List;

/** toArray() as a terminal operation; the no-argument form returns Object[]. */
public class ToArrayDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);

        Object[] arr = numbers.stream().distinct().toArray();
        System.out.println("toArray length: " + arr.length);
        // toArray length: 7
    }
}
