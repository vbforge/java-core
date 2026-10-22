package com.vbforge.collections_framework_advanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Demonstrates {@link TreeSet}: a {@code NavigableSet} that keeps its elements UNIQUE and SORTED
 * at all times (a red-black tree under the hood).
 *
 * <p>Why it matters: {@code HashSet} is fast but unordered; sorting a list after every insert is
 * wasteful. A {@code TreeSet} stays sorted by construction and answers "what is the closest value
 * to X?" questions ({@code floor}, {@code ceiling}, ranges) that a hash set cannot.
 *
 * <p>Complexity: {@code add}, {@code remove}, {@code contains}, {@code floor}, {@code ceiling},
 * {@code first}, {@code last} are all O(log n). Iteration is O(n) in sorted order.
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code TreeSetDemoTest}
 * can verify every claim.
 */
public class TreeSetDemo {

    // ── 1. Sorted and unique ──────────────────────────────────────────────────
    /**
     * Adding elements in any order, with duplicates, yields ascending order without duplicates.
     */
    public static List<Integer> sortedUniqueDemo(List<Integer> items) {
        return new ArrayList<>(new TreeSet<>(items));
    }


    // ── 2. Navigation: floor / ceiling / lower / higher ───────────────────────
    /**
     * Four neighbour queries around a value {@code x}:
     * <pre>
     *   lower(x)    greatest element  &lt;  x
     *   floor(x)    greatest element  &lt;= x
     *   ceiling(x)  smallest element  &gt;= x
     *   higher(x)   smallest element  &gt;  x
     * </pre>
     * Each returns {@code null} when no such element exists (the map below keeps those entries).
     */
    public static Map<String, Integer> navigationDemo(TreeSet<Integer> set, int x) {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("lower", set.lower(x));
        result.put("floor", set.floor(x));
        result.put("ceiling", set.ceiling(x));
        result.put("higher", set.higher(x));
        return result;
    }


    // ── 3. Range views ────────────────────────────────────────────────────────
    /**
     * {@code subSet(from, fromInclusive, to, toInclusive)} selects a range. Here: from inclusive,
     * to exclusive — the usual half-open interval. {@code headSet(to)} and {@code tailSet(from)}
     * are the open-ended variants.
     */
    public static List<Integer> subSetDemo(TreeSet<Integer> set, int fromInclusive, int toExclusive) {
        return new ArrayList<>(set.subSet(fromInclusive, true, toExclusive, false));
    }

    /**
     * <b>Gotcha:</b> range methods return LIVE VIEWS, not copies. Changes to the original show up in the
     * view (and vice versa). Starts with [1, 3, 5, 7]; the view contains everything below 6.
     */
    public static List<Integer> viewIsLiveDemo() {
        TreeSet<Integer> set = new TreeSet<>(Arrays.asList(1, 3, 5, 7));
        SortedSet<Integer> below6 = set.headSet(6);   // view: [1, 3, 5]
        set.add(4);
        set.remove(1);
        return new ArrayList<>(below6);               // view now reflects both changes
    }

    /** A view only accepts elements inside its range — otherwise {@link IllegalArgumentException}. */
    public static boolean addOutsideViewRangeDemo() {
        SortedSet<Integer> view = new TreeSet<>(Arrays.asList(1, 2, 3)).headSet(3);
        return view.add(10);
    }


    // ── 4. Descending order and polling the ends ──────────────────────────────
    /** {@code descendingSet()} is a reverse-ordered VIEW — no copying, no re-sorting. */
    public static List<Integer> descendingDemo(List<Integer> items) {
        return new ArrayList<>(new TreeSet<>(items).descendingSet());
    }

    /**
     * {@code pollFirst()}/{@code pollLast()} REMOVE and return the smallest/largest element
     * (or {@code null} if the set is empty). The set is modified.
     */
    public static List<Integer> pollEndsDemo(TreeSet<Integer> set) {
        List<Integer> polled = new ArrayList<>();
        polled.add(set.pollFirst());
        polled.add(set.pollLast());
        return polled;
    }


    // ── 5. The comparator — not equals() — decides what is a duplicate ────────
    /**
     * <b>Gotcha:</b> a {@code TreeSet} never calls {@code equals()}. Two elements are "the same" when the
     * ordering says {@code compare(a, b) == 0}. With a case-insensitive comparator {@code "Apple"} and
     * {@code "apple"} are duplicates, and the FIRST spelling added wins.
     */
    public static List<String> caseInsensitiveSetDemo(List<String> words) {
        TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(words);
        return new ArrayList<>(set);
    }

    /** The same rule applies to {@code contains}: it asks the comparator, not {@code equals}. */
    public static boolean caseInsensitiveContainsDemo(List<String> words, String probe) {
        TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        set.addAll(words);
        return set.contains(probe);
    }


    // ── 6. Null and non-comparable elements ───────────────────────────────────
    /** With natural ordering {@code null} cannot be compared — {@link NullPointerException}. */
    public static boolean addNullDemo() {
        return new TreeSet<String>().add(null);
    }

    /** Elements without a Comparator must be {@link Comparable}: otherwise {@link ClassCastException}. */
    public static boolean addNonComparableDemo() {
        return new TreeSet<Object>().add(new Object());
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        TreeSet<Integer> prices = new TreeSet<>(Arrays.asList(10, 20, 30, 40));

        System.out.println("=== 1. sorted and unique ===");
        System.out.println(sortedUniqueDemo(Arrays.asList(5, 1, 5, 3, 1)));

        System.out.println("---");
        System.out.println("=== 2. navigation around 25 ===");
        System.out.println(navigationDemo(prices, 25));
        System.out.println("around 30: " + navigationDemo(prices, 30));
        System.out.println("around 5:  " + navigationDemo(prices, 5));

        System.out.println("---");
        System.out.println("=== 3. range views ===");
        System.out.println("[20, 40): " + subSetDemo(prices, 20, 40));
        System.out.println("live view: " + viewIsLiveDemo());
        try {
            addOutsideViewRangeDemo();
        } catch (IllegalArgumentException e) {
            System.out.println("outside range -> IllegalArgumentException: " + e.getMessage());
        }

        System.out.println("---");
        System.out.println("=== 4. descending / poll ===");
        System.out.println(descendingDemo(Arrays.asList(3, 1, 2)));
        System.out.println(pollEndsDemo(new TreeSet<>(prices)));

        System.out.println("---");
        System.out.println("=== 5. comparator decides equality ===");
        System.out.println(caseInsensitiveSetDemo(Arrays.asList("Apple", "apple", "BANANA", "banana", "cherry")));
        System.out.println("contains \"APPLE\": " + caseInsensitiveContainsDemo(Arrays.asList("apple"), "APPLE"));

        System.out.println("---");
        System.out.println("=== 6. null / non-comparable ===");
        try {
            addNullDemo();
        } catch (NullPointerException e) {
            System.out.println("null -> NullPointerException");
        }
        try {
            addNonComparableDemo();
        } catch (ClassCastException e) {
            System.out.println("Object -> ClassCastException");
        }
    }
}
