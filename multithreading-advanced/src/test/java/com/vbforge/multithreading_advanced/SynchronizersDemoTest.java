package com.vbforge.multithreading_advanced;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("SynchronizersDemo — API contracts")
@Timeout(value = 20, unit = TimeUnit.SECONDS)
class SynchronizersDemoTest {

    @Nested
    @DisplayName("1. CountDownLatch: wait for N workers")
    class WaitForWorkers {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(SynchronizersDemo::countDownLatchAwaitWorkersDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("await() returns only after every worker has counted down")
        void awaitWaitsForAllWorkers() throws Exception {
            final int workers = 4;
            CountDownLatch allDone = new CountDownLatch(workers);
            AtomicInteger finished = new AtomicInteger();
            ExecutorService pool = Executors.newFixedThreadPool(workers);
            try {
                for (int i = 1; i <= workers; i++) {
                    final int id = i;
                    pool.execute(() -> {
                        try {
                            Thread.sleep(20L * id);
                            finished.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            allDone.countDown();
                        }
                    });
                }
                assertThat(allDone.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(finished.get()).isEqualTo(workers);
                assertThat(allDone.getCount()).isZero();
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("getCount() goes down by one per countDown()")
        void getCountDecrements() {
            CountDownLatch latch = new CountDownLatch(3);
            assertThat(latch.getCount()).isEqualTo(3);
            latch.countDown();
            assertThat(latch.getCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("A worker that never counts down leaves the coordinator waiting")
        void missingCountDownBlocksForever() throws Exception {
            CountDownLatch latch = new CountDownLatch(2);
            latch.countDown(); // only one of two workers reported back
            assertThat(latch.await(100, TimeUnit.MILLISECONDS)).isFalse();
            assertThat(latch.getCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("2. CountDownLatch: start gate")
    class StartGate {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(SynchronizersDemo::startGateDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("No worker passes the gate until it is opened; all do afterwards")
        void gateHoldsWorkersBack() throws Exception {
            final int workers = 3;
            CountDownLatch ready = new CountDownLatch(workers);
            CountDownLatch startGun = new CountDownLatch(1);
            CountDownLatch finished = new CountDownLatch(workers);
            AtomicInteger started = new AtomicInteger();
            ExecutorService pool = Executors.newFixedThreadPool(workers);
            try {
                for (int i = 0; i < workers; i++) {
                    pool.execute(() -> {
                        try {
                            ready.countDown();
                            startGun.await();
                            started.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            finished.countDown();
                        }
                    });
                }
                assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(started.get()).isZero();

                startGun.countDown();

                assertThat(finished.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(started.get()).isEqualTo(workers);
            } finally {
                startGun.countDown();
                pool.shutdownNow();
            }
        }
    }

    @Nested
    @DisplayName("3. CountDownLatch gotchas")
    class LatchGotchas {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(SynchronizersDemo::latchGotchasDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("await(timeout) returns false while the count is above zero")
        void awaitTimeoutReturnsFalse() throws Exception {
            CountDownLatch latch = new CountDownLatch(2);
            assertThat(latch.await(50, TimeUnit.MILLISECONDS)).isFalse();
        }

        @Test
        @DisplayName("Extra countDown() calls do not push the count below zero")
        void countStopsAtZero() {
            CountDownLatch latch = new CountDownLatch(2);
            latch.countDown();
            latch.countDown();
            latch.countDown();
            assertThat(latch.getCount()).isZero();
        }

        @Test
        @DisplayName("An open latch stays open: await returns true immediately, every time")
        void openLatchStaysOpen() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            latch.countDown();
            assertThat(latch.await(50, TimeUnit.MILLISECONDS)).isTrue();
            assertThat(latch.await(50, TimeUnit.MILLISECONDS)).isTrue();
        }
    }

    @Nested
    @DisplayName("4. Semaphore: limit concurrency")
    class SemaphoreLimit {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(SynchronizersDemo::semaphoreLimitDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("10 threads and 3 permits never put more than 3 threads in the section")
        void neverMoreThanPermitsInside() throws Exception {
            final int tasks = 10;
            Semaphore permits = new Semaphore(3);
            AtomicInteger running = new AtomicInteger();
            AtomicInteger maxRunning = new AtomicInteger();
            CountDownLatch allDone = new CountDownLatch(tasks);
            ExecutorService pool = Executors.newFixedThreadPool(tasks);
            try {
                for (int i = 0; i < tasks; i++) {
                    pool.execute(() -> {
                        try {
                            permits.acquire();
                            try {
                                maxRunning.accumulateAndGet(running.incrementAndGet(), Math::max);
                                Thread.sleep(20);
                            } finally {
                                running.decrementAndGet();
                                permits.release();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            allDone.countDown();
                        }
                    });
                }
                assertThat(allDone.await(10, TimeUnit.SECONDS)).isTrue();
                assertThat(maxRunning.get()).isBetween(1, 3);
                assertThat(permits.availablePermits()).isEqualTo(3);
            } finally {
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("A thread blocks in acquire() when no permit is free")
        void acquireBlocksWithoutPermits() throws Exception {
            Semaphore noPermits = new Semaphore(0);
            CountDownLatch acquired = new CountDownLatch(1);
            Thread waiter = new Thread(() -> {
                try {
                    noPermits.acquire();
                    acquired.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            waiter.setDaemon(true);
            waiter.start();

            assertThat(acquired.await(100, TimeUnit.MILLISECONDS)).isFalse();
            noPermits.release();
            assertThat(acquired.await(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Nested
    @DisplayName("5. Semaphore: permit bookkeeping")
    class PermitBookkeeping {

        @Test
        @DisplayName("Demo section runs without error")
        void demoRuns() {
            assertThatCode(SynchronizersDemo::semaphorePermitsDemo).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("tryAcquire() is non-blocking: true while permits remain, false afterwards")
        void tryAcquireDoesNotBlock() {
            Semaphore semaphore = new Semaphore(2);
            assertThat(semaphore.tryAcquire()).isTrue();
            assertThat(semaphore.tryAcquire()).isTrue();
            assertThat(semaphore.tryAcquire()).isFalse();
            assertThat(semaphore.availablePermits()).isZero();
        }

        @Test
        @DisplayName("release() returns a permit that tryAcquire() can take again")
        void releaseGivesPermitBack() {
            Semaphore semaphore = new Semaphore(1);
            assertThat(semaphore.tryAcquire()).isTrue();
            assertThat(semaphore.tryAcquire()).isFalse();
            semaphore.release();
            assertThat(semaphore.tryAcquire()).isTrue();
        }

        @Test
        @DisplayName("A release() without a matching acquire() raises the permit count")
        void strayReleaseInflatesPermits() {
            Semaphore semaphore = new Semaphore(1);
            semaphore.release();
            assertThat(semaphore.availablePermits()).isEqualTo(2);
        }

        @Test
        @DisplayName("acquire(n) takes n permits and drainPermits() takes the rest")
        void bulkAcquireAndDrain() throws Exception {
            Semaphore semaphore = new Semaphore(5);
            semaphore.acquire(3);
            assertThat(semaphore.availablePermits()).isEqualTo(2);
            assertThat(semaphore.drainPermits()).isEqualTo(2);
            assertThat(semaphore.availablePermits()).isZero();
        }

        @Test
        @DisplayName("The fairness flag is exposed through isFair()")
        void fairness() {
            assertThat(new Semaphore(1).isFair()).isFalse();
            assertThat(new Semaphore(1, true).isFair()).isTrue();
        }
    }
}
