package com.vbforge.jit_and_warmup;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Interpreter → C1 → C2: how HotSpot turns slow bytecode into fast machine code.
 *
 * <p>Run it:  {@code mvn exec:java -Dexec.mainClass="com.vbforge.jit_and_warmup.TieredCompilationDemo"}
 * <p>Watch it: add {@code -XX:+PrintCompilation} to the JVM options and look at the tier column.
 */
public class TieredCompilationDemo {

    /** Written once per measurement so the JIT cannot prove the measured work is unused. */
    private static volatile long sink;

    public static void main(String[] args) {
        tiersDemo();
        System.out.println("---");
        warmupCurveDemo();
        System.out.println("---");
        onStackReplacementDemo();
        System.out.println("---");
        jitIntrospectionDemo();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 1. The five tiers
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: HotSpot starts every method in the <b>interpreter</b> (tier 0) and
     * promotes it through compiler tiers based on how often it runs.
     *
     * <p>Why it matters: the JVM cannot know in advance which methods are hot.
     * Compiling everything up-front would make start-up slow; interpreting forever
     * would make steady state slow. Tiered compilation gives fast start-up (C1)
     * <i>and</i> fast peak performance (C2).
     *
     * <ul>
     *   <li>Tier 0 – interpreter: no compilation cost, slowest execution (roughly 10–50x slower)</li>
     *   <li>Tier 1 – C1, no profiling: for trivial methods that C2 could not improve</li>
     *   <li>Tier 2 – C1, limited profiling: used when the C2 queue is busy</li>
     *   <li>Tier 3 – C1, full profiling: collects type and branch statistics for C2</li>
     *   <li>Tier 4 – C2: aggressive, profile-guided optimisation</li>
     * </ul>
     *
     * <p>Typical path of a hot method: 0 → 3 → 4. A trivial method: 0 → 1.
     *
     * <p>Common gotcha: "Java is slow at start" is not a myth — the first calls really
     * do run in the interpreter. That is exactly what <i>warm-up</i> means.
     */
    public static void tiersDemo() {
        for (int level = 0; level <= 4; level++) {
            System.out.println("Tier " + level + ": " + describeTier(level));
        }
    }

    /**
     * @param level compilation tier, 0 (interpreter) to 4 (C2)
     * @return a one-line description of that tier
     * @throws IllegalArgumentException if level is outside 0..4
     */
    public static String describeTier(int level) {
        switch (level) {
            case 0:
                return "Interpreter - executes bytecode directly and counts invocations and loop back-edges";
            case 1:
                return "C1 - fully optimised, no profiling (trivial methods only)";
            case 2:
                return "C1 - limited profiling (counters only, used when the C2 queue is long)";
            case 3:
                return "C1 - full profiling (receiver types, branch frequencies); slower than tiers 1 and 2";
            case 4:
                return "C2 - aggressive optimisation driven by the collected profile";
            default:
                throw new IllegalArgumentException("Tier must be 0..4 but was " + level);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. The warm-up curve
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: the same work, measured in consecutive batches, gets faster
     * while the JIT compiles the hot code.
     *
     * <p>Why it matters: a single timing of a Java method is meaningless. The first
     * batch includes interpretation and compilation; later batches run compiled code.
     * Any benchmark must separate <i>warm-up</i> from <i>steady state</i>.
     *
     * <ul>
     *   <li>Work per batch: O(callsPerBatch × workSize) time, O(1) space</li>
     *   <li>Compilation happens on background compiler threads, so the curve is
     *       noisy and machine-dependent — never assert on it in a test</li>
     * </ul>
     *
     * <p>Common gotcha: the output differs from run to run. That is the lesson,
     * not a bug.
     */
    public static void warmupCurveDemo() {
        long[] batchNanos = measureBatches(10, 2_000, 1_000);
        for (int i = 0; i < batchNanos.length; i++) {
            System.out.println("batch " + i + ": " + batchNanos[i] / 1_000 + " us");
        }
        long first = batchNanos[0];
        long last = batchNanos[batchNanos.length - 1];
        System.out.println("first batch / last batch = " + first + " / " + last + " ns (usually first >> last)");
    }

    /**
     * Deterministic CPU-bound work: a rolling checksum over {@code 0..n-1}.
     *
     * @param n number of loop iterations
     * @return the checksum (0 for n ≤ 1)
     */
    public static long checksum(int n) {
        long acc = 0;
        for (int i = 0; i < n; i++) {
            acc = acc * 31 + (i ^ (i >>> 3));
        }
        return acc;
    }

    /**
     * Times {@code batches} consecutive batches, each calling {@link #checksum(int)}
     * {@code callsPerBatch} times.
     *
     * @return elapsed nanoseconds for each batch, in order
     * @throws IllegalArgumentException if batches or callsPerBatch is not positive, or workSize is negative
     */
    public static long[] measureBatches(int batches, int callsPerBatch, int workSize) {
        if (batches <= 0 || callsPerBatch <= 0 || workSize < 0) {
            throw new IllegalArgumentException("batches and callsPerBatch must be > 0 and workSize >= 0");
        }
        long[] nanos = new long[batches];
        for (int b = 0; b < batches; b++) {
            long start = System.nanoTime();
            long acc = 0;
            for (int c = 0; c < callsPerBatch; c++) {
                acc += checksum(workSize);
            }
            nanos[b] = System.nanoTime() - start;
            sink = acc; // keeps the work observable
        }
        return nanos;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. On-stack replacement (OSR)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: a method that is called <b>once</b> but loops for a long time cannot
     * wait for its next invocation to be compiled. HotSpot counts loop back-edges and,
     * when they get hot, compiles the loop and <i>swaps the running frame</i> to the
     * compiled version in the middle of execution. That is on-stack replacement.
     *
     * <p>Why it matters: it is why a {@code main} that contains one giant loop
     * still speeds up. But OSR code is often less optimised than a normal compilation
     * of the same method, so timing "one big loop in main" does not predict how the
     * method behaves when called normally.
     *
     * <ul>
     *   <li>Trigger: back-edge counter, not the invocation counter</li>
     *   <li>Shown in {@code -XX:+PrintCompilation} with a {@code %} marker</li>
     * </ul>
     *
     * <p>Common gotcha: benchmarks written as a single loop in {@code main} measure
     * OSR code. Put the work in a method and call it many times.
     */
    public static void onStackReplacementDemo() {
        long start = System.nanoTime();
        long result = singleCallHotLoop(100_000_000L);
        long elapsed = System.nanoTime() - start;
        sink = result;
        System.out.println("one call, 100M iterations: result=" + result + ", " + elapsed / 1_000_000 + " ms");
        System.out.println("(look for a '%' line in -XX:+PrintCompilation output: that is the OSR compile)");
    }

    /**
     * @param iterations loop count
     * @return the sum 0 + 1 + … + (iterations - 1); 0 if iterations ≤ 0
     */
    public static long singleCallHotLoop(long iterations) {
        long sum = 0;
        for (long i = 0; i < iterations; i++) {
            sum += i;
        }
        return sum;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. Looking at the JIT from the outside
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: the standard way to ask "which JIT is this JVM running?" plus the
     * handful of flags worth knowing.
     *
     * <p>Why it matters: you cannot reason about performance you cannot observe.
     * {@code -XX:+PrintCompilation} turns the invisible compiler into a log.
     *
     * <p>Common gotcha: {@code -Xint} and {@code -Xcomp} are experiment flags,
     * not tuning flags. Never ship them.
     */
    public static void jitIntrospectionDemo() {
        System.out.println("JIT: " + jitName());
        CompilationMXBean bean = ManagementFactory.getCompilationMXBean();
        if (bean != null && bean.isCompilationTimeMonitoringSupported()) {
            System.out.println("Total compilation time so far: " + bean.getTotalCompilationTime() + " ms");
        }
        for (Map.Entry<String, String> flag : jitFlagCheatSheet().entrySet()) {
            System.out.println(flag.getKey() + " -> " + flag.getValue());
        }
    }

    /**
     * @return the name of the JIT compiler reported by the JVM, or a placeholder if none is exposed
     */
    public static String jitName() {
        CompilationMXBean bean = ManagementFactory.getCompilationMXBean();
        return bean == null ? "none (interpreter only)" : bean.getName();
    }

    /**
     * @return JVM flag → what it does, in a stable (insertion) order
     */
    public static Map<String, String> jitFlagCheatSheet() {
        Map<String, String> flags = new LinkedHashMap<>();
        flags.put("-Xint", "interpreter only, JIT disabled (experiment: how slow is Java without it?)");
        flags.put("-Xcomp", "compile every method on first call (slow start; experiment only)");
        flags.put("-XX:TieredStopAtLevel=1", "stop at C1: fastest warm-up, lower peak performance");
        flags.put("-XX:-TieredCompilation", "disable tiers: interpreter then C2 only");
        flags.put("-XX:+PrintCompilation", "log each method as it is compiled, with its tier");
        flags.put("-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining", "log inlining decisions per call site");
        return flags;
    }
}
