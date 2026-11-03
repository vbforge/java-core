package com.vbforge.multithreading_basics;

/**
 * Tasks for {@code volatile}. Implement every method so that
 * {@code VolatileTasksTest} goes green (remove {@code @Disabled} per task).
 *
 * This class already declares the shared fields {@code stopRequested}, {@code payload}
 * and {@code ready}. How they are declared is part of the exercise.
 */
public class VolatileTasks {

    @SuppressWarnings("unused")
    private boolean stopRequested;
    @SuppressWarnings("unused")
    private int payload;
    @SuppressWarnings("unused")
    private boolean ready;

    /**
     * Asks a thread that is running {@link #runUntilStopped()} to stop.
     *
     * <pre>
     * Examples:
     *   thread A: runUntilStopped()   → keeps looping
     *   thread B: requestStop()       → A leaves its loop and returns
     * </pre>
     *
     * Hints:
     * - The reader thread must be guaranteed to see this write
     * - Check how the field {@code stopRequested} is declared, not only this method body
     */
    public void requestStop() {
        throw new UnsupportedOperationException("TODO: implement requestStop");
    }

    /**
     * Busy-loops, counting iterations, until {@link #requestStop()} has been called
     * from another thread. Returns the number of iterations performed (at least 1).
     *
     * <pre>
     * Examples:
     *   thread A: n = runUntilStopped(); thread B: requestStop()  → A returns n &gt;= 1
     * </pre>
     *
     * Hints:
     * - Do not use sleep, wait or locks inside the loop
     * - Count at least one iteration even if stop was already requested
     *
     * @return number of loop iterations (&gt;= 1)
     */
    public long runUntilStopped() {
        throw new UnsupportedOperationException("TODO: implement runUntilStopped");
    }

    /**
     * Publishes {@code value} to a thread blocked in {@link #awaitPayload()}.
     *
     * <pre>
     * Examples:
     *   thread A: awaitPayload()   → spins
     *   thread B: publish(42)      → A returns 42
     * </pre>
     *
     * Hints:
     * - Order matters: data first, signal last
     * - Only ONE of the two shared fields needs special treatment — decide which
     *
     * @param value the value to hand over
     */
    public void publish(int value) {
        throw new UnsupportedOperationException("TODO: implement publish");
    }

    /**
     * Spins (without sleeping or locking) until a value was published, then returns it.
     *
     * <pre>
     * Examples:
     *   awaitPayload() after publish(7) → 7
     * </pre>
     *
     * Hints:
     * - Mirror of publish(): read the signal first, the data afterwards
     * - Thread.yield() inside the loop is allowed
     *
     * @return the published value
     */
    public int awaitPayload() {
        throw new UnsupportedOperationException("TODO: implement awaitPayload");
    }

    /**
     * Counts to {@code threads * perThread} using {@code threads} threads that each
     * increment one shared counter {@code perThread} times, and returns the final value.
     * The result must be exact.
     *
     * <pre>
     * Examples:
     *   countConcurrently(4, 50_000) → 200_000
     *   countConcurrently(1, 0)      → 0
     * </pre>
     *
     * Hints:
     * - Making a counter volatile is not enough; look into java.util.concurrent.atomic
     * - Start all threads before you wait for any of them
     *
     * @param threads   number of threads (&gt;= 1)
     * @param perThread increments per thread (&gt;= 0)
     * @return the final counter value
     * @throws InterruptedException if interrupted while waiting
     */
    public int countConcurrently(int threads, int perThread) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement countConcurrently");
    }
}
