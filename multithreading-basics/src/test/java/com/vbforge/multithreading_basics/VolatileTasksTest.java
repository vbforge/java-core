package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VolatileTasks — your implementations")
class VolatileTasksTest {

    private VolatileTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new VolatileTasks();
    }

    private static boolean isVolatile(String field) throws NoSuchFieldException {
        return Modifier.isVolatile(VolatileTasks.class.getDeclaredField(field).getModifiers());
    }

    @Nested
    @Disabled("Remove @Disabled when you implement requestStop()")
    @DisplayName("requestStop")
    class RequestStopTest {

        @Test
        @DisplayName("A looping thread leaves runUntilStopped() after requestStop()")
        void stopsLoop() throws InterruptedException {
            AtomicLong iterations = new AtomicLong();
            Thread t = new Thread(() -> iterations.set(tasks.runUntilStopped()));
            t.setDaemon(true);
            t.start();
            Thread.sleep(20);
            tasks.requestStop();
            t.join(5_000);
            assertThat(t.isAlive()).isFalse();
        }

        @Test
        @DisplayName("The stopRequested field is declared volatile")
        void flagIsVolatile() throws NoSuchFieldException {
            assertThat(isVolatile("stopRequested")).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runUntilStopped()")
    @DisplayName("runUntilStopped")
    class RunUntilStoppedTest {

        @Test
        @DisplayName("Returns at least 1 iteration after being stopped")
        void atLeastOne() throws InterruptedException {
            AtomicLong iterations = new AtomicLong();
            Thread t = new Thread(() -> iterations.set(tasks.runUntilStopped()));
            t.setDaemon(true);
            t.start();
            Thread.sleep(20);
            tasks.requestStop();
            t.join(5_000);
            assertThat(t.isAlive()).isFalse();
            assertThat(iterations.get()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Stop requested before the loop starts → returns promptly with at least 1 iteration")
        void stopAlreadyRequested() {
            tasks.requestStop();
            assertThat(tasks.runUntilStopped()).isGreaterThanOrEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement publish()")
    @DisplayName("publish")
    class PublishTest {

        @Test
        @DisplayName("A spinning reader receives the published value")
        void readerReceives() throws InterruptedException {
            AtomicInteger received = new AtomicInteger(-1);
            Thread reader = new Thread(() -> received.set(tasks.awaitPayload()));
            reader.setDaemon(true);
            reader.start();
            Thread.sleep(20);
            tasks.publish(42);
            reader.join(5_000);
            assertThat(reader.isAlive()).isFalse();
            assertThat(received.get()).isEqualTo(42);
        }

        @Test
        @DisplayName("The ready field is declared volatile")
        void readyIsVolatile() throws NoSuchFieldException {
            assertThat(isVolatile("ready")).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement awaitPayload()")
    @DisplayName("awaitPayload")
    class AwaitPayloadTest {

        @Test
        @DisplayName("Value published earlier is returned immediately")
        void alreadyPublished() {
            tasks.publish(7);
            assertThat(tasks.awaitPayload()).isEqualTo(7);
        }

        @Test
        @DisplayName("Zero is a valid payload")
        void zeroPayload() {
            tasks.publish(0);
            assertThat(tasks.awaitPayload()).isZero();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countConcurrently()")
    @DisplayName("countConcurrently")
    class CountConcurrentlyTest {

        @Test
        @DisplayName("4 threads × 50 000 → 200 000")
        void happyPath() throws InterruptedException {
            assertThat(tasks.countConcurrently(4, 50_000)).isEqualTo(200_000);
        }

        @Test
        @DisplayName("perThread = 0 → 0")
        void zero() throws InterruptedException {
            assertThat(tasks.countConcurrently(1, 0)).isZero();
        }

        @Test
        @DisplayName("Single thread gives the plain product")
        void singleThread() throws InterruptedException {
            assertThat(tasks.countConcurrently(1, 1_000)).isEqualTo(1_000);
        }

        @Test
        @DisplayName("Result is exact on 8 threads")
        void eightThreads() throws InterruptedException {
            assertThat(tasks.countConcurrently(8, 25_000)).isEqualTo(200_000);
        }
    }
}
