package com.vbforge.jvm_diagnostic_tools;

import java.util.Map;

/**
 * Tasks for {@link LeakHuntingDemo}: the arithmetic of a leak investigation.
 * Replace every {@code UnsupportedOperationException} with a working implementation.
 */
public class LeakHuntingTasks {

    /**
     * Decides whether a series of "heap used after full GC" readings looks like a leak.
     * It does when there are at least 4 samples, no sample is smaller than the one before it,
     * and the last sample is strictly greater than the first.
     *
     * <p>Examples:
     * <pre>
     * [100, 150, 150, 200]   → true    (staircase)
     * [100, 100, 100, 100]   → false   (flat: healthy)
     * [100, 200, 150, 300]   → false   (a dip: the GC did free memory)
     * [100, 200, 300]        → false   (too few samples to say)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One pass comparing neighbours is enough.</li>
     *   <li>Null is treated like an empty array.</li>
     * </ul>
     *
     * @param usedAfterGc readings in time order, in bytes
     * @return true when the pattern matches a leak
     */
    public boolean isLeakSuspect(long[] usedAfterGc) {
        throw new UnsupportedOperationException("TODO: implement isLeakSuspect");
    }

    /**
     * Compares two class histograms (class name → bytes) and returns by how much each class GREW.
     * Classes that shrank, stayed equal, or disappeared are left out. A class missing from
     * {@code before} counts as 0 bytes before.
     *
     * <p>Examples:
     * <pre>
     * before {A=100, B=200}, after {A=150, B=200, C=40}  → {A=50, C=40}
     * before {A=100},        after {A=60}                → {}
     * before {},             after {}                    → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Iterate over {@code after}; look up the old value with a default.</li>
     *   <li>Return a new map; the arguments must not be modified.</li>
     * </ul>
     *
     * @param before histogram taken first
     * @param after histogram taken later
     * @return class name → positive growth in bytes
     */
    public Map<String, Long> growthByClass(Map<String, Long> before, Map<String, Long> after) {
        throw new UnsupportedOperationException("TODO: implement growthByClass");
    }

    /**
     * Names the leak suspect: the class that grew the most between two histograms.
     * When two classes grew by the same amount, the one that comes first alphabetically wins.
     *
     * <p>Examples:
     * <pre>
     * before {A=100, B=200}, after {A=150, B=900}   → "B"
     * before {A=100, B=100}, after {A=150, B=150}   → "A"    (tie)
     * before {A=100},        after {A=100}          → null   (nothing grew)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You can build on {@link #growthByClass(Map, Map)}.</li>
     *   <li>A {@code TreeMap} iterates in alphabetical order.</li>
     * </ul>
     *
     * @param before histogram taken first
     * @param after histogram taken later
     * @return the class name, or null when no class grew
     */
    public String biggestGrower(Map<String, Long> before, Map<String, Long> after) {
        throw new UnsupportedOperationException("TODO: implement biggestGrower");
    }

    /**
     * Extrapolates how many bytes per hour a leak consumes.
     *
     * <p>Examples:
     * <pre>
     * (1_000_000, 4_000_000, 1_800_000)  → 6_000_000.0   (3 MB in half an hour)
     * (5_000, 5_000, 3_600_000)          → 0.0
     * (1_000, 500, 3_600_000)            → -500.0        (shrinking is allowed)
     * (1_000, 2_000, 0)                  → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One hour is 3,600,000 milliseconds.</li>
     *   <li>Divide as {@code double}.</li>
     * </ul>
     *
     * @param firstBytes used-after-GC at the start
     * @param lastBytes used-after-GC at the end
     * @param elapsedMillis time between the two readings
     * @return bytes per hour
     * @throws IllegalArgumentException if {@code elapsedMillis} is zero or negative
     */
    public double bytesPerHour(long firstBytes, long lastBytes, long elapsedMillis) {
        throw new UnsupportedOperationException("TODO: implement bytesPerHour");
    }

    /**
     * Estimates how many hours remain until the heap is full at the current leak rate.
     *
     * <p>Examples:
     * <pre>
     * (400_000_000, 1_000_000_000, 100_000_000.0)  → 6.0
     * (1_000_000_000, 1_000_000_000, 5.0)          → 0.0     (already full)
     * (400_000_000, 1_000_000_000, 0.0)            → Infinity
     * (400_000_000, 1_000_000_000, -3.0)           → Infinity
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A leak that is not growing never fills the heap.</li>
     *   <li>{@code Double.POSITIVE_INFINITY} is a legitimate answer.</li>
     * </ul>
     *
     * @param usedBytes heap used after GC now
     * @param maxBytes heap ceiling (-Xmx)
     * @param bytesPerHour growth rate, as returned by {@link #bytesPerHour(long, long, long)}
     * @return hours until usedBytes reaches maxBytes
     */
    public double hoursUntilFull(long usedBytes, long maxBytes, double bytesPerHour) {
        throw new UnsupportedOperationException("TODO: implement hoursUntilFull");
    }
}
