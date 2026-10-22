package com.vbforge.collections_framework_advanced;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Demonstrates {@link TreeMap}: a {@code NavigableMap} whose KEYS are always sorted.
 *
 * <p>Why it matters: {@code HashMap} gives O(1) lookups but no order and no "nearest key" queries.
 * A {@code TreeMap} keeps keys sorted, so it can answer range questions — "which price tier does a weight of
 * 7 kg fall into?", "what happened between 10:00 and 11:00?" — that a hash map cannot.
 *
 * <p>Complexity: {@code put}, {@code get}, {@code remove}, {@code containsKey}, {@code floorKey},
 * {@code ceilingKey}, {@code firstKey}, {@code lastKey} are O(log n). Iteration is O(n), in key order.
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code TreeMapDemoTest}
 * can verify every claim.
 */
public class TreeMapDemo {

    // ── 1. Keys are sorted ────────────────────────────────────────────────────
    /**
     * Whatever the insertion order, iteration visits the keys in ascending natural order.
     */
    public static List<String> sortedKeysDemo(Map<String, Integer> input) {
        return new ArrayList<>(new TreeMap<>(input).keySet());
    }


    // ── 2. Range lookup: floor / ceiling ──────────────────────────────────────
    /**
     * The classic TreeMap use: map the LOWER BOUND of each range to a result, then ask for the greatest
     * key that is {@code <=} the value. Here: score boundaries to grades. Scores below 0 are invalid.
     */
    public static String gradeDemo(int score) {
        TreeMap<Integer, String> grades = new TreeMap<>();
        grades.put(0, "F");
        grades.put(60, "D");
        grades.put(70, "C");
        grades.put(80, "B");
        grades.put(90, "A");

        Map.Entry<Integer, String> entry = grades.floorEntry(score);
        return entry == null ? "invalid" : entry.getValue();
    }

    /**
     * The four neighbour queries on KEYS: {@code lowerKey} (&lt;), {@code floorKey} (&lt;=),
     * {@code ceilingKey} (&gt;=), {@code higherKey} (&gt;). Missing neighbours are {@code null}.
     */
    public static Map<String, Integer> navigationDemo(TreeMap<Integer, String> map, int key) {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("lowerKey", map.lowerKey(key));
        result.put("floorKey", map.floorKey(key));
        result.put("ceilingKey", map.ceilingKey(key));
        result.put("higherKey", map.higherKey(key));
        return result;
    }


    // ── 3. First / last entries ───────────────────────────────────────────────
    /**
     * {@code firstKey()}/{@code lastKey()} throw {@link NoSuchElementException} on an empty map,
     * so the result is "firstKey..lastKey".
     */
    public static String firstAndLastDemo(TreeMap<String, Integer> map) {
        return map.firstKey() + ".." + map.lastKey();
    }

    /** {@code firstKey()} on an empty map throws {@link NoSuchElementException}. */
    public static String firstKeyOfEmptyDemo() {
        return new TreeMap<String, Integer>().firstKey();
    }

    /** ...whereas {@code firstEntry()} on an empty map returns {@code null}. */
    public static boolean firstEntryOfEmptyIsNullDemo() {
        return new TreeMap<String, Integer>().firstEntry() == null;
    }

    /** {@code pollFirstEntry()} removes the entry with the smallest key and returns it as "key=value". */
    public static String pollFirstEntryDemo(TreeMap<String, Integer> map) {
        Map.Entry<String, Integer> entry = map.pollFirstEntry();
        return entry.getKey() + "=" + entry.getValue();
    }

    /**
     * <b>Gotcha:</b> the entries returned by {@code firstEntry()}, {@code floorEntry()} and friends are
     * immutable SNAPSHOTS. {@code setValue} throws {@link UnsupportedOperationException}; use {@code put} instead.
     */
    public static void snapshotEntryDemo() {
        TreeMap<String, Integer> map = new TreeMap<>();
        map.put("a", 1);
        map.firstEntry().setValue(99);
    }


    // ── 4. Sub-map views ──────────────────────────────────────────────────────
    /** {@code headMap(to, false)}: values of all keys strictly below {@code toKey}, in key order. */
    public static List<String> headMapDemo(TreeMap<Integer, String> map, int toKey) {
        return new ArrayList<>(map.headMap(toKey, false).values());
    }

    /** {@code tailMap(from, true)}: values of all keys from {@code fromKey} upwards, in key order. */
    public static List<String> tailMapDemo(TreeMap<Integer, String> map, int fromKey) {
        return new ArrayList<>(map.tailMap(fromKey, true).values());
    }

