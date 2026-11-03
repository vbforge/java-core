package com.vbforge.multithreading_advanced;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 🎯 Tasks for {@link ExecutorServiceDemo}.
 *
 * <p>Implement each method, remove the {@code UnsupportedOperationException},
 * then remove {@code @Disabled} from the matching nested class in
 * {@code ExecutorServiceTasksTest} and run it.
 */
public class ExecutorServiceTasks {

    /**
     * Squares every number concurrently on a fixed thread pool that you create
     * inside this method, and returns the squares in the <b>same order</b> as
     * the input. The pool must be shut down before the method returns, even if
     * a task fails.
     *
     * <p>Examples:
     * <pre>
     *   squareAll([1, 2, 3, 4], 2)  → [1, 4, 9, 16]
     *   squareAll([], 3)            → []
     *   squareAll([5], 0)           → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One task per number; keep the futures in a list in input order.</li>
     *   <li>{@code Executors.newFixedThreadPool} already rejects a size below 1.</li>
     *   <li>Which block guarantees the pool is shut down on every path?</li>
     * </ul>
     *
     * @param numbers  the numbers to square (never null)
     * @param poolSize number of worker threads
     * @return the squares, in input order
     * @throws InterruptedException if interrupted while waiting for a result
     * @throws ExecutionException   if a task fails
     */
    public List<Integer> squareAll(List<Integer> numbers, int poolSize)
            throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("TODO: implement squareAll");
    }

    /**
     * Shuts an executor down in two phases: first politely, then forcefully.
     *
     * <p>Stop accepting new tasks and give the running and queued tasks up to
     * {@code timeoutMillis} to finish. If the executor has terminated by then,
     * return {@code true}. Otherwise interrupt everything that is still
     * running and return {@code false}. If the calling thread is interrupted
     * while waiting, also force the shutdown, re-set the thread's interrupt
     * flag, and return {@code false}.
     *
     * <p>Examples:
     * <pre>
     *   executor with only quick tasks, timeout 1000   → true  (executor is terminated)
     *   executor with a task that never ends, timeout 50 → false (executor is shut down,
     *                                                           and the task was interrupted)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Three methods of {@code ExecutorService} are involved: one polite,
     *       one that waits, one forceful.</li>
     *   <li>{@code InterruptedException} clears the interrupt flag when it is thrown.</li>
     * </ul>
     *
     * @param executor      the executor to stop (never null)
     * @param timeoutMillis how long to wait for a graceful stop, in milliseconds
     * @return {@code true} if it terminated gracefully, {@code false} if it had to be forced
     */
    public boolean shutdownGracefully(ExecutorService executor, long timeoutMillis) {
        throw new UnsupportedOperationException("TODO: implement shutdownGracefully");
    }

    /**
     * Creates a fixed thread pool whose threads are named {@code prefix-1},
     * {@code prefix-2}, … {@code prefix-size}, numbered in creation order
     * starting at 1.
     *
     * <p>Examples:
     * <pre>
     *   newNamedFixedPool(2, "report")  → threads named "report-1" and "report-2"
     *   newNamedFixedPool(1, "io")      → a thread named "io-1"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Executors.newFixedThreadPool} has an overload that takes a factory.</li>
     *   <li>{@code ThreadFactory} has a single method, so a lambda works.</li>
     *   <li>The counter is shared by several threads — which class is safe for that?</li>
     * </ul>
     *
     * @param size   number of worker threads
     * @param prefix the thread-name prefix
     * @return the new pool (the caller shuts it down)
     */
    public ExecutorService newNamedFixedPool(int size, String prefix) {
        throw new UnsupportedOperationException("TODO: implement newNamedFixedPool");
    }

    /**
     * Creates a <b>bounded</b> pool: {@code coreSize} core threads, up to
     * {@code maxSize} threads in total, and a waiting queue holding at most
     * {@code queueCapacity} tasks. When the pool and queue are both full, a new
     * task must be refused with a {@code RejectedExecutionException}. Idle extra
     * threads may be reclaimed after 30 seconds.
     *
     * <p>Examples (every submitted task blocks until released):
     * <pre>
     *   newBoundedPool(1, 2, 1):
     *     task 1 → accepted (runs on the core thread)
     *     task 2 → accepted (waits in the queue)
     *     task 3 → accepted (the queue is full, so a 2nd thread is created)
     *     task 4 → throws RejectedExecutionException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Build it with the {@code ThreadPoolExecutor} constructor, not an {@code Executors} factory.</li>
     *   <li>Which {@code BlockingQueue} implementation has a fixed capacity?</li>
     *   <li>You do not need to pass a rejection handler explicitly — check the default.</li>
     * </ul>
     *
     * @param coreSize      number of core threads
     * @param maxSize       maximum number of threads
     * @param queueCapacity capacity of the waiting queue
     * @return the new pool (the caller shuts it down)
     */
    public ThreadPoolExecutor newBoundedPool(int coreSize, int maxSize, int queueCapacity) {
        throw new UnsupportedOperationException("TODO: implement newBoundedPool");
    }

    /**
     * Schedules a greeting to be produced after a delay.
     *
     * <p>The returned future completes with {@code "Hello, <name>!"} once
     * {@code delayMillis} milliseconds have passed. The method itself must
     * return immediately — it must not wait for the delay.
     *
     * <p>Examples:
     * <pre>
     *   scheduleGreeting(scheduler, "Ann", 20).get()   → "Hello, Ann!"   (after ~20 ms)
     *   scheduleGreeting(scheduler, "Bob", 2000).isDone() → false        (right after the call)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ScheduledExecutorService} has a {@code schedule} overload for tasks that return a value.</li>
     *   <li>A lambda that returns a {@code String} becomes a {@code Callable<String>}.</li>
     * </ul>
     *
     * @param scheduler   the scheduler to use (never null)
     * @param name        who to greet
     * @param delayMillis delay before the greeting is produced, in milliseconds
     * @return the scheduled future
     */
    public ScheduledFuture<String> scheduleGreeting(ScheduledExecutorService scheduler,
                                                    String name,
                                                    long delayMillis) {
        throw new UnsupportedOperationException("TODO: implement scheduleGreeting");
    }
}
