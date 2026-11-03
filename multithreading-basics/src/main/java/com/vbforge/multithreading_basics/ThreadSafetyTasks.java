package com.vbforge.multithreading_basics;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Tasks for thread safety. Implement every method so that
 * {@code ThreadSafetyTasksTest} goes green (remove {@code @Disabled} per task).
 */
public class ThreadSafetyTasks {

    /**
     * Increments {@code counter} by one ONLY if its value is below {@code limit}.
     * Must never let the counter pass the limit, even when many threads call it at once.
     *
     * <pre>
     * Examples:
     *   counter = 3, limit = 5 → incrementIfBelow(counter, 5) → true,  counter = 4
     *   counter = 5, limit = 5 → incrementIfBelow(counter, 5) → false, counter = 5
     *   8 threads × 1000 calls with limit 1000 → exactly 1000 calls return true, counter = 1000
     * </pre>
     *
     * Hints:
     * - Read-then-write is a check-then-act; make the pair atomic
     * - AtomicInteger offers a conditional update that reports whether it succeeded
     *
     * @param counter shared counter
     * @param limit   exclusive upper bound
     * @return true if this call incremented the counter
     */
    public boolean incrementIfBelow(AtomicInteger counter, int limit) {
        throw new UnsupportedOperationException("TODO: implement incrementIfBelow");
    }

    /**
     * Creates a ThreadLocal whose value for each thread is produced by {@code factory}
     * the first time that thread asks for it.
     *
     * <pre>
     * Examples:
     *   tl = perThread(() -> new StringBuilder())
     *   tl.get() twice on thread A → the same StringBuilder
     *   tl.get() on thread B       → a different StringBuilder
     * </pre>
     *
     * Hints:
     * - ThreadLocal has a static factory method that takes a Supplier
     * - The factory must not run when get() is never called
     *
     * @param factory creates the per-thread value
     * @param <T>     value type
     * @return the thread-local holder
     */
    public <T> ThreadLocal<T> perThread(Supplier<T> factory) {
        throw new UnsupportedOperationException("TODO: implement perThread");
    }

    /**
     * Returns a read-only snapshot of {@code source}. Later changes to {@code source} must
     * NOT show up in the result, and the result itself must reject modification.
     *
     * <pre>
     * Examples:
     *   source = [1, 2, 3]; copy = unmodifiableCopy(source)
     *   source.add(4)   → copy is still [1, 2, 3]
     *   copy.add(9)     → throws UnsupportedOperationException
     *   unmodifiableCopy([]) → []
     * </pre>
     *
     * Hints:
     * - Two different steps are needed: copy, then wrap
     * - Wrapping alone is only a read-only VIEW of the original
     *
     * @param source the list to snapshot (not null)
     * @return an unmodifiable independent copy
     */
    public List<Integer> unmodifiableCopy(List<Integer> source) {
        throw new UnsupportedOperationException("TODO: implement unmodifiableCopy");
    }

    /**
     * Returns the single shared instance stored in {@code ref}, creating it with
     * {@code factory} if it does not exist yet. When many threads call this at the same
     * time on the same {@code ref}, the factory must run EXACTLY once and every caller must
     * get that same instance.
     *
     * <pre>
     * Examples:
     *   ref = empty; getOrCreate(ref, f) → value from f, ref now holds it
     *   ref = holds X; getOrCreate(ref, f) → X, f is not called
     *   16 threads call at once → f called once, all 16 results are the same object
     * </pre>
     *
     * Hints:
     * - "Is it there? If not, create it" must be one atomic step
     * - An atomic compare-and-set alone would let the factory run more than once
     *
     * @param ref     shared holder
     * @param factory creates the instance
     * @return the shared instance
     */
    public Object getOrCreate(AtomicReference<Object> ref, Supplier<Object> factory) {
        throw new UnsupportedOperationException("TODO: implement getOrCreate");
    }

    /**
     * Sums {@code numbers} using up to {@code threads} threads. Split the array into
     * contiguous chunks, let every thread sum its own chunk, then add the partial sums.
     *
     * <pre>
     * Examples:
     *   sumConcurrently([1, 2, 3, 4, 5, 6], 3) → 21
     *   sumConcurrently([1, 2, 3], 8)          → 6   (more threads than elements)
     *   sumConcurrently([], 4)                 → 0
     *   sumConcurrently([1], 0)                → throws IllegalArgumentException
     * </pre>
     *
     * Hints:
     * - Threads that never touch the same variable need no locks
     * - Each thread writes only its own slot of a results array; collect after waiting for all
     *
     * @param numbers values to add (not null)
     * @param threads number of worker threads (must be &gt;= 1)
     * @return the sum
     * @throws InterruptedException     if interrupted while waiting
     * @throws IllegalArgumentException if threads &lt; 1
     */
    public long sumConcurrently(int[] numbers, int threads) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement sumConcurrently");
    }
}
