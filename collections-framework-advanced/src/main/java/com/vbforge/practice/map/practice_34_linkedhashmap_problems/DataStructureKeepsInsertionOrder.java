package com.vbforge.practice.map.practice_34_linkedhashmap_problems;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Problem:
 * Implement a map-like data structure where you can:
 * Insert key-value pairs
 * Update values
 * Iterate in the order they were inserted
 * Why LinkedHashMap?
 * It naturally preserves insertion order without any extra logic.
 * Much simpler than manually tracking an order with List + HashMap.
 * */
public class DataStructureKeepsInsertionOrder {
    public static void main(String[] args) {

        Map<String, Integer> map = new LinkedHashMap<>();

        map.put("Apple", 10);
        map.put("Banana", 20);
        map.put("Orange", 30);
        map.put("Mango", 40);

        System.out.println(map);

    }
}
