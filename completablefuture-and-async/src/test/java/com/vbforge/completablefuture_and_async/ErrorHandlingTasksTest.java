package com.vbforge.completablefuture_and_async;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ErrorHandlingTasks — your implementations")
class ErrorHandlingTasksTest {

    private ErrorHandlingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ErrorHandlingTasks();
    }

    /** Bounded wait so a future that never completes fails the test instead of hanging it. */
    private static <T> T await(CompletableFuture<T> future) throws Exception {
        return future.get(5, TimeUnit.SECONDS);
    }

    private static <T> CompletableFuture<T> failed(Throwable error) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(error);
        return future;
    }

    private static Supplier<String> throwing(String message) {
        return () -> {
            throw new IllegalStateException(message);
        };
    }

    @Nested
    @Disabled("Remove @Disabled when you implement unwrap()")
    @DisplayName("unwrap")
    class UnwrapTest {

        @Test
        @DisplayName("example from the Javadoc: CompletionException(cause) → cause")
        void completionException() {
            IllegalStateException real = new IllegalStateException("x");

            assertThat(tasks.unwrap(new CompletionException(real))).isSameAs(real);
        }

        @Test
        @DisplayName("example from the Javadoc: nested wrappers are all peeled")
        void nestedWrappers() {
            IOException real = new IOException("io");

            Throwable wrapped = new CompletionException(new ExecutionException(real));

            assertThat(tasks.unwrap(wrapped)).isSameAs(real);
        }

        @Test
        @DisplayName("ExecutionException is a wrapper too")
        void executionException() {
            IllegalStateException real = new IllegalStateException("x");

            assertThat(tasks.unwrap(new ExecutionException(real))).isSameAs(real);
        }

        @Test
        @DisplayName("example from the Javadoc: a plain exception is returned as it is")
        void plainException() {
            IllegalArgumentException plain = new IllegalArgumentException("plain");

            assertThat(tasks.unwrap(plain)).isSameAs(plain);
        }

        @Test
        @DisplayName("example from the Javadoc: a non-wrapper with a cause is not peeled")
        void nonWrapperWithCause() {
            RuntimeException outer = new RuntimeException("outer", new IllegalStateException("inner"));

            assertThat(tasks.unwrap(outer)).isSameAs(outer);
        }

        @Test
        @DisplayName("a wrapper without a cause is returned as it is")
        void wrapperWithoutCause() {
            CompletionException lonely = new CompletionException("no cause", null);

            assertThat(tasks.unwrap(lonely)).isSameAs(lonely);
        }

        @Test
        @DisplayName("only the wrapper layers are peeled, the real exception's own cause is kept")
        void stopsAtRealException() {
            IllegalStateException real = new IllegalStateException("real", new IOException("deeper"));

            assertThat(tasks.unwrap(new CompletionException(real))).isSameAs(real);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement parseOrDefault()")
    @DisplayName("parseOrDefault")
    class ParseOrDefaultTest {

        @Test
        @DisplayName("example from the Javadoc: '42' → 42")
        void happyPath() throws Exception {
            assertThat(await(tasks.parseOrDefault("42", 0))).isEqualTo(42);
        }

        @Test
        @DisplayName("example from the Javadoc: 'abc' with default -1 → -1")
        void notANumber() throws Exception {
            assertThat(await(tasks.parseOrDefault("abc", -1))).isEqualTo(-1);
        }

        @Test
        @DisplayName("example from the Javadoc: null with default 3 → 3")
        void nullText() throws Exception {
            assertThat(await(tasks.parseOrDefault(null, 3))).isEqualTo(3);
        }

        @ParameterizedTest(name = "[{index}] parse ''{0}'' (default {1}) → {2}")
        @CsvSource({
                "42, 0, 42",
                "-8, 0, -8",
                "abc, -1, -1",
                "'', 5, 5",
                "' 7', 9, 9",
                "2147483648, 4, 4"
        })
        @DisplayName("parses valid ints and falls back for everything else")
        void parametrized(String text, int defaultValue, int expected) throws Exception {
            assertThat(await(tasks.parseOrDefault(text, defaultValue))).isEqualTo(expected);
        }

        @Test
        @DisplayName("the returned future never completes exceptionally")
        void neverFails() throws Exception {
            CompletableFuture<Integer> future = tasks.parseOrDefault("not a number", 1);

            await(future);
            assertThat(future).isNotCompletedExceptionally();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement describeOutcome()")
    @DisplayName("describeOutcome")
    class DescribeOutcomeTest {

        @Test
        @DisplayName("example from the Javadoc: completed with 5 → 'ok:5'")
        void success() throws Exception {
            assertThat(await(tasks.describeOutcome(CompletableFuture.completedFuture(5)))).isEqualTo("ok:5");
        }

        @Test
        @DisplayName("example from the Javadoc: completeExceptionally(ISE 'boom') → 'failed:boom'")
        void rawFailure() throws Exception {
            CompletableFuture<Integer> source = failed(new IllegalStateException("boom"));

            assertThat(await(tasks.describeOutcome(source))).isEqualTo("failed:boom");
        }

        @Test
        @DisplayName("example from the Javadoc: a dependent stage of a failed future → 'failed:boom'")
        void dependentFailure() throws Exception {
            CompletableFuture<Integer> source = CompletableFuture.<Integer>supplyAsync(() -> {
                throw new IllegalStateException("boom");
            }).thenApply(n -> n + 1);

            assertThat(await(tasks.describeOutcome(source))).isEqualTo("failed:boom");
        }

        @Test
        @DisplayName("supplyAsync that threw is described by the real message")
        void supplyAsyncThrew() throws Exception {
            CompletableFuture<Integer> source = CompletableFuture.supplyAsync(() -> {
                throw new IllegalArgumentException("bad input");
            });

            assertThat(await(tasks.describeOutcome(source))).isEqualTo("failed:bad input");
        }

        @Test
        @DisplayName("the returned future itself never fails")
        void resultNeverFails() throws Exception {
            CompletableFuture<String> result = tasks.describeOutcome(failed(new IllegalStateException("boom")));

            await(result);
            assertThat(result).isNotCompletedExceptionally();
        }

        @Test
        @DisplayName("waits for a pending source")
        void waitsForSource() throws Exception {
            CompletableFuture<Integer> source = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.describeOutcome(source);

            assertThat(result).isNotDone();
            source.complete(11);
            assertThat(await(result)).isEqualTo("ok:11");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement fallbackChain()")
    @DisplayName("fallbackChain")
    class FallbackChainTest {

        @Test
        @DisplayName("example from the Javadoc: [throws, 'b', 'c'] → 'b'")
        void happyPath() throws Exception {
            List<Supplier<String>> suppliers = Arrays.asList(throwing("a broke"), () -> "b", () -> "c");

            assertThat(await(tasks.fallbackChain(suppliers))).isEqualTo("b");
        }

        @Test
        @DisplayName("suppliers after the first success are never called")
        void laterSuppliersAreLazy() throws Exception {
            AtomicInteger thirdCalls = new AtomicInteger();
            List<Supplier<String>> suppliers = Arrays.asList(throwing("a broke"), () -> "b", () -> {
                thirdCalls.incrementAndGet();
                return "c";
            });

            await(tasks.fallbackChain(suppliers));

            assertThat(thirdCalls.get()).isZero();
        }

        @Test
        @DisplayName("suppliers are tried strictly in order")
        void triedInOrder() throws Exception {
            List<String> calls = Collections.synchronizedList(new ArrayList<String>());
            List<Supplier<String>> suppliers = Arrays.asList(
                    () -> {
                        calls.add("first");
                        throw new IllegalStateException("first broke");
                    },
                    () -> {
                        calls.add("second");
                        throw new IllegalStateException("second broke");
                    },
                    () -> {
                        calls.add("third");
                        return "ok";
                    });

            assertThat(await(tasks.fallbackChain(suppliers))).isEqualTo("ok");
            assertThat(calls).containsExactly("first", "second", "third");
        }

        @Test
        @DisplayName("the first supplier is used when it succeeds")
        void firstSupplierSucceeds() throws Exception {
            AtomicInteger secondCalls = new AtomicInteger();
            List<Supplier<String>> suppliers = Arrays.asList(() -> "first", () -> {
                secondCalls.incrementAndGet();
                return "second";
            });

            assertThat(await(tasks.fallbackChain(suppliers))).isEqualTo("first");
            assertThat(secondCalls.get()).isZero();
        }

        @Test
        @DisplayName("example from the Javadoc: all throw → failure of the last supplier")
        void allFail() {
            List<Supplier<String>> suppliers = Arrays.asList(throwing("x"), throwing("last"));

            CompletableFuture<String> result = tasks.fallbackChain(suppliers);

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("last");
        }

        @Test
        @DisplayName("example from the Javadoc: empty list → IllegalArgumentException")
        void emptyList() {
            CompletableFuture<String> result = tasks.fallbackChain(Collections.<Supplier<String>>emptyList());

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("suppliers do not run on the calling thread")
        void runsAsynchronously() throws Exception {
            Thread caller = Thread.currentThread();
            List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());
            List<Supplier<String>> suppliers = Arrays.asList(() -> {
                threads.add(Thread.currentThread());
                return "x";
            });

            await(tasks.fallbackChain(suppliers));

            assertThat(threads).hasSize(1).doesNotContain(caller);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement alwaysCleanup()")
    @DisplayName("alwaysCleanup")
    class AlwaysCleanupTest {

        @Test
        @DisplayName("example from the Javadoc: success → value kept, log ['cleanup']")
        void success() throws Exception {
            List<String> log = Collections.synchronizedList(new ArrayList<String>());

            CompletableFuture<String> result = tasks.alwaysCleanup(CompletableFuture.completedFuture("v"), log);

            assertThat(await(result)).isEqualTo("v");
            assertThat(log).containsExactly("cleanup");
        }

        @Test
        @DisplayName("example from the Javadoc: failure → still failing, log ['cleanup']")
        void failure() {
            List<String> log = Collections.synchronizedList(new ArrayList<String>());

            CompletableFuture<String> result = tasks.alwaysCleanup(
                    failed(new IllegalStateException("source broke")), log);

            assertThatThrownBy(() -> await(result))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("source broke");
            assertThat(log).containsExactly("cleanup");
        }

        @Test
        @DisplayName("cleanup runs only once the source has completed")
        void waitsForSource() throws Exception {
            List<String> log = Collections.synchronizedList(new ArrayList<String>());
            CompletableFuture<String> source = new CompletableFuture<>();

            CompletableFuture<String> result = tasks.alwaysCleanup(source, log);

            assertThat(result).isNotDone();
            assertThat(log).isEmpty();

            source.complete("late");
            assertThat(await(result)).isEqualTo("late");
            assertThat(log).containsExactly("cleanup");
        }

        @Test
        @DisplayName("cleanup is written exactly once")
        void exactlyOnce() throws Exception {
            List<String> log = Collections.synchronizedList(new ArrayList<String>());

            await(tasks.alwaysCleanup(CompletableFuture.completedFuture("v"), log));

            assertThat(log).hasSize(1);
        }
    }
}
