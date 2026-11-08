package com.vbforge.practice.practice_12_deoptimization;

/**
 * Deoptimisation: the JIT speculates ("only one implementation of Greeter is loaded"),
 * then a new class breaks the assumption and the compiled code is thrown away.
 * Class Late is not touched until phase 2, so it is loaded only then.
 * Run with -XX:+PrintCompilation and look for "made not entrant" after the "phase 2" line.
 * Batch timings may show a short bump; checksums are deterministic.
 */
public class DeoptimizationDemo {

    static volatile long sink;

    interface Greeter {
        long score(long x);
    }

    static final class Early implements Greeter {
        public long score(long x) {
            return x + 1;
        }
    }

    static final class Late implements Greeter {
        public long score(long x) {
            return x + 2;
        }
    }

    static long loop(Greeter g, int n) {
        long acc = 0;
        for (int i = 0; i < n; i++) {
            acc += g.score(i);
        }
        return acc;
    }

    static long batch(Greeter g) {
        long t = System.nanoTime();
        sink = loop(g, 1_000_000);
        return (System.nanoTime() - t) / 1_000;
    }

    public static void main(String[] args) {
        Greeter early = new Early();
        System.out.println("phase 1: only Early is loaded, JIT specialises for it");
        for (int i = 1; i <= 20; i++) {
            long us = batch(early);
            if (i % 5 == 0) {
                System.out.println("  batch " + i + ": " + us + " us");
            }
        }

        System.out.println("phase 2: Late is used for the first time, class gets loaded, speculation breaks");
        Greeter late = new Late();
        for (int i = 1; i <= 6; i++) {
            System.out.println("  batch " + i + " (Late): " + batch(late) + " us");
        }
        System.out.println("checksums: Early=" + loop(early, 1000) + ", Late=" + loop(late, 1000));
    }
}
