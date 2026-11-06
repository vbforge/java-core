package com.vbforge.practice.practice_5_filter_and_map;

import java.util.Arrays;
import java.util.List;

/** filter keeps the elements that match a predicate. */
public class FilterByLengthDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("stream", "api", "lambda", "java", "filter", "map", "api");

        System.out.print("filter len>4: ");
        words.stream()
                .filter(w -> w.length() > 4)
                .forEach(w -> System.out.print(w + " "));
        System.out.println();
        // stream lambda filter
    }
}
