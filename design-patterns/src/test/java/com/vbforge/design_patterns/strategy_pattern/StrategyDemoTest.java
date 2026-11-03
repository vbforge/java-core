package com.vbforge.design_patterns.strategy_pattern;

import com.vbforge.design_patterns.strategy_pattern.StrategyDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StrategyDemo — API contracts")
class StrategyDemoTest {

    @Nested
    @DisplayName("1. Two strategies")
    class TwoStrategiesTest {

        @Test
        @DisplayName("no-discount returns base")
        void noDiscount() {
            StrategyDemo.PricingStrategy s = base -> base;
            assertThat(s.price(100)).isEqualTo(100.0);
        }

        @Test
        @DisplayName("ten-percent applies 0.9 multiplier")
        void tenPercent() {
            StrategyDemo.PricingStrategy s = base -> base * 0.9;
            assertThat(s.price(200)).isEqualTo(180.0);
        }
    }

    @Nested
    @DisplayName("2. Context delegates")
    class ContextTest {

        @Test
        @DisplayName("Checkout with no-discount returns base")
        void noDiscount() {
            assertThat(new StrategyDemo.Checkout(base -> base).checkout(150)).isEqualTo(150.0);
        }

        @Test
        @DisplayName("Checkout with ten-percent returns base * 0.9")
        void tenPercent() {
            assertThat(new StrategyDemo.Checkout(base -> base * 0.9).checkout(200)).isEqualTo(180.0);
        }
    }

    @Nested
    @DisplayName("3. Lambda strategies")
    class LambdaTest {

        @Test
        @DisplayName("ad-hoc lambda strategy works")
        void adHoc() {
            StrategyDemo.PricingStrategy half = base -> base * 0.5;
            assertThat(new StrategyDemo.Checkout(half).checkout(100)).isEqualTo(50.0);
        }
    }
}