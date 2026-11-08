package com.vbforge.practice.practice_4_microbenchmark_pitfalls;

/**
 * Constant folding: with compile-time constant inputs the JIT can compute the answer once.
 * A benchmark that feeds constants may measure nothing. Feeding a value the JIT cannot know
 * (a non-final field) keeps the work real. Timings vary; the results are deterministic.
 */
public class ConstantFoldingDemo {

    static volatile long sink;
    static int runtimeInput = 12;   // non-final: the JIT must treat it as unknown

    static final int CONSTANT_INPUT = 12;

    static long slowFib(int n) {
        return n < 2 ? n : slowFib(n - 1) + slowFib(n - 2);
    }

    static long loopWithConstant(int repeats) {
        long acc = 0;
        for (int i = 0; i < repeats; i++) {
            acc += slowFib(CONSTANT_INPUT);
        }
        return acc;
    }

    static long loopWithRuntimeValue(int repeats) {
        long acc = 0;
        for (int i = 0; i < repeats; i++) {
            acc += slowFib(runtimeInput);
        }
        return acc;
    }

    public static void main(String[] args) {
        int repeats = 200_000;
        long r1 = 0;
        long r2 = 0;
        long constNs = 0;
        long runtimeNs = 0;

        for (int round = 0; round < 6; round++) {   // last round is the steady-state one
            long t0 = System.nanoTime();
            r1 = loopWithConstant(repeats);
            constNs = System.nanoTime() - t0;

            long t1 = System.nanoTime();
            r2 = loopWithRuntimeValue(repeats);
            runtimeNs = System.nanoTime() - t1;
        }
        sink = r1 + r2;

        System.out.println("constant input:      " + constNs / 1_000 + " us");
        System.out.println("runtime input:       " + runtimeNs / 1_000 + " us");
        System.out.println("same result: " + (r1 == r2) + " (" + r1 + ")");
    }
}
