package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.OpenClosedDemo.AreaCalculatorBad;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.Circle;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.DiscountCalculator;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.DiscountCalculatorBad;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.DiscountPolicy;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.Rectangle;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.Shape;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.Triangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("OpenClosedDemo — API contracts")
class OpenClosedDemoTest {

    @Nested
    @DisplayName("1. if/else chain that must be edited for every new type")
    class IfElseSection {

        @ParameterizedTest(name = "{0}: {1} -> {2}")
        @CsvSource({
                "REGULAR, 100.0, 100.0",
                "SILVER,  100.0,  95.0",
                "GOLD,    100.0,  90.0"
        })
        @DisplayName("known customer types get their discount")
        void knownTypes(String type, double price, double expected) {
            assertThat(new DiscountCalculatorBad().finalPrice(type, price)).isEqualTo(expected);
        }

        @Test
        @DisplayName("a new customer type fails until someone edits the method")
        void newTypeNeedsAnEdit() {
            assertThatThrownBy(() -> new DiscountCalculatorBad().finalPrice("PLATINUM", 100.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Unknown customer type: PLATINUM");
        }
    }


    @Nested
    @DisplayName("2. Strategy + registry")
    class StrategySection {

        @ParameterizedTest(name = "{0}: {1} -> {2}")
        @CsvSource({
                "REGULAR, 100.0, 100.0",
                "SILVER,  100.0,  95.0",
                "GOLD,    100.0,  90.0"
        })
        @DisplayName("standard policies give the same prices as the if/else chain")
        void sameResultsAsBadVersion(String type, double price, double expected) {
            DiscountCalculator calculator = new DiscountCalculator(OpenClosedDemo.standardPolicies());

            assertThat(calculator.finalPrice(type, price)).isEqualTo(expected);
            assertThat(calculator.finalPrice(type, price))
                    .isEqualTo(new DiscountCalculatorBad().finalPrice(type, price));
        }

        @Test
        @DisplayName("a new customer type is just a new map entry — the calculator is not edited")
        void extensionWithoutModification() {
            Map<String, DiscountPolicy> policies = OpenClosedDemo.standardPolicies();
            policies.put("PLATINUM", price -> price * 0.20);

            assertThat(new DiscountCalculator(policies).finalPrice("PLATINUM", 100.0)).isEqualTo(80.0);
        }

        @Test
        @DisplayName("an unknown type is rejected with a clear message")
        void unknownType() {
            DiscountCalculator calculator = new DiscountCalculator(OpenClosedDemo.standardPolicies());

            assertThatThrownBy(() -> calculator.finalPrice("VIP", 10.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Unknown customer type: VIP");
        }

        @Test
        @DisplayName("the constructor copies the map, so later changes cannot sneak in")
        void defensiveCopy() {
            Map<String, DiscountPolicy> policies = OpenClosedDemo.standardPolicies();
            DiscountCalculator calculator = new DiscountCalculator(policies);

            policies.put("VIP", price -> price * 0.5);

            assertThatThrownBy(() -> calculator.finalPrice("VIP", 10.0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }


    @Nested
    @DisplayName("3. instanceof chain vs polymorphism")
    class PolymorphismSection {

        @Test
        @DisplayName("each shape knows its own area")
        void shapeAreas() {
            assertThat(new Circle(2).area()).isCloseTo(Math.PI * 4, within(1e-9));
            assertThat(new Rectangle(2, 3).area()).isEqualTo(6.0);
            assertThat(new Triangle(4, 5).area()).isEqualTo(10.0);
        }

        @Test
        @DisplayName("the bad calculator handles only the shapes it was edited for")
        void badCalculatorKnownShapes() {
            double total = new AreaCalculatorBad().totalArea(Arrays.asList(new Rectangle(2, 3), new Rectangle(1, 1)));

            assertThat(total).isEqualTo(7.0);
        }

        @Test
        @DisplayName("the bad calculator throws for a shape added later")
        void badCalculatorUnknownShape() {
            assertThatThrownBy(() -> new AreaCalculatorBad().totalArea(Arrays.asList(new Rectangle(2, 3), new Triangle(4, 5))))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Unsupported shape: Triangle");
        }

        @Test
        @DisplayName("polymorphic code handles the new shape without any change")
        void polymorphicCodeNeedsNoEdit() {
            double total = 0;
            for (Shape shape : Arrays.asList(new Rectangle(2, 3), new Triangle(4, 5))) {
                total += shape.area();
            }

            assertThat(total).isEqualTo(16.0);
        }
    }
}
