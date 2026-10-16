package com.vbforge.sealed_classes_pattern_matching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.vbforge.sealed_classes_pattern_matching.SealedTypesDemo.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SealedTypesTasks — your implementations")
class SealedTypesTasksTest {

    private SealedTypesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new SealedTypesTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement shapeName()")
    @DisplayName("shapeName")
    class ShapeNameTest {

        @Test
        @DisplayName("names circle and square")
        void circleAndSquare() {
            assertThat(tasks.shapeName(new Circle(1.0))).isEqualTo("circle");
            assertThat(tasks.shapeName(new Square(2.0))).isEqualTo("square");
        }

        @Test
        @DisplayName("polygon name includes its number of sides")
        void polygon() {
            assertThat(tasks.shapeName(new Polygon(3, 4.5))).isEqualTo("polygon with 3 sides");
            assertThat(tasks.shapeName(new Polygon(8, 1.0))).isEqualTo("polygon with 8 sides");
        }

        @Test
        @DisplayName("subclass of the non-sealed Polygon is named as a polygon")
        void hexagon() {
            assertThat(tasks.shapeName(new Hexagon(10.0))).isEqualTo("polygon with 6 sides");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement vehicleKind()")
    @DisplayName("vehicleKind")
    class VehicleKindTest {

        @Test
        @DisplayName("electric and petrol cars")
        void cars() {
            assertThat(tasks.vehicleKind(new ElectricCar())).isEqualTo("electric");
            assertThat(tasks.vehicleKind(new PetrolCar())).isEqualTo("petrol");
        }

        @Test
        @DisplayName("bicycle is human-powered")
        void bicycle() {
            assertThat(tasks.vehicleKind(new Bicycle())).isEqualTo("human-powered");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countLeafTypes()")
    @DisplayName("countLeafTypes")
    class CountLeafTypesTest {

        @Test
        @DisplayName("Shape has three leaves; non-sealed Polygon counts as one leaf")
        void shape() {
            assertThat(tasks.countLeafTypes(Shape.class)).isEqualTo(3);
        }

        @Test
        @DisplayName("Vehicle: leaves are found through the sub-sealed Car")
        void vehicle() {
            assertThat(tasks.countLeafTypes(Vehicle.class)).isEqualTo(3);
        }

        @Test
        @DisplayName("starting from a sub-sealed type counts only its branch")
        void subBranch() {
            assertThat(tasks.countLeafTypes(Car.class)).isEqualTo(2);
        }

        @Test
        @DisplayName("implicitly permitted records are leaves too")
        void trafficLight() {
            assertThat(tasks.countLeafTypes(TrafficLight.class)).isEqualTo(3);
        }

        @Test
        @DisplayName("a type that is not sealed counts as one leaf")
        void notSealed() {
            assertThat(tasks.countLeafTypes(Circle.class)).isEqualTo(1);
            assertThat(tasks.countLeafTypes(String.class)).isEqualTo(1);
        }
    }
}
