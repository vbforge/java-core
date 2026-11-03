package com.vbforge.locks_and_conditions;

import com.vbforge.locks_and_conditions.LivenessDemo.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@DisplayName("LivenessDemo — API contracts")
class LivenessDemoTest {

    private static boolean contains(long[] ids, long id) {
        for (long candidate : ids) {
            if (candidate == id) {
                return true;
            }
        }
        return false;
    }

    @Nested
    @DisplayName("1. Deadlock")
    class DeadlockTest {

        @Test
        @DisplayName("Opposite lock order produces a deadlock the JVM can see")
        void deadlockDetected() throws InterruptedException {
            LivenessDemo.DeadlockScenario scenario = LivenessDemo.startDeadlock();
            try {
                long[] ids = LivenessDemo.findDeadlockedThreadIds();
                assertThat(contains(ids, scenario.getFirst().getId())).isTrue();
                assertThat(contains(ids, scenario.getSecond().getId())).isTrue();
            } finally {
                scenario.resolve();
            }
        }

        @Test
        @DisplayName("Interrupting the waiters breaks the deadlock")
        void interruptBreaksDeadlock() throws InterruptedException {
            LivenessDemo.DeadlockScenario scenario = LivenessDemo.startDeadlock();
            scenario.resolve();

            assertThat(scenario.getFirst().isAlive()).isFalse();
            assertThat(scenario.getSecond().isAlive()).isFalse();
            assertThat(contains(LivenessDemo.findDeadlockedThreadIds(), scenario.getFirst().getId())).isFalse();
        }

        @Test
        @DisplayName("Deadlocked threads are parked (WAITING), not running")
        void threadsAreWaiting() throws InterruptedException {
            LivenessDemo.DeadlockScenario scenario = LivenessDemo.startDeadlock();
            try {
                assertThat(scenario.getFirst().getState()).isEqualTo(Thread.State.WAITING);
                assertThat(scenario.getSecond().getState()).isEqualTo(Thread.State.WAITING);
            } finally {
                scenario.resolve();
            }
        }
    }

    @Nested
    @DisplayName("2. Lock ordering")
    class LockOrdering {

        @Test
        @DisplayName("Transfer moves money when funds suffice")
        void transferWorks() {
            Account a = new Account(1, 100);
            Account b = new Account(2, 50);

            assertThat(LivenessDemo.transferOrdered(a, b, 30)).isTrue();

            assertThat(a.getBalance()).isEqualTo(70);
            assertThat(b.getBalance()).isEqualTo(80);
        }

        @Test
        @DisplayName("Transfer is refused when funds are insufficient")
        void insufficientFunds() {
            Account a = new Account(1, 10);
            Account b = new Account(2, 50);

            assertThat(LivenessDemo.transferOrdered(a, b, 30)).isFalse();

            assertThat(a.getBalance()).isEqualTo(10);
            assertThat(b.getBalance()).isEqualTo(50);
        }

        @Test
        @DisplayName("Locks are released after a transfer (both directions)")
        void locksReleased() {
            Account a = new Account(1, 100);
            Account b = new Account(2, 100);

            LivenessDemo.transferOrdered(a, b, 1);
            LivenessDemo.transferOrdered(b, a, 1);

            assertThat(a.getLock().isLocked()).isFalse();
            assertThat(b.getLock().isLocked()).isFalse();
        }

        @Test
        @DisplayName("Opposite transfers in two threads finish and conserve the total")
        void oppositeTransfersDoNotDeadlock() {
            Account a = new Account(1, 1_000);
            Account b = new Account(2, 1_000);

            assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
                Thread t1 = new Thread(() -> {
                    for (int i = 0; i < 5_000; i++) {
                        LivenessDemo.transferOrdered(a, b, 1);
                    }
                });
                Thread t2 = new Thread(() -> {
                    for (int i = 0; i < 5_000; i++) {
                        LivenessDemo.transferOrdered(b, a, 1);
                    }
                });
                t1.start();
                t2.start();
                t1.join();
                t2.join();
            });

            assertThat(a.getBalance() + b.getBalance()).isEqualTo(2_000);
        }
    }

    @Nested
    @DisplayName("3. tryLock with timeout")
    class TryLockTimeout {

        @Test
        @DisplayName("Free accounts: transfer succeeds")
        void freeAccounts() throws InterruptedException {
            Account a = new Account(1, 100);
            Account b = new Account(2, 100);

            assertThat(LivenessDemo.transferWithTimeout(a, b, 40, 50)).isTrue();

            assertThat(a.getBalance()).isEqualTo(60);
            assertThat(b.getBalance()).isEqualTo(140);
        }

        @Test
        @DisplayName("Target locked elsewhere: gives up, balances unchanged, source lock released")
        void targetBusy() throws InterruptedException {
            Account a = new Account(1, 100);
            Account b = new Account(2, 100);
            CountDownLatch held = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);
            Thread holder = new Thread(() -> {
                ReentrantLock lock = b.getLock();
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

            boolean result = LivenessDemo.transferWithTimeout(a, b, 10, 30);
            boolean sourceStillLocked = a.getLock().isLocked();

            release.countDown();
            holder.join();
            assertThat(result).isFalse();
            assertThat(sourceStillLocked).isFalse();
            assertThat(a.getBalance()).isEqualTo(100);
            assertThat(b.getBalance()).isEqualTo(100);
        }

        @Test
        @DisplayName("Insufficient funds: false and both locks released")
        void insufficientFunds() throws InterruptedException {
            Account a = new Account(1, 5);
            Account b = new Account(2, 5);

            assertThat(LivenessDemo.transferWithTimeout(a, b, 10, 50)).isFalse();

            assertThat(a.getLock().isLocked()).isFalse();
            assertThat(b.getLock().isLocked()).isFalse();
        }
    }

    @Nested
    @DisplayName("4. Livelock")
    class Livelock {

        @Test
        @DisplayName("Symmetric rule never lets them pass")
        void symmetricNeverPasses() {
            assertThat(LivenessDemo.roundsUntilPassed(true, 10_000)).isEqualTo(-1);
        }

        @Test
        @DisplayName("Priority rule breaks the symmetry in round 1")
        void asymmetricPasses() {
            assertThat(LivenessDemo.roundsUntilPassed(false, 10_000)).isEqualTo(1);
        }

        @Test
        @DisplayName("A livelock is invisible to deadlock detection")
        void notReportedAsDeadlock() {
            LivenessDemo.roundsUntilPassed(true, 1_000);
            assertThat(LivenessDemo.findDeadlockedThreadIds()).isEmpty();
        }
    }

    @Nested
    @DisplayName("5. Fair lock")
    class FairLock {

        @Test
        @DisplayName("Queued threads acquire a fair lock in arrival order")
        void fifoOrder() throws InterruptedException {
            assertThat(LivenessDemo.fairLockAcquisitionOrder(5)).containsExactly(0, 1, 2, 3, 4);
        }

        @Test
        @DisplayName("Fair lock reports isFair()")
        void reportsFair() {
            assertThat(new ReentrantLock(true).isFair()).isTrue();
        }
    }
}
