package com.vbforge.arrays_collections_basics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CollectionsDemo — annotated reference for arrays and the core Java Collections types.
 *
 * <p>Read top-to-bottom. Each section covers one data structure with concrete examples
 * and a note explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Topics:
 * <ol>
 *   <li>Arrays — declaration, initialisation, multi-dimensional, {@link Arrays} utility</li>
 *   <li>ArrayList — dynamic array, random access, auto-resizing</li>
 *   <li>LinkedList — doubly-linked list, efficient head/tail ops, as a Deque</li>
 *   <li>HashSet — unordered, no duplicates, O(1) contains</li>
 *   <li>HashMap — key→value mapping, O(1) get/put, iteration patterns</li>
 *   <li>Iteration — for-each, iterator, index-based, entrySet loop</li>
 *   <li>Collections utility class — sort, reverse, min/max, frequency, unmodifiable</li>
 * </ol>
 */
public class CollectionsDemo {

    public static void main(String[] args) {
        arraysDemo();
        System.out.println("---");
        arrayListDemo();
        System.out.println("---");
        linkedListDemo();
        System.out.println("---");
        hashSetDemo();
        System.out.println("---");
        hashMapDemo();
        System.out.println("---");
        iterationDemo();
        System.out.println("---");
        collectionsUtilDemo();
    }

    // ── 1. Arrays ─────────────────────────────────────────────────────────────

    /**
     * Arrays are fixed-size, zero-indexed, and store elements contiguously in memory.
     *
     * <p>WHY this matters: arrays are the fastest collection for indexed access (O(1))
     * and the most memory-efficient, but their size is fixed at creation time.
     * When you need to grow or shrink, use {@link ArrayList} instead.
     */
    static void arraysDemo() {
        System.out.println("=== Arrays ===");

        // Declaration and initialisation
        int[] numbers = new int[5];                        // all zeros by default
        int[] primes  = {2, 3, 5, 7, 11};                 // array literal
        String[] days = new String[]{"Mon", "Tue", "Wed"}; // explicit new — same as literal

        System.out.println("length        : " + primes.length);   // 5  (field, not method)
        System.out.println("primes[0]     : " + primes[0]);       // 2
        System.out.println("primes[4]     : " + primes[4]);       // 11

        // Default values: int→0, boolean→false, Object→null
        System.out.println("numbers[0]    : " + numbers[0]);      // 0
        boolean[] flags = new boolean[3];
        System.out.println("flags[0]      : " + flags[0]);        // false
        String[]  names = new String[2];
        System.out.println("names[0]      : " + names[0]);        // null

        // Arrays.sort — in-place, uses dual-pivot quicksort for primitives
        int[] unsorted = {5, 3, 1, 4, 2};
        Arrays.sort(unsorted);
        System.out.println("after sort    : " + Arrays.toString(unsorted)); // [1, 2, 3, 4, 5]

        // Arrays.binarySearch — only valid on SORTED arrays; returns insertion point if absent
        int idx = Arrays.binarySearch(unsorted, 3);
        System.out.println("binarySearch  : " + idx);   // 2

        // Arrays.copyOf — creates a new array; truncates or pads with defaults
        int[] copy = Arrays.copyOf(primes, 3);
        System.out.println("copyOf(3)     : " + Arrays.toString(copy));      // [2, 3, 5]
        int[] padded = Arrays.copyOf(primes, 7);
        System.out.println("copyOf(7)     : " + Arrays.toString(padded));    // [2,3,5,7,11,0,0]

        // Arrays.copyOfRange — copy a slice [from, to)
        int[] slice = Arrays.copyOfRange(primes, 1, 4);
        System.out.println("copyOfRange   : " + Arrays.toString(slice));     // [3, 5, 7]

        // Arrays.fill — fills all (or a range) with a single value
        int[] filled = new int[5];
        Arrays.fill(filled, 9);
        System.out.println("fill(9)       : " + Arrays.toString(filled));    // [9,9,9,9,9]

        // Arrays.equals — element-wise comparison (not reference!)
        int[] a = {1, 2, 3};
        int[] b = {1, 2, 3};
        System.out.println("arrays ==     : " + (a == b));              // false — different refs
        System.out.println("Arrays.equals : " + Arrays.equals(a, b));   // true  — same content

        // 2D arrays — array of arrays; rows can differ in length (jagged)
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.println("matrix[1][2]  : " + matrix[1][2]);          // 6
        System.out.println("deepToString  : " + Arrays.deepToString(matrix)); // [[1,2,3],[4,5,6],[7,8,9]]

        // Convert array ↔ List
        String[]     arr  = {"a", "b", "c"};
        List<String> list = Arrays.asList(arr);   // fixed-size backed list — no add/remove!
        System.out.println("asList        : " + list);
        // list.add("d");  // ← throws UnsupportedOperationException

        List<String> mutable = new ArrayList<>(Arrays.asList(arr)); // mutable copy
        mutable.add("d");
        System.out.println("mutable list  : " + mutable);
    }

