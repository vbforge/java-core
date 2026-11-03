package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ThreadBasicsDemo — API contracts")
class ThreadBasicsDemoTest {

    private static void awaitState(Thread t, Thread.State expected) throws InterruptedException {
        long deadline = System.nanoTime() + 5_000_000_000L;
        while (t.getState() != expected && System.nanoTime() < deadline) {
            Thread.sleep(1);
        }
        assertThat(t.getState()).isEqualTo(expected);
    }

    @Nested
    @DisplayName("1. Extending Thread")
    class ExtendThread {

        @Test
        @DisplayName("Subclass run() executes on the thread with the given name")
        void runsOnNamedThread() throws InterruptedException {
            AtomicReference<String> seen = new AtomicReference<>();
            Thread t = new ThreadBasicsDemo.HelloThread("hello-test") {
                @Override
                public void run() {
                    seen.set(Thread.currentThread().getName());
                }
            };
            t.start();
            t.join();
            assertThat(seen.get()).isEqualTo("hello-test");
        }

        @Test
        @DisplayName("Constructor name is returned by getName()")
        void constructorName() {
            assertThat(new ThreadBasicsDemo.HelloThread("abc").getName()).isEqualTo("abc");
        }
    }

    @Nested
    @DisplayName("2. Implementing Runnable")
    class RunnableTasks {

        @Test
        @DisplayName("The same Runnable can be run by two different threads")
        void sameRunnableTwoThreads() throws InterruptedException {
            java.util.Set<String> names = java.util.Collections.synchronizedSet(new java.util.HashSet<String>());
            Runnable task = () -> names.add(Thread.currentThread().getName());
            Thread a = new Thread(task, "A");
            Thread b = new Thread(task, "B");
            a.start();
            b.start();
            a.join();
            b.join();
            assertThat(names).containsExactlyInAnyOrder("A", "B");
        }

        @Test
        @DisplayName("Runnable work does not run on the caller's thread")
        void notOnCaller() throws InterruptedException {
            AtomicReference<Thread> runner = new AtomicReference<>();
            Thread t = new Thread(() -> runner.set(Thread.currentThread()));
            t.start();
            t.join();
            assertThat(runner.get()).isSameAs(t).isNotSameAs(Thread.currentThread());
        }
    }

    @Nested
    @DisplayName("3. start() vs run()")
    class StartVsRun {

        @Test
        @DisplayName("run() executes on the calling thread")
        void runIsPlainCall() {
            AtomicReference<Thread> runner = new AtomicReference<>();
            new Thread(() -> runner.set(Thread.currentThread())).run();
            assertThat(runner.get()).isSameAs(Thread.currentThread());
        }

        @Test
        @DisplayName("start() executes on a different thread")
        void startUsesNewThread() throws InterruptedException {
            AtomicReference<Thread> runner = new AtomicReference<>();
            Thread t = new Thread(() -> runner.set(Thread.currentThread()));
            t.start();
            t.join();
            assertThat(runner.get()).isNotSameAs(Thread.currentThread());
        }

        @Test
        @DisplayName("A thread cannot be started twice")
        void secondStartFails() throws InterruptedException {
            Thread t = new Thread(() -> { });
            t.start();
            t.join();
            assertThatThrownBy(t::start).isInstanceOf(IllegalThreadStateException.class);
        }
    }

    @Nested
    @DisplayName("4. join()")
    class Join {

        @Test
        @DisplayName("After join() the worker is dead and its result is visible")
        void joinMakesResultVisible() throws InterruptedException {
            int[] result = new int[1];
            Thread worker = new Thread(() -> result[0] = 42);
            worker.start();
            worker.join();
            assertThat(worker.isAlive()).isFalse();
            assertThat(result[0]).isEqualTo(42);
        }

        @Test
        @DisplayName("join(millis) returns while a slower thread is still alive")
        void timedJoin() throws InterruptedException {
            Thread slow = new Thread(() -> {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {
                    // test cleanup interrupts us
                }
            });
            slow.start();
            slow.join(50);
            assertThat(slow.isAlive()).isTrue();
            slow.interrupt();
            slow.join();
        }
    }

