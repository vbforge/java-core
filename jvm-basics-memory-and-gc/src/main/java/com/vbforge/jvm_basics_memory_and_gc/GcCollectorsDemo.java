package com.vbforge.jvm_basics_memory_and_gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Garbage collector basics and the Serial / Parallel / G1 / ZGC trade-offs.
 */
public class GcCollectorsDemo {

    public static void main(String[] args) {
        stopTheWorldDemo();
        System.out.println("---");
        collectorProfilesDemo();
        System.out.println("---");
        selectionDemo();
        System.out.println("---");
        metricsDemo();
        System.out.println("---");
        liveCollectorsDemo();
    }

    /** The collectors you will meet in practice. */
    public enum Collector {
        SERIAL("-XX:+UseSerialGC",
                "One GC thread, fully stop-the-world",
                "tiny heaps, 1 CPU, small containers"),
        PARALLEL("-XX:+UseParallelGC",
                "Many GC threads, fully stop-the-world",
                "batch jobs where total throughput matters more than pause length"),
        G1("-XX:+UseG1GC",
                "Region-based, mostly concurrent marking, incremental evacuation pauses",
                "balanced default for medium and large heaps (JVM default since Java 9)"),
        ZGC("-XX:+UseZGC",
                "Region-based, concurrent marking AND relocation, sub-millisecond pauses",
                "very large heaps and strict latency goals, at some CPU/memory cost");

        private final String flag;
        private final String design;
        private final String bestFor;

        Collector(String flag, String design, String bestFor) {
            this.flag = flag;
            this.design = design;
            this.bestFor = bestFor;
        }

        public String flag() {
            return flag;
        }

        public String design() {
            return design;
        }

