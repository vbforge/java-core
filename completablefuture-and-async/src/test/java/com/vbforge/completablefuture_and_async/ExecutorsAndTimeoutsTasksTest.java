package com.vbforge.completablefuture_and_async;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ExecutorsAndTimeoutsTasks — your implementations")
class ExecutorsAndTimeoutsTasksTest {

    /** Daemon scheduler so a failing test can never keep the JVM alive. */
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "tasks-test-scheduler");
        thread.setDaemon(true);
        return thread;
    });

    private ExecutorsAndTimeoutsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ExecutorsAndTimeoutsTasks();
    }

    @AfterAll
    static void stopScheduler() {
        SCHEDULER.shutdownNow();
    }

    /** Bounded wait so a future that never completes fails the test instead of hanging it. */
    private static <T> T await(CompletableFuture<T> future) throws Exception {
        return future.get(5, TimeUnit.SECONDS);
    }

    @Nested
    @Disabled("Remove @Disabled when you implement namedDaemonThreadFactory()")
    @DisplayName("namedDaemonThreadFactory")
    class NamedDaemonThreadFactoryTest {

        @Test
        @DisplayName("example from the Javadoc: first thread is named 'io-1' and is a daemon")
        void happyPath() {
            ThreadFactory factory = tasks.namedDaemonThreadFactory("io");

            Thread thread = factory.newThread(() -> { });

            assertThat(thread.getName()).isEqualTo("io-1");
            assertThat(thread.isDaemon()).isTrue();
        }

        @Test
        @DisplayName("example from the Javadoc: the counter increases per thread")
        void counterIncreases() {
            ThreadFactory factory = tasks.namedDaemonThreadFactory("io");

            Thread first = factory.newThread(() -> { });
            Thread second = factory.newThread(() -> { });
            Thread third = factory.newThread(() -> { });

            assertThat(first.getName()).isEqualTo("io-1");
            assertThat(second.getName()).isEqualTo("io-2");
            assertThat(third.getName()).isEqualTo("io-3");
        }

        @Test
        @DisplayName("example from the Javadoc: every factory has its own counter")
        void independentCounters() {
            ThreadFactory a = tasks.namedDaemonThreadFactory("io");
            ThreadFactory b = tasks.namedDaemonThreadFactory("io");

            a.newThread(() -> { });
            a.newThread(() -> { });

            assertThat(b.newThread(() -> { }).getName()).isEqualTo("io-1");
        }

        @ParameterizedTest(name = "[{index}] prefix ''{0}'' → ''{0}-1''")
        @CsvSource({"worker", "db-pool", "x"})
        @DisplayName("uses the given prefix")
        void usesPrefix(String prefix) {
            Thread thread = tasks.namedDaemonThreadFactory(prefix).newThread(() -> { });

            assertThat(thread.getName()).isEqualTo(prefix + "-1");
        }

        @Test
        @DisplayName("newThread does not start the thread")
        void notStarted() {
            Thread thread = tasks.namedDaemonThreadFactory("io").newThread(() -> { });

            assertThat(thread.getState()).isEqualTo(Thread.State.NEW);
        }

        @Test
        @DisplayName("the created thread runs the given runnable")
        void runsTheRunnable() throws Exception {
            CountDownLatch ran = new CountDownLatch(1);
            Thread thread = tasks.namedDaemonThreadFactory("io").newThread(ran::countDown);

            thread.start();

            assertThat(ran.await(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement currentThreadName()")
    @DisplayName("currentThreadName")
    class CurrentThreadNameTest {

        @Test
        @DisplayName("example from the Javadoc: runs on the pool's thread")
        void runsOnPool() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable, "custom-worker");
                thread.setDaemon(true);
                return thread;
            });
            try {
                assertThat(await(tasks.currentThreadName(pool))).isEqualTo("custom-worker");
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("example from the Javadoc: a caller-runs executor reports the caller's thread")
        void callerRuns() throws Exception {
            Executor callerRuns = Runnable::run;

            assertThat(await(tasks.currentThreadName(callerRuns))).isEqualTo(Thread.currentThread().getName());
        }

        @Test
        @DisplayName("the task is handed to the given executor exactly once")
        void usesTheExecutor() throws Exception {
            AtomicInteger executions = new AtomicInteger();
            Executor counting = task -> {
                executions.incrementAndGet();
                task.run();
            };

            await(tasks.currentThreadName(counting));

            assertThat(executions.get()).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumOfSquares()")
    @DisplayName("sumOfSquares")
    class SumOfSquaresTest {

        @Test
        @DisplayName("example from the Javadoc: [1, 2, 3] → 14")
        void happyPath() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                assertThat(await(tasks.sumOfSquares(pool, Arrays.asList(1, 2, 3)))).isEqualTo(14);
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("example from the Javadoc: negative numbers → 13")
        void negativeNumbers() throws Exception {
            Executor callerRuns = Runnable::run;

            assertThat(await(tasks.sumOfSquares(callerRuns, Arrays.asList(-2, 3)))).isEqualTo(13);
        }

        @Test
        @DisplayName("example from the Javadoc: empty list → 0")
        void emptyList() throws Exception {
            Executor callerRuns = Runnable::run;

            assertThat(await(tasks.sumOfSquares(callerRuns, Collections.<Integer>emptyList()))).isZero();
        }

        @Test
        @DisplayName("each number is squared as its own task on the given executor")
        void oneTaskPerNumber() throws Exception {
            AtomicInteger submitted = new AtomicInteger();
            Executor counting = task -> {
                submitted.incrementAndGet();
                task.run();
            };

            await(tasks.sumOfSquares(counting, Arrays.asList(1, 2, 3, 4)));

            assertThat(submitted.get()).isGreaterThanOrEqualTo(4);
        }

        @Test
        @DisplayName("a single number")
        void singleNumber() throws Exception {
            Executor callerRuns = Runnable::run;

            assertThat(await(tasks.sumOfSquares(callerRuns, Collections.singletonList(9)))).isEqualTo(81);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement withFallbackOnTimeout()")
    @DisplayName("withFallbackOnTimeout")
    class WithFallbackOnTimeoutTest {

        @Test
        @DisplayName("example from the Javadoc: a source that never completes → fallback")
        void happyPath() throws Exception {
            CompletableFuture<String> never = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.withFallbackOnTimeout(never, SCHEDULER, 50, "fallback");

            assertThat(await(result)).isEqualTo("fallback");
        }

        @Test
        @DisplayName("example from the Javadoc: an already completed source keeps its value")
        void completedSource() throws Exception {
            CompletableFuture<String> result = tasks.withFallbackOnTimeout(
                    CompletableFuture.completedFuture("value"), SCHEDULER, 60_000, "fallback");

            assertThat(await(result)).isEqualTo("value");
        }

        @Test
        @DisplayName("a source that completes before the deadline keeps its value")
        void completesInTime() throws Exception {
            CompletableFuture<String> source = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.withFallbackOnTimeout(source, SCHEDULER, 60_000, "fallback");

            assertThat(result).isNotDone();
            source.complete("in time");
            assertThat(await(result)).isEqualTo("in time");
        }

        @Test
        @DisplayName("example from the Javadoc: a failing source is not replaced by the fallback")
        void failureIsNotReplaced() {
            CompletableFuture<String> source = new CompletableFuture<>();
            source.completeExceptionally(new IllegalStateException("source broke"));

            CompletableFuture<String> result = tasks.withFallbackOnTimeout(source, SCHEDULER, 60_000, "fallback");

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("source broke");
        }

        @Test
        @DisplayName("a late completion of the source does not change the result")
        void lateCompletionIgnored() throws Exception {
            CompletableFuture<String> source = new CompletableFuture<>();
            CompletableFuture<String> result = tasks.withFallbackOnTimeout(source, SCHEDULER, 50, "fallback");

            assertThat(await(result)).isEqualTo("fallback");
            source.complete("too late");

            assertThat(await(result)).isEqualTo("fallback");
        }

        @Test
        @DisplayName("the result is pending until the source completes or the time runs out")
        void pendingBeforeDeadline() {
            CompletableFuture<String> result = tasks.withFallbackOnTimeout(
                    new CompletableFuture<String>(), SCHEDULER, 60_000, "fallback");

            assertThat(result).isNotDone();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement completesWithin()")
    @DisplayName("completesWithin")
    class CompletesWithinTest {

        @Test
        @DisplayName("example from the Javadoc: an already completed future → true")
        void completed() throws Exception {
            CompletableFuture<String> done = CompletableFuture.completedFuture("x");

            assertThat(await(tasks.completesWithin(done, SCHEDULER, 60_000))).isTrue();
        }

        @Test
        @DisplayName("example from the Javadoc: an already failed future → true")
        void failed() throws Exception {
            CompletableFuture<String> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("broke"));

            assertThat(await(tasks.completesWithin(failed, SCHEDULER, 60_000))).isTrue();
        }

        @Test
        @DisplayName("example from the Javadoc: a future that never completes → false")
        void neverCompletes() throws Exception {
            CompletableFuture<String> never = new CompletableFuture<>();

            assertThat(await(tasks.completesWithin(never, SCHEDULER, 50))).isFalse();
        }

        @Test
        @DisplayName("a future that completes before the deadline → true")
        void completesInTime() throws Exception {
            CompletableFuture<String> future = new CompletableFuture<>();

            CompletableFuture<Boolean> result = tasks.completesWithin(future, SCHEDULER, 60_000);

            assertThat(result).isNotDone();
            future.complete("x");
            assertThat(await(result)).isTrue();
        }

        @Test
        @DisplayName("the returned future never fails, even for a failed input")
        void resultNeverFails() throws Exception {
            CompletableFuture<String> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("broke"));

            CompletableFuture<Boolean> result = tasks.completesWithin(failed, SCHEDULER, 60_000);

            await(result);
            assertThat(result).isNotCompletedExceptionally();
        }

        @Test
        @DisplayName("the input future is left untouched when time runs out")
        void inputUntouched() throws Exception {
            CompletableFuture<String> never = new CompletableFuture<>();

            await(tasks.completesWithin(never, SCHEDULER, 50));

            assertThat(never).isNotDone();
            assertThat(never.isCancelled()).isFalse();
        }
    }
}
