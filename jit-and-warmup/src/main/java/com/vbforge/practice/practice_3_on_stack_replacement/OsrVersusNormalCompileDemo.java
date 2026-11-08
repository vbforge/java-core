package com.vbforge.practice.practice_3_on_stack_replacement;

/**
 * On-stack replacement (OSR): a method called ONCE with a hot loop is compiled in the middle of the run.
 * The same total work split over many calls gets a normal compile at method entry.
 * Run with -XX:+PrintCompilation: OSR compiles carry a % marker (e.g. "%  3  ...::hotLoopOnce @ 8").
 * Both variants return the same checksum; the timings are printed for curiosity, not as a verdict.
 */
public class OsrVersusNormalCompileDemo {

    static volatile long sink;

    /** One call, one giant loop: only OSR can optimise it while it runs. */
    static long hotLoopOnce(int iterations) {
        long s = 0;
        for (int i = 0; i < iterations; i++) {
            s += (i * 31L) % 17;
        }
        return s;
    }

    /** Same body, small per-call work: reaches normal tier-3/tier-4 compiles through invocation counts. */
    static long smallChunk(int from, int to) {
        long s = 0;
        for (int i = from; i < to; i++) {
            s += (i * 31L) % 17;
        }
        return s;
    }

    public static void main(String[] args) {
        int total = 50_000_000;
        int chunk = 5_000;

        long t0 = System.nanoTime();
        long once = hotLoopOnce(total);
        long onceNs = System.nanoTime() - t0;
        sink = once;

        long t1 = System.nanoTime();
        long many = 0;
        for (int from = 0; from < total; from += chunk) {
            many += smallChunk(from, from + chunk);
        }
        long manyNs = System.nanoTime() - t1;
        sink = many;

        System.out.println("one call (OSR):   checksum=" + once + ", " + onceNs / 1_000_000 + " ms");
        System.out.println("many calls:       checksum=" + many + ", " + manyNs / 1_000_000 + " ms");
        System.out.println("same result: " + (once == many));
    }
}
