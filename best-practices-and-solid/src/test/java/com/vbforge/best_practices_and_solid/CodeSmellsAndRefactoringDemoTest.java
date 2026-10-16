package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.CodeSmellsAndRefactoringDemo.IntRange;
import com.vbforge.best_practices_and_solid.CodeSmellsAndRefactoringDemo.LineItem;
import com.vbforge.best_practices_and_solid.CodeSmellsAndRefactoringDemo.Money;
import com.vbforge.best_practices_and_solid.CodeSmellsAndRefactoringDemo.Order;
import com.vbforge.best_practices_and_solid.CodeSmellsAndRefactoringDemo.OrderPrinterBad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CodeSmellsAndRefactoringDemo — API contracts")
class CodeSmellsAndRefactoringDemoTest {

    @Nested
    @DisplayName("1. Long parameter list -> parameter object")
    class ParameterObjectSection {

        @Test
        @DisplayName("swapped arguments are silently accepted by the bad signature")
        void badSignatureAcceptsSwappedArguments() {
            assertThat(CodeSmellsAndRefactoringDemo.containsBad(1, 5, 3)).isTrue();
            assertThat(CodeSmellsAndRefactoringDemo.containsBad(5, 1, 3)).isFalse();
        }

        @Test
        @DisplayName("IntRange rejects start > end once, in the constructor")
        void rangeValidatesOnce() {
            assertThatThrownBy(() -> new IntRange(5, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("start must not be greater than end");
        }

        @Test
        @DisplayName("contains is inclusive on both ends")
        void containsIsInclusive() {
            IntRange range = new IntRange(1, 5);

            assertThat(range.contains(1)).isTrue();
            assertThat(range.contains(5)).isTrue();
            assertThat(range.contains(0)).isFalse();
            assertThat(range.contains(6)).isFalse();
        }

        @Test
        @DisplayName("length counts both ends")
        void lengthCountsBothEnds() {
            assertThat(new IntRange(1, 5).length()).isEqualTo(5);
            assertThat(new IntRange(3, 3).length()).isEqualTo(1);
        }
    }


    @Nested
    @DisplayName("2. Primitive obsession -> value object")
    class ValueObjectSection {

        @Test
        @DisplayName("double arithmetic introduces binary rounding error")
        void doubleRoundingError() {
            assertThat(CodeSmellsAndRefactoringDemo.addPricesBad(0.1, 0.2)).isNotEqualTo(0.3);
        }

        @Test
        @DisplayName("Money adds exact cents")
        void moneyAddsExactly() {
            Money sum = Money.of("USD", 10).plus(Money.of("USD", 20));

            assertThat(sum).isEqualTo(Money.of("USD", 30));
            assertThat(sum.getCents()).isEqualTo(30);
        }

        @Test
        @DisplayName("adding different currencies fails loudly")
        void currencyMismatch() {
            assertThatThrownBy(() -> Money.of("USD", 100).plus(Money.of("EUR", 100)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Currency mismatch");
        }

        @Test
        @DisplayName("equal currency and cents mean equal objects with equal hash codes")
        void valueEquality() {
            assertThat(Money.of("USD", 250)).isEqualTo(Money.of("USD", 250));
            assertThat(Money.of("USD", 250).hashCode()).isEqualTo(Money.of("USD", 250).hashCode());
            assertThat(Money.of("USD", 250)).isNotEqualTo(Money.of("EUR", 250));
        }

        @ParameterizedTest(name = "{0} cents -> \"{1}\"")
        @CsvSource({"1234, 'USD 12.34'", "5, 'USD 0.05'", "0, 'USD 0.00'", "-150, 'USD -1.50'", "-50, 'USD -0.50'"})
        @DisplayName("toString prints currency, whole units and two-digit cents")
        void toStringFormat(long cents, String expected) {
            assertThat(Money.of("USD", cents).toString()).isEqualTo(expected);
        }

        @Test
        @DisplayName("an empty currency is rejected")
        void emptyCurrencyRejected() {
            assertThatThrownBy(() -> Money.of("", 1)).isInstanceOf(IllegalArgumentException.class);
        }
    }


    @Nested
    @DisplayName("3. Feature envy -> move method")
    class FeatureEnvySection {

        private Order sampleOrder() {
            return new Order().add(new LineItem("pen", 2, 1.5)).add(new LineItem("book", 1, 4.0));
        }

        @Test
        @DisplayName("the order computes its own total")
        void orderTotal() {
            assertThat(sampleOrder().total()).isEqualTo(7.0);
        }

        @Test
        @DisplayName("the envious printer computes the same number by reaching into the order")
        void printerComputesSameTotal() {
            assertThat(new OrderPrinterBad().totalOf(sampleOrder())).isEqualTo(sampleOrder().total());
        }

        @Test
        @DisplayName("an empty order totals zero")
        void emptyOrder() {
            assertThat(new Order().total()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("the item list is exposed read-only")
        void itemsAreReadOnly() {
            Order order = sampleOrder();

            assertThatThrownBy(() -> order.getItems().add(new LineItem("x", 1, 1.0)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }


    @Nested
    @DisplayName("4. Refactoring with a safety net")
    class SafetyNetSection {

        @Test
        @DisplayName("the refactored price equals the legacy price for every quantity 0..1000")
        void behaviourIsPreserved() {
            for (int quantity = 0; quantity <= 1000; quantity++) {
                assertThat(CodeSmellsAndRefactoringDemo.price(quantity))
                        .as("quantity %d", quantity)
                        .isEqualTo(CodeSmellsAndRefactoringDemo.legacyPrice(quantity));
            }
        }

        @ParameterizedTest(name = "quantity {0} -> {1}")
        @CsvSource({"0, 0", "9, 900", "10, 900", "49, 4410", "50, 4000"})
        @DisplayName("tier boundaries behave as before (including the odd price drop at 10 and 50)")
        void tierBoundaries(int quantity, int expected) {
            assertThat(CodeSmellsAndRefactoringDemo.price(quantity)).isEqualTo(expected);
            assertThat(CodeSmellsAndRefactoringDemo.legacyPrice(quantity)).isEqualTo(expected);
        }

        @Test
        @DisplayName("both versions reject a negative quantity")
        void negativeQuantity() {
            assertThatThrownBy(() -> CodeSmellsAndRefactoringDemo.price(-1))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> CodeSmellsAndRefactoringDemo.legacyPrice(-1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
