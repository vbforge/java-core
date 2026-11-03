package com.vbforge.multithreading_advanced;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("CallableFutureDemo — API contracts")
@Timeout(value = 20, unit = TimeUnit.SECONDS)
class CallableFutureDemoTest {

    private static Callable<String> after(final long millis, final String value) {
        return () -> {
            Thread.sleep(millis);
            return value;
        };
    }

    @Nested
    @DisplayName("1. Callable vs Runnable")
    class CallableVsRunnable {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(CallableFutureDemo::callableVsRunnableDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("submit(Callable) delivers the returned value")
        void callableDeliversValue() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                Callable<Integer> answer = () -> 6 * 7;
                assertThat(pool.submit(answer).get()).isEqualTo(42);
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("submit(Runnable) yields null; submit(Runnable, T) yields T")
        void runnableResults() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                Runnable noOp = () -> { };
                assertThat(pool.submit(noOp).get()).isNull();
                assertThat(pool.submit(noOp, "fixed result").get()).isEqualTo("fixed result");
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("A Callable may throw a checked exception")
        void callableMayThrowChecked() {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                Callable<String> risky = () -> {
                    throw new Exception("checked");
                };
                Future<String> future = pool.submit(risky);
                assertThatThrownBy(future::get)
                        .isInstanceOf(ExecutionException.class)
                        .hasCauseExactlyInstanceOf(Exception.class)
                        .hasRootCauseMessage("checked");
            } finally {
                pool.shutdownNow();
            }
        }
    }

    @Nested
    @DisplayName("2. Future.get() and timeouts")
    class FutureGet {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(CallableFutureDemo::futureGetDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("isDone is false while the task runs, true afterwards")
        void isDoneTracksCompletion() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            CountDownLatch gate = new CountDownLatch(1);
            try {
                Future<String> future = pool.submit(() -> {
                    gate.await();
                    return "done";
                });
                assertThat(future.isDone()).isFalse();
                gate.countDown();
                assertThat(future.get()).isEqualTo("done");
                assertThat(future.isDone()).isTrue();
            } finally {
                gate.countDown();
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("get(timeout) throws TimeoutException but does not stop the task")
        void timeoutDoesNotCancel() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            CountDownLatch gate = new CountDownLatch(1);
            try {
                Future<String> future = pool.submit(() -> {
                    gate.await();
                    return "still alive";
                });
                assertThatThrownBy(() -> future.get(50, TimeUnit.MILLISECONDS))
                        .isInstanceOf(TimeoutException.class);
                assertThat(future.isCancelled()).isFalse();
                assertThat(future.isDone()).isFalse();

                gate.countDown();
                assertThat(future.get()).isEqualTo("still alive");
            } finally {
                gate.countDown();
                pool.shutdownNow();
            }
        }
    }

