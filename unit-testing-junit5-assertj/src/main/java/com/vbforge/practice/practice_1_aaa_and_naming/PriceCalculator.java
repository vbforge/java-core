package com.vbforge.practice.practice_1_aaa_and_naming;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Small pure class used to practise Arrange-Act-Assert and test naming.
 * Pure = same input, same output, no hidden state: the easiest thing to test.
 */
public class PriceCalculator {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /** Applies a percentage discount (0..100) and rounds to cents. */
    public BigDecimal applyDiscount(BigDecimal price, int percent) {
        requireNonNegative(price);
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be in 0..100 but was " + percent);
        }
        BigDecimal factor = BigDecimal.valueOf(100 - percent).divide(HUNDRED);
        return price.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    /** Adds tax (percent, may be 0) and rounds to cents. */
    public BigDecimal addTax(BigDecimal price, BigDecimal taxPercent) {
        requireNonNegative(price);
        requireNonNegative(taxPercent);
        BigDecimal tax = price.multiply(taxPercent).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        return price.add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    private static void requireNonNegative(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException("value must not be negative: " + value);
        }
    }
}
