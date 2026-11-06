package com.vbforge.practice.practice_2_primitive_streams;

import java.util.stream.IntStream;

/** IntStream.range is end-exclusive, rangeClosed is end-inclusive. */
public class IntStreamRangeDemo {

    public static void main(String[] args) {
        System.out.print("range(1,6):       ");
        IntStream.range(1, 6).forEach(n -> System.out.print(n + " "));
        System.out.println();

        System.out.print("rangeClosed(1,5): ");
        IntStream.rangeClosed(1, 5).forEach(n -> System.out.print(n + " "));
        System.out.println();
    }
}
