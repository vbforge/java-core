package com.vbforge.equals_hashcode_and_object_contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * EqualsContractDemo — annotated reference for {@code Object.equals(Object)}.
 *
 * <p>Read top-to-bottom. Each section covers one facet of equality with
 * concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>The five rules — reflexive, symmetric, transitive, consistent, {@code null}-safe</li>
 *   <li>The default is identity — {@code ==} vs {@code equals}</li>
 *   <li>The overload trap — {@code equals(Point)} is NOT an override</li>
 *   <li>Symmetry — why {@code equals} must not interoperate with other types</li>
 *   <li>Inheritance and transitivity — {@code instanceof} vs {@code getClass()}</li>
 *   <li>Doubles and records — {@code NaN}, {@code -0.0} and generated {@code equals}</li>
 *   <li>Null fields and arrays — {@code Objects.equals}, {@code Arrays.equals}</li>
 * </ol>
 */
public class EqualsContractDemo {

    public static void main(String[] args) {
        fiveRulesDemo();
        System.out.println("---");
        identityDemo();
        System.out.println("---");
        overloadTrapDemo();
        System.out.println("---");
        symmetryDemo();
        System.out.println("---");
        transitivityDemo();
        System.out.println("---");
        doublesAndRecordsDemo();
        System.out.println("---");
        nullsAndArraysDemo();
    }

    // ── 1. The five rules ─────────────────────────────────────────────────────

