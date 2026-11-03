package com.vbforge.multithreading_advanced;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

/**
 * 🎯 Tasks for {@link SynchronizersDemo}.
 *
 * <p>Implement each method, remove the {@code UnsupportedOperationException},
 * then remove {@code @Disabled} from the matching nested class in
 * {@code SynchronizersTasksTest} and run it.
 */
public class SynchronizersTasks {

    /**
     * Starts every job on its <b>own new thread</b> (so they run concurrently)
     * and returns only after every job has finished. A job that throws must not
     * make this method wait forever.
     *
     * <p>Examples:
     * <pre>
     *   jobs [ increment counter, increment counter, increment counter ]  → returns after counter == 3
     *   jobs []                                                           → returns at once
     *   jobs [ job that throws, job that sleeps 50 ms ]                   → still returns, after both ended
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A latch initialised with the number of jobs is the coordination tool.</li>
     *   <li>Where does the "this job is over" signal belong so that it also runs when the job throws?</li>
     * </ul>
     *
     * @param jobs the jobs to run (never null)
     * @throws InterruptedException if interrupted while waiting
     */
    public void runAndWaitForAll(List<Runnable> jobs) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement runAndWaitForAll");
    }

    /**
     * Like {@link #runAndWaitForAll}, but gives up waiting after
     * {@code timeoutMillis}: starts every job on its own new thread and reports
     * whether all of them finished in time. Jobs still running after the
     * timeout are left alone (do not interrupt them).
     *
     * <p>Examples:
     * <pre>
     *   quick jobs, timeout 1000                       → true
     *   one job that blocks for good, timeout 100      → false
     *   jobs [], timeout 100                           → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The latch's {@code await} overload with a timeout returns a {@code boolean}.</li>
     *   <li>Consider making the threads daemon threads so a stuck job cannot keep the JVM alive.</li>
     * </ul>
     *
     * @param jobs          the jobs to run (never null)
     * @param timeoutMillis maximum time to wait, in milliseconds
     * @return {@code true} if every job finished within the timeout
     * @throws InterruptedException if interrupted while waiting
     */
    public boolean runWithTimeout(List<Runnable> jobs, long timeoutMillis) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement runWithTimeout");
    }

    /**
     * Runs {@code action} while holding one permit of the semaphore and returns
     * its result. Blocks until a permit is available. The permit must be given
     * back whether the action succeeds or throws; an exception from the action
     * must propagate unchanged.
     *
     * <p>Examples:
     * <pre>
     *   Semaphore(2), action returns "ok"             → "ok"; availablePermits() is 2 again afterwards
     *                                                   (and was 1 while the action ran)
     *   Semaphore(2), action throws IllegalStateException → the same exception propagates;
     *                                                   availablePermits() is 2 again afterwards
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Acquire <em>before</em> the {@code try}, release in {@code finally} — why that order?</li>
     * </ul>
     *
     * @param semaphore the semaphore guarding the resource (never null)
     * @param action    the work to run under the permit (never null)
     * @param <T>       the result type
     * @return whatever the action returned
     * @throws Exception if the action throws, or if interrupted while waiting for a permit
     */
    public <T> T callWithPermit(Semaphore semaphore, Callable<T> action) throws Exception {
        throw new UnsupportedOperationException("TODO: implement callWithPermit");
    }

    /**
     * Non-blocking variant of {@link #callWithPermit}: if a permit is available
     * <em>right now</em>, run the action under it and return its result wrapped in
     * an {@code Optional}; if not, return an empty {@code Optional} without
     * running the action. A permit that was never acquired must never be released.
     *
     * <p>Examples:
     * <pre>
     *   Semaphore(1), action returns "ok"   → Optional.of("ok"); availablePermits() is 1 afterwards
     *   Semaphore(0), action returns "ok"   → Optional.empty();  availablePermits() is still 0
     *                                         (the action did not run)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a non-blocking way to ask a semaphore for a permit.</li>
     *   <li>A stray {@code release()} increases the permit count beyond what the semaphore started with.</li>
     * </ul>
     *
     * @param semaphore the semaphore guarding the resource (never null)
     * @param action    the work to run under the permit; assumed to return a non-null value
     * @param <T>       the result type
     * @return the action's result, or empty if no permit was available
     * @throws Exception if the action throws
     */
    public <T> Optional<T> tryCallWithPermit(Semaphore semaphore, Callable<T> action) throws Exception {
        throw new UnsupportedOperationException("TODO: implement tryCallWithPermit");
    }
}
