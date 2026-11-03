package com.vbforge.locks_and_conditions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ReentrantLockTasks — your implementations")
class ReentrantLockTasksTest {

    private ReentrantLockTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ReentrantLockTasks();
    }

    /** Starts a thread that holds {@code lock} until the returned latch is counted down. */
    private static Thread holdInBackground(ReentrantLock lock, CountDownLatch release)
            throws InterruptedException {
        CountDownLatch held = new CountDownLatch(1);
        Thread holder = new Thread(() -> {
            lock.lock();
            try {
                held.countDown();
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });
        holder.start();
        held.await();
        return holder;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement incrementConcurrently()")
    @DisplayName("incrementConcurrently")
    class IncrementConcurrentlyTest {

        @Test
        @DisplayName("4 threads x 1000 increments = 4000")
        void javadocExample() throws InterruptedException {
            assertThat(tasks.incrementConcurrently(4, 1_000)).isEqualTo(4_000);
        }

        @Test
        @DisplayName("Single thread")
        void singleThread() throws InterruptedException {
            assertThat(tasks.incrementConcurrently(1, 5)).isEqualTo(5);
        }

        @Test
        @DisplayName("Zero increments gives zero")
        void zeroIncrements() throws InterruptedException {
            assertThat(tasks.incrementConcurrently(3, 0)).isZero();
        }

        @Test
        @DisplayName("Zero threads gives zero")
        void zeroThreads() throws InterruptedException {
            assertThat(tasks.incrementConcurrently(0, 100)).isZero();
        }

        @ParameterizedTest(name = "{0} threads x {1} increments = {2}")
        @CsvSource({"2, 500, 1000", "8, 250, 2000", "16, 100, 1600"})
        @DisplayName("Parametrised thread counts")
        void parametrized(int threads, int increments, int expected) throws InterruptedException {
            assertThat(tasks.incrementConcurrently(threads, increments)).isEqualTo(expected);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runIfFree()")
    @DisplayName("runIfFree")
    class RunIfFreeTest {

        @Test
        @DisplayName("Free lock: action runs, true returned, lock free afterwards")
        void freeLock() {
            ReentrantLock lock = new ReentrantLock();
            AtomicBoolean ran = new AtomicBoolean();

            boolean result = tasks.runIfFree(lock, () -> ran.set(true));

            assertThat(result).isTrue();
            assertThat(ran).isTrue();
            assertThat(lock.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Lock held by another thread: action skipped, false returned")
        void busyLock() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            CountDownLatch release = new CountDownLatch(1);
            Thread holder = holdInBackground(lock, release);
            AtomicBoolean ran = new AtomicBoolean();

            boolean result = tasks.runIfFree(lock, () -> ran.set(true));

            release.countDown();
            holder.join();
            assertThat(result).isFalse();
            assertThat(ran).isFalse();
        }

        @Test
        @DisplayName("Action runs while the lock is held")
        void lockHeldDuringAction() {
            ReentrantLock lock = new ReentrantLock();
            AtomicBoolean heldDuringAction = new AtomicBoolean();

            tasks.runIfFree(lock, () -> heldDuringAction.set(lock.isHeldByCurrentThread()));

            assertThat(heldDuringAction).isTrue();
        }

        @Test
        @DisplayName("Lock is released even when the action throws")
        void releasedOnException() {
            ReentrantLock lock = new ReentrantLock();

            assertThatThrownBy(() -> tasks.runIfFree(lock, () -> {
                throw new IllegalStateException("boom");
            })).isInstanceOf(IllegalStateException.class);

            assertThat(lock.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Busy lock stays owned by its holder (not unlocked by mistake)")
        void doesNotUnlockForeignLock() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            CountDownLatch release = new CountDownLatch(1);
            Thread holder = holdInBackground(lock, release);

            tasks.runIfFree(lock, () -> { });

            boolean stillLocked = lock.isLocked();
            release.countDown();
            holder.join();
            assertThat(stillLocked).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement nestedHoldCount()")
    @DisplayName("nestedHoldCount")
    class NestedHoldCountTest {

        @Test
        @DisplayName("Depth 3 reports hold count 3 and frees the lock")
        void javadocExample() {
            ReentrantLock lock = new ReentrantLock();

            assertThat(tasks.nestedHoldCount(lock, 3)).isEqualTo(3);
            assertThat(lock.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Depth 1")
        void depthOne() {
            ReentrantLock lock = new ReentrantLock();

            assertThat(tasks.nestedHoldCount(lock, 1)).isEqualTo(1);
            assertThat(lock.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Depth 0 never touches the lock")
        void depthZero() {
            ReentrantLock lock = new ReentrantLock();

            assertThat(tasks.nestedHoldCount(lock, 0)).isZero();
            assertThat(lock.isLocked()).isFalse();
        }

        @ParameterizedTest(name = "depth {0} -> hold count {0}")
        @CsvSource({"2", "5", "10", "50"})
        @DisplayName("Hold count equals depth")
        void parametrized(int depth) {
            ReentrantLock lock = new ReentrantLock();

            assertThat(tasks.nestedHoldCount(lock, depth)).isEqualTo(depth);
            assertThat(lock.getHoldCount()).isZero();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement acquireWithTimeout()")
    @DisplayName("acquireWithTimeout")
    class AcquireWithTimeoutTest {

        @Test
        @DisplayName("Free lock: true, and the lock is released again")
        void freeLock() {
            ReentrantLock lock = new ReentrantLock();

            assertThat(tasks.acquireWithTimeout(lock, 100)).isTrue();
            assertThat(lock.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Lock held by another thread: false after the timeout")
        void busyLock() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            CountDownLatch release = new CountDownLatch(1);
            Thread holder = holdInBackground(lock, release);

            boolean result = tasks.acquireWithTimeout(lock, 30);

            release.countDown();
            holder.join();
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Lock becomes free while waiting: true")
        void lockFreedInTime() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            CountDownLatch release = new CountDownLatch(1);
            Thread holder = holdInBackground(lock, release);
            AtomicBoolean result = new AtomicBoolean();

            Thread waiter = new Thread(() -> result.set(tasks.acquireWithTimeout(lock, 10_000)));
            waiter.start();
            while (!lock.hasQueuedThread(waiter)) {
                Thread.yield();
            }
            release.countDown();
            holder.join();
            waiter.join();

            assertThat(result).isTrue();
            assertThat(lock.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Interrupt while waiting: false and interrupt flag restored")
        void interrupted() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            CountDownLatch release = new CountDownLatch(1);
            Thread holder = holdInBackground(lock, release);
            AtomicBoolean result = new AtomicBoolean(true);
            AtomicBoolean flagKept = new AtomicBoolean();

            Thread waiter = new Thread(() -> {
                result.set(tasks.acquireWithTimeout(lock, 10_000));
                flagKept.set(Thread.currentThread().isInterrupted());
            });
            waiter.start();
            while (!lock.hasQueuedThread(waiter)) {
                Thread.yield();
            }
            waiter.interrupt();
            waiter.join();

            release.countDown();
            holder.join();
            assertThat(result).isFalse();
            assertThat(flagKept).isTrue();
        }
    }
}
