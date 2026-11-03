package com.vbforge.java_memory_model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReorderingTasks — your implementations")
@Timeout(value = 120, unit = TimeUnit.SECONDS)
class ReorderingTasksTest {

    private ReorderingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ReorderingTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement storeBufferingOutcomes()")
    @DisplayName("storeBufferingOutcomes")
    class StoreBufferingOutcomesTest {

        @Test
        @DisplayName("0 iterations give [0, 0, 0, 0]")
        void javadocZeroExample() {
            assertThat(tasks.storeBufferingOutcomes(0)).containsExactly(0, 0, 0, 0);
        }

        @Test
        @DisplayName("1000 iterations: four buckets that add up to 1000, bucket 0 empty")
        void javadocThousandExample() {
            int[] outcomes = tasks.storeBufferingOutcomes(1000);

            assertThat(outcomes).hasSize(4);
            assertThat(Arrays.stream(outcomes).sum()).isEqualTo(1000);
            assertThat(outcomes[0]).isZero();
        }

        @Test
        @DisplayName("1 iteration lands in exactly one bucket")
        void singleIteration() {
            int[] outcomes = tasks.storeBufferingOutcomes(1);

            assertThat(Arrays.stream(outcomes).sum()).isEqualTo(1);
            assertThat(outcomes[0]).isZero();
        }

        @ParameterizedTest(name = "{0} iterations")
        @DisplayName("(0,0) never occurs, whatever the number of rounds")
        @CsvSource({"10", "500", "3000"})
        void bothZeroNeverOccurs(int iterations) {
            assertThat(tasks.storeBufferingOutcomes(iterations)[0]).isZero();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countStaleReads()")
    @DisplayName("countStaleReads")
    class CountStaleReadsTest {

        @Test
        @DisplayName("0 iterations give 0")
        void zeroIterations() {
            assertThat(tasks.countStaleReads(0)).isZero();
        }

        @Test
        @DisplayName("1 iteration gives 0")
        void singleIteration() {
            assertThat(tasks.countStaleReads(1)).isZero();
        }

        @ParameterizedTest(name = "{0} iterations")
        @DisplayName("with a volatile flag the reader never sees stale data")
        @CsvSource({"100", "1000", "3000"})
        void parametrized(int iterations) {
            assertThat(tasks.countStaleReads(iterations)).isZero();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countStaleReadsSynchronized()")
    @DisplayName("countStaleReadsSynchronized")
    class CountStaleReadsSynchronizedTest {

        @Test
        @DisplayName("0 iterations give 0")
        void zeroIterations() {
            assertThat(tasks.countStaleReadsSynchronized(0)).isZero();
        }

        @Test
        @DisplayName("1000 iterations give 0")
        void javadocExample() {
            assertThat(tasks.countStaleReadsSynchronized(1000)).isZero();
        }

        @ParameterizedTest(name = "{0} iterations")
        @DisplayName("with one shared lock the reader never sees stale data")
        @CsvSource({"1", "100", "3000"})
        void parametrized(int iterations) {
            assertThat(tasks.countStaleReadsSynchronized(iterations)).isZero();
        }
    }
}
