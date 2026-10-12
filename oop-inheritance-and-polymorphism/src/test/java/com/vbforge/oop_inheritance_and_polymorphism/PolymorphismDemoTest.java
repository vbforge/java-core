package com.vbforge.oop_inheritance_and_polymorphism;

import com.vbforge.oop_inheritance_and_polymorphism.PolymorphismDemo.Circle;
import com.vbforge.oop_inheritance_and_polymorphism.PolymorphismDemo.Rectangle;
import com.vbforge.oop_inheritance_and_polymorphism.PolymorphismDemo.Shape;
import com.vbforge.oop_inheritance_and_polymorphism.PolymorphismDemo.Square;
import com.vbforge.oop_inheritance_and_polymorphism.PolymorphismDemo.Triangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * PolymorphismDemoTest — verifies every concept shown in {@link PolymorphismDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * Java's polymorphism rules.
 */
@DisplayName("PolymorphismDemo — API contracts")
class PolymorphismDemoTest {

    // ── 1. Dynamic dispatch ───────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Dynamic dispatch")
    class DynamicDispatchTest {

        @Test
        @DisplayName("each element of a List<Shape> runs its own name()")
        void eachElementRunsItsOwnOverride() {
            assertThat(PolymorphismDemo.namesOf(PolymorphismDemo.sampleShapes()))
                    .containsExactly("Circle", "Rectangle", "Square");
        }

        @Test
        @DisplayName("area() is dispatched on the runtime class")
        void areaIsDispatched() {
            List<Shape> shapes = PolymorphismDemo.sampleShapes();
            assertThat(shapes.get(0).area()).isCloseTo(Math.PI, within(1e-9));
            assertThat(shapes.get(1).area()).isEqualTo(6.0);
            assertThat(shapes.get(2).area()).isEqualTo(4.0);
        }

        @Test
        @DisplayName("the base class implementation is only the fallback")
        void baseFallback() {
            assertThat(new Shape().area()).isEqualTo(0.0);
            assertThat(new Shape().name()).isEqualTo("Shape");
        }

        @Test
        @DisplayName("toString() is dispatched dynamically, even through an Object reference")
        void toStringIsDispatched() {
            Object rect = new Rectangle(2, 3);
            assertThat(rect.toString()).isEqualTo("Rectangle[area=6.0]");
            assertThat(new Circle(1).toString()).isEqualTo("Circle[area=3.14]");
        }

        @Test
        @DisplayName("a subclass that overrides only name() reuses the parent's area()")
        void partialOverride() {
            Shape square = new Square(3);
            assertThat(square.name()).isEqualTo("Square");
            assertThat(square.area()).isEqualTo(9.0);
        }
    }

    // ── 2. Static type vs runtime type ────────────────────────────────────────

    @Nested
    @DisplayName("2. Static type vs runtime type")
    class StaticVsRuntimeTypeTest {

        @Test
        @DisplayName("getClass() reports the runtime type, not the declared type")
        void getClassIsRuntimeType() {
            Shape s = new Circle(2);
            assertThat(PolymorphismDemo.runtimeClassName(s)).isEqualTo("Circle");
            assertThat(s.getClass()).isEqualTo(Circle.class);
        }

        @Test
        @DisplayName("upcasting does not change the object")
        void upcastKeepsObject() {
            Circle circle = new Circle(2);
            Shape asShape = circle;
            assertThat(asShape).isSameAs(circle);
            assertThat(asShape.name()).isEqualTo("Circle");
        }

        @Test
        @DisplayName("subtype-specific members need a cast back to the subtype")
        void subtypeMembersNeedCast() {
            Shape s = new Circle(2);
            assertThat(((Circle) s).radius()).isEqualTo(2.0);
        }
    }

    // ── 3. Upcasting, downcasting and instanceof ──────────────────────────────

    @Nested
    @DisplayName("3. Upcasting, downcasting and instanceof")
    class CastingTest {

        @Test
        @DisplayName("instanceof is true for the class and all its supertypes")
        void instanceofSupertypes() {
            Shape s = new Square(3);
            assertThat(s instanceof Square).isTrue();
            assertThat(s instanceof Rectangle).isTrue();
            assertThat(s instanceof Shape).isTrue();
            assertThat(s instanceof Circle).isFalse();
        }

        @Test
        @DisplayName("instanceof is false for null")
        void instanceofNull() {
            Shape s = null;
            assertThat(s instanceof Shape).isFalse();
        }

        @Test
        @DisplayName("downcast to a real supertype of the object succeeds and keeps behaviour")
        void validDowncast() {
            Shape s = new Square(3);
            Rectangle r = (Rectangle) s;
            assertThat(r.name()).isEqualTo("Square");
        }

        @Test
        @DisplayName("downcast to an unrelated subtype throws ClassCastException")
        void invalidDowncast() {
            assertThatThrownBy(() -> PolymorphismDemo.unsafeCastToCircle(new Square(3)))
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("guarded downcast with instanceof never throws")
        void guardedDowncast() {
            assertThat(PolymorphismDemo.radiusOrZero(new Circle(5))).isEqualTo(5.0);
            assertThat(PolymorphismDemo.radiusOrZero(new Square(5))).isEqualTo(0.0);
            assertThat(PolymorphismDemo.radiusOrZero(null)).isEqualTo(0.0);
        }
    }

    // ── 4. Overloading vs overriding ──────────────────────────────────────────

    @Nested
    @DisplayName("4. Overloading vs overriding")
    class OverloadVsOverrideTest {

        @Test
        @DisplayName("overload is chosen by the STATIC type of the argument (Shape)")
        void overloadByStaticTypeShape() {
            assertThat(PolymorphismDemo.overloadChosenByStaticType()).isEqualTo("Printer.print(Shape)");
        }

        @Test
        @DisplayName("overload is chosen by the STATIC type of the argument (Object)")
        void overloadByStaticTypeObject() {
            assertThat(PolymorphismDemo.overloadChosenForObject()).isEqualTo("Printer.print(Object)");
        }

        @Test
        @DisplayName("the most specific applicable overload wins")
        void mostSpecificOverload() {
            assertThat(PolymorphismDemo.overloadMostSpecific()).isEqualTo("Printer.print(Circle)");
        }

        @Test
        @DisplayName("override is chosen by the RUNTIME type of the receiver")
        void overrideByRuntimeType() {
            assertThat(PolymorphismDemo.overrideChosenByRuntimeType()).isEqualTo("FancyPrinter.print(Shape)");
        }

        @Test
        @DisplayName("overloads the subclass did not override keep the parent's behaviour")
        void inheritedOverload() {
            assertThat(PolymorphismDemo.inheritedOverloadOnSubclass()).isEqualTo("Printer.print(Circle)");
        }
    }

    // ── 5. Extensibility ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Extensibility")
    class ExtensibilityTest {

        @Test
        @DisplayName("totalPerimeter works for the shapes it was written for")
        void existingSubtypes() {
            List<Shape> shapes = Arrays.<Shape>asList(new Rectangle(2, 3), new Square(1));
            assertThat(PolymorphismDemo.totalPerimeter(shapes)).isEqualTo(14.0);
        }

        @Test
        @DisplayName("a new subtype works without changing totalPerimeter")
        void newSubtype() {
            List<Shape> shapes = new ArrayList<Shape>(Arrays.asList(new Rectangle(2, 3), new Square(1)));
            shapes.add(new Triangle(3, 4, 5));
            assertThat(PolymorphismDemo.totalPerimeter(shapes)).isEqualTo(26.0);
        }

        @Test
        @DisplayName("empty list → 0.0")
        void emptyList() {
            assertThat(PolymorphismDemo.totalPerimeter(new ArrayList<Shape>())).isEqualTo(0.0);
        }

        @Test
        @DisplayName("a subtype that does not override perimeter() falls back to the parent's 0.0")
        void fallbackToParent() {
            assertThat(new Shape().perimeter()).isEqualTo(0.0);
            assertThat(new Triangle(3, 4, 5).area()).isEqualTo(0.0);
        }
    }

    // ── 6. Covariant arrays ───────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Covariant arrays")
    class CovariantArraysTest {

        @Test
        @DisplayName("a String[] can be referenced as Object[]")
        void arrayIsCovariant() {
            Object[] objects = new String[2];
            assertThat(objects).hasSize(2);
            assertThat(objects.getClass()).isEqualTo(String[].class);
        }

        @Test
        @DisplayName("storing a String through the Object[] reference works")
        void storeCompatibleElement() {
            Object[] objects = new String[1];
            objects[0] = "fine";
            assertThat(objects[0]).isEqualTo("fine");
        }

        @Test
        @DisplayName("storing an Integer through the Object[] reference throws ArrayStoreException")
        void storeIncompatibleElement() {
            assertThatThrownBy(PolymorphismDemo::storeIntegerThroughObjectArray)
                    .isInstanceOf(ArrayStoreException.class);
        }
    }
}
