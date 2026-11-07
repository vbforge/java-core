package com.vbforge.practice.map.practice_31_map_compute_and_immutable_factories;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Deep dive into the Java Map interface and its key implementations.
 *
 * Map contract (java.util.Map<K,V>):
 *   - Key-value pairs; keys are unique, values can repeat
 *   - put(k, v) — add or replace; returns OLD value (or null)
 *   - get(k)    — lookup by key; returns null if absent (ambiguous with null value!)
 *   - getOrDefault(k, def) — safer: returns def when key absent
 *   - containsKey(k) / containsValue(v) — membership tests
 *
 * Implementations:
 * ┌─────────────────┬──────────────┬──────────────────────────────────────────┐
 * │ HashMap         │ O(1) avg     │ No order; null key OK; NOT thread-safe   │
 * │ LinkedHashMap   │ O(1) avg     │ Insertion-order (default) or access-order│
 * │ TreeMap         │ O(log n)     │ Sorted by key; NavigableMap; no null key │
 * │ Hashtable       │ O(1) avg     │ Legacy, synchronized, no null key/value  │
 * └─────────────────┴──────────────┴──────────────────────────────────────────┘
 *
 * HashMap internals (critical for interviews):
 *   - Backed by array of "buckets" (Node<K,V>[])
 *   - Default capacity = 16, load factor = 0.75
 *   - Resize at 75% full → new array (capacity×2), rehash all entries
 *   - Collision handling:
 *     - Java 7-: linked list in bucket
 *     - Java 8+: linked list → Red-Black tree when bucket size ≥ 8 (treeify)
 *                            → back to list when bucket shrinks to ≤ 6
 *   - hashCode() determines bucket index; equals() resolves collisions
 *   - Bad hashCode (many collisions) → O(n) worst case; O(log n) with treeified bucket
 *
 * Thread safety: use ConcurrentHashMap for concurrent access (see concurrent_collections).
 */
public class MapComputeMethodsDemo {

    // ─────────────────────────────────────────────────────────────
    // 1. HashMap — core CRUD operations
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 2. Iteration patterns
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 3. Java 8+ compute methods — the atomic put patterns
    // ─────────────────────────────────────────────────────────────

    /**
     * These methods perform read-modify-write atomically at the map level
     * (not thread-safe unless using ConcurrentHashMap, but eliminate boilerplate).
     *
     *   compute(k, (k,v) → newV)    — always runs, v=null if absent
     *   computeIfAbsent(k, k → v)  — runs only if key absent; good for lazy init
     *   computeIfPresent(k, (k,v)) — runs only if key present; removes if result=null
     *   merge(k, val, (old,val) → merged) — great for frequency counting / accumulation
     */
    public static void computeMethods() {
        System.out.println("\n=== Compute Methods ===");

        // Word frequency counter with merge — cleanest pattern
        String text = "the cat sat on the mat the cat";
        Map<String, Integer> freq = new HashMap<>();
        for (String word : text.split(" ")) {
            freq.merge(word, 1, Integer::sum);
        }
        // Sort for deterministic output
        new TreeMap<>(freq).forEach((w, c) -> System.out.println("  " + w + ": " + c));

        // computeIfAbsent — lazy list creation (multi-map pattern)
        Map<String, List<String>> groups = new HashMap<>();
        String[] names = {"Alice", "Bob", "Amy", "Brian", "Charlie"};
        for (String name : names) {
            groups.computeIfAbsent(
                String.valueOf(name.charAt(0)),  // key = first letter
                k -> new ArrayList<>()
            ).add(name);
        }
        System.out.println("\nGrouped by first letter: " + new TreeMap<>(groups));

        // compute — unconditional update
        Map<String, Integer> hits = new HashMap<>();
        hits.put("page1", 5);
        hits.compute("page1", (k, v) -> v == null ? 1 : v + 1);
        hits.compute("page2", (k, v) -> v == null ? 1 : v + 1);  // absent → 1
        System.out.println("\nHits after compute: " + hits);

        // computeIfPresent — only updates existing keys
        hits.computeIfPresent("page1", (k, v) -> v * 10);
        hits.computeIfPresent("page3", (k, v) -> v * 10); // page3 absent — no-op
        System.out.println("After computeIfPresent: " + hits);
    }

    // ─────────────────────────────────────────────────────────────
    // 4. LinkedHashMap — insertion order & access-order (LRU cache)
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 5. TreeMap — sorted keys, NavigableMap
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 6. HashMap internals — hashCode distribution matters
    // ─────────────────────────────────────────────────────────────


    static class BadHashKey {
        final String val;
        BadHashKey(String val) { this.val = val; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BadHashKey)) return false;
            return val.equals(((BadHashKey) o).val);
        }

        @Override
        public int hashCode() { return 0; }  // Terrible! All keys → bucket 0
    }

    // ─────────────────────────────────────────────────────────────
    // 7. Immutable Maps (Java 9+)
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // main
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        computeMethods();
    }
}
