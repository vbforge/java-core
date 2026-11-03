package com.vbforge.virtual_threads_intro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Structured concurrency — the idea, built from final Java 21 APIs.
 *
 * <p><b>Principle:</b> if a task splits into concurrent subtasks, the subtasks must start
 * and finish <i>inside the code block that started them</i>. No subtask outlives its scope,
 * an error in one subtask cancels its siblings, and the parent never leaks threads.
 *
 * <p>Java 21 ships an API for this, {@code StructuredTaskScope} — but it is a
 * <b>preview API</b> (JEP 453): it needs {@code --enable-preview} and its shape changed in
 * later previews. So this module teaches the idea with the standard, final API instead,
 * plus a small hand-written {@link TaskScope} that makes the rules visible.
 * For reference, the Java 21 preview form looks like this (NOT compiled here):
 * <pre>
 * try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
 *     var user   = scope.fork(() -&gt; findUser());
 *     var orders = scope.fork(() -&gt; fetchOrders());
 *     scope.join().throwIfFailed();
 *     return new Page(user.get(), orders.get());
 * }
 * </pre>
 *
 * <p>Sections:
 * <ol>
 *   <li>A block that owns its tasks: {@code ExecutorService} as {@code AutoCloseable}</li>
 *   <li>Fork and join with {@link TaskScope}</li>
 *   <li>Fail fast: one failure cancels the siblings</li>
 * </ol>
 */
public class StructuredConcurrencyDemo {

    public static void main(String[] args) throws Exception {
        executorLifetimeDemo();
        System.out.println("---");
        forkJoinDemo();
        System.out.println("---");
        failFastDemo();
    }

