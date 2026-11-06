package com.vbforge.practice.practice_6_sorted;

import java.util.Arrays;
import java.util.List;

/** sorted() uses the natural order; distinct() afterwards removes the duplicate. */
public class SortedNaturalOrderDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("stream", "api", "lambda", "java", "filter", "map", "api");

        System.out.print("sorted: ");
        words.stream()
                .sorted()
                .distinct()
                .forEach(w -> System.out.print(w + " "));
        System.out.println();
        // api filter java lambda map stream
    }
}
