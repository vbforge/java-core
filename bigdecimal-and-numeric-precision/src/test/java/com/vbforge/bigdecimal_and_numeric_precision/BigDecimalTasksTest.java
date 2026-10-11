package com.vbforge.bigdecimal_and_numeric_precision;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BigDecimalTasksTest — tests for every method in {@link BigDecimalTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code BigDecimalTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=BigDecimalTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("BigDecimalTasks — your implementations")
class BigDecimalTasksTest {

    private BigDecimalTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new BigDecimalTasks();
    }

    // ── Task 1: calculatePriceWithTax ─────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement calculatePriceWithTax()")
    @DisplayName("Task 1 — calculatePriceWithTax()")
    class CalculatePriceWithTaxTest {

        @Test
        @DisplayName("19.99 with 8% tax → 21.59")
        void classic() {
            BigDecimal result = tasks.calculatePriceWithTax(
                    new BigDecimal("19.99"), new BigDecimal("0.08"));
            assertThat(result).isEqualByComparingTo(new BigDecimal("21.59"));
        }

        @Test
        @DisplayName("returned value has scale 2")
        void scaleIsTwo() {
            BigDecimal result = tasks.calculatePriceWithTax(
                    new BigDecimal("19.99"), new BigDecimal("0.08"));
            assertThat(result.scale()).isEqualTo(2);
        }

        @Test
        @DisplayName("100.00 with 7% tax → 107.00")
        void roundNumbers() {
            BigDecimal result = tasks.calculatePriceWithTax(
                    new BigDecimal("100.00"), new BigDecimal("0.07"));
            assertThat(result).isEqualByComparingTo(new BigDecimal("107.00"));
        }

        @Test
        @DisplayName("zero price → zero total with scale 2")
        void zeroPrice() {
            BigDecimal result = tasks.calculatePriceWithTax(
                    new BigDecimal("0.00"), new BigDecimal("0.08"));
            assertThat(result).isEqualByComparingTo(new BigDecimal("0.00"));
            assertThat(result.scale()).isEqualTo(2);
        }

        @Test
        @DisplayName("zero tax rate → total equals price, with scale 2")
        void zeroTaxRate() {
            BigDecimal result = tasks.calculatePriceWithTax(
                    new BigDecimal("9.99"), new BigDecimal("0.00"));
            assertThat(result).isEqualByComparingTo(new BigDecimal("9.99"));
        }

        @Test
        @DisplayName("tax is rounded HALF_UP — 1.5992 becomes 1.60")
        void halfUpRoundingOnTax() {
            // 19.99 * 0.08 = 1.5992 → HALF_UP → 1.60
            // If DOWN or wrong rounding were used, total would be 21.58
            BigDecimal result = tasks.calculatePriceWithTax(
                    new BigDecimal("19.99"), new BigDecimal("0.08"));
            assertThat(result).isEqualByComparingTo(new BigDecimal("21.59"));
        }
    }

    // ── Task 2: sumPrices ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sumPrices()")
    @DisplayName("Task 2 — sumPrices()")
    class SumPricesTest {

        @Test
        @DisplayName("sums a list of prices exactly")
        void basicSum() {
            BigDecimal result = tasks.sumPrices(List.of(
                    new BigDecimal("1.10"),
                    new BigDecimal("2.20"),
                    new BigDecimal("3.30")));
            assertThat(result).isEqualByComparingTo(new BigDecimal("6.60"));
        }

        @Test
        @DisplayName("empty list → 0.00 with scale 2")
        void emptyList() {
            BigDecimal result = tasks.sumPrices(List.of());
            assertThat(result).isEqualByComparingTo(new BigDecimal("0.00"));
            assertThat(result.scale()).isEqualTo(2);
        }

        @Test
        @DisplayName("many one-cent additions are exact — no floating-point drift")
        void noDrift() {
            BigDecimal result = tasks.sumPrices(List.of(
                    new BigDecimal("0.01"),
                    new BigDecimal("0.01"),
                    new BigDecimal("0.01")));
            assertThat(result).isEqualByComparingTo(new BigDecimal("0.03"));
        }

        @Test
        @DisplayName("result has scale 2")
        void scaleIsTwo() {
            BigDecimal result = tasks.sumPrices(List.of(new BigDecimal("1.5")));
            assertThat(result.scale()).isEqualTo(2);
        }
    }

    // ── Task 3: splitEqually ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement splitEqually()")
    @DisplayName("Task 3 — splitEqually()")
    class SplitEquallyTest {

        @Test
        @DisplayName("10.00 / 3 → [3.34, 3.33, 3.33]")
        void threeParts() {
            List<BigDecimal> parts = tasks.splitEqually(new BigDecimal("10.00"), 3);
            assertThat(parts).hasSize(3);
            assertThat(parts.get(0)).isEqualByComparingTo(new BigDecimal("3.34"));
            assertThat(parts.get(1)).isEqualByComparingTo(new BigDecimal("3.33"));
            assertThat(parts.get(2)).isEqualByComparingTo(new BigDecimal("3.33"));
        }

        @Test
        @DisplayName("10.00 / 4 → [2.50, 2.50, 2.50, 2.50]")
        void evenSplit() {
            List<BigDecimal> parts = tasks.splitEqually(new BigDecimal("10.00"), 4);
            assertThat(parts).allSatisfy(p ->
                    assertThat(p).isEqualByComparingTo(new BigDecimal("2.50")));
        }

        @Test
        @DisplayName("0.05 / 2 → [0.03, 0.02]")
        void tinyAmount() {
            List<BigDecimal> parts = tasks.splitEqually(new BigDecimal("0.05"), 2);
            assertThat(parts.get(0)).isEqualByComparingTo(new BigDecimal("0.03"));
            assertThat(parts.get(1)).isEqualByComparingTo(new BigDecimal("0.02"));
        }

        @Test
        @DisplayName("1.00 / 3 → [0.34, 0.33, 0.33]")
        void oneDollar() {
            List<BigDecimal> parts = tasks.splitEqually(new BigDecimal("1.00"), 3);
            assertThat(parts.get(0)).isEqualByComparingTo(new BigDecimal("0.34"));
            assertThat(parts.get(1)).isEqualByComparingTo(new BigDecimal("0.33"));
            assertThat(parts.get(2)).isEqualByComparingTo(new BigDecimal("0.33"));
        }

        @Test
        @DisplayName("sum of returned parts equals the original amount exactly")
        void sumEqualsOriginal() {
            BigDecimal amount = new BigDecimal("10.00");
            List<BigDecimal> parts = tasks.splitEqually(amount, 3);
            BigDecimal sum = parts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            assertThat(sum).isEqualByComparingTo(amount);
        }

        @Test
        @DisplayName("each part has scale 2")
        void eachPartScaleTwo() {
            List<BigDecimal> parts = tasks.splitEqually(new BigDecimal("10.00"), 3);
            assertThat(parts).allSatisfy(p -> assertThat(p.scale()).isEqualTo(2));
        }

        @Test
        @DisplayName("zero amount → list of zero shares with scale 2")
        void zeroAmount() {
            List<BigDecimal> parts = tasks.splitEqually(new BigDecimal("0.00"), 5);
            assertThat(parts).hasSize(5);
            assertThat(parts).allSatisfy(p ->
                    assertThat(p).isEqualByComparingTo(new BigDecimal("0.00")));
        }

        @Test
        @DisplayName("parts ≤ 0 throws IllegalArgumentException")
        void invalidParts() {
            assertThatThrownBy(() ->
                    tasks.splitEqually(new BigDecimal("10.00"), 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Task 4: wouldOverflowOnAdd ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement wouldOverflowOnAdd()")
    @DisplayName("Task 4 — wouldOverflowOnAdd()")
    class WouldOverflowOnAddTest {

        @Test
        @DisplayName("MAX_VALUE + 1 overflows")
        void maxPlusOne() {
            assertThat(tasks.wouldOverflowOnAdd(Integer.MAX_VALUE, 1)).isTrue();
        }

        @Test
        @DisplayName("MIN_VALUE + (-1) overflows")
        void minMinusOne() {
            assertThat(tasks.wouldOverflowOnAdd(Integer.MIN_VALUE, -1)).isTrue();
        }

        @Test
        @DisplayName("small addition does not overflow")
        void smallAdd() {
            assertThat(tasks.wouldOverflowOnAdd(1, 2)).isFalse();
        }

        @Test
        @DisplayName("MAX_VALUE + 0 does not overflow")
        void maxPlusZero() {
            assertThat(tasks.wouldOverflowOnAdd(Integer.MAX_VALUE, 0)).isFalse();
        }

        @Test
        @DisplayName("MIN_VALUE + 0 does not overflow")
        void minPlusZero() {
            assertThat(tasks.wouldOverflowOnAdd(Integer.MIN_VALUE, 0)).isFalse();
        }

        @Test
        @DisplayName("MAX_VALUE + MIN_VALUE does not overflow")
        void maxPlusMin() {
            assertThat(tasks.wouldOverflowOnAdd(Integer.MAX_VALUE, Integer.MIN_VALUE)).isFalse();
        }

        @Test
        @DisplayName("MAX_VALUE + (-1) does not overflow")
        void maxMinusOne() {
            assertThat(tasks.wouldOverflowOnAdd(Integer.MAX_VALUE, -1)).isFalse();
        }

        @Test
        @DisplayName("MIN_VALUE + 1 does not overflow")
        void minPlusOne() {
            assertThat(tasks.wouldOverflowOnAdd(Integer.MIN_VALUE, 1)).isFalse();
        }
    }

    // ── Task 5: centsToDollars ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement centsToDollars()")
    @DisplayName("Task 5 — centsToDollars()")
    class CentsToDollarsTest {

        @Test
        @DisplayName("1234 cents → 12.34")
        void basic() {
            BigDecimal result = tasks.centsToDollars(1234L);
            assertThat(result).isEqualByComparingTo(new BigDecimal("12.34"));
            assertThat(result.scale()).isEqualTo(2);
        }

        @Test
        @DisplayName("0 cents → 0.00 with scale 2")
        void zero() {
            BigDecimal result = tasks.centsToDollars(0L);
            assertThat(result).isEqualByComparingTo(new BigDecimal("0.00"));
            assertThat(result.scale()).isEqualTo(2);
        }

        @Test
        @DisplayName("negative cents → negative dollars")
        void negative() {
            BigDecimal result = tasks.centsToDollars(-500L);
            assertThat(result).isEqualByComparingTo(new BigDecimal("-5.00"));
        }

        @Test
        @DisplayName("5 cents → 0.05")
        void smallAmount() {
            BigDecimal result = tasks.centsToDollars(5L);
            assertThat(result).isEqualByComparingTo(new BigDecimal("0.05"));
        }
    }

    // ── Task 6: isNumericallyEqual ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isNumericallyEqual()")
    @DisplayName("Task 6 — isNumericallyEqual()")
    class IsNumericallyEqualTest {

        @Test
        @DisplayName("2.0 and 2.00 are numerically equal")
        void sameValueDifferentScale() {
            assertThat(tasks.isNumericallyEqual(
                    new BigDecimal("2.0"), new BigDecimal("2.00"))).isTrue();
        }

        @Test
        @DisplayName("2.0 and 2 are numerically equal")
        void integerForm() {
            assertThat(tasks.isNumericallyEqual(
                    new BigDecimal("2.0"), new BigDecimal("2"))).isTrue();
        }

        @Test
        @DisplayName("2.0 and 2.001 are not numerically equal")
        void differentValues() {
            assertThat(tasks.isNumericallyEqual(
                    new BigDecimal("2.0"), new BigDecimal("2.001"))).isFalse();
        }

        @Test
        @DisplayName("negative zero and zero are numerically equal")
        void zeroForms() {
            assertThat(tasks.isNumericallyEqual(
                    new BigDecimal("-0.0"), new BigDecimal("0.00"))).isTrue();
        }

        @Test
        @DisplayName("large equal values with different scales")
        void largeValues() {
            assertThat(tasks.isNumericallyEqual(
                    new BigDecimal("1000000.000"), new BigDecimal("1000000.0"))).isTrue();
        }
    }
}