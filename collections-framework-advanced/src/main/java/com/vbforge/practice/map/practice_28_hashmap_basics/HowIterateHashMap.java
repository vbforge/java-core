package com.vbforge.practice.map.practice_28_hashmap_basics;

import java.util.HashMap;
import java.util.Map;

public class HowIterateHashMap {
    public static void main(String[] args) {

        // Create and populate HashMap
        Map<String, Integer> map = new HashMap<>();
        map.put("Apple", 10);
        map.put("Banana", 5);
        map.put("Orange", 8);

        // 1. Iterate using entrySet in a for-each loop
        System.out.println("1. Using entrySet (for-each):");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        // 2. Iterate using keySet and get()
        System.out.println("\n2. Using keySet (for-each):");
        for (String key : map.keySet()) {
            System.out.println("Key: " + key + ", Value: " + map.get(key));
        }

        // 3. Iterate using values only
        System.out.println("\n3. Using values():");
        for (Integer value : map.values()) {
            System.out.println("Value: " + value);
        }

        // 4. Iterate using Iterator over entrySet
        System.out.println("\n4. Using Iterator:");
        var iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        // 5. Iterate using forEach + lambda (Java 8+)
        System.out.println("\n5. Using forEach + lambda:");
        map.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

    }
}
