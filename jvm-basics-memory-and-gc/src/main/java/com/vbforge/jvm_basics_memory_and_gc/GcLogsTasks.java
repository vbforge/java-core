package com.vbforge.jvm_basics_memory_and_gc;

import java.util.List;

/**
 * Tasks for {@link GcLogsDemo}: implement each method so that
 * {@code GcLogsTasksTest} passes.
 *
 * <p>All lines use the Java 9+ unified format produced by {@code -Xlog:gc}:
 * <pre>
 * [4.400s][info][gc] GC(3) Pause Full (G1 Compaction Pause) 250M-&gt;48M(256M) 612.345ms
 * </pre>
 * Sizes carry a unit of {@code K}, {@code M} or {@code G} (1 M = 1024 K, 1 G = 1024 M).
 * Lines that are not collection summaries (for example {@code "[0.5s][info][gc] Using G1"})
 * are <b>ignored</b> by the list-based methods.
 */
public class GcLogsTasks {

    /**
     * Return how many kilobytes a single collection freed: heap used before minus heap
     * used after, both converted to kilobytes.
     *
     * <p>Examples:
     * <pre>
     * "[0.7s][info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 24M-&gt;4M(256M) 3.456ms" → 20480
     * "[9.0s][info][gc] GC(5) Pause Young (Normal) (G1 Evacuation Pause) 2G-&gt;512M(4G) 10.0ms"   → 1572864
     * "[1.0s][info][gc] GC(1) Pause Young (Allocation Failure) 900K-&gt;100K(2048K) 1.0ms"         → 800
     * "[0.5s][info][gc] Using G1"                                                                → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A regular expression with groups for number and unit keeps the parsing short</li>
     *   <li>Convert both sizes to the same unit <em>before</em> subtracting</li>
     * </ul>
     *
     * @param line one log line
     * @return kilobytes reclaimed
     * @throws IllegalArgumentException if the line is not a collection summary
     */
    public long reclaimedKb(String line) {
        throw new UnsupportedOperationException("TODO: implement reclaimedKb");
    }

    /**
     * Return the pause duration of a single collection line, in milliseconds.
     *
     * <p>Examples:
     * <pre>
     * "[0.7s][info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 24M-&gt;4M(256M) 3.456ms"  → 3.456
     * "[4.4s][info][gc] GC(3) Pause Full (G1 Compaction Pause) 250M-&gt;48M(256M) 612.345ms"         → 612.345
     * "[0.5s][info][gc] Using G1"                                                                 → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The duration is the last token and ends with {@code ms}</li>
     *   <li>Reject lines that do not look like a collection summary</li>
     * </ul>
     *
     * @param line one log line
     * @return the pause in milliseconds
     * @throws IllegalArgumentException if the line is not a collection summary
     */
    public double pauseMs(String line) {
        throw new UnsupportedOperationException("TODO: implement pauseMs");
    }

    /**
     * Return the sum of the pause durations of all collection lines in the log, in
     * milliseconds. Non-collection lines contribute nothing. An empty log gives {@code 0.0}.
     *
     * <p>Examples:
     * <pre>
     * [ "Using G1", GC(0) … 3.0ms, GC(1) … 7.0ms ] → 10.0
     * [ "Using G1" ]                               → 0.0
     * [ ]                                          → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You can reuse your single-line parser — decide how to skip lines it rejects</li>
     * </ul>
     *
     * @param lines the log lines
     * @return total pause time in milliseconds
     */
    public double totalPauseMs(List<String> lines) {
        throw new UnsupportedOperationException("TODO: implement totalPauseMs");
    }

    /**
     * Count the full collections in the log: collection lines whose description starts with
     * {@code "Pause Full"}.
     *
     * <p>Examples:
     * <pre>
     * [ Young, Young, Pause Full, Young, Pause Full ] → 2
     * [ Young, Young ]                                → 0
     * [ ]                                             → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Do not count lines that merely <em>mention</em> "Full" elsewhere</li>
     *   <li>Only lines that are collection summaries can count</li>
     * </ul>
     *
     * @param lines the log lines
     * @return the number of full collections
     */
    public int countFullGcs(List<String> lines) {
        throw new UnsupportedOperationException("TODO: implement countFullGcs");
    }

    /**
     * Return {@code true} when the log suggests a memory leak: there are <b>at least three</b>
     * collection lines and the heap used <em>after</em> each collection is strictly larger
     * than after the one before it. Compare sizes in the same unit.
     *
     * <p>Examples:
     * <pre>
     * after-values 10M, 20M, 35M, 50M → true
     * after-values 10M, 20M, 20M      → false  (not strictly rising)
     * after-values 10M, 30M, 12M, 40M → false  (saw-tooth)
     * after-values 10M, 20M           → false  (fewer than three events)
     * after-values 900K, 1M, 2M       → true   (900K &lt; 1024K &lt; 2048K — units matter)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Collect the "after" values first, normalised to kilobytes, then compare neighbours</li>
     * </ul>
     *
     * @param lines the log lines
     * @return whether the live set keeps growing
     */
    public boolean heapAfterGcGrowing(List<String> lines) {
        throw new UnsupportedOperationException("TODO: implement heapAfterGcGrowing");
    }
}
