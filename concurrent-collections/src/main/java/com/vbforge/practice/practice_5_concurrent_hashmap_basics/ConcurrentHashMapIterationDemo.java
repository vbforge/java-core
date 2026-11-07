package com.vbforge.practice.practice_5_concurrent_hashmap_basics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap iteration: iterators are weakly consistent.
 * They never throw ConcurrentModificationException, even if the map is changed
 * while iterating (they may or may not reflect such changes).
 */
public class ConcurrentHashMapIterationDemo {

    public static void main(String[] args) {

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        for (String key : map.keySet()) {
            System.out.println(key + " → " + map.get(key));
        }

        // Removing while iterating: no ConcurrentModificationException
        List<String> visited = new ArrayList<>();
        for (String key : map.keySet()) {
            visited.add(key);
            map.remove(key);
        }
        Collections.sort(visited);
        System.out.println("Visited while removing: " + visited + ", map size now: " + map.size());
    }
}
