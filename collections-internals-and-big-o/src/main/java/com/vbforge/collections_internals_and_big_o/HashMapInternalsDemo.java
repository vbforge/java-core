package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.mini.MiniHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates how {@link HashMap} works inside: an ARRAY OF BUCKETS, a hash function that picks the bucket,
 * and a chain of nodes for keys that land in the same bucket.
 *
 * <p>The real table is private, so most demos use {@link MiniHashMap} — a small re-implementation of the same
 * mechanics with inspection methods. Where the real {@code HashMap} reveals its structure through its ITERATION
 * ORDER (which is bucket order), the demos use the real class.
 *
 * <p>Complexity: {@code get}/{@code put}/{@code remove}/{@code containsKey} are O(1) on AVERAGE. With a terrible
 * {@code hashCode} every key lands in one bucket and a lookup degrades to O(n) (O(log n) for the real map once a
 * long chain is turned into a tree, see section 5).
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code HashMapInternalsDemoTest}
 * can verify every claim.
 */
public class HashMapInternalsDemo {

    // ── 1. From key to bucket: spread + mask ──────────────────────────────────
    /**
     * The bucket index is {@code spread(key.hashCode()) & (tableSize - 1)}, where
     * {@code spread(h) = h ^ (h >>> 16)}. The table size is ALWAYS a power of two, so {@code & (size - 1)} is a
     * cheap replacement for {@code % size}. A {@code null} key has hash 0 and always lives in bucket 0.
     */
    public static int bucketIndexDemo(Object key, int tableSize) {
        int hash = key == null ? 0 : MiniHashMap.spread(key.hashCode());
        return hash & (tableSize - 1);
    }

    /**
     * Without spreading, the mask looks only at the LOW bits of {@code hashCode()}: hash codes that differ only
     * in their high bits would all collide. That is why the real map mixes the high 16 bits into the low ones first.
     */
    public static int bucketIndexWithoutSpreadDemo(Object key, int tableSize) {
        return key.hashCode() & (tableSize - 1);
    }

    /** {@code HashMap} accepts ONE {@code null} key (it goes to bucket 0). */
    public static String nullKeyDemo() {
        Map<String, String> map = new HashMap<>();
        map.put(null, "stored under null");
        return map.get(null);
    }


    // ── 2. Collisions: a chain per bucket ─────────────────────────────────────
    /**
     * Different keys can share a bucket. 1, 17 and 33 all give index 1 in a table of 16
     * (their low four bits are the same), so they form a chain of length 3.
     */
    public static int longestChainDemo(int... keys) {
        MiniHashMap<Integer, String> map = new MiniHashMap<>();
        for (int key : keys) {
            map.put(key, "v" + key);
        }
        return map.longestChain();
    }

    /** {@code "Aa"} and {@code "BB"} are different Strings with the SAME hashCode (2112). */
    public static boolean sameHashCodeDemo() {
        return "Aa".hashCode() == "BB".hashCode();
    }