    /** A correct value class: final, {@code instanceof} check, every significant field compared. */
    static final class Point {
        private final int x;
        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;                                  // fast path, also guarantees reflexivity
            }
            if (!(o instanceof Point)) {
                return false;                                 // also covers null: null instanceof X is false
            }
            Point other = (Point) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Point(" + x + ", " + y + ")";
        }
    }

    /**
     * {@code equals} must be an equivalence relation on NON-null objects:
     * <ul>
     *   <li><b>reflexive</b> — {@code x.equals(x)} is true</li>
     *   <li><b>symmetric</b> — {@code x.equals(y)} iff {@code y.equals(x)}</li>
     *   <li><b>transitive</b> — {@code x.equals(y)} and {@code y.equals(z)} imply {@code x.equals(z)}</li>
     *   <li><b>consistent</b> — repeated calls give the same answer while nothing relevant changes</li>
     *   <li><b>null-safe</b> — {@code x.equals(null)} is false, never an exception</li>
     * </ul>
     *
     * <p>WHY this matters: {@code HashMap}, {@code HashSet}, {@code List.contains},
     * {@code List.remove} and {@code assertEquals} all assume these rules. Break one and the
     * collections behave in ways that depend on which argument happens to be on the left.
     *
     * <p>Common gotcha: the order of the checks matters — test {@code instanceof} BEFORE casting
     * and never dereference the argument before the type check.
     */
    static void fiveRulesDemo() {
        System.out.println("=== The five rules ===");
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);
        Point c = new Point(1, 2);
        System.out.println("reflexive   : " + a.equals(a));
        System.out.println("symmetric   : " + (a.equals(b) == b.equals(a)));
        System.out.println("transitive  : " + (a.equals(b) && b.equals(c) && a.equals(c)));
        System.out.println("null        : " + a.equals(null));
    }

    // ── 2. The default is identity ────────────────────────────────────────────

    /** No {@code equals} override: inherits {@code Object.equals}, which is just {@code ==}. */
    static class Plain {
        final int id;

        Plain(int id) {
            this.id = id;
        }
    }

    /** Returns {@code {a == b, a.equals(b)}} for two Strings with the same text. */
    static boolean[] stringComparison() {
        String a = "hi";
        String b = new String("hi");                           // a different object on purpose
        return new boolean[] {a == b, a.equals(b)};
    }

    /**
     * {@code Object.equals} compares REFERENCES: two objects are equal only if they are the same
     * object. Two {@code Plain(1)} instances are therefore not equal, even though they hold the
     * same data. Classes that represent VALUES ({@code String}, {@code Integer}, {@code LocalDate},
     * your {@code Point}) override it; classes that represent ENTITIES with identity (a thread, a
     * connection) usually should not.
     *
     * <p>WHY this matters: {@code ==} on objects asks "same object?", {@code equals} asks "same
     * value?". Using {@code ==} on Strings works by accident for literals and fails for any String
     * built at runtime.
     *
     * <p>Common gotcha: comparing Strings (or boxed {@code Integer}s above 127) with {@code ==}.
     */
    static void identityDemo() {
        System.out.println("=== The default is identity ===");
        Plain p1 = new Plain(1);
        Plain p2 = new Plain(1);
        System.out.println("Plain(1).equals(Plain(1)) : " + p1.equals(p2));          // false
        System.out.println("same reference            : " + p1.equals(p1));          // true
        boolean[] strings = stringComparison();
        System.out.println("\"hi\" == new String(\"hi\") : " + strings[0]);             // false
        System.out.println("\"hi\".equals(new String(\"hi\")) : " + strings[1]);       // true
    }

    // ── 3. The overload trap ──────────────────────────────────────────────────

    static class OverloadedPoint {
        final int x;
        final int y;

        OverloadedPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /** An OVERLOAD: a different method than {@code equals(Object)}. Adding {@code @Override} would not compile. */
        public boolean equals(OverloadedPoint other) {
            return other != null && x == other.x && y == other.y;
        }
    }

    /** Returns {@code {direct call, call via Object reference, List.contains}}. */
    static boolean[] overloadTrap() {
        OverloadedPoint a = new OverloadedPoint(1, 2);
        OverloadedPoint b = new OverloadedPoint(1, 2);
        Object asObject = b;
        List<OverloadedPoint> list = new ArrayList<>();
        list.add(a);
        return new boolean[] {a.equals(b), a.equals(asObject), list.contains(b)};
    }

    /** True if {@code type} itself declares a real {@code equals(Object)} override. */
    static boolean overridesEquals(Class<?> type) {
        try {
            type.getDeclaredMethod("equals", Object.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Overloads are chosen at COMPILE time by the static type of the argument. The call
     * {@code a.equals(b)} with {@code b} declared as {@code OverloadedPoint} picks the new
     * overload and looks fine; but the collections, and any call through an {@code Object}
     * reference, call {@code equals(Object)} — the inherited identity version. The same class
     * therefore answers {@code true} in one place and {@code false} in another.
     *
     * <p>WHY this matters: the bug is silent. {@code list.contains(b)} just returns false.
     *
     * <p>Fix: always write {@code @Override public boolean equals(Object o)} — the annotation turns
     * the typo into a compile error.
     */
    static void overloadTrapDemo() {
        System.out.println("=== The overload trap ===");
        boolean[] result = overloadTrap();
        System.out.println("a.equals(b)                 : " + result[0]);   // true
        System.out.println("a.equals((Object) b)        : " + result[1]);   // false
        System.out.println("list.contains(b)            : " + result[2]);   // false
        System.out.println("Point overrides equals?     : " + overridesEquals(Point.class));
        System.out.println("OverloadedPoint overrides?  : " + overridesEquals(OverloadedPoint.class));
    }

    // ── 4. Symmetry ───────────────────────────────────────────────────────────

    /** BROKEN on purpose: tries to be equal to plain Strings as well. */
    static final class CaseInsensitiveText {
        private final String text;

        CaseInsensitiveText(String text) {
            this.text = Objects.requireNonNull(text);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CaseInsensitiveText) {
                return text.equalsIgnoreCase(((CaseInsensitiveText) o).text);
            }
            if (o instanceof String) {
                return text.equalsIgnoreCase((String) o);      // one-way street: String knows nothing about us
            }
            return false;
        }

        @Override
        public int hashCode() {
            return text.toLowerCase(Locale.ROOT).hashCode();
        }
    }

    /**
     * {@code new CaseInsensitiveText("Java").equals("java")} is true, but
     * {@code "java".equals(new CaseInsensitiveText("Java"))} is false — {@code String.equals} only
     * accepts Strings. The relation is not symmetric.
     *
     * <p>WHY this matters: {@code list.contains(text)} and the reverse check give different
     * answers depending on which side the library puts the argument. Nothing you can write
     * inside your own class fixes {@code String.equals}.
     *
     * <p>Rule: {@code equals} should only be equal to objects of its OWN type (or a deliberately
     * shared supertype). Never try to be "equal to a String".
     */
    static void symmetryDemo() {
        System.out.println("=== Symmetry ===");
        CaseInsensitiveText text = new CaseInsensitiveText("Java");
        System.out.println("text.equals(\"java\")  : " + text.equals("java"));      // true
        System.out.println("\"java\".equals(text)  : " + "java".equals(text));      // false
    }

    // ── 5. Inheritance and transitivity ───────────────────────────────────────

    static class Pt {
        final int x;
        final int y;

        Pt(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pt)) {
                return false;
            }
            Pt other = (Pt) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /** Symmetric on purpose (it asks the other side when types differ) — but not transitive. */
    static class ColorPt extends Pt {
        final String color;

        ColorPt(int x, int y, String color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pt)) {
                return false;
            }
            if (!(o instanceof ColorPt)) {
                return o.equals(this);                         // compare as a plain point, ignoring colour
            }
            return super.equals(o) && color.equals(((ColorPt) o).color);
        }
    }

    /** The {@code getClass()} approach: transitive, but a subclass instance is never equal to its parent. */
    static class StrictPt {
        final int x;
        final int y;

        StrictPt(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StrictPt other = (StrictPt) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /**
     * Adding a value-significant field in a subclass is the classic equals trap. With
     * {@code instanceof} you can keep symmetry or colour-awareness but not transitivity:
     * {@code red == plain}, {@code plain == blue}, yet {@code red != blue}.
     *
     * <p>The {@code getClass()} variant restores the rules, but breaks the Liskov substitution
     * principle: {@code new StrictPt(1,2)} is not equal to an anonymous subclass
     * {@code new StrictPt(1,2) { }} that adds no state at all — so a {@code HashSet<StrictPt>}
     * behaves differently once a framework hands you a proxy subclass.
     *
     * <p>WHY this matters (Effective Java, Item 10): there is NO way to extend an instantiable
     * class and add a value component while preserving the contract. Prefer {@code final} value
     * classes, or composition ("a ColorPoint HAS a Point").
     */
    static void transitivityDemo() {
        System.out.println("=== Inheritance and transitivity ===");
        ColorPt red = new ColorPt(1, 2, "red");
        Pt plain = new Pt(1, 2);
        ColorPt blue = new ColorPt(1, 2, "blue");
        System.out.println("red.equals(plain)  : " + red.equals(plain));    // true
        System.out.println("plain.equals(blue) : " + plain.equals(blue));   // true
        System.out.println("red.equals(blue)   : " + red.equals(blue));     // false  ← transitivity broken
        System.out.println("StrictPt vs subclass: " + new StrictPt(1, 2).equals(new StrictPt(1, 2) { }));   // false
    }

    // ── 6. Doubles and records ────────────────────────────────────────────────

    record Reading(double value) { }

    record Tag(String name) { }

    /** Returns {@code {0.0 == -0.0, Double 0.0 equals -0.0, NaN == NaN, Double NaN equals NaN}}. */
    static boolean[] doubleEquality() {
        return new boolean[] {
                0.0 == -0.0,
                Double.valueOf(0.0).equals(-0.0),
                Double.NaN == Double.NaN,
                Double.valueOf(Double.NaN).equals(Double.NaN)
        };
    }

    /**
     * Primitive {@code ==} and {@code Double.equals} disagree: {@code 0.0 == -0.0} is true but
     * {@code Double.equals} says they differ; {@code NaN == NaN} is false but {@code Double.equals}
     * says equal. To satisfy REFLEXIVITY (a {@code NaN} field must equal itself) a hand-written
     * {@code equals} must compare {@code double} fields with {@code Double.compare(a, b) == 0}.
     *
     * <p>A record's generated {@code equals} already does the right thing: it compares each
     * component the way the matching wrapper's {@code equals} would — so
     * {@code Reading(NaN)} equals itself and {@code Reading(0.0)} does not equal {@code Reading(-0.0)};
     * reference components are compared like {@code Objects.equals}, so {@code null} is fine.
     *
     * <p>WHY this matters: with {@code ==} on a double field, a {@code NaN} object is not even
     * equal to itself, and a {@code HashSet} can never find it again.
     */
    static void doublesAndRecordsDemo() {
        System.out.println("=== Doubles and records ===");
        boolean[] d = doubleEquality();
        System.out.println("0.0 == -0.0            : " + d[0]);   // true
        System.out.println("Double 0.0 equals -0.0 : " + d[1]);   // false
        System.out.println("NaN == NaN             : " + d[2]);   // false
        System.out.println("Double NaN equals NaN  : " + d[3]);   // true
        System.out.println("Reading(NaN) self-equal: " + new Reading(Double.NaN).equals(new Reading(Double.NaN)));
        System.out.println("Tag(null) equals Tag(null): " + new Tag(null).equals(new Tag(null)));
    }

    // ── 7. Null fields and arrays ─────────────────────────────────────────────

    /** Returns {@code {a.equals(b), Arrays.equals(a, b)}} for two int arrays. */
    static boolean[] arrayEquality(int[] a, int[] b) {
        return new boolean[] {a.equals(b), Arrays.equals(a, b)};
    }

    /**
     * Fields that may be {@code null} are compared with {@code Objects.equals(a, b)} — it handles
     * {@code null} on either side without a {@code NullPointerException}. Array fields need
     * {@code Arrays.equals} (one level) or {@code Arrays.deepEquals} (nested): calling
     * {@code a.equals(b)} on an array is plain identity.
     *
     * <p>WHY this matters: {@code name.equals(other.name)} throws when {@code name} is null — a
     * constructor that allows null then makes {@code equals} blow up inside a HashMap.
     *
     * <p>Common gotcha: put the cheap, most-discriminating fields first ({@code id} before a long
     * description) — {@code equals} is called on every bucket collision.
     */
    static void nullsAndArraysDemo() {
        System.out.println("=== Null fields and arrays ===");
        System.out.println("Objects.equals(null, null) : " + Objects.equals(null, null));   // true
        System.out.println("Objects.equals(\"a\", null)  : " + Objects.equals("a", null));  // false
        boolean[] arrays = arrayEquality(new int[] {1, 2}, new int[] {1, 2});
        System.out.println("array.equals               : " + arrays[0]);                    // false
        System.out.println("Arrays.equals              : " + arrays[1]);                    // true
        System.out.println("deepEquals nested          : "
                + Arrays.deepEquals(new int[][] {{1}, {2}}, new int[][] {{1}, {2}}));        // true
    }
}
