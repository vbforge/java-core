package com.vbforge.practice.practice_14_broken_combiner;

import java.util.stream.Collector;
import java.util.stream.IntStream;

/**
 * A wrong combiner is invisible on a sequential stream (it is never called) and silently loses data
 * on a parallel one. Only booleans are printed because the wrong parallel total is not reproducible.
 */
public class BrokenCombinerDemo {

    static Collector<Integer, long[], Long> sum(boolean correctCombiner) {
        return Collector.of(
                () -> new long[1],
                (acc, n) -> acc[0] += n,
                correctCombiner ? (a, b) -> { a[0] += b[0]; return a; } : (a, b) -> a, // broken: forgets b
                acc -> acc[0]);
    }

    public static void main(String[] args) {
        long expected = 100_000L * 100_001L / 2;

        boolean brokenSequential = IntStream.rangeClosed(1, 100_000).boxed().collect(sum(false)) == expected;
        boolean brokenParallel = IntStream.rangeClosed(1, 100_000).boxed().parallel().collect(sum(false)) == expected;
        boolean correctParallel = IntStream.rangeClosed(1, 100_000).boxed().parallel().collect(sum(true)) == expected;

        System.out.println("broken combiner, sequential correct: " + brokenSequential);
        System.out.println("broken combiner, parallel correct:   " + brokenParallel);
        System.out.println("correct combiner, parallel correct:  " + correctParallel);
        // true / false / true
    }
}
