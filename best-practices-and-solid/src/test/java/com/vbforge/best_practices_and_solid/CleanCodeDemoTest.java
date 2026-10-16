package com.vbforge.best_practices_and_solid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("CleanCodeDemo — API contracts")
class CleanCodeDemoTest {

    @Nested
    @DisplayName("1. Intention-revealing names")
    class NamesSection {

        @Test
        @DisplayName("calc and calculateSimpleInterest compute the same number")
        void sameResult() {
            assertThat(CleanCodeDemo.calc(1000, 0.05, 3)).isEqualTo(CleanCodeDemo.calculateSimpleInterest(1000, 0.05, 3));
        }

        @Test
        @DisplayName("simple interest is principal * rate * years")
        void simpleInterest() {
            assertThat(CleanCodeDemo.calculateSimpleInterest(1000, 0.05, 3)).isCloseTo(150.0, within(1e-9));
            assertThat(CleanCodeDemo.calculateSimpleInterest(1000, 0.05, 0)).isEqualTo(0.0);
        }
    }


    @Nested
    @DisplayName("2. Guard clauses")
    class GuardClausesSection {

        @ParameterizedTest(name = "age {0}, student={1} -> {2}")
        @CsvSource({
                "3,  false, free",
                "5,  true,  free",
                "6,  false, reduced",
                "17, false, reduced",
                "18, true,  reduced",
                "18, false, full",
                "65, false, full"
        })
        @DisplayName("nested and guarded versions return the same category")
        void sameCategory(int age, boolean student, String expected) {
            assertThat(CleanCodeDemo.ticketCategoryNested(age, student)).isEqualTo(expected);
            assertThat(CleanCodeDemo.ticketCategory(age, student)).isEqualTo(expected);
        }

        @Test
        @DisplayName("both versions reject an age out of range with the same message")
        void sameValidation() {
            assertThatThrownBy(() -> CleanCodeDemo.ticketCategoryNested(-1, false))
                    .isInstanceOf(IllegalArgumentException.class).hasMessage("age out of range: -1");
            assertThatThrownBy(() -> CleanCodeDemo.ticketCategory(-1, false))
                    .isInstanceOf(IllegalArgumentException.class).hasMessage("age out of range: -1");
            assertThatThrownBy(() -> CleanCodeDemo.ticketCategory(121, false))
                    .isInstanceOf(IllegalArgumentException.class).hasMessage("age out of range: 121");
        }
    }


    @Nested
    @DisplayName("3. Named constants instead of magic numbers")
    class MagicNumbersSection {

        @ParameterizedTest(name = "total {0}, {1} kg -> same fee in both versions")
        @CsvSource({"20.0, 2.0", "49.99, 0.0", "50.0, 3.0", "120.0, 10.0"})
        @DisplayName("magic and named versions agree")
        void sameFee(double orderTotal, double weightKg) {
            assertThat(CleanCodeDemo.shippingFee(orderTotal, weightKg))
                    .isEqualTo(CleanCodeDemo.shippingFeeMagic(orderTotal, weightKg));
        }

        @Test
        @DisplayName("orders at or above the threshold ship free")
        void freeShipping() {
            assertThat(CleanCodeDemo.shippingFee(CleanCodeDemo.FREE_SHIPPING_THRESHOLD, 5.0)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("smaller orders pay the base fee plus a fee per kilogram")
        void paidShipping() {
            assertThat(CleanCodeDemo.shippingFee(20.0, 2.0)).isCloseTo(5.99, within(1e-9));
            assertThat(CleanCodeDemo.shippingFee(49.99, 0.0)).isEqualTo(CleanCodeDemo.BASE_SHIPPING_FEE);
        }
    }


    @Nested
    @DisplayName("4. No flag arguments")
    class FlagArgumentsSection {

        @Test
        @DisplayName("the flag version formats with or without currency")
        void flagVersion() {
            assertThat(CleanCodeDemo.formatPrice(12.5, false)).isEqualTo("12.50");
            assertThat(CleanCodeDemo.formatPrice(12.5, true)).isEqualTo("USD 12.50");
        }

        @Test
        @DisplayName("two intention-named methods give the same results")
        void separateMethods() {
            assertThat(CleanCodeDemo.formatAmount(12.5)).isEqualTo(CleanCodeDemo.formatPrice(12.5, false));
            assertThat(CleanCodeDemo.formatWithCurrency(12.5)).isEqualTo(CleanCodeDemo.formatPrice(12.5, true));
        }
    }
}
