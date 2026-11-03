package com.vbforge.completablefuture_and_async;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AllOfAnyOfTasks — your implementations")
class AllOfAnyOfTasksTest {

    private AllOfAnyOfTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new AllOfAnyOfTasks();
    }

    /** Bounded wait so a future that never completes fails the test instead of hanging it. */
    private static <T> T await(CompletableFuture<T> future) throws Exception {
        return future.get(5, TimeUnit.SECONDS);
    }

    private static <T> CompletableFuture<T> done(T value) {
        return CompletableFuture.completedFuture(value);
    }

    private static <T> CompletableFuture<T> failed(String message) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalStateException(message));
        return future;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement allAsList()")
    @DisplayName("allAsList")
    class AllAsListTest {

        @Test
        @DisplayName("example from the Javadoc: [1, 2, 3]")
        void happyPath() throws Exception {
            List<CompletableFuture<Integer>> input = Arrays.asList(done(1), done(2), done(3));

            assertThat(await(tasks.allAsList(input))).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("example from the Javadoc: empty list → empty list")
        void emptyList() throws Exception {
            List<CompletableFuture<Integer>> input = Collections.emptyList();

            assertThat(await(tasks.allAsList(input))).isEmpty();
        }

        @Test
        @DisplayName("keeps input order even when futures complete in a different order")
        void keepsInputOrder() throws Exception {
            CompletableFuture<String> first = new CompletableFuture<>();
            CompletableFuture<String> second = new CompletableFuture<>();
            CompletableFuture<String> third = new CompletableFuture<>();

            CompletableFuture<List<String>> result = tasks.allAsList(Arrays.asList(first, second, third));

            third.complete("C");
            first.complete("A");
            second.complete("B");

            assertThat(await(result)).containsExactly("A", "B", "C");
        }

        @Test
        @DisplayName("stays pending until the last input completes")
        void waitsForLast() throws Exception {
            CompletableFuture<Integer> first = new CompletableFuture<>();
            CompletableFuture<Integer> second = new CompletableFuture<>();

            CompletableFuture<List<Integer>> result = tasks.allAsList(Arrays.asList(first, second));

            first.complete(1);
            assertThat(result).isNotDone();
            second.complete(2);
            assertThat(await(result)).containsExactly(1, 2);
        }

        @Test
        @DisplayName("fails when any input fails")
        void failsWhenAnyFails() {
            List<CompletableFuture<Integer>> input = Arrays.asList(done(1), failed("second broke"), done(3));

            CompletableFuture<List<Integer>> result = tasks.allAsList(input);

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseMessage("second broke");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstCompleted()")
    @DisplayName("firstCompleted")
    class FirstCompletedTest {

        @Test
        @DisplayName("example from the Javadoc: [pending, done('B'), pending] → 'B'")
        void happyPath() throws Exception {
            List<CompletableFuture<String>> input = Arrays.asList(
                    new CompletableFuture<String>(), done("B"), new CompletableFuture<String>());

            assertThat(await(tasks.firstCompleted(input))).isEqualTo("B");
        }

        @Test
        @DisplayName("example from the Javadoc: stays pending while all inputs are pending")
        void pendingWhileNoneDone() {
            List<CompletableFuture<String>> input = Arrays.asList(
                    new CompletableFuture<String>(), new CompletableFuture<String>());

            assertThat(tasks.firstCompleted(input)).isNotDone();
        }

        @Test
        @DisplayName("takes the value of the input that completes first, not the first in the list")
        void firstToCompleteWins() throws Exception {
            CompletableFuture<String> slow = new CompletableFuture<>();
            CompletableFuture<String> fast = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.firstCompleted(Arrays.asList(slow, fast));

            fast.complete("fast");
            assertThat(await(result)).isEqualTo("fast");
            slow.complete("slow");
            assertThat(await(result)).isEqualTo("fast");
        }

        @Test
        @DisplayName("a single input is returned as it completes")
        void singleInput() throws Exception {
            CompletableFuture<Integer> only = new CompletableFuture<>();

            CompletableFuture<Integer> result = tasks.firstCompleted(Collections.singletonList(only));

            only.complete(7);
            assertThat(await(result)).isEqualTo(7);
        }

        @Test
        @DisplayName("an input that fails first makes the result fail")
        void failureCanWin() {
            CompletableFuture<String> failing = new CompletableFuture<>();
            CompletableFuture<String> pending = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.firstCompleted(Arrays.asList(failing, pending));

            failing.completeExceptionally(new IllegalStateException("first failed"));

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseMessage("first failed");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countSuccessful()")
    @DisplayName("countSuccessful")
    class CountSuccessfulTest {

        @Test
        @DisplayName("example from the Javadoc: [ok, failed, ok] → 2")
        void happyPath() throws Exception {
            List<CompletableFuture<String>> input = Arrays.asList(done("a"), failed("b broke"), done("c"));

            assertThat(await(tasks.countSuccessful(input))).isEqualTo(2L);
        }

        @Test
        @DisplayName("example from the Javadoc: all failed → 0, and the result itself does not fail")
        void allFailed() throws Exception {
            List<CompletableFuture<String>> input = Arrays.asList(failed("one"), failed("two"));

            assertThat(await(tasks.countSuccessful(input))).isEqualTo(0L);
        }

        @Test
        @DisplayName("example from the Javadoc: empty list → 0")
        void emptyList() throws Exception {
            assertThat(await(tasks.countSuccessful(Collections.<CompletableFuture<String>>emptyList())))
                    .isEqualTo(0L);
        }

        @Test
        @DisplayName("every input successful → count equals the list size")
        void allSucceeded() throws Exception {
            List<CompletableFuture<String>> input = Arrays.asList(done("a"), done("b"), done("c"), done("d"));

            assertThat(await(tasks.countSuccessful(input))).isEqualTo(4L);
        }

        @Test
        @DisplayName("waits for pending inputs even when another one has already failed")
        void waitsForEveryInput() throws Exception {
            CompletableFuture<String> failing = failed("early failure");
            CompletableFuture<String> pending = new CompletableFuture<>();

            CompletableFuture<Long> result = tasks.countSuccessful(Arrays.asList(failing, pending));

            assertThat(result).isNotDone();
            pending.complete("late success");
            assertThat(await(result)).isEqualTo(1L);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstSuccessful()")
    @DisplayName("firstSuccessful")
    class FirstSuccessfulTest {

        @Test
        @DisplayName("example from the Javadoc: [failed, pending, done('C')] → 'C'")
        void happyPath() throws Exception {
            List<CompletableFuture<String>> input = Arrays.asList(
                    failed("a broke"), new CompletableFuture<String>(), done("C"));

            assertThat(await(tasks.firstSuccessful(input))).isEqualTo("C");
        }

        @Test
        @DisplayName("ignores failures and keeps waiting for a success")
        void ignoresFailures() throws Exception {
            CompletableFuture<String> failing = new CompletableFuture<>();
            CompletableFuture<String> pending = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.firstSuccessful(Arrays.asList(failing, pending));

            failing.completeExceptionally(new IllegalStateException("replica 1 down"));
            assertThat(result).isNotDone();
            pending.complete("replica 2");
            assertThat(await(result)).isEqualTo("replica 2");
        }

        @Test
        @DisplayName("the first success wins; later successes do not change the result")
        void firstSuccessWins() throws Exception {
            CompletableFuture<String> first = new CompletableFuture<>();
            CompletableFuture<String> second = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.firstSuccessful(Arrays.asList(first, second));

            second.complete("second");
            first.complete("first");

            assertThat(await(result)).isEqualTo("second");
        }

        @Test
        @DisplayName("example from the Javadoc: all failed → result fails")
        void allFailed() {
            List<CompletableFuture<String>> input = Arrays.asList(failed("one"), failed("two"));

            CompletableFuture<String> result = tasks.firstSuccessful(input);

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("example from the Javadoc: empty list → IllegalArgumentException")
        void emptyList() {
            CompletableFuture<String> result = tasks.firstSuccessful(Collections.<CompletableFuture<String>>emptyList());

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(IllegalArgumentException.class);
        }
    }
}
