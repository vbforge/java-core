package com.vbforge.practice.practice_5_benchmark_statistics;

import java.util.Arrays;

/** Small statistics helpers for benchmark samples (nanoseconds). */
public final class BenchmarkStatistics {

    private BenchmarkStatistics() {
    }

    /** Median of the samples; works on a copy, the input stays unsorted. */
    public static double median(long[] samples) {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return sorted.length % 2 == 1 ? sorted[mid] : (sorted[mid - 1] + sorted[mid]) / 2.0;
    }

    public static double mean(long[] samples) {
        double sum = 0;
        for (long s : samples) {
            sum += s;
        }
        return sum / samples.length;
    }

    /** Mean after dropping the lowest and highest {@code trimFraction} of the samples (0 <= f < 0.5). */
    public static double trimmedMean(long[] samples, double trimFraction) {
        long[] sorted = samples.clone();
        Arrays.sort(sorted);
        int cut = (int) (sorted.length * trimFraction);
        return mean(Arrays.copyOfRange(sorted, cut, sorted.length - cut));
    }

    /** Standard deviation divided by mean: below ~0.05 is a stable measurement, above ~0.2 is noisy. */
    public static double coefficientOfVariation(long[] samples) {
        double mean = mean(samples);
        double sq = 0;
        for (long s : samples) {
            sq += (s - mean) * (s - mean);
        }
        return Math.sqrt(sq / samples.length) / mean;
    }

    /** Operations per second for an average time per operation in nanoseconds. */
    public static double opsPerSecond(double nanosPerOp) {
        return 1_000_000_000.0 / nanosPerOp;
    }
}
