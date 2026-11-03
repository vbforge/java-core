package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ThreadBasicsTasks — your implementations")
class ThreadBasicsTasksTest {

    private ThreadBasicsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ThreadBasicsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement startNamedThread()")
    @DisplayName("startNamedThread")
    class StartNamedThreadTest {

        @Test
        @DisplayName("Returned thread has the requested name and ran the task")
        void happyPath() throws InterruptedException {
            AtomicBoolean ran = new AtomicBoolean(false);
            Thread t = tasks.startNamedThread("worker-7", () -> ran.set(true));
            t.join(5_000);
            assertThat(t.getName()).isEqualTo("worker-7");
            assertThat(ran.get()).isTrue();
        }

        @Test
        @DisplayName("Task runs on the returned thread, not on the caller")
        void runsOnReturnedThread() throws InterruptedException {
            AtomicReference<Thread> runner = new AtomicReference<>();
            Thread t = tasks.startNamedThread("x", () -> runner.set(Thread.currentThread()));
            t.join(5_000);
            assertThat(runner.get()).isSameAs(t).isNotSameAs(Thread.currentThread());
        }

        @Test
        @DisplayName("Thread has already been started (starting again fails)")
        void alreadyStarted() throws InterruptedException {
            Thread t = tasks.startNamedThread("y", () -> { });
            t.join(5_000);
            assertThatThrownBy(t::start)
                    .isInstanceOf(IllegalThreadStateException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runOnNewThread()")
    @DisplayName("runOnNewThread")
    class RunOnNewThreadTest {

        @Test
        @DisplayName("Returns the supplier's value")
        void happyPath() throws InterruptedException {
            assertThat(tasks.runOnNewThread(() -> "hi")).isEqualTo("hi");
        }

        @Test
        @DisplayName("Supplier runs on a different thread than the caller")
        void differentThread() throws InterruptedException {
            String name = tasks.runOnNewThread(() -> Thread.currentThread().getName());
            assertThat(name).isNotEqualTo(Thread.currentThread().getName());
        }

        @Test
        @DisplayName("Null result is returned as null")
        void nullResult() throws InterruptedException {
            assertThat(tasks.<String>runOnNewThread(() -> null)).isNull();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement parallelSquares()")
    @DisplayName("parallelSquares")
    class ParallelSquaresTest {

        @Test
        @DisplayName("[1, 2, 3, 4] → [1, 4, 9, 16]")
        void happyPath() throws InterruptedException {
            assertThat(tasks.parallelSquares(Arrays.asList(1, 2, 3, 4))).containsExactly(1, 4, 9, 16);
        }

        @Test
        @DisplayName("Empty list → empty list")
        void empty() throws InterruptedException {
            assertThat(tasks.parallelSquares(Collections.<Integer>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("Negative number is squared to a positive one")
        void negative() throws InterruptedException {
            assertThat(tasks.parallelSquares(Collections.singletonList(-3))).containsExactly(9);
        }

        @Test
        @DisplayName("Order of the input is preserved for 200 elements")
        void orderPreserved() throws InterruptedException {
            Integer[] in = new Integer[200];
            Integer[] expected = new Integer[200];
            for (int i = 0; i < in.length; i++) {
                in[i] = i;
                expected[i] = i * i;
            }
            List<Integer> out = tasks.parallelSquares(Arrays.asList(in));
            assertThat(out).containsExactly(expected);
        }

        @Test
        @DisplayName("Input list is not modified")
        void inputUntouched() throws InterruptedException {
            List<Integer> in = Arrays.asList(2, 3);
            tasks.parallelSquares(in);
            assertThat(in).containsExactly(2, 3);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement startCancellableCounter()")
    @DisplayName("startCancellableCounter")
    class StartCancellableCounterTest {

        @Test
        @DisplayName("Counter grows, then thread ends after interrupt()")
        void happyPath() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger();
            Thread t = tasks.startCancellableCounter(counter);
            long deadline = System.nanoTime() + 5_000_000_000L;
            while (counter.get() == 0 && System.nanoTime() < deadline) {
                Thread.sleep(1);
            }
            assertThat(counter.get()).isGreaterThan(0);

            t.interrupt();
            t.join(5_000);
            assertThat(t.isAlive()).isFalse();
        }

        @Test
        @DisplayName("Counter stops changing once the thread has ended")
        void stopsCounting() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger();
            Thread t = tasks.startCancellableCounter(counter);
            Thread.sleep(30);
            t.interrupt();
            t.join(5_000);
            int atEnd = counter.get();
            Thread.sleep(30);
            assertThat(counter.get()).isEqualTo(atEnd);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement captureUncaught()")
    @DisplayName("captureUncaught")
    class CaptureUncaughtTest {

        @Test
        @DisplayName("Returns the exception that escaped run()")
        void happyPath() throws InterruptedException {
            IllegalStateException boom = new IllegalStateException("x");
            Throwable result = tasks.captureUncaught(() -> {
                throw boom;
            });
            assertThat(result).isSameAs(boom);
        }

        @Test
        @DisplayName("Returns null when the task completes normally")
        void noFailure() throws InterruptedException {
            assertThat(tasks.captureUncaught(() -> { })).isNull();
        }

        @Test
        @DisplayName("Works with an Error as well as an Exception")
        void errorToo() throws InterruptedException {
            AssertionError err = new AssertionError("e");
            assertThat(tasks.captureUncaught(() -> {
                throw err;
            })).isSameAs(err);
        }
    }
}
