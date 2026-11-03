package com.vbforge.completablefuture_and_async;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ExecutorsAndTimeoutsDemo — API contracts")
class ExecutorsAndTimeoutsDemoTest {

    /** Daemon scheduler so a failing test can never keep the JVM alive. */
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "demo-test-scheduler");
        thread.setDaemon(true);
        return thread;
    });

    @AfterAll
    static void stopScheduler() {
        SCHEDULER.shutdownNow();
    }

    @Nested
    @DisplayName("1. The default executor")
    class CommonPoolTests {

        @Test
        @DisplayName("supplyAsync does not run the supplier on the calling thread")
        void runsOnAnotherThread() {
            String caller = Thread.currentThread().getName();

            String worker = CompletableFuture.supplyAsync(() -> Thread.currentThread().getName()).join();

            assertThat(worker).isNotEqualTo(caller);
        }

        @Test
        @DisplayName("common pool threads are daemon threads")
        void commonPoolThreadsAreDaemon() {
            // With a common-pool parallelism of 1 or less the JDK starts a plain new thread per task instead
            Assumptions.assumeTrue(ForkJoinPool.getCommonPoolParallelism() > 1);

            boolean daemon = CompletableFuture.supplyAsync(() -> Thread.currentThread().isDaemon()).join();

            assertThat(daemon).isTrue();
        }
    }

    @Nested
    @DisplayName("2. A custom executor")
    class CustomExecutorTests {

        @Test
        @DisplayName("the supplier runs on a thread of the given pool")
        void runsOnGivenPool() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                Thread poolThread = pool.submit(Thread::currentThread).get(5, TimeUnit.SECONDS);

                Thread actual = CompletableFuture.supplyAsync(Thread::currentThread, pool).get(5, TimeUnit.SECONDS);

                assertThat(actual).isSameAs(poolThread);
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("a shut-down pool rejects new tasks")
        void shutDownPoolRejects() {
            assertThat(ExecutorsAndTimeoutsDemo.submitAfterShutdownIsRejected()).isTrue();
        }

        @Test
        @DisplayName("shutdown lets already submitted work finish")
        void shutdownLetsWorkFinish() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            CompletableFuture<String> work = CompletableFuture.supplyAsync(() -> "finished", pool);

            pool.shutdown();

            assertThat(work.get(5, TimeUnit.SECONDS)).isEqualTo("finished");
            assertThat(pool.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Nested
    @DisplayName("3. thenApply vs thenApplyAsync")
    class AsyncVariantsTests {

        @Test
        @DisplayName("thenApply on an already completed future runs on the calling thread")
        void thenApplyRunsOnCaller() {
            Thread stageThread = CompletableFuture.completedFuture("x")
                    .thenApply(v -> Thread.currentThread())
                    .join();

            assertThat(stageThread).isSameAs(Thread.currentThread());
        }

        @Test
        @DisplayName("thenApplyAsync runs on another thread even if the future is already completed")
        void thenApplyAsyncRunsElsewhere() {
            Thread stageThread = CompletableFuture.completedFuture("x")
                    .thenApplyAsync(v -> Thread.currentThread())
                    .join();

            assertThat(stageThread).isNotSameAs(Thread.currentThread());
        }

        @Test
        @DisplayName("thenApplyAsync with an executor runs on that executor's thread")
        void thenApplyAsyncWithExecutor() throws Exception {
            ExecutorService stagePool = Executors.newSingleThreadExecutor();
            try {
                Thread poolThread = stagePool.submit(Thread::currentThread).get(5, TimeUnit.SECONDS);

                Thread stageThread = CompletableFuture.completedFuture("x")
                        .thenApplyAsync(v -> Thread.currentThread(), stagePool)
                        .get(5, TimeUnit.SECONDS);

                assertThat(stageThread).isSameAs(poolThread);
            } finally {
                stagePool.shutdownNow();
            }
        }

        @Test
        @DisplayName("thenApply on a pending future runs on the thread that completes it")
        void thenApplyRunsOnCompleter() {
            CompletableFuture<String> source = new CompletableFuture<>();
            CompletableFuture<Thread> stage = source.thenApply(v -> Thread.currentThread());

            source.complete("x");

            assertThat(stage.join()).isSameAs(Thread.currentThread());
        }
    }

    @Nested
    @DisplayName("4. Timeouts in Java 8")
    class TimeoutTests {

        @Test
        @DisplayName("a source that never completes fails with TimeoutException")
        void slowSourceTimesOut() {
            CompletableFuture<String> never = new CompletableFuture<>();

            CompletableFuture<String> guarded = ExecutorsAndTimeoutsDemo.withTimeout(never, SCHEDULER, 50);

            assertThatThrownBy(() -> guarded.get(5, TimeUnit.SECONDS))
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(TimeoutException.class);
        }

        @Test
        @DisplayName("join reports it as CompletionException caused by TimeoutException")
        void joinWrapsTimeout() {
            CompletableFuture<String> guarded =
                    ExecutorsAndTimeoutsDemo.withTimeout(new CompletableFuture<String>(), SCHEDULER, 50);

            assertThatThrownBy(guarded::join)
                    .isInstanceOf(CompletionException.class)
                    .hasCauseInstanceOf(TimeoutException.class);
        }

        @Test
        @DisplayName("a source that is already complete wins")
        void fastSourceWins() throws Exception {
            CompletableFuture<String> guarded = ExecutorsAndTimeoutsDemo.withTimeout(
                    CompletableFuture.completedFuture("fast enough"), SCHEDULER, 60_000);

            assertThat(guarded.get(5, TimeUnit.SECONDS)).isEqualTo("fast enough");
        }

        @Test
        @DisplayName("a source that completes before the deadline wins")
        void sourceCompletesInTime() throws Exception {
            CompletableFuture<String> source = new CompletableFuture<>();
            CompletableFuture<String> guarded = ExecutorsAndTimeoutsDemo.withTimeout(source, SCHEDULER, 60_000);

            assertThat(guarded).isNotDone();
            source.complete("in time");

            assertThat(guarded.get(5, TimeUnit.SECONDS)).isEqualTo("in time");
        }

        @Test
        @DisplayName("the guarded future is pending until either side finishes")
        void pendingUntilEitherSide() {
            CompletableFuture<String> guarded =
                    ExecutorsAndTimeoutsDemo.withTimeout(new CompletableFuture<String>(), SCHEDULER, 60_000);

            assertThat(guarded).isNotDone();
        }
    }

    @Nested
    @DisplayName("5. A timeout does not stop the source")
    class TimeoutDoesNotStopSourceTests {

        @Test
        @DisplayName("the source is still pending after the wrapper timed out")
        void sourceStillPending() {
            CompletableFuture<String> source = new CompletableFuture<>();
            CompletableFuture<String> guarded = ExecutorsAndTimeoutsDemo.withTimeout(source, SCHEDULER, 50);

            guarded.handle((value, error) -> null).join();

            assertThat(guarded).isCompletedExceptionally();
            assertThat(source).isNotDone();
            assertThat(source.isCancelled()).isFalse();
        }

        @Test
        @DisplayName("the source can still be completed afterwards, without changing the wrapper")
        void lateCompletionAccepted() {
            CompletableFuture<String> source = new CompletableFuture<>();
            CompletableFuture<String> guarded = ExecutorsAndTimeoutsDemo.withTimeout(source, SCHEDULER, 50);
            guarded.handle((value, error) -> null).join();

            boolean accepted = source.complete("late value");

            assertThat(accepted).isTrue();
            assertThat(source.join()).isEqualTo("late value");
            assertThat(guarded).isCompletedExceptionally();
        }
    }

    @Nested
    @DisplayName("6. main() smoke test")
    class MainSmokeTests {

        @Test
        @DisplayName("main runs every section and prints the separators")
        void mainRuns() {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured));
            try {
                ExecutorsAndTimeoutsDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }

            assertThat(captured.toString()).contains("---").contains("late value accepted");
        }
    }
}
