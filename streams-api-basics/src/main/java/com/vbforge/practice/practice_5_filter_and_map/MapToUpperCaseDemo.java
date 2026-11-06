package com.vbforge.practice.practice_5_filter_and_map;

import java.util.Arrays;
import java.util.List;

/** map transforms each element (String to upper-case String); limit(4) only shortens the output. */
public class MapToUpperCaseDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("stream", "api", "lambda", "java", "filter", "map", "api");

        System.out.print("map toUpper: ");
        words.stream()
                .map(String::toUpperCase)
                .limit(4)
                .forEach(w -> System.out.print(w + " "));
        System.out.println();
        // STREAM API LAMBDA JAVA
    }
}