    // ── 2. ArrayList ──────────────────────────────────────────────────────────

    /**
     * {@link ArrayList} is a resizable array backed by an {@code Object[]}.
     *
     * <p>WHY use it over a plain array: you don't need to know the size upfront,
     * and it offers a rich API. The trade-off is boxing for primitives and
     * O(n) insertion/removal in the middle (elements must shift).
     *
     * <p>Time complexity:
     * <ul>
     *   <li>get(i) / set(i) — O(1)</li>
     *   <li>add(e) at end — O(1) amortised</li>
     *   <li>add(i, e) / remove(i) in middle — O(n)</li>
     *   <li>contains / indexOf — O(n)</li>
     * </ul>
     */
    static void arrayListDemo() {
        System.out.println("=== ArrayList ===");

        // Creation
        ArrayList<String> list = new ArrayList<>();          // empty, initial capacity 10
        ArrayList<String> withCap = new ArrayList<>(32);    // pre-allocated — avoids resizing
        ArrayList<String> fromList = new ArrayList<>(List.of("a", "b", "c")); // copy constructor

        // Add
        list.add("Alice");
        list.add("Bob");
        list.add("Carol");
        list.add(1, "Zara");    // inserts at index 1 — shifts rest right
        System.out.println("after adds    : " + list);   // [Alice, Zara, Bob, Carol]

        // Access
        System.out.println("get(0)        : " + list.get(0));   // Alice
        System.out.println("size()        : " + list.size());   // 4

        // Update
        list.set(0, "Anna");
        System.out.println("after set(0)  : " + list);

        // Remove by index vs by object
        list.remove(0);                  // removes "Anna" at index 0
        System.out.println("remove(0)     : " + list);
        list.remove("Bob");              // removes first occurrence of "Bob"
        System.out.println("remove(Bob)   : " + list);

        // Search
        list.add("Zara");                // add duplicate
        System.out.println("contains Zara : " + list.contains("Zara"));  // true
        System.out.println("indexOf Zara  : " + list.indexOf("Zara"));   // first occurrence
        System.out.println("lastIndexOf   : " + list.lastIndexOf("Zara")); // last occurrence

        // Bulk operations
        List<String> batch = List.of("Dan", "Eve");
        list.addAll(batch);
        System.out.println("after addAll  : " + list);
        list.removeAll(batch);
        System.out.println("after removeAll: " + list);

        // subList — a VIEW, not a copy; mutations reflect in both
        ArrayList<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        List<Integer> sub = nums.subList(1, 4);   // [2, 3, 4]
        System.out.println("subList       : " + sub);
        sub.set(0, 99);                            // mutates nums too!
        System.out.println("nums after sub mutation: " + nums);

        // Sort via Collections.sort or List.sort
        ArrayList<Integer> toSort = new ArrayList<>(List.of(5, 3, 1, 4, 2));
        Collections.sort(toSort);                           // natural order
        System.out.println("sorted        : " + toSort);
        toSort.sort((x, y) -> y - x);                      // reverse via Comparator
        System.out.println("reverse sorted: " + toSort);

        // Convert to array
        String[] arr = fromList.toArray(new String[0]);
        System.out.println("toArray       : " + Arrays.toString(arr));

        // isEmpty and clear
        ArrayList<String> temp = new ArrayList<>(List.of("x", "y"));
        System.out.println("isEmpty       : " + temp.isEmpty());   // false
        temp.clear();
        System.out.println("after clear   : " + temp.isEmpty());   // true
    }

    // ── 3. LinkedList ─────────────────────────────────────────────────────────

