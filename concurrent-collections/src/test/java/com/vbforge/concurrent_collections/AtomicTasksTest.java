package com.vbforge.concurrent_collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AtomicTasks — your implementations")
class AtomicTasksTest {

    private AtomicTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new AtomicTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countConcurrently()")
    @DisplayName("countConcurrently")
    @Timeout(10)
    class CountConcurrentlyTest {

        @Test
        @DisplayName("4 threads x 10,000 increments (Javadoc example)")
        void example() throws InterruptedException {
            assertThat(tasks.countConcurrently(4, 10_000)).isEqualTo(40_000L);
        }

        @Test
        @DisplayName("a single thread")
        void singleThread() throws InterruptedException {
            assertThat(tasks.countConcurrently(1, 5)).isEqualTo(5L);
        }

        @Test
        @DisplayName("zero increments")
        void zeroIncrements() throws InterruptedException {
            assertThat(tasks.countConcurrently(3, 0)).isEqualTo(0L);
        }

        @ParameterizedTest(name = "{0} threads x {1} increments = {2}")
        @CsvSource({"2, 50000, 100000", "8, 25000, 200000", "16, 10000, 160000"})
        void heavyContention(int threads, int perThread, long expected) throws InterruptedException {
            assertThat(tasks.countConcurrently(threads, perThread)).isEqualTo(expected);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement generateUniqueIds()")
    @DisplayName("generateUniqueIds")
    @Timeout(10)
    class GenerateUniqueIdsTest {

        @Test
        @DisplayName("2 threads x 3 ids give 1..6 (Javadoc example)")
        void example() throws InterruptedException {
            assertThat(tasks.generateUniqueIds(2, 3)).containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L, 6L);
        }

        @Test
        @DisplayName("a single id")
        void singleId() throws InterruptedException {
            assertThat(tasks.generateUniqueIds(1, 1)).containsExactly(1L);
        }

        @Test
        @DisplayName("zero ids per thread gives an empty list")
        void noIds() throws InterruptedException {
            assertThat(tasks.generateUniqueIds(4, 0)).isEmpty();
        }

        @Test
        @DisplayName("8 threads x 1000 ids: no duplicates, no gaps")
        void noDuplicatesNoGaps() throws InterruptedException {
            List<Long> ids = tasks.generateUniqueIds(8, 1000);

            assertThat(ids).hasSize(8000);
            assertThat(ids).doesNotHaveDuplicates();
            assertThat(ids).contains(1L, 8000L);
            assertThat(ids.stream().mapToLong(Long::longValue).sum()).isEqualTo(8000L * 8001L / 2);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement atomicMax()")
    @DisplayName("atomicMax")
    @Timeout(10)
    class AtomicMaxTest {

        @Test
        @DisplayName("raises the value, leaves it alone for smaller and equal candidates (Javadoc example)")
        void example() {
            AtomicInteger target = new AtomicInteger(5);

            assertThat(tasks.atomicMax(target, 9)).isEqualTo(9);
            assertThat(target.get()).isEqualTo(9);

            assertThat(tasks.atomicMax(target, 3)).isEqualTo(9);
            assertThat(target.get()).isEqualTo(9);

            assertThat(tasks.atomicMax(target, 9)).isEqualTo(9);
            assertThat(target.get()).isEqualTo(9);
        }

        @Test
        @DisplayName("negative numbers and Integer.MIN_VALUE")
        void negatives() {
            AtomicInteger target = new AtomicInteger(Integer.MIN_VALUE);

            assertThat(tasks.atomicMax(target, -7)).isEqualTo(-7);
            assertThat(tasks.atomicMax(target, -9)).isEqualTo(-7);
            assertThat(target.get()).isEqualTo(-7);
        }

        @Test
        @DisplayName("4 threads offering 0..399 leave 399")
        void concurrentOffers() throws InterruptedException {
            AtomicInteger target = new AtomicInteger(Integer.MIN_VALUE);

            runInParallel(4, thread -> {
                for (int i = 0; i < 100; i++) {
                    tasks.atomicMax(target, thread * 100 + i);
                }
            });

            assertThat(target.get()).isEqualTo(399);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement initOnce()")
    @DisplayName("initOnce")
    @Timeout(10)
    class InitOnceTest {

        @Test
        @DisplayName("the first call runs init, the second does not (Javadoc example)")
        void example() {
            AtomicBoolean flag = new AtomicBoolean(false);
            AtomicInteger runs = new AtomicInteger();

            assertThat(tasks.initOnce(flag, runs::incrementAndGet)).isTrue();
            assertThat(flag.get()).isTrue();
            assertThat(tasks.initOnce(flag, runs::incrementAndGet)).isFalse();
            assertThat(runs.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("an already-set flag means init is never run")
        void alreadyInitialised() {
            AtomicBoolean flag = new AtomicBoolean(true);
            AtomicInteger runs = new AtomicInteger();

            assertThat(tasks.initOnce(flag, runs::incrementAndGet)).isFalse();
            assertThat(runs.get()).isEqualTo(0);
        }

        @Test
        @DisplayName("8 racing threads: init runs once and exactly one caller gets true")
        void racing() throws InterruptedException {
            AtomicBoolean flag = new AtomicBoolean(false);
            AtomicInteger runs = new AtomicInteger();
            AtomicInteger winners = new AtomicInteger();

            runInParallel(8, i -> {
                if (tasks.initOnce(flag, runs::incrementAndGet)) {
                    winners.incrementAndGet();
                }
            });

            assertThat(runs.get()).isEqualTo(1);
            assertThat(winners.get()).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement transition()")
    @DisplayName("transition")
    @Timeout(10)
    class TransitionTest {

        @Test
        @DisplayName("moves the state only when it matches (Javadoc example)")
        void example() {
            AtomicReference<String> state = new AtomicReference<>("NEW");

            assertThat(tasks.transition(state, "NEW", "RUNNING")).isTrue();
            assertThat(state.get()).isEqualTo("RUNNING");

            assertThat(tasks.transition(state, "NEW", "RUNNING")).isFalse();
            assertThat(state.get()).isEqualTo("RUNNING");

            assertThat(tasks.transition(state, "RUNNING", "DONE")).isTrue();
            assertThat(state.get()).isEqualTo("DONE");
        }

        @Test
        @DisplayName("compares by equals: a different String object with the same text matches")
        void comparesByValue() {
            AtomicReference<String> state = new AtomicReference<>(new String("NEW"));

            assertThat(tasks.transition(state, new String("NEW"), "RUNNING")).isTrue();
            assertThat(state.get()).isEqualTo("RUNNING");
        }

        @Test
        @DisplayName("a mismatch leaves the very same object in place")
        void mismatchKeepsReference() {
            String initial = new String("NEW");
            AtomicReference<String> state = new AtomicReference<>(initial);

            assertThat(tasks.transition(state, "DONE", "RUNNING")).isFalse();
            assertThat(state.get()).isSameAs(initial);
        }

        @Test
        @DisplayName("8 threads racing for NEW -> RUNNING: exactly one wins")
        void exactlyOneWinner() throws InterruptedException {
            AtomicReference<String> state = new AtomicReference<>(new String("NEW"));
            AtomicInteger winners = new AtomicInteger();

            runInParallel(8, i -> {
                if (tasks.transition(state, new String("NEW"), "RUNNING")) {
                    winners.incrementAndGet();
                }
            });

            assertThat(winners.get()).isEqualTo(1);
            assertThat(state.get()).isEqualTo("RUNNING");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement histogram()")
    @DisplayName("histogram")
    @Timeout(10)
    class HistogramTest {

        @Test
        @DisplayName("counts each value (Javadoc example)")
        void example() throws InterruptedException {
            assertThat(tasks.histogram(new int[]{0, 1, 1, 2, 2, 2}, 4, 2)).containsExactly(1, 2, 3, 0);
        }

        @Test
        @DisplayName("no values: every bucket is 0")
        void noValues() throws InterruptedException {
            assertThat(tasks.histogram(new int[0], 3, 4)).containsExactly(0, 0, 0);
        }

        @Test
        @DisplayName("more threads than values")
        void moreThreadsThanValues() throws InterruptedException {
            assertThat(tasks.histogram(new int[]{2, 2}, 3, 8)).containsExactly(0, 0, 2);
        }

        @Test
        @DisplayName("a single bucket")
        void singleBucket() throws InterruptedException {
            assertThat(tasks.histogram(new int[]{0, 0, 0, 0}, 1, 3)).containsExactly(4);
        }

        @ParameterizedTest(name = "{0} thread(s) give the same histogram")
        @CsvSource({"1", "2", "4", "8"})
        void sameResultForAnyThreadCount(int threads) throws InterruptedException {
            int[] values = new int[30_000];
            for (int i = 0; i < values.length; i++) {
                values[i] = i % 3;
            }

            assertThat(tasks.histogram(values, 3, threads)).containsExactly(10_000, 10_000, 10_000);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: start N threads together, wait for all of them
    // -------------------------------------------------------------------------

    @FunctionalInterface
    private interface Body {
        void run(int threadIndex) throws Exception;
    }

    private static void runInParallel(int threads, Body body) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int index = i;
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    body.run(index);
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                }
            }, "worker-" + i);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
        if (failure.get() != null) {
            throw new AssertionError("a worker thread failed: " + failure.get(), failure.get());
        }
    }
}
