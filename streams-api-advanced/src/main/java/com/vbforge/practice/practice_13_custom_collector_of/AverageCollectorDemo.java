package com.vbforge.practice.practice_13_custom_collector_of;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Collector.of(supplier, accumulator, combiner, finisher): the accumulator type (long[] = sum, count)
 * differs from the result type (Double); the finisher converts one into the other.
 */
public class AverageCollectorDemo {

    static Collector<Integer, long[], Double> average() {
        return Collector.of(
                () -> new long[2],                                       // [sum, count]
                (acc, n) -> { acc[0] += n; acc[1]++; },                  // fold one element in
                (a, b) -> { a[0] += b[0]; a[1] += b[1]; return a; },     // merge two partial results
                acc -> acc[1] == 0 ? 0.0 : (double) acc[0] / acc[1]);    // finisher: long[] -> Double
    }

    public static void main(String[] args) {
        System.out.println("average of 1..4: " + Arrays.asList(1, 2, 3, 4).stream().collect(average()));
        System.out.println("average of empty: " + Stream.<Integer>empty().collect(average()));
        // average of 1..4: 2.5
        // average of empty: 0.0
    }
}
