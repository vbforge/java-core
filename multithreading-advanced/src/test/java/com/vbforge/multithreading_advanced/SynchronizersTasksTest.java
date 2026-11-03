package com.vbforge.multithreading_advanced;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("SynchronizersTasks — your implementations")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SynchronizersTasksTest {

    private SynchronizersTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new SynchronizersTasks();
    }

    private static Runnable sleepThenIncrement(final AtomicInteger counter, final long millis) {
        return () -> {
            try {
                Thread.sleep(millis);
                counter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runAndWaitForAll()")
    @DisplayName("runAndWaitForAll")
    class RunAndWaitForAllTest {

        @Test
        @DisplayName("Returns only after all three jobs have run")
        void example() throws Exception {
            AtomicInteger counter = new AtomicInteger();
            List<Runnable> jobs = new ArrayList<>();
            jobs.add(sleepThenIncrement(counter, 30));
            jobs.add(sleepThenIncrement(counter, 60));
            jobs.add(sleepThenIncrement(counter, 90));

            tasks.runAndWaitForAll(jobs);

            assertThat(counter.get()).isEqualTo(3);
        }

        @Test
        @DisplayName("No jobs: returns at once")
        void empty() {
            assertTimeoutPreemptively(Duration.ofSeconds(2),
                    () -> tasks.runAndWaitForAll(Collections.<Runnable>emptyList()));
        }

        @Test
        @DisplayName("Jobs run concurrently: three jobs that wait for each other all meet")
        void jobsRunConcurrently() throws Exception {
            CountDownLatch allStarted = new CountDownLatch(3);
            AtomicInteger met = new AtomicInteger();
            Runnable meetUp = () -> {
                allStarted.countDown();
                try {
                    if (allStarted.await(3, TimeUnit.SECONDS)) {
                        met.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            List<Runnable> jobs = new ArrayList<>();
            jobs.add(meetUp);
            jobs.add(meetUp);
            jobs.add(meetUp);

            tasks.runAndWaitForAll(jobs);

            assertThat(met.get()).isEqualTo(3);
        }

        @Test
        @DisplayName("Every job runs on its own new thread, not on the caller's thread")
        void ownThreadPerJob() throws Exception {
            Set<Thread> threads = Collections.newSetFromMap(new ConcurrentHashMap<Thread, Boolean>());
            Runnable record = () -> threads.add(Thread.currentThread());
            List<Runnable> jobs = new ArrayList<>();
            jobs.add(record);
            jobs.add(record);
            jobs.add(record);

            tasks.runAndWaitForAll(jobs);

            assertThat(threads).hasSize(3).doesNotContain(Thread.currentThread());
        }

        @Test
        @DisplayName("A job that throws does not make the method wait forever")
        void throwingJob() {
            AtomicInteger counter = new AtomicInteger();
            List<Runnable> jobs = new ArrayList<>();
            jobs.add(() -> {
                throw new IllegalStateException("job failed on purpose");
            });
            jobs.add(sleepThenIncrement(counter, 50));

            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> tasks.runAndWaitForAll(jobs));

            assertThat(counter.get()).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runWithTimeout()")
    @DisplayName("runWithTimeout")
    class RunWithTimeoutTest {

        @Test
        @DisplayName("Quick jobs finish in time: true")
        void example() throws Exception {
            AtomicInteger counter = new AtomicInteger();
            List<Runnable> jobs = new ArrayList<>();
            jobs.add(sleepThenIncrement(counter, 10));
            jobs.add(sleepThenIncrement(counter, 20));

            assertThat(tasks.runWithTimeout(jobs, 1000)).isTrue();
            assertThat(counter.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("A job that blocks for good: false after about the timeout")
        void blockedJob() throws Exception {
            CountDownLatch release = new CountDownLatch(1);
            List<Runnable> jobs = new ArrayList<>();
            jobs.add(() -> {
                try {
                    release.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            try {
                long start = System.nanoTime();
                boolean finished = tasks.runWithTimeout(jobs, 100);
                long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

                assertThat(finished).isFalse();
                assertThat(elapsedMillis).isLessThan(3000);
            } finally {
                release.countDown();
            }
        }

        @Test
        @DisplayName("No jobs: true")
        void empty() throws Exception {
            assertThat(tasks.runWithTimeout(Collections.<Runnable>emptyList(), 100)).isTrue();
        }

        @Test
        @DisplayName("The slow job is not interrupted when the timeout expires")
        void timeoutDoesNotInterrupt() throws Exception {
            CountDownLatch release = new CountDownLatch(1);
            CountDownLatch finishedNormally = new CountDownLatch(1);
            List<Runnable> jobs = new ArrayList<>();
            jobs.add(() -> {
                try {
                    release.await();
                    finishedNormally.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            assertThat(tasks.runWithTimeout(jobs, 50)).isFalse();

            release.countDown();
            assertThat(finishedNormally.await(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement callWithPermit()")
    @DisplayName("callWithPermit")
    class CallWithPermitTest {

        @Test
        @DisplayName("Returns the action's result; holds one permit meanwhile and returns it afterwards")
        void example() throws Exception {
            Semaphore semaphore = new Semaphore(2);
            AtomicInteger permitsDuringAction = new AtomicInteger(-1);

            String result = tasks.callWithPermit(semaphore, () -> {
                permitsDuringAction.set(semaphore.availablePermits());
                return "ok";
            });

            assertThat(result).isEqualTo("ok");
            assertThat(permitsDuringAction.get()).isEqualTo(1);
            assertThat(semaphore.availablePermits()).isEqualTo(2);
        }

        @Test
        @DisplayName("When the action throws: the same exception propagates and the permit is returned")
        void actionThrows() {
            Semaphore semaphore = new Semaphore(2);
            IllegalStateException failure = new IllegalStateException("action failed");

            assertThatThrownBy(() -> tasks.callWithPermit(semaphore, () -> {
                throw failure;
            })).isSameAs(failure);

            assertThat(semaphore.availablePermits()).isEqualTo(2);
        }

        @Test
        @DisplayName("Blocks while no permit is free, then runs once one is released")
        void blocksUntilPermitAvailable() throws Exception {
            Semaphore semaphore = new Semaphore(0);
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                Future<String> future = pool.submit(() -> tasks.callWithPermit(semaphore, () -> "late"));

                assertThatThrownBy(() -> future.get(100, TimeUnit.MILLISECONDS))
                        .isInstanceOf(TimeoutException.class);

                semaphore.release();
                assertThat(future.get(5, TimeUnit.SECONDS)).isEqualTo("late");
                assertThat(semaphore.availablePermits()).isEqualTo(1);
            } finally {
                semaphore.release();
                pool.shutdownNow();
            }
        }

        @Test
        @DisplayName("A binary semaphore lets only one action run at a time")
        void binarySemaphoreExcludesOthers() throws Exception {
            Semaphore semaphore = new Semaphore(1);
            AtomicInteger inside = new AtomicInteger();
            AtomicInteger maxInside = new AtomicInteger();
            ExecutorService pool = Executors.newFixedThreadPool(4);
            try {
                List<Future<Integer>> futures = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    futures.add(pool.submit(() -> tasks.callWithPermit(semaphore, () -> {
                        maxInside.accumulateAndGet(inside.incrementAndGet(), Math::max);
                        Thread.sleep(20);
                        return inside.decrementAndGet();
                    })));
                }
                for (Future<Integer> future : futures) {
                    future.get(5, TimeUnit.SECONDS);
                }
                assertThat(maxInside.get()).isEqualTo(1);
                assertThat(semaphore.availablePermits()).isEqualTo(1);
            } finally {
                pool.shutdownNow();
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement tryCallWithPermit()")
    @DisplayName("tryCallWithPermit")
    class TryCallWithPermitTest {

        @Test
        @DisplayName("Permit available: Optional.of(result), permit returned afterwards")
        void permitAvailable() throws Exception {
            Semaphore semaphore = new Semaphore(1);
            AtomicInteger permitsDuringAction = new AtomicInteger(-1);

            Optional<String> result = tasks.tryCallWithPermit(semaphore, () -> {
                permitsDuringAction.set(semaphore.availablePermits());
                return "ok";
            });

            assertThat(result).contains("ok");
            assertThat(permitsDuringAction.get()).isZero();
            assertThat(semaphore.availablePermits()).isEqualTo(1);
        }

        @Test
        @DisplayName("No permit: empty Optional, action not run, permit count unchanged")
        void noPermit() throws Exception {
            Semaphore semaphore = new Semaphore(0);
            AtomicBoolean ran = new AtomicBoolean(false);

            Optional<String> result = tasks.tryCallWithPermit(semaphore, () -> {
                ran.set(true);
                return "ok";
            });

            assertThat(result).isEmpty();
            assertThat(ran.get()).isFalse();
            assertThat(semaphore.availablePermits()).isZero();
        }

        @Test
        @DisplayName("Does not block when no permit is free")
        void doesNotBlock() {
            Semaphore semaphore = new Semaphore(0);
            assertTimeoutPreemptively(Duration.ofSeconds(2),
                    () -> tasks.tryCallWithPermit(semaphore, () -> "ok"));
        }

        @Test
        @DisplayName("When the action throws: the exception propagates and the permit is returned")
        void actionThrows() {
            Semaphore semaphore = new Semaphore(1);
            IllegalStateException failure = new IllegalStateException("action failed");

            assertThatThrownBy(() -> tasks.tryCallWithPermit(semaphore, () -> {
                throw failure;
            })).isSameAs(failure);

            assertThat(semaphore.availablePermits()).isEqualTo(1);
        }

        @ParameterizedTest(name = "{0} permit(s) available")
        @CsvSource({"1", "2", "5"})
        @DisplayName("The permit count is the same before and after a successful call")
        void permitCountRestored(int permits) throws Exception {
            Semaphore semaphore = new Semaphore(permits);

            Optional<Integer> result = tasks.tryCallWithPermit(semaphore, () -> 99);

            assertThat(result).contains(99);
            assertThat(semaphore.availablePermits()).isEqualTo(permits);
        }
    }
}
