package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ThreadLifecycleTasks — your implementations")
class ThreadLifecycleTasksTest {

    private ThreadLifecycleTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ThreadLifecycleTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement finishesWithin()")
    @DisplayName("finishesWithin")
    class FinishesWithinTest {

        @Test
        @DisplayName("Quick task with a generous timeout → true")
        void quickTask() throws InterruptedException {
            assertThat(tasks.finishesWithin(() -> { }, 5_000)).isTrue();
        }

        @Test
        @DisplayName("Slow task with a short timeout → false")
        void slowTask() throws InterruptedException {
            boolean result = tasks.finishesWithin(() -> {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ignored) {
                    // test task ends early if interrupted
                }
            }, 20);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("The task really runs (side effect visible after a successful wait)")
        void sideEffect() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger();
            assertThat(tasks.finishesWithin(counter::incrementAndGet, 5_000)).isTrue();
            assertThat(counter.get()).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement observeStateWhileSleeping()")
    @DisplayName("observeStateWhileSleeping")
    class ObserveStateWhileSleepingTest {

        @Test
        @DisplayName("Sleeping thread is TIMED_WAITING")
        void happyPath() throws InterruptedException {
            assertThat(tasks.observeStateWhileSleeping()).isEqualTo(Thread.State.TIMED_WAITING);
        }

        @Test
        @DisplayName("Method returns quickly (the thread is woken, not waited out)")
        void returnsQuickly() throws InterruptedException {
            long start = System.nanoTime();
            tasks.observeStateWhileSleeping();
            long millis = (System.nanoTime() - start) / 1_000_000;
            assertThat(millis).isLessThan(5_000);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement observeStateWhileWaiting()")
    @DisplayName("observeStateWhileWaiting")
    class ObserveStateWhileWaitingTest {

        @Test
        @DisplayName("Thread in wait() without timeout is WAITING")
        void happyPath() throws InterruptedException {
            assertThat(tasks.observeStateWhileWaiting(new Object())).isEqualTo(Thread.State.WAITING);
        }

        @Test
        @DisplayName("Works with any lock object, and the caller does not need to own it")
        void anyLock() throws InterruptedException {
            assertThat(tasks.observeStateWhileWaiting("a string used as a lock")).isEqualTo(Thread.State.WAITING);
        }

        @Test
        @DisplayName("Method returns (the waiting thread is woken up and joined)")
        void returns() throws InterruptedException {
            Object lock = new Object();
            long start = System.nanoTime();
            tasks.observeStateWhileWaiting(lock);
            assertThat((System.nanoTime() - start) / 1_000_000).isLessThan(5_000);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement observeStateWhileBlocked()")
    @DisplayName("observeStateWhileBlocked")
    class ObserveStateWhileBlockedTest {

        @Test
        @DisplayName("Thread contending for a held monitor is BLOCKED")
        void happyPath() throws InterruptedException {
            assertThat(tasks.observeStateWhileBlocked()).isEqualTo(Thread.State.BLOCKED);
        }

        @Test
        @DisplayName("Can be called repeatedly (no leaked locks)")
        void repeatable() throws InterruptedException {
            for (int i = 0; i < 3; i++) {
                assertThat(tasks.observeStateWhileBlocked()).isEqualTo(Thread.State.BLOCKED);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement tryRestart()")
    @DisplayName("tryRestart")
    class TryRestartTest {

        @Test
        @DisplayName("Second start() never succeeds → false")
        void happyPath() throws InterruptedException {
            assertThat(tasks.tryRestart(() -> { })).isFalse();
        }

        @Test
        @DisplayName("The task has run exactly once")
        void runsOnce() throws InterruptedException {
            AtomicInteger runs = new AtomicInteger();
            tasks.tryRestart(runs::incrementAndGet);
            assertThat(runs.get()).isEqualTo(1);
        }
    }
}
