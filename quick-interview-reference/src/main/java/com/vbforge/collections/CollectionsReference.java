package com.vbforge.collections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ═══════════════════════════════════════════════════════════════
 *  COLLECTIONS INTERVIEW REFERENCE
 *  Topics: Big-O tables · HashMap internals · List/Set/Map
 *          comparisons · Comparable vs Comparator ·
 *          fail-fast vs fail-safe · common gotchas
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class CollectionsReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. BIG-O COMPLEXITY TABLE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  LIST IMPLEMENTATIONS:
     *  Operation          │ ArrayList   │ LinkedList  │ ArrayDeque
     *  ───────────────────┼─────────────┼─────────────┼───────────
     *  get(index)         │ O(1)        │ O(n)        │ O(1) ends
     *  add(end)           │ O(1) amort  │ O(1)        │ O(1) amort
     *  add(index)         │ O(n)        │ O(n)        │ O(n)
     *  remove(index)      │ O(n)        │ O(n)        │ O(n)
     *  remove(first/last) │ O(n)/O(1)   │ O(1)        │ O(1)
     *  contains           │ O(n)        │ O(n)        │ O(n)
     *  Memory overhead    │ Low         │ High (nodes)│ Low
     *
     *  SET IMPLEMENTATIONS:
     *  Operation          │ HashSet     │ LinkedHashSet│ TreeSet
     *  ───────────────────┼─────────────┼──────────────┼──────────
     *  add/remove/contains│ O(1) avg    │ O(1) avg     │ O(log n)
     *  Iteration order    │ None        │ Insertion    │ Sorted
     *  Null allowed       │ 1 null      │ 1 null       │ ❌ No
     *
     *  MAP IMPLEMENTATIONS:
     *  Operation          │ HashMap     │ LinkedHashMap│ TreeMap
     *  ───────────────────┼─────────────┼──────────────┼──────────
     *  get/put/remove     │ O(1) avg    │ O(1) avg     │ O(log n)
     *  Iteration order    │ None        │ Insertion    │ Key-sorted
     *  Null key allowed   │ 1 null key  │ 1 null key   │ ❌ No
     *  Thread-safe        │ ❌ No       │ ❌ No        │ ❌ No
     *
     *  USE ConcurrentHashMap for thread-safe map operations.
     */


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. HASHMAP INTERNALS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Structure: array of buckets (default 16).
     *  Each bucket = linked list → treeified to red-black tree when size ≥ 8 (Java 8+).
     *
     *  PUT flow:
     *  1. key.hashCode() → spread hash → bucket index
     *  2. If bucket empty → store directly
     *  3. If collision → traverse list/tree, use equals() to find existing key
     *
     *  RESIZE (rehash): when size > capacity * loadFactor (default 0.75)
     *  → new array of 2x size, all entries re-hashed → O(n) operation
     *
     *  INTERVIEW QUESTIONS:
     *  Q: What happens if hashCode() always returns 0?
     *  A: All keys go to bucket 0 → single linked list → O(n) for all operations
     *
     *  Q: What if you mutate a key after putting it in a HashMap?
     *  A: hashCode() changes → object is now in wrong bucket → get() returns null
     *     → memory leak (entry exists but unreachable)
     *
     *  Q: HashMap vs Hashtable?
     *  A: Hashtable: synchronized (slow), no null keys/values, legacy — avoid.
     *     HashMap: not synchronized, 1 null key, modern — use HashMap or ConcurrentHashMap.
     */
    static void hashMapInternals() {
        // ⚠️ Pre-size HashMap when you know the expected size — avoids rehashing
        int expectedSize = 1000;
        Map<String, Integer> preScaled = new HashMap<>(
            (int)(expectedSize / 0.75) + 1   // formula: expectedSize / loadFactor
        );

        // ⚠️ Mutable key gotcha
        Map<List<Integer>, String> map = new HashMap<>();
        List<Integer> key = new ArrayList<>(List.of(1, 2, 3));
        map.put(key, "value");
        key.add(4);                           // mutate key AFTER put
        System.out.println(map.get(key));     // null — hashCode changed, wrong bucket!

        // ✅ Useful Map API (Java 8+)
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 90);

        scores.getOrDefault("Bob", 0);                          // 0 — no NPE
        scores.putIfAbsent("Alice", 100);                       // ignored — already exists
        scores.computeIfAbsent("Charlie", k -> k.length());     // computes and stores
        scores.merge("Alice", 5, Integer::sum);                 // 90+5=95
        scores.forEach((k, v) -> System.out.println(k + ": " + v));
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. ARRAYLIST vs LINKEDLIST — side-by-side
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ArrayList:
     *  ✅ O(1) random access (backed by array)
     *  ✅ CPU cache-friendly (contiguous memory)
     *  ✅ Low memory overhead
     *  ❌ O(n) insert/remove in middle (shifts elements)
     *  ❌ Resize copies entire array
     *
     *  LinkedList:
     *  ✅ O(1) insert/remove at known node (doubly-linked)
     *  ✅ Implements Deque — efficient as queue/stack
     *  ❌ O(n) random access (must traverse from head)
     *  ❌ High memory overhead (next/prev pointers per node)
     *  ❌ Cache-unfriendly (scattered heap objects)
     *
     *  PRACTICAL RULE: Use ArrayList almost always.
     *  Use LinkedList only as a Queue/Deque implementation.
     *  ArrayDeque is even better than LinkedList for queue/stack use cases.
     */


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. COMPARABLE vs COMPARATOR — side-by-side
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Comparable  — "natural ordering" — implemented BY the class itself
     *  Comparator  — "custom ordering"  — implemented OUTSIDE the class
     *
     *  Feature              │ Comparable          │ Comparator
     *  ─────────────────────┼─────────────────────┼──────────────────────
     *  Package              │ java.lang           │ java.util
     *  Method               │ compareTo(T o)      │ compare(T o1, T o2)
     *  Definition location  │ Inside class        │ Outside (separate/lambda)
     *  Multiple orderings   │ ❌ Only one         │ ✅ Many
     *  Modify original class│ ✅ Required         │ ❌ Not needed
     *  Used by              │ TreeSet, TreeMap,   │ sort(), Stream.sorted(),
     *                       │ Collections.sort()  │ Comparator.comparing()
     *
     *  compareTo() return convention:
     *  negative → this < other
     *  zero     → this == other
     *  positive → this > other
     */
    record Student(String name, int gpa) implements Comparable<Student> {
        @Override
        public int compareTo(Student other) {
            return Integer.compare(this.gpa, other.gpa);  // natural: ascending GPA
        }
    }

    static void sortingExamples() {
        List<Student> students = new ArrayList<>(List.of(
            new Student("Alice", 90),
            new Student("Bob", 85),
            new Student("Charlie", 92)
        ));

        // Natural order (Comparable)
        Collections.sort(students);

        // Custom order (Comparator) — descending name, then ascending GPA
        students.sort(Comparator.comparing(Student::name)
                                .reversed()
                                .thenComparingInt(Student::gpa));

        // ⚠️ Comparator.comparing with method reference is null-safe only with nullsFirst/Last
        Comparator<Student> nullSafe = Comparator.nullsFirst(
            Comparator.comparing(Student::name)
        );
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 5. FAIL-FAST vs FAIL-SAFE ITERATORS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  FAIL-FAST: throws ConcurrentModificationException if collection
     *             is structurally modified while iterating.
     *             Uses an internal modCount counter.
     *             Collections: ArrayList, HashMap, HashSet (most java.util)
     *
     *  FAIL-SAFE: iterates over a snapshot/copy — no CME.
     *             Changes during iteration may or may not be visible.
     *             Collections: ConcurrentHashMap, CopyOnWriteArrayList
     */
    static void iteratorGotchas() {
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));

        // ❌ Anti-pattern: modifying list while iterating with for-each → ConcurrentModificationException
        // for (String s : list) { if (s.equals("b")) list.remove(s); }

        // ✅ Use iterator.remove() — safe
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().equals("b")) it.remove();   // safe removal during iteration
        }

        // ✅ Or use removeIf() — Java 8+
        list.removeIf(s -> s.equals("c"));

        // ✅ ConcurrentHashMap — fail-safe, no CME
        Map<String, Integer> concurrent = new ConcurrentHashMap<>();
        concurrent.put("x", 1);
        for (String key : concurrent.keySet()) {
            concurrent.put("y", 2);  // safe — no CME
        }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 6. COMMON GOTCHAS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void collectionGotchas() {
        // ⚠️ Arrays.asList() returns FIXED-SIZE list — add/remove throws UnsupportedOperationException
        List<String> fixed = Arrays.asList("a", "b", "c");
        // fixed.add("d");  // UnsupportedOperationException!
        fixed.set(0, "z"); // ✅ set() is allowed — size doesn't change

        // ⚠️ List.of() returns IMMUTABLE list — even set() throws
        List<String> immutable = List.of("a", "b", "c");
        // immutable.set(0, "z");  // UnsupportedOperationException!

        // ✅ To get mutable copy:
        List<String> mutable = new ArrayList<>(List.of("a", "b", "c"));

        // ⚠️ Set.of() / Map.of() have RANDOM iteration order (may differ per JVM run)
        // ⚠️ Map.of() keys must be unique — duplicate key → IllegalArgumentException at runtime

        // ⚠️ Integer key in Map — autoboxing comparison
        Map<Integer, String> m = new HashMap<>();
        m.put(1, "one");
        Integer key = 1;
        System.out.println(m.get(key));          // "one" — uses equals() internally
        System.out.println(m.containsKey(1));    // true

        // ⚠️ PriorityQueue does NOT iterate in sorted order — only poll() is ordered
        PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(3, 1, 4, 1, 5));
        System.out.println(pq);          // NOT sorted print — internal heap array
        System.out.println(pq.poll());   // 1 — correctly removes minimum
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Using LinkedList for random access      → use ArrayList
     *  ❌ Modifying list during for-each          → CME — use iterator.remove() or removeIf()
     *  ❌ Using HashMap with mutable keys         → entries become unreachable
     *  ❌ Assuming HashMap iteration order        → use LinkedHashMap if order matters
     *  ❌ Not pre-sizing HashMap for large data   → multiple expensive rehashes
     *  ❌ Using Vector/Hashtable/Stack            → legacy, use ArrayList/HashMap/ArrayDeque
     *  ❌ Calling pq.toString() expecting sorted  → iterate via poll() for sorted output
     */

    public static void main(String[] args) {
        hashMapInternals();
        sortingExamples();
        iteratorGotchas();
        collectionGotchas();
    }
}
