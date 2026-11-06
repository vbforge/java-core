package com.vbforge.practice.practice_12_builtin_collector_composition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** toMap throws on duplicate keys unless a merge function is given; the fourth argument picks the Map type. */
public class ToMapMergeAndFactoryDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("stream", "api", "map", "java", "lambda");

        try {
            words.stream().collect(Collectors.toMap(String::length, w -> w));
        } catch (IllegalStateException e) {
            System.out.println("no merge function -> IllegalStateException: " + e.getMessage());
        }

        Map<Integer, String> byLength = words.stream()
                .collect(Collectors.toMap(String::length, w -> w, (a, b) -> a + "|" + b, TreeMap::new));
        System.out.println("merged: " + byLength);
        // merged: {3=api|map, 4=java, 6=stream|lambda}
    }
}
