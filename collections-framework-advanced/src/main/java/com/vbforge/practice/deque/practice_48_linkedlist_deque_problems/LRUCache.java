package com.vbforge.practice.deque.practice_48_linkedlist_deque_problems;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LRUCache {

    private final int capacity;
    private final LinkedList<Integer> usageOrder;  // Most recent at front
    private final Map<Integer, Integer> map;        // Key-value store

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.usageOrder = new LinkedList<>();
        this.map = new HashMap<>();
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        // Move key to front as it is now most recently used
        usageOrder.remove((Integer) key);
        usageOrder.addFirst(key);
        return map.get(key);
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            usageOrder.remove((Integer) key);
        } else if (map.size() == capacity) {
            // Remove least recently used
            int lruKey = usageOrder.removeLast();
            map.remove(lruKey);
        }
        usageOrder.addFirst(key);
        map.put(key, value);
    }

    // Demo
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));   // 1
        cache.put(3, 3);                    // Evicts key 2
        System.out.println(cache.get(2));   // -1
        cache.put(4, 4);                    // Evicts key 1
        System.out.println(cache.get(1));   // -1
        System.out.println(cache.get(3));   // 3
        System.out.println(cache.get(4));   // 4
    }

}
