package com.vbforge.locks_and_conditions;

import com.vbforge.locks_and_conditions.LivenessDemo.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@DisplayName("LivenessTasks — your implementations")
class LivenessTasksTest {

    private LivenessTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new LivenessTasks();
    }

    /** Holds {@code account}'s lock in a background thread until the returned latch is released. */
    private static Thread holdLock(Account account, CountDownLatch release) throws InterruptedException {
        CountDownLatch held = new CountDownLatch(1);
        Thread holder = new Thread(() -> {
            account.getLock().lock();
            try {
                held.countDown();
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                account.getLock().unlock();
            }
        });
        holder.start();
        held.await();
        return holder;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement transferOrdered()")
    @DisplayName("transferOrdered")
    class TransferOrderedTest {

        @Test
        @DisplayName("100 -> 50, amount 30: balances 70 and 80")
        void javadocExample() {
            Account from = new Account(1, 100);
            Account to = new Account(2, 50);

            assertThat(tasks.transferOrdered(from, to, 30)).isTrue();

            assertThat(from.getBalance()).isEqualTo(70);
            assertThat(to.getBalance()).isEqualTo(80);
        }

        @Test
        @DisplayName("Insufficient funds: false, balances unchanged")
        void insufficientFunds() {
            Account from = new Account(1, 10);
            Account to = new Account(2, 50);

            assertThat(tasks.transferOrdered(from, to, 30)).isFalse();

            assertThat(from.getBalance()).isEqualTo(10);
            assertThat(to.getBalance()).isEqualTo(50);
        }

        @Test
        @DisplayName("Works when 'from' has the higher id")
        void higherIdSource() {
            Account from = new Account(9, 100);
            Account to = new Account(2, 0);

            assertThat(tasks.transferOrdered(from, to, 100)).isTrue();

            assertThat(from.getBalance()).isZero();
            assertThat(to.getBalance()).isEqualTo(100);
        }

        @Test
        @DisplayName("Exact balance may be transferred")
        void exactBalance() {
            Account from = new Account(1, 30);
            Account to = new Account(2, 0);

            assertThat(tasks.transferOrdered(from, to, 30)).isTrue();
        }

        @Test
        @DisplayName("Both locks are released afterwards, success or not")
        void locksReleased() {
            Account a = new Account(1, 10);
            Account b = new Account(2, 10);

            tasks.transferOrdered(a, b, 5);
            tasks.transferOrdered(a, b, 500);

            assertThat(a.getLock().isLocked()).isFalse();
            assertThat(b.getLock().isLocked()).isFalse();
        }

        @Test
        @DisplayName("Opposite transfers in two threads do not deadlock and conserve the total")
        void noDeadlock() {
            Account a = new Account(1, 1_000);
            Account b = new Account(2, 1_000);

            assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
                Thread t1 = new Thread(() -> {
                    for (int i = 0; i < 10_000; i++) {
                        tasks.transferOrdered(a, b, 1);
                    }
                });
                Thread t2 = new Thread(() -> {
                    for (int i = 0; i < 10_000; i++) {
                        tasks.transferOrdered(b, a, 1);
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
    @Disabled("Remove @Disabled when you implement transferWithTimeout()")
    @DisplayName("transferWithTimeout")
    class TransferWithTimeoutTest {

        @Test
        @DisplayName("Both free, enough money: true")
        void javadocExample() throws InterruptedException {
            Account from = new Account(1, 100);
            Account to = new Account(2, 100);

            assertThat(tasks.transferWithTimeout(from, to, 40, 50)).isTrue();

            assertThat(from.getBalance()).isEqualTo(60);
            assertThat(to.getBalance()).isEqualTo(140);
        }

        @Test
        @DisplayName("'to' locked elsewhere: false and 'from' is not left locked")
        void targetBusy() throws InterruptedException {
            Account from = new Account(1, 100);
            Account to = new Account(2, 100);
            CountDownLatch release = new CountDownLatch(1);
            Thread holder = holdLock(to, release);

            boolean result = tasks.transferWithTimeout(from, to, 10, 30);
            boolean fromLocked = from.getLock().isLocked();

            release.countDown();
            holder.join();
            assertThat(result).isFalse();
            assertThat(fromLocked).isFalse();
            assertThat(from.getBalance()).isEqualTo(100);
        }

        @Test
        @DisplayName("'from' locked elsewhere: false")
        void sourceBusy() throws InterruptedException {
            Account from = new Account(1, 100);
            Account to = new Account(2, 100);
            CountDownLatch release = new CountDownLatch(1);
            Thread holder = holdLock(from, release);

            boolean result = tasks.transferWithTimeout(from, to, 10, 30);
            boolean toLocked = to.getLock().isLocked();

            release.countDown();
            holder.join();
            assertThat(result).isFalse();
            assertThat(toLocked).isFalse();
        }

        @Test
        @DisplayName("Insufficient funds: false and both locks released")
        void insufficientFunds() throws InterruptedException {
            Account from = new Account(1, 5);
            Account to = new Account(2, 5);

            assertThat(tasks.transferWithTimeout(from, to, 10, 50)).isFalse();

            assertThat(from.getLock().isLocked()).isFalse();
            assertThat(to.getLock().isLocked()).isFalse();
            assertThat(from.getBalance()).isEqualTo(5);
        }

        @Test
        @DisplayName("Waits for a lock that frees up in time")
        void waitsForRelease() throws InterruptedException {
            Account from = new Account(1, 100);
            Account to = new Account(2, 0);
            CountDownLatch release = new CountDownLatch(1);
            Thread holder = holdLock(to, release);
            boolean[] result = new boolean[1];

            Thread worker = new Thread(() -> {
                try {
                    result[0] = tasks.transferWithTimeout(from, to, 10, 10_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            worker.start();
            while (!to.getLock().hasQueuedThread(worker)) {
                Thread.yield();
            }
            release.countDown();
            holder.join();
            worker.join();

            assertThat(result[0]).isTrue();
            assertThat(to.getBalance()).isEqualTo(10);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement findDeadlockedThreadIds()")
    @DisplayName("findDeadlockedThreadIds")
    class FindDeadlockedThreadIdsTest {

        @Test
        @DisplayName("No deadlock: empty set")
        void noDeadlock() {
            assertThat(tasks.findDeadlockedThreadIds()).isEmpty();
        }

        @Test
        @DisplayName("Two deadlocked threads: both ids reported")
        void twoDeadlocked() throws InterruptedException {
            LivenessDemo.DeadlockScenario scenario = LivenessDemo.startDeadlock();
            try {
                Set<Long> ids = tasks.findDeadlockedThreadIds();

                assertThat(ids).contains(scenario.getFirst().getId(), scenario.getSecond().getId());
            } finally {
                scenario.resolve();
            }
        }

        @Test
        @DisplayName("After the deadlock is broken: empty set again")
        void afterResolve() throws InterruptedException {
            LivenessDemo.startDeadlock().resolve();

            assertThat(tasks.findDeadlockedThreadIds()).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement backoffDelayMillis()")
    @DisplayName("backoffDelayMillis")
    class BackoffDelayMillisTest {

        @ParameterizedTest(name = "attempt {0}, base {1}, cap {2} -> {3}")
        @CsvSource({
                "0, 10, 1000, 10",
                "1, 10, 1000, 20",
                "3, 10, 1000, 80",
                "6, 10, 1000, 640",
                "7, 10, 1000, 1000",
                "10, 10, 1000, 1000"
        })
        @DisplayName("Doubles each attempt up to the cap")
        void doublesUpToCap(int attempt, long base, long cap, long expected) {
            assertThat(tasks.backoffDelayMillis(attempt, base, cap)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "attempt {0} does not overflow")
        @CsvSource({"62", "63", "64", "65", "200", "100000"})
        @DisplayName("Huge attempt numbers are capped, not overflowed")
        void noOverflow(int attempt) {
            assertThat(tasks.backoffDelayMillis(attempt, 10, 1_000)).isEqualTo(1_000);
        }

        @Test
        @DisplayName("Cap equal to base always returns base")
        void capEqualsBase() {
            assertThat(tasks.backoffDelayMillis(5, 50, 50)).isEqualTo(50);
        }

        @Test
        @DisplayName("Delay never decreases as attempts grow")
        void monotonic() {
            long previous = 0;
            for (int attempt = 0; attempt < 100; attempt++) {
                long delay = tasks.backoffDelayMillis(attempt, 7, 5_000);
                assertThat(delay).isGreaterThanOrEqualTo(previous).isLessThanOrEqualTo(5_000);
                previous = delay;
            }
        }
    }
}
