package com.vbforge.virtual_threads_intro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StructuredConcurrencyTasks — your implementations")
class StructuredConcurrencyTasksTest {

    private StructuredConcurrencyTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new StructuredConcurrencyTasks();
    }

    /** A task that signals {@code started}, then blocks until interrupted; the interrupt counts down {@code interrupted}. */
    private static Callable<String> blocksUntilInterrupted(CountDownLatch started, CountDownLatch interrupted) {
        return () -> {
            started.countDown();
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                interrupted.countDown();
                throw e;
            }
            return "never";
        };
    }

    /** Cancellation is cooperative and asynchronous: allow a few seconds for the interrupt to arrive. */
    private static void assertInterrupted(CountDownLatch interrupted) throws InterruptedException {
        assertThat(interrupted.await(5, TimeUnit.SECONDS)).isTrue();
    }

    private static Callable<String> failsWith(String message) {
        return () -> {
            throw new IllegalStateException(message);
        };
    }

    @Nested
    @Disabled("Remove @Disabled when you implement combine()")
    @DisplayName("1. combine")
    class CombineTest {

        @Test
        @DisplayName("example: ('A', 'B') → 'A+B'")
        void example() throws Exception {
            assertThat(tasks.combine(() -> "A", () -> "B")).isEqualTo("A+B");
        }

        @Test
        @DisplayName("order follows the arguments, not the completion order")
        void orderFollowsArguments() throws Exception {
            Callable<String> slowLeft = () -> {
                Thread.sleep(50);
                return "L";
            };

            assertThat(tasks.combine(slowLeft, () -> "R")).isEqualTo("L+R");
        }

        @Test
        @Timeout(10)
        @DisplayName("right fails while left blocks → ExecutionException, left interrupted")
        void rightFailureCancelsLeft() throws Exception {
            CountDownLatch leftStarted = new CountDownLatch(1);
            CountDownLatch leftInterrupted = new CountDownLatch(1);
            Callable<String> right = () -> {
                leftStarted.await();
                throw new IllegalStateException("boom");
            };

            assertThatThrownBy(() -> tasks.combine(blocksUntilInterrupted(leftStarted, leftInterrupted), right))
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("boom");
            assertInterrupted(leftInterrupted);
        }

        @Test
        @Timeout(10)
        @DisplayName("left fails while right blocks → ExecutionException, right interrupted")
        void leftFailureCancelsRight() throws Exception {
            CountDownLatch rightStarted = new CountDownLatch(1);
            CountDownLatch rightInterrupted = new CountDownLatch(1);
            Callable<String> left = () -> {
                rightStarted.await();
                throw new IllegalStateException("boom");
            };

            assertThatThrownBy(() -> tasks.combine(left, blocksUntilInterrupted(rightStarted, rightInterrupted)))
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(IllegalStateException.class);
            assertInterrupted(rightInterrupted);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstSuccessful()")
    @DisplayName("2. firstSuccessful")
    class FirstSuccessfulTest {

        @Test
        @DisplayName("example: [fail, 'B', fail] → 'B'")
        void example() throws Exception {
            List<Callable<String>> input = List.of(failsWith("x"), () -> "B", failsWith("y"));

            assertThat(tasks.firstSuccessful(input)).isEqualTo("B");
        }

        @Test
        @DisplayName("single task → its result")
        void singleTask() throws Exception {
            assertThat(tasks.firstSuccessful(List.of(() -> "only"))).isEqualTo("only");
        }

        @Test
        @DisplayName("all tasks fail → ExecutionException")
        void allFail() {
            List<Callable<String>> input = List.of(failsWith("x"), failsWith("y"));

            assertThatThrownBy(() -> tasks.firstSuccessful(input)).isInstanceOf(ExecutionException.class);
        }

        @Test
        @DisplayName("empty list → IllegalArgumentException")
        void emptyList() {
            assertThatThrownBy(() -> tasks.firstSuccessful(List.of())).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @Timeout(10)
        @DisplayName("slow loser is interrupted")
        void loserIsCancelled() throws Exception {
            CountDownLatch slowStarted = new CountDownLatch(1);
            CountDownLatch slowInterrupted = new CountDownLatch(1);
            Callable<String> fast = () -> {
                slowStarted.await();
                return "fast";
            };

            String result = tasks.firstSuccessful(List.of(blocksUntilInterrupted(slowStarted, slowInterrupted), fast));

            assertThat(result).isEqualTo("fast");
            assertInterrupted(slowInterrupted);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement callWithTimeout()")
    @DisplayName("3. callWithTimeout")
    class CallWithTimeoutTest {

        @Test
        @DisplayName("example: fast task → its result")
        void example() throws Exception {
            assertThat(tasks.callWithTimeout(() -> 42, Duration.ofSeconds(5))).isEqualTo(42);
        }

        @Test
        @DisplayName("task that needs a little time still succeeds within a generous timeout")
        void shortWork() throws Exception {
            Callable<String> work = () -> {
                Thread.sleep(20);
                return "done";
            };

            assertThat(tasks.callWithTimeout(work, Duration.ofSeconds(5))).isEqualTo("done");
        }

        @Test
        @Timeout(10)
        @DisplayName("too slow → TimeoutException, task interrupted")
        void timeoutCancelsTask() throws Exception {
            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch interrupted = new CountDownLatch(1);

            assertThatThrownBy(() -> tasks.callWithTimeout(blocksUntilInterrupted(started, interrupted), Duration.ofSeconds(1)))
                    .isInstanceOf(TimeoutException.class);
            assertInterrupted(interrupted);
        }

        @Test
        @DisplayName("task failure → ExecutionException with the original cause")
        void taskFailure() {
            assertThatThrownBy(() -> tasks.callWithTimeout(failsWith("boom"), Duration.ofSeconds(5)))
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("boom");
        }
    }
}
