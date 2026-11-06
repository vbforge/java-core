package com.vbforge.practice.practice_6_sorted;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** sorted(Comparator): by length, descending. The sort is stable, so equal lengths keep their encounter order. */
public class SortedByLengthDescendingDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("stream", "api", "lambda", "java", "filter", "map", "api");

        System.out.print("sorted by len: ");
        words.stream()
                .distinct()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .forEach(w -> System.out.print(w + " "));
        System.out.println();
        // stream lambda filter java api map
    }
}
