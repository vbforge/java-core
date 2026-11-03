package com.vbforge.virtual_threads_intro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VirtualThreadPitfallsTasks — your implementations")
class VirtualThreadPitfallsTasksTest {

    private VirtualThreadPitfallsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new VirtualThreadPitfallsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement maxObservedConcurrency()")
    @DisplayName("1. maxObservedConcurrency")
    class MaxObservedConcurrencyTest {

        @Test
        @DisplayName("example: one permit → never more than 1 at a time")
        void onePermit() {
            assertThat(tasks.maxObservedConcurrency(100, 1)).isEqualTo(1);
        }

        @Test
        @DisplayName("example: 50 tasks, 3 permits → between 1 and 3")
        void threePermits() {
            assertThat(tasks.maxObservedConcurrency(50, 3)).isBetween(1, 3);
        }

        @Test
        @DisplayName("zero tasks → 0")
        void zeroTasks() {
            assertThat(tasks.maxObservedConcurrency(0, 5)).isZero();
        }

        @Test
        @DisplayName("a single task → 1, even with many permits")
        void singleTask() {
            assertThat(tasks.maxObservedConcurrency(1, 10)).isEqualTo(1);
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 4, 8})
        @DisplayName("never exceeds the number of permits")
        void neverExceedsPermits(int permits) {
            assertThat(tasks.maxObservedConcurrency(60, permits)).isBetween(1, permits);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement perThreadCounts()")
    @DisplayName("2. perThreadCounts")
    class PerThreadCountsTest {

        @Test
        @DisplayName("example: (3, 1000) → [1000, 1000, 1000]")
        void example() throws Exception {
            assertThat(tasks.perThreadCounts(3, 1000)).containsExactly(1000, 1000, 1000);
        }

        @Test
        @DisplayName("zero increments → all zeros")
        void zeroIncrements() throws Exception {
            assertThat(tasks.perThreadCounts(2, 0)).containsExactly(0, 0);
        }

        @Test
        @DisplayName("zero threads → empty list")
        void zeroThreads() throws Exception {
            assertThat(tasks.perThreadCounts(0, 10)).isEmpty();
        }

        @Test
        @DisplayName("counters are never shared: 50 threads × 200 → fifty 200s")
        void countersAreNotShared() throws Exception {
            List<Integer> counts = tasks.perThreadCounts(50, 200);

            assertThat(counts).hasSize(50);
            assertThat(counts).containsOnly(200);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement withdrawConcurrently()")
    @DisplayName("3. withdrawConcurrently")
    class WithdrawConcurrentlyTest {

        @Test
        @DisplayName("example: (1000, 100, 20) → 0 — balance never goes negative")
        void example() throws Exception {
            assertThat(tasks.withdrawConcurrently(1000, 100, 20)).isZero();
        }

        @Test
        @DisplayName("example: (1000, 10, 20) → 800 — all withdrawals succeed")
        void allSucceed() throws Exception {
            assertThat(tasks.withdrawConcurrently(1000, 10, 20)).isEqualTo(800);
        }

        @ParameterizedTest
        @CsvSource({
                "100,10,20,0",
                "10,5,20,10",
                "50,0,20,50",
                "0,5,20,0",
                "70,10,20,10"
        })
        @DisplayName("final = initial − amount × min(threads, initial / amount)")
        void parametrized(int initial, int threads, int amount, int expected) throws Exception {
            assertThat(tasks.withdrawConcurrently(initial, threads, amount)).isEqualTo(expected);
        }
    }
}
