package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ThreadLifecycleDemo — API contracts")
class ThreadLifecycleDemoTest {

    @Nested
    @DisplayName("1. NEW and TERMINATED")
    class NewAndTerminated {

        @Test
        @DisplayName("A constructed thread is NEW and not alive")
        void newState() {
            Thread t = new Thread(() -> { });
            assertThat(t.getState()).isEqualTo(Thread.State.NEW);
            assertThat(t.isAlive()).isFalse();
        }

        @Test
        @DisplayName("After join() the thread is TERMINATED and not alive")
        void terminatedState() throws InterruptedException {
            Thread t = new Thread(() -> { });
            t.start();
            t.join();
            assertThat(t.getState()).isEqualTo(Thread.State.TERMINATED);
            assertThat(t.isAlive()).isFalse();
        }

        @Test
        @DisplayName("A thread that dies with an exception is also TERMINATED")
        void terminatedByException() throws InterruptedException {
            Thread t = new Thread(() -> {
                throw new IllegalStateException("boom");
            });
            t.setUncaughtExceptionHandler((th, ex) -> { });
            t.start();
            t.join();
            assertThat(t.getState()).isEqualTo(Thread.State.TERMINATED);
        }
    }

    @Nested
    @DisplayName("2. RUNNABLE")
    class RunnableState {

        @Test
        @DisplayName("A spinning thread is RUNNABLE and alive")
        void spinningIsRunnable() throws InterruptedException {
            AtomicBoolean stop = new AtomicBoolean(false);
            Thread t = new Thread(() -> {
                while (!stop.get()) {
                    Thread.yield();
                }
            });
            t.start();
            try {
                assertThat(ThreadLifecycleDemo.awaitState(t, Thread.State.RUNNABLE)).isEqualTo(Thread.State.RUNNABLE);
                assertThat(t.isAlive()).isTrue();
            } finally {
                stop.set(true);
                t.join();
            }
        }
    }

    @Nested
    @DisplayName("3. TIMED_WAITING")
    class TimedWaiting {

        @Test
        @DisplayName("A sleeping thread is TIMED_WAITING")
        void sleepingState() throws InterruptedException {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {
                    // cleanup
                }
            });
            t.start();
            try {
                assertThat(ThreadLifecycleDemo.awaitState(t, Thread.State.TIMED_WAITING))
                        .isEqualTo(Thread.State.TIMED_WAITING);
            } finally {
                t.interrupt();
                t.join();
            }
        }

        @Test
        @DisplayName("wait(millis) is also TIMED_WAITING")
        void timedWaitState() throws InterruptedException {
            Object lock = new Object();
            Thread t = new Thread(() -> {
                synchronized (lock) {
                    try {
                        lock.wait(10_000);
                    } catch (InterruptedException ignored) {
                        // cleanup
                    }
                }
            });
            t.start();
            try {
                assertThat(ThreadLifecycleDemo.awaitState(t, Thread.State.TIMED_WAITING))
                        .isEqualTo(Thread.State.TIMED_WAITING);
            } finally {
                t.interrupt();
                t.join();
            }
        }
    }

    @Nested
    @DisplayName("4. WAITING")
    class Waiting {

        @Test
        @DisplayName("Object.wait() without timeout is WAITING and ends after notifyAll()")
        void waitState() throws InterruptedException {
            Object lock = new Object();
            Thread t = new Thread(() -> {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ignored) {
                        // not used
                    }
                }
            });
            t.start();
            assertThat(ThreadLifecycleDemo.awaitState(t, Thread.State.WAITING)).isEqualTo(Thread.State.WAITING);
            synchronized (lock) {
                lock.notifyAll();
            }
            t.join(5_000);
            assertThat(t.getState()).isEqualTo(Thread.State.TERMINATED);
        }

        @Test
        @DisplayName("join() on another live thread is WAITING")
        void joinIsWaiting() throws InterruptedException {
            Thread target = new Thread(() -> {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {
                    // cleanup
                }
            });
            Thread joiner = new Thread(() -> {
                try {
                    target.join();
                } catch (InterruptedException ignored) {
                    // cleanup
                }
            });
            target.start();
            joiner.start();
            try {
                assertThat(ThreadLifecycleDemo.awaitState(joiner, Thread.State.WAITING))
                        .isEqualTo(Thread.State.WAITING);
            } finally {
                target.interrupt();
                joiner.join();
                target.join();
            }
        }

        @Test
        @DisplayName("wait() without owning the monitor throws IllegalMonitorStateException")
        void waitNeedsMonitor() {
            assertThatThrownBy(() -> new Object().wait()).isInstanceOf(IllegalMonitorStateException.class);
        }
    }

    @Nested
    @DisplayName("5. BLOCKED")
    class Blocked {

        @Test
        @DisplayName("A thread waiting to enter a held monitor is BLOCKED")
        void blockedState() throws InterruptedException {
            Object lock = new Object();
            Thread t;
            synchronized (lock) {
                t = new Thread(() -> {
                    synchronized (lock) {
                        // enters once the test releases the monitor
                    }
                });
                t.start();
                assertThat(ThreadLifecycleDemo.awaitState(t, Thread.State.BLOCKED)).isEqualTo(Thread.State.BLOCKED);
            }
            t.join(5_000);
            assertThat(t.getState()).isEqualTo(Thread.State.TERMINATED);
        }
    }

    @Nested
    @DisplayName("6. No restart")
    class NoRestart {

        @Test
        @DisplayName("start() on a TERMINATED thread throws IllegalThreadStateException")
        void secondStartFails() throws InterruptedException {
            Thread t = new Thread(() -> { });
            t.start();
            t.join();
            assertThatThrownBy(t::start).isInstanceOf(IllegalThreadStateException.class);
        }

        @Test
        @DisplayName("Running the same Runnable on a new Thread works")
        void newThreadForSameTask() throws InterruptedException {
            AtomicBoolean ran = new AtomicBoolean(false);
            Runnable task = () -> ran.set(true);
            Thread first = new Thread(task);
            first.start();
            first.join();
            ran.set(false);
            Thread second = new Thread(task);
            second.start();
            second.join();
            assertThat(ran.get()).isTrue();
        }
    }
}
