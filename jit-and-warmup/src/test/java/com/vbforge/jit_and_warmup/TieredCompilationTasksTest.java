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

@DisplayName("TieredCompilationTasks — your implementations")
class TieredCompilationTasksTest {

    private TieredCompilationTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TieredCompilationTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement tierForInvocations()")
    @DisplayName("tierForInvocations")
    class TierForInvocationsTest {

        @ParameterizedTest
        @CsvSource({
                "0, INTERPRETER",
                "1, INTERPRETER",
                "199, INTERPRETER",
                "200, C1",
                "4999, C1",
                "5000, C2",
                "1000000, C2"
        })
        @DisplayName("Javadoc examples and boundaries")
        void parametrized(int invocations, String expected) {
            assertThat(tasks.tierForInvocations(invocations)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Largest int is still C2")
        void maxValue() {
            assertThat(tasks.tierForInvocations(Integer.MAX_VALUE)).isEqualTo("C2");
        }

        @Test
        @DisplayName("Negative counts are rejected")
        void negative() {
            assertThatThrownBy(() -> tasks.tierForInvocations(-1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement median()")
    @DisplayName("median")
    class MedianTest {

        @Test
        @DisplayName("Odd count: unsorted input gives the middle value")
        void odd() {
            assertThat(tasks.median(new long[]{5, 1, 3})).isEqualTo(3.0);
        }

        @Test
        @DisplayName("Even count: average of the two middle values")
        void even() {
            assertThat(tasks.median(new long[]{4, 1, 3, 2})).isEqualTo(2.5);
        }

        @Test
        @DisplayName("Single value is its own median")
        void single() {
            assertThat(tasks.median(new long[]{7})).isEqualTo(7.0);
        }

        @Test
        @DisplayName("One huge outlier does not move the median")
        void outlier() {
            assertThat(tasks.median(new long[]{10, 11, 12, 13, 1_000_000_000L})).isEqualTo(12.0);
        }

        @Test
        @DisplayName("Large values do not overflow when averaging")
        void noOverflow() {
            long big = Long.MAX_VALUE;
            assertThat(tasks.median(new long[]{big, big})).isEqualTo((double) big);
        }

        @Test
        @DisplayName("Input array is left unsorted")
        void doesNotMutateInput() {
            long[] input = {5, 1, 3};
            tasks.median(input);
            assertThat(input).containsExactly(5, 1, 3);
        }

        @Test
        @DisplayName("Empty and null arrays are rejected")
        void emptyAndNull() {
            assertThatThrownBy(() -> tasks.median(new long[0]))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.median(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement findWarmupIndex()")
    @DisplayName("findWarmupIndex")
    class FindWarmupIndexTest {

        @Test
        @DisplayName("Typical warm-up curve")
        void typicalCurve() {
            assertThat(tasks.findWarmupIndex(new long[]{900, 500, 300, 110, 100, 105, 100}, 0.2)).isEqualTo(3);
        }

        @Test
        @DisplayName("A late spike resets the warm-up point to just after it")
        void lateSpike() {
            assertThat(tasks.findWarmupIndex(new long[]{100, 100, 400, 100, 100}, 0.1)).isEqualTo(3);
        }

        @Test
        @DisplayName("Already steady from the start")
        void alreadySteady() {
            assertThat(tasks.findWarmupIndex(new long[]{100, 100, 100}, 0.1)).isEqualTo(0);
        }

        @Test
        @DisplayName("Zero tolerance requires values no larger than the last one")
        void zeroTolerance() {
            assertThat(tasks.findWarmupIndex(new long[]{300, 200, 100}, 0.0)).isEqualTo(2);
        }

        @Test
        @DisplayName("Single sample is index 0")
        void single() {
            assertThat(tasks.findWarmupIndex(new long[]{42}, 0.5)).isEqualTo(0);
        }

        @Test
        @DisplayName("Input array is not modified")
        void doesNotMutateInput() {
            long[] input = {900, 500, 300, 110, 100, 105, 100};
            tasks.findWarmupIndex(input, 0.2);
            assertThat(input).containsExactly(900, 500, 300, 110, 100, 105, 100);
        }

        @Test
        @DisplayName("Empty array and negative tolerance are rejected")
        void invalid() {
            assertThatThrownBy(() -> tasks.findWarmupIndex(new long[0], 0.1))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.findWarmupIndex(new long[]{1, 2}, -0.1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
