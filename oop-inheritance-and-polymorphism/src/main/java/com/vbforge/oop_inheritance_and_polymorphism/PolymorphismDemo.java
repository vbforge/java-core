package com.vbforge.oop_inheritance_and_polymorphism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PolymorphismDemo — annotated reference for runtime polymorphism in Java.
 *
 * <p>Read top-to-bottom. Each section shows one facet of "one reference type, many
 * behaviours" with concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>Dynamic dispatch — the runtime object decides which override runs</li>
 *   <li>Static type vs runtime type — what the compiler sees vs what the JVM sees</li>
 *   <li>Upcasting, downcasting, {@code instanceof} and {@link ClassCastException}</li>
 *   <li>Overloading vs overriding — compile-time vs runtime selection</li>
 *   <li>Extensibility — adding a new subtype without touching existing code</li>
 *   <li>Covariant arrays — a polymorphism trap ({@link ArrayStoreException})</li>
 * </ol>
 */
public class PolymorphismDemo {

    public static void main(String[] args) {
        dynamicDispatchDemo();
        System.out.println("---");
        staticVsRuntimeTypeDemo();
        System.out.println("---");
        castingDemo();
        System.out.println("---");
        overloadVsOverrideDemo();
        System.out.println("---");
        extensibilityDemo();
        System.out.println("---");
        covariantArraysDemo();
    }

    // ── Shared model ──────────────────────────────────────────────────────────

    /** Base type of the hierarchy Shape ← Circle, Rectangle ← Square, Triangle. */
    static class Shape {
        double area() {
            return 0.0;
        }

        double perimeter() {
            return 0.0;
        }

        String name() {
            return "Shape";
        }

        @Override
        public String toString() {
            return name() + "[area=" + round2(area()) + "]";
        }

        private static double round2(double value) {
            return Math.round(value * 100) / 100.0;
        }
    }

    static class Circle extends Shape {
        private final double radius;

        Circle(double radius) {
            this.radius = radius;
        }

        double radius() {
            return radius;
        }

        @Override
        double area() {
            return Math.PI * radius * radius;
        }

        @Override
        double perimeter() {
            return 2 * Math.PI * radius;
        }

        @Override
        String name() {
            return "Circle";
        }
    }

    static class Rectangle extends Shape {
        private final double width;
        private final double height;

        Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        double area() {
            return width * height;
        }

        @Override
        double perimeter() {
            return 2 * (width + height);
        }

        @Override
        String name() {
            return "Rectangle";
        }
    }

    static class Square extends Rectangle {
        Square(double side) {
            super(side, side);
        }

        @Override
        String name() {
            return "Square";
        }
    }

    /** Added in section 5 — none of the existing code below needs to change for it. */
    static class Triangle extends Shape {
        private final double a;
        private final double b;
        private final double c;

        Triangle(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        double perimeter() {
            return a + b + c;
        }

        @Override
        String name() {
            return "Triangle";
        }
    }

    // ── 1. Dynamic dispatch ───────────────────────────────────────────────────

    /**
     * Dynamic dispatch (runtime polymorphism): when you call an overridden instance method
     * through a reference, the JVM runs the version belonging to the object's ACTUAL class.
     *
     * <p>WHY this matters: you can write code against the parent type ({@code Shape}) once and it
     * works for every present and future subtype. It replaces long {@code if (x instanceof A) ...
     * else if (x instanceof B) ...} chains.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>A {@code List<Shape>} can hold a Circle, a Rectangle and a Square at once</li>
     *   <li>{@code shape.area()} runs a different method body for each element</li>
     *   <li>{@code println(shape)} calls {@link Object#toString()} — which is dispatched dynamically too</li>
     * </ul>
     *
     * <p>Common gotcha: dispatch only applies to instance methods. {@code static} methods, fields
     * and {@code private} methods are NOT dispatched dynamically (see {@link InheritanceDemo}, section 6).
     */
    static void dynamicDispatchDemo() {
        System.out.println("=== Dynamic dispatch ===");

        List<Shape> shapes = sampleShapes();
        for (Shape shape : shapes) {
            System.out.println(shape.name() + " → " + shape);           // toString() is virtual as well
        }
        System.out.println("names                 : " + namesOf(shapes));   // [Circle, Rectangle, Square]
    }

    static List<Shape> sampleShapes() {
        return new ArrayList<Shape>(Arrays.asList(new Circle(1), new Rectangle(2, 3), new Square(2)));
    }

    static List<String> namesOf(List<? extends Shape> shapes) {
        List<String> names = new ArrayList<>();
        for (Shape shape : shapes) {
            names.add(shape.name());                                    // each element runs ITS override
        }
        return names;
    }