    /** {@code subMap(from, true, to, true)}: values of the keys in the closed range. */
    public static List<String> subMapDemo(TreeMap<Integer, String> map, int fromKey, int toKey) {
        return new ArrayList<>(map.subMap(fromKey, true, toKey, true).values());
    }

    /**
     * <b>Gotcha:</b> like TreeSet ranges, sub-maps are LIVE VIEWS of the original map.
     * Starts as {1=a, 3=c, 5=e}; the view contains the keys below 4.
     */
    public static Map<Integer, String> viewIsLiveDemo() {
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(1, "a");
        map.put(3, "c");
        map.put(5, "e");
        Map<Integer, String> below4 = map.headMap(4);   // view: {1=a, 3=c}
        map.put(2, "b");
        map.remove(1);
        return new LinkedHashMap<>(below4);             // view now reflects both changes
    }


    // ── 5. Descending order ───────────────────────────────────────────────────
    /** {@code descendingKeySet()} is a reverse-ordered view of the keys — nothing is copied or re-sorted. */
    public static List<String> descendingKeysDemo(Map<String, Integer> input) {
        return new ArrayList<>(new TreeMap<>(input).descendingKeySet());
    }


    // ── 6. The comparator decides key equality; nulls ─────────────────────────
    /**
     * <b>Gotcha:</b> as with TreeSet, key identity is decided by the comparator, never by {@code equals()}.
     * With {@code CASE_INSENSITIVE_ORDER}, {@code "Key"} and {@code "KEY"} are the SAME key: the second
     * {@code put} replaces the VALUE but keeps the first key spelling.
     */
    public static TreeMap<String, Integer> caseInsensitiveMapDemo() {
        TreeMap<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.put("Key", 1);
        map.put("KEY", 2);
        return map;
    }

    /** With natural ordering a {@code null} key cannot be compared — {@link NullPointerException}. */
    public static Integer putNullKeyDemo() {
        return new TreeMap<String, Integer>().put(null, 1);
    }

    /** {@code null} VALUES are fine; {@code containsKey} tells "mapped to null" from "absent". */
    public static boolean nullValueDemo() {
        TreeMap<String, Integer> map = new TreeMap<>();
        map.put("k", null);
        return map.containsKey("k") && map.get("k") == null;
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        Map<String, Integer> stock = new LinkedHashMap<>();
        stock.put("pear", 3);
        stock.put("apple", 5);
        stock.put("fig", 1);

        System.out.println("=== 1. sorted keys ===");
        System.out.println(sortedKeysDemo(stock));

        System.out.println("---");
        System.out.println("=== 2. range lookup ===");
        for (int score : new int[]{95, 90, 89, 60, 0, -1}) {
            System.out.println(score + " -> " + gradeDemo(score));
        }
        TreeMap<Integer, String> tiers = new TreeMap<>();
        tiers.put(10, "x");
        tiers.put(20, "y");
        tiers.put(30, "z");
        System.out.println(navigationDemo(tiers, 20));

        System.out.println("---");
        System.out.println("=== 3. first / last ===");
        TreeMap<String, Integer> sorted = new TreeMap<>(stock);
        System.out.println(firstAndLastDemo(sorted));
        System.out.println("firstEntry() on empty == null: " + firstEntryOfEmptyIsNullDemo());
        try {
            firstKeyOfEmptyDemo();
        } catch (NoSuchElementException e) {
            System.out.println("firstKey() on empty -> NoSuchElementException");
        }
        System.out.println(pollFirstEntryDemo(sorted) + " | left: " + sorted);
        try {
            snapshotEntryDemo();
        } catch (UnsupportedOperationException e) {
            System.out.println("entry.setValue -> UnsupportedOperationException");
        }

        System.out.println("---");
        System.out.println("=== 4. sub-map views ===");
        TreeMap<Integer, String> timeline = new TreeMap<>();
        timeline.put(9, "standup");
        timeline.put(11, "review");
        timeline.put(14, "lunch");
        timeline.put(17, "deploy");
        System.out.println("head(<14):    " + headMapDemo(timeline, 14));
        System.out.println("tail(>=14):   " + tailMapDemo(timeline, 14));
        System.out.println("sub[11..14]:  " + subMapDemo(timeline, 11, 14));
        System.out.println("live view:    " + viewIsLiveDemo());

        System.out.println("---");
        System.out.println("=== 5. descending ===");
        System.out.println(descendingKeysDemo(stock));

        System.out.println("---");
        System.out.println("=== 6. comparator equality / nulls ===");
        System.out.println(caseInsensitiveMapDemo());
        try {
            putNullKeyDemo();
        } catch (NullPointerException e) {
            System.out.println("null key -> NullPointerException");
        }
        System.out.println("null value allowed: " + nullValueDemo());
    }
}
