package com.vbforge.practice.practice_8_laziness;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Building the pipeline runs nothing. The terminal findFirst() starts the work and
 * short-circuits after the first match, so only elements 1 and 2 are processed.
 */
public class LazyPipelineFindFirstDemo {

    public static void main(String[] args) {
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        System.out.println("Building pipeline... (no output yet)");

        // Pipeline built but NOT executed
        Stream<String> pipeline = data.stream()
                .filter(n -> {
                    System.out.println("  filter: " + n);
                    return n % 2 == 0;
                })
                .map(n -> {
                    System.out.println("  map: " + n);
                    return "item-" + n;
                });

        System.out.println("findFirst() - only processes until the first match:");
        Optional<String> first = pipeline.findFirst();
        System.out.println("Result: " + first.orElse("none"));
        // filter: 1, filter: 2, map: 2, Result: item-2
    }
}
