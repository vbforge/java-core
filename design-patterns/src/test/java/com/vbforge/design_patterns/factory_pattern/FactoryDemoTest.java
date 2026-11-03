package com.vbforge.design_patterns.factory_pattern;

import com.vbforge.design_patterns.factory_pattern.FactoryDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FactoryDemo — API contracts")
class FactoryDemoTest {

    @Nested
    @DisplayName("1. Factory method")
    class FactoryMethodTest {

        @Test
        @DisplayName("createShape returns the correct concrete type")
        void knownTypes() {
            assertThat(FactoryDemo.createShape("circle").name()).isEqualTo("circle");
            assertThat(FactoryDemo.createShape("square").name()).isEqualTo("square");
            assertThat(FactoryDemo.createShape("triangle").name()).isEqualTo("triangle");
        }

        @Test
        @DisplayName("createShape is case-insensitive")
        void caseInsensitive() {
            assertThat(FactoryDemo.createShape("CIRCLE").name()).isEqualTo("circle");
        }

        @Test
        @DisplayName("unknown type throws IllegalArgumentException")
        void unknown() {
            assertThatThrownBy(() -> FactoryDemo.createShape("hexagon"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("hexagon");
        }

        @Test
        @DisplayName("null type throws IllegalArgumentException")
        void nullType() {
            assertThatThrownBy(() -> FactoryDemo.createShape(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("circles have area ≈ π")
        void circleArea() {
            assertThat(FactoryDemo.createShape("circle").area()).isCloseTo(Math.PI, org.assertj.core.data.Offset.offset(1e-9));
        }
    }

    @Nested
    @DisplayName("2. Abstract factory")
    class AbstractFactoryTest {

        @Test
        @DisplayName("supplier produces fresh instances each call")
        void freshInstances() {
            Supplier<FactoryDemo.Shape> s = FactoryDemo.shapeSupplier("circle");
            assertThat(s.get()).isNotSameAs(s.get());
        }

        @Test
        @DisplayName("supplier produces the right type")
        void rightType() {
            assertThat(FactoryDemo.shapeSupplier("square").get().name()).isEqualTo("square");
        }

        @Test
        @DisplayName("bad type fails at factory creation, not at get()")
        void failFast() {
            assertThatThrownBy(() -> FactoryDemo.shapeSupplier("hexagon"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("3. Batch")
    class BatchTest {

        @Test
        @DisplayName("createAll preserves order")
        void preservesOrder() {
            List<FactoryDemo.Shape> shapes =
                    FactoryDemo.createAll(List.of("circle", "square", "triangle"));
            assertThat(shapes).extracting(FactoryDemo.Shape::name)
                    .containsExactly("circle", "square", "triangle");
        }

        @Test
        @DisplayName("empty input yields empty list")
        void empty() {
            assertThat(FactoryDemo.createAll(List.of())).isEmpty();
        }

        @Test
        @DisplayName("unknown element throws")
        void unknownThrows() {
            assertThatThrownBy(() -> FactoryDemo.createAll(List.of("circle", "hexagon")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}