package com.vbforge.bigdecimal_and_numeric_precision;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BigDecimalDemoTest — verifies every concept shown in {@link BigDecimalDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * why {@code double} cannot represent money and how {@link BigDecimal} fixes it.
 */
@DisplayName("BigDecimalDemo — API contracts")
class BigDecimalDemoTest {

    // ── 1. Floating-point error ───────────────────────────────────────────────

    @Nested
    @DisplayName("1. Floating-point error")
    class FloatingPointErrorTest {

        @Test
        @DisplayName("0.1 + 0.2 is NOT exactly 0.3 in double arithmetic")
        void classicBug() {
            double sum = 0.1 + 0.2;
            assertThat(sum == 0.3).isFalse();
        }

        @Test
        @DisplayName("BigDecimal with String constructor gives the exact decimal result")
        void bigDecimalExact() {
            BigDecimal sum = new BigDecimal("0.1").add(new BigDecimal("0.2"));
            assertThat(sum).isEqualByComparingTo(new BigDecimal("0.3"));
        }

        @Test
        @DisplayName("0.1 * 3 also drifts with double")
        void multiplicationDrifts() {
            double total = 0.1 * 3;
            assertThat(total == 0.3).isFalse();
        }
    }

    // ── 2. BigDecimal creation ────────────────────────────────────────────────

    @Nested
    @DisplayName("2. BigDecimal creation")
    class BigDecimalCreationTest {

        @Test
        @DisplayName("new BigDecimal(String) equals BigDecimal.valueOf(double)")
        void stringEqualsValueOf() {
            assertThat(new BigDecimal("0.1")).isEqualTo(BigDecimal.valueOf(0.1));
        }

        @Test
        @DisplayName("new BigDecimal(double) captures binary noise, not the decimal value")
        void doubleConstructorCapturesNoise() {
            BigDecimal fromDouble = new BigDecimal(0.1);
            assertThat(fromDouble).isNotEqualTo(new BigDecimal("0.1"));
        }

        @Test
        @DisplayName("int constructor is exact for integers")
        void intConstructorExact() {
            assertThat(new BigDecimal(42)).isEqualTo(new BigDecimal("42"));
        }

        @Test
        @DisplayName("BigDecimal.ZERO and BigDecimal.TEN are pre-defined constants")
        void constants() {
            assertThat(BigDecimal.ZERO).isEqualTo(new BigDecimal("0"));
            assertThat(BigDecimal.TEN).isEqualTo(new BigDecimal("10"));
        }
    }

    // ── 3. Scale and rounding ─────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Scale and rounding")
    class ScaleAndRoundingTest {

        @Test
        @DisplayName("scale() reports digits after the decimal point")
        void scale() {
            assertThat(new BigDecimal("19.99").scale()).isEqualTo(2);
            assertThat(new BigDecimal("19.990").scale()).isEqualTo(3);
            assertThat(new BigDecimal("19").scale()).isZero();
        }

        @Test
        @DisplayName("HALF_UP rounds 2.345 to 2.35")
        void halfUp() {
            assertThat(new BigDecimal("2.345").setScale(2, RoundingMode.HALF_UP))
                    .isEqualByComparingTo(new BigDecimal("2.35"));
        }

        @Test
        @DisplayName("HALF_DOWN rounds 2.345 to 2.34")
        void halfDown() {
            assertThat(new BigDecimal("2.345").setScale(2, RoundingMode.HALF_DOWN))
                    .isEqualByComparingTo(new BigDecimal("2.34"));
        }

        @Test
        @DisplayName("DOWN truncates toward zero")
        void down() {
            assertThat(new BigDecimal("2.349").setScale(2, RoundingMode.DOWN))
                    .isEqualByComparingTo(new BigDecimal("2.34"));
            assertThat(new BigDecimal("-2.349").setScale(2, RoundingMode.DOWN))
                    .isEqualByComparingTo(new BigDecimal("-2.34"));
        }

        @Test
        @DisplayName("CEILING rounds toward positive infinity")
        void ceiling() {
            assertThat(new BigDecimal("2.341").setScale(2, RoundingMode.CEILING))
                    .isEqualByComparingTo(new BigDecimal("2.35"));
            assertThat(new BigDecimal("-2.349").setScale(2, RoundingMode.CEILING))
                    .isEqualByComparingTo(new BigDecimal("-2.34"));
        }

        @Test
        @DisplayName("FLOOR rounds toward negative infinity")
        void floor() {
            assertThat(new BigDecimal("2.349").setScale(2, RoundingMode.FLOOR))
                    .isEqualByComparingTo(new BigDecimal("2.34"));
            assertThat(new BigDecimal("-2.341").setScale(2, RoundingMode.FLOOR))
                    .isEqualByComparingTo(new BigDecimal("-2.35"));
        }

        @Test
        @DisplayName("setScale without RoundingMode throws on lossy conversion")
        void noModeThrows() {
            assertThatThrownBy(() -> new BigDecimal("2.345").setScale(2))
                    .isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("Growing scale is exact — pads with zeros")
        void growingScaleIsExact() {
            assertThat(new BigDecimal("2.5").setScale(4, RoundingMode.HALF_UP))
                    .isEqualByComparingTo(new BigDecimal("2.5000"));
        }
    }

    // ── 4. compareTo vs equals ────────────────────────────────────────────────

    @Nested
    @DisplayName("4. compareTo vs equals")
    class CompareToVsEqualsTest {

        @Test
        @DisplayName("equals() is false when scales differ, even if values are equal")
        void equalsIsScaleSensitive() {
            assertThat(new BigDecimal("2.0").equals(new BigDecimal("2.00"))).isFalse();
        }

        @Test
        @DisplayName("compareTo() returns 0 for numerically equal values with different scales")
        void compareToIsScaleInsensitive() {
            assertThat(new BigDecimal("2.0").compareTo(new BigDecimal("2.00"))).isZero();
        }

        @Test
        @DisplayName("compareTo returns negative when this < other")
        void compareToLess() {
            assertThat(new BigDecimal("2.0").compareTo(new BigDecimal("2.001"))).isNegative();
        }

        @Test
        @DisplayName("compareTo returns positive when this > other")
        void compareToGreater() {
            assertThat(new BigDecimal("2.0").compareTo(new BigDecimal("1.999"))).isPositive();
        }

        @Test
        @DisplayName("min() and max() pick by value, ignoring scale")
        void minMax() {
            BigDecimal a = new BigDecimal("2.0");
            BigDecimal b = new BigDecimal("1.5");
            assertThat(a.min(b)).isEqualByComparingTo(new BigDecimal("1.5"));
            assertThat(a.max(b)).isEqualByComparingTo(new BigDecimal("2.0"));
        }
    }

    // ── 5. Integer overflow ───────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Integer overflow")
    class IntegerOverflowTest {

        @Test
        @DisplayName("MAX_VALUE + 1 wraps to MIN_VALUE silently")
        void wraparound() {
            assertThat(Integer.MAX_VALUE + 1).isEqualTo(Integer.MIN_VALUE);
        }

        @Test
        @DisplayName("Math.addExact throws ArithmeticException on overflow")
        void addExactThrows() {
            assertThatThrownBy(() -> Math.addExact(Integer.MAX_VALUE, 1))
                    .isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("Math.addExact succeeds when the sum fits")
        void addExactSucceeds() {
            assertThat(Math.addExact(1, 2)).isEqualTo(3);
        }

        @Test
        @DisplayName("widening to long avoids overflow for one addition")
        void wideningToLong() {
            long wideSum = (long) Integer.MAX_VALUE + 1;
            assertThat(wideSum).isEqualTo(2147483648L);
        }
    }

    // ── 6. Money in cents ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Money in cents")
    class MoneyInCentsTest {

        @Test
        @DisplayName("integer addition of cents is exact")
        void centAddition() {
            long total = 1999L + 160L;
            assertThat(total).isEqualTo(2159L);
        }

        @Test
        @DisplayName("BigDecimal.valueOf(long, int) produces dollars with scale 2")
        void centsToDollars() {
            BigDecimal dollars = BigDecimal.valueOf(2159L, 2);
            assertThat(dollars).isEqualByComparingTo(new BigDecimal("21.59"));
            assertThat(dollars.scale()).isEqualTo(2);
        }

        @Test
        @DisplayName("movePointRight(2).longValueExact() round-trips back to cents")
        void roundTrip() {
            BigDecimal dollars = BigDecimal.valueOf(2159L, 2);
            long back = dollars.movePointRight(2).longValueExact();
            assertThat(back).isEqualTo(2159L);
        }
    }
}