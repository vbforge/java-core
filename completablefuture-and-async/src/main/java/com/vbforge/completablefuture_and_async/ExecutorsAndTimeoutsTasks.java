package com.vbforge.completablefuture_and_async;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Practice: executors and timeouts (see {@link ExecutorsAndTimeoutsDemo}).
 * Everything here must work on Java 8 — no {@code orTimeout} / {@code completeOnTimeout}.
 */
public class ExecutorsAndTimeoutsTasks {

    /**
     * Create a {@link ThreadFactory} whose threads are <b>daemon</b> threads named
     * {@code prefix + "-" + n}, where {@code n} counts up from 1 for each thread the factory
     * creates. Every factory has its own counter. {@code newThread} returns the thread
     * <b>without starting it</b>.
     *
     * <p>Examples:
     * <pre>
     * namedDaemonThreadFactory("io").newThread(r)  →  thread named "io-1", daemon = true
     * (second call on the same factory)            →  thread named "io-2"
     * namedDaemonThreadFactory("io")               →  a new factory starts again at "io-1"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A counter that several threads may increment needs an atomic type</li>
     *   <li>Named threads make thread dumps readable; daemon threads do not keep the JVM alive</li>
     * </ul>
     *
     * @param prefix the thread-name prefix
     * @return the thread factory
     */
    public ThreadFactory namedDaemonThreadFactory(String prefix) {
        throw new UnsupportedOperationException("TODO: implement namedDaemonThreadFactory");
    }

    /**
     * Run a task on {@code executor} that reports the name of the thread it runs on.
     *
     * <p>Examples:
     * <pre>
     * executor = pool whose only thread is named "custom-worker"  →  future of "custom-worker"
     * executor = Runnable::run (runs on the caller)               →  future of the caller's thread name
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use the overload of the async factory that takes an {@code Executor}</li>
     * </ul>
     *
     * @param executor where to run the task
     * @return a future of the executing thread's name
     */
    public CompletableFuture<String> currentThreadName(Executor executor) {
        throw new UnsupportedOperationException("TODO: implement currentThreadName");
    }

    /**
     * Square every number as its <b>own task on {@code executor}</b> and complete with the sum
     * of the squares. An empty list gives {@code 0}.
     *
     * <p>Examples:
     * <pre>
     * sumOfSquares(executor, [1, 2, 3])  →  future of 14
     * sumOfSquares(executor, [-2, 3])    →  future of 13
     * sumOfSquares(executor, [])         →  future of 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One asynchronous task per element, all handed to the given executor</li>
     *   <li>Wait for all of them, then add the results up</li>
     * </ul>
     *
     * @param executor where to run the squaring tasks
     * @param numbers  the numbers to square
     * @return a future of the sum of squares
     */
    public CompletableFuture<Integer> sumOfSquares(Executor executor, List<Integer> numbers) {
        throw new UnsupportedOperationException("TODO: implement sumOfSquares");
    }

    /**
     * Return {@code source}'s outcome, but if {@code source} has not completed within
     * {@code millis} milliseconds, complete with {@code fallback} instead (this is what
     * Java 9's {@code completeOnTimeout} does). A failure of {@code source} that happens
     * <b>before</b> the timeout is <b>not</b> replaced by the fallback — it still fails.
     * Once the returned future is complete, a late completion of {@code source} does not change it.
     *
     * <p>Examples:
     * <pre>
     * source never completes, 50 ms, "fallback"    →  future of "fallback"
     * source completed with "value"                →  future of "value"
     * source fails with IllegalStateException      →  failed future (IllegalStateException)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use the scheduler to complete a second future after the delay</li>
     *   <li>Race the two futures with a stage that takes "either of two"</li>
     * </ul>
     *
     * @param source    the future to guard
     * @param scheduler used to schedule the timeout
     * @param millis    the timeout in milliseconds
     * @param fallback  value used when the timeout elapses
     * @param <T>       value type
     * @return a future of the value or the fallback
     */
    public <T> CompletableFuture<T> withFallbackOnTimeout(CompletableFuture<T> source,
                                                          ScheduledExecutorService scheduler,
                                                          long millis,
                                                          T fallback) {
        throw new UnsupportedOperationException("TODO: implement withFallbackOnTimeout");
    }

    /**
     * Tell whether {@code future} finishes within {@code millis} milliseconds. The answer is
     * {@code true} if it completes in time — <b>normally or exceptionally</b> — and
     * {@code false} if the time runs out first. The returned future itself never fails.
     *
     * <p>Examples:
     * <pre>
     * completesWithin(alreadyDone, scheduler, 1000)    →  future of true
     * completesWithin(alreadyFailed, scheduler, 1000)  →  future of true
     * completesWithin(neverDone, scheduler, 50)        →  future of false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>First turn "finished, however it ended" into {@code true}; then race it against a timer</li>
     * </ul>
     *
     * @param future    the future to watch
     * @param scheduler used to schedule the timeout
     * @param millis    the time limit in milliseconds
     * @return a future of {@code true} (finished in time) or {@code false} (timed out)
     */
    public CompletableFuture<Boolean> completesWithin(CompletableFuture<?> future,
                                                      ScheduledExecutorService scheduler,
                                                      long millis) {
        throw new UnsupportedOperationException("TODO: implement completesWithin");
    }
}
