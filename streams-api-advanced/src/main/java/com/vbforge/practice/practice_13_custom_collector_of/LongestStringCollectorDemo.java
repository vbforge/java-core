package com.vbforge.practice.practice_13_custom_collector_of;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Stream;

/** A collector whose mutable container is a one-element String[] (a String cannot be mutated); the combiner is parallel-safe. */
public class LongestStringCollectorDemo {

    static Collector<String, String[], String> longest() {
        return Collector.of(
                () -> new String[1],
                (acc, s) -> { if (acc[0] == null || s.length() > acc[0].length()) acc[0] = s; },
                (a, b) -> {
                    if (b[0] != null && (a[0] == null || b[0].length() > a[0].length())) a[0] = b[0];
                    return a;
                },
                acc -> acc[0]);
    }

    public static void main(String[] args) {
        System.out.println("longest: " + Arrays.asList("stream", "api", "collector", "java").stream().collect(longest()));
        System.out.println("empty:   " + Stream.<String>empty().collect(longest()));
        // longest: collector
        // empty:   null
    }
}
