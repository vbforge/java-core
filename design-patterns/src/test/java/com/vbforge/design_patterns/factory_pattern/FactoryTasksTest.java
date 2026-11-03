package com.vbforge.design_patterns.factory_pattern;

import com.vbforge.design_patterns.factory_pattern.FactoryTasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FactoryTasks — your implementations")
class FactoryTasksTest {

    private FactoryTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new FactoryTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement createShape()")
    @DisplayName("Task 1 — createShape()")
    class CreateShapeTest {

        @ParameterizedTest(name = "type={0} → name={1}")
        @CsvSource({"circle,circle", "square,square", "triangle,triangle", "CIRCLE,circle", "Square,square"})
        void knownTypes(String type, String expectedName) {
            assertThat(tasks.createShape(type).name()).isEqualTo(expectedName);
        }

        @Test
        @DisplayName("circle area ≈ π")
        void circleArea() {
            assertThat(tasks.createShape("circle").area())
                    .isCloseTo(Math.PI, org.assertj.core.data.Offset.offset(1e-9));
        }

        @Test
        @DisplayName("unknown type throws IllegalArgumentException")
        void unknown() {
            assertThatThrownBy(() -> tasks.createShape("hexagon"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("null type throws IllegalArgumentException")
        void nullType() {
            assertThatThrownBy(() -> tasks.createShape(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement shapeSupplier()")
    @DisplayName("Task 2 — shapeSupplier()")
    class ShapeSupplierTest {

        @Test
        @DisplayName("supplier returns fresh instances each call")
        void freshInstances() {
            Supplier<FactoryTasks.Shape> s = tasks.shapeSupplier("circle");
            assertThat(s.get()).isNotSameAs(s.get());
        }

        @Test
        @DisplayName("supplier returns the requested type")
        void rightType() {
            assertThat(tasks.shapeSupplier("triangle").get().name()).isEqualTo("triangle");
        }

        @Test
        @DisplayName("bad type fails at factory creation")
        void failFast() {
            assertThatThrownBy(() -> tasks.shapeSupplier("hexagon"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement createAll()")
    @DisplayName("Task 3 — createAll()")
    class CreateAllTest {

        @Test
        @DisplayName("preserves input order")
        void preservesOrder() {
            List<FactoryTasks.Shape> shapes =
                    tasks.createAll(List.of("triangle", "circle", "square"));
            assertThat(shapes).extracting(FactoryTasks.Shape::name)
                    .containsExactly("triangle", "circle", "square");
        }

        @Test
        @DisplayName("empty input yields empty list")
        void empty() {
            assertThat(tasks.createAll(List.of())).isEmpty();
        }

        @Test
        @DisplayName("unknown element throws")
        void unknownThrows() {
            assertThatThrownBy(() -> tasks.createAll(List.of("circle", "hexagon")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}