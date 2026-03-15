package com.vbforge.oop;

import java.util.Objects;

/**
 * ═══════════════════════════════════════════════════════════════
 *  OOP INTERVIEW REFERENCE
 *  Topics: equals/hashCode · inheritance · polymorphism ·
 *          abstract vs interface · records · sealed classes ·
 *          immutability · access modifiers
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class OopReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. equals() & hashCode() CONTRACT ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  THE CONTRACT (must ALL hold):
     *  1. Reflexive:   x.equals(x)              → true
     *  2. Symmetric:   x.equals(y) == y.equals(x)
     *  3. Transitive:  x.equals(y) && y.equals(z) → x.equals(z)
     *  4. Consistent:  multiple calls return same result (no state change)
     *  5. Null-safe:   x.equals(null)            → false (never throw NPE)
     *
     *  CRITICAL RULE: if you override equals(), you MUST override hashCode().
     *  Objects that are equals() MUST have the same hashCode().
     *  Objects with the same hashCode() do NOT need to be equals() (hash collision).
     *
     *  WHY IT MATTERS FOR COLLECTIONS:
     *  HashMap/HashSet use hashCode() to find the bucket, then equals() to find the key.
     *  If you break the contract → object stored but never found in HashMap.
     */
    static class Point {
        final int x, y;

        Point(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;                    // 1. same reference — fast path
            if (!(o instanceof Point p)) return false;     // 2. null check + type check via pattern matching
            return x == p.x && y == p.y;                  // 3. field comparison
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);   // ALWAYS based on SAME fields as equals()
        }
    }

    // ⚠️ ANTI-PATTERN: overriding equals without hashCode
    static class BrokenPoint {
        int x, y;
        BrokenPoint(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BrokenPoint p)) return false;
            return x == p.x && y == p.y;
        }
        // ❌ No hashCode override → same-value objects have different hashCodes
        // → HashMap.get() will NOT find the key even if equals() says they match!
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. ABSTRACT CLASS vs INTERFACE — comparison table
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Feature                    │ Abstract Class      │ Interface (Java 8+)
     *  ───────────────────────────┼─────────────────────┼──────────────────────
     *  Instantiation              │ ❌ No               │ ❌ No
     *  Multiple inheritance       │ ❌ No (single)      │ ✅ Yes (many)
     *  Constructor                │ ✅ Yes              │ ❌ No
     *  Instance fields            │ ✅ Yes              │ ❌ No (only static final)
     *  Access modifiers on methods│ ✅ Any              │ public/private only
     *  Abstract methods           │ ✅ Yes              │ ✅ Yes (implicitly abstract)
     *  Concrete methods           │ ✅ Yes              │ ✅ default methods only
     *  Static methods             │ ✅ Yes              │ ✅ Yes (Java 8+)
     *  State (mutable fields)     │ ✅ Yes              │ ❌ No
     *
     *  WHEN TO USE:
     *  Abstract class → shared state + partial implementation ("is-a" + template)
     *  Interface      → contract / capability ("can-do"), multiple implementations
     */
    abstract static class Animal {
        private final String name;   // state — allowed in abstract class
        Animal(String name) { this.name = name; }
        abstract String sound();
        String describe() { return name + " says " + sound(); }  // template method
    }

    interface Flyable {
        double maxAltitudeMeters();
        default String altitudeReport() {   // default method — Java 8+
            return "Can fly up to " + maxAltitudeMeters() + "m";
        }
    }

    static class Eagle extends Animal implements Flyable {
        Eagle() { super("Eagle"); }
        @Override public String sound() { return "screech"; }
        @Override public double maxAltitudeMeters() { return 3000; }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. IMMUTABILITY — how to enforce it correctly
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Rules for a truly immutable class:
     *  1. Declare class final (prevent subclassing)
     *  2. All fields private final
     *  3. No setters
     *  4. Deep-copy mutable objects in constructor (defensive copy)
     *  5. Return copies of mutable fields from getters (not the original reference)
     */
    static final class ImmutableRange {
        private final int start;
        private final int end;
        private final int[] values;  // ⚠️ array is mutable — needs defensive copy

        ImmutableRange(int start, int end, int[] values) {
            this.start  = start;
            this.end    = end;
            this.values = values.clone();   // defensive copy — not the caller's array
        }

        public int getStart()   { return start; }
        public int getEnd()     { return end; }
        public int[] getValues(){ return values.clone(); }  // return copy, not original
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. RECORDS (Java 16+) vs CLASSES — comparison
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Record auto-generates: constructor · getters · equals() · hashCode() · toString()
     *
     *  Feature             │ Record              │ Regular Class
     *  ────────────────────┼─────────────────────┼──────────────────────
     *  Immutable by default│ ✅ Yes (final fields)│ ❌ No
     *  Boilerplate         │ ✅ None              │ ❌ Verbose
     *  Inheritance         │ ❌ Cannot extend     │ ✅ Yes
     *  Mutable state       │ ❌ No                │ ✅ Yes
     *  Implement interface │ ✅ Yes               │ ✅ Yes
     *  Compact constructor │ ✅ Yes               │ N/A
     *
     *  USE RECORDS FOR: DTOs, value objects, data carriers, method return groupings
     *  AVOID RECORDS FOR: entities with mutable state, classes needing inheritance
     */
    record Coordinate(double lat, double lon) {
        // Compact constructor — validation runs before fields are set
        Coordinate {
            if (lat < -90 || lat > 90)   throw new IllegalArgumentException("Invalid lat: " + lat);
            if (lon < -180 || lon > 180) throw new IllegalArgumentException("Invalid lon: " + lon);
        }
        // Can add custom methods
        double distanceTo(Coordinate other) {
            return Math.sqrt(Math.pow(lat - other.lat, 2) + Math.pow(lon - other.lon, 2));
        }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 5. SEALED CLASSES (Java 17+) — exhaustiveness
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Sealed class/interface restricts which classes can extend/implement it.
     *  Permitted subclasses: final (no further extension) OR sealed OR non-sealed.
     *  BENEFIT: compiler-enforced exhaustiveness in switch expressions.
     */
    sealed interface Shape permits Circle, Rectangle, Triangle {}
    record Circle(double radius) implements Shape {}
    record Rectangle(double w, double h) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    static double area(Shape shape) {
        return switch (shape) {
            case Circle c      -> Math.PI * c.radius() * c.radius();
            case Rectangle r   -> r.w() * r.h();
            case Triangle t    -> 0.5 * t.base() * t.height();
            // No default needed — compiler knows all subtypes via sealed
        };
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 6. POLYMORPHISM GOTCHAS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static class Base {
        String name() { return "Base"; }           // instance method — overridable
        static String staticName() { return "Base static"; }  // static — NOT overridable
    }

    static class Child extends Base {
        @Override
        String name() { return "Child"; }          // overrides — runtime dispatch
        static String staticName() { return "Child static"; }  // HIDES, not overrides
    }

    static void polymorphismGotcha() {
        Base obj = new Child();

        System.out.println(obj.name());             // "Child" — runtime type wins (override)
        System.out.println(Base.staticName());      // "Base static" — compile-time type wins (hiding)

        // ⚠️ Covariant return type — allowed since Java 5
        // Subclass can return a more specific type than the parent declared
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Overriding equals() without hashCode()    → breaks HashMap/HashSet
     *  ❌ Calling overridable methods in constructor → child's override runs before child init
     *  ❌ Mutable fields in equals()/hashCode()     → object "disappears" in maps after mutation
     *  ❌ Using instanceof without pattern matching → verbose and error-prone (pre-Java 16)
     *  ❌ Deep inheritance hierarchies (>2-3 levels)→ fragile base class problem
     *  ❌ Exposing mutable internals from getters   → breaks encapsulation
     */

    public static void main(String[] args) {
        polymorphismGotcha();

        var p1 = new Point(1, 2);
        var p2 = new Point(1, 2);
        System.out.println(p1.equals(p2));    // true
        System.out.println(p1.hashCode() == p2.hashCode()); // true

        var coord = new Coordinate(48.8566, 2.3522); // Paris
        System.out.println(coord);  // auto-generated toString

        Shape s = new Circle(5);
        System.out.println("Circle area: " + area(s));
    }
}