    /**
     * {@link LinkedList} is a doubly-linked list implementing both {@link List}
     * and {@link java.util.Deque}.
     *
     * <p>WHY use it: O(1) add/remove at the head or tail. Use it as a queue,
     * stack, or deque. Avoid it when random access is frequent — {@code get(i)}
     * is O(n) because it must walk the chain.
     *
     * <p>Time complexity:
     * <ul>
     *   <li>addFirst / addLast / removeFirst / removeLast — O(1)</li>
     *   <li>get(i) — O(n)</li>
     *   <li>contains — O(n)</li>
     * </ul>
     */
    static void linkedListDemo() {
        System.out.println("=== LinkedList ===");

        LinkedList<String> ll = new LinkedList<>();

        // List operations (same API as ArrayList)
        ll.add("B");
        ll.add("C");
        ll.add(0, "A");
        System.out.println("as List      : " + ll);   // [A, B, C]

        // Deque / Queue operations — unique to LinkedList
        ll.addFirst("Z");               // O(1) — prepend
        ll.addLast("D");                // O(1) — append (same as add())
        System.out.println("addFirst/Last: " + ll);  // [Z, A, B, C, D]

        System.out.println("peekFirst()  : " + ll.peekFirst());    // Z — does NOT remove
        System.out.println("peekLast()   : " + ll.peekLast());     // D — does NOT remove

        System.out.println("pollFirst()  : " + ll.pollFirst());    // Z — removes head
        System.out.println("pollLast()   : " + ll.pollLast());     // D — removes tail
        System.out.println("after polls  : " + ll);                 // [A, B, C]

        // Used as a STACK (LIFO) — push to front, pop from front
        LinkedList<String> stack = new LinkedList<>();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        System.out.println("stack pop    : " + stack.pop());  // third (LIFO)
        System.out.println("stack after  : " + stack);

        // Used as a QUEUE (FIFO) — offer to tail, poll from head
        LinkedList<String> queue = new LinkedList<>();
        queue.offer("first");
        queue.offer("second");
        queue.offer("third");
        System.out.println("queue poll   : " + queue.poll()); // first (FIFO)
        System.out.println("queue after  : " + queue);
    }

    // ── 4. HashSet ────────────────────────────────────────────────────────────

    /**
     * {@link HashSet} stores unique elements with no guaranteed iteration order.
     * Backed by a {@link HashMap} internally.
     *
     * <p>WHY use it: O(1) average-case {@code contains}, {@code add}, and
     * {@code remove} — far faster than scanning a list for membership checks.
     * The contract: elements must correctly implement {@code equals()} and
     * {@code hashCode()}, because the set uses the hash to place them in buckets.
     *
     * <p>Time complexity (average):
     * <ul>
     *   <li>add / remove / contains — O(1)</li>
     *   <li>iteration — O(n)</li>
     * </ul>
     */
    static void hashSetDemo() {
        System.out.println("=== HashSet ===");

        HashSet<String> set = new HashSet<>();

        // add() returns true if element was new, false if already present
        System.out.println("add Alice : " + set.add("Alice"));  // true
        System.out.println("add Bob   : " + set.add("Bob"));    // true
        System.out.println("add Alice : " + set.add("Alice"));  // false — duplicate

        System.out.println("size      : " + set.size());         // 2 (not 3!)
        System.out.println("contains  : " + set.contains("Bob")); // true

        // remove() returns true if element existed
        System.out.println("remove    : " + set.remove("Bob"));  // true
        System.out.println("remove    : " + set.remove("Bob"));  // false — already gone

        // Set operations — intersection, union, difference
        Set<Integer> a = new HashSet<>(List.of(1, 2, 3, 4));
        Set<Integer> b = new HashSet<>(List.of(3, 4, 5, 6));

        // Union — all elements from both
        Set<Integer> union = new HashSet<>(a);
        union.addAll(b);
        System.out.println("union     : " + union);       // [1,2,3,4,5,6] (order varies)

        // Intersection — only elements in both
        Set<Integer> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        System.out.println("intersect : " + intersection); // [3,4]

        // Difference — elements in a but NOT in b
        Set<Integer> diff = new HashSet<>(a);
        diff.removeAll(b);
        System.out.println("diff a∖b  : " + diff);         // [1,2]

        // containsAll — subset check
        Set<Integer> sub = new HashSet<>(List.of(3, 4));
        System.out.println("subset    : " + a.containsAll(sub)); // true

        // Order is NOT guaranteed — never rely on iteration order with HashSet
        // Use LinkedHashSet to preserve insertion order
        // Use TreeSet for sorted order
        Set<String> ordered = new java.util.LinkedHashSet<>(List.of("banana", "apple", "cherry"));
        System.out.println("LinkedHashSet: " + ordered); // [banana, apple, cherry] — insertion order
    }

