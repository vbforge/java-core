package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SynchronizationDemo — API contracts")
class SynchronizationDemoTest {

    private static final int THREADS = 4;
    private static final int TIMES = 50_000;
    private static final int EXPECTED = THREADS * TIMES;

    @Nested
    @DisplayName("1. Race condition")
    class RaceCondition {

        @Test
        @DisplayName("Unsynchronized counter never exceeds the expected total (updates can only be lost)")
        void neverMoreThanExpected() throws InterruptedException {
            SynchronizationDemo.UnsafeCounter counter = new SynchronizationDemo.UnsafeCounter();
            SynchronizationDemo.hammer(THREADS, TIMES, counter::increment);
            assertThat(counter.get()).isBetween(1, EXPECTED);
        }

        @Test
        @DisplayName("hammer() with a single thread is exact — the race needs 2+ threads")
        void singleThreadIsExact() throws InterruptedException {
            SynchronizationDemo.UnsafeCounter counter = new SynchronizationDemo.UnsafeCounter();
            SynchronizationDemo.hammer(1, TIMES, counter::increment);
            assertThat(counter.get()).isEqualTo(TIMES);
        }
    }

    @Nested
    @DisplayName("2. synchronized method")
    class SynchronizedMethod {

        @Test
        @DisplayName("No update is lost")
        void exactTotal() throws InterruptedException {
            SynchronizationDemo.SynchronizedCounter counter = new SynchronizationDemo.SynchronizedCounter();
            SynchronizationDemo.hammer(THREADS, TIMES, counter::increment);
            assertThat(counter.get()).isEqualTo(EXPECTED);
        }

        @Test
        @DisplayName("Holding the monitor makes Thread.holdsLock() true, outside it is false")
        void holdsLock() {
            Object lock = new Object();
            assertThat(Thread.holdsLock(lock)).isFalse();
            synchronized (lock) {
                assertThat(Thread.holdsLock(lock)).isTrue();
            }
            assertThat(Thread.holdsLock(lock)).isFalse();
        }
    }

    @Nested
    @DisplayName("3. synchronized block with a private lock")
    class SynchronizedBlock {

        @Test
        @DisplayName("No update is lost")
        void exactTotal() throws InterruptedException {
            SynchronizationDemo.LockObjectCounter counter = new SynchronizationDemo.LockObjectCounter();
            SynchronizationDemo.hammer(THREADS, TIMES, counter::increment);
            assertThat(counter.get()).isEqualTo(EXPECTED);
        }
    }

    @Nested
    @DisplayName("4. Instance lock vs class lock")
    class InstanceVsClassLock {

        @Test
        @DisplayName("A static synchronized method is not blocked by a held instance lock")
        void differentMonitors() throws InterruptedException {
            SynchronizationDemo.Holder holder = new SynchronizationDemo.Holder();
            CountDownLatch entered = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);
            Thread t = new Thread(() -> {
                try {
                    holder.holdInstanceLock(entered, release);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            t.start();
            try {
                assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(SynchronizationDemo.Holder.staticMethod()).isEqualTo("static-ok");
            } finally {
                release.countDown();
                t.join();
            }
        }

        @Test
        @DisplayName("A second thread cannot enter another synchronized method of the same instance")
        void sameInstanceIsExclusive() throws InterruptedException {
            SynchronizationDemo.Holder holder = new SynchronizationDemo.Holder();
            CountDownLatch entered = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);
            Thread owner = new Thread(() -> {
                try {
                    holder.holdInstanceLock(entered, release);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            owner.start();
            assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();

            Thread contender = new Thread(holder::inner);
            contender.start();
            try {
                assertThat(ThreadLifecycleDemo.awaitState(contender, Thread.State.BLOCKED))
                        .isEqualTo(Thread.State.BLOCKED);
            } finally {
                release.countDown();
                owner.join();
                contender.join();
            }
        }
    }

    @Nested
    @DisplayName("5. Reentrancy")
    class Reentrancy {

        @Test
        @DisplayName("A synchronized method can call another synchronized method of the same object")
        void noSelfDeadlock() throws InterruptedException {
            AtomicReference<Integer> result = new AtomicReference<>();
            Thread t = new Thread(() -> result.set(new SynchronizationDemo.Holder().outer()));
            t.setDaemon(true);
            t.start();
            t.join(5_000);
            assertThat(t.isAlive()).isFalse();
            assertThat(result.get()).isEqualTo(42);
        }
    }

    @Nested
    @DisplayName("6. wait / notifyAll")
    class WaitNotify {

        @Test
        @DisplayName("take() blocks until put() delivers a value")
        void takeWaitsForPut() throws InterruptedException {
            SynchronizationDemo.Mailbox box = new SynchronizationDemo.Mailbox();
            AtomicReference<String> received = new AtomicReference<>();
            Thread consumer = new Thread(() -> {
                try {
                    received.set(box.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumer.start();
            assertThat(ThreadLifecycleDemo.awaitState(consumer, Thread.State.WAITING)).isEqualTo(Thread.State.WAITING);
            assertThat(received.get()).isNull();

            box.put("hello");
            consumer.join(5_000);
            assertThat(received.get()).isEqualTo("hello");
        }

        @Test
        @DisplayName("put() on a full mailbox waits until the value was taken")
        void putWaitsForTake() throws Exception {
            SynchronizationDemo.Mailbox box = new SynchronizationDemo.Mailbox();
            box.put("first");
            Thread producer = new Thread(() -> {
                try {
                    box.put("second");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            producer.start();
            assertThat(ThreadLifecycleDemo.awaitState(producer, Thread.State.WAITING)).isEqualTo(Thread.State.WAITING);

            assertThat(box.take()).isEqualTo("first");
            producer.join(5_000);
            assertThat(box.take()).isEqualTo("second");
        }

        @Test
        @DisplayName("wait() and notifyAll() require owning the monitor")
        void needsMonitor() {
            Object lock = new Object();
            assertThatThrownBy(lock::wait).isInstanceOf(IllegalMonitorStateException.class);
            assertThatThrownBy(lock::notifyAll).isInstanceOf(IllegalMonitorStateException.class);
        }
    }
}
