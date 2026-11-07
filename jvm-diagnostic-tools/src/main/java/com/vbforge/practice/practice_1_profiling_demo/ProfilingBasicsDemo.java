package com.vbforge.practice.practice_1_profiling_demo;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Demonstrates basic profiling and measurement techniques in Java.
 *
 * Profiling philosophy:
 *   "Measure, don't guess." — premature optimization is the root of all evil (Knuth).
 *   Profile first → identify actual bottleneck → optimize → measure again.
 *
 * Levels of measurement:
 *   1. Wall-clock time  — System.nanoTime() for elapsed time between two points
 *   2. CPU time         — ThreadMXBean.getCurrentThreadCpuTime()
 *   3. Memory usage     — MemoryMXBean / Runtime.freeMemory() delta
 *   4. GC activity      — GarbageCollectorMXBean for collection counts/times
 *   5. Full profiler    — async-profiler, JFR (Java Flight Recorder) — attach externally
 *
 * Microbenchmarking pitfalls:
 *   - JIT warm-up: HotSpot compiles hot methods after ~10,000 invocations.
 *     Measure after warm-up or use JMH (Java Microbenchmark Harness).
 *   - Dead-code elimination: JIT may optimize away code with no side effects.
 *     Use results to prevent elimination.
 *   - GC interference: a GC pause mid-measurement skews results.
 *   - OS scheduling: other processes affect wall-clock time.
 *
 * For production benchmarking: always use JMH. This demo shows the concepts.
 */
public class ProfilingBasicsDemo {

    // -------------------------------------------------------------------------
    // 1. Simple wall-clock timer utility
    // -------------------------------------------------------------------------

    /**
     * Measures wall-clock elapsed nanoseconds for a Runnable.
     *
     * System.nanoTime():
     *   - Not related to wall-clock "real" time — counts elapsed nanoseconds
     *     from an arbitrary origin (usually JVM start).
     *   - Monotonically non-decreasing — safe for elapsed-time measurement.
     *   - Resolution: OS/hardware dependent, typically ~100ns on modern systems.
     *
     * System.currentTimeMillis():
     *   - Real wall-clock time (UTC milliseconds since epoch)
     *   - Can jump backward due to NTP adjustments — DO NOT use for intervals.
     *   - Use for timestamps, logging, deadlines.
     */
    public static long measureNanos(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return System.nanoTime() - start;
    }

    /**
     * Converts nanoseconds to a human-readable string with appropriate unit.
     */
    public static String formatNanos(long nanos) {
        if (nanos < 1_000) {
            return nanos + " ns";
        } else if (nanos < 1_000_000) {
            return String.format("%.2f µs", nanos / 1_000.0);
        } else if (nanos < 1_000_000_000) {
            return String.format("%.2f ms", nanos / 1_000_000.0);
        } else {
            return String.format("%.2f s", nanos / 1_000_000_000.0);
        }
    }

    // -------------------------------------------------------------------------
    // 2. Warm-up aware measurement (poor-man's JMH)
    // -------------------------------------------------------------------------

    /**
     * Runs the task WARMUP_ITERATIONS times first (JIT compiles it),
     * then measures MEASURE_ITERATIONS times and returns average nanoseconds.
     *
     * This is a simplified model of what JMH does automatically.
     * For real benchmarks: use JMH.
     */
    public static long benchmarkAvgNanos(Runnable task, int warmupIterations, int measureIterations) {
        // warm-up — let JIT compile the task
        for (int i = 0; i < warmupIterations; i++) {
            task.run();
        }

        // measure
        long total = 0;
        for (int i = 0; i < measureIterations; i++) {
            total += measureNanos(task);
        }
        return total / measureIterations;
    }

    // -------------------------------------------------------------------------
    // 3. Memory usage measurement
    // -------------------------------------------------------------------------

