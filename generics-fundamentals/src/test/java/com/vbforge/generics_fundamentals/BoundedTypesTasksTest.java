package com.vbforge.generics_fundamentals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BoundedTypesTasks — your implementations")
class BoundedTypesTasksTest {

    private BoundedTypesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new BoundedTypesTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement max()")
    @DisplayName("Task 1 — max(list)")
    class MaxTest {

        @Test
        @DisplayName("example: largest Integer")
        void integers() {
            assertThat(tasks.max(Arrays.asList(3, 9, 4))).isEqualTo(9);
        }

        @Test
        @DisplayName("example: largest String")
        void strings() {
            assertThat(tasks.max(Arrays.asList("pear", "apple"))).isEqualTo("pear");
        }

        @Test
        @DisplayName("example: single element")
        void singleElement() {
            assertThat(tasks.max(Collections.singletonList(5))).isEqualTo(5);
        }

        @Test
        @DisplayName("example: empty list throws IllegalArgumentException")
        void emptyList() {
            assertThatThrownBy(() -> tasks.max(Collections.<Integer>emptyList()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the maximum may be first or last")
        void positions() {
            assertThat(tasks.max(Arrays.asList(9, 1, 2))).isEqualTo(9);
            assertThat(tasks.max(Arrays.asList(1, 2, 9))).isEqualTo(9);
        }

        @Test
        @DisplayName("negative numbers are handled")
        void negatives() {
            assertThat(tasks.max(Arrays.asList(-5, -2, -9))).isEqualTo(-2);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement isSorted()")
    @DisplayName("Task 2 — isSorted(list)")
    class IsSortedTest {

        @Test
        @DisplayName("example: ascending with equal neighbours is sorted")
        void sortedWithDuplicates() {
            assertThat(tasks.isSorted(Arrays.asList(1, 2, 2, 5))).isTrue();
        }

        @Test
        @DisplayName("example: a descent makes the list unsorted")
        void unsorted() {
            assertThat(tasks.isSorted(Arrays.asList(1, 3, 2))).isFalse();
        }

        @Test
        @DisplayName("example: Strings in order")
        void strings() {
            assertThat(tasks.isSorted(Arrays.asList("a", "b"))).isTrue();
        }

        @Test
        @DisplayName("example: empty list is sorted")
        void emptyList() {
            assertThat(tasks.isSorted(Collections.<Integer>emptyList())).isTrue();
        }

        @Test
        @DisplayName("a single element is sorted")
        void singleElement() {
            assertThat(tasks.isSorted(Collections.singletonList("x"))).isTrue();
        }

        @Test
        @DisplayName("a descent at the very end is detected")
        void lastPairWrong() {
            assertThat(tasks.isSorted(Arrays.asList(1, 2, 3, 0))).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement clamp()")
    @DisplayName("Task 3 — clamp(value, min, max)")
    class ClampTest {

        @ParameterizedTest(name = "clamp({0}, {1}, {2}) = {3}")
        @CsvSource({
                "5, 1, 10, 5",
                "-3, 0, 10, 0",
                "42, 0, 10, 10",
                "1, 1, 10, 1",
                "10, 1, 10, 10"
        })
        @DisplayName("example: limits integers to the closed range")
        void integers(int value, int min, int max, int expected) {
            assertThat(tasks.clamp(value, min, max)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example: works for Strings")
        void strings() {
            assertThat(tasks.clamp("m", "a", "f")).isEqualTo("f");
        }

        @Test
        @DisplayName("example: min greater than max throws IllegalArgumentException")
        void invalidRange() {
            assertThatThrownBy(() -> tasks.clamp(1, 10, 0)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a value inside the range is returned as the same object")
        void sameObjectInsideRange() {
            String value = "c";

            assertThat(tasks.clamp(value, "a", "f")).isSameAs(value);
        }

        @Test
        @DisplayName("min equal to max is a valid range")
        void degenerateRange() {
            assertThat(tasks.clamp(100, 7, 7)).isEqualTo(7);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement average()")
    @DisplayName("Task 4 — average(numbers)")
    class AverageTest {

        @Test
        @DisplayName("example: Integer array")
        void integers() {
            assertThat(tasks.average(new Integer[]{1, 2, 3, 4})).isEqualTo(2.5);
        }

        @Test
        @DisplayName("example: Double array")
        void doubles() {
            assertThat(tasks.average(new Double[]{1.5, 2.5})).isEqualTo(2.0);
        }

        @Test
        @DisplayName("example: empty array is 0.0")
        void empty() {
            assertThat(tasks.average(new Integer[0])).isEqualTo(0.0);
        }

        @Test
        @DisplayName("integer division must not be used: 1 and 2 average to 1.5")
        void noIntegerDivision() {
            assertThat(tasks.average(new Integer[]{1, 2})).isEqualTo(1.5);
        }

        @Test
        @DisplayName("works for Long and negative values")
        void longsAndNegatives() {
            assertThat(tasks.average(new Long[]{-10L, 10L, 30L})).isEqualTo(10.0);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumAbove()")
    @DisplayName("Task 5 — sumAbove(list, threshold)")
    class SumAboveTest {

        @Test
        @DisplayName("example: Integers above 4")
        void integers() {
            assertThat(tasks.sumAbove(Arrays.asList(1, 5, 10), 4)).isEqualTo(15.0);
        }

        @Test
        @DisplayName("example: Doubles above 3.0")
        void doubles() {
            assertThat(tasks.sumAbove(Arrays.asList(2.5, 3.5), 3.0)).isEqualTo(3.5);
        }

        @Test
        @DisplayName("example: the threshold itself is excluded")
        void strictlyGreater() {
            assertThat(tasks.sumAbove(Arrays.asList(1, 2, 3), 3)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("example: empty list gives 0.0")
        void emptyList() {
            assertThat(tasks.sumAbove(Collections.<Integer>emptyList(), 0)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("every element above the threshold is summed")
        void allQualify() {
            assertThat(tasks.sumAbove(Arrays.asList(10L, 20L, 30L), 0L)).isEqualTo(60.0);
        }

        @Test
        @DisplayName("negative thresholds work")
        void negativeThreshold() {
            assertThat(tasks.sumAbove(Arrays.asList(-5, -1, 2), -2)).isEqualTo(1.0);
        }
    }
}
