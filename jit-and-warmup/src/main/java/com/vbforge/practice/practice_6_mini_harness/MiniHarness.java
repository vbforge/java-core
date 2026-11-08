package com.vbforge.practice.practice_6_mini_harness;

import java.util.function.LongSupplier;

/**
 * A hand-made micro-harness: warm-up rounds (discarded), then measured rounds.
 * Every result is folded into a volatile sink so the JIT cannot remove the work.
 * For real decisions use JMH, which also forks a fresh JVM per benchmark.
 */
public final class MiniHarness {

    static volatile long sink;

    private MiniHarness() {
    }

    /** Returns the nanoseconds of each measured round; the task runs {@code callsPerRound} times per round. */
    public static long[] run(LongSupplier task, int warmupRounds, int measuredRounds, int callsPerRound) {
        for (int w = 0; w < warmupRounds; w++) {
            round(task, callsPerRound);
        }
        long[] samples = new long[measuredRounds];
        for (int m = 0; m < measuredRounds; m++) {
            long start = System.nanoTime();
            round(task, callsPerRound);
            samples[m] = System.nanoTime() - start;
        }
        return samples;
    }

    private static void round(LongSupplier task, int calls) {
        long acc = 0;
        for (int i = 0; i < calls; i++) {
            acc += task.getAsLong();
        }
        sink = acc;
    }
}