    // ── 5. HashMap ────────────────────────────────────────────────────────────

    /**
     * {@link HashMap} maps keys to values. Keys are unique; values may repeat.
     * Internally it uses an array of buckets, with chaining or treeification
     * for collision handling (Java 8+).
     *
     * <p>WHY use it: O(1) average-case lookup, insertion, and deletion by key.
     * The contract: keys must implement {@code equals()} and {@code hashCode()}.
     * Null keys and null values are permitted (one null key only).
     *
     * <p>Time complexity (average):
     * <ul>
     *   <li>get / put / containsKey / remove — O(1)</li>
     *   <li>iteration — O(n + capacity)</li>
     * </ul>
     */
    static void hashMapDemo() {
        System.out.println("=== HashMap ===");

        HashMap<String, Integer> scores = new HashMap<>();

        // put() — adds or replaces; returns previous value (or null if new)
        scores.put("Alice", 95);
        scores.put("Bob",   80);
        scores.put("Carol", 72);
        Integer old = scores.put("Alice", 99);   // update
        System.out.println("old Alice score : " + old);   // 95

        // get() — returns value or null if key absent
        System.out.println("Alice score     : " + scores.get("Alice"));    // 99
        System.out.println("Dave score      : " + scores.get("Dave"));     // null

        // getOrDefault() — null-safe fallback
        System.out.println("Dave default    : " + scores.getOrDefault("Dave", 0)); // 0

        // containsKey / containsValue
        System.out.println("has Alice       : " + scores.containsKey("Alice"));   // true
        System.out.println("has score 80    : " + scores.containsValue(80));      // true

        // putIfAbsent() — only inserts if key is not already present
        scores.putIfAbsent("Dave", 88);   // Dave is new — inserted
        scores.putIfAbsent("Alice", 0);   // Alice exists — NOT overwritten
        System.out.println("Dave added      : " + scores.get("Dave"));     // 88
        System.out.println("Alice unchanged : " + scores.get("Alice"));    // 99

        // remove() — by key, or by key+value pair (only removes if value matches)
        scores.remove("Dave");                   // remove by key
        boolean removed = scores.remove("Bob", 0); // value mismatch — NOT removed
        System.out.println("Bob still there : " + scores.containsKey("Bob")); // true

        // compute patterns — powerful for accumulation
        HashMap<String, Integer> wordCount = new HashMap<>();
        String[] words = {"cat", "dog", "cat", "fish", "dog", "cat"};
        for (String w : words) {
            // getOrDefault — increment if present, insert 1 if absent
            wordCount.put(w, wordCount.getOrDefault(w, 0) + 1);
        }
        System.out.println("word counts     : " + wordCount); // {cat=3, dog=2, fish=1}

        // merge() — cleaner than getOrDefault for accumulation
        HashMap<String, Integer> merged = new HashMap<>();
        for (String w : words) {
            merged.merge(w, 1, Integer::sum);  // start at 1, add 1 on collision
        }
        System.out.println("merge counts    : " + merged);

        // size, isEmpty, clear
        System.out.println("size            : " + scores.size());
        System.out.println("isEmpty         : " + scores.isEmpty());

        // Keys, values, entries as views
        System.out.println("keySet          : " + scores.keySet());
        System.out.println("values          : " + scores.values());
        System.out.println("entrySet size   : " + scores.entrySet().size());
    }

    // ── 6. Iteration patterns ─────────────────────────────────────────────────

    /**
     * Shows every standard way to iterate over Java collections.
     * Prefer for-each for simplicity; use {@link Iterator} when you need to
     * remove while iterating (removing during for-each throws
     * {@link java.util.ConcurrentModificationException}).
     */
    static void iterationDemo() {
        System.out.println("=== Iteration ===");

        List<String> list = new ArrayList<>(List.of("alpha", "beta", "gamma", "delta"));

        // For-each — clean, preferred for read-only iteration
        System.out.print("for-each   : ");
        for (String s : list) {
            System.out.print(s + " ");
        }
        System.out.println();

        // Index-based — needed when you need the index
        System.out.print("indexed    : ");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(i + ":" + list.get(i) + " ");
        }
        System.out.println();