        public String bestFor() {
            return bestFor;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Stop-the-world, throughput and latency
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: a collector must sometimes pause ALL application threads (a
     *       "stop-the-world" pause, STW) to reach a consistent view of the heap.
     *       Two numbers describe the cost:
     *         throughput = share of time NOT spent in GC
     *         latency    = how long individual pauses are
     *
     * Why it matters: you cannot maximise both. Fewer, longer pauses give high throughput
     *       (Parallel). Many short pauses or concurrent work give low latency (G1, ZGC)
     *       but use more CPU, so total throughput drops a little.
     *
     * Complexity: STW cost is roughly proportional to the live data that must be
     *       marked or copied inside the pause — not to the amount of garbage.
     *
     * Common gotcha: an average pause hides tail latency. Look at percentiles
     *       (p99, p99.9), not at the mean.
     */
    public static void stopTheWorldDemo() {
        List<Long> pauses = new ArrayList<>();
        Collections.addAll(pauses, 5L, 6L, 5L, 7L, 6L, 5L, 6L, 5L, 7L, 480L);
        System.out.println("pauses (ms)    : " + pauses);
        System.out.println("overhead       : " + gcOverheadPercent(totalOf(pauses), 10_000) + " %");
        System.out.println("p50 pause      : " + pausePercentile(pauses, 50) + " ms");
        System.out.println("p99 pause      : " + pausePercentile(pauses, 99) + " ms  <- the tail the mean hides");
    }

    private static long totalOf(List<Long> values) {
        long sum = 0;
        for (long value : values) {
            sum += value;
        }
        return sum;
    }

    /** Percentage of wall-clock time spent paused: {@code 100 * totalPauseMs / runtimeMs}. */
    public static double gcOverheadPercent(long totalPauseMs, long runtimeMs) {
        if (runtimeMs <= 0) {
            throw new IllegalArgumentException("runtimeMs must be positive");
        }
        return 100.0 * totalPauseMs / runtimeMs;
    }

    /** Nearest-rank percentile ({@code 0 < p <= 100}) of the pause list; 0 for an empty list. */
    public static long pausePercentile(List<Long> pausesMs, double percentile) {
        if (pausesMs.isEmpty()) {
            return 0;
        }
        List<Long> sorted = new ArrayList<>(pausesMs);
        Collections.sort(sorted);
        int rank = (int) Math.ceil(percentile / 100.0 * sorted.size());
        return sorted.get(Math.max(rank, 1) - 1);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. Collector profiles: Serial, Parallel, G1, ZGC
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: the design and sweet spot of each collector (see the Collector enum).
     *
     * G1 vs ZGC trade-offs:
     *   G1  — pause target via -XX:MaxGCPauseMillis (default 200 ms, a goal, not a
     *         guarantee); pauses grow with live set and remembered-set work; lower CPU
     *         and memory overhead; excellent all-rounder.
     *   ZGC — pauses stay in the sub-millisecond to low-millisecond range regardless of
     *         heap size (up to multi-terabyte); pays with higher CPU use (load barriers,
     *         concurrent threads) and needs extra headroom so concurrent collection can
     *         keep up with allocation. In JDK 21 enable generational mode with
     *         -XX:+ZGenerational; from JDK 23 generational is the default, and JDK 24
     *         removed the non-generational mode.
     *
     * Why it matters: choosing a collector is choosing which cost you can afford.
     *
     * Common gotcha: if allocation outpaces a concurrent collector, G1 falls back to a
     *       long full GC and ZGC stalls allocating threads ("allocation stall") — more
     *       headroom (-Xmx) is the first fix, not exotic flags.
     */
    public static void collectorProfilesDemo() {
        for (Collector collector : Collector.values()) {
            System.out.println(collector + "  " + collector.flag());
            System.out.println("    design  : " + collector.design());
            System.out.println("    best for: " + collector.bestFor());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Choosing a collector
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: HotSpot picks Serial on a machine with fewer than 2 CPUs or roughly less
     *       than 2 GB of memory (a "non-server-class machine"), otherwise G1.
     *       The latency / throughput branches below are a teaching heuristic, not a
     *       HotSpot rule.
     *
     * Why it matters: containers often report 1 CPU — then Serial is selected
     *       silently and you may be surprised by the GC log.
     *
     * Common gotcha: measure with your real workload before leaving the default.
     */
    public static void selectionDemo() {
        System.out.println("1 CPU, 4 GB, any goal       -> " + recommend(1, 4096, "BALANCED"));
        System.out.println("8 CPUs, 16 GB, BALANCED     -> " + recommend(8, 16384, "BALANCED"));
        System.out.println("8 CPUs, 32 GB, LATENCY      -> " + recommend(8, 32768, "LATENCY"));
        System.out.println("8 CPUs, 16 GB, THROUGHPUT   -> " + recommend(8, 16384, "THROUGHPUT"));
        System.out.println("flags: " + jvmFlags("512m", "4g", Collector.G1));
    }

    /**
     * Suggests a collector. {@code goal} is {@code THROUGHPUT}, {@code LATENCY} or anything
     * else (treated as balanced).
     */
    public static Collector recommend(int cpus, long memoryMb, String goal) {
        if (cpus < 2 || memoryMb < 2048) {
            return Collector.SERIAL;
        }
        if ("LATENCY".equals(goal) && memoryMb >= 8192) {
            return Collector.ZGC;
        }
        if ("THROUGHPUT".equals(goal)) {
            return Collector.PARALLEL;
        }
        return Collector.G1;
    }

    /** Builds a launch fragment such as {@code -Xms512m -Xmx4g -XX:+UseG1GC}. */
    public static String jvmFlags(String xms, String xmx, Collector collector) {
        return "-Xms" + xms + " -Xmx" + xmx + " " + collector.flag();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. Metrics that matter
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: the three numbers to watch — pause percentiles, GC overhead
     *       (target < 5–10 %), and live set after GC (stable = healthy, climbing = leak).
     *
     * Why it matters: they tell you whether to tune, add heap, or fix code.
     */
    public static void metricsDemo() {
        System.out.println("overhead of 300 ms in 10 s = " + gcOverheadPercent(300, 10_000) + " %");
        System.out.println("p100 of [3, 9, 4]          = " + pausePercentile(listOf(3L, 9L, 4L), 100) + " ms");
    }

    private static List<Long> listOf(Long... values) {
        List<Long> list = new ArrayList<>();
        Collections.addAll(list, values);
        return list;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. Observing the live JVM
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: GarbageCollectorMXBean exposes each active collector's name, collection
     *       count and accumulated collection time. G1 reports two beans
     *       (young + old), ZGC reports its cycle/pause beans, Serial reports
     *       "Copy" and "MarkSweepCompact".
     *
     * Why it matters: the same data a monitoring agent scrapes, available in-process.
     *
     * Common gotcha: names differ by collector and Java version — never hard-code
     *       them for logic.
     */
    public static void liveCollectorsDemo() {
        System.out.println("active collectors: " + activeCollectorNames());
        long before = totalCollectionCount();
        System.gc();
        System.out.println("collections before/after System.gc(): " + before + " / " + totalCollectionCount());
    }

    public static List<String> activeCollectorNames() {
        List<String> names = new ArrayList<>();
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            names.add(bean.getName());
        }
        return names;
    }

    /** Sum of collection counts over all collectors; ignores collectors that report -1. */
    public static long totalCollectionCount() {
        long total = 0;
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            total += Math.max(0, bean.getCollectionCount());
        }
        return total;
    }
}
