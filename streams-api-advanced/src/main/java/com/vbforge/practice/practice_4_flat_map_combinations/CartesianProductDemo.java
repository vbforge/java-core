package com.vbforge.practice.practice_4_flat_map_combinations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Outer flatMap + inner map = every combination (cartesian product) of two lists. */
public class CartesianProductDemo {

    public static void main(String[] args) {
        List<String> colors = Arrays.asList("red", "blue");
        List<String> sizes = Arrays.asList("S", "M", "L");

        List<String> combinations = colors.stream()
                .flatMap(color -> sizes.stream().map(size -> color + "-" + size))
                .collect(Collectors.toList());

        System.out.println(combinations);
        // [red-S, red-M, red-L, blue-S, blue-M, blue-L]
    }
}