    /**
     * A hash collision is only a hint to compare with {@code equals}: both keys are stored and found separately.
     * Returns {@code [size, get("Aa"), get("BB")]}.
     */
    public static List<Integer> distinctKeysWithSameHashDemo() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Aa", 1);
        map.put("BB", 2);
        List<Integer> result = new ArrayList<>();
        result.add(map.size());
        result.add(map.get("Aa"));
        result.add(map.get("BB"));
        return result;
    }


    // ── 3. Load factor and resizing ───────────────────────────────────────────
    /**
     * The table is allocated lazily (size 0 before the first {@code put}) with 16 buckets. When the number of
     * entries exceeds {@code capacity * 0.75} (the LOAD FACTOR) the table DOUBLES: 12 → 13th entry grows to 32,
     * 24 → 25th entry grows to 64, and so on.
     */
    public static int tableSizeAfterDemo(int entries) {
        MiniHashMap<Integer, String> map = new MiniHashMap<>();
        for (int i = 0; i < entries; i++) {
            map.put(i, "v");
        }
        return map.tableSize();
    }

    /** How many times the table doubled while inserting {@code entries} entries into a default map. */
    public static int resizesWithoutPresizingDemo(int entries) {
        MiniHashMap<Integer, String> map = new MiniHashMap<>();
        for (int i = 0; i < entries; i++) {
            map.put(i, "v");
        }
        return map.resizeCount();
    }

    /**
     * If you know the number of entries, ask for enough capacity up front: {@code expected / 0.75 + 1} buckets
     * (rounded up to a power of two) means the table never has to double. Every resize re-distributes ALL entries — O(n).
     */
    public static int resizesWithPresizingDemo(int entries) {
        MiniHashMap<Integer, String> map = new MiniHashMap<>((int) (entries / 0.75f) + 1);
        for (int i = 0; i < entries; i++) {
            map.put(i, "v");
        }
        return map.resizeCount();
    }


    // ── 4. Iteration order reveals the buckets (real HashMap) ─────────────────
    /**
     * Iterating a {@code HashMap} walks the table bucket by bucket, each chain from head to tail. The keys 33, 1, 17
     * all share bucket 1 in a table of 16, so they appear in INSERTION order, followed by the keys of buckets 2..10.
     * This map holds 12 entries — exactly the threshold, so it has NOT resized yet.
     */
    public static List<Integer> orderBeforeResizeDemo() {
        return new ArrayList<>(buildOrderExperiment(10).keySet());
    }

    /**
     * One more entry (13 in total) makes the table grow to 32 buckets and every key is re-distributed. 17 now has
     * its own bucket (17 &amp; 31 = 17) and moves behind everything else; 33 and 1 still share bucket 1.
     * <b>Gotcha:</b> the order of a HashMap/HashSet is an implementation detail that changes with its size — never rely on it.
     */
    public static List<Integer> orderAfterResizeDemo() {
        return new ArrayList<>(buildOrderExperiment(11).keySet());
    }

    private static Map<Integer, String> buildOrderExperiment(int lastSmallKey) {
        Map<Integer, String> map = new HashMap<>();
        map.put(33, "a");
        map.put(1, "b");
        map.put(17, "c");
        for (int key = 2; key <= lastSmallKey; key++) {
            map.put(key, "v");
        }
        return map;
    }

    /** The iteration order of {@link MiniHashMap} for the same keys — it matches the real map (see the tests). */
    public static List<Integer> miniIterationOrderDemo(int... keys) {
        MiniHashMap<Integer, String> map = new MiniHashMap<>();
        for (int key : keys) {
            map.put(key, "v");
        }
        return map.keysInIterationOrder();
    }


    // ── 5. Treeification ──────────────────────────────────────────────────────
    /**
     * The real {@code HashMap} protects itself against long chains. Constants from its source:
     * {@code TREEIFY_THRESHOLD = 8} and {@code MIN_TREEIFY_CAPACITY = 64}. When a node is added to a bin that already
     * holds 8 or more nodes:
     * <ul>
     *   <li>if the table has fewer than 64 buckets, the table is RESIZED instead (more buckets spread the keys out)</li>
     *   <li>otherwise the chain is converted into a red-black tree, so a lookup costs O(log n) instead of O(n)</li>
     * </ul>
     * Returns "none", "resize" or "treeify".
     */
    public static String treeifyDecisionDemo(int nodesInBinBeforeInsert, int tableSize) {
        if (nodesInBinBeforeInsert < 8) {
            return "none";
        }
        return tableSize < 64 ? "resize" : "treeify";
    }


    // ── 6. A bad hashCode destroys the O(1) ───────────────────────────────────
    /** A key whose {@code hashCode} is the same constant for every instance — the worst possible hash function. */
    public static final class ConstantHashKey {
        private final int id;

        public ConstantHashKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof ConstantHashKey && ((ConstantHashKey) other).id == id;
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    /**
     * All {@code n} keys land in ONE bucket, so finding the last one means walking the whole chain:
     * {@code n} nodes examined — O(n).
     */
    public static int probesWithConstantHashDemo(int n) {
        MiniHashMap<ConstantHashKey, Integer> map = new MiniHashMap<>();
        ConstantHashKey last = null;
        for (int i = 0; i < n; i++) {
            last = new ConstantHashKey(i);
            map.put(last, i);
        }
        return map.probesForGet(last);
    }

    /** With evenly spread hash codes the same lookup examines a single node — O(1). */
    public static int probesWithGoodHashDemo(int n) {
        MiniHashMap<Integer, Integer> map = new MiniHashMap<>();
        for (int i = 0; i < n; i++) {
            map.put(i, i);
        }
        return map.probesForGet(n - 1);
    }


    // ── 7. Gotcha: mutating a key after insertion ─────────────────────────────
    /** A key whose hash code depends on a MUTABLE field. */
    public static final class MutableKey {
        private int id;

        public MutableKey(int id) {
            this.id = id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof MutableKey && ((MutableKey) other).id == id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     * <b>Gotcha:</b> the entry was filed in the bucket of the OLD hash code. After the key is mutated a lookup goes to the
     * bucket of the NEW hash code and finds nothing — the entry is still inside the map, but unreachable.
     */
    public static String lookupAfterMutationDemo() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "value");
        key.setId(2);
        return map.get(key);
    }

    /** Restoring the old state makes the entry reachable again. */
    public static String lookupAfterRestoringDemo() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "value");
        key.setId(2);
        key.setId(1);
        return map.get(key);
    }

    /** The mutated key's entry still counts towards {@code size()} — a classic memory-leak pattern. */
    public static int sizeAfterMutationDemo() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "value");
        key.setId(2);
        return map.size();
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. from key to bucket ===");
        System.out.println("bucket of 65536 with spread:    " + bucketIndexDemo(65536, 16));
        System.out.println("bucket of 65536 without spread: " + bucketIndexWithoutSpreadDemo(65536, 16));
        System.out.println("bucket of null:                 " + bucketIndexDemo(null, 16));
        System.out.println("null key lookup:                " + nullKeyDemo());

        System.out.println("---");
        System.out.println("=== 2. collisions ===");
        System.out.println("longest chain for 1, 17, 33: " + longestChainDemo(1, 17, 33));
        System.out.println("\"Aa\" and \"BB\" share a hashCode: " + sameHashCodeDemo()
                + " -> " + distinctKeysWithSameHashDemo());

        System.out.println("---");
        System.out.println("=== 3. load factor and resize ===");
        for (int entries : new int[]{0, 1, 12, 13, 24, 25, 49}) {
            System.out.println(entries + " entries -> table size " + tableSizeAfterDemo(entries));
        }
        System.out.println("100 entries, default map : " + resizesWithoutPresizingDemo(100) + " resizes");
        System.out.println("100 entries, presized map: " + resizesWithPresizingDemo(100) + " resizes");

        System.out.println("---");
        System.out.println("=== 4. iteration order ===");
        System.out.println("12 entries: " + orderBeforeResizeDemo());
        System.out.println("13 entries: " + orderAfterResizeDemo());

        System.out.println("---");
        System.out.println("=== 5. treeification ===");
        System.out.println("7 nodes, table 16 : " + treeifyDecisionDemo(7, 16));
        System.out.println("8 nodes, table 16 : " + treeifyDecisionDemo(8, 16));
        System.out.println("8 nodes, table 64 : " + treeifyDecisionDemo(8, 64));

        System.out.println("---");
        System.out.println("=== 6. bad hashCode ===");
        System.out.println("constant hash, 1000 keys: " + probesWithConstantHashDemo(1000) + " probes");
        System.out.println("good hash,     1000 keys: " + probesWithGoodHashDemo(1000) + " probe");

        System.out.println("---");
        System.out.println("=== 7. mutable key ===");
        System.out.println("after mutation:  " + lookupAfterMutationDemo() + " (size " + sizeAfterMutationDemo() + ")");
        System.out.println("after restoring: " + lookupAfterRestoringDemo());
    }
}
