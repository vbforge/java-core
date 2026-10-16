package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.OpenClosedDemo.Circle;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.Rectangle;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.Shape;
import com.vbforge.best_practices_and_solid.OpenClosedDemo.Triangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("OpenClosedTasks — your implementations")
class OpenClosedTasksTest {

    private OpenClosedTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new OpenClosedTasks();
    }

    private static Map<String, DoubleUnaryOperator> shippingRules() {
        Map<String, DoubleUnaryOperator> rules = new HashMap<>();
        rules.put("STANDARD", kg -> 5 + kg);
        rules.put("EXPRESS", kg -> 15 + 2.5 * kg);
        return rules;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement totalArea()")
    @DisplayName("totalArea(shapes)")
    class TotalAreaTest {

        @Test
        @DisplayName("sums the areas of different shape types")
        void sumsAreas() {
            List<Shape> shapes = Arrays.<Shape>asList(new Rectangle(2, 3), new Triangle(4, 5));

            assertThat(tasks.totalArea(shapes)).isEqualTo(16.0);
        }

        @Test
        @DisplayName("an empty list has area zero")
        void emptyList() {
            assertThat(tasks.totalArea(Collections.<Shape>emptyList())).isEqualTo(0.0);
        }

        @Test
        @DisplayName("works for a shape type the method has never heard of")
        void unknownShapeType() {
            Shape fixed = () -> 7.5;

            assertThat(tasks.totalArea(Arrays.asList(fixed, new Rectangle(1, 2)))).isEqualTo(9.5);
        }

        @Test
        @DisplayName("includes circles")
        void circles() {
            assertThat(tasks.totalArea(Collections.<Shape>singletonList(new Circle(1))))
                    .isCloseTo(Math.PI, within(1e-9));
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement applyTransformations()")
    @DisplayName("applyTransformations(text, transformations)")
    class ApplyTransformationsTest {

        @Test
        @DisplayName("applies method-reference transformations in order")
        void trimThenUpperCase() {
            List<UnaryOperator<String>> steps = Arrays.<UnaryOperator<String>>asList(String::trim, String::toUpperCase);

            assertThat(tasks.applyTransformations("  hello ", steps)).isEqualTo("HELLO");
        }

        @Test
        @DisplayName("applies lambda transformations in order")
        void lambdas() {
            List<UnaryOperator<String>> steps = Arrays.<UnaryOperator<String>>asList(s -> s + "!", s -> s + "?");

            assertThat(tasks.applyTransformations("abc", steps)).isEqualTo("abc!?");
        }

        @Test
        @DisplayName("an empty list returns the text unchanged")
        void emptyList() {
            assertThat(tasks.applyTransformations("abc", Collections.<UnaryOperator<String>>emptyList())).isEqualTo("abc");
        }

        @Test
        @DisplayName("the order of the list decides the result")
        void orderMatters() {
            UnaryOperator<String> append = s -> s + "x";
            UnaryOperator<String> replace = s -> s.replace("x", "y");

            assertThat(tasks.applyTransformations("a", Arrays.asList(append, replace))).isEqualTo("ay");
            assertThat(tasks.applyTransformations("a", Arrays.asList(replace, append))).isEqualTo("ax");
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement shippingCost()")
    @DisplayName("shippingCost(method, weightKg, rules)")
    class ShippingCostTest {

        @ParameterizedTest(name = "{0}, {1} kg -> {2}")
        @CsvSource({"STANDARD, 3.0, 8.0", "EXPRESS, 2.0, 20.0", "STANDARD, 0.0, 5.0"})
        @DisplayName("applies the rule registered for the method")
        void appliesRule(String method, double weight, double expected) {
            assertThat(tasks.shippingCost(method, weight, shippingRules())).isEqualTo(expected);
        }

        @Test
        @DisplayName("an unknown method is rejected with a clear message")
        void unknownMethod() {
            assertThatThrownBy(() -> tasks.shippingCost("DRONE", 2.0, shippingRules()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Unknown shipping method: DRONE");
        }

        @Test
        @DisplayName("a brand-new rule works without changing the method")
        void newRule() {
            Map<String, DoubleUnaryOperator> rules = shippingRules();
            rules.put("PICKUP", kg -> 0.0);

            assertThat(tasks.shippingCost("PICKUP", 10.0, rules)).isEqualTo(0.0);
        }
    }
}
