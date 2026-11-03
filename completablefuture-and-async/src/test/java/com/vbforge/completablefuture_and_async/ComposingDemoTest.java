package com.vbforge.completablefuture_and_async;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ComposingDemo — API contracts")
class ComposingDemoTest {

    @Nested
    @DisplayName("1. supplyAsync / runAsync / completedFuture / manual completion")
    class SupplyAsyncTests {

        @Test
        @DisplayName("supplyAsync computes its value on a different thread")
        void supplyAsyncRunsElsewhere() {
            Thread caller = Thread.currentThread();

            CompletableFuture<Thread> future = CompletableFuture.supplyAsync(Thread::currentThread);

            assertThat(future.join()).isNotSameAs(caller);
        }

        @Test
        @DisplayName("runAsync produces a Void future whose value is null")
        void runAsyncIsVoid() {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> { });

            assertThat(future.join()).isNull();
        }

        @Test
        @DisplayName("completedFuture is done immediately")
        void completedFutureIsDone() {
            CompletableFuture<String> future = CompletableFuture.completedFuture("already known");

            assertThat(future).isDone();
            assertThat(future.join()).isEqualTo("already known");
        }

        @Test
        @DisplayName("an empty future is not done until somebody completes it")
        void emptyFutureIsNotDone() {
            CompletableFuture<String> future = new CompletableFuture<>();

            assertThat(future).isNotDone();
            future.complete("filled");
            assertThat(future).isDone();
        }

