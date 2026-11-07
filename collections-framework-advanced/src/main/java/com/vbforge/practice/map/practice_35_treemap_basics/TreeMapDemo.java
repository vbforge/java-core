package com.vbforge.practice.map.practice_35_treemap_basics;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TreeMapDemo {

    public static void main(String[] args) {

        // ── WHAT IT IS ────────────────────────────────────────────────────────────
        // A Map whose entries are kept SORTED BY KEY at all times (red-black tree).
        // • Keys must be mutually Comparable, or you must pass a Comparator.
        // • null KEYS are NOT allowed with natural ordering  -> NullPointerException.
        //   (a Comparator that tolerates null technically permits a null key.)
        // • Complexity: put/get/remove = O(log n); iteration is always in key order.
        // • NOT thread-safe.

        // ── 1. Natural ordering (String is Comparable) ────────────────────────────
        NavigableMap<String, Integer> stock = new TreeMap<>();
        stock.put("apple",  10);
        stock.put("banana", 5);
        stock.put("cherry", 20);
        stock.put("date",   7);
        stock.put("elder",  3);

        System.out.println("sorted by key : " + stock);
        // {apple=10, banana=5, cherry=20, date=7, elder=3}

        // ── 2. Navigation helpers ─────────────────────────────────────────────────
        System.out.println("firstKey()     = " + stock.firstKey());      // apple
        System.out.println("lastKey()      = " + stock.lastKey());       // elder
        System.out.println("firstEntry()   = " + stock.firstEntry());    // apple=10
        System.out.println("lastEntry()    = " + stock.lastEntry());     // elder=3

        // floor/ceiling/lower/higher -> nearest entries relative to a key
        System.out.println("floorKey(cherry)   = " + stock.floorKey("cherry"));   // cherry (<=)
        System.out.println("lowerKey(cherry)   = " + stock.lowerKey("cherry"));   // banana (<)
        System.out.println("ceilingKey(cherry) = " + stock.ceilingKey("cherry")); // cherry (>=)
        System.out.println("higherKey(cherry)  = " + stock.higherKey("cherry"));  // date   (>)

        // ── 3. Range views (LIVE, backed by the map — not copies) ─────────────────
        //         headMap(to, inclusive)   tailMap(from, inclusive)   subMap(from, fromInc, to, toInc)
        System.out.println("headMap(date)              = " + stock.headMap("date", false));
        System.out.println("tailMap(cherry)            = " + stock.tailMap("cherry", true));
        System.out.println("subMap(banana, date)       = " + stock.subMap("banana", true, "date", false));

        // Views are LIVE: a change in the view changes the map (and vice-versa)
        stock.headMap("cherry", false).clear();     // removes apple, banana
        System.out.println("after clearing headMap<cherry : " + stock);

        // ── 4. Descending view (also a live view) ─────────────────────────────────
        System.out.println("descendingMap() = " + stock.descendingMap());

        // ── 5. First / last removal (super handy in schedulers) ───────────────────
        stock.put("apple", 10);                     // put it back
        System.out.println("pollFirstEntry() = " + stock.pollFirstEntry());
        System.out.println("pollLastEntry()  = " + stock.pollLastEntry());
        System.out.println("after polls      = " + stock);

        // ── 6. Custom Comparator (e.g. reverse order, or by value) ────────────────
        NavigableMap<String, Integer> reverse = new TreeMap<>(Comparator.reverseOrder());
        reverse.putAll(Map.of("a", 1, "b", 2, "c", 3));
        System.out.println("reverseOrder()   = " + reverse);           // {c=3, b=2, a=1}

        // ── 7. null keys & null values ────────────────────────────────────────────
        // null VALUE is fine:
        stock.put("fig", null);
        System.out.println("null value OK    : " + stock.get("fig"));   // null
        // null KEY is not:
        try {
            stock.put(null, 1);
        } catch (NullPointerException npe) {
            System.out.println("null key -> NPE  : " + npe.getClass().getSimpleName());
        }

        // ── 8. TreeMap as a sorted counter / record-like use case ─────────────────
        // Ceiling/floor lookups make TreeMap ideal for "rank by score" boards:
        NavigableMap<Integer, String> rank = new TreeMap<>();
        rank.put(950, "Alice");
        rank.put(870, "Bob");
        rank.put(990, "Carol");
        System.out.println("Top score         : " + rank.lastEntry());
        System.out.println("Score ≥ 900       : " + rank.tailMap(900, true));
    }
}
