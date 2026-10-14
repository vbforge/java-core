package com.vbforge.oop_encapsulation_patterns;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * RecordsDemo — annotated reference for Java records (Java 16+).
 *
 * <p>Read top-to-bottom. Each section covers one facet of records with
 * concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>Record basics — one line gives constructor, accessors, {@code equals}, {@code hashCode},
 *       {@code toString}</li>
 *   <li>The compact constructor — validate and normalise</li>
 *   <li>Shallow immutability — lists and arrays inside a record</li>
 *   <li>Extra members — static factories, instance methods, "withers"</li>
 *   <li>Records, interfaces and reflection — what a record can and cannot extend</li>
 *   <li>Record vs hand-written class — and when NOT to use a record</li>
 * </ol>
 */
public class RecordsDemo {

    public static void main(String[] args) {
        basicsDemo();
        System.out.println("---");
        compactConstructorDemo();
        System.out.println("---");
        shallowImmutabilityDemo();
        System.out.println("---");
        extraMembersDemo();
        System.out.println("---");
        interfacesAndReflectionDemo();
        System.out.println("---");
        recordVsClassDemo();
    }

    // ── 1. Record basics ──────────────────────────────────────────────────────

    record Point(int x, int y) { }

    /**
     * {@code record Point(int x, int y) { }} is a complete class. The compiler generates:
     * <ul>
     *   <li>two {@code private final} fields {@code x} and {@code y}</li>
     *   <li>a canonical constructor {@code Point(int x, int y)}</li>
     *   <li>accessors named after the components — {@code x()}, {@code y()} (NOT {@code getX()})</li>
     *   <li>{@code equals}/{@code hashCode} based on ALL components</li>
     *   <li>{@code toString} like {@code Point[x=1, y=2]}</li>
     * </ul>
     *
     * <p>WHY this matters: a "plain data carrier" used to need about 30 lines of boilerplate
     * (see section 6), and every hand-written {@code equals} was a chance for a bug. A record
     * states the intent in one line and removes the boilerplate.
     *
     * <p>Common gotcha: the accessor is {@code p.x()}, not {@code p.getX()}; and a record has
     * NO setters — its fields are final.
     */
    static void basicsDemo() {
        System.out.println("=== Record basics ===");
        Point p = new Point(1, 2);
        System.out.println("toString        : " + p);                       // Point[x=1, y=2]
        System.out.println("x() / y()       : " + p.x() + " / " + p.y());
        System.out.println("equals          : " + p.equals(new Point(1, 2)));   // true
    }

    // ── 2. The compact constructor ────────────────────────────────────────────

    record Range(int from, int to) {
        Range {                                                  // compact: no parameter list
            if (from > to) {
                throw new IllegalArgumentException("from must not exceed to");
            }
        }

        int length() {
            return to - from;
        }
    }

    record Username(String value) {
        Username {
            Objects.requireNonNull(value, "value");
            value = value.trim().toLowerCase(Locale.ROOT);       // assigns the PARAMETER; the field gets it after
            if (value.isEmpty()) {
                throw new IllegalArgumentException("value must not be blank");
            }
        }
    }