        @Test
        @DisplayName("only the first complete() wins; later calls return false")
        void firstCompleteWins() {
            CompletableFuture<String> future = new CompletableFuture<>();

            boolean first = future.complete("first");
            boolean second = future.complete("second");

            assertThat(first).isTrue();
            assertThat(second).isFalse();
            assertThat(future.join()).isEqualTo("first");
        }
    }

    @Nested
    @DisplayName("2. thenApply")
    class ThenApplyTests {

        @Test
        @DisplayName("transforms the value of the source future")
        void transformsValue() {
            assertThat(ComposingDemo.findUserName(1).thenApply(String::length).join()).isEqualTo(6);
        }

        @Test
        @DisplayName("returns a new future and leaves the source untouched")
        void sourceUntouched() {
            CompletableFuture<String> source = ComposingDemo.findUserName(1);

            CompletableFuture<String> shouted = source.thenApply(String::toUpperCase);

            assertThat(shouted).isNotSameAs(source);
            assertThat(source.join()).isEqualTo("user-1");
            assertThat(shouted.join()).isEqualTo("USER-1");
        }

        @Test
        @DisplayName("stages can be chained")
        void chains() {
            CompletableFuture<String> shout = ComposingDemo.findUserName(1)
                    .thenApply(String::toUpperCase)
                    .thenApply(s -> s + "!");

            assertThat(shout.join()).isEqualTo("USER-1!");
        }

        @Test
        @DisplayName("the function is skipped when the source failed")
        void skippedOnFailure() {
            AtomicBoolean called = new AtomicBoolean(false);
            CompletableFuture<String> source = ComposingDemo.failed(new IllegalStateException("upstream"));

            CompletableFuture<String> next = source.thenApply(s -> {
                called.set(true);
                return s;
            });

            assertThat(next).isCompletedExceptionally();
            assertThat(called).isFalse();
        }
    }

    @Nested
    @DisplayName("3. thenAccept / thenRun")
    class ThenAcceptTests {

        @Test
        @DisplayName("thenAccept hands the value to the consumer and yields a Void future")
        void thenAcceptConsumes() {
            AtomicReference<String> received = new AtomicReference<>();

            CompletableFuture<Void> accepted = ComposingDemo.findUserName(2).thenAccept(received::set);

            assertThat(accepted.join()).isNull();
            assertThat(received.get()).isEqualTo("user-2");
        }

        @Test
        @DisplayName("thenRun runs the action without access to the value")
        void thenRunIgnoresValue() {
            AtomicBoolean ran = new AtomicBoolean(false);

            ComposingDemo.findUserName(3).thenRun(() -> ran.set(true)).join();

            assertThat(ran).isTrue();
        }

        @Test
        @DisplayName("the Void future completes only after the consumer has finished")
        void voidFutureWaitsForConsumer() {
            CompletableFuture<String> source = new CompletableFuture<>();
            AtomicBoolean consumed = new AtomicBoolean(false);

            CompletableFuture<Void> accepted = source.thenAccept(v -> consumed.set(true));

            assertThat(accepted).isNotDone();
            source.complete("go");
            accepted.join();
            assertThat(consumed).isTrue();
        }
    }

    @Nested
    @DisplayName("4. thenCompose")
    class ThenComposeTests {

        @Test
        @DisplayName("thenApply with an async function leaves a future inside a future")
        void thenApplyNests() {
            CompletableFuture<CompletableFuture<String>> nested =
                    ComposingDemo.findUserName(4).thenApply(ComposingDemo::findEmail);

            assertThat(nested.join()).isInstanceOf(CompletableFuture.class);
            assertThat(nested.join().join()).isEqualTo("user-4@example.com");
        }

        @Test
        @DisplayName("thenCompose flattens it into a single future")
        void thenComposeFlattens() {
            CompletableFuture<String> flat = ComposingDemo.findUserName(4).thenCompose(ComposingDemo::findEmail);

            assertThat(flat.join()).isEqualTo("user-4@example.com");
        }

        @Test
        @DisplayName("the composed future waits for the inner future")
        void waitsForInner() {
            CompletableFuture<String> inner = new CompletableFuture<>();

            CompletableFuture<String> composed = CompletableFuture.completedFuture("x").thenCompose(s -> inner);

            assertThat(composed).isNotDone();
            inner.complete("inner result");
            assertThat(composed.join()).isEqualTo("inner result");
        }

        @Test
        @DisplayName("the inner step does not start until the outer future completes")
        void innerStartsLate() {
            CompletableFuture<String> outer = new CompletableFuture<>();
            AtomicBoolean innerStarted = new AtomicBoolean(false);

            CompletableFuture<String> composed = outer.thenCompose(name -> {
                innerStarted.set(true);
                return ComposingDemo.findEmail(name);
            });

            assertThat(innerStarted).isFalse();
            outer.complete("late-user");
            assertThat(composed.join()).isEqualTo("late-user@example.com");
            assertThat(innerStarted).isTrue();
        }
    }

    @Nested
    @DisplayName("5. thenCombine")
    class ThenCombineTests {

        @Test
        @DisplayName("merges two independent futures")
        void merges() {
            assertThat(ComposingDemo.userCard(5).join()).isEqualTo("user-5 @ dept-2");
        }

        @Test
        @DisplayName("is not done until both futures are done")
        void waitsForBoth() {
            CompletableFuture<Integer> left = new CompletableFuture<>();
            CompletableFuture<Integer> right = new CompletableFuture<>();

            CompletableFuture<Integer> sum = left.thenCombine(right, Integer::sum);

            left.complete(20);
            assertThat(sum).isNotDone();
            right.complete(22);
            assertThat(sum.join()).isEqualTo(42);
        }

        @Test
        @DisplayName("fails when either input fails")
        void failsWhenEitherFails() {
            CompletableFuture<Integer> left = new CompletableFuture<>();
            CompletableFuture<Integer> right = new CompletableFuture<>();

            CompletableFuture<Integer> sum = left.thenCombine(right, Integer::sum);

            left.completeExceptionally(new IllegalStateException("left failed"));
            right.complete(1);

            assertThat(sum).isCompletedExceptionally();
        }
    }

    @Nested
    @DisplayName("6. join vs get")
    class JoinVsGetTests {

        @Test
        @DisplayName("join throws the unchecked CompletionException wrapping the cause")
        void joinThrowsCompletionException() {
            CompletableFuture<String> broken = ComposingDemo.failed(new IllegalStateException("boom"));

            assertThatThrownBy(broken::join)
                    .isInstanceOf(CompletionException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("boom");
        }

        @Test
        @DisplayName("get throws the checked ExecutionException wrapping the cause")
        void getThrowsExecutionException() {
            CompletableFuture<String> broken = ComposingDemo.failed(new IllegalStateException("boom"));

            assertThatThrownBy(broken::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("boom");
        }

        @Test
        @DisplayName("get with a timeout throws TimeoutException and leaves the future pending")
        void getTimesOut() {
            CompletableFuture<String> never = new CompletableFuture<>();

            assertThatThrownBy(() -> never.get(50, TimeUnit.MILLISECONDS))
                    .isInstanceOf(TimeoutException.class);
            assertThat(never).isNotDone();
        }
    }

    @Nested
    @DisplayName("7. main() smoke test")
    class MainSmokeTests {

        @Test
        @DisplayName("main runs every section and prints the separators")
        void mainRuns() {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured));
            try {
                ComposingDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }

            assertThat(captured.toString()).contains("---").contains("thenCompose");
        }
    }
}
