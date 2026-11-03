package com.vbforge.java_memory_model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tasks for visibility and atomicity.
 * Implement each method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code VisibilityAndAtomicityTasksTest}.
 */
public class VisibilityAndAtomicityTasks {

    /**
     * Start {@code threads} threads; each one adds 1 to ONE shared counter
     * {@code incrementsPerThread} times. Wait for all of them and return the total.
     * Protect the counter with intrinsic locking or an explicit lock — not with
     * {@code volatile} alone.
     *
     * <p>Examples:</p>
     * <pre>
     * countWithLock(1, 1000)  → 1000
     * countWithLock(4, 5000)  → 20000
     * countWithLock(8, 0)     → 0
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>{@code synchronized} or {@code ReentrantLock} both work</li>
     *   <li>Wait for every thread before reading the total</li>
     * </ul>
     *
     * @param threads             number of worker threads (≥ 1)
     * @param incrementsPerThread increments done by each worker (≥ 0)
     * @return the final value of the shared counter
     */
    public long countWithLock(int threads, int incrementsPerThread) {
        throw new UnsupportedOperationException("TODO: implement countWithLock");
    }

    /**
     * Same contract as {@link #countWithLock(int, int)}, but the shared counter must be
     * lock-free — built on the {@code java.util.concurrent.atomic} package.
     *
     * <p>Examples:</p>
     * <pre>
     * countWithAtomic(1, 1000)  → 1000
     * countWithAtomic(4, 5000)  → 20000
     * countWithAtomic(8, 0)     → 0
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>Look at {@code AtomicLong}, or {@code LongAdder} if you want to compare</li>
     * </ul>
     *
     * @param threads             number of worker threads (≥ 1)
     * @param incrementsPerThread increments done by each worker (≥ 0)
     * @return the final value of the shared counter
     */
    public long countWithAtomic(int threads, int incrementsPerThread) {
        throw new UnsupportedOperationException("TODO: implement countWithAtomic");
    }

    /**
     * Start a <b>daemon</b> worker thread that loops forever (doing trivial work such as
     * incrementing a local counter) until a shared stop flag is raised. The calling thread
     * sleeps {@code runMillis}, raises the flag, then joins the worker for at most 2000 ms.
     * The worker must be guaranteed to notice the flag.
     *
     * <p>Examples:</p>
     * <pre>
     * workerStopsOnFlag(50)  → true   (worker terminated after the flag was raised)
     * workerStopsOnFlag(0)   → true
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>A plain {@code boolean} field is a data race — which keyword or class fixes it?</li>
     *   <li>{@code Thread.join(long)} + {@code isAlive()} tell you whether the worker finished</li>
     * </ul>
     *
     * @param runMillis how long the caller waits before raising the flag (≥ 0)
     * @return {@code true} if the worker thread is no longer alive after the join
     */
    public boolean workerStopsOnFlag(long runMillis) {
        throw new UnsupportedOperationException("TODO: implement workerStopsOnFlag");
    }

    /**
     * Atomically increment {@code counter} <b>only if</b> its current value is strictly less
     * than {@code limit}. Safe when many threads call it concurrently on the same counter:
     * the counter must never end above {@code limit}, and exactly
     * {@code limit - startValue} calls may return {@code true}.
     *
     * <p>Examples:</p>
     * <pre>
     * counter = 0, limit = 2:
     *   incrementIfBelow(counter, 2) → true   (counter is now 1)
     *   incrementIfBelow(counter, 2) → true   (counter is now 2)
     *   incrementIfBelow(counter, 2) → false  (counter stays 2)
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>{@code if (c.get() &lt; limit) c.incrementAndGet()} is a check-then-act race</li>
     *   <li>Read, decide, then try to swap — and decide what to do when the swap fails</li>
     * </ul>
     *
     * @param counter shared counter
     * @param limit   exclusive upper bound
     * @return {@code true} if this call performed the increment
     */
    public boolean incrementIfBelow(AtomicInteger counter, int limit) {
        throw new UnsupportedOperationException("TODO: implement incrementIfBelow");
    }
}
