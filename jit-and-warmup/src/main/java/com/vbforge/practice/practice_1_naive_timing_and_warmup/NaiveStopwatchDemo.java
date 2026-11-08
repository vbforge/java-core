package com.vbforge.practice.practice_1_naive_timing_and_warmup;

/**
 * One stopwatch reading of Java code means little: the first call runs in the interpreter,
 * later calls get faster as the JIT compiles the method.
 * The checksum is deterministic, the nanosecond values are not.
 */
public class NaiveStopwatchDemo {

    static volatile long sink;

    static long work(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            s += (long) i * i % 7;
        }
        return s;
    }

    public static void main(String[] args) {
        long checksum = 0;
        for (int run = 1; run <= 6; run++) {
            long start = System.nanoTime();
            long result = work(200_000);
            long elapsed = System.nanoTime() - start;
            sink = result;
            checksum += result;
            System.out.println("run " + run + ": " + elapsed + " ns (varies between JVM runs)");
        }
        System.out.println("checksum = " + checksum);
        System.out.println("Same code, same input, different timings: a single reading is not a measurement.");
    }
}
