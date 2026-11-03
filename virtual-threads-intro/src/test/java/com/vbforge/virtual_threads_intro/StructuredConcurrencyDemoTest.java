package com.vbforge.virtual_threads_intro;

import com.vbforge.virtual_threads_intro.StructuredConcurrencyDemo.TaskScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StructuredConcurrencyDemo — API contracts")
class StructuredConcurrencyDemoTest {

    @Nested
    @DisplayName("1. A block that owns its tasks")
    class ExecutorLifetime {

        @Test
        @DisplayName("try-with-resources: every task is finished when the block ends")
        void tasksFinishedAtEndOfBlock() {
            AtomicInteger finished = new AtomicInteger();

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < 5; i++) {
                    executor.submit(() -> {
                        Thread.sleep(30);
                        return finished.incrementAndGet();
                    });
                }
            }

            assertThat(finished.get()).isEqualTo(5);
        }

        @Test
        @DisplayName("an executor that was closed rejects new tasks")
        void closedExecutorRejectsTasks() {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            executor.close();

            assertThatThrownBy(() -> executor.submit(() -> "late")).isInstanceOf(RejectedExecutionException.class);
        }
    }

    @Nested
    @DisplayName("2. Fork and join")
    class ForkAndJoin {

        @Test
        @DisplayName("results are available through resultNow() after joinAll()")
        void resultsAfterJoin() throws Exception {
            try (TaskScope scope = new TaskScope()) {
                Future<String> user = scope.fork(() -> {
                    Thread.sleep(30);
                    return "alice";
                });
                Future<Integer> orders = scope.fork(() -> 3);

                scope.joinAll();

                assertThat(user.resultNow()).isEqualTo("alice");
                assertThat(orders.resultNow()).isEqualTo(3);
            }
        }

        @Test
        @DisplayName("subtasks run on virtual threads")
        void subtasksAreVirtual() throws Exception {
            try (TaskScope scope = new TaskScope()) {
                Future<Boolean> virtual = scope.fork(() -> Thread.currentThread().isVirtual());

                scope.joinAll();

                assertThat(virtual.resultNow()).isTrue();
            }
        }

        @Test
        @DisplayName("subtasks run in parallel: two tasks that wait for each other both complete")
        void subtasksRunInParallel() throws Exception {
            CountDownLatch bothArrived = new CountDownLatch(2);

            try (TaskScope scope = new TaskScope()) {
                Future<Boolean> a = scope.fork(() -> {
                    bothArrived.countDown();
                    return bothArrived.await(10, TimeUnit.SECONDS);
                });
                Future<Boolean> b = scope.fork(() -> {
                    bothArrived.countDown();
                    return bothArrived.await(10, TimeUnit.SECONDS);
                });

                scope.joinAll();

                assertThat(a.resultNow()).isTrue();
                assertThat(b.resultNow()).isTrue();
            }
        }

        @Test
        @DisplayName("joinAll() with nothing forked returns immediately")
        void emptyScope() throws Exception {
            try (TaskScope scope = new TaskScope()) {
                scope.joinAll();
            }
        }

        @Test
        @DisplayName("fork after close() is rejected")
        void forkAfterCloseIsRejected() {
            TaskScope scope = new TaskScope();
            scope.close();

            assertThatThrownBy(() -> scope.fork(() -> "late")).isInstanceOf(RejectedExecutionException.class);
        }
    }

    @Nested
    @DisplayName("3. Fail fast")
    class FailFast {

        @Test
        @DisplayName("first failure is thrown as ExecutionException with the original cause")
        void failureIsPropagated() {
            assertThatThrownBy(() -> {
                try (TaskScope scope = new TaskScope()) {
                    scope.fork(() -> {
                        throw new IllegalStateException("boom");
                    });
                    scope.joinAll();
                }
            })
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("boom");
        }

        @Test
        @DisplayName("a failure cancels a slow sibling, and the sibling has ended when the scope exits")
        void failureCancelsSibling() {
            AtomicBoolean slowInterrupted = new AtomicBoolean();
            CountDownLatch slowStarted = new CountDownLatch(1);

            assertThatThrownBy(() -> {
                try (TaskScope scope = new TaskScope()) {
                    scope.fork(() -> {
                        slowStarted.countDown();
                        try {
                            Thread.sleep(60_000);
                        } catch (InterruptedException e) {
                            slowInterrupted.set(true);
                            throw e;
                        }
                        return "slow";
                    });
                    scope.fork(() -> {
                        slowStarted.await();
                        throw new IllegalStateException("boom");
                    });
                    scope.joinAll();
                }
            }).isInstanceOf(ExecutionException.class);

            assertThat(slowInterrupted.get()).isTrue();
        }

        @Test
        @DisplayName("leaving the scope without joinAll() cancels running subtasks")
        void closeCancelsRunningSubtasks() throws Exception {
            AtomicBoolean interrupted = new AtomicBoolean();
            CountDownLatch started = new CountDownLatch(1);

            try (TaskScope scope = new TaskScope()) {
                scope.fork(() -> {
                    started.countDown();
                    try {
                        Thread.sleep(60_000);
                    } catch (InterruptedException e) {
                        interrupted.set(true);
                        throw e;
                    }
                    return "never";
                });
                started.await();
            }

            assertThat(interrupted.get()).isTrue();
        }

        @Test
        @DisplayName("an interrupted sleep reports InterruptedException and clears the interrupt flag")
        void interruptionContract() throws Exception {
            AtomicBoolean flagAfterCatch = new AtomicBoolean(true);
            AtomicBoolean caught = new AtomicBoolean();
            Thread t = Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(60_000);
                } catch (InterruptedException e) {
                    caught.set(true);
                    flagAfterCatch.set(Thread.currentThread().isInterrupted());
                }
            });

            t.interrupt();
            t.join();

            assertThat(caught.get()).isTrue();
            assertThat(flagAfterCatch.get()).isFalse();
        }
    }
}
