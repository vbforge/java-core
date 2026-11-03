package com.vbforge.virtual_threads_intro;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 🎯 Tasks for structured concurrency. Read {@link StructuredConcurrencyDemo} first
 * (its nested {@link StructuredConcurrencyDemo.TaskScope} is yours to use if you like).
 *
 * <p>The structured-concurrency rule applies to every task below: <b>when the method returns or
 * throws, no subtask may be left running on its own</b> — losing subtasks must be cancelled
 * (interrupted). Interruption is cooperative, so the tests allow a few seconds for it to arrive.
 *
 * <p>Each method currently throws {@link UnsupportedOperationException}.
 * Implement it, then remove {@code @Disabled} from the matching nested class in
 * {@code StructuredConcurrencyTasksTest}.
 */
public class StructuredConcurrencyTasks {

    /**
     * Runs {@code left} and {@code right} IN PARALLEL and returns {@code leftResult + "+" + rightResult}.
     * If either one fails, the other is cancelled (interrupted) and the failure is thrown as an
     * {@link ExecutionException}. A failure must be noticed immediately, even when the other
     * branch is still blocked.
     *
     * <p>Examples:
     * <pre>
     * combine(() -&gt; "A", () -&gt; "B")                  → "A+B"
     * combine(blockForever, () -&gt; throw boom)         → throws ExecutionException (cause: boom),
     *                                                      and blockForever was interrupted
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Waiting for the left result first would hide a failure on the right</li>
     *   <li>A scope that cancels siblings on failure is already in the Demo file</li>
     * </ul>
     *
     * @param left  first branch
     * @param right second branch
     * @return {@code left + "+" + right}
     */
    public String combine(Callable<String> left, Callable<String> right)
            throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("TODO: implement combine");
    }

    /**
     * Runs all tasks in parallel and returns the result of the FIRST one that completes
     * successfully. Failed tasks are ignored. As soon as one succeeds, the others are cancelled (interrupted).
     *
     * <p>Examples:
     * <pre>
     * firstSuccessful([fail, () -&gt; "B", fail])  → "B"
     * firstSuccessful([() -&gt; "only"])           → "only"
     * firstSuccessful([fail, fail])             → throws ExecutionException
     * firstSuccessful([])                       → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ExecutorService} has more methods than {@code submit}</li>
     *   <li>Do not write the "who finished first" loop yourself</li>
     * </ul>
     *
     * @param tasks the alternatives (e.g. mirrors of the same service)
     * @return the result of the first successful task
     */
    public String firstSuccessful(List<Callable<String>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("TODO: implement firstSuccessful");
    }

    /**
     * Runs {@code task} on a virtual thread and waits at most {@code timeout} for its result.
     * If it is not done in time, the task is cancelled (interrupted) and a {@link TimeoutException}
     * is thrown. If the task itself throws, that failure is thrown as an {@link ExecutionException}.
     *
     * <p>Examples:
     * <pre>
     * callWithTimeout(() -&gt; 42, 1 second)              → 42
     * callWithTimeout(sleeps 60 s, 1 second)           → throws TimeoutException; the sleep was interrupted
     * callWithTimeout(() -&gt; throw boom, 1 second)      → throws ExecutionException (cause: boom)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Future.get} has a variant with a time limit</li>
     *   <li>A task that has timed out must not keep running — cancel it, and let it be interrupted</li>
     * </ul>
     *
     * @param task    the work to run
     * @param timeout the maximum time to wait
     * @param <T>     the result type
     * @return the task's result
     */
    public <T> T callWithTimeout(Callable<T> task, Duration timeout)
            throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("TODO: implement callWithTimeout");
    }
}
