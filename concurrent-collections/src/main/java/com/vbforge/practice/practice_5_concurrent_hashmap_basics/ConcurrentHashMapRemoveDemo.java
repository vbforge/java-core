package com.vbforge.practice.practice_5_concurrent_hashmap_basics;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap removal: remove(key) and the atomic conditional remove(key, value).
 */
public class ConcurrentHashMapRemoveDemo {

    public static void main(String[] args) {

        ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
        users.put("u1", "Alice");
        users.put("u2", "Bob");

        users.remove("u2");

        System.out.println("Contains u2? " + users.containsKey("u2")); // false

        // also available: remove only if the key is still mapped to this value
        users.remove("u1", "Alice");
        System.out.println("Contains u1? " + users.containsKey("u1")); // false
    }
}
