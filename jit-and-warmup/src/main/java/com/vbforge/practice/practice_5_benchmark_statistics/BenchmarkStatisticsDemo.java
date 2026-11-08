package com.vbforge.practice.practice_5_benchmark_statistics;

/**
 * Why median and trimmed mean: one GC pause or one cold sample ruins the plain mean.
 * Fixed samples, so the output is deterministic.
 */
public class BenchmarkStatisticsDemo {

    public static void main(String[] args) {
        long[] steady = {100, 102, 98, 101, 99, 100, 103, 97, 100, 101};
        long[] withOutlier = {100, 102, 98, 101, 99, 100, 103, 97, 100, 5_000};

        print("steady", steady);
        print("with one outlier", withOutlier);

        System.out.println("input not modified by median(): " + (withOutlier[9] == 5_000));
        System.out.println("100 ns per op = " + (long) BenchmarkStatistics.opsPerSecond(100) + " ops/s");
    }

    static void print(String label, long[] samples) {
        System.out.println("--- " + label);
        System.out.printf("mean            = %.1f%n", BenchmarkStatistics.mean(samples));
        System.out.printf("median          = %.1f%n", BenchmarkStatistics.median(samples));
        System.out.printf("trimmed mean 10%% = %.1f%n", BenchmarkStatistics.trimmedMean(samples, 0.1));
        System.out.printf("CV              = %.3f%n", BenchmarkStatistics.coefficientOfVariation(samples));
    }
}
