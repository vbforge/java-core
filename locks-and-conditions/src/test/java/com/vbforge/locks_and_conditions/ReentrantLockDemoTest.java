package com.vbforge.locks_and_conditions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ReentrantLockDemo — API contracts")
class ReentrantLockDemoTest {

    /** Starts a thread that takes {@code lock} and keeps it until {@code gate.release()}. */
    private static Thread holdInBackground(ReentrantLock lock, ReentrantLockDemo.LockHandoff gate)
            throws InterruptedException {
        Thread holder = new Thread(() -> {
            lock.lock();
            try {
                gate.signalHeld();
                gate.awaitRelease();
            } finally {
                lock.unlock();
            }
        });
        holder.start();
        gate.awaitHeld();
        return holder;
    }

    @Nested
    @DisplayName("1. lock / unlock with try-finally")
    class LockUnlock {

        @Test
        @DisplayName("New counter starts at zero")
        void startsAtZero() {
            assertThat(new ReentrantLockDemo.LockedCounter().get()).isZero();
        }

        @Test
        @DisplayName("Single-threaded increments are counted")
        void singleThread() {
            ReentrantLockDemo.LockedCounter counter = new ReentrantLockDemo.LockedCounter();
            counter.increment();
            counter.increment();
            assertThat(counter.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("Concurrent increments lose no updates")
        void concurrentIncrements() throws InterruptedException {
            ReentrantLockDemo.LockedCounter counter = new ReentrantLockDemo.LockedCounter();
            Thread[] threads = new Thread[4];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 1_000; j++) {
                        counter.increment();
                    }
                });
                threads[i].start();
            }
            for (Thread t : threads) {
                t.join();
            }
            assertThat(counter.get()).isEqualTo(4_000);
        }

        @Test
        @DisplayName("finally releases the lock when the critical section throws")
        void releasedOnException() {
            ReentrantLockDemo.LockedCounter counter = new ReentrantLockDemo.LockedCounter();
            assertThatThrownBy(counter::failWhileLocked).isInstanceOf(IllegalStateException.class);
            assertThat(counter.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Unlocking a lock you do not hold throws IllegalMonitorStateException")
        void unlockWithoutLock() {
            ReentrantLock lock = new ReentrantLock();
            assertThatThrownBy(lock::unlock).isInstanceOf(IllegalMonitorStateException.class);
        }
    }

    @Nested
    @DisplayName("2. Reentrancy and hold count")
    class Reentrancy {

        @Test
        @DisplayName("Nested acquisition sees hold count 2")
        void nestedHoldCount() {
            assertThat(new ReentrantLockDemo.HoldCountProbe().outer()).isEqualTo(2);
        }

        @Test
        @DisplayName("Hold count is back to 0 after the outer call returns")
        void holdCountResets() {
            ReentrantLockDemo.HoldCountProbe probe = new ReentrantLockDemo.HoldCountProbe();
            probe.outer();
            assertThat(probe.holdCountNow()).isZero();
        }

        @Test
        @DisplayName("Lock is free after balanced lock/unlock calls")
        void freeAfterwards() {
            ReentrantLockDemo.HoldCountProbe probe = new ReentrantLockDemo.HoldCountProbe();
            probe.outer();
            assertThat(probe.isLocked()).isFalse();
        }

        @Test
        @DisplayName("A missing unlock keeps the lock held")
        void missingUnlock() {
            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            lock.lock();
            lock.unlock();
            assertThat(lock.isLocked()).isTrue();
            assertThat(lock.getHoldCount()).isEqualTo(1);
            lock.unlock();
            assertThat(lock.isLocked()).isFalse();
        }
    }

    @Nested
    @DisplayName("3. tryLock() — do not wait at all")
    class TryLock {

        @Test
        @DisplayName("Free lock: action runs and true is returned")
        void freeLock() {
            ReentrantLock lock = new ReentrantLock();
            AtomicBoolean ran = new AtomicBoolean();
            assertThat(ReentrantLockDemo.tryRun(lock, () -> ran.set(true))).isTrue();
            assertThat(ran).isTrue();
        }

        @Test
        @DisplayName("Lock is released after the action")
        void releasedAfterwards() {
            ReentrantLock lock = new ReentrantLock();
            ReentrantLockDemo.tryRun(lock, () -> { });
            assertThat(lock.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Busy lock: action is skipped and false is returned")
        void busyLock() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            ReentrantLockDemo.LockHandoff gate = new ReentrantLockDemo.LockHandoff();
            Thread holder = holdInBackground(lock, gate);
            AtomicBoolean ran = new AtomicBoolean();

            boolean result = ReentrantLockDemo.tryRun(lock, () -> ran.set(true));

            gate.release();
            holder.join();
            assertThat(result).isFalse();
            assertThat(ran).isFalse();
        }

        @Test
        @DisplayName("Owner thread can tryLock again (reentrant)")
        void ownerCanTryAgain() {
            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            try {
                assertThat(ReentrantLockDemo.tryRun(lock, () -> { })).isTrue();
                assertThat(lock.isHeldByCurrentThread()).isTrue();
            } finally {
                lock.unlock();
            }
        }
    }

    @Nested
    @DisplayName("4. tryLock(timeout) — wait, but not forever")
    class TryLockTimeout {

        @Test
        @DisplayName("Free lock is acquired within the timeout")
        void freeLock() {
            assertThat(ReentrantLockDemo.acquireWithin(new ReentrantLock(), 50)).isTrue();
        }

        @Test
        @DisplayName("Lock is released again after a successful acquire")
        void releasedAgain() {
            ReentrantLock lock = new ReentrantLock();
            ReentrantLockDemo.acquireWithin(lock, 50);
            assertThat(lock.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Busy lock: gives up after the timeout")
        void busyLock() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            ReentrantLockDemo.LockHandoff gate = new ReentrantLockDemo.LockHandoff();
            Thread holder = holdInBackground(lock, gate);

            boolean result = ReentrantLockDemo.acquireWithin(lock, 30);

            gate.release();
            holder.join();
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Interrupt while waiting returns false and keeps the interrupt flag")
        void interruptedWhileWaiting() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            ReentrantLockDemo.LockHandoff gate = new ReentrantLockDemo.LockHandoff();
            Thread holder = holdInBackground(lock, gate);
            AtomicBoolean result = new AtomicBoolean(true);
            AtomicBoolean flagKept = new AtomicBoolean();

            Thread waiter = new Thread(() -> {
                result.set(ReentrantLockDemo.acquireWithin(lock, 10_000));
                flagKept.set(Thread.currentThread().isInterrupted());
            });
            waiter.start();
            while (!lock.hasQueuedThread(waiter)) {
                Thread.yield();
            }
            waiter.interrupt();
            waiter.join();

            gate.release();
            holder.join();
            assertThat(result).isFalse();
            assertThat(flagKept).isTrue();
        }
    }

    @Nested
    @DisplayName("5. lockInterruptibly() — waiting that can be cancelled")
    class LockInterruptibly {

        @Test
        @DisplayName("Free lock is obtained and released")
        void freeLock() {
            ReentrantLock lock = new ReentrantLock();
            assertThat(ReentrantLockDemo.lockOrGiveUpWhenInterrupted(lock)).isTrue();
            assertThat(lock.isLocked()).isFalse();
        }

        @Test
        @DisplayName("Waiting thread gives up when interrupted")
        void interruptCancelsWaiting() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            AtomicBoolean result = new AtomicBoolean(true);
            try {
                Thread waiter = new Thread(() -> result.set(ReentrantLockDemo.lockOrGiveUpWhenInterrupted(lock)));
                waiter.start();
                while (!lock.hasQueuedThread(waiter)) {
                    Thread.yield();
                }
                waiter.interrupt();
                waiter.join();
            } finally {
                lock.unlock();
            }
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Interrupted thread stops being queued")
        void leavesQueue() throws InterruptedException {
            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            AtomicReference<Thread> waiterRef = new AtomicReference<>();
            try {
                Thread waiter = new Thread(() -> ReentrantLockDemo.lockOrGiveUpWhenInterrupted(lock));
                waiterRef.set(waiter);
                waiter.start();
                while (!lock.hasQueuedThread(waiter)) {
                    Thread.yield();
                }
                waiter.interrupt();
                waiter.join();
            } finally {
                lock.unlock();
            }
            assertThat(lock.hasQueuedThread(waiterRef.get())).isFalse();
        }
    }

    @Nested
    @DisplayName("6. Fair vs non-fair")
    class Fairness {

        @Test
        @DisplayName("Default lock is non-fair")
        void defaultNonFair() {
            assertThat(new ReentrantLock().isFair()).isFalse();
        }

        @Test
        @DisplayName("ReentrantLock(true) is fair")
        void fairLock() {
            assertThat(new ReentrantLock(true).isFair()).isTrue();
        }
    }
}
