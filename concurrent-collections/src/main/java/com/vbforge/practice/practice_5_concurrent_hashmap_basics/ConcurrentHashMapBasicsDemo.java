package com.vbforge.practice.practice_5_concurrent_hashmap_basics;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap basics: thread-safe, high-throughput hash table (since Java 1.5).
 * Many threads can read and update it without external synchronization.
 *
 * Key features:
 *   - thread-safe individual operations, no synchronized blocks needed
 *   - Java 7: lock striping (segments); Java 8+: CAS + synchronized on single bins
 *     (either way there is no single global lock)
 *   - NO null keys and NO null values (null would be ambiguous with "absent")
 *   - scales much better than Hashtable / Collections.synchronizedMap
 */
public class ConcurrentHashMapBasicsDemo {

    public static void main(String[] args) {

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        map.put("Java", 10);
        map.put("Python", 20);
        map.put("C++", 30);

        System.out.println("Java score: " + map.get("Java"));

        // null key and null value are rejected with NullPointerException
        try {
            map.put(null, 1);
        } catch (NullPointerException e) {
            System.out.println("NPE: ConcurrentHashMap does not allow null keys");
        }
        try {
            map.put("Kotlin", null);
        } catch (NullPointerException e) {
            System.out.println("NPE: ConcurrentHashMap does not allow null values");
        }
    }
}