    // ── 2. Static type vs runtime type ────────────────────────────────────────

    /**
     * Every reference has two types: the STATIC (declared) type the compiler knows, and the
     * RUNTIME (actual) type of the object it points to.
     *
     * <p>WHY this matters: the compiler uses the static type to decide what you may CALL
     * ("does Shape have this method?"); the JVM uses the runtime type to decide which body RUNS.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code Shape s = new Circle(2)} — static type Shape, runtime type Circle</li>
     *   <li>{@code s.getClass()} reports the runtime type</li>
     *   <li>{@code s.radius()} does NOT compile — {@code Shape} has no such method; the compiler
     *       only looks at the static type</li>
     * </ul>
     *
     * <p>Common gotcha: assigning to a wider type never changes the object. Upcasting only changes
     * what the compiler lets you call.
     */
    static void staticVsRuntimeTypeDemo() {
        System.out.println("=== Static vs runtime type ===");

        Shape s = new Circle(2);                                        // static: Shape, runtime: Circle
        System.out.println("runtime class         : " + runtimeClassName(s));   // Circle
        System.out.println("s.name()              : " + s.name());              // Circle
        // s.radius();                                                  // ← compile error: not a Shape method
        System.out.println("((Circle) s).radius() : " + ((Circle) s).radius());// 2.0
    }

    static String runtimeClassName(Object o) {
        return o.getClass().getSimpleName();
    }

    // ── 3. Upcasting, downcasting and instanceof ──────────────────────────────

    /**
     * Casting moves a reference up or down the hierarchy WITHOUT changing the object.
     *
     * <p>WHY this matters: upcasting is always safe and implicit; downcasting is how you get back
     * subtype-specific members, but the JVM re-checks it at runtime.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>Upcast ({@code Shape s = new Square(3)}) — implicit, always succeeds</li>
     *   <li>Downcast to a type the object really has ({@code (Rectangle) s}) — succeeds</li>
     *   <li>Downcast to an unrelated subtype ({@code (Circle) s}) — throws {@link ClassCastException}</li>
     *   <li>{@code x instanceof T} is {@code true} for T and all its supertypes, and {@code false} for {@code null}</li>
     * </ul>
     *
     * <p>Common gotcha: if you find yourself downcasting often, the parent type is probably missing
     * a method that should be overridden instead.
     */
    static void castingDemo() {
        System.out.println("=== Casting and instanceof ===");

        Shape s = new Square(3);                                        // implicit upcast
        System.out.println("s instanceof Rectangle: " + (s instanceof Rectangle));   // true  (Square IS-A Rectangle)
        System.out.println("s instanceof Circle   : " + (s instanceof Circle));      // false
        System.out.println("null instanceof Shape : " + (null instanceof Shape));    // false

        Rectangle r = (Rectangle) s;                                    // legal downcast
        System.out.println("downcast to Rectangle : " + r.name());                  // Square (still a Square!)
        System.out.println("radiusOrZero(circle)  : " + radiusOrZero(new Circle(5)));   // 5.0
        System.out.println("radiusOrZero(square)  : " + radiusOrZero(s));               // 0.0

        try {
            Circle bad = (Circle) s;                                    // compiles, fails at runtime
            System.out.println(bad);
        } catch (ClassCastException e) {
            System.out.println("(Circle) square      : ClassCastException");
        }
    }

    /** Guarded downcast: check with instanceof first, then cast. */
    static double radiusOrZero(Shape shape) {
        if (shape instanceof Circle) {
            return ((Circle) shape).radius();
        }
        return 0.0;
    }

    static Circle unsafeCastToCircle(Shape shape) {
        return (Circle) shape;
    }

    // ── 4. Overloading vs overriding ──────────────────────────────────────────

    static class Printer {
        String print(Shape shape) {
            return "Printer.print(Shape)";
        }

        String print(Circle circle) {
            return "Printer.print(Circle)";
        }

        String print(Object object) {
            return "Printer.print(Object)";
        }
    }

    static class FancyPrinter extends Printer {
        @Override
        String print(Shape shape) {                                     // overrides ONE of the three overloads
            return "FancyPrinter.print(Shape)";
        }
    }

