package com.vbforge.practice.map.practice_34_linkedhashmap_problems;

import com.vbforge.practice.map.practice_34_linkedhashmap_problems.DataStructureKeepsInsertionOrder;

import java.util.*;

/**
 * Contra-demo: what you'd have to write BY HAND to preserve insertion order
 * if LinkedHashMap did not exist. Compare with
 * {@link DataStructureKeepsInsertionOrder} where the whole class collapses to
 * a single {@code new LinkedHashMap<>()}.
 */
public class MyOrderedMap<K, V> {

    private final Map<K, V> map = new HashMap<>();
    private final List<K> order = new LinkedList<>();

    public static void main(String[] args) {

        MyOrderedMap<String, Integer> orderedMap = new MyOrderedMap<>();

        orderedMap.put("apple", 1);
        orderedMap.put("banana", 2);
        orderedMap.put("cherry", 3);

        System.out.println("Get 'banana': " + orderedMap.get("banana"));

        orderedMap.put("banana", 22); // update value, but not order

        System.out.println("\nContents in insertion order:");
        orderedMap.printInOrder();

        orderedMap.remove("apple");

        System.out.println("\nAfter removing 'apple':");
        orderedMap.printInOrder();

    }

    // Put method (insert or update)
    public void put(K key, V value) {
        if (!map.containsKey(key)) {
            order.add(key); // preserve order only on first insert
        }
        map.put(key, value);
    }

    // Get method
    public V get(K key) {
        return map.get(key);
    }

    // Remove method
    public void remove(K key) {
        if (map.containsKey(key)) {
            map.remove(key);
            order.remove(key);
        }
    }

    // Check if key exists
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    // Print in insertion order
    public void printInOrder() {
        for (K key : order) {
            System.out.println(key + " : " + map.get(key));
        }
    }

    // Size
    public int size() {
        return map.size();
    }

    // Optional: Clear
    public void clear() {
        map.clear();
        order.clear();
    }

}
