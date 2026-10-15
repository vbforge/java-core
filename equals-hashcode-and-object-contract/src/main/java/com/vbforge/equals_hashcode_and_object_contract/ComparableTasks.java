package com.vbforge.equals_hashcode_and_object_contract;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * ComparableTasks — implement every method below to make {@code ComparableTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ComparableDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ComparableTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The classes {@link Version} and {@link Person} are already written except for the stubs. Do not
 * change method signatures or add external libraries — only the Java standard library is needed.
 */
public class ComparableTasks {

    // ── Task 1: Version ────────────────────────────────────────────────────────

    /** A semantic version {@code major.minor.patch}; {@code equals} compares all three numbers. */
    public static final class Version implements Comparable<Version> {
        private final int major;
        private final int minor;
        private final int patch;

        public Version(int major, int minor, int patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        /**
         * Orders versions by {@code major}, then {@code minor}, then {@code patch} (numerically).
         * Returns a negative number, zero or a positive number; only the SIGN matters.
         *
         * <p>Examples:
         * <pre>
         *   new Version(1, 2, 3).compareTo(new Version(1, 2, 4))   → negative
         *   new Version(2, 0, 0).compareTo(new Version(1, 9, 9))   → positive
         *   new Version(1, 2, 3).compareTo(new Version(1, 2, 3))   → 0
         *   new Version(1, 10, 0).compareTo(new Version(1, 9, 0))  → positive   (10 > 9, not "10" &lt; "9")
         *   new Version(Integer.MIN_VALUE, 0, 0).compareTo(new Version(1, 0, 0))   → negative (no overflow!)
         *   new Version(1, 0, 0).compareTo(null)                   → throws NullPointerException
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>{@link Integer#compare(int, int)} — never {@code a - b}.</li>
         *   <li>Move to the next field only when the previous one compared equal.</li>
         *   <li>{@code null} needs no special code: touching {@code other.major} already throws.</li>
         * </ul>
         *
         * @param other the version to compare with (never {@code null})
         * @return a negative, zero or positive number
         */
        @Override
        public int compareTo(Version other) {
            throw new UnsupportedOperationException("TODO: implement Version.compareTo");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Version)) {
                return false;
            }
            Version v = (Version) o;
            return major == v.major && minor == v.minor && patch == v.patch;
        }

        @Override
        public int hashCode() {
            return Objects.hash(major, minor, patch);
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + patch;
        }
    }

    // ── Given: Person ──────────────────────────────────────────────────────────

    /** A person with a name (possibly {@code null}) and an age. */
    public static final class Person {
        private final String name;
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String name() {
            return name;
        }

        public int age() {
            return age;
        }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a comparator that orders people by age ascending, and people of the same age by name
     * ascending (natural {@code String} order). Names are never {@code null} here.
     *
     * <p>Examples:
     * <pre>
     *   [Cid(30), Ann(30), Bob(25)] sorted   → [Bob(25), Ann(30), Cid(30)]
     *   [Bob(25), Bob(20)] sorted            → [Bob(20), Bob(25)]
     *   compare(Ann(30), Ann(30))            → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Comparator#comparingInt(java.util.function.ToIntFunction)} followed by
     *       {@link Comparator#thenComparing(java.util.function.Function)}.</li>
     * </ul>
     *
     * @return the comparator
     */
    public Comparator<Person> byAgeThenName() {
        throw new UnsupportedOperationException("TODO: implement byAgeThenName");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a comparator that orders people by name ascending, with people whose name is
     * {@code null} LAST. People with equal names (or both {@code null}) compare as equal.
     *
     * <p>Examples:
     * <pre>
     *   [Cid, null, Ann] sorted             → [Ann, Cid, null]
     *   compare(Person(null), Person(null)) → 0
     *   compare(Person("Ann"), Person(null))→ negative   (a name comes before null)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Comparator#nullsLast(Comparator)} wraps a comparator for a nullable key.</li>
     *   <li>{@link Comparator#comparing(java.util.function.Function, Comparator)} takes the key
     *       extractor and the key comparator.</li>
     * </ul>
     *
     * @return the comparator
     */
    public Comparator<Person> byNameNullsLast() {
        throw new UnsupportedOperationException("TODO: implement byNameNullsLast");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Compares two ints and returns exactly {@code -1}, {@code 0} or {@code 1}, without overflow.
     *
     * <p>Examples:
     * <pre>
     *   safeCompare(1, 2)                         → -1
     *   safeCompare(2, 2)                         → 0
     *   safeCompare(3, 2)                         → 1
     *   safeCompare(Integer.MAX_VALUE, -1)        → 1     ({@code a - b} would be negative!)
     *   safeCompare(Integer.MIN_VALUE, 1)         → -1    ({@code a - b} would be positive!)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Both {@code Integer.compare} and {@code Integer.signum} exist — one of them is enough.</li>
     *   <li>The JDK only promises the SIGN of {@code Integer.compare}, so normalise the result.</li>
     * </ul>
     *
     * @param a the first number
     * @param b the second number
     * @return {@code -1}, {@code 0} or {@code 1}
     */
    public int safeCompare(int a, int b) {
        throw new UnsupportedOperationException("TODO: implement safeCompare");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code compareTo} and {@code equals} AGREE for this pair, i.e. if
     * {@code (a.compareTo(b) == 0) == a.equals(b)}.
     *
     * <p>Examples:
     * <pre>
     *   isConsistentWithEquals(1, 1)                                  → true    (0 and equal)
     *   isConsistentWithEquals(1, 2)                                  → true    (not 0, not equal)
     *   isConsistentWithEquals("a", "a")                              → true
     *   isConsistentWithEquals(new BigDecimal("1.0"), new BigDecimal("1.00"))  → false  (compareTo 0, equals false)
     *   isConsistentWithEquals(new Version(1,2,3), new Version(1,2,3))         → true   (once Task 1 is done)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Both sides of {@code ==} are booleans.</li>
     * </ul>
     *
     * @param a the first value (never {@code null})
     * @param b the second value (never {@code null})
     * @param <T> a type with a natural ordering
     * @return {@code true} if the two methods agree for this pair
     */
    public <T extends Comparable<T>> boolean isConsistentWithEquals(T a, T b) {
        throw new UnsupportedOperationException("TODO: implement isConsistentWithEquals");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the elements of {@code items} in ascending natural order WITHOUT duplicates, where
     * "duplicate" means {@code compareTo == 0} (not {@code equals}). The input is not modified.
     *
     * <p>Examples:
     * <pre>
     *   distinctSorted(["b", "a", "b"])                                  → ["a", "b"]
     *   distinctSorted([3, 1, 2, 1])                                     → [1, 2, 3]
     *   distinctSorted([])                                               → []
     *   distinctSorted([new BigDecimal("1.0"), new BigDecimal("1.00"), new BigDecimal("2")])
     *                                                                    → 2 elements (1.0 and 1.00 are one)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One of the JDK's sorted collections removes duplicates by the comparison result.</li>
     *   <li>Which of the two equal BigDecimals survives is up to the collection — the tests only
     *       count the elements.</li>
     * </ul>
     *
     * @param items the values (never {@code null})
     * @param <T>   a type with a natural ordering
     * @return a new list, sorted and without compareTo-duplicates
     */
    public <T extends Comparable<T>> List<T> distinctSorted(Collection<T> items) {
        throw new UnsupportedOperationException("TODO: implement distinctSorted");
    }
}
