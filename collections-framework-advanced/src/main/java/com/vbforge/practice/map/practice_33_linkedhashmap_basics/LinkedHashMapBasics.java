package com.vbforge.practice.map.practice_33_linkedhashmap_basics;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapBasics {
    public static void main(String[] args) {

        /* LinkedHashMap is a subclass of HashMap.
        It maintains a **doubly-linked list** to preserve the **insertion order** of elements.

        Core Properties:
        - Unique keys (no duplicates)
        - Maintains insertion order (unlike HashMap)
        - Allows one null key and multiple null values
        - Insertion and access time is O(1) on average
        - Slightly slower than HashMap due to ordering overhead
        */

        // LinkedHashMap – Preserves insertion order
        // Best for: Ordered map with predictable iteration

        Map<String, Integer> linkedMap = new LinkedHashMap<>();

        // Add entries
        linkedMap.put("apple", 3);
        linkedMap.put("banana", 5);
        linkedMap.put("cherry", 7);
        linkedMap.put("banana", 10); // update existing key, value overridden

        // Access
        System.out.println("Value for key 'apple': " + linkedMap.get("apple")); // 3

        // Remove
        linkedMap.remove("banana");

        // Check presence
        System.out.println("Contains key 'apple'? " + linkedMap.containsKey("apple"));   // true
        System.out.println("Contains value 7? " + linkedMap.containsValue(7));           // true

        // Iterate - preserves insertion order
        System.out.println("\n--- Iterating LinkedHashMap ---");
        for (Map.Entry<String, Integer> entry : linkedMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + " : " + value);
        }

        // Optional: Demonstrate null key/value support
        linkedMap.put(null, 100);
        linkedMap.put("date", null);
        System.out.println("\n--- After adding null key and null value ---");
        linkedMap.forEach((k, v) -> System.out.println(k + " : " + v));
    }
}
