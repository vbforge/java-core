package com.vbforge.equals_hashcode_and_object_contract;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * ComparableDemo — annotated reference for {@code Comparable} and {@code Comparator}.
 *
 * <p>Read top-to-bottom. Each section covers one facet of ordering with
 * concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>The {@code compareTo} contract — a natural ordering</li>
 *   <li>The subtraction trap — {@code a - b} overflows</li>
 *   <li>Consistent with {@code equals} — the {@code BigDecimal} surprise</li>
 *   <li>Comparators — orderings you do not bake into the class</li>
 *   <li>{@code TreeSet}/{@code TreeMap} use {@code compareTo}, not {@code equals}</li>
 *   <li>Records — {@code Comparable} for free-ish</li>
 * </ol>
 */
public class ComparableDemo {

    public static void main(String[] args) {
        contractDemo();
        System.out.println("---");
        subtractionTrapDemo();
        System.out.println("---");
        consistentWithEqualsDemo();
        System.out.println("---");
        comparatorsDemo();
        System.out.println("---");
        treeCollectionsDemo();
        System.out.println("---");
        recordsDemo();
    }

    // ── 1. The compareTo contract ─────────────────────────────────────────────

    static final class Version implements Comparable<Version> {
        private final int major;
        private final int minor;
        private final int patch;

