package com.vbforge.practice.map.practice_7_map_internals;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * HashMap vs TreeMap — internal mechanics and the cost of sorting.
 *
 * HashMap: O(1) average put/get — hash-based bucketing.
 *          Order not guaranteed; keys unsorted.
 *
 * TreeMap: O(log n) put/get — Red-Black tree; keys kept sorted.
 *          Use when sorted key iteration is needed.
 *
 * Also demonstrates pre-sizing a HashMap to avoid rehashing.
 */
public class MapInternalsDemo {

    public static void main(String[] args) {

        // ================== Scene 1: HashMap vs TreeMap key order ==================
        System.out.println("=== HashMap vs TreeMap key order ===");
        String[] keys = {"banana", "apple", "cherry", "date"};
        System.out.println("HashMap keys (unordered): " + buildHashMap(keys).keySet());
        System.out.println("TreeMap keys (sorted):    " + buildTreeMap(keys).keySet());

        // ================== Scene 2: pre-sizing for known expected size ==================
        System.out.println("\n=== Pre-sizing HashMap ===");
        HashMap<String, Integer> presized = buildPresizedHashMap(1000);
        System.out.println("Presized HashMap initial capacity for expectedSize=1000: ~"
                + "no rehash needed below 1000 entries");
        // populate up to expected size (no resize triggered)
        for (int i = 0; i < 1000; i++) {
            presized.put("key-" + i, i);
        }
        System.out.println("size after populating 1000 entries: " + presized.size());
    }

    /**
     * HashMap: O(1) average put/get — hash-based bucketing.
     * TreeMap: O(log n) put/get — Red-Black tree; keys kept sorted.
     */
    public static Map<String, Integer> buildHashMap(String[] keys) {
        Map<String, Integer> map = new HashMap<>(keys.length * 2); // pre-size to avoid rehash
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], i);
        }
        return map;
    }

    public static Map<String, Integer> buildTreeMap(String[] keys) {
        Map<String, Integer> map = new TreeMap<>();
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], i);
        }
        return map;
    }

    /**
     * HashMap default initial capacity = 16, load factor = 0.75.
     * Resize (rehash) doubles the table and redistributes all entries — O(n).
     *
     * When expected size is known, pass capacity = expectedSize / 0.75 + 1
     * (or simply 2× expectedSize as a safe approximation).
     *
     * Same applies to ArrayList: new ArrayList<>(expectedSize) avoids array copies.
     */
    public static HashMap<String, Integer> buildPresizedHashMap(int expectedSize) {
        int capacity = (int) (expectedSize / 0.75) + 1;
        return new HashMap<>(capacity);
    }
}
