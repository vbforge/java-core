package com.vbforge.practice.map.practice_34_linkedhashmap_problems;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Problem:
 * Design a data structure that works like an LRU Cache:
 * Stores key-value pairs up to a fixed capacity.
 * When the cache exceeds capacity, remove the least recently used entry.
 * On every get(key) or put(key, value), the key is marked as most recently used.
 * Why LinkedHashMap?
 * LinkedHashMap with accessOrder=true automatically keeps track of access order.
 * Override removeEldestEntry() to evict oldest entry when capacity is exceeded.
 * */
public class LeastRecentlyUsedCache {
    public static void main(String[] args) {

        LRUCache<Integer, String> cache = new LRUCache<>(3);

        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");
        cache.get(1);                // Access 1, makes it most recent
        cache.put(4, "D");           // Removes least recently used (key 2)

        System.out.println(cache);


    }

    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

}
