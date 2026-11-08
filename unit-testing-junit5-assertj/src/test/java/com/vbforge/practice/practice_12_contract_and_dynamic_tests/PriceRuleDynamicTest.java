package com.vbforge.practice.practice_12_contract_and_dynamic_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

/**
 * Technique: dynamic tests. A @TestFactory builds the test cases AT RUNTIME (from a list, a file, a database of cases)
 * and returns them as a stream of DynamicTest / DynamicContainer. Use it when the NUMBER of cases is not known at compile time.
 * Notes: @BeforeEach / @AfterEach run once around the whole factory, not around each dynamic test; no per-case @DisplayName annotation,
 * the display name is the first argument of dynamicTest().
 */
class PriceRuleDynamicTest {

    private static Map<String, PriceRule> rules() {
        Map<String, PriceRule> rules = new LinkedHashMap<>();
        rules.put("no discount", new NoDiscount());
        rules.put("10% off", new PercentageDiscount(10));
        rules.put("50% off", new PercentageDiscount(50));
        rules.put("fixed 250", new FixedDiscount(250));
        return rules;
    }

    @TestFactory
    Stream<DynamicNode> everyRule_neverChargesMoreThanTheOriginalPrice() {
        long[] prices = {0, 1, 99, 100, 1_000, 123_456};
        return rules().entrySet().stream().map(entry ->
                dynamicContainer("rule: " + entry.getKey(),
                        java.util.Arrays.stream(prices).mapToObj(price ->
                                dynamicTest("price " + price, () ->
                                        assertThat(entry.getValue().apply(price)).isBetween(0L, price)))));
    }
}
