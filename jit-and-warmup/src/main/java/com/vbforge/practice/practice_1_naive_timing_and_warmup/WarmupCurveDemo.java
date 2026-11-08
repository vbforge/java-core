package com.vbforge.practice.practice_1_naive_timing_and_warmup;

/**
 * Warm-up curve: time many equal batches and print the series.
 * Early batches are slow (interpreter, C1), later ones settle (C2 steady state).
 * Run with -XX:+PrintCompilation to see the compiles appear between the first batches.
 */
public class WarmupCurveDemo {

    static volatile long sink;

    static long work(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            s += (i ^ (i >>> 3)) & 15;
        }
        return s;
    }

    public static void main(String[] args) {
        int batches = 25;
        int callsPerBatch = 3_000;
        long[] micros = new long[batches];
        long checksum = 0;

        for (int b = 0; b < batches; b++) {
            long start = System.nanoTime();
            long acc = 0;
            for (int c = 0; c < callsPerBatch; c++) {
                acc += work(500);
            }
            micros[b] = (System.nanoTime() - start) / 1_000;
            sink = acc;
            checksum += acc;
        }

        for (int b = 0; b < batches; b++) {
            System.out.println("batch " + String.format("%2d", b + 1) + ": " + micros[b] + " us");
        }
        System.out.println("checksum = " + checksum);
        System.out.println("First batch vs last batch: " + micros[0] + " us -> " + micros[batches - 1] + " us (timings vary)");
    }
}
