package com.vbforge.practice.practice_2_flat_map_pitfalls;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** A null returned from the flatMap function is treated as an empty stream (the element just disappears). */
public class NullResultFlatMapDemo {

    public static void main(String[] args) {
        List<String> result = Stream.of("a", "b", "c")
                .flatMap(s -> s.equals("b") ? null : Stream.of(s, s.toUpperCase()))
                .collect(Collectors.toList());

        System.out.println("result: " + result);
        // result: [a, A, c, C]
    }
}
