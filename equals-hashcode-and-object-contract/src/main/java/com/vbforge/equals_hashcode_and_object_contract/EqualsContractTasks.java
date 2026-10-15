package com.vbforge.equals_hashcode_and_object_contract;

import java.util.Locale;
import java.util.Objects;

/**
 * EqualsContractTasks — implement every method below to make {@code EqualsContractTasksTest}
 * go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code EqualsContractDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=EqualsContractTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The classes below are already written except for the stubs. Their {@code hashCode} is given
 * and already consistent with the {@code equals} you must write (hashing is the next module
 * pair). Do not change method signatures or add external libraries — only the Java standard
 * library is needed.
 */
public class EqualsContractTasks {

    // ── Task 1: Point ──────────────────────────────────────────────────────────

    /** An immutable 2D point. */
    public static final class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Two points are equal when they have the same {@code x} and the same {@code y}.
         *
         * <p>Examples:
         * <pre>
         *   new Point(1, 2).equals(new Point(1, 2))   → true
         *   new Point(1, 2).equals(new Point(2, 1))   → false
         *   new Point(1, 2).equals(new Point(1, 3))   → false
         *   new Point(1, 2).equals(null)              → false    (never an exception)
         *   new Point(1, 2).equals("1,2")             → false    (other type)
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Check the type with {@code instanceof} BEFORE you cast.</li>
         *   <li>{@code null instanceof Point} is {@code false} — one check covers both cases.</li>
         * </ul>
         *
         * @param o the object to compare with
         * @return {@code true} if {@code o} is a {@code Point} with the same coordinates
         */
        @Override
        public boolean equals(Object o) {
            throw new UnsupportedOperationException("TODO: implement Point.equals");
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    // ── Task 2: Name ───────────────────────────────────────────────────────────

    /** A person's name; two names that differ only in letter case are the same name. */
    public static final class Name {
        private final String first;
        private final String last;

        public Name(String first, String last) {
            this.first = Objects.requireNonNull(first);
            this.last = Objects.requireNonNull(last);
        }

        /**
         * Two names are equal when first AND last names match, ignoring letter case.
         *
         * <p>Examples:
         * <pre>
         *   new Name("Ann", "Lee").equals(new Name("ANN", "lee"))   → true
         *   new Name("Ann", "Lee").equals(new Name("Ann", "Li"))    → false
         *   new Name("Ann", "Lee").equals(new Name("Lee", "Ann"))   → false   (order matters)
         *   new Name("Ann", "Lee").equals(null)                     → false
         *   new Name("Ann", "Lee").equals("Ann Lee")                → false   (never equal to a String)
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>{@link String#equalsIgnoreCase(String)} does the case-insensitive comparison.</li>
         *   <li>Do not make {@code Name} equal to a {@code String} — that would break symmetry.</li>
         * </ul>
         *
         * @param o the object to compare with
         * @return {@code true} if {@code o} is a {@code Name} with the same first and last name
         */
        @Override
        public boolean equals(Object o) {
            throw new UnsupportedOperationException("TODO: implement Name.equals");
        }

        @Override
        public int hashCode() {
            return Objects.hash(first.toLowerCase(Locale.ROOT), last.toLowerCase(Locale.ROOT));
        }
    }

    // ── Task 3: Measurement ────────────────────────────────────────────────────

    /** A measured value with a unit. */
    public static final class Measurement {
        private final double value;
        private final String unit;

        public Measurement(double value, String unit) {
            this.value = value;
            this.unit = unit;                  // may be null
        }

        /**
         * Two measurements are equal when their values are equal AND their units are equal. The
         * unit may be {@code null}. Values are compared the way {@link Double#equals} does, so the
         * rules of {@code equals} (especially reflexivity) hold for every {@code double}.
         *
         * <p>Examples:
         * <pre>
         *   new Measurement(2.5, "m").equals(new Measurement(2.5, "m"))                  → true
         *   new Measurement(2.5, "m").equals(new Measurement(2.5, "km"))                 → false
         *   new Measurement(Double.NaN, "m").equals(new Measurement(Double.NaN, "m"))    → true
         *   new Measurement(0.0, "m").equals(new Measurement(-0.0, "m"))                 → false
         *   new Measurement(1.0, null).equals(new Measurement(1.0, null))                → true
         *   new Measurement(1.0, null).equals(new Measurement(1.0, "m"))                 → false
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>{@code ==} on doubles is NOT reflexive for {@code NaN} — see {@link Double#compare}.</li>
         *   <li>{@link Objects#equals(Object, Object)} is {@code null}-safe.</li>
         * </ul>
         *
         * @param o the object to compare with
         * @return {@code true} if {@code o} is a {@code Measurement} with equal value and unit
         */
        @Override
        public boolean equals(Object o) {
            throw new UnsupportedOperationException("TODO: implement Measurement.equals");
        }

        @Override
        public int hashCode() {
            return Objects.hash(Double.valueOf(value), unit);
        }
    }

    // ── Task 4: UnorderedPair ──────────────────────────────────────────────────

    /** A pair where the order does not matter: {@code (a, b)} is the same pair as {@code (b, a)}. */
    public static final class UnorderedPair {
        private final String a;
        private final String b;

        public UnorderedPair(String a, String b) {
            this.a = Objects.requireNonNull(a);
            this.b = Objects.requireNonNull(b);
        }

        /**
         * Two pairs are equal when they contain the same two elements in either order.
         *
         * <p>Examples:
         * <pre>
         *   new UnorderedPair("x", "y").equals(new UnorderedPair("x", "y"))   → true
         *   new UnorderedPair("x", "y").equals(new UnorderedPair("y", "x"))   → true
         *   new UnorderedPair("x", "y").equals(new UnorderedPair("x", "z"))   → false
         *   new UnorderedPair("x", "x").equals(new UnorderedPair("x", "y"))   → false
         *   new UnorderedPair("x", "y").equals(null)                          → false
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Two cases make a match: same order, or swapped.</li>
         *   <li>Be careful with {@code ("x","x")} vs {@code ("x","y")} — comparing only one
         *       side of a swap would wrongly accept it.</li>
         * </ul>
         *
         * @param o the object to compare with
         * @return {@code true} if {@code o} is an {@code UnorderedPair} with the same elements
         */
        @Override
        public boolean equals(Object o) {
            throw new UnsupportedOperationException("TODO: implement UnorderedPair.equals");
        }

        @Override
        public int hashCode() {
            return a.hashCode() + b.hashCode();             // order-independent on purpose
        }
    }

    // ── Given: classes used by Tasks 5 and 6 ───────────────────────────────────

    /** BROKEN on purpose (see the demo, section 4): also claims to equal plain Strings. */
    public static final class CaseInsensitiveText {
        private final String text;

        public CaseInsensitiveText(String text) {
            this.text = Objects.requireNonNull(text);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CaseInsensitiveText) {
                return text.equalsIgnoreCase(((CaseInsensitiveText) o).text);
            }
            if (o instanceof String) {
                return text.equalsIgnoreCase((String) o);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return text.toLowerCase(Locale.ROOT).hashCode();
        }
    }

    /** Overrides {@code equals(Object)} properly. */
    public static class GoodEquals {
        @Override
        public boolean equals(Object o) {
            return o instanceof GoodEquals;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    /** Declares {@code equals(OverloadedEquals)} — an overload, NOT an override. */
    public static class OverloadedEquals {
        public boolean equals(OverloadedEquals other) {
            return other != null;
        }
    }

    /** Declares no {@code equals} at all. */
    public static class NoEquals { }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code a} and {@code b} VIOLATE symmetry, i.e. if
     * {@code a.equals(b)} and {@code b.equals(a)} give different answers.
     *
     * <p>Examples:
     * <pre>
     *   violatesSymmetry(new CaseInsensitiveText("Java"), "java")                  → true    (true vs false)
     *   violatesSymmetry("java", new CaseInsensitiveText("Java"))                  → true
     *   violatesSymmetry(new CaseInsensitiveText("Java"), new CaseInsensitiveText("JAVA")) → false
     *   violatesSymmetry("a", "a")                                                 → false
     *   violatesSymmetry("a", "b")                                                 → false   (false vs false)
     *   violatesSymmetry(1, 1L)                                                    → false   (both false: Integer ≠ Long)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Call {@code equals} in BOTH directions and compare the two booleans.</li>
     *   <li>Both arguments are non-{@code null}.</li>
     * </ul>
     *
     * @param a the first object (never {@code null})
     * @param b the second object (never {@code null})
     * @return {@code true} if {@code a.equals(b) != b.equals(a)}
     */
    public boolean violatesSymmetry(Object a, Object b) {
        throw new UnsupportedOperationException("TODO: implement violatesSymmetry");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code type} itself DECLARES a real {@code equals(Object)} override.
     * An overload such as {@code equals(MyType)} does not count, and an inherited
     * {@code equals} does not count either.
     *
     * <p>Examples:
     * <pre>
     *   overridesEquals(GoodEquals.class)         → true
     *   overridesEquals(OverloadedEquals.class)   → false   (equals(OverloadedEquals) is an overload)
     *   overridesEquals(NoEquals.class)           → false   (inherits Object.equals)
     *   overridesEquals(String.class)             → true
     *   overridesEquals(Object.class)             → true    (declares it itself)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#getDeclaredMethod(String, Class[])} looks up ONE method by exact
     *       parameter types and throws {@link NoSuchMethodException} if there is none.</li>
     * </ul>
     *
     * @param type the class to inspect (never {@code null})
     * @return {@code true} if {@code type} declares {@code equals(Object)}
     */
    public boolean overridesEquals(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement overridesEquals");
    }
}