        Version(int major, int minor, int patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        @Override
        public int compareTo(Version other) {               // NullPointerException for null: by convention
            int result = Integer.compare(major, other.major);
            if (result != 0) {
                return result;
            }
            result = Integer.compare(minor, other.minor);
            if (result != 0) {
                return result;
            }
            return Integer.compare(patch, other.patch);
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

    /**
     * {@code compareTo} returns a negative number, zero or a positive number when {@code this} is
     * less than, equal to or greater than the argument. The contract:
     * <ol>
     *   <li><b>antisymmetric</b> — {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))}</li>
     *   <li><b>transitive</b> — {@code x > y} and {@code y > z} imply {@code x > z}</li>
     *   <li><b>consistent</b> — {@code x.compareTo(y) == 0} implies {@code x} and {@code y} sort the
     *       same way against every {@code z}</li>
     *   <li><b>recommended</b> — {@code (x.compareTo(y) == 0) == x.equals(y)} (section 3)</li>
     *   <li>{@code x.compareTo(null)} throws {@code NullPointerException} (unlike {@code equals})</li>
     * </ol>
     *
     * <p>WHY this matters: {@code Collections.sort}, {@code TreeSet}, {@code PriorityQueue},
     * {@code Arrays.binarySearch} and {@code stream.sorted()} all rely on it. A broken
     * {@code compareTo} gives a wrong order or, with TimSort, an
     * {@code IllegalArgumentException: Comparison method violates its general contract!} on
     * some inputs only.
     *
     * <p>Rule: compare the most significant field first, and finish with a field that makes
     * the ordering total. Note that {@code "1.10.0"} sorts AFTER {@code "1.9.0"} as a Version but
     * BEFORE it as a String.
     */
    static void contractDemo() {
        System.out.println("=== The compareTo contract ===");
        List<Version> versions = new ArrayList<>();
        versions.add(new Version(1, 10, 0));
        versions.add(new Version(1, 9, 0));
        versions.add(new Version(1, 2, 3));
        Collections.sort(versions);
        System.out.println("sorted : " + versions);                                        // [1.2.3, 1.9.0, 1.10.0]
        System.out.println("max    : " + Collections.max(versions));
        System.out.println("1.2.3 vs 1.2.4 : " + Integer.signum(new Version(1, 2, 3).compareTo(new Version(1, 2, 4))));
    }

    // ── 2. The subtraction trap ───────────────────────────────────────────────

    /** BROKEN: {@code a - b} overflows when the operands are far apart. */
    static int brokenCompare(int a, int b) {
        return a - b;
    }

    static int safeCompare(int a, int b) {
        return Integer.compare(a, b);
    }

    /**
     * {@code a - b} looks like a compact comparison, but {@code int} arithmetic wraps around:
     * {@code Integer.MAX_VALUE - (-1)} is {@code Integer.MIN_VALUE}, a NEGATIVE number, so the broken
     * version claims {@code MAX_VALUE < -1}. Such a comparator works on every test with small numbers
     * and corrupts the order only for huge values — a bug that appears once in production.
     *
     * <p>Use {@code Integer.compare(a, b)} (also {@code Long.compare}, {@code Double.compare},
     * {@code Boolean.compare}, {@code Character.compare}). Only the SIGN of the result matters; do
     * not assume it is {@code -1/0/1}.
     */
    static void subtractionTrapDemo() {
        System.out.println("=== The subtraction trap ===");
        System.out.println("brokenCompare(MAX, -1) : " + brokenCompare(Integer.MAX_VALUE, -1));   // negative!
        System.out.println("safeCompare(MAX, -1)   : " + safeCompare(Integer.MAX_VALUE, -1));     // 1
    }

    // ── 3. Consistent with equals ─────────────────────────────────────────────

    /** Returns the sizes of a HashSet and a TreeSet holding {@code 1.0} and {@code 1.00}. */
    static int[] bigDecimalSetSizes() {
        BigDecimal one = new BigDecimal("1.0");
        BigDecimal oneAgain = new BigDecimal("1.00");
        Set<BigDecimal> hashSet = new HashSet<>();
        hashSet.add(one);
        hashSet.add(oneAgain);
        Set<BigDecimal> treeSet = new TreeSet<>();
        treeSet.add(one);
        treeSet.add(oneAgain);
        return new int[] {hashSet.size(), treeSet.size()};
    }

    /**
     * "Consistent with equals" means {@code x.compareTo(y) == 0} exactly when {@code x.equals(y)}.
     * It is only RECOMMENDED, and {@code BigDecimal} is the famous exception: {@code 1.0} and
     * {@code 1.00} have different scales, so {@code equals} says false while {@code compareTo} says 0.
     * A {@code HashSet} (equals/hashCode) therefore keeps both, a {@code TreeSet} (compareTo) keeps
     * one.
     *
     * <p>WHY this matters: the same data gives different answers depending on the collection. If you
     * break consistency, say so in the Javadoc — and compare with {@code compareTo(...) == 0}, not
     * {@code equals}, when you mean numeric equality.
     */
    static void consistentWithEqualsDemo() {
        System.out.println("=== Consistent with equals ===");
        BigDecimal one = new BigDecimal("1.0");
        BigDecimal oneAgain = new BigDecimal("1.00");
        System.out.println("1.0 equals 1.00       : " + one.equals(oneAgain));          // false
        System.out.println("1.0 compareTo 1.00    : " + one.compareTo(oneAgain));        // 0
        int[] sizes = bigDecimalSetSizes();
        System.out.println("HashSet / TreeSet size: " + sizes[0] + " / " + sizes[1]);   // 2 / 1
    }

    // ── 4. Comparators ────────────────────────────────────────────────────────

    static final class Person {
        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        String name() {
            return name;
        }

        int age() {
            return age;
        }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    static final Comparator<Person> BY_AGE_THEN_NAME =
            Comparator.comparingInt(Person::age).thenComparing(Person::name);

    static final Comparator<Person> BY_NAME_NULLS_FIRST =
            Comparator.comparing(Person::name, Comparator.nullsFirst(Comparator.<String>naturalOrder()));

    static List<Person> sortedBy(List<Person> people, Comparator<Person> comparator) {
        List<Person> copy = new ArrayList<>(people);
        copy.sort(comparator);
        return copy;
    }

    /**
     * A {@code Comparator} defines an ordering OUTSIDE the class — you can have as many as you like,
     * and you can order classes you do not own. Build them from small pieces:
     * {@code Comparator.comparing(keyExtractor)}, {@code comparingInt}, {@code .thenComparing(...)}
     * for tie-breakers, {@code .reversed()}, and {@code nullsFirst}/{@code nullsLast} for nullable keys.
     *
     * <p>{@code List.sort} and {@code Collections.sort} are STABLE: elements that compare equal keep
     * their original relative order — which is why sorting by age and then by name in two passes
     * works.
     *
     * <p>WHY this matters: implement {@code Comparable} for the ONE obvious natural order (versions,
     * dates, amounts) and use comparators for everything else (by age, by name, by newest first).
     *
     * <p>Common gotcha: {@code reversed()} applies to the whole comparator built so far — write
     * {@code comparingInt(Person::age).reversed().thenComparing(Person::name)} to reverse only the age.
     */
    static void comparatorsDemo() {
        System.out.println("=== Comparators ===");
        List<Person> people = new ArrayList<>();
        people.add(new Person("Cid", 30));
        people.add(new Person("Ann", 30));
        people.add(new Person("Bob", 25));
        System.out.println("by age then name : " + sortedBy(people, BY_AGE_THEN_NAME));              // [Bob(25), Ann(30), Cid(30)]
        System.out.println("reversed         : " + sortedBy(people, BY_AGE_THEN_NAME.reversed()));   // [Cid(30), Ann(30), Bob(25)]
    }

    // ── 5. TreeSet / TreeMap ──────────────────────────────────────────────────

    /** Returns the sizes of a HashSet and a case-insensitive TreeSet holding {@code "a"} and {@code "A"}. */
    static int[] caseInsensitiveSetSizes() {
        Set<String> hashSet = new HashSet<>();
        hashSet.add("a");
        hashSet.add("A");
        Set<String> treeSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        treeSet.add("a");
        treeSet.add("A");
        return new int[] {hashSet.size(), treeSet.size()};
    }

    static Integer treeMapLookupIgnoringCase() {
        TreeMap<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.put("apple", 1);
        return map.get("APPLE");
    }

    /**
     * {@code TreeSet} and {@code TreeMap} never call {@code equals} or {@code hashCode}: two elements
     * are "the same" when the comparison returns 0. With {@code String.CASE_INSENSITIVE_ORDER}, "a"
     * and "A" are one element, and {@code map.get("APPLE")} finds the key {@code "apple"}.
     *
     * <p>WHY this matters: the comparator is the definition of identity inside a sorted collection.
     * A comparator that ignores a field silently merges (and drops) elements that differ in it —
     * add a final tie-breaker on a unique field if you need to keep them all.
     *
     * <p>Complexity: {@code TreeSet}/{@code TreeMap} operations are O(log n); {@code HashSet} is O(1)
     * on average but unordered.
     */
    static void treeCollectionsDemo() {
        System.out.println("=== TreeSet / TreeMap ===");
        int[] sizes = caseInsensitiveSetSizes();
        System.out.println("HashSet / TreeSet(case-insensitive) : " + sizes[0] + " / " + sizes[1]);   // 2 / 1
        System.out.println("TreeMap.get(\"APPLE\")                : " + treeMapLookupIgnoringCase()); // 1
    }

    // ── 6. Records ────────────────────────────────────────────────────────────

    record Release(int major, int minor) implements Comparable<Release> {
        static final Comparator<Release> ORDER =
                Comparator.comparingInt(Release::major).thenComparingInt(Release::minor);

        @Override
        public int compareTo(Release other) {
            return ORDER.compare(this, other);
        }
    }

    /**
     * A record generates {@code equals}, {@code hashCode} and {@code toString} from ALL its
     * components, but never {@code compareTo} — you write it, and the cleanest way is to delegate to
     * a {@code static final Comparator} built from the component accessors. If the comparator uses
     * every component, the ordering is automatically consistent with the generated {@code equals}.
     *
     * <p>WHY this matters: a single {@code Comparator} constant is easy to reuse (for sorting, for a
     * {@code TreeSet}) and is checked in one place.
     *
     * <p>Common gotcha: forgetting a component in the comparator makes {@code compareTo == 0} for records
     * that are NOT equal — the {@code BigDecimal} problem again.
     */
    static void recordsDemo() {
        System.out.println("=== Records ===");
        List<Release> releases = new ArrayList<>();
        releases.add(new Release(2, 0));
        releases.add(new Release(1, 5));
        releases.add(new Release(1, 10));
        Collections.sort(releases);
        System.out.println("sorted : " + releases);
        System.out.println("equal and compareTo == 0 : "
                + (new Release(1, 5).equals(new Release(1, 5)) && new Release(1, 5).compareTo(new Release(1, 5)) == 0));
    }
}