    @Nested
    @DisplayName("3. Exceptions: ExecutionException, submit vs execute")
    class Exceptions {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(CallableFutureDemo::exceptionHandlingDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("get() wraps the task's exception in an ExecutionException")
        void getWrapsException() {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                Callable<String> failing = () -> {
                    throw new IllegalStateException("boom");
                };
                Future<String> future = pool.submit(failing);
                assertThatThrownBy(future::get)
                        .isInstanceOf(ExecutionException.class)
                        .hasCauseExactlyInstanceOf(IllegalStateException.class)
                        .hasRootCauseMessage("boom");
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("submit() swallows a failure until get() is called; the future is still done")
        void submitSwallowsFailure() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                Future<?> future = pool.submit(() -> {
                    throw new IllegalStateException("nobody is listening");
                });
                // wait for completion without calling get()
                pool.shutdown();
                assertThat(pool.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
                assertThat(future.isDone()).isTrue();
                assertThat(future.isCancelled()).isFalse();
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("execute() hands the exception to the thread's uncaught-exception handler")
        void executeUsesUncaughtHandler() throws Exception {
            CountDownLatch handled = new CountDownLatch(1);
            AtomicReference<Throwable> seen = new AtomicReference<>();
            ThreadFactory factory = runnable -> {
                Thread thread = new Thread(runnable);
                thread.setUncaughtExceptionHandler((t, ex) -> {
                    seen.set(ex);
                    handled.countDown();
                });
                return thread;
            };
            ExecutorService pool = Executors.newSingleThreadExecutor(factory);
            try {
                pool.execute(() -> {
                    throw new IllegalArgumentException("thrown from execute()");
                });
                assertThat(handled.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(seen.get())
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("thrown from execute()");
            } finally {
                pool.shutdownNow();
            }
        }
    }

    @Nested
    @DisplayName("4. Cancellation")
    class Cancellation {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(CallableFutureDemo::cancellationDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("cancel(true) interrupts a running task and get() throws CancellationException")
        void cancelTrueInterrupts() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                CountDownLatch started = new CountDownLatch(1);
                CountDownLatch interrupted = new CountDownLatch(1);
                Future<?> future = pool.submit(() -> {
                    started.countDown();
                    try {
                        Thread.sleep(60_000);
                    } catch (InterruptedException e) {
                        interrupted.countDown();
                    }
                });
                assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

                assertThat(future.cancel(true)).isTrue();

                assertThat(interrupted.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(future.isCancelled()).isTrue();
                assertThat(future.isDone()).isTrue();
                assertThatThrownBy(future::get).isInstanceOf(CancellationException.class);
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("cancel(false) marks the future cancelled but lets the running task finish")
        void cancelFalseDoesNotInterrupt() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                CountDownLatch started = new CountDownLatch(1);
                CountDownLatch release = new CountDownLatch(1);
                CountDownLatch finishedNormally = new CountDownLatch(1);
                Future<?> future = pool.submit(() -> {
                    started.countDown();
                    try {
                        release.await();
                        finishedNormally.countDown();
                    } catch (InterruptedException e) {
                        // would mean we were interrupted — finishedNormally stays closed
                    }
                });
                assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

                assertThat(future.cancel(false)).isTrue();
                assertThat(future.isCancelled()).isTrue();

                release.countDown();
                assertThat(finishedNormally.await(5, TimeUnit.SECONDS)).isTrue();
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("cancel() on a finished task returns false")
        void cancelAfterCompletion() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                Future<String> future = pool.submit(() -> "done");
                assertThat(future.get()).isEqualTo("done");
                assertThat(future.cancel(true)).isFalse();
                assertThat(future.isCancelled()).isFalse();
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("A task cancelled before it starts never runs")
        void cancelBeforeStart() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            CountDownLatch release = new CountDownLatch(1);
            AtomicInteger runs = new AtomicInteger();
            try {
                pool.submit(() -> {
                    release.await();
                    return null;
                });
                Future<?> queued = pool.submit(runs::incrementAndGet);
                assertThat(queued.cancel(false)).isTrue();

                release.countDown();
                pool.shutdown();
                assertThat(pool.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
                assertThat(runs.get()).isZero();
            } finally {
                release.countDown();
                pool.shutdownNow();
            }
        }
    }

    @Nested
    @DisplayName("5. invokeAll and invokeAny")
    class InvokeAllAndAny {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(CallableFutureDemo::invokeAllAndAnyDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("invokeAll returns futures in input order, all of them done")
        void invokeAllKeepsInputOrder() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(3);
            try {
                List<Callable<String>> jobs = Arrays.asList(
                        after(100, "slow"), after(0, "fast"), after(50, "medium"));
                List<Future<String>> futures = pool.invokeAll(jobs);

                assertThat(futures).hasSize(3).allMatch(Future::isDone);
                assertThat(futures.get(0).get()).isEqualTo("slow");
                assertThat(futures.get(1).get()).isEqualTo("fast");
                assertThat(futures.get(2).get()).isEqualTo("medium");
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("invokeAll returns failed futures instead of throwing")
        void invokeAllDoesNotThrowOnFailure() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                Callable<String> failing = () -> {
                    throw new IllegalStateException("bad job");
                };
                List<Future<String>> futures = pool.invokeAll(Arrays.asList(after(0, "ok"), failing));

                assertThat(futures.get(0).get()).isEqualTo("ok");
                assertThatThrownBy(() -> futures.get(1).get())
                        .isInstanceOf(ExecutionException.class)
                        .hasRootCauseMessage("bad job");
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("invokeAny skips failed tasks and returns a successful result")
        void invokeAnySkipsFailures() throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                Callable<String> failing = () -> {
                    throw new IllegalStateException("mirror down");
                };
                String winner = pool.invokeAny(Arrays.asList(failing, after(50, "healthy mirror")));
                assertThat(winner).isEqualTo("healthy mirror");
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("invokeAny throws ExecutionException when every task fails")
        void invokeAnyAllFail() {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                Callable<String> failing = () -> {
                    throw new IllegalStateException("down");
                };
                assertThatThrownBy(() -> pool.invokeAny(Arrays.asList(failing, failing)))
                        .isInstanceOf(ExecutionException.class);
            } finally {
                pool.shutdownNow();
            }
        }
    }

    @Nested
    @DisplayName("6. FutureTask")
    class FutureTaskSection {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(CallableFutureDemo::futureTaskDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("A FutureTask run by a plain Thread delivers its result")
        void futureTaskOnPlainThread() throws Exception {
            FutureTask<Integer> task = new FutureTask<Integer>(() -> 1 + 2);
            new Thread(task).start();
            assertThat(task.get()).isEqualTo(3);
        }

        @Test
        @DisplayName("A FutureTask is both a Runnable and a Future")
        void futureTaskIsRunnableAndFuture() {
            FutureTask<Integer> task = new FutureTask<Integer>(() -> 1);
            assertThat(task).isInstanceOf(Runnable.class).isInstanceOf(Future.class);
        }

        @Test
        @DisplayName("A FutureTask runs its Callable at most once")
        void futureTaskRunsOnce() throws Exception {
            AtomicInteger calls = new AtomicInteger();
            FutureTask<Integer> task = new FutureTask<Integer>(calls::incrementAndGet);
            task.run();
            task.run();
            assertThat(task.get()).isEqualTo(1);
            assertThat(calls.get()).isEqualTo(1);
        }
    }
}
