package com.vbforge.oop_encapsulation_patterns;

import java.lang.reflect.Member;
import java.util.List;

/**
 * AccessModifiersTasks — implement every method below to make {@code AccessModifiersTasksTest}
 * go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code AccessModifiersDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=AccessModifiersTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The small classes {@link Levels}, {@link TidyPoint}, {@link LeakyPoint}, {@link Marker}
 * and {@link Temperature} are already written — do NOT modify them (except the one stub in
 * {@link Temperature}). Do not change method signatures or add external libraries — only the
 * Java standard library is needed.
 */
public class AccessModifiersTasks {

    // ── Given: sample classes (do NOT modify) ──────────────────────────────────

    /** One field of each access level. */
    public static class Levels {
        private int secret;
        int shared;
        protected int family;
        public int open;
    }

    /** Well encapsulated: every instance field is private. */
    public static class TidyPoint {
        public static final int ORIGIN = 0;     // a public CONSTANT is fine — it is static
        private final int x;
        private final int y;

        public TidyPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }
    }

    /** Leaks one field: {@code x} is public. */
    public static class LeakyPoint {
        public int x;
        private int y;

        public int y() {
            return y;
        }
    }

    /** No fields and no methods. */
    public static class Marker { }

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the access level of a field, method or constructor as one of the four strings
     * {@code "public"}, {@code "protected"}, {@code "package-private"} or {@code "private"}.
     *
     * <p>Examples:
     * <pre>
     *   accessLevel(Levels.class.getDeclaredField("secret"))     → "private"
     *   accessLevel(Levels.class.getDeclaredField("shared"))     → "package-private"
     *   accessLevel(Levels.class.getDeclaredField("family"))     → "protected"
     *   accessLevel(Levels.class.getDeclaredField("open"))       → "public"
     *   accessLevel(String.class.getMethod("length"))            → "public"
     *   accessLevel(Object.class.getDeclaredMethod("clone"))     → "protected"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Member#getModifiers()} returns a bit-mask; {@link java.lang.reflect.Modifier}
     *       has a test method for each flag.</li>
     *   <li>Package-private has no flag of its own.</li>
     * </ul>
     *
     * @param member the field, method or constructor to inspect (never {@code null})
     * @return the access level as a lower-case string
     */
    public String accessLevel(Member member) {
        throw new UnsupportedOperationException("TODO: implement accessLevel");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the names of the {@code private} fields that {@code type} itself DECLARES (static
     * or not, inherited ones excluded), sorted alphabetically.
     *
     * <p>Examples:
     * <pre>
     *   privateFieldNames(TidyPoint.class)   → ["x", "y"]      (ORIGIN is public)
     *   privateFieldNames(LeakyPoint.class)  → ["y"]
     *   privateFieldNames(Levels.class)      → ["secret"]
     *   privateFieldNames(Marker.class)      → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#getDeclaredFields()} returns the fields declared by the class itself —
     *       in NO guaranteed order, so sort the result.</li>
     *   <li>Skip compiler-generated (synthetic) fields.</li>
     * </ul>
     *
     * @param type the class to inspect (never {@code null})
     * @return sorted names of the private fields declared directly in {@code type}
     */
    public List<String> privateFieldNames(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement privateFieldNames");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if every INSTANCE field declared by {@code type} is {@code private}.
     * Static fields are ignored (a {@code public static final} constant does not break
     * encapsulation). A class with no instance fields counts as encapsulated.
     *
     * <p>Examples:
     * <pre>
     *   isEncapsulated(TidyPoint.class)   → true     (ORIGIN is static → ignored)
     *   isEncapsulated(LeakyPoint.class)  → false    (x is public)
     *   isEncapsulated(Levels.class)      → false    (shared, family, open are not private)
     *   isEncapsulated(Marker.class)      → true     (no fields at all)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Both {@code isPrivate} and {@code isStatic} live in {@link java.lang.reflect.Modifier}.</li>
     *   <li>Skip compiler-generated (synthetic) fields.</li>
     * </ul>
     *
     * @param type the class to inspect (never {@code null})
     * @return {@code true} if no instance field is visible outside {@code type}
     */
    public boolean isEncapsulated(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement isEncapsulated");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /** A temperature that hides its raw value behind the class boundary. */
    public static class Temperature {
        private final double celsius;

        public Temperature(double celsius) {
            this.celsius = celsius;
        }

        public double celsius() {
            return celsius;
        }

        /**
         * Returns {@code true} if THIS temperature is strictly higher than {@code other}.
         *
         * <p>Examples:
         * <pre>
         *   new Temperature(20).isWarmerThan(new Temperature(10))   → true
         *   new Temperature(10).isWarmerThan(new Temperature(20))   → false
         *   new Temperature(15).isWarmerThan(new Temperature(15))   → false   (equal is not warmer)
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Both objects belong to the same class — think about what {@code private}
         *       actually restricts.</li>
         * </ul>
         *
         * @param other the temperature to compare with (never {@code null})
         * @return {@code true} if this one is warmer
         */
        public boolean isWarmerThan(Temperature other) {
            throw new UnsupportedOperationException("TODO: implement isWarmerThan");
        }
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns how many {@code public} methods {@code type} itself DECLARES — the size of its
     * public API. Constructors, fields, inherited methods and compiler-generated (synthetic)
     * methods are not counted.
     *
     * <p>Examples:
     * <pre>
     *   publicApiSize(Temperature.class)   → 2    (celsius, isWarmerThan)
     *   publicApiSize(TidyPoint.class)     → 2    (x, y)
     *   publicApiSize(Marker.class)        → 0
     *   publicApiSize(Runnable.class)      → 1    (interface methods are public)
     *   publicApiSize(Comparable.class)    → 1
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#getDeclaredMethods()} includes private and package-private methods too —
     *       filter them out.</li>
     * </ul>
     *
     * @param type the class or interface to inspect (never {@code null})
     * @return the number of public methods declared directly in {@code type}
     */
    public int publicApiSize(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement publicApiSize");
    }
}
