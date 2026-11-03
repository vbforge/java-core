package com.vbforge.java_memory_model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VisibilityAndAtomicityTasks — your implementations")
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class VisibilityAndAtomicityTasksTest {

    private VisibilityAndAtomicityTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new VisibilityAndAtomicityTasks();
    }

    private static void runConcurrently(int threads, Runnable body) throws InterruptedException {
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(body);
            workers[i].start();
        }
        for (Thread worker : workers) {
            worker.join();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countWithLock()")
    @DisplayName("countWithLock")
    class CountWithLockTest {

        @Test
        @DisplayName("one thread counts to 1000")
        void singleThread() {
            assertThat(tasks.countWithLock(1, 1000)).isEqualTo(1000L);
        }

        @Test
        @DisplayName("4 threads x 5000 = 20000")
        void fourThreads() {
            assertThat(tasks.countWithLock(4, 5000)).isEqualTo(20_000L);
        }

        @Test
        @DisplayName("zero increments give 0")
        void zeroIncrements() {
            assertThat(tasks.countWithLock(8, 0)).isZero();
        }

        @ParameterizedTest(name = "{0} threads x {1} increments")
        @DisplayName("total is always threads x increments")
        @CsvSource({"2,10000", "8,10000", "16,2500", "3,1"})
        void parametrized(int threads, int perThread) {
            assertThat(tasks.countWithLock(threads, perThread)).isEqualTo((long) threads * perThread);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countWithAtomic()")
    @DisplayName("countWithAtomic")
    class CountWithAtomicTest {

        @Test
        @DisplayName("one thread counts to 1000")
        void singleThread() {
            assertThat(tasks.countWithAtomic(1, 1000)).isEqualTo(1000L);
        }

        @Test
        @DisplayName("4 threads x 5000 = 20000")
        void fourThreads() {
            assertThat(tasks.countWithAtomic(4, 5000)).isEqualTo(20_000L);
        }

        @Test
        @DisplayName("zero increments give 0")
        void zeroIncrements() {
            assertThat(tasks.countWithAtomic(8, 0)).isZero();
        }

        @ParameterizedTest(name = "{0} threads x {1} increments")
        @DisplayName("total is always threads x increments")
        @CsvSource({"2,10000", "8,10000", "16,2500", "3,1"})
        void parametrized(int threads, int perThread) {
            assertThat(tasks.countWithAtomic(threads, perThread)).isEqualTo((long) threads * perThread);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement workerStopsOnFlag()")
    @DisplayName("workerStopsOnFlag")
    class WorkerStopsOnFlagTest {

        @Test
        @DisplayName("worker terminates after 50 ms")
        void stopsAfterShortRun() {
            assertThat(tasks.workerStopsOnFlag(50)).isTrue();
        }

        @Test
        @DisplayName("worker terminates when the flag is raised immediately")
        void stopsImmediately() {
            assertThat(tasks.workerStopsOnFlag(0)).isTrue();
        }

        @Test
        @DisplayName("worker terminates after a longer run, when the loop is surely JIT-compiled")
        void stopsAfterLongRun() {
            assertThat(tasks.workerStopsOnFlag(300)).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement incrementIfBelow()")
    @DisplayName("incrementIfBelow")
    class IncrementIfBelowTest {

        @Test
        @DisplayName("increments twice, then refuses at the limit")
        void javadocExample() {
            AtomicInteger counter = new AtomicInteger(0);

            assertThat(tasks.incrementIfBelow(counter, 2)).isTrue();
            assertThat(tasks.incrementIfBelow(counter, 2)).isTrue();
            assertThat(tasks.incrementIfBelow(counter, 2)).isFalse();
            assertThat(counter.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("counter already at the limit stays unchanged")
        void atLimit() {
            AtomicInteger counter = new AtomicInteger(5);

            assertThat(tasks.incrementIfBelow(counter, 5)).isFalse();
            assertThat(counter.get()).isEqualTo(5);
        }

        @Test
        @DisplayName("counter above the limit is never decreased or touched")
        void aboveLimit() {
            AtomicInteger counter = new AtomicInteger(9);

            assertThat(tasks.incrementIfBelow(counter, 5)).isFalse();
            assertThat(counter.get()).isEqualTo(9);
        }

        @Test
        @DisplayName("negative start values below the limit are incremented")
        void negativeStart() {
            AtomicInteger counter = new AtomicInteger(-3);

            assertThat(tasks.incrementIfBelow(counter, 0)).isTrue();
            assertThat(counter.get()).isEqualTo(-2);
        }

        @Test
        @DisplayName("8 racing threads never push the counter past the limit; exactly 'limit' calls succeed")
        void concurrentCallsRespectLimit() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger(0);
            AtomicInteger successes = new AtomicInteger(0);

            runConcurrently(8, () -> {
                for (int i = 0; i < 1000; i++) {
                    if (tasks.incrementIfBelow(counter, 100)) {
                        successes.incrementAndGet();
                    }
                }
            });

            assertThat(counter.get()).isEqualTo(100);
            assertThat(successes.get()).isEqualTo(100);
        }
    }
}
