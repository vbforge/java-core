package com.vbforge.equals_hashcode_and_object_contract;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

/**
 * HashCodeTasks — implement every method below to make {@code HashCodeTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code HashCodeDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=HashCodeTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The {@code equals} of every class below is already written — your {@code hashCode} must be
 * CONSISTENT with it (equal objects → equal hash codes) and should spread different objects
 * well. Do not change method signatures or add external libraries — only the Java standard
 * library is needed.
 */
public class HashCodeTasks {

    // ── Task 1: Point ──────────────────────────────────────────────────────────

    /** An immutable 2D point; {@code equals} compares {@code x} and {@code y}. */
    public static final class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Point)) {
                return false;
            }
            Point other = (Point) o;
            return x == other.x && y == other.y;
        }

        /**
         * Returns a hash code built from {@code x} and {@code y} that is consistent with
         * {@code equals} and sensitive to the ORDER of the coordinates and to each coordinate.
         *
         * <p>Examples:
         * <pre>
         *   new Point(1, 2).hashCode() == new Point(1, 2).hashCode()   → true
         *   new Point(1, 2).hashCode() == new Point(2, 1).hashCode()   → false   (order matters)
         *   new Point(1, 2).hashCode() == new Point(1, 3).hashCode()   → false
         *   the 100 points of a 10×10 grid          → at least 90 distinct hash codes
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>A plain {@code x + y} is consistent but puts {@code (1,2)} and {@code (2,1)} in
         *       the same bucket — see the 31 recipe in {@code HashCodeDemo}, section 4.</li>
         *   <li>{@link Objects#hash(Object...)} is the shortest correct solution.</li>
         * </ul>
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            throw new UnsupportedOperationException("TODO: implement Point.hashCode");
        }
    }

    // ── Task 2: Name ───────────────────────────────────────────────────────────

    /** A name; {@code equals} ignores letter case in both parts. */
    public static final class Name {
        private final String first;
        private final String last;

        public Name(String first, String last) {
            this.first = Objects.requireNonNull(first);
            this.last = Objects.requireNonNull(last);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Name)) {
                return false;
            }
            Name other = (Name) o;
            return first.equalsIgnoreCase(other.first) && last.equalsIgnoreCase(other.last);
        }

        /**
         * Returns a hash code consistent with the case-INSENSITIVE {@code equals}: names that differ
         * only in letter case must have the same hash code.
         *
         * <p>Examples:
         * <pre>
         *   new Name("Ann", "Lee").hashCode() == new Name("ANN", "lee").hashCode()   → true
         *   new Name("Ann", "Lee").hashCode() == new Name("Lee", "Ann").hashCode()   → false
         *   new Name("Ann", "Lee").hashCode() == new Name("Bob", "Lee").hashCode()   → false
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Normalise the case first, then hash. Use {@link Locale#ROOT} so the result does not
         *       depend on the machine's language (the Turkish dotless i!).</li>
         * </ul>
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            throw new UnsupportedOperationException("TODO: implement Name.hashCode");
        }
    }

    // ── Task 3: Measurement ────────────────────────────────────────────────────

    /** A value with an optional unit; {@code equals} compares values like {@link Double#equals}. */
    public static final class Measurement {
        private final double value;
        private final String unit;

        public Measurement(double value, String unit) {
            this.value = value;
            this.unit = unit;                  // may be null
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Measurement)) {
                return false;
            }
            Measurement other = (Measurement) o;
            return Double.compare(value, other.value) == 0 && Objects.equals(unit, other.unit);
        }

        /**
         * Returns a hash code consistent with {@code equals}. The unit may be {@code null} (no
         * {@code NullPointerException}!), and {@code NaN} must hash the same every time.
         *
         * <p>Examples:
         * <pre>
         *   new Measurement(2.5, "m").hashCode() == new Measurement(2.5, "m").hashCode()   → true
         *   new Measurement(Double.NaN, "m").hashCode() == new Measurement(Double.NaN, "m").hashCode()   → true
         *   new Measurement(1.0, null).hashCode()   → no exception
         *   new Measurement(1.0, "m").hashCode() == new Measurement(2.0, "m").hashCode()   → false
         *   new Measurement(1.0, "m").hashCode() == new Measurement(1.0, "km").hashCode()  → false
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>{@link Double#hashCode(double)} matches {@code Double.compare} equality.</li>
         *   <li>{@link Objects#hashCode(Object)} returns 0 for {@code null}.</li>
         * </ul>
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            throw new UnsupportedOperationException("TODO: implement Measurement.hashCode");
        }
    }

    // ── Task 4: UnorderedPair ──────────────────────────────────────────────────

    /** A pair where order does not matter: {@code (a, b)} equals {@code (b, a)}. */
    public static final class UnorderedPair {
        private final String a;
        private final String b;

        public UnorderedPair(String a, String b) {
            this.a = Objects.requireNonNull(a);
            this.b = Objects.requireNonNull(b);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof UnorderedPair)) {
                return false;
            }
            UnorderedPair other = (UnorderedPair) o;
            return (a.equals(other.a) && b.equals(other.b)) || (a.equals(other.b) && b.equals(other.a));
        }

        /**
         * Returns a hash code that does NOT depend on the order of the two elements.
         *
         * <p>Examples:
         * <pre>
         *   new UnorderedPair("x", "y").hashCode() == new UnorderedPair("y", "x").hashCode()   → true
         *   new UnorderedPair("x", "y").hashCode() == new UnorderedPair("x", "z").hashCode()   → false
         *   new UnorderedPair("x", "x").hashCode() == new UnorderedPair("x", "y").hashCode()   → false
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>The 31 recipe is order-SENSITIVE ({@code 31*h(a)+h(b)} differs from {@code 31*h(b)+h(a)}).
         *       Combine the two element hashes with an operation that does not care about order.</li>
         * </ul>
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            throw new UnsupportedOperationException("TODO: implement UnorderedPair.hashCode");
        }
    }

    // ── Given: classes used by Tasks 5 and 6 ───────────────────────────────────

    /** BROKEN on purpose: overrides {@code equals} but keeps the identity {@code hashCode}. */
    @SuppressWarnings("overrides")   // intentional: this is the bug being demonstrated
    public static final class BrokenHash {
        private final int id;

        public BrokenHash(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BrokenHash && ((BrokenHash) o).id == id;
        }
    }

    /** A key whose id can change after construction. */
    public static final class MutableKey {
        private int id;

        public MutableKey(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof MutableKey && ((MutableKey) o).id == id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Checks the {@code equals}/{@code hashCode} contract for ONE pair of objects: returns
     * {@code false} exactly when the objects are equal but their hash codes differ, and
     * {@code true} in every other case.
     *
     * <p>Examples:
     * <pre>
     *   isConsistentWithEquals("a", "a")                          → true    (equal, same hash)
     *   isConsistentWithEquals("a", "b")                          → true    (not equal: nothing to check)
     *   isConsistentWithEquals(new Name("Ann","Lee"), new Name("ANN","lee"))  → true  (once Task 2 is done)
     *   isConsistentWithEquals(new BrokenHash(1), new BrokenHash(1))          → false (equal, different hashes)
     *   isConsistentWithEquals(new BrokenHash(1), new BrokenHash(2))          → true  (not equal)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>"If A then B" is {@code !A || B}.</li>
     *   <li>Both arguments are non-{@code null}.</li>
     * </ul>
     *
     * @param a the first object (never {@code null})
     * @param b the second object (never {@code null})
     * @return {@code false} if {@code a.equals(b)} but the hash codes differ
     */
    public boolean isConsistentWithEquals(Object a, Object b) {
        throw new UnsupportedOperationException("TODO: implement isConsistentWithEquals");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Reproduces the "vanished key" bug: puts {@code key} into a {@link HashSet}, then CHANGES the
     * key's id to {@code id + 1}, and returns {@code true} if the set can no longer find the key
     * with {@code contains}. The key keeps its changed id afterwards.
     *
     * <p>Examples:
     * <pre>
     *   k = new MutableKey(1); isLostAfterMutation(k)   → true    (and k.id() is now 2)
     *   k = new MutableKey(-5); isLostAfterMutation(k)  → true    (and k.id() is now -4)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Order of the steps: add → mutate → contains. See {@code HashCodeDemo}, section 5.</li>
     *   <li>Use {@link MutableKey#setId(int)}.</li>
     * </ul>
     *
     * @param key the key to add and then mutate (never {@code null})
     * @return {@code true} if {@code contains(key)} is {@code false} after the mutation
     */
    public boolean isLostAfterMutation(MutableKey key) {
        throw new UnsupportedOperationException("TODO: implement isLostAfterMutation");
    }
}
