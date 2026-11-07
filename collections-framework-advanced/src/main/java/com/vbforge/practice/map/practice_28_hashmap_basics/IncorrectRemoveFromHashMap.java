package com.vbforge.practice.map.practice_28_hashmap_basics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IncorrectRemoveFromHashMap {
    public static void main(String[] args) {

        Map<String, Integer> map = new HashMap<>();
        map.put("Apple", 10);
        map.put("Banana", 5);
        map.put("Orange", 8);
        map.put("Grapes", 3);

        System.out.println("Initial Map: " + map);

        System.out.println("\n❌ Incorrect: Removing in for-each loop (will throw exception)");
        try {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() < 6) {
                    map.remove(entry.getKey()); // ❌ modifying map during iteration
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        System.out.println("Map after failed attempt: " + map);

        //Here is the solution for this issue:
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue() < 6) {
                iterator.remove(); // ✅ Safe
            }
        }

    }
}
