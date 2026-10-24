package com.vbforge.collections_internals_and_big_o;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BigODemo — API contracts")
class BigODemoTest {

    private static int[] ascending(int n) {
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = i;
        }
        return data;
    }

    @Nested
    @DisplayName("1. Linear vs binary search")
    class SearchTest {

        @Test
        @DisplayName("linear search examines every element when the target is absent")
        void linearAbsent() {
            assertThat(BigODemo.linearSearchStepsDemo(ascending(1_024), -1)).isEqualTo(1_024);
        }

        @Test
        @DisplayName("linear search stops at the target")
        void linearFound() {
            assertThat(BigODemo.linearSearchStepsDemo(ascending(1_024), 0)).isEqualTo(1);
            assertThat(BigODemo.linearSearchStepsDemo(ascending(1_024), 99)).isEqualTo(100);
        }

        @Test
        @DisplayName("binary search needs 11 comparisons in the worst case for 1 024 elements")
        void binaryWorstCase() {
            assertThat(BigODemo.binarySearchStepsDemo(ascending(1_024), Integer.MAX_VALUE)).isEqualTo(11);
        }

        @Test
        @DisplayName("doubling the input DOUBLES the linear cost but adds only ONE binary step")
        void doublingTheInput() {
            int linearSmall = BigODemo.linearSearchStepsDemo(ascending(1_024), Integer.MAX_VALUE);
            int linearBig = BigODemo.linearSearchStepsDemo(ascending(2_048), Integer.MAX_VALUE);
            int binarySmall = BigODemo.binarySearchStepsDemo(ascending(1_024), Integer.MAX_VALUE);
            int binaryBig = BigODemo.binarySearchStepsDemo(ascending(2_048), Integer.MAX_VALUE);

            assertThat(linearBig).isEqualTo(2 * linearSmall);
            assertThat(binaryBig).isEqualTo(binarySmall + 1);
        }

        @Test
        @DisplayName("a million elements: a million linear steps, at most 21 binary steps")
        void aMillionElements() {
            int[] data = ascending(1_048_576);

            assertThat(BigODemo.linearSearchStepsDemo(data, Integer.MAX_VALUE)).isEqualTo(1_048_576);
            assertThat(BigODemo.binarySearchStepsDemo(data, Integer.MAX_VALUE)).isEqualTo(21);
        }

        @Test
        @DisplayName("binary search finds present targets with a few comparisons too")
        void binaryFound() {
            int[] data = ascending(1_000);

            for (int target = 0; target < 1_000; target += 37) {
                assertThat(BigODemo.binarySearchStepsDemo(data, target)).isBetween(1, 10);
            }
        }

        @Test
        @DisplayName("searching an empty array costs nothing")
        void emptyArray() {
            assertThat(BigODemo.linearSearchStepsDemo(new int[0], 1)).isZero();
            assertThat(BigODemo.binarySearchStepsDemo(new int[0], 1)).isZero();
        }
    }

    @Nested
    @DisplayName("2. Growth rates")
    class GrowthRatesTest {

        @ParameterizedTest(name = "{0} for n = 1024 is {1}")
        @CsvSource({
                "O(1),        1",
                "O(log n),    10",
                "O(n),        1024",
                "O(n log n),  10240",
                "O(n^2),      1048576"
        })
        @DisplayName("the number of steps for n = 1024")
        void stepsFor1024(String complexity, long expected) {
            assertThat(BigODemo.stepsDemo(complexity, 1_024)).isEqualTo(expected);
        }

        @Test
        @DisplayName("doubling n: O(1) stays, O(log n) gains one, O(n) doubles, O(n^2) quadruples")
        void doublingN() {
            assertThat(BigODemo.stepsDemo("O(1)", 2_000)).isEqualTo(BigODemo.stepsDemo("O(1)", 1_000));
            assertThat(BigODemo.stepsDemo("O(log n)", 2_000)).isEqualTo(BigODemo.stepsDemo("O(log n)", 1_000) + 1);
            assertThat(BigODemo.stepsDemo("O(n)", 2_000)).isEqualTo(2 * BigODemo.stepsDemo("O(n)", 1_000));
            assertThat(BigODemo.stepsDemo("O(n^2)", 2_000)).isEqualTo(4 * BigODemo.stepsDemo("O(n^2)", 1_000));
        }

        @Test
        @DisplayName("O(2^n): adding ONE element doubles the cost")
        void exponential() {
            assertThat(BigODemo.stepsDemo("O(2^n)", 20)).isEqualTo(1_048_576);
            assertThat(BigODemo.stepsDemo("O(2^n)", 21)).isEqualTo(2 * BigODemo.stepsDemo("O(2^n)", 20));
        }

        @Test
        @DisplayName("O(n^2) of a million needs a long — it does not fit in an int")
        void quadraticNeedsLong() {
            assertThat(BigODemo.stepsDemo("O(n^2)", 1_000_000)).isEqualTo(1_000_000_000_000L);
        }

        @Test
        @DisplayName("O(2^n) beyond n = 62 is rejected (it would overflow)")
        void exponentialOverflow() {
            assertThatThrownBy(() -> BigODemo.stepsDemo("O(2^n)", 63)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an unknown class or an n below 1 is rejected")
        void invalidArguments() {
            assertThatThrownBy(() -> BigODemo.stepsDemo("O(n!)", 10)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> BigODemo.stepsDemo("O(n)", 0)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("3. Quadratic vs linear: finding a duplicate")
    class DuplicateTest {

        @Test
        @DisplayName("100 distinct values cost 4950 pairwise comparisons (n(n-1)/2) but only 100 hash-set steps")
        void hundredValues() {
            int[] data = ascending(100);

            assertThat(BigODemo.quadraticDuplicateCheckStepsDemo(data)).isEqualTo(4_950);
            assertThat(BigODemo.hashSetDuplicateCheckStepsDemo(data)).isEqualTo(100);
        }

        @Test
        @DisplayName("10 000 distinct values: about 50 million comparisons against 10 000 steps")
        void tenThousandValues() {
            int[] data = ascending(10_000);

            assertThat(BigODemo.quadraticDuplicateCheckStepsDemo(data)).isEqualTo(49_995_000L);
            assertThat(BigODemo.hashSetDuplicateCheckStepsDemo(data)).isEqualTo(10_000);
        }

        @Test
        @DisplayName("both stop early when a duplicate is found")
        void earlyExit() {
            int[] data = {1, 2, 3, 2, 9, 9, 9};

            assertThat(BigODemo.hashSetDuplicateCheckStepsDemo(data)).isEqualTo(4);
            assertThat(BigODemo.quadraticDuplicateCheckStepsDemo(data)).isEqualTo(8);   // 6 for index 0, then 2 for index 1
        }

        @Test
        @DisplayName("an empty array needs no steps")
        void emptyArray() {
            assertThat(BigODemo.quadraticDuplicateCheckStepsDemo(new int[0])).isZero();
            assertThat(BigODemo.hashSetDuplicateCheckStepsDemo(new int[0])).isZero();
        }
    }

    @Nested
    @DisplayName("4. Amortised O(1)")
    class AmortisedTest {

        @Test
        @DisplayName("an append costs about 3.4 element writes on average, even for a million appends")
        void constantAverage() {
            double small = BigODemo.averageCostPerAppendDemo(1_000);
            double large = BigODemo.averageCostPerAppendDemo(1_000_000);

            assertThat(small).isBetween(1.0, 4.0);
            assertThat(large).isBetween(1.0, 4.0);
        }

        @Test
        @DisplayName("the average does not grow with n: 1000 times more appends, nearly the same cost per append")
        void doesNotGrowWithN() {
            double small = BigODemo.averageCostPerAppendDemo(1_000);
            double large = BigODemo.averageCostPerAppendDemo(1_000_000);

            assertThat(Math.abs(large - small)).isLessThan(1.0);
        }

        @Test
        @DisplayName("up to the initial capacity every append costs exactly one write")
        void noGrowthNoCopies() {
            assertThat(BigODemo.averageCostPerAppendDemo(10)).isEqualTo(1.0);
        }
    }
}
