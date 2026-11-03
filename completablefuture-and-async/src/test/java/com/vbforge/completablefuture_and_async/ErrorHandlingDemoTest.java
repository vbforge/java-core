package com.vbforge.completablefuture_and_async;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ErrorHandlingDemo — API contracts")
class ErrorHandlingDemoTest {

    @Nested
    @DisplayName("1. A failure skips every dependent stage")
    class FailureSkipsDependentsTests {

        @Test
        @DisplayName("dependent stages are not executed")
        void dependentsNotExecuted() {
            AtomicInteger executed = new AtomicInteger();

            CompletableFuture<Integer> chain = ComposingDemo.<String>failed(new IllegalStateException("upstream"))
                    .thenApply(s -> {
                        executed.incrementAndGet();
                        return s.length();
                    })
                    .thenApply(n -> {
                        executed.incrementAndGet();
                        return n * 2;
                    });

            assertThat(chain).isCompletedExceptionally();
            assertThat(executed.get()).isZero();
        }

        @Test
        @DisplayName("the failure travels down to the end of the chain")
        void failureTravels() {
            CompletableFuture<String> end = ComposingDemo.<String>failed(new IllegalStateException("upstream"))
                    .thenApply(String::toUpperCase)
                    .thenApply(s -> s + "!");

            assertThatThrownBy(end::join)
                    .isInstanceOf(CompletionException.class)
                    .hasRootCauseMessage("upstream");
        }

        @Test
        @DisplayName("an unobserved failure does not throw into the creating thread")
        void unobservedFailureIsSilent() {
            CompletableFuture<String> silent = CompletableFuture.supplyAsync(() -> {
                throw new IllegalStateException("nobody will ever see this");
            });

            // no exception here — the failure only surfaces if somebody joins or attaches a handler
            assertThat(silent.handle((v, e) -> e).join()).isNotNull();
        }
    }

    @Nested
    @DisplayName("2. exceptionally")
    class ExceptionallyTests {

        @Test
        @DisplayName("supplies a fallback value when the source failed")
        void fallbackOnFailure() {
            CompletableFuture<Integer> price = ComposingDemo.<Integer>failed(new IllegalStateException("down"))
                    .exceptionally(error -> -1);

            assertThat(price.join()).isEqualTo(-1);
        }

        @Test
        @DisplayName("is skipped when the source succeeded")
        void skippedOnSuccess() {
            AtomicInteger called = new AtomicInteger();

            CompletableFuture<Integer> healthy = CompletableFuture.completedFuture(99).exceptionally(error -> {
                called.incrementAndGet();
                return -1;
            });

            assertThat(healthy.join()).isEqualTo(99);
            assertThat(called.get()).isZero();
        }

