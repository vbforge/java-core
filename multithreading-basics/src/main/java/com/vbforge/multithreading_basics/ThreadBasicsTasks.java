package com.vbforge.multithreading_basics;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Tasks for Thread and Runnable basics. Implement every method so that
 * {@code ThreadBasicsTasksTest} goes green (remove {@code @Disabled} per task).
 */
public class ThreadBasicsTasks {

    /**
     * Creates a thread with the given name that runs {@code task}, starts it,
     * and returns it WITHOUT waiting for it to finish.
     *
     * <pre>
     * Examples:
     *   startNamedThread("worker-7", task).getName()  → "worker-7"
     *   the task runs on that thread, not on the caller
     * </pre>
     *
     * Hints:
     * - There is a Thread constructor that takes a Runnable and a name
     * - Starting and running are different operations
     *
     * @param name the thread name
     * @param task the work to run
     * @return the started thread
     */
    public Thread startNamedThread(String name, Runnable task) {
        throw new UnsupportedOperationException("TODO: implement startNamedThread");
    }

    /**
     * Runs {@code supplier} on a NEW thread, waits for it to finish and returns
     * what it produced.
     *
     * <pre>
     * Examples:
     *   runOnNewThread(() -> "hi")                              → "hi"
     *   runOnNewThread(() -> Thread.currentThread().getName())  → a name different from the caller's
     * </pre>
     *
     * Hints:
     * - A Runnable cannot return a value; capture a holder (array, AtomicReference) instead
     * - Waiting for completion also guarantees you see the written value
     *
     * @param supplier the computation to run on another thread
     * @param <T>      result type
     * @return the supplier's result
     * @throws InterruptedException if the caller is interrupted while waiting
     */
    public <T> T runOnNewThread(Supplier<T> supplier) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement runOnNewThread");
    }

    /**
     * Squares every number using one thread PER element and returns the squares
     * in the original order.
     *
     * <pre>
     * Examples:
     *   parallelSquares([1, 2, 3, 4]) → [1, 4, 9, 16]
     *   parallelSquares([])           → []
     *   parallelSquares([-3])         → [9]
     * </pre>
     *
     * Hints:
     * - Each thread should write only to its own slot, so no locking is needed
     * - Start them all first, then wait for all of them
     *
     * @param numbers input numbers (not null)
     * @return squares, same order as the input
     * @throws InterruptedException if interrupted while waiting
     */
    public List<Integer> parallelSquares(List<Integer> numbers) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement parallelSquares");
    }

    /**
     * Starts and returns a thread that increments {@code counter} roughly once per
     * millisecond until it is interrupted, then ends normally.
     *
     * <pre>
     * Examples:
     *   t = startCancellableCounter(counter);
     *   ...later: t.interrupt(); t.join();   → t is no longer alive, counter.get() &gt; 0
     * </pre>
     *
     * Hints:
     * - Sleeping is a blocking call that reports interruption with a checked exception
     * - When interruption is reported, the loop must end (do not swallow it and carry on)
     *
     * @param counter the shared counter
     * @return the started thread
     */
    public Thread startCancellableCounter(AtomicInteger counter) {
        throw new UnsupportedOperationException("TODO: implement startCancellableCounter");
    }

    /**
     * Runs {@code failing} on a new thread, waits for that thread to die and returns
     * the exception that escaped from it (or {@code null} if nothing escaped).
     *
     * <pre>
     * Examples:
     *   captureUncaught(() -> { throw new IllegalStateException("x"); }) → the IllegalStateException
     *   captureUncaught(() -> { })                                      → null
     * </pre>
     *
     * Hints:
     * - A Thread can be given a handler for exceptions that escape run()
     * - You cannot catch it with try/catch around start()
     *
     * @param failing the task that may throw
     * @return the escaped throwable or null
     * @throws InterruptedException if interrupted while waiting
     */
    public Throwable captureUncaught(Runnable failing) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement captureUncaught");
    }
}