    // =========================================================================
    // 1. A BLOCK THAT OWNS ITS TASKS
    // =========================================================================
    /*
     * What:
     *   Since Java 19, ExecutorService implements AutoCloseable. close() stops accepting
     *   tasks and waits until every submitted task has finished. try-with-resources
     *   therefore creates a lexical scope: tasks begin after the "{" and are all
     *   finished at the "}".
     *
     * Why it matters:
     *   Classic code often leaves threads running after the method returns ("fire and
     *   forget"), which makes leaks, lost exceptions and shutdown bugs hard to see.
     *   A scope puts the lifetime of concurrent work into the structure of the code.
     *
     * Common gotcha:
     *   close() WAITS. If a task never finishes, the block never exits. Make tasks
     *   respond to interruption so a scope can cancel them.
     */
    static void executorLifetimeDemo() {
        AtomicInteger finished = new AtomicInteger();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 3; i++) {
                executor.submit(() -> {
                    Thread.sleep(30);
                    return finished.incrementAndGet();
                });
            }
            System.out.println("inside the block, finished = (probably) " + finished.get());
        }

        System.out.println("after the block, finished = " + finished.get());
    }

    // =========================================================================
    // 2. FORK AND JOIN
    // =========================================================================
    /*
     * What:
     *   fork(task) starts a subtask on a new virtual thread and returns a Future.
     *   joinAll() waits for all of them. Results are read AFTER joinAll — with
     *   Future.resultNow() (Java 19+), which throws if the task is not successfully done.
     *
     * Why it matters:
     *   The two subtasks run in parallel, so the page takes max(30, 20) ≈ 30 ms instead of
     *   30 + 20 ≈ 50 ms — and the code still reads top to bottom.
     *
     * Complexity:
     *   - Wall time: O(slowest subtask), not O(sum)
     *   - Threads: one virtual thread per fork
     */
    static void forkJoinDemo() throws Exception {
        try (TaskScope scope = new TaskScope()) {
            Future<String> user = scope.fork(() -> {
                Thread.sleep(30);
                return "alice";
            });
            Future<Integer> orders = scope.fork(() -> {
                Thread.sleep(20);
                return 3;
            });

            scope.joinAll();

            System.out.println(user.resultNow() + " has " + orders.resultNow() + " orders");
        }
    }

    // =========================================================================
    // 3. FAIL FAST
    // =========================================================================
    /*
     * What:
     *   When one subtask fails, joinAll() cancels the others (cancel(true) = interrupt)
     *   and throws the failure. Leaving the try block waits until every cancelled task
     *   has really stopped.
     *
     * Why it matters:
     *   If the user lookup fails, spending a minute on the slow orders query is wasted
     *   work. Cancellation via interruption works because blocking calls such as
     *   Thread.sleep throw InterruptedException — on virtual threads too.
     *
     * Common gotcha:
     *   Cancellation is cooperative. A task that ignores interruption (a tight CPU loop
     *   that never checks Thread.interrupted()) cannot be cancelled, so close() would
     *   wait for it to finish by itself.
     */
    static void failFastDemo() throws InterruptedException {
        AtomicBoolean slowWasInterrupted = new AtomicBoolean();
        CountDownLatch slowStarted = new CountDownLatch(1);

        try (TaskScope scope = new TaskScope()) {
            scope.fork(() -> {
                slowStarted.countDown();
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException e) {
                    slowWasInterrupted.set(true);
                    throw e;
                }
                return "slow result";
            });
            scope.fork(() -> {
                slowStarted.await();
                throw new IllegalStateException("user service is down");
            });

            scope.joinAll();
        } catch (ExecutionException e) {
            System.out.println("scope failed: " + e.getCause().getMessage());
        }

        System.out.println("slow sibling was interrupted: " + slowWasInterrupted.get());
    }

    // =========================================================================
    // TaskScope — a minimal structured scope (teaching aid)
    // =========================================================================
    /**
     * A tiny structured-concurrency scope built on {@code newVirtualThreadPerTaskExecutor}.
     *
     * <ul>
     *   <li>{@link #fork} starts a subtask on a new virtual thread</li>
     *   <li>{@link #joinAll} waits for all subtasks; on the FIRST failure it cancels the
     *       rest and throws {@link ExecutionException}</li>
     *   <li>{@link #close} cancels anything still running and waits until every subtask
     *       thread has ended — no subtask outlives the scope</li>
     * </ul>
     *
     * <p>Not thread-safe: use it from the single thread that owns the scope. Call
     * {@code joinAll()} at most once.
     */
    public static final class TaskScope implements AutoCloseable {

        private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        private final BlockingQueue<Future<?>> completed = new LinkedBlockingQueue<>();
        private final List<Future<?>> forked = new ArrayList<>();
        private int pending;

        /**
         * Starts {@code task} on a new virtual thread.
         *
         * <p>Why {@code FutureTask} + {@code execute} instead of {@code executor.submit}? Cancelling
         * only REQUESTS an interrupt. In an experiment on JDK 21, {@code close()} returned before a
         * task cancelled through a {@code submit} future had reacted to the interrupt, whereas with
         * a {@code FutureTask} run via {@code execute} it always waited. This scope promises that no
         * subtask outlives it, so it uses the second form.
         *
         * @throws java.util.concurrent.RejectedExecutionException if the scope is already closed
         */
        public <T> Future<T> fork(Callable<T> task) {
            FutureTask<T> future = new FutureTask<>(task) {
                @Override
                protected void done() {
                    completed.add(this);
                }
            };
            executor.execute(future);
            forked.add(future);
            pending++;
            return future;
        }

        /**
         * Waits until every subtask has completed. Completion is observed in the order it
         * happens, so a failure is noticed immediately even if an earlier subtask is slow.
         *
         * @throws ExecutionException   the first failure; all other subtasks are cancelled
         * @throws InterruptedException if the owner thread is interrupted; subtasks are cancelled
         */
        public void joinAll() throws InterruptedException, ExecutionException {
            boolean success = false;
            try {
                while (pending > 0) {
                    Future<?> next = completed.take();
                    pending--;
                    next.get(); // already done: returns at once, or throws ExecutionException
                }
                success = true;
            } finally {
                if (!success) {
                    cancelAll();
                }
            }
        }

        private void cancelAll() {
            for (Future<?> f : forked) {
                f.cancel(true);
            }
        }

        /** Cancels unfinished subtasks, then waits until all subtask threads have ended. */
        @Override
        public void close() {
            executor.shutdownNow();
            executor.close();
        }
    }
}
