package com.vbforge.completablefuture_and_async;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AllOfAnyOfDemo — API contracts")
class AllOfAnyOfDemoTest {

    @Nested
    @DisplayName("1. allOf")
    class AllOfTests {

        @Test
        @DisplayName("is not done until every input is done")
        void waitsForEveryInput() {
            CompletableFuture<String> a = new CompletableFuture<>();
            CompletableFuture<String> b = new CompletableFuture<>();
            CompletableFuture<String> c = new CompletableFuture<>();

            CompletableFuture<Void> all = CompletableFuture.allOf(a, b, c);

            a.complete("A");
            b.complete("B");
            assertThat(all).isNotDone();
            c.complete("C");
            assertThat(all).isDone();
        }

        @Test
        @DisplayName("its value is Void, i.e. null")
        void valueIsVoid() {
            CompletableFuture<Void> all = CompletableFuture.allOf(
                    CompletableFuture.completedFuture("A"), CompletableFuture.completedFuture("B"));

            assertThat(all.join()).isNull();
        }

        @Test
        @DisplayName("is completed by the thread that completes the last input — no extra thread")
        void completedByLastCompleter() {
            CompletableFuture<String> a = new CompletableFuture<>();
            CompletableFuture<String> b = new CompletableFuture<>();
            CompletableFuture<Void> all = CompletableFuture.allOf(a, b);

            a.complete("A");
            b.complete("B");

            // complete() returned on THIS thread and allOf is already done: it was finished synchronously
            assertThat(all).isDone();
        }

        @Test
        @DisplayName("with no inputs it is already complete")
        void noInputs() {
            assertThat(CompletableFuture.allOf()).isDone();
        }
    }

    @Nested
    @DisplayName("2. Gathering results after allOf")
    class GatheringTests {

        @Test
        @DisplayName("sums the values of three futures")
        void sumsThree() {
            CompletableFuture<Integer> total = AllOfAnyOfDemo.totalOfThree(
                    CompletableFuture.completedFuture(10),
                    CompletableFuture.completedFuture(20),
                    CompletableFuture.completedFuture(12));

            assertThat(total.join()).isEqualTo(42);
        }

        @Test
        @DisplayName("waits until the last input completes")
        void waitsForLast() {
            CompletableFuture<Integer> p1 = new CompletableFuture<>();
            CompletableFuture<Integer> p2 = new CompletableFuture<>();
            CompletableFuture<Integer> p3 = new CompletableFuture<>();

            CompletableFuture<Integer> total = AllOfAnyOfDemo.totalOfThree(p1, p2, p3);

            p1.complete(1);
            p3.complete(3);
            assertThat(total).isNotDone();
            p2.complete(2);
            assertThat(total.join()).isEqualTo(6);
        }

        @Test
        @DisplayName("does not depend on completion order")
        void orderIndependent() {
            CompletableFuture<Integer> p1 = new CompletableFuture<>();
            CompletableFuture<Integer> p2 = new CompletableFuture<>();
            CompletableFuture<Integer> p3 = new CompletableFuture<>();
            CompletableFuture<Integer> total = AllOfAnyOfDemo.totalOfThree(p1, p2, p3);

            p3.complete(300);
            p2.complete(20);
            p1.complete(1);

            assertThat(total.join()).isEqualTo(321);
        }
    }

    @Nested
    @DisplayName("3. allOf and failures")
    class AllOfFailureTests {

        @Test
        @DisplayName("a failed input does not complete allOf while others are pending")
        void noFailFast() {
            CompletableFuture<String> failing = new CompletableFuture<>();
            CompletableFuture<String> slow = new CompletableFuture<>();

            CompletableFuture<Void> all = CompletableFuture.allOf(failing, slow);
            failing.completeExceptionally(new IllegalStateException("down"));

            assertThat(all).isNotDone();
        }

        @Test
        @DisplayName("once the last input completes, allOf fails")
        void failsAfterLast() {
            CompletableFuture<String> failing = new CompletableFuture<>();
            CompletableFuture<String> slow = new CompletableFuture<>();
            CompletableFuture<Void> all = CompletableFuture.allOf(failing, slow);

            failing.completeExceptionally(new IllegalStateException("down"));
            slow.complete("finished");

            assertThat(all).isCompletedExceptionally();
        }

        @Test
        @DisplayName("the successful inputs keep their values")
        void successfulInputsKeepValues() {
            CompletableFuture<String> failing = new CompletableFuture<>();
            CompletableFuture<String> fine = new CompletableFuture<>();
            CompletableFuture<Void> all = CompletableFuture.allOf(failing, fine);

            failing.completeExceptionally(new IllegalStateException("down"));
            fine.complete("still readable");

            assertThat(all).isCompletedExceptionally();
            assertThat(fine.join()).isEqualTo("still readable");
        }
    }

