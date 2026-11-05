package com.vbforge.jvm_basics_memory_and_gc;

import java.util.List;

/**
 * Tasks for {@link GcCollectorsDemo}: implement each method so that
 * {@code GcCollectorsTasksTest} passes.
 */
public class GcCollectorsTasks {

    /**
     * Return the share of wall-clock time spent in GC pauses, as a percentage:
     * {@code 100 * totalPauseMs / runtimeMs}.
     *
     * <p>Examples:
     * <pre>
     * gcOverheadPercent(300, 10_000) → 3.0
     * gcOverheadPercent(0, 5_000)    → 0.0
     * gcOverheadPercent(50, 100)     → 50.0
     * gcOverheadPercent(10, 0)       → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Beware of integer division — convert before dividing</li>
     *   <li>A non-positive runtime makes the ratio meaningless</li>
     * </ul>
     *
     * @param totalPauseMs the summed stop-the-world pause time in milliseconds
     * @param runtimeMs    the observed wall-clock time in milliseconds
     * @return the overhead percentage
     * @throws IllegalArgumentException if {@code runtimeMs <= 0}
     */
    public double gcOverheadPercent(long totalPauseMs, long runtimeMs) {
        throw new UnsupportedOperationException("TODO: implement gcOverheadPercent");
    }

    /**
     * Return the {@code percentile}-th percentile of the pause durations using the
     * <b>nearest-rank</b> method: sort ascending, take rank {@code ceil(p/100 * n)}
     * (1-based). An empty list gives {@code 0}. The input list must not be modified.
     *
     * <p>Examples:
     * <pre>
     * pausePercentile([5, 6, 5, 7, 6, 5, 6, 5, 7, 480], 50)  → 6
     * pausePercentile([5, 6, 5, 7, 6, 5, 6, 5, 7, 480], 99)  → 480
     * pausePercentile([5, 6, 5, 7, 6, 5, 6, 5, 7, 480], 90)  → 7
     * pausePercentile([42], 99)                              → 42
     * pausePercentile([], 99)                                → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Sort a copy, not the caller's list</li>
     *   <li>Rounding up the rank is what makes the tail visible</li>
     * </ul>
     *
     * @param pausesMs   pause durations in milliseconds
     * @param percentile a value in {@code (0, 100]}
     * @return the pause duration at that percentile
     */
    public long pausePercentile(List<Long> pausesMs, double percentile) {
        throw new UnsupportedOperationException("TODO: implement pausePercentile");
    }

    /**
     * Suggest a collector name for a machine and goal. Return one of the strings
     * {@code "SERIAL"}, {@code "PARALLEL"}, {@code "G1"}, {@code "ZGC"} using these rules,
     * checked in order:
     * <ol>
     *   <li>fewer than 2 CPUs <b>or</b> less than 2048 MB of memory → {@code SERIAL}</li>
     *   <li>goal {@code "LATENCY"} and at least 8192 MB → {@code ZGC}</li>
     *   <li>goal {@code "THROUGHPUT"} → {@code PARALLEL}</li>
     *   <li>anything else → {@code G1}</li>
     * </ol>
     *
     * <p>Examples:
     * <pre>
     * recommendCollector(1, 4096, "LATENCY")      → "SERIAL"
     * recommendCollector(4, 1024, "THROUGHPUT")   → "SERIAL"
     * recommendCollector(8, 32768, "LATENCY")     → "ZGC"
     * recommendCollector(8, 4096, "LATENCY")      → "G1"
     * recommendCollector(8, 16384, "THROUGHPUT")  → "PARALLEL"
     * recommendCollector(8, 16384, "BALANCED")    → "G1"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Order of the checks matters — the first matching rule wins</li>
     *   <li>Compare strings with {@code equals}, not {@code ==}</li>
     * </ul>
     *
     * @param cpus     available processors
     * @param memoryMb machine memory in megabytes
     * @param goal     {@code "LATENCY"}, {@code "THROUGHPUT"} or anything else for balanced
     * @return the collector name
     */
    public String recommendCollector(int cpus, long memoryMb, String goal) {
        throw new UnsupportedOperationException("TODO: implement recommendCollector");
    }

    /**
     * Build a JVM launch fragment: {@code "-Xms<xms> -Xmx<xmx> <collector flag>"}, joined
     * by single spaces. The collector flags are {@code SERIAL → -XX:+UseSerialGC},
     * {@code PARALLEL → -XX:+UseParallelGC}, {@code G1 → -XX:+UseG1GC},
     * {@code ZGC → -XX:+UseZGC}. An unknown collector name throws.
     *
     * <p>Examples:
     * <pre>
     * jvmFlags("512m", "4g", "G1")   → "-Xms512m -Xmx4g -XX:+UseG1GC"
     * jvmFlags("2g", "2g", "ZGC")    → "-Xms2g -Xmx2g -XX:+UseZGC"
     * jvmFlags("64m", "256m", "CMS") → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A {@code switch} on the collector name keeps the mapping readable</li>
     * </ul>
     *
     * @param xms       initial heap size such as {@code "512m"}
     * @param xmx       maximum heap size such as {@code "4g"}
     * @param collector one of SERIAL, PARALLEL, G1, ZGC
     * @return the launch fragment
     * @throws IllegalArgumentException for an unknown collector
     */
    public String jvmFlags(String xms, String xmx, String collector) {
        throw new UnsupportedOperationException("TODO: implement jvmFlags");
    }
}
