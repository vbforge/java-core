package com.vbforge.practice.practice_6_mini_harness;

import com.vbforge.practice.practice_5_benchmark_statistics.BenchmarkStatistics;

/**
 * Compares two ways to build a string with the mini harness: String += vs StringBuilder.
 * The ranking is stable, the absolute numbers are not (they change between machines and runs).
 */
public class MiniHarnessDemo {

    static long concatWithPlus() {
        String s = "";
        for (int i = 0; i < 50; i++) {
            s += i;
        }
        return s.length();
    }

    static long concatWithBuilder() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            sb.append(i);
        }
        return sb.length();
    }

    public static void main(String[] args) {
        long[] plus = MiniHarness.run(MiniHarnessDemo::concatWithPlus, 30, 15, 2_000);
        long[] builder = MiniHarness.run(MiniHarnessDemo::concatWithBuilder, 30, 15, 2_000);

        report("String +=      ", plus);
        report("StringBuilder  ", builder);
        System.out.println("both build the same length: " + (concatWithPlus() == concatWithBuilder()));
    }

    static void report(String name, long[] samples) {
        double perOp = BenchmarkStatistics.median(samples) / 2_000;
        System.out.printf("%s median %.0f ns/op, CV %.2f, ~%d ops/s%n", name, perOp,
                BenchmarkStatistics.coefficientOfVariation(samples), (long) BenchmarkStatistics.opsPerSecond(perOp));
    }
}
