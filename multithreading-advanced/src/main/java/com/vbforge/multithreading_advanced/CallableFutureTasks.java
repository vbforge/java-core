package com.vbforge.multithreading_advanced;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 🎯 Tasks for {@link CallableFutureDemo}.
 *
 * <p>Implement each method, remove the {@code UnsupportedOperationException},
 * then remove {@code @Disabled} from the matching nested class in
 * {@code CallableFutureTasksTest} and run it.
 */
public class CallableFutureTasks {

    /**
     * Runs all jobs on the given executor, waits for all of them, and returns
     * their results in the <b>same order as the jobs</b> (not in completion order).
     *
     * <p>Examples:
     * <pre>
     *   jobs [ "A" after 80 ms, "B" at once, "C" after 40 ms ]  → ["A", "B", "C"]
     *   jobs []                                                  → []
     *   jobs [ "A", (throws IllegalStateException) ]             → throws ExecutionException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ExecutorService} has a method that takes the whole collection and blocks.</li>
     *   <li>Each returned future is already done — but may have failed.</li>
     * </ul>
     *
     * @param executor the executor to run the jobs on (never null; the caller shuts it down)
     * @param jobs     the jobs to run
     * @return the job results, in job order
     * @throws InterruptedException if interrupted while waiting
     * @throws ExecutionException   if any job fails
     */
    public List<String> runAll(ExecutorService executor, List<Callable<String>> jobs)
            throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("TODO: implement runAll");
    }

    /**
     * Returns the result of whichever job completes <b>successfully</b> first.
     * Jobs that fail are ignored as long as at least one job succeeds. The
     * remaining jobs should not be left running.
     *
     * <p>Examples:
     * <pre>
     *   jobs [ (throws IllegalStateException), "B" after 50 ms ]  → "B"
     *   jobs [ "A" ]                                               → "A"
     *   jobs [ (throws), (throws) ]                                → throws ExecutionException
     *   jobs []                                                    → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a sibling of {@code invokeAll} that already does exactly this.</li>
     *   <li>You do not have to write the empty-list check yourself.</li>
     * </ul>
     *
     * @param executor the executor to run the jobs on (never null; the caller shuts it down)
     * @param jobs     the jobs to race
     * @return the first successful result
     * @throws InterruptedException if interrupted while waiting
     * @throws ExecutionException   if every job fails
     */
    public String firstSuccessful(ExecutorService executor, List<Callable<String>> jobs)
            throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("TODO: implement firstSuccessful");
    }

    /**
     * Waits for a future for at most {@code timeoutMillis}.
     *
     * <p>If the result arrives in time, return it wrapped in an {@code Optional}.
     * If the time runs out, <b>cancel</b> the task (interrupting it if it is running)
     * and return an empty {@code Optional}. If the task itself failed, let the
     * {@code ExecutionException} propagate.
     *
     * <p>Examples:
     * <pre>
     *   future of a task returning 42 at once, timeout 1000   → Optional.of(42)
     *   future of a task that never ends, timeout 50          → Optional.empty()
     *                                                           and future.isCancelled() is true
     *   future of a task that throws, timeout 1000            → throws ExecutionException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Future.get} has an overload with a timeout; what does it throw when time runs out?</li>
     *   <li>A timeout only means you stopped waiting — it does not stop the task.</li>
     * </ul>
     *
     * @param future        the future to wait for (never null)
     * @param timeoutMillis the maximum wait, in milliseconds
     * @return the result, or empty if it did not arrive in time
     * @throws InterruptedException if interrupted while waiting
     * @throws ExecutionException   if the task failed
     */
    public Optional<Integer> getWithTimeout(Future<Integer> future, long timeoutMillis)
            throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("TODO: implement getWithTimeout");
    }

    /**
     * Describes how a <b>finished</b> future ended, as a short string.
     *
     * <p>Examples:
     * <pre>
     *   completed normally                                  → "SUCCESS"
     *   cancelled                                           → "CANCELLED"
     *   failed with new IllegalStateException("boom")       → "FAILED: boom"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Calling {@code get()} on a cancelled future throws an unchecked exception — ask the future first.</li>
     *   <li>The message you want belongs to the <em>cause</em> of the exception that {@code get()} throws.</li>
     * </ul>
     *
     * @param future a future for which {@code isDone()} is already true
     * @return {@code "SUCCESS"}, {@code "CANCELLED"} or {@code "FAILED: " + cause message}
     * @throws InterruptedException declared by {@code Future.get()}
     */
    public String outcome(Future<?> future) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement outcome");
    }

    /**
     * Sums an array in parallel: split it into {@code parts} slices of
     * (almost) equal size, sum each slice as its own task on the executor,
     * and add the partial sums together.
     *
     * <p>Examples:
     * <pre>
     *   parallelSum(executor, [1, 2, 3, 4, 5, 6, 7, 8, 9, 10], 2)  → 55
     *   parallelSum(executor, [], 3)                                → 0
     *   parallelSum(executor, [1, 2, 3], 10)                        → 6   (more parts than elements)
     *   parallelSum(executor, [1, 2, 3], 0)                         → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Each slice is a {@code Callable<Integer>}; the loop bounds are fiddly when the length is not divisible by {@code parts}.</li>
     *   <li>Collect all futures first, then call {@code get()} — otherwise the slices run one by one.</li>
     * </ul>
     *
     * @param executor the executor to run the slices on (never null; the caller shuts it down)
     * @param numbers  the numbers to add
     * @param parts    the number of slices; must be at least 1
     * @return the sum of all numbers
     * @throws InterruptedException if interrupted while waiting
     * @throws ExecutionException   if a slice task fails
     */
    public int parallelSum(ExecutorService executor, int[] numbers, int parts)
            throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("TODO: implement parallelSum");
    }
}
