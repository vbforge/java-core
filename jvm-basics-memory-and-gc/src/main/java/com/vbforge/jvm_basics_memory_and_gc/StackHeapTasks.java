package com.vbforge.jvm_basics_memory_and_gc;

/**
 * Tasks for {@link StackHeapDemo}: implement each method so that
 * {@code StackHeapTasksTest} passes.
 */
public class StackHeapTasks {

    /**
     * Swap the first two elements of the array <b>in place</b> and return the very
     * same array instance. Arrays with fewer than two elements are returned unchanged.
     *
     * <p>Examples:
     * <pre>
     * swapFirstTwoInPlace([1, 2, 3]) → [2, 1, 3]   (same instance returned)
     * swapFirstTwoInPlace([5])       → [5]
     * swapFirstTwoInPlace([])        → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The callee receives a copy of the reference — what does a write through it affect?</li>
     *   <li>Do not allocate a new array</li>
     * </ul>
     *
     * @param array the array to modify, never {@code null}
     * @return the same array instance, after the swap
     */
    public int[] swapFirstTwoInPlace(int[] array) {
        throw new UnsupportedOperationException("TODO: implement swapFirstTwoInPlace");
    }

    /**
     * Return a <b>new</b> array containing all elements of {@code source} followed by
     * {@code value}. The {@code source} array must not be modified.
     *
     * <p>Examples:
     * <pre>
     * appendCopy([1, 2], 3) → [1, 2, 3]   (source still [1, 2])
     * appendCopy([], 7)     → [7]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Arrays cannot grow — look at {@code java.util.Arrays} for copying with a larger length</li>
     * </ul>
     *
     * @param source the original array, never {@code null}
     * @param value  the element to add at the end
     * @return a new array one element longer than {@code source}
     */
    public int[] appendCopy(int[] source, int value) {
        throw new UnsupportedOperationException("TODO: implement appendCopy");
    }

    /**
     * Return the sum {@code 1 + 2 + ... + n} as a {@code long} without using recursion,
     * so that very large {@code n} does not overflow the thread stack.
     *
     * <p>Examples:
     * <pre>
     * sumIteratively(0)         → 0
     * sumIteratively(4)         → 10
     * sumIteratively(5_000_000) → 12_500_002_500_000
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Watch the accumulator type — {@code int} overflows for large {@code n}</li>
     * </ul>
     *
     * @param n a non-negative upper bound
     * @return the sum of all integers from 1 to n inclusive
     */
    public long sumIteratively(int n) {
        throw new UnsupportedOperationException("TODO: implement sumIteratively");
    }

    /**
     * Run {@code action} and report whether it died with a {@link StackOverflowError}.
     * Return {@code true} if a {@code StackOverflowError} was thrown, {@code false} if
     * the action completed normally. Any other exception must propagate to the caller.
     *
     * <p>Examples:
     * <pre>
     * overflowsStack(() -> { })                          → false
     * overflowsStack(() -> infiniteRecursion())          → true
     * overflowsStack(() -> { throw new IllegalStateException(); })
     *                                                    → IllegalStateException propagates
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code StackOverflowError} is an {@link Error}, not an {@link Exception}</li>
     *   <li>Catch only the specific type you care about</li>
     * </ul>
     *
     * @param action the code to run, never {@code null}
     * @return {@code true} if the action overflowed the stack
     */
    public boolean overflowsStack(Runnable action) {
        throw new UnsupportedOperationException("TODO: implement overflowsStack");
    }

    /**
     * Compute how many more bytes the heap can still use before it hits its ceiling:
     * {@code max - (total - free)}, i.e. maximum heap minus the bytes currently in use.
     *
     * <p>Examples:
     * <pre>
     * heapHeadroomBytes(1000, 400, 100) → 700   (used = 300)
     * heapHeadroomBytes(1000, 1000, 0)  → 0     (fully used and fully grown)
     * heapHeadroomBytes(1000, 200, 200) → 1000  (nothing used)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>"Free" is only the unused part of the <em>committed</em> heap</li>
     * </ul>
     *
     * @param maxBytes   the heap ceiling, as from {@code Runtime.maxMemory()}
     * @param totalBytes the committed heap, as from {@code Runtime.totalMemory()}
     * @param freeBytes  the unused committed heap, as from {@code Runtime.freeMemory()}
     * @return bytes still available before an OutOfMemoryError becomes possible
     */
    public long heapHeadroomBytes(long maxBytes, long totalBytes, long freeBytes) {
        throw new UnsupportedOperationException("TODO: implement heapHeadroomBytes");
    }
}
