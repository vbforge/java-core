package com.vbforge.multithreading_advanced;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("CallableFutureTasks — your implementations")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class CallableFutureTasksTest {

    private CallableFutureTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new CallableFutureTasks();
    }

    private static Callable<String> after(final long millis, final String value) {
        return () -> {
            Thread.sleep(millis);
            return value;
        };
    }

    private static Callable<String> failing(final String message) {
        return () -> {
            throw new IllegalStateException(message);
        };
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runAll()")
    @DisplayName("runAll")
    class RunAllTest {

        private ExecutorService executor;

        @BeforeEach
        void createExecutor() {
            executor = Executors.newFixedThreadPool(3);
        }

        @AfterEach
        void stopExecutor() {
            executor.shutdownNow();
        }

        @Test
        @DisplayName("Results come back in job order, not completion order")
        void example() throws Exception {
            List<Callable<String>> jobs = Arrays.asList(after(80, "A"), after(0, "B"), after(40, "C"));
            assertThat(tasks.runAll(executor, jobs)).containsExactly("A", "B", "C");
        }

        @Test
        @DisplayName("No jobs gives an empty result")
        void empty() throws Exception {
            assertThat(tasks.runAll(executor, Collections.<Callable<String>>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("A single job")
        void singleJob() throws Exception {
            assertThat(tasks.runAll(executor, Arrays.asList(after(0, "only")))).containsExactly("only");
        }

        @Test
        @DisplayName("A failing job makes the call throw ExecutionException")
        void failingJob() {
            List<Callable<String>> jobs = Arrays.asList(after(0, "A"), failing("job B broke"));
            assertThatThrownBy(() -> tasks.runAll(executor, jobs))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseMessage("job B broke");
        }

        @Test
        @DisplayName("Jobs run in parallel: 3 jobs that wait for each other all finish")
        void jobsRunInParallel() throws Exception {
            CountDownLatch allStarted = new CountDownLatch(3);
            Callable<String> meetUp = () -> {
                allStarted.countDown();
                return allStarted.await(3, TimeUnit.SECONDS) ? "met" : "alone";
            };
            assertThat(tasks.runAll(executor, Arrays.asList(meetUp, meetUp, meetUp)))
                    .containsExactly("met", "met", "met");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstSuccessful()")
    @DisplayName("firstSuccessful")
    class FirstSuccessfulTest {

        private ExecutorService executor;

        @BeforeEach
        void createExecutor() {
            executor = Executors.newFixedThreadPool(3);
        }

        @AfterEach
        void stopExecutor() {
            executor.shutdownNow();
        }

        @Test
        @DisplayName("A failing job is ignored when another job succeeds")
        void example() throws Exception {
            List<Callable<String>> jobs = Arrays.asList(failing("down"), after(50, "B"));
            assertThat(tasks.firstSuccessful(executor, jobs)).isEqualTo("B");
        }

        @Test
        @DisplayName("A single job")
        void singleJob() throws Exception {
            assertThat(tasks.firstSuccessful(executor, Arrays.asList(after(0, "A")))).isEqualTo("A");
        }

        @Test
        @DisplayName("A fast job beats a slow one")
        void fastBeatsSlow() throws Exception {
            List<Callable<String>> jobs = Arrays.asList(after(3000, "slow"), after(0, "fast"));
            assertThat(tasks.firstSuccessful(executor, jobs)).isEqualTo("fast");
        }

        @Test
        @DisplayName("When every job fails: throws ExecutionException")
        void allFail() {
            List<Callable<String>> jobs = Arrays.asList(failing("one"), failing("two"));
            assertThatThrownBy(() -> tasks.firstSuccessful(executor, jobs))
                    .isInstanceOf(ExecutionException.class);
        }

        @Test
        @DisplayName("No jobs: throws IllegalArgumentException")
        void noJobs() {
            assertThatThrownBy(() -> tasks.firstSuccessful(executor, Collections.<Callable<String>>emptyList()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement getWithTimeout()")
    @DisplayName("getWithTimeout")
    class GetWithTimeoutTest {

        private ExecutorService executor;

        @BeforeEach
        void createExecutor() {
            executor = Executors.newSingleThreadExecutor();
        }

        @AfterEach
        void stopExecutor() {
            executor.shutdownNow();
        }

        @Test
        @DisplayName("A result that is ready: Optional.of(42)")
        void example() throws Exception {
            Future<Integer> future = executor.submit(() -> 42);
            assertThat(tasks.getWithTimeout(future, 1000)).isEqualTo(Optional.of(42));
        }

        @Test
        @DisplayName("A task that never ends: empty Optional, and the future is cancelled")
        void timesOut() throws Exception {
            CountDownLatch neverReleased = new CountDownLatch(1);
            Future<Integer> future = executor.submit(() -> {
                neverReleased.await();
                return 1;
            });

            Optional<Integer> result = tasks.getWithTimeout(future, 50);

            assertThat(result).isEmpty();
            assertThat(future.isCancelled()).isTrue();
        }

        @Test
        @DisplayName("A task that throws: ExecutionException propagates")
        void failedTask() {
            Future<Integer> future = executor.submit(() -> {
                throw new IllegalStateException("broken");
            });
            assertThatThrownBy(() -> tasks.getWithTimeout(future, 1000))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseMessage("broken");
        }

        @Test
        @DisplayName("A task that finishes shortly before the timeout still delivers its value")
        void finishesWithinTimeout() throws Exception {
            Future<Integer> future = executor.submit(() -> {
                Thread.sleep(50);
                return 7;
            });
            assertThat(tasks.getWithTimeout(future, 5000)).contains(7);
            assertThat(future.isCancelled()).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement outcome()")
    @DisplayName("outcome")
    class OutcomeTest {

        @Test
        @DisplayName("A future that completed normally: SUCCESS")
        void success() throws Exception {
            FutureTask<String> future = new FutureTask<String>(() -> "fine");
            future.run();
            assertThat(tasks.outcome(future)).isEqualTo("SUCCESS");
        }

        @Test
        @DisplayName("A cancelled future: CANCELLED")
        void cancelled() throws Exception {
            FutureTask<String> future = new FutureTask<String>(() -> "never runs");
            future.cancel(false);
            assertThat(tasks.outcome(future)).isEqualTo("CANCELLED");
        }

        @Test
        @DisplayName("A failed future: FAILED: boom")
        void failed() throws Exception {
            FutureTask<String> future = new FutureTask<String>(failing("boom"));
            future.run();
            assertThat(tasks.outcome(future)).isEqualTo("FAILED: boom");
        }

        @ParameterizedTest(name = "cause message \"{0}\"")
        @CsvSource({"boom", "disk full", "x"})
        @DisplayName("FAILED uses the message of the cause, not of the ExecutionException")
        void failedMessages(String message) throws Exception {
            FutureTask<String> future = new FutureTask<String>(failing(message));
            future.run();
            assertThat(tasks.outcome(future)).isEqualTo("FAILED: " + message);
        }

        @Test
        @DisplayName("A task that returns null still counts as SUCCESS")
        void nullResultIsSuccess() throws Exception {
            FutureTask<String> future = new FutureTask<String>(() -> null);
            future.run();
            assertThat(tasks.outcome(future)).isEqualTo("SUCCESS");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement parallelSum()")
    @DisplayName("parallelSum")
    class ParallelSumTest {

        private ExecutorService executor;

        @BeforeEach
        void createExecutor() {
            executor = Executors.newFixedThreadPool(4);
        }

        @AfterEach
        void stopExecutor() {
            executor.shutdownNow();
        }

        @Test
        @DisplayName("1..10 in 2 parts is 55")
        void example() throws Exception {
            assertThat(tasks.parallelSum(executor, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 2)).isEqualTo(55);
        }

        @Test
        @DisplayName("An empty array sums to 0")
        void emptyArray() throws Exception {
            assertThat(tasks.parallelSum(executor, new int[0], 3)).isZero();
        }

        @Test
        @DisplayName("More parts than elements still gives the right sum")
        void morePartsThanElements() throws Exception {
            assertThat(tasks.parallelSum(executor, new int[]{1, 2, 3}, 10)).isEqualTo(6);
        }

        @Test
        @DisplayName("Fewer than 1 part throws IllegalArgumentException")
        void invalidParts() {
            assertThatThrownBy(() -> tasks.parallelSum(executor, new int[]{1, 2, 3}, 0))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.parallelSum(executor, new int[]{1, 2, 3}, -1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Negative numbers cancel positive ones")
        void negativeNumbers() throws Exception {
            assertThat(tasks.parallelSum(executor, new int[]{-5, 5, -10, 10, 3}, 2)).isEqualTo(3);
        }

        @ParameterizedTest(name = "1..{0} in {1} parts is {2}")
        @CsvSource({"10,2,55", "100,4,5050", "100,7,5050", "5,10,15", "1,1,1"})
        @DisplayName("Sums 1..n correctly for any number of parts")
        void sumsOneToN(int n, int parts, int expected) throws Exception {
            int[] numbers = new int[n];
            for (int i = 0; i < n; i++) {
                numbers[i] = i + 1;
            }
            assertThat(tasks.parallelSum(executor, numbers, parts)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Does not modify the input array")
        void doesNotMutateInput() throws Exception {
            int[] numbers = {4, 3, 2, 1};
            tasks.parallelSum(executor, numbers, 2);
            assertThat(numbers).containsExactly(4, 3, 2, 1);
        }
    }
}