    @Nested
    @DisplayName("4. anyOf")
    class AnyOfTests {

        @Test
        @DisplayName("completes with the value of the first input to complete")
        void firstWins() {
            CompletableFuture<String> slow = new CompletableFuture<>();
            CompletableFuture<String> fast = new CompletableFuture<>();

            CompletableFuture<Object> first = CompletableFuture.anyOf(slow, fast);
            fast.complete("fast answer");

            assertThat(first.join()).isEqualTo("fast answer");
        }

        @Test
        @DisplayName("is not done while all inputs are pending")
        void pendingWhileNoneDone() {
            CompletableFuture<Object> first = CompletableFuture.anyOf(
                    new CompletableFuture<String>(), new CompletableFuture<String>());

            assertThat(first).isNotDone();
        }

        @Test
        @DisplayName("does not cancel the losers")
        void losersKeepRunning() {
            CompletableFuture<String> slow = new CompletableFuture<>();
            CompletableFuture<String> fast = new CompletableFuture<>();
            CompletableFuture<Object> first = CompletableFuture.anyOf(slow, fast);

            fast.complete("fast answer");

            assertThat(first).isDone();
            assertThat(slow).isNotDone();
            assertThat(slow.isCancelled()).isFalse();
        }

        @Test
        @DisplayName("has type Object — the caller must cast")
        void resultNeedsCast() {
            CompletableFuture<Object> first = CompletableFuture.anyOf(CompletableFuture.completedFuture("text"));

            String value = (String) first.join();

            assertThat(value).isEqualTo("text");
        }
    }

    @Nested
    @DisplayName("5. anyOf pitfall")
    class AnyOfPitfallTests {

        @Test
        @DisplayName("an early failure wins over a later success")
        void failureWins() {
            CompletableFuture<String> failsFast = ComposingDemo.failed(new IllegalStateException("replica 1 down"));
            CompletableFuture<String> succeedsLater = new CompletableFuture<>();

            CompletableFuture<Object> first = CompletableFuture.anyOf(failsFast, succeedsLater);
            succeedsLater.complete("replica 2 answer");

            assertThat(first).isCompletedExceptionally();
        }

        @Test
        @DisplayName("the late success itself is unaffected")
        void lateSuccessStillSucceeds() {
            CompletableFuture<String> failsFast = ComposingDemo.failed(new IllegalStateException("replica 1 down"));
            CompletableFuture<String> succeedsLater = new CompletableFuture<>();
            CompletableFuture.anyOf(failsFast, succeedsLater);

            succeedsLater.complete("replica 2 answer");

            assertThat(succeedsLater.join()).isEqualTo("replica 2 answer");
        }
    }

    @Nested
    @DisplayName("6. applyToEither / acceptEither")
    class EitherTests {

        @Test
        @DisplayName("applyToEither transforms the value of whichever future completes first")
        void applyToEither() {
            CompletableFuture<String> primary = new CompletableFuture<>();
            CompletableFuture<String> backup = new CompletableFuture<>();

            CompletableFuture<String> shouted = primary.applyToEither(backup, String::toUpperCase);
            backup.complete("backup answer");

            assertThat(shouted.join()).isEqualTo("BACKUP ANSWER");
        }

        @Test
        @DisplayName("acceptEither consumes the value of whichever future completes first")
        void acceptEither() {
            CompletableFuture<String> primary = new CompletableFuture<>();
            CompletableFuture<String> backup = new CompletableFuture<>();
            StringBuilder seen = new StringBuilder();

            CompletableFuture<Void> consumed = primary.acceptEither(backup, seen::append);
            primary.complete("primary answer");
            consumed.join();

            assertThat(seen).hasToString("primary answer");
        }

        @Test
        @DisplayName("an early failure of either future wins as well")
        void failureWinsToo() {
            CompletableFuture<String> primary = new CompletableFuture<>();
            CompletableFuture<String> backup = new CompletableFuture<>();

            CompletableFuture<String> shouted = primary.applyToEither(backup, String::toUpperCase);
            primary.completeExceptionally(new IllegalStateException("primary down"));
            backup.complete("backup answer");

            assertThat(shouted).isCompletedExceptionally();
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
                AllOfAnyOfDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }

            assertThat(captured.toString()).contains("---").contains("winner");
        }
    }
}
