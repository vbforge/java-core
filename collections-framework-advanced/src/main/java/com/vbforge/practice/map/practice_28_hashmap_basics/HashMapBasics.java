package com.vbforge.practice.map.practice_28_hashmap_basics;

import java.util.HashMap;
import java.util.Map;

public class HashMapBasics {
    public static void main(String[] args) {

        /*A Map is a data structure that stores key-value pairs.
        In Java, Map<K, V> is an interface in the java.util package.
        Core Properties:
        Unique keys (no duplicates)
        Values can be duplicated
        Keys can be null (depending on implementation)
        to put another value by the existing key, stands latest(overridden) version value by this key
        */
        //HashMap – Most Common Map
        //Best for: Fast lookup, insert, and delete operations (average O(1))

        Map<String, Integer> map = new HashMap<>();

        // Add entries
        map.put("apple", 3);
        map.put("banana", 5);

        // Access
        System.out.println(map.get("apple")); // 3

        // Remove
        map.remove("banana");

        // Check
        map.containsKey("apple"); // true
        map.containsValue(3);     // true

        //Iteration:
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + " : " + value);
        }

    }
}
