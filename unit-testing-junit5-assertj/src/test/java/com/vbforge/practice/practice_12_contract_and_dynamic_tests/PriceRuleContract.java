package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

/**
 * Technique: contract test written ONCE as a test interface with default @Test methods.
 * Every implementation's test class just does `implements PriceRuleContract` and supplies createRule().
 * A new implementation that breaks the contract (negative result, price grows) fails immediately,
 * and Liskov-substitutability stops being a hope and becomes a check.
 */
interface PriceRuleContract {

    PriceRule createRule();

    @Test
    default void zeroPrice_staysZero() {
        assertThat(createRule().apply(0)).isZero();
    }

    @Test
    default void result_isNeverNegative_andNeverAboveTheOriginalPrice() {
        PriceRule rule = createRule();

        for (long price = 0; price <= 5_000; price += 7) {
            assertThat(rule.apply(price)).as("apply(%d)", price).isBetween(0L, price);
        }
    }

    @Test
    default void result_neverDecreasesWhenThePriceGrows() {
        PriceRule rule = createRule();
        long previous = rule.apply(0);

        for (long price = 1; price <= 5_000; price++) {
            long current = rule.apply(price);
            assertThat(current).as("apply(%d) vs apply(%d)", price, price - 1).isGreaterThanOrEqualTo(previous);
            previous = current;
        }
    }

    @Test
    default void negativePrice_isRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> createRule().apply(-1));
    }
}
