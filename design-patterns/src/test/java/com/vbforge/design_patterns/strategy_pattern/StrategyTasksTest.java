package com.vbforge.design_patterns.strategy_pattern;

import com.vbforge.design_patterns.strategy_pattern.StrategyTasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StrategyTasks — your implementations")
class StrategyTasksTest {

    private StrategyTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new StrategyTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement noDiscount()")
    @DisplayName("Task 1 — noDiscount()")
    class NoDiscountTest {

        @ParameterizedTest(name = "price({0}) = {0}")
        @CsvSource({"0", "50", "100", "999.99"})
        void returnsInput(double base) {
            assertThat(tasks.noDiscount().price(base)).isEqualTo(base);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement tenPercentOff()")
    @DisplayName("Task 2 — tenPercentOff()")
    class TenPercentOffTest {

        @ParameterizedTest(name = "price({0}) = {1}")
        @CsvSource({"100,90.0", "200,180.0", "50,45.0", "0,0.0"})
        void applies90Multiplier(double base, double expected) {
            assertThat(tasks.tenPercentOff().price(base)).isEqualTo(expected);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement percentage()")
    @DisplayName("Task 3 — percentage()")
    class PercentageTest {

        @Test
        @DisplayName("0% returns base unchanged")
        void zero() {
            assertThat(tasks.percentage(0).price(100)).isEqualTo(100.0);
        }

        @Test
        @DisplayName("50% halves the price")
        void fifty() {
            assertThat(tasks.percentage(50).price(100)).isEqualTo(50.0);
        }

        @Test
        @DisplayName("100% zeroes the price")
        void hundred() {
            assertThat(tasks.percentage(100).price(100)).isEqualTo(0.0);
        }

        @ParameterizedTest(name = "pct={0}, base={1} → {2}")
        @CsvSource({"25,200,150.0", "10,100,90.0", "20,50,40.0"})
        void parametrized(double pct, double base, double expected) {
            assertThat(tasks.percentage(pct).price(base)).isEqualTo(expected);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement checkout()")
    @DisplayName("Task 4 — checkout()")
    class CheckoutTest {

        @Test
        @DisplayName("delegates to strategy.price()")
        void delegates() {
            assertThat(tasks.checkout(100, base -> base)).isEqualTo(100.0);
            assertThat(tasks.checkout(100, base -> base * 0.5)).isEqualTo(50.0);
        }

        @Test
        @DisplayName("works with a strategy from Task 3")
        void withTask3() {
            assertThat(tasks.checkout(200, tasks.percentage(25))).isEqualTo(150.0);
        }
    }
}