    /**
     * Rough heap usage delta before/after a block.
     *
     * Limitations:
     *   - GC may run between measurements (skewing the delta)
     *   - JIT may eliminate allocations (escape analysis)
     *   - Runtime.freeMemory() is approximate
     *
     * For accurate allocation profiling: use async-profiler or JFR.
     */
    public static long measureHeapDeltaBytes(Runnable task) {
        Runtime rt = Runtime.getRuntime();
        System.gc();   // request GC to get a cleaner baseline (not guaranteed)
        long before = rt.totalMemory() - rt.freeMemory();
        task.run();
        long after = rt.totalMemory() - rt.freeMemory();
        return after - before;
    }

    // -------------------------------------------------------------------------
    // 4. JVM MXBean — CPU time and GC stats
    // -------------------------------------------------------------------------

    /**
     * CPU time consumed by the current thread.
     * Excludes time thread is blocked or sleeping.
     * Requires ThreadMXBean.isCurrentThreadCpuTimeSupported() == true.
     */
    public static long currentThreadCpuNanos() {
        ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
        if (tmx.isCurrentThreadCpuTimeSupported()) {
            return tmx.getCurrentThreadCpuTime();
        }
        return -1; // unsupported on this JVM
    }

    /**
     * Prints GC summary: collection counts and total pause time per collector.
     * Useful to detect GC pressure after a workload.
     */
    public static void printGcStats() {
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.printf("  GC [%-20s]: collections=%-5d  time=%d ms%n",
                    gc.getName(),
                    gc.getCollectionCount(),
                    gc.getCollectionTime());
        }
    }

    /**
     * Prints heap memory usage via MemoryMXBean.
     */
    public static void printHeapUsage() {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        System.out.printf("  Heap: used=%d MB  committed=%d MB  max=%d MB%n",
                heap.getUsed()      / 1024 / 1024,
                heap.getCommitted() / 1024 / 1024,
                heap.getMax()       / 1024 / 1024);
    }

    // -------------------------------------------------------------------------
    // 5. Controlled workloads for demo comparison
    // -------------------------------------------------------------------------

    /**
     * CPU-bound: sorts a shuffled list.
     * Result consumed to prevent JIT dead-code elimination.
     */
    public static int cpuWorkload(int size) {
        List<Integer> list = new ArrayList<>(size);
        for (int i = size; i >= 0; i--) {
            list.add(i);
        }
        Collections.sort(list);
        return list.get(0); // consume result
    }

    /**
     * Allocation-heavy: creates many short-lived objects.
     * Exercises the GC young-generation.
     */
    public static int allocationWorkload(int count) {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            String s = "item-" + i;  // new String object each iteration
            sum += s.length();
        }
        return sum;
    }

    // -------------------------------------------------------------------------
    // main — illustrative profiling output
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        System.out.println("=== Profiling Basics Demo ===\n");

        // Wall-clock measurement
        System.out.println("-- Wall-clock measurement --");
        long nanos = measureNanos(() -> cpuWorkload(10_000));
        System.out.println("cpuWorkload(10_000): " + formatNanos(nanos));

        // Warm-up aware benchmark
        System.out.println("\n-- Warm-up aware benchmark (sort 5000 elements) --");
        long avg = benchmarkAvgNanos(() -> cpuWorkload(5_000), 50, 200);
        System.out.println("Average (after JIT warm-up): " + formatNanos(avg));

        // Heap delta
        System.out.println("\n-- Heap allocation delta --");
        long delta = measureHeapDeltaBytes(() -> allocationWorkload(10_000));
        System.out.printf("Approximate heap delta: %+d bytes%n", delta);

        // GC stats
        System.out.println("\n-- GC statistics --");
        printGcStats();

        // Heap usage
        System.out.println("\n-- Heap memory usage --");
        printHeapUsage();

        // CPU time
        System.out.println("\n-- Thread CPU time --");
        long cpuBefore = currentThreadCpuNanos();
        cpuWorkload(20_000);
        long cpuAfter = currentThreadCpuNanos();
        if (cpuBefore >= 0) {
            System.out.println("CPU time for cpuWorkload(20_000): " + formatNanos(cpuAfter - cpuBefore));
        } else {
            System.out.println("CPU time measurement not supported on this JVM.");
        }
    }
}
