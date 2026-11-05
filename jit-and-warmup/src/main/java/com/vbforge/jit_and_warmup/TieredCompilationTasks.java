package com.vbforge.jit_and_warmup;

/**
 * Tasks for {@link TieredCompilationDemo}: tiers and warm-up analysis.
 * Read the demo first, then implement each method.
 */
public class TieredCompilationTasks {

    /**
     * Returns which execution engine a method would be running in after the given
     * number of invocations, using a <b>simplified model</b> of HotSpot's tiers:
     * fewer than 200 invocations → interpreter; 200 up to (but not including) 5000 → C1;
     * 5000 or more → C2. (200 and 5000 are the default {@code Tier3InvocationThreshold}
     * and {@code Tier4InvocationThreshold}; the real VM also looks at loop counters
     * and compiler-queue load.)
     *
     * <p>Examples:
     * <pre>
     * tierForInvocations(0)       → "INTERPRETER"
     * tierForInvocations(199)     → "INTERPRETER"
     * tierForInvocations(200)     → "C1"
     * tierForInvocations(4999)    → "C1"
     * tierForInvocations(5000)    → "C2"
     * tierForInvocations(-1)      → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Check the boundaries carefully: which side of each threshold is inclusive?</li>
     *   <li>Return exactly the strings shown above</li>
     * </ul>
     *
     * @param invocations how many times the method has been called
     * @return "INTERPRETER", "C1" or "C2"
     * @throws IllegalArgumentException if invocations is negative
     */
    public String tierForInvocations(int invocations) {
        throw new UnsupportedOperationException("TODO: implement tierForInvocations");
    }

    /**
     * Computes the median of the samples. The median is what benchmark reports
     * prefer over the mean because one slow outlier (a GC pause, a compile) does not move it.
     * For an even number of samples it is the average of the two middle values.
     * The input array must <b>not</b> be modified.
     *
     * <p>Examples:
     * <pre>
     * median([5, 1, 3])     → 3.0
     * median([4, 1, 3, 2])  → 2.5
     * median([7])           → 7.0
     * median([])            → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Work on a copy: see {@code Arrays.copyOf}</li>
     *   <li>Sorting makes "the middle" easy to find</li>
     *   <li>Beware of overflow when averaging two large longs</li>
     * </ul>
     *
     * @param samples measured values (for example nanoseconds per iteration)
     * @return the median
     * @throws IllegalArgumentException if samples is null or empty
     */
    public double median(long[] samples) {
        throw new UnsupportedOperationException("TODO: implement median");
    }

    /**
     * Finds where warm-up ends in a series of per-iteration timings.
     * The reference (steady-state) value is the <b>last</b> sample. Warm-up ends at the
     * smallest index {@code i} such that every sample from {@code i} to the end is at most
     * {@code reference * (1 + tolerance)}. A late spike therefore pushes the answer to
     * just after the spike.
     *
     * <p>Examples:
     * <pre>
     * findWarmupIndex([900, 500, 300, 110, 100, 105, 100], 0.2)  → 3   (limit 120.0)
     * findWarmupIndex([100, 100, 400, 100, 100], 0.1)            → 3   (the spike at index 2 resets it)
     * findWarmupIndex([100, 100, 100], 0.1)                      → 0
     * findWarmupIndex([300, 200, 100], 0.0)                      → 2
     * findWarmupIndex([], 0.1)                                   → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Compute the limit once</li>
     *   <li>You do not have to scan from the front — which direction is cheaper?</li>
     * </ul>
     *
     * @param iterationNanos time of each iteration, in order
     * @param tolerance      allowed relative excess over the last sample (0.2 = 20 %), must be ≥ 0
     * @return index of the first steady-state iteration
     * @throws IllegalArgumentException if the array is null or empty, or tolerance is negative
     */
    public int findWarmupIndex(long[] iterationNanos, double tolerance) {
        throw new UnsupportedOperationException("TODO: implement findWarmupIndex");
    }
}
