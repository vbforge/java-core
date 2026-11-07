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
public class ImmutableMapsDemo {

    // ─────────────────────────────────────────────────────────────
    // 1. HashMap — core CRUD operations
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 2. Iteration patterns
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 3. Java 8+ compute methods — the atomic put patterns
    // ─────────────────────────────────────────────────────────────


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

    /**
     * Map.of()       — up to 10 key-value pairs; unmodifiable; no null keys/values
     * Map.copyOf()   — unmodifiable copy of existing map
     * Map.entry(k,v) — unmodifiable Entry; used in Map.ofEntries()
     */
    public static void immutableMaps() {
        System.out.println("\n=== Immutable Maps ===");

        Map<String, Integer> immutable = Map.of("one", 1, "two", 2, "three", 3);
        System.out.println("Map.of: " + immutable);

        try {
            immutable.put("four", 4);
        } catch (UnsupportedOperationException e) {
            System.out.println("UOE: Map.of is unmodifiable");
        }

        // More than 10 entries → Map.ofEntries
        Map<String, Integer> big = Map.ofEntries(
            Map.entry("a", 1), Map.entry("b", 2), Map.entry("c", 3),
            Map.entry("d", 4), Map.entry("e", 5), Map.entry("f", 6)
        );
        System.out.println("Map.ofEntries size: " + big.size());  // 6

        // Map.copyOf — unmodifiable snapshot of a mutable map
        Map<String, Integer> mutable = new HashMap<>(Map.of("x", 10, "y", 20));
        Map<String, Integer> copy    = Map.copyOf(mutable);
        mutable.put("z", 30);  // original changes
        System.out.println("copyOf unchanged: " + copy.containsKey("z")); // false
    }

    // ─────────────────────────────────────────────────────────────
    // main
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        immutableMaps();
    }
}
