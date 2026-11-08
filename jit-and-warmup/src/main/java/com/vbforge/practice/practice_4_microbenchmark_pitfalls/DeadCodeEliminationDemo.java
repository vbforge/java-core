package com.vbforge.practice.practice_4_microbenchmark_pitfalls;

/**
 * Dead-code elimination: if nobody uses the result, the JIT may remove the computation.
 * Three versions of the same loop: result discarded, result written to a volatile sink,
 * result returned and used. The "discarded" time can collapse towards zero after warm-up.
 * Timings vary; the consumed checksum is deterministic.
 */
public class DeadCodeEliminationDemo {

    static volatile long sink;

    static long squareSum(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            s += (long) i * i;
        }
        return s;
    }

    static void discarded(int n) {
        squareSum(n);
    }

    static void consumed(int n) {
        sink = squareSum(n);
    }

    static long timeNanos(Runnable r, int repeats) {
        long start = System.nanoTime();
        for (int i = 0; i < repeats; i++) {
            r.run();
        }
        return System.nanoTime() - start;
    }

    public static void main(String[] args) {
        int n = 100_000;
        int repeats = 2_000;

        // warm both paths so the measured round is steady-state
        for (int w = 0; w < 5; w++) {
            timeNanos(() -> discarded(n), repeats);
            timeNanos(() -> consumed(n), repeats);
        }

        long discardedNs = timeNanos(() -> discarded(n), repeats);
        long consumedNs = timeNanos(() -> consumed(n), repeats);

        System.out.println("result discarded: " + discardedNs / repeats + " ns per call (may be ~0: code removed)");
        System.out.println("result consumed:  " + consumedNs / repeats + " ns per call");
        System.out.println("checksum of one real computation = " + squareSum(n));
    }
}
