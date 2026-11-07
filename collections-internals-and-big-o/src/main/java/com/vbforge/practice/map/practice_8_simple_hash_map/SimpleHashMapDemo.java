package com.vbforge.practice.map.practice_8_simple_hash_map;

import java.util.Objects;

/**
 * Simplified HashMap — hash / bucket / collision mechanics.
 *
 *   - Hash function maps key -> bucket index
 *   - Each bucket holds a linked list of entries (separate chaining)
 *   - put: hash -> bucket -> check for existing key -> insert or update
 *   - get: hash -> bucket -> scan for key
 *
 * Real Java HashMap does the same but adds Red-Black treeification (Java 8+)
 * for pathological collision chains. This simplified version stays chained.
 */
public class SimpleHashMapDemo {

    public static void main(String[] args) {
        SimpleHashMap<String, Integer> map = new SimpleHashMap<>();
        map.put("a", 1); map.put("b", 2); map.put("c", 3);
        System.out.println("get('b'):              " + map.get("b"));
        map.put("b", 99);
        System.out.println("after put('b',99):     " + map.get("b"));
        System.out.println("containsKey('a'):      " + map.containsKey("a"));
        map.remove("a");
        System.out.println("after remove('a'):     size=" + map.size() + ", empty=" + map.isEmpty());
    }

    static class SimpleHashMap<K, V> {

        private static final int DEFAULT_CAPACITY = 16;

        private static class Entry<K, V> {
            K key;
            V value;
            Entry<K, V> next;  // for chaining collisions

            Entry(K key, V value) {
                this.key   = key;
                this.value = value;
            }
        }

        @SuppressWarnings("unchecked")
        private final Entry<K, V>[] buckets = new Entry[DEFAULT_CAPACITY];
        private int size;

        // Map key -> bucket index (always positive, within capacity)
        private int bucketIndex(K key) {
            return key == null ? 0 : Math.abs(key.hashCode() % DEFAULT_CAPACITY);
        }

        /**
         * O(1) average — may be O(n) in worst case (all keys hash to same bucket).
         * Returns old value if key existed, null otherwise.
         */
        public V put(K key, V value) {
            int idx = bucketIndex(key);
            Entry<K, V> entry = buckets[idx];

            // Walk the chain: if key already exists, update and return old value
            while (entry != null) {
                if (Objects.equals(entry.key, key)) {
                    V old = entry.value;
                    entry.value = value;
                    return old;
                }
                entry = entry.next;
            }

            // Key not found — prepend new entry to bucket chain
            Entry<K, V> newEntry = new Entry<>(key, value);
            newEntry.next  = buckets[idx];
            buckets[idx]   = newEntry;
            size++;
            return null;
        }

        /**
         * O(1) average. Returns null if key absent or key maps to null value.
         */
        public V get(K key) {
            int idx = bucketIndex(key);
            Entry<K, V> entry = buckets[idx];
            while (entry != null) {
                if (Objects.equals(entry.key, key)) return entry.value;
                entry = entry.next;
            }
            return null;
        }

        public boolean containsKey(K key) {
            int idx = bucketIndex(key);
            Entry<K, V> entry = buckets[idx];
            while (entry != null) {
                if (Objects.equals(entry.key, key)) return true;
                entry = entry.next;
            }
            return false;
        }

        /**
         * O(1) average. Returns removed value, or null if key absent.
         */
        public V remove(K key) {
            int idx = bucketIndex(key);
            Entry<K, V> entry = buckets[idx];
            Entry<K, V> prev  = null;

            while (entry != null) {
                if (Objects.equals(entry.key, key)) {
                    if (prev == null) buckets[idx] = entry.next;  // first in chain
                    else              prev.next     = entry.next;
                    size--;
                    return entry.value;
                }
                prev  = entry;
                entry = entry.next;
            }
            return null;
        }

        public int size() { return size; }

        public boolean isEmpty() { return size == 0; }
    }
}