        @Test
        @DisplayName("recovery lets later stages run normally")
        void laterStagesRun() {
            CompletableFuture<Integer> doubled = ComposingDemo.<Integer>failed(new IllegalStateException("down"))
                    .exceptionally(error -> 5)
                    .thenApply(n -> n * 2);

            assertThat(doubled.join()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("3. CompletionException wrapping")
    class WrappingTests {

        @Test
        @DisplayName("same future completed with completeExceptionally → the raw exception")
        void rawExceptionOnSameFuture() {
            CompletableFuture<String> raw = ComposingDemo.failed(new IllegalStateException("original"));

            assertThat(ErrorHandlingDemo.exceptionSeenBy(raw)).isEqualTo(IllegalStateException.class);
        }

        @Test
        @DisplayName("a dependent stage → CompletionException")
        void wrappedOnDependent() {
            CompletableFuture<String> dependent = ComposingDemo.<String>failed(new IllegalStateException("original"))
                    .thenApply(s -> s);

            assertThat(ErrorHandlingDemo.exceptionSeenBy(dependent)).isEqualTo(CompletionException.class);
        }

        @Test
        @DisplayName("supplyAsync whose supplier threw → CompletionException")
        void wrappedOnSupplyAsync() {
            CompletableFuture<String> thrown = CompletableFuture.supplyAsync(() -> {
                throw new IllegalStateException("original");
            });

            assertThat(ErrorHandlingDemo.exceptionSeenBy(thrown)).isEqualTo(CompletionException.class);
        }

        @Test
        @DisplayName("the real exception is the cause of the wrapper")
        void realExceptionIsTheCause() {
            CompletableFuture<String> dependent = ComposingDemo.<String>failed(new IllegalStateException("original"))
                    .thenApply(s -> s);

            Throwable seen = dependent.handle((value, error) -> error).join();

            assertThat(seen).isInstanceOf(CompletionException.class);
            assertThat(seen.getCause()).isInstanceOf(IllegalStateException.class).hasMessage("original");
        }
    }

    @Nested
    @DisplayName("4. handle")
    class HandleTests {

        @Test
        @DisplayName("receives the value and a null error on success")
        void successBranch() {
            assertThat(ErrorHandlingDemo.label(CompletableFuture.completedFuture(21))).isEqualTo("value=21");
        }

        @Test
        @DisplayName("receives a null value and the error on failure")
        void failureBranch() {
            CompletableFuture<Integer> failing = ComposingDemo.failed(new IllegalStateException("nope"));

            assertThat(ErrorHandlingDemo.label(failing)).isEqualTo("error=IllegalStateException");
        }

        @Test
        @DisplayName("can change the result type")
        void changesType() {
            CompletableFuture<String> text = CompletableFuture.completedFuture(7)
                    .handle((value, error) -> "seven=" + value);

            assertThat(text.join()).isEqualTo("seven=7");
        }

        @Test
        @DisplayName("the returned future completes normally even if the source failed")
        void resultDoesNotFail() {
            CompletableFuture<Integer> failing = ComposingDemo.failed(new IllegalStateException("nope"));

            CompletableFuture<String> handled = failing.handle((value, error) -> "recovered");

            assertThat(handled).isNotCompletedExceptionally();
        }
    }

    @Nested
    @DisplayName("5. whenComplete")
    class WhenCompleteTests {

        @Test
        @DisplayName("keeps the value of a successful source")
        void keepsValue() {
            List<String> audit = new ArrayList<>();

            CompletableFuture<String> good = CompletableFuture.completedFuture("v")
                    .whenComplete((value, error) -> audit.add("success:" + value));

            assertThat(good.join()).isEqualTo("v");
            assertThat(audit).containsExactly("success:v");
        }

        @Test
        @DisplayName("keeps the failure of a failed source")
        void keepsFailure() {
            List<String> audit = new ArrayList<>();

            CompletableFuture<String> bad = ComposingDemo.<String>failed(new IllegalStateException("x"))
                    .whenComplete((value, error) -> audit.add("failure:" + error.getMessage()));

            assertThat(bad).isCompletedExceptionally();
            assertThat(audit).containsExactly("failure:x");
        }

        @Test
        @DisplayName("a throwing action fails a successful source")
        void throwingActionFailsSuccess() {
            CompletableFuture<String> result = CompletableFuture.completedFuture("v").whenComplete((value, error) -> {
                throw new IllegalArgumentException("from the action");
            });

            assertThatThrownBy(result::join)
                    .isInstanceOf(CompletionException.class)
                    .hasRootCauseInstanceOf(IllegalArgumentException.class)
                    .hasRootCauseMessage("from the action");
        }

        @Test
        @DisplayName("a throwing action does not replace the original failure")
        void originalFailureWins() {
            CompletableFuture<String> result = ComposingDemo.<String>failed(new IllegalStateException("original"))
                    .whenComplete((value, error) -> {
                        throw new IllegalArgumentException("from the action");
                    });

            assertThatThrownBy(result::join)
                    .isInstanceOf(CompletionException.class)
                    .hasRootCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("original");
        }
    }

    @Nested
    @DisplayName("6. Recovering with another async call")
    class RecoverWithTests {

        @Test
        @DisplayName("a healthy primary is used and the backup is never started")
        void backupNotStarted() {
            AtomicInteger backupCalls = new AtomicInteger();

            String result = ErrorHandlingDemo.recoverWith(CompletableFuture.completedFuture("primary"), () -> {
                backupCalls.incrementAndGet();
                return CompletableFuture.completedFuture("backup");
            }).join();

            assertThat(result).isEqualTo("primary");
            assertThat(backupCalls.get()).isZero();
        }

        @Test
        @DisplayName("a failed primary starts the backup")
        void backupStarted() {
            AtomicInteger backupCalls = new AtomicInteger();

            String result = ErrorHandlingDemo.recoverWith(
                    ComposingDemo.<String>failed(new IllegalStateException("primary down")),
                    () -> {
                        backupCalls.incrementAndGet();
                        return CompletableFuture.supplyAsync(() -> "backup");
                    }).join();

            assertThat(result).isEqualTo("backup");
            assertThat(backupCalls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("if the backup fails too, the result fails with the backup's error")
        void backupFailsToo() {
            CompletableFuture<String> result = ErrorHandlingDemo.recoverWith(
                    ComposingDemo.<String>failed(new IllegalStateException("primary down")),
                    () -> ComposingDemo.<String>failed(new IllegalArgumentException("backup down")));

            assertThatThrownBy(result::join)
                    .isInstanceOf(CompletionException.class)
                    .hasRootCauseMessage("backup down");
        }
    }

    @Nested
    @DisplayName("7. cancel")
    class CancelTests {

        @Test
        @DisplayName("cancel completes the future exceptionally with CancellationException")
        void cancelCompletesExceptionally() {
            CompletableFuture<String> pending = new CompletableFuture<>();

            boolean cancelled = pending.cancel(true);

            assertThat(cancelled).isTrue();
            assertThat(pending.isCancelled()).isTrue();
            assertThat(pending).isCompletedExceptionally();
            assertThatThrownBy(pending::join).isInstanceOf(CancellationException.class);
        }

        @Test
        @DisplayName("cancelling a future that is already done has no effect")
        void cancelAfterDone() {
            CompletableFuture<String> done = CompletableFuture.completedFuture("v");

            assertThat(done.cancel(true)).isFalse();
            assertThat(done.join()).isEqualTo("v");
        }

        @Test
        @DisplayName("cancel does not stop a supplier that is already running")
        void supplierKeepsRunning() {
            assertThat(ErrorHandlingDemo.cancelledSupplierStillRuns()).isTrue();
        }

        @Test
        @DisplayName("dependents of a cancelled future fail with CompletionException")
        void dependentsFail() {
            CompletableFuture<String> source = new CompletableFuture<>();
            CompletableFuture<String> dependent = source.thenApply(String::toUpperCase);

            source.cancel(true);

            assertThatThrownBy(dependent::join)
                    .isInstanceOf(CompletionException.class)
                    .hasCauseInstanceOf(CancellationException.class);
        }
    }

    @Nested
    @DisplayName("8. main() smoke test")
    class MainSmokeTests {

        @Test
        @DisplayName("main runs every section and prints the separators")
        void mainRuns() {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured));
            try {
                ErrorHandlingDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }

            assertThat(captured.toString()).contains("---").contains("audit trail");
        }
    }
}