    /**
     * OVERLOADING (same name, different parameter types) is resolved by the compiler from the
     * STATIC types of the arguments. OVERRIDING is resolved by the JVM from the RUNTIME type of
     * the receiver.
     *
     * <p>WHY this matters: the two mechanisms look alike but answer different questions, and mixing
     * them up is a classic interview trap.
     *
     * <p>Rules shown here (all with a Circle object):
     * <ul>
     *   <li>Argument declared as {@code Shape} → {@code print(Shape)} is chosen, even though the object is a Circle</li>
     *   <li>Argument declared as {@code Object} → {@code print(Object)}</li>
     *   <li>Argument expression of type {@code Circle} → the most specific overload, {@code print(Circle)}</li>
     *   <li>Receiver declared as {@code Printer} but created as {@code FancyPrinter} → the override runs</li>
     * </ul>
     *
     * <p>Common gotcha: overload resolution NEVER looks at the argument's runtime class. To get
     * runtime behaviour, put the logic inside an overridden method of the argument's own type.
     */
    static void overloadVsOverrideDemo() {
        System.out.println("=== Overloading vs overriding ===");

        System.out.println("static type Shape     : " + overloadChosenByStaticType());      // Printer.print(Shape)
        System.out.println("static type Object    : " + overloadChosenForObject());         // Printer.print(Object)
        System.out.println("static type Circle    : " + overloadMostSpecific());            // Printer.print(Circle)
        System.out.println("override at runtime   : " + overrideChosenByRuntimeType());     // FancyPrinter.print(Shape)
        System.out.println("overload not overridden: " + inheritedOverloadOnSubclass());    // Printer.print(Circle)
    }

    static String overloadChosenByStaticType() {
        Shape shape = new Circle(1);                                    // runtime Circle, static Shape
        return new Printer().print(shape);
    }

    static String overloadChosenForObject() {
        Object object = new Circle(1);
        return new Printer().print(object);
    }

    static String overloadMostSpecific() {
        return new Printer().print(new Circle(1));
    }

    static String overrideChosenByRuntimeType() {
        Printer printer = new FancyPrinter();                           // static Printer, runtime FancyPrinter
        Shape shape = new Circle(1);
        return printer.print(shape);
    }

    static String inheritedOverloadOnSubclass() {
        Printer printer = new FancyPrinter();
        return printer.print(new Circle(1));                            // FancyPrinter did not override print(Circle)
    }

    // ── 5. Extensibility ──────────────────────────────────────────────────────

    /**
     * Polymorphic code is OPEN for extension and CLOSED for modification: adding a new subtype
     * does not require editing the code that uses the parent type.
     *
     * <p>WHY this matters: this is the practical payoff of polymorphism (the "O" in SOLID).
     * {@code totalPerimeter} below was written before {@code Triangle} existed and still works.
     *
     * <p>Common gotcha: the pay-off disappears the moment you add {@code instanceof} chains inside
     * the generic method — every new subtype then forces you to edit it again.
     */
    static void extensibilityDemo() {
        System.out.println("=== Extensibility ===");

        List<Shape> shapes = new ArrayList<Shape>(Arrays.asList(new Rectangle(2, 3), new Square(1)));
        System.out.println("perimeter, 2 shapes   : " + totalPerimeter(shapes));        // 14.0

        shapes.add(new Triangle(3, 4, 5));                              // brand-new subtype, no other edits
        System.out.println("perimeter, +Triangle  : " + totalPerimeter(shapes));        // 26.0
    }

    static double totalPerimeter(List<? extends Shape> shapes) {
        double total = 0;
        for (Shape shape : shapes) {
            total += shape.perimeter();                                 // no instanceof, no casts
        }
        return total;
    }

    // ── 6. Covariant arrays ───────────────────────────────────────────────────

    /**
     * Arrays are COVARIANT: a {@code String[]} is accepted wherever an {@code Object[]} is expected.
     * The JVM remembers the real element type and checks every store at runtime.
     *
     * <p>WHY this matters: it is the one place where polymorphism of references lets you write
     * type-unsafe code that the compiler accepts. Generic collections avoid the problem because
     * {@code List<String>} is NOT a {@code List<Object>} (generics are invariant).
     *
     * <p>Common gotcha: the failure is an {@link ArrayStoreException} at the moment of the store,
     * not at the assignment of the array reference.
     */
    static void covariantArraysDemo() {
        System.out.println("=== Covariant arrays ===");

        Object[] objects = new String[2];                               // legal: String[] IS-A Object[]
        objects[0] = "fine";
        System.out.println("store a String        : ok → " + objects[0]);
        try {
            storeIntegerThroughObjectArray();
        } catch (ArrayStoreException e) {
            System.out.println("store an Integer      : ArrayStoreException");
        }
    }

    static void storeIntegerThroughObjectArray() {
        Object[] objects = new String[1];
        objects[0] = Integer.valueOf(1);                                // compiles; throws ArrayStoreException
    }
}
