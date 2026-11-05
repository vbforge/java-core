package com.vbforge.jit_and_warmup;

/**
 * Tasks for {@link MicrobenchmarkDemo}: the statistics you need to read benchmark results.
 * Read the demo first.
 */
public class MicrobenchmarkTasks {

    /**
     * Computes the <b>trimmed mean</b>: sort the samples, drop the {@code trim} smallest and the
     * {@code trim} largest, and average the rest. Benchmark samples often contain a few
     * wild values (a GC pause, a compile), and trimming removes their influence.
     * The input array must <b>not</b> be modified.
     *
     * <p>Examples:
     * <pre>
     * trimmedMean([100, 1, 2, 3, 900], 1)  → 35.0    (mean of [2, 3, 100])
     * trimmedMean([1, 2, 3], 0)            → 2.0
     * trimmedMean([4, 8], 0)               → 6.0
     * trimmedMean([1, 2, 3], 2)            → throws IllegalArgumentException (nothing left)
     * trimmedMean([1, 2, 3], -1)           → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Work on a copy: {@code Arrays.copyOf}, then {@code Arrays.sort}</li>
     *   <li>After sorting, the values to keep are one contiguous range</li>
     * </ul>
     *
     * @param samples measured values
     * @param trim    how many values to drop from <i>each</i> end, must be ≥ 0
     * @return the mean of the remaining values
     * @throws IllegalArgumentException if samples is null, trim is negative, or {@code 2 * trim >= samples.length}
     */
    public double trimmedMean(long[] samples, int trim) {
        throw new UnsupportedOperationException("TODO: implement trimmedMean");
    }

    /**
     * Converts an operation count and an elapsed time into throughput in operations per second.
     *
     * <p>Examples:
     * <pre>
     * opsPerSecond(1_000_000, 500_000_000)   → 2_000_000.0   (1M ops in 0.5 s)
     * opsPerSecond(1, 1_000_000_000)         → 1.0
     * opsPerSecond(0, 1_000)                 → 0.0
     * opsPerSecond(10, 0)                    → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There are 1,000,000,000 nanoseconds in a second</li>
     *   <li>Use floating-point division — integer division would truncate</li>
     * </ul>
     *
     * @param operations   number of operations performed, must be ≥ 0
     * @param elapsedNanos elapsed time in nanoseconds, must be &gt; 0
     * @return operations per second
     * @throws IllegalArgumentException if operations is negative or elapsedNanos is not positive
     */
    public double opsPerSecond(long operations, long elapsedNanos) {
        throw new UnsupportedOperationException("TODO: implement opsPerSecond");
    }

    /**
     * Computes the coefficient of variation: the <b>population</b> standard deviation (divide by
     * {@code n}, not {@code n - 1}) divided by the mean. A small value (say below 0.05) means the
     * measurements are stable; a large one means the run was noisy or still warming up.
     *
     * <p>Examples:
     * <pre>
     * coefficientOfVariation([10, 10, 10])                → 0.0
     * coefficientOfVariation([2, 4, 4, 4, 5, 5, 7, 9])    → 0.4    (mean 5, std-dev 2)
     * coefficientOfVariation([5])                         → 0.0
     * coefficientOfVariation([])                          → throws IllegalArgumentException
     * coefficientOfVariation([0, 0])                      → throws IllegalArgumentException (mean is 0)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Mean first, then the average squared deviation, then {@code Math.sqrt}</li>
     * </ul>
     *
     * @param samples measured values
     * @return standard deviation divided by mean
     * @throws IllegalArgumentException if samples is null or empty, or the mean is zero
     */
    public double coefficientOfVariation(double[] samples) {
        throw new UnsupportedOperationException("TODO: implement coefficientOfVariation");
    }
}
