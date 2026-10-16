package com.vbforge.sealed_classes_pattern_matching;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static com.vbforge.sealed_classes_pattern_matching.SealedTypesDemo.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("SealedTypesDemo — API contracts")
class SealedTypesDemoTest {

    @Nested
    @DisplayName("1. Sealed interface with permits")
    class SealedInterface {

        @Test
        @DisplayName("Shape is sealed and permits exactly Circle, Polygon and Square")
        void permittedSubtypes() {
            assertThat(Shape.class.isSealed()).isTrue();
            assertThat(permittedSubclassNames(Shape.class)).containsExactly("Circle", "Polygon", "Square");
        }

        @Test
        @DisplayName("each permitted subtype declares its own modifier")
        void subtypeModifiers() {
            assertThat(modifierOf(Shape.class)).isEqualTo("sealed");
            assertThat(modifierOf(Circle.class)).isEqualTo("final");
            assertThat(modifierOf(Square.class)).isEqualTo("final");
            assertThat(modifierOf(Polygon.class)).isEqualTo("non-sealed");
        }

        @Test
        @DisplayName("non-sealed Polygon may be extended — Hexagon is still a Shape")
        void nonSealedReopensHierarchy() {
            Hexagon hexagon = new Hexagon(10.0);

            assertThat(hexagon).isInstanceOf(Shape.class);
            assertThat(hexagon.sides()).isEqualTo(6);
            assertThat(permittedSubclassNames(Shape.class)).doesNotContain("Hexagon");
        }

        @Test
        @DisplayName("a type that is not sealed has no permitted subclasses")
        void notSealedHasNoPermittedSubclasses() {
            assertThat(permittedSubclassNames(String.class)).isEmpty();
            assertThat(permittedSubclassNames(Circle.class)).isEmpty();
        }
    }

    @Nested
    @DisplayName("2. Sealed abstract class with a sub-sealed branch")
    class SubSealed {

        @Test
        @DisplayName("Vehicle permits Bicycle and Car; Car permits ElectricCar and PetrolCar")
        void twoLevels() {
            assertThat(permittedSubclassNames(Vehicle.class)).containsExactly("Bicycle", "Car");
            assertThat(permittedSubclassNames(Car.class)).containsExactly("ElectricCar", "PetrolCar");
        }

        @Test
        @DisplayName("Car is sealed itself; its subtypes are final")
        void subSealedModifiers() {
            assertThat(modifierOf(Car.class)).isEqualTo("sealed");
            assertThat(modifierOf(ElectricCar.class)).isEqualTo("final");
            assertThat(modifierOf(Bicycle.class)).isEqualTo("final");
        }

        @Test
        @DisplayName("Vehicle and Car are abstract, so only the leaves can be instantiated")
        void abstractRoots() {
            assertThat(Modifier.isAbstract(Vehicle.class.getModifiers())).isTrue();
            assertThat(Modifier.isAbstract(Car.class.getModifiers())).isTrue();
        }

        @Test
        @DisplayName("wheels() is inherited through the hierarchy")
        void wheels() {
            assertThat(new ElectricCar().wheels()).isEqualTo(4);
            assertThat(new PetrolCar().wheels()).isEqualTo(4);
            assertThat(new Bicycle().wheels()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("3. Exhaustive switch")
    class ExhaustiveSwitch {

        @Test
        @DisplayName("area of a circle is pi * r^2")
        void circleArea() {
            assertThat(area(new Circle(2.0))).isCloseTo(Math.PI * 4, within(1e-9));
        }

        @Test
        @DisplayName("area of a square is side^2")
        void squareArea() {
            assertThat(area(new Square(3.0))).isEqualTo(9.0);
        }

        @Test
        @DisplayName("Polygon branch also handles Hexagon, a subclass of the non-sealed type")
        void polygonBranchCoversSubclasses() {
            assertThat(area(new Polygon(3, 4.5))).isEqualTo(4.5);
            assertThat(area(new Hexagon(10.0))).isEqualTo(10.0);
        }

        @Test
        @DisplayName("traffic light cycle: red → green → yellow → red")
        void trafficLightCycle() {
            assertThat(nextLight(new Red())).isEqualTo(new Green());
            assertThat(nextLight(new Green())).isEqualTo(new Yellow());
            assertThat(nextLight(new Yellow())).isEqualTo(new Red());
        }
    }

    @Nested
    @DisplayName("4. Implicit permits")
    class ImplicitPermits {

        @Test
        @DisplayName("compiler infers the permitted subtypes from the same file")
        void inferredPermits() {
            assertThat(TrafficLight.class.isSealed()).isTrue();
            assertThat(permittedSubclassNames(TrafficLight.class)).containsExactly("Green", "Red", "Yellow");
        }

        @Test
        @DisplayName("records are implicitly final")
        void recordsAreFinal() {
            assertThat(modifierOf(Red.class)).isEqualTo("final");
            assertThat(Modifier.isFinal(Green.class.getModifiers())).isTrue();
        }
    }
}