        // Iterator — safe removal during iteration
        List<String> mutable = new ArrayList<>(list);
        Iterator<String> it = mutable.iterator();
        while (it.hasNext()) {
            if (it.next().startsWith("b")) {
                it.remove();    // safe! no ConcurrentModificationException
            }
        }
        System.out.println("after remove-b: " + mutable); // [alpha, gamma, delta]

        // forEach with lambda (Java 8+)
        System.out.print("forEach λ  : ");
        list.forEach(s -> System.out.print(s.toUpperCase() + " "));
        System.out.println();

        // Iterating a HashMap — entrySet is the standard approach
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1); map.put("two", 2); map.put("three", 3);

        System.out.print("entrySet   : ");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
        }
        System.out.println();

        // forEach on Map (Java 8+)
        System.out.print("map forEach: ");
        map.forEach((k, v) -> System.out.print(k + "→" + v + " "));
        System.out.println();

        // Iterating a Set — same as List for-each, but order not guaranteed
        Set<String> set = new HashSet<>(List.of("x", "y", "z"));
        System.out.print("set for-each: ");
        for (String s : set) {
            System.out.print(s + " ");
        }
        System.out.println();

        // WRONG — never remove from a list using for-each
        // for (String s : list) { list.remove(s); }  // ← ConcurrentModificationException!
    }

    // ── 7. Collections utility class ──────────────────────────────────────────

    /**
     * {@link Collections} provides static utility methods for working with
     * {@link List} and other collection types.
     */
    static void collectionsUtilDemo() {
        System.out.println("=== Collections utility ===");

        List<Integer> nums = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9, 2, 6));

        // sort / reverse
        Collections.sort(nums);
        System.out.println("sorted      : " + nums);
        Collections.reverse(nums);
        System.out.println("reversed    : " + nums);

        // min / max
        System.out.println("min         : " + Collections.min(nums));
        System.out.println("max         : " + Collections.max(nums));

        // frequency — counts occurrences of an element
        List<String> words = List.of("cat", "dog", "cat", "fish", "cat");
        System.out.println("frequency   : " + Collections.frequency(words, "cat")); // 3

        // shuffle — randomises order (useful for testing / games)
        List<Integer> shuffled = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        Collections.shuffle(shuffled);
        System.out.println("shuffled    : " + shuffled);  // random order

        // fill — replaces all elements with a single value
        List<String> filled = new ArrayList<>(List.of("a", "b", "c"));
        Collections.fill(filled, "x");
        System.out.println("fill        : " + filled);   // [x, x, x]

        // nCopies — creates an immutable list of n copies
        List<String> copies = Collections.nCopies(4, "hello");
        System.out.println("nCopies     : " + copies);   // [hello, hello, hello, hello]

        // unmodifiableList — wraps list in a read-only view
        List<String> base = new ArrayList<>(List.of("a", "b", "c"));
        List<String> readOnly = Collections.unmodifiableList(base);
        System.out.println("unmodifiable: " + readOnly);
        try {
            readOnly.add("d");   // throws!
        } catch (UnsupportedOperationException e) {
            System.out.println("add blocked : UnsupportedOperationException");
        }

        // List.of / Set.of / Map.of (Java 9+) — truly immutable, no nulls
        List<String> immutable = List.of("a", "b", "c");
        Set<String>  immutableSet = Set.of("x", "y", "z");
        Map<String, Integer> immutableMap = Map.of("one", 1, "two", 2);
        System.out.println("List.of     : " + immutable);
        System.out.println("Set.of      : " + immutableSet);
        System.out.println("Map.of      : " + immutableMap);

        // disjoint — true if two collections have no elements in common
        Set<Integer> s1 = new HashSet<>(List.of(1, 2, 3));
        Set<Integer> s2 = new HashSet<>(List.of(4, 5, 6));
        Set<Integer> s3 = new HashSet<>(List.of(3, 4, 5));
        System.out.println("disjoint 1,2: " + Collections.disjoint(s1, s2)); // true
        System.out.println("disjoint 1,3: " + Collections.disjoint(s1, s3)); // false
    }
}
