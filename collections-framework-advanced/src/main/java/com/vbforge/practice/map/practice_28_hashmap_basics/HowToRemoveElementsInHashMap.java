package com.vbforge.practice.map.practice_28_hashmap_basics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HowToRemoveElementsInHashMap {
    public static void main(String[] args) {

        // Sample HashMap
        Map<String, Integer> map = new HashMap<>();
        map.put("Apple", 10);
        map.put("Banana", 5);
        map.put("Orange", 8);
        map.put("Grapes", 3);

        // 1. Remove by key
        System.out.println("1. Remove by key:");
        map.remove("Banana");
        System.out.println(map);

        // 2. Remove by key only if value matches
        System.out.println("\n2. Remove by key and value condition:");
        boolean removed = map.remove("Orange", 8); // returns true
        System.out.println("Removed? " + removed);
        System.out.println(map);

        // 3. Remove using Iterator safely (e.g., remove all entries with value < 5)
        System.out.println("\n3. Remove using Iterator (value < 5):");
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (entry.getValue() < 5) {
                iterator.remove(); // SAFE removal
            }
        }
        System.out.println(map);

        // 4. Remove using `removeIf` (Java 8+)
        System.out.println("\n4. Remove using removeIf (key starts with 'A'):");
        map.entrySet().removeIf(entry -> entry.getKey().startsWith("A"));
        System.out.println(map);

        // Reset map for next example
        map.put("Banana", 5);
        map.put("Apricot", 7);

        // 5. Conditional removal using filter logic
        System.out.println("\n5. Conditional removal (value is even):");
        map.entrySet().removeIf(entry -> entry.getValue() % 2 == 0);
        System.out.println(map);

    }
}
