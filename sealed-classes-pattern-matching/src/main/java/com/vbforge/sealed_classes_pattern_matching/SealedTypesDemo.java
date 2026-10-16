package com.vbforge.sealed_classes_pattern_matching;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * Sealed classes and interfaces (Java 17+).
 *
 * <p>A <b>sealed</b> type lists exactly which types may extend / implement it.
 * Anyone else gets a compile error. This gives you a <i>closed</i> hierarchy:
 * the compiler knows every possible subtype, so a {@code switch} over the
 * hierarchy can be checked for completeness without a {@code default} branch.
 *
 * <p>Sections:
 * <ol>
 *   <li>Sealed interface + {@code permits} + the three subtype modifiers</li>
 *   <li>Sealed abstract class with a sub-sealed branch</li>
 *   <li>Exhaustive {@code switch} — the payoff</li>
 *   <li>Implicit {@code permits} (everything in one file)</li>
 * </ol>
 *
 * <p>Complexity: not relevant here — sealing is a compile-time / class-loading
 * contract, not an algorithm.
 */
public class SealedTypesDemo {

    // ── 1. Sealed interface + permits ──────────────────────────────────────────
    /**
     * What: {@code sealed ... permits A, B, C} — only A, B and C may implement Shape.
     *
     * <p>Why: before Java 17 you had two bad options — an {@code interface}/open class
     * (anyone can add subtypes, so every {@code switch} needs a defensive
     * {@code default}) or an {@code enum} (closed, but every constant is a single
     * instance with the same fields). Sealed types are closed <i>and</i> each
     * subtype can carry its own data.
     *
     * <p>Every permitted subtype MUST choose exactly one modifier:
     * <ul>
     *   <li>{@code final} — no further subclasses (records and enums are implicitly final)</li>
     *   <li>{@code sealed} — the hierarchy continues, with its own {@code permits}</li>
     *   <li>{@code non-sealed} — the hierarchy is deliberately re-opened below this type</li>
     * </ul>
     *
     * <p>Gotcha: omitting the modifier is a compile error — the compiler forces you to
     * decide, on purpose.
     */
    public sealed interface Shape permits Circle, Square, Polygon { }

    public static final class Circle implements Shape {
        private final double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        public double radius() {
            return radius;
        }
    }

    public static final class Square implements Shape {
        private final double side;

        public Square(double side) {
            this.side = side;
        }

        public double side() {
            return side;
        }
    }

    /** non-sealed: anybody may extend Polygon, e.g. {@link Hexagon} below. */
    public static non-sealed class Polygon implements Shape {
        private final int sides;
        private final double area;

        public Polygon(int sides, double area) {
            this.sides = sides;
            this.area = area;
        }

        public int sides() {
            return sides;
        }

        public double area() {
            return area;
        }
    }

    /** Not listed in Shape's permits — allowed only because Polygon is non-sealed. */
    public static class Hexagon extends Polygon {
        public Hexagon(double area) {
            super(6, area);
        }
    }

    /**
     * Reflection makes the "closed world" visible at runtime.
     *
     * @return simple names of the permitted direct subtypes, sorted alphabetically;
     *         empty list if {@code type} is not sealed
     */
    public static List<String> permittedSubclassNames(Class<?> type) {
        if (!type.isSealed()) {
            return List.of();
        }
        return Arrays.stream(type.getPermittedSubclasses())
                .map(Class::getSimpleName)
                .sorted()
                .toList();
    }

    /**
     * @return "sealed", "final" or "non-sealed" — the modifier a type has inside a
     *         sealed hierarchy
     */
    public static String modifierOf(Class<?> type) {
        if (type.isSealed()) {
            return "sealed";
        }
        return Modifier.isFinal(type.getModifiers()) ? "final" : "non-sealed";
    }

    public static void sealedInterfaceDemo() {
        System.out.println("Shape permits:   " + permittedSubclassNames(Shape.class));
        System.out.println("Circle   is:     " + modifierOf(Circle.class));
        System.out.println("Polygon  is:     " + modifierOf(Polygon.class));
        System.out.println("Hexagon extends Polygon, so it is a Shape: " + (new Hexagon(10) instanceof Shape));

        // class Triangle implements Shape { }   // ❌ does not compile — Triangle is not in permits
    }


    // ── 2. Sealed abstract class with a sub-sealed branch ─────────────────────
    /**
     * What: sealed works on classes too, and a permitted subtype may itself be
     * {@code sealed} — the closed hierarchy then has several levels.
     *
     * <p>Why: model "a Vehicle is a Car or a Bicycle, and a Car is electric or
     * petrol" with the compiler knowing the complete tree.
     *
     * <p>Gotcha: permitted subclasses must live in the same module (or, in the
     * unnamed module, the same package) as the sealed class.
     */
    public abstract static sealed class Vehicle permits Car, Bicycle {
        public abstract int wheels();
    }

    public abstract static sealed class Car extends Vehicle permits ElectricCar, PetrolCar {
        @Override
        public int wheels() {
            return 4;
        }
    }

    public static final class ElectricCar extends Car { }

    public static final class PetrolCar extends Car { }

    public static final class Bicycle extends Vehicle {
        @Override
        public int wheels() {
            return 2;
        }
    }

    public static void subSealedDemo() {
        System.out.println("Vehicle permits: " + permittedSubclassNames(Vehicle.class));
        System.out.println("Car permits:     " + permittedSubclassNames(Car.class));
        System.out.println("Car is:          " + modifierOf(Car.class));
        System.out.println("Electric car wheels: " + new ElectricCar().wheels());
    }


    // ── 3. Exhaustive switch ──────────────────────────────────────────────────
    /**
     * What: a {@code switch} over a sealed type that covers every permitted subtype
     * needs no {@code default}.
     *
     * <p>Why: the compiler checks completeness. If you later add a fourth Shape,
     * every such switch stops compiling until you handle it — bugs move from
     * runtime to compile time.
     *
     * <p>Gotcha: adding a {@code default} silences that safety net — the new subtype
     * would silently fall into it.
     */
    public static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Square s -> s.side() * s.side();
            case Polygon p -> p.area();         // also matches Hexagon
        };
    }

    public static void exhaustiveSwitchDemo() {
        System.out.println("Circle(1) area:   " + area(new Circle(1)));
        System.out.println("Square(3) area:   " + area(new Square(3)));
        System.out.println("Hexagon(10) area: " + area(new Hexagon(10)));
    }


    // ── 4. Implicit permits ───────────────────────────────────────────────────
    /**
     * What: if the sealed type and all its subtypes are in the SAME source file, the
     * {@code permits} clause can be omitted — the compiler infers it.
     *
     * <p>Why: less ceremony for small hierarchies. Records are implicitly final,
     * so they are the natural permitted subtypes.
     */
    public sealed interface TrafficLight { }

    public record Red() implements TrafficLight { }

    public record Yellow() implements TrafficLight { }

    public record Green() implements TrafficLight { }

    public static TrafficLight nextLight(TrafficLight light) {
        return switch (light) {
            case Red r -> new Green();
            case Green g -> new Yellow();
            case Yellow y -> new Red();
        };
    }

    public static void implicitPermitsDemo() {
        System.out.println("TrafficLight permits: " + permittedSubclassNames(TrafficLight.class));
        System.out.println("After Red comes: " + nextLight(new Red()));
    }


    public static void main(String[] args) {
        sealedInterfaceDemo();
        System.out.println("---");
        subSealedDemo();
        System.out.println("---");
        exhaustiveSwitchDemo();
        System.out.println("---");
        implicitPermitsDemo();
    }
}