    /**
     * A compact constructor ({@code Range { ... }}) runs BEFORE the fields are assigned. Inside
     * it you can validate the parameters and even reassign them — {@code value = value.trim()}
     * changes the parameter, and the compiler then copies the final parameter values into the
     * fields. You never write {@code this.value = value}.
     *
     * <p>WHY this matters: the compact constructor is the one place every record instance passes
     * through, so it is the place to enforce the invariant ("from ≤ to") and to normalise input
     * ("  Bob " → "bob").
     *
     * <p>Common gotcha: you cannot assign to the fields ({@code this.value = ...}) inside a
     * compact constructor — only to the parameters.
     */
    static void compactConstructorDemo() {
        System.out.println("=== The compact constructor ===");
        System.out.println("Range(2, 7).length()   : " + new Range(2, 7).length());
        System.out.println("Username normalised    : " + new Username("  BoB ").value());   // bob
        try {
            new Range(5, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("Range(5, 1) refused    : " + e.getMessage());
        }
    }

    // ── 3. Shallow immutability ───────────────────────────────────────────────

    record Team(String name, List<String> members) { }

    record SafeTeam(String name, List<String> members) {
        SafeTeam {
            members = List.copyOf(members);                      // defensive copy, read-only
        }
    }

    record RawSamples(int[] values) { }

    record Samples(int[] values) {
        Samples {
            values = values.clone();                             // copy IN
        }

        @Override
        public int[] values() {
            return values.clone();                               // copy OUT
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Samples other && Arrays.equals(values, other.values);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(values);
        }

        @Override
        public String toString() {
            return "Samples" + Arrays.toString(values);
        }
    }

    /**
     * A record's fields are {@code final}, so the REFERENCES never change — but a record holding
     * a {@code List} or an array is only as immutable as that list or array. Everything from the
     * {@code ImmutabilityDemo} (section 3: shallow vs deep, defensive copies) applies unchanged.
     *
     * <p>Two traps:
     * <ul>
     *   <li>A mutable {@code List} component: copy it with {@code List.copyOf} in the compact
     *       constructor.</li>
     *   <li>An ARRAY component: the generated {@code equals} compares arrays with {@code ==}
     *       (identity), not by content, and {@code toString} prints {@code [I@1b2c3d}. Override
     *       {@code equals}, {@code hashCode}, {@code toString} and the accessor.</li>
     * </ul>
     *
     * <p>Common gotcha: prefer a {@code List} over an array as a record component — it avoids
     * the whole second trap.
     */
    static void shallowImmutabilityDemo() {
        System.out.println("=== Shallow immutability ===");
        List<String> source = new ArrayList<>(List.of("ann"));
        Team leaky = new Team("A", source);
        SafeTeam safe = new SafeTeam("A", source);
        source.add("bob");
        System.out.println("leaky members : " + leaky.members());            // [ann, bob]
        System.out.println("safe members  : " + safe.members());             // [ann]

        System.out.println("raw arrays equal?  : " + new RawSamples(new int[] {1}).equals(new RawSamples(new int[] {1})));   // false
        System.out.println("fixed arrays equal?: " + new Samples(new int[] {1}).equals(new Samples(new int[] {1})));        // true
    }

    // ── 4. Extra members ──────────────────────────────────────────────────────

    record Rectangle(int width, int height) {
        static final Rectangle UNIT = new Rectangle(1, 1);       // static fields are allowed

        Rectangle(int side) {                                    // extra constructors MUST delegate
            this(side, side);
        }

        static Rectangle square(int side) {                      // static factory
            return new Rectangle(side);
        }

        int area() {                                             // instance method
            return width * height;
        }

        Rectangle withWidth(int newWidth) {                      // "wither": a new record, not a setter
            return new Rectangle(newWidth, height);
        }
        // int extra;                                            // ← compile error: no instance fields
    }

    /**
     * A record body can contain: static fields, static methods (factories), instance methods,
     * extra constructors (each must start with {@code this(...)}), nested types. It can NOT
     * declare additional INSTANCE fields — the state is exactly the components.
     *
     * <p>Records have no built-in "with" feature: to "change" one component you write a wither
     * that returns a new record, exactly as with any immutable class.
     *
     * <p>WHY this matters: because the state is fixed, {@code equals}, {@code hashCode} and
     * {@code toString} stay correct no matter how many methods you add.
     *
     * <p>Common gotcha: you CAN declare the accessor yourself ({@code public int width() { ... }})
     * to transform the value on the way out — it must be {@code public} and return the same type.
     */
    static void extraMembersDemo() {
        System.out.println("=== Extra members ===");
        Rectangle r = new Rectangle(3, 4);
        System.out.println("area()            : " + r.area());                 // 12
        System.out.println("square(5)         : " + Rectangle.square(5));      // Rectangle[width=5, height=5]
        System.out.println("withWidth(10)     : " + r.withWidth(10));          // Rectangle[width=10, height=4]
        System.out.println("original          : " + r);                        // unchanged
        System.out.println("UNIT              : " + Rectangle.UNIT);
    }

    // ── 5. Records, interfaces and reflection ─────────────────────────────────

    interface Shape {
        double area();
    }

    record Circle(double radius) implements Shape {
        @Override
        public double area() {                                   // interface methods must be public
            return Math.PI * radius * radius;
        }
    }

    static List<String> componentNames(Class<?> type) {
        List<String> names = new ArrayList<>();
        if (type.isRecord()) {
            for (RecordComponent component : type.getRecordComponents()) {
                names.add(component.getName());
            }
        }
        return names;
    }

    /**
     * A record IMPLICITLY extends {@code java.lang.Record} and is implicitly {@code final}, so:
     * <ul>
     *   <li>it can implement any number of interfaces ({@code Circle implements Shape})</li>
     *   <li>it can NOT extend another class (the one superclass slot is taken)</li>
     *   <li>nothing can extend it (it is final), and it cannot be {@code abstract}</li>
     * </ul>
     *
     * <p>Reflection knows about records: {@code Class.isRecord()} and
     * {@code getRecordComponents()} list the components in declaration order.
     *
     * <p>WHY this matters: records plus interfaces model "closed, immutable data variants"
     * (the next step is sealed interfaces — see {@code sealed-classes-pattern-matching}).
     */
    static void interfacesAndReflectionDemo() {
        System.out.println("=== Records, interfaces and reflection ===");
        Shape shape = new Circle(2);
        System.out.printf(Locale.ROOT, "Circle(2).area()   : %.4f%n", shape.area());
        System.out.println("isRecord           : " + Circle.class.isRecord());
        System.out.println("superclass         : " + Circle.class.getSuperclass().getName());
        System.out.println("final?             : " + Modifier.isFinal(Circle.class.getModifiers()));
        System.out.println("components of Rect : " + componentNames(Rectangle.class));   // [width, height]
    }

    // ── 6. Record vs hand-written class ───────────────────────────────────────

    record PointRecord(int x, int y) { }

    static final class PointClass {
        private final int x;
        private final int y;

        PointClass(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x() {
            return x;
        }

        int y() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PointClass other = (PointClass) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "PointClass[x=" + x + ", y=" + y + "]";
        }
    }

    /**
     * {@code PointRecord} (1 line) and {@code PointClass} (about 30 lines) behave the same way:
     * same accessors, value-based {@code equals}, a {@code toString} listing the fields.
     *
     * <p>When a record is the RIGHT choice: DTOs, value objects ({@code Money}, {@code Range}),
     * map keys, multiple return values, API responses.
     *
     * <p>When it is the WRONG choice:
     * <ul>
     *   <li>the object must change over time (needs setters) — e.g. a JPA entity</li>
     *   <li>it needs a superclass or must be subclassed</li>
     *   <li>identity matters more than value (two orders with identical fields are still two orders)</li>
     * </ul>
     */
    static void recordVsClassDemo() {
        System.out.println("=== Record vs hand-written class ===");
        System.out.println("record toString : " + new PointRecord(1, 2));
        System.out.println("class toString  : " + new PointClass(1, 2));
        System.out.println("record equals   : " + new PointRecord(1, 2).equals(new PointRecord(1, 2)));
        System.out.println("class equals    : " + new PointClass(1, 2).equals(new PointClass(1, 2)));
    }
}
