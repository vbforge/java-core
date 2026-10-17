package com.vbforge.generics_fundamentals;

import java.util.Objects;

/**
 * Demonstrates bounded type parameters: {@code extends} bounds on classes and methods,
 * {@code Comparable} bounds, multiple bounds and custom types that satisfy a bound.
 */
public class BoundedTypesDemo {

    // ── 1. Bounded type parameter on a class ───────────────────────────────────
    /*
     * WHAT:  <T extends Number> restricts T to Number and its subclasses
     *        (Integer, Double, BigDecimal, …). "extends" is used for classes AND interfaces here.
     * WHY:   An unbounded T is just an Object — you can only call equals/hashCode/toString.
     *        A bound tells the compiler which methods ARE available, so
     *        value.doubleValue() compiles, and NumberBox<String> is rejected.
     * ERASURE: an unbounded T is erased to Object; a bounded T is erased to its bound
     *        (Number here). The test suite checks this with reflection.
     * GOTCHA: The bound limits what callers may pass AND lets the class use the bound's API —
     *        the two are the same feature seen from both sides.
     */
    public static class NumberBox<T extends Number> {
        private final T value;

        public NumberBox(T value) {
            this.value = Objects.requireNonNull(value);
        }

        public T get() {
            return value;
        }

        /** Only compiles because T is known to be a Number. */
        public double asDouble() {
            return value.doubleValue();
        }

        public boolean isPositive() {
            return value.doubleValue() > 0;
        }
    }

    /** The runtime type of the {@code value} field — Number, not Object, because of the bound. */
    public static Class<?> erasedValueType() {
        try {
            return NumberBox.class.getDeclaredField("value").getType();
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    static void boundedClassDemo() {
        NumberBox<Integer> ints = new NumberBox<>(42);
        NumberBox<Double> doubles = new NumberBox<>(-1.5);
        System.out.println("42 as double: " + ints.asDouble() + ", positive? " + ints.isPositive());
        System.out.println("-1.5 positive? " + doubles.isPositive());
        // NumberBox<String> bad = new NumberBox<>("x");   // ← compile error: String is not a Number
        System.out.println("erased field type: " + erasedValueType().getSimpleName());
    }


    // ── 2. Bounded generic methods and Comparable ──────────────────────────────
    /*
     * WHAT:  <T extends Comparable<T>> means "T can be compared with other Ts".
     * WHY:   It is how you write ONE max/min/sort/binary-search for every ordered type
     *        (String, Integer, LocalDate, your own classes) while keeping the result typed.
     *        Without the bound, a.compareTo(b) would not compile.
     * COMPLEXITY of largest(): O(n) time, O(1) extra space.
     * GOTCHA: Comparable<T> refers to T itself ("recursive bound"). Do NOT write the raw
     *         Comparable — it compiles with warnings and loses the type check on compareTo.
     */
    public static <T extends Comparable<T>> T maxOf(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    public static <T extends Comparable<T>> T largest(T[] items) {
        if (items.length == 0) {
            throw new IllegalArgumentException("array must not be empty");
        }
        T best = items[0];
        for (int i = 1; i < items.length; i++) {
            if (items[i].compareTo(best) > 0) {
                best = items[i];
            }
        }
        return best;
    }

    static void comparableBoundDemo() {
        System.out.println("maxOf(3, 7): " + maxOf(3, 7));
        System.out.println("maxOf(\"pear\", \"apple\"): " + maxOf("pear", "apple"));
        System.out.println("largest: " + largest(new String[]{"b", "z", "a"}));
        // maxOf(1, "a");   // ← compile error: Integer and String share no common T extends Comparable<T>
    }


    // ── 3. Multiple bounds ─────────────────────────────────────────────────────
    /*
     * WHAT:  <T extends A & B & C> — T must satisfy ALL bounds.
     * RULES: At most ONE class, and it must come FIRST; every other bound is an interface.
     *        (<T extends Comparable<T> & Number> does not compile.)
     * WHY:   Lets one method use the API of several types at once —
     *        here Number.doubleValue() AND Comparable.compareTo().
     * ERASURE: T is erased to the FIRST bound (Number).
     * GOTCHA: Only types that satisfy every bound qualify. Integer, Double and BigDecimal do;
     *         AtomicInteger is a Number but not Comparable, so it is rejected.
     */
    public static <T extends Number & Comparable<T>> boolean isBetween(T value, T low, T high) {
        return value.compareTo(low) >= 0 && value.compareTo(high) <= 0;
    }

    public static <T extends Number & Comparable<T>> double largerAsDouble(T a, T b) {
        T larger = a.compareTo(b) >= 0 ? a : b;       // from Comparable
        return larger.doubleValue();                  // from Number
    }

    static void multipleBoundsDemo() {
        System.out.println("5 between 1 and 10? " + isBetween(5, 1, 10));
        System.out.println("2.5 between 3.0 and 4.0? " + isBetween(2.5, 3.0, 4.0));
        System.out.println("larger as double: " + largerAsDouble(3, 8));
    }


    // ── 4. Your own types satisfy bounds too ───────────────────────────────────
    /*
     * WHAT:  Any class that implements Comparable<Itself> automatically works with every
     *        <T extends Comparable<T>> method above.
     * WHY:   This is the "open for extension" payoff of bounds: maxOf/largest were written
     *        long before Version existed and still work.
     * NEXT:  A subclass of a Comparable class (Timestamp extends Date) fails
     *        T extends Comparable<T>; the fix is T extends Comparable<? super T>,
     *        explained in WildcardsDemo.
     */
    public static class Version implements Comparable<Version> {
        private final int major;
        private final int minor;

        public Version(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }

        @Override
        public int compareTo(Version other) {
            return major != other.major ? Integer.compare(major, other.major)
                                        : Integer.compare(minor, other.minor);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Version)) return false;
            Version other = (Version) o;
            return major == other.major && minor == other.minor;
        }

        @Override
        public int hashCode() {
            return Objects.hash(major, minor);
        }

        @Override
        public String toString() {
            return major + "." + minor;
        }
    }

    static void customComparableDemo() {
        Version newest = largest(new Version[]{new Version(1, 9), new Version(2, 0), new Version(1, 10)});
        System.out.println("newest version: " + newest);
        System.out.println("maxOf(1.2, 1.10): " + maxOf(new Version(1, 2), new Version(1, 10)));
    }


    // ── Main — runs every section ──────────────────────────────────────────────
    public static void main(String[] args) {
        boundedClassDemo();
        System.out.println("---");
        comparableBoundDemo();
        System.out.println("---");
        multipleBoundsDemo();
        System.out.println("---");
        customComparableDemo();
    }
}
