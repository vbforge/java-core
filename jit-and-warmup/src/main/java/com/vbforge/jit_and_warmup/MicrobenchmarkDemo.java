package com.vbforge.jit_and_warmup;

import java.util.Arrays;
import java.util.function.LongSupplier;

/**
 * Why measuring Java is hard, how to do it by hand, and what JMH does for you.
 *
 * <p>Run it:  {@code mvn exec:java -Dexec.mainClass="com.vbforge.jit_and_warmup.MicrobenchmarkDemo"}
 *
 * <p>This module uses the Java standard library only, so JMH itself is shown as
 * source text (section 4) and in the README rather than added as a dependency.
 */
public class MicrobenchmarkDemo {

    /** The "black hole": a volatile write the JIT cannot prove is unused. */
    private static volatile long sink;

    public static void main(String[] args) {
        naiveTimingDemo();
        System.out.println("---");
        deadCodeDemo();
        System.out.println("---");
        harnessDemo();
        System.out.println("---");
        jmhFirstLookDemo();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 1. The naive stopwatch
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: {@code start = nanoTime(); work(); elapsed = nanoTime() - start}.
     *
     * <p>Why it matters: it looks right, and it is wrong for Java. The first run includes
     * interpretation and compilation, later runs do not, and the numbers jump around.
     * Run it twice and you will see two different answers; run it five times and
     * the first is usually the slowest.
     *
     * <p>Common gotcha: a single measurement is an anecdote. You need warm-up, many
     * iterations and a statistic that ignores outliers.
     */
    public static void naiveTimingDemo() {
        LongSupplier work = () -> TieredCompilationDemo.checksum(50_000);
        for (int run = 0; run < 5; run++) {
            System.out.println("run " + run + ": " + timeOnceNanos(work) + " ns");
        }
    }

    /**
     * Runs the work once and returns how long it took. The result is consumed
     * so the work is not dead code.
     *
     * @param work the work to time
     * @return elapsed nanoseconds
     */
    public static long timeOnceNanos(LongSupplier work) {
        long start = System.nanoTime();
        consume(work.getAsLong());
        return System.nanoTime() - start;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. Dead-code elimination
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: if a computed value is never used and the computation has no side
     * effects, the JIT may delete the whole computation.
     *
     * <p>Why it matters: a benchmark that throws its result away can report
     * "0 ns per operation" because it measured nothing. The cure is to <i>consume</i> every
     * result: return it (JMH consumes returned values) or feed it to a {@code Blackhole}.
     * The {@link #consume(long)} method below is a hand-made black hole.
     *
     * <ul>
     *   <li>Cost of consuming: one volatile write — small, and the same in every benchmark</li>
     * </ul>
     *
     * <p>Common gotcha: the same family of traps includes <b>constant folding</b>
     * (benchmarking {@code sqrt(2.0)} measures a constant) and <b>loop hoisting</b>.
     * Feed benchmarks from non-constant inputs (JMH {@code @State} fields).
     */
    public static void deadCodeDemo() {
        consume(7);
        computeAndDiscard(100_000);
        System.out.println("after a discarded computation the sink still holds: " + sink());
        consume(squareSum(100_000));
        System.out.println("after a consumed computation the sink holds: " + sink());
    }

    /** Sums the squares of 0..n-1 as a long. */
    public static long squareSum(int n) {
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += (long) i * i;
        }
        return sum;
    }

    /** Computes {@link #squareSum(int)} and drops the result: the JIT is free to remove all of it. */
    public static void computeAndDiscard(int n) {
        squareSum(n);
    }

    /** Stores the value in a volatile field so the computation that produced it cannot be removed. */
    public static void consume(long value) {
        sink = value;
    }

    /** @return the last value passed to {@link #consume(long)} */
    public static long sink() {
        return sink;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. A minimal harness: warm-up, then measure
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: the smallest honest benchmark loop. Run the work a number of times and
     * throw the timings away (warm-up), then run it again and keep the timings.
     *
     * <p>Why it matters: this is the core of what JMH automates. What JMH adds on top:
     * <ul>
     *   <li>separate <b>forked JVMs</b>, so one benchmark's type profile cannot pollute the next</li>
     *   <li>time-based iterations, per-fork statistics, error bars</li>
     *   <li>correct black-holing of results, and generated code that resists loop optimisations</li>
     *   <li>parameters, thread scaling, profilers</li>
     * </ul>
     *
     * <p>Common gotcha: this harness runs everything in the <i>current</i> JVM, so it cannot
     * protect you from profile pollution. It is for learning, not for decisions.
     */
    public static void harnessDemo() {
        LongSupplier work = () -> TieredCompilationDemo.checksum(50_000);
        long[] samples = runHarness(work, 200, 20);
        long[] sorted = Arrays.copyOf(samples, samples.length);
        Arrays.sort(sorted);
        System.out.println("min " + sorted[0] + " ns, median " + sorted[sorted.length / 2]
                + " ns, max " + sorted[sorted.length - 1] + " ns");
    }

    /**
     * Runs {@code warmupIterations} untimed-and-discarded iterations, then
     * {@code measureIterations} timed ones.
     *
     * @param work               the work to measure; its result is consumed
     * @param warmupIterations   iterations whose timings are discarded (≥ 0)
     * @param measureIterations  iterations whose timings are returned (&gt; 0)
     * @return one elapsed-nanosecond value per measured iteration, in order
     * @throws IllegalArgumentException if warmupIterations is negative or measureIterations is not positive
     */
    public static long[] runHarness(LongSupplier work, int warmupIterations, int measureIterations) {
        if (warmupIterations < 0 || measureIterations <= 0) {
            throw new IllegalArgumentException("warmupIterations must be >= 0 and measureIterations > 0");
        }
        for (int i = 0; i < warmupIterations; i++) {
            timeOnceNanos(work);
        }
        long[] samples = new long[measureIterations];
        for (int i = 0; i < measureIterations; i++) {
            samples[i] = timeOnceNanos(work);
        }
        return samples;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. JMH, first look
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: JMH (Java Microbenchmark Harness) is the OpenJDK project's benchmarking tool.
     * You write annotated methods; JMH generates the harness, forks JVMs, warms up, measures
     * and reports.
     *
     * <p>Why it matters: it is the accepted way to answer "is A faster than B?" for code
     * that runs in nanoseconds or microseconds. The listing below is a complete benchmark.
     * It is printed as text because this module deliberately has no dependencies.
     *
     * <p>Common gotcha: JMH numbers answer a narrow question about a small piece of code in
     * isolation. Confirm with a profiler or load test before restructuring real code for them.
     */
    public static void jmhFirstLookDemo() {
        System.out.println(jmhExampleSource());
    }

    /** @return the source of a minimal JMH benchmark class, as text */
    public static String jmhExampleSource() {
        return String.join("\n",
                "@State(Scope.Thread)",
                "@BenchmarkMode(Mode.AverageTime)",
                "@OutputTimeUnit(TimeUnit.NANOSECONDS)",
                "@Warmup(iterations = 5, time = 1)",
                "@Measurement(iterations = 5, time = 1)",
                "@Fork(2)",
                "public class SumBenchmark {",
                "",
                "    @Param({\"1000\", \"100000\"})",
                "    int size;",
                "    int[] data;",
                "",
                "    @Setup",
                "    public void setUp() {",
                "        data = new int[size];",
                "        for (int i = 0; i < size; i++) data[i] = i;",
                "    }",
                "",
                "    @Benchmark",
                "    public long sumLoop() {          // returned value is consumed by JMH",
                "        long sum = 0;",
                "        for (int v : data) sum += v;",
                "        return sum;",
                "    }",
                "",
                "    @Benchmark",
                "    public void sumToBlackhole(Blackhole bh) {   // explicit black hole",
                "        long sum = 0;",
                "        for (int v : data) sum += v;",
                "        bh.consume(sum);",
                "    }",
                "}");
    }
}
