package com.vbforge.virtual_threads_intro;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 🎯 Tasks for virtual-thread pitfalls. Read {@link VirtualThreadPitfallsDemo} first.
 *
 * <p>Each method currently throws {@link UnsupportedOperationException}.
 * Implement it, then remove {@code @Disabled} from the matching nested class in
 * {@code VirtualThreadPitfallsTasksTest}.
 */
public class VirtualThreadPitfallsTasks {

    /**
     * Runs {@code taskCount} tasks, each on its own virtual thread, but lets at most
     * {@code permits} of them be inside the "critical region" at once. Each task, while
     * inside the region, sleeps 5 ms. Return the HIGHEST number of tasks that were
     * inside the region at the same moment.
     *
     * <p>Examples:
     * <pre>
     * maxObservedConcurrency(100, 1)  → 1        (never more than one at a time)
     * maxObservedConcurrency(50, 3)   → 1..3     (never more than 3)
     * maxObservedConcurrency(0, 5)    → 0        (nothing ran)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The limit belongs to the region, not to the executor</li>
     *   <li>You need a counter that goes up on entry, down on exit, and remembers its maximum</li>
     *   <li>A permit must be given back even if the task fails</li>
     * </ul>
     *
     * @param taskCount number of tasks (&gt;= 0)
     * @param permits   maximum concurrent tasks inside the region (&gt;= 1)
     * @return the highest concurrency observed inside the region
     */
    public int maxObservedConcurrency(int taskCount, int permits) {
        throw new UnsupportedOperationException("TODO: implement maxObservedConcurrency");
    }

    /**
     * Starts {@code threads} virtual threads. Every thread owns its OWN counter stored in a
     * {@link ThreadLocal}, increments it {@code incrementsPerThread} times (no locks needed),
     * and reports its final value. Return the reported values in thread-index order.
     *
     * <p>Examples:
     * <pre>
     * perThreadCounts(3, 1000)  → [1000, 1000, 1000]
     * perThreadCounts(2, 0)     → [0, 0]
     * perThreadCounts(0, 10)    → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ThreadLocal.withInitial} gives each thread a fresh starting value</li>
     *   <li>The thread-local is a mutable holder — an {@code int} itself cannot be updated in place</li>
     *   <li>Each thread must hand its value back to the caller</li>
     * </ul>
     *
     * @param threads             number of virtual threads (&gt;= 0)
     * @param incrementsPerThread increments done by each thread (&gt;= 0)
     * @return one final count per thread, in thread-index order
     */
    public List<Integer> perThreadCounts(int threads, int incrementsPerThread)
            throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("TODO: implement perThreadCounts");
    }

    /**
     * Simulates {@code threads} concurrent withdrawals of {@code amount} each from one account
     * that starts with {@code initialBalance}. Every withdrawal, on its own virtual thread:
     * <ol>
     *   <li>checks that the balance is at least {@code amount}</li>
     *   <li>calls {@code Thread.sleep(1)} (a stand-in for a blocking fraud-check service)</li>
     *   <li>only then subtracts {@code amount}</li>
     * </ol>
     * A withdrawal that finds too little money does nothing. The balance must never go
     * negative. Return the final balance.
     *
     * <p>Examples:
     * <pre>
     * withdrawConcurrently(1000, 100, 20)  → 0     (only 50 of 100 withdrawals succeed)
     * withdrawConcurrently(1000, 10, 20)   → 800   (all 10 succeed)
     * withdrawConcurrently(10, 5, 20)      → 10    (not enough money for even one)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Check-then-act must be one atomic step, including the sleep</li>
     *   <li>Which lock type is friendly to a thread that blocks while holding it?</li>
     * </ul>
     *
     * @param initialBalance starting balance (&gt;= 0)
     * @param threads        number of concurrent withdrawals (&gt;= 0)
     * @param amount         amount of each withdrawal (&gt; 0)
     * @return the final balance
     */
    public int withdrawConcurrently(int initialBalance, int threads, int amount) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement withdrawConcurrently");
    }
}
