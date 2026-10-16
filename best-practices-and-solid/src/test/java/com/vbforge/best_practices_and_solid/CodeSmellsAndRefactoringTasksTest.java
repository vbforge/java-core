package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.CodeSmellsAndRefactoringDemo.IntRange;
import com.vbforge.best_practices_and_solid.CodeSmellsAndRefactoringDemo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.OptionalInt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CodeSmellsAndRefactoringTasks — your implementations")
class CodeSmellsAndRefactoringTasksTest {

    private CodeSmellsAndRefactoringTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new CodeSmellsAndRefactoringTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstDifference()")
    @DisplayName("firstDifference(legacy, refactored, from, to)")
    class FirstDifferenceTest {

        @Test
        @DisplayName("equivalent functions produce an empty result")
        void equivalentFunctions() {
            assertThat(tasks.firstDifference(x -> x * 2, x -> x + x, 0, 100)).isEmpty();
        }

        @Test
        @DisplayName("returns the first input where the results differ")
        void firstMismatch() {
            assertThat(tasks.firstDifference(x -> x * x, x -> x + x, 0, 5)).isEqualTo(OptionalInt.of(1));
        }

        @Test
        @DisplayName("inputs where the results happen to agree are skipped")
        void agreeingInputsAreSkipped() {
            assertThat(tasks.firstDifference(x -> x * x, x -> x + x, 2, 5)).isEqualTo(OptionalInt.of(3));
        }

        @Test
        @DisplayName("an empty range (from > to) compares nothing")
        void emptyRange() {
            assertThat(tasks.firstDifference(x -> x, x -> x + 1, 5, 1)).isEmpty();
        }

        @Test
        @DisplayName("both ends of the range are included")
        void bothEndsIncluded() {
            assertThat(tasks.firstDifference(x -> x, x -> x == 7 ? 0 : x, 7, 7)).isEqualTo(OptionalInt.of(7));
            assertThat(tasks.firstDifference(x -> x, x -> x == 9 ? 0 : x, 1, 9)).isEqualTo(OptionalInt.of(9));
        }

        @Test
        @DisplayName("catches a boundary bug in a refactored pricing function")
        void catchesBoundaryBug() {
            assertThat(tasks.firstDifference(
                    CodeSmellsAndRefactoringDemo::legacyPrice,
                    q -> q >= 11 ? (q >= 50 ? q * 80 : q * 90) : q * 100,
                    0, 60)).isEqualTo(OptionalInt.of(10));
        }

        @Test
        @DisplayName("the real refactoring from the demo is equivalent over 0..1000")
        void realRefactoringIsEquivalent() {
            assertThat(tasks.firstDifference(
                    CodeSmellsAndRefactoringDemo::legacyPrice, CodeSmellsAndRefactoringDemo::price, 0, 1000)).isEmpty();
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement sum()")
    @DisplayName("sum(amounts, currency)")
    class SumTest {

        @Test
        @DisplayName("adds amounts of the same currency")
        void addsAmounts() {
            assertThat(tasks.sum(Arrays.asList(Money.of("USD", 150), Money.of("USD", 225)), "USD"))
                    .isEqualTo(Money.of("USD", 375));
        }

        @Test
        @DisplayName("an empty list is zero in the requested currency")
        void emptyList() {
            assertThat(tasks.sum(Collections.<Money>emptyList(), "EUR")).isEqualTo(Money.of("EUR", 0));
        }

        @Test
        @DisplayName("a single amount is returned as an equal value")
        void singleAmount() {
            assertThat(tasks.sum(Collections.singletonList(Money.of("USD", 99)), "USD")).isEqualTo(Money.of("USD", 99));
        }

        @Test
        @DisplayName("mixing currencies in the list fails")
        void mixedCurrencies() {
            assertThatThrownBy(() -> tasks.sum(Arrays.asList(Money.of("USD", 100), Money.of("EUR", 100)), "USD"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an amount in a different currency than requested fails")
        void wrongCurrency() {
            assertThatThrownBy(() -> tasks.sum(Collections.singletonList(Money.of("EUR", 100)), "USD"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement overlaps()")
    @DisplayName("overlaps(a, b)")
    class OverlapsTest {

        @ParameterizedTest(name = "[{0},{1}] and [{2},{3}] -> {4}")
        @CsvSource({
                "1,  5,  5,  9, true",
                "1,  4,  5,  9, false",
                "1, 10,  3,  4, true",
                "5,  9,  1,  5, true",
                "5,  9,  1,  4, false",
                "3,  3,  3,  3, true",
                "3,  3,  4,  4, false"
        })
        @DisplayName("inclusive ranges overlap when they share at least one integer")
        void overlapCases(int aStart, int aEnd, int bStart, int bEnd, boolean expected) {
            assertThat(tasks.overlaps(new IntRange(aStart, aEnd), new IntRange(bStart, bEnd))).isEqualTo(expected);
        }

        @Test
        @DisplayName("the result does not depend on argument order")
        void symmetric() {
            IntRange a = new IntRange(1, 4);
            IntRange b = new IntRange(4, 8);
            IntRange c = new IntRange(6, 8);

            assertThat(tasks.overlaps(a, b)).isEqualTo(tasks.overlaps(b, a)).isTrue();
            assertThat(tasks.overlaps(a, c)).isEqualTo(tasks.overlaps(c, a)).isFalse();
        }
    }
}
