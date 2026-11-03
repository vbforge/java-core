package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SynchronizationTasks — your implementations")
class SynchronizationTasksTest {

    private SynchronizationTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new SynchronizationTasks();
    }

    /** Starts a daemon thread (so a deadlocked solution cannot keep the JVM alive). */
    private static Thread daemon(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
        return t;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement incrementSafely()")
    @DisplayName("incrementSafely")
    class IncrementSafelyTest {

        @Test
        @DisplayName("Single thread: 5 increments → 5")
        void singleThread() {
            int[] c = {0};
            tasks.incrementSafely(c, 5);
            assertThat(c[0]).isEqualTo(5);
        }

        @Test
        @DisplayName("times = 0 leaves the counter unchanged")
        void zeroTimes() {
            int[] c = {7};
            tasks.incrementSafely(c, 0);
            assertThat(c[0]).isEqualTo(7);
        }

        @Test
        @DisplayName("4 threads × 50 000 increments on the same array → 200 000")
        void concurrent() throws InterruptedException {
            int[] c = {0};
            SynchronizationDemo.hammer(4, 1, () -> tasks.incrementSafely(c, 50_000));
            assertThat(c[0]).isEqualTo(200_000);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement transfer()")
    @DisplayName("transfer")
    class TransferTest {

        @Test
        @DisplayName("{1000, 1000}, move 100 from 0 to 1 → {900, 1100}")
        void happyPath() {
            long[] a = {1000, 1000};
            tasks.transfer(a, 0, 1, 100);
            assertThat(a).containsExactly(900, 1100);
        }

        @Test
        @DisplayName("Transfer of 0 changes nothing")
        void zeroAmount() {
            long[] a = {5, 6};
            tasks.transfer(a, 1, 0, 0);
            assertThat(a).containsExactly(5, 6);
        }

        @Test
        @DisplayName("Opposite transfers in two threads keep both balances at 1000")
        void concurrentOpposite() throws InterruptedException {
            long[] a = {1000, 1000};
            Thread t1 = new Thread(() -> {
                for (int i = 0; i < 20_000; i++) {
                    tasks.transfer(a, 0, 1, 1);
                }
            });
            Thread t2 = new Thread(() -> {
                for (int i = 0; i < 20_000; i++) {
                    tasks.transfer(a, 1, 0, 1);
                }
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            assertThat(a).containsExactly(1000, 1000);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement putValue()")
    @DisplayName("putValue")
    class PutValueTest {

        @Test
        @DisplayName("Stores the value in box[0]")
        void stores() {
            Object[] box = {null};
            tasks.putValue(box, "A");
            assertThat(box[0]).isEqualTo("A");
        }

        @Test
        @DisplayName("Can be called by a thread that owns no monitor (it must lock by itself)")
        void ownsMonitorItself() throws InterruptedException {
            Object[] box = {null};
            AtomicReference<Throwable> failure = new AtomicReference<>();
            Thread t = new Thread(() -> tasks.putValue(box, "B"));
            t.setUncaughtExceptionHandler((th, ex) -> failure.set(ex));
            t.start();
            t.join();
            assertThat(failure.get()).isNull();
            assertThat(box[0]).isEqualTo("B");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement takeValue()")
    @DisplayName("takeValue")
    class TakeValueTest {

        @Test
        @DisplayName("Value already present → returned immediately and box emptied")
        void alreadyThere() throws InterruptedException {
            Object[] box = {"A"};
            assertThat(tasks.takeValue(box)).isEqualTo("A");
            assertThat(box[0]).isNull();
        }

        @Test
        @DisplayName("Empty box → blocks until putValue() delivers")
        void blocksUntilPut() throws InterruptedException {
            Object[] box = {null};
            AtomicReference<Object> received = new AtomicReference<>();
            Thread consumer = new Thread(() -> {
                try {
                    received.set(tasks.takeValue(box));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumer.setDaemon(true);
            consumer.start();
            assertThat(ThreadLifecycleDemo.awaitState(consumer, Thread.State.WAITING)).isEqualTo(Thread.State.WAITING);
            assertThat(received.get()).isNull();

            tasks.putValue(box, "B");
            consumer.join(5_000);
            assertThat(received.get()).isEqualTo("B");
            assertThat(box[0]).isNull();
        }

        @Test
        @DisplayName("Interrupting a waiting taker ends it with InterruptedException")
        void interruptible() throws InterruptedException {
            Object[] box = {null};
            AtomicReference<Throwable> failure = new AtomicReference<>();
            Thread consumer = new Thread(() -> {
                try {
                    tasks.takeValue(box);
                } catch (InterruptedException e) {
                    failure.set(e);
                }
            });
            consumer.setDaemon(true);
            consumer.start();
            assertThat(ThreadLifecycleDemo.awaitState(consumer, Thread.State.WAITING)).isEqualTo(Thread.State.WAITING);
            consumer.interrupt();
            consumer.join(5_000);
            assertThat(failure.get()).isInstanceOf(InterruptedException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement transferOrdered()")
    @DisplayName("transferOrdered")
    class TransferOrderedTest {

        @Test
        @DisplayName("{1000, 1000}, move 100 from 0 to 1 → {900, 1100}")
        void happyPath() {
            long[] a = {1000, 1000};
            Object[] locks = {new Object(), new Object()};
            tasks.transferOrdered(a, locks, 0, 1, 100);
            assertThat(a).containsExactly(900, 1100);
        }

        @Test
        @DisplayName("Works in both directions")
        void reverse() {
            long[] a = {1000, 1000};
            Object[] locks = {new Object(), new Object()};
            tasks.transferOrdered(a, locks, 1, 0, 250);
            assertThat(a).containsExactly(1250, 750);
        }

        @Test
        @DisplayName("Opposite transfers in two threads finish (no deadlock) and keep the total")
        void noDeadlock() throws InterruptedException {
            long[] a = {1000, 1000};
            Object[] locks = {new Object(), new Object()};
            Thread t1 = daemon(() -> {
                for (int i = 0; i < 20_000; i++) {
                    tasks.transferOrdered(a, locks, 0, 1, 1);
                }
            });
            Thread t2 = daemon(() -> {
                for (int i = 0; i < 20_000; i++) {
                    tasks.transferOrdered(a, locks, 1, 0, 1);
                }
            });
            t1.join(20_000);
            t2.join(20_000);
            assertThat(t1.isAlive()).as("thread 1 deadlocked").isFalse();
            assertThat(t2.isAlive()).as("thread 2 deadlocked").isFalse();
            assertThat(a).containsExactly(1000, 1000);
        }
    }
}