    @Nested
    @DisplayName("5. Daemon threads")
    class Daemon {

        @Test
        @DisplayName("Threads are non-daemon by default when created by a non-daemon thread")
        void defaultNonDaemon() {
            assertThat(new Thread(() -> { }).isDaemon()).isFalse();
        }

        @Test
        @DisplayName("setDaemon(true) before start() is reflected by isDaemon()")
        void setBeforeStart() {
            Thread t = new Thread(() -> { });
            t.setDaemon(true);
            assertThat(t.isDaemon()).isTrue();
        }

        @Test
        @DisplayName("setDaemon() after start() throws IllegalThreadStateException")
        void setAfterStartFails() throws InterruptedException {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException ignored) {
                    // test cleanup interrupts us
                }
            });
            t.start();
            try {
                assertThatThrownBy(() -> t.setDaemon(true)).isInstanceOf(IllegalThreadStateException.class);
            } finally {
                t.interrupt();
                t.join();
            }
        }

        @Test
        @DisplayName("A thread inherits the daemon flag from its creator")
        void inheritsDaemonFlag() throws InterruptedException {
            AtomicReference<Boolean> childIsDaemon = new AtomicReference<>();
            Thread parent = new Thread(() -> childIsDaemon.set(new Thread(() -> { }).isDaemon()));
            parent.setDaemon(true);
            parent.start();
            parent.join();
            assertThat(childIsDaemon.get()).isTrue();
        }
    }

    @Nested
    @DisplayName("6. Interruption")
    class Interruption {

        @Test
        @DisplayName("interrupt() sets the flag; isInterrupted() keeps it, interrupted() clears it")
        void flagSemantics() {
            Thread.currentThread().interrupt();
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
            assertThat(Thread.interrupted()).isTrue();
            assertThat(Thread.interrupted()).isFalse();
        }

        @Test
        @DisplayName("sleep() throws InterruptedException and clears the flag")
        void sleepThrowsAndClears() throws InterruptedException {
            AtomicReference<Boolean> caught = new AtomicReference<>(false);
            AtomicReference<Boolean> flagAfter = new AtomicReference<>();
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                    caught.set(true);
                    flagAfter.set(Thread.currentThread().isInterrupted());
                }
            });
            t.start();
            awaitState(t, Thread.State.TIMED_WAITING);
            t.interrupt();
            t.join();
            assertThat(caught.get()).isTrue();
            assertThat(flagAfter.get()).isFalse();
        }

        @Test
        @DisplayName("Sleeping on an already-interrupted thread throws immediately")
        void sleepWithFlagSet() {
            Thread.currentThread().interrupt();
            try {
                assertThatThrownBy(() -> Thread.sleep(10_000)).isInstanceOf(InterruptedException.class);
            } finally {
                Thread.interrupted(); // make sure the flag never leaks into other tests
            }
        }
    }

    @Nested
    @DisplayName("7. Uncaught exceptions")
    class UncaughtExceptions {

        @Test
        @DisplayName("An exception in run() does not reach the caller of start()")
        void callerNeverSeesIt() throws InterruptedException {
            Thread t = new Thread(() -> {
                throw new IllegalStateException("boom");
            });
            t.setUncaughtExceptionHandler((th, ex) -> { });
            t.start();
            t.join();
            assertThat(t.isAlive()).isFalse();
        }

        @Test
        @DisplayName("UncaughtExceptionHandler receives the escaped exception")
        void handlerReceivesIt() throws InterruptedException {
            AtomicReference<Throwable> seen = new AtomicReference<>();
            Thread t = new Thread(() -> {
                throw new IllegalStateException("boom");
            });
            t.setUncaughtExceptionHandler((th, ex) -> seen.set(ex));
            t.start();
            t.join();
            assertThat(seen.get()).isInstanceOf(IllegalStateException.class).hasMessage("boom");
        }
    }
}
