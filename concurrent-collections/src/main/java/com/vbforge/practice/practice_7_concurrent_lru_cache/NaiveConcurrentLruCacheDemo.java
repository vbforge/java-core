package com.vbforge.practice.practice_7_concurrent_lru_cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * LRU (Least Recently Used) cache built from ConcurrentHashMap + ConcurrentLinkedDeque (access order).
 *
 * WARNING - this is a naive version, NOT thread-safe as a whole.
 * Each collection is thread-safe on its own, but every cache operation is a SEQUENCE of calls
 * (containsKey -> remove -> addFirst -> get, or size check -> pollLast -> remove -> put).
 * Another thread can interleave between those calls, so the map and the deque can drift apart
 * (this is the "thread-safe parts != thread-safe whole" lesson).
 * A correct concurrent LRU needs one lock around the whole operation
 * (or a purpose-built structure such as a synchronized LinkedHashMap).
 *
 * Here it is used from one thread to show the LRU eviction logic only.
 */
public class NaiveConcurrentLruCacheDemo<K, V> {

    private final int capacity;
    private final ConcurrentHashMap<K, V> map;
    private final ConcurrentLinkedDeque<K> deque;

    public NaiveConcurrentLruCacheDemo(int capacity) {
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>();
        this.deque = new ConcurrentLinkedDeque<>();
    }

    public V get(K key) {
        if (!map.containsKey(key)) return null;
        // Move to recent
        deque.remove(key);
        deque.addFirst(key);
        return map.get(key);
    }

    public void put(K key, V value) {
        if (map.containsKey(key)) {
            deque.remove(key);
        } else if (map.size() >= capacity) {
            // Evict the least recently used
            K lru = deque.pollLast();
            if (lru != null) {
                map.remove(lru);
            }
        }

        deque.addFirst(key);
        map.put(key, value);
    }

    public void printState() {
        System.out.println("Cache: " + map);
        System.out.println("Order: " + deque);
    }

    public static void main(String[] args) {
        NaiveConcurrentLruCacheDemo<Integer, String> cache = new NaiveConcurrentLruCacheDemo<>(3);

        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        cache.printState();

        cache.get(1);         // Access 1 → moves to front
        cache.put(4, "Four"); // Evicts 2
        cache.printState();
    }
}
