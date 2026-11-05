package com.vbforge.jit_and_warmup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("MicrobenchmarkTasks — your implementations")
class MicrobenchmarkTasksTest {

    private MicrobenchmarkTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new MicrobenchmarkTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement trimmedMean()")
    @DisplayName("trimmedMean")
    class TrimmedMeanTest {

        @Test
        @DisplayName("Drops the smallest and largest outliers")
        void dropsOutliers() {
            assertThat(tasks.trimmedMean(new long[]{100, 1, 2, 3, 900}, 1)).isCloseTo(35.0, within(1e-9));
        }

        @Test
        @DisplayName("Trim 0 is the plain mean")
        void trimZero() {
            assertThat(tasks.trimmedMean(new long[]{1, 2, 3}, 0)).isCloseTo(2.0, within(1e-9));
            assertThat(tasks.trimmedMean(new long[]{4, 8}, 0)).isCloseTo(6.0, within(1e-9));
        }

        @Test
        @DisplayName("Trimming leaves exactly one value")
        void leavesOne() {
            assertThat(tasks.trimmedMean(new long[]{9, 1, 5, 7, 3}, 2)).isCloseTo(5.0, within(1e-9));
        }

        @Test
        @DisplayName("Duplicates are handled")
        void duplicates() {
            assertThat(tasks.trimmedMean(new long[]{5, 5, 5, 5}, 1)).isCloseTo(5.0, within(1e-9));
        }

        @Test
        @DisplayName("Input array is left unsorted")
        void doesNotMutateInput() {
            long[] input = {100, 1, 2, 3, 900};
            tasks.trimmedMean(input, 1);
            assertThat(input).containsExactly(100, 1, 2, 3, 900);
        }

        @Test
        @DisplayName("Nothing left to average is rejected")
        void tooMuchTrim() {
            assertThatThrownBy(() -> tasks.trimmedMean(new long[]{1, 2, 3}, 2))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.trimmedMean(new long[]{1, 2}, 1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Negative trim and null input are rejected")
        void invalid() {
            assertThatThrownBy(() -> tasks.trimmedMean(new long[]{1, 2, 3}, -1))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.trimmedMean(null, 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement opsPerSecond()")
    @DisplayName("opsPerSecond")
    class OpsPerSecondTest {

        @ParameterizedTest
        @CsvSource({
                "1000000, 500000000, 2000000.0",
                "1, 1000000000, 1.0",
                "0, 1000, 0.0",
                "3, 2000000000, 1.5",
                "1000, 1000, 1000000000.0"
        })
        @DisplayName("Javadoc examples and more")
        void parametrized(long operations, long nanos, double expected) {
            assertThat(tasks.opsPerSecond(operations, nanos)).isCloseTo(expected, within(1e-6));
        }

        @Test
        @DisplayName("Does not truncate like integer division")
        void noTruncation() {
            assertThat(tasks.opsPerSecond(1, 3_000_000_000L)).isCloseTo(1.0 / 3.0, within(1e-9));
        }

        @Test
        @DisplayName("Zero or negative elapsed time is rejected")
        void invalidTime() {
            assertThatThrownBy(() -> tasks.opsPerSecond(10, 0))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.opsPerSecond(10, -5))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Negative operation count is rejected")
        void negativeOps() {
            assertThatThrownBy(() -> tasks.opsPerSecond(-1, 1_000))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement coefficientOfVariation()")
    @DisplayName("coefficientOfVariation")
    class CoefficientOfVariationTest {

        @Test
        @DisplayName("Identical samples have no variation")
        void identical() {
            assertThat(tasks.coefficientOfVariation(new double[]{10, 10, 10})).isCloseTo(0.0, within(1e-12));
        }

        @Test
        @DisplayName("Classic example: mean 5, population std-dev 2 → 0.4")
        void classic() {
            assertThat(tasks.coefficientOfVariation(new double[]{2, 4, 4, 4, 5, 5, 7, 9}))
                    .isCloseTo(0.4, within(1e-9));
        }

        @Test
        @DisplayName("Single sample has no variation")
        void single() {
            assertThat(tasks.coefficientOfVariation(new double[]{5})).isCloseTo(0.0, within(1e-12));
        }

        @Test
        @DisplayName("Two samples: mean 15, std-dev 5 → 1/3")
        void two() {
            assertThat(tasks.coefficientOfVariation(new double[]{10, 20}))
                    .isCloseTo(1.0 / 3.0, within(1e-9));
        }

        @Test
        @DisplayName("Empty and null arrays are rejected")
        void emptyAndNull() {
            assertThatThrownBy(() -> tasks.coefficientOfVariation(new double[0]))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.coefficientOfVariation(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("A zero mean is rejected")
        void zeroMean() {
            assertThatThrownBy(() -> tasks.coefficientOfVariation(new double[]{0, 0}))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
