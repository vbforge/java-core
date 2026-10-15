package com.vbforge.equals_hashcode_and_object_contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * HashCodeDemo — annotated reference for {@code Object.hashCode()} and hash collections.
 *
 * <p>Read top-to-bottom. Each section covers one facet of hashing with
 * concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>The contract — equal objects MUST have equal hash codes</li>
 *   <li>Overriding {@code equals} without {@code hashCode} — the lost element</li>
 *   <li>Collisions are legal but slow — hash quality</li>
 *   <li>How to write one — the 31 recipe, {@code Objects.hash}, {@code Arrays.hashCode}</li>
 *   <li>Mutable keys in a {@code HashMap} — the vanished entry</li>
 *   <li>Stability and caching — one run, one value</li>
 * </ol>
 */
public class HashCodeDemo {

    public static void main(String[] args) {
        contractDemo();
        System.out.println("---");
        missingHashCodeDemo();
        System.out.println("---");
        collisionsDemo();
        System.out.println("---");
        recipesDemo();
        System.out.println("---");
        mutableKeyDemo();
        System.out.println("---");
        stabilityDemo();
    }

    // ── 1. The contract ───────────────────────────────────────────────────────

    static final class Point {
        private final int x;
        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Point)) {
                return false;
            }
            Point other = (Point) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);                   // same fields as equals — and ONLY those
        }
    }

    /**
     * The contract of {@code hashCode}:
     * <ol>
     *   <li><b>consistent</b> — during one run, repeated calls return the same value as long as the
     *       fields used by {@code equals} do not change</li>
     *   <li><b>equal objects → equal hash codes</b> — if {@code a.equals(b)} then
     *       {@code a.hashCode() == b.hashCode()}</li>
     *   <li><b>unequal objects MAY share a hash code</b> — collisions are allowed (there are only
     *       2^32 ints and far more objects)</li>
     * </ol>
     *
     * <p>WHY this matters: a {@code HashMap} first computes the hash to pick a bucket, and only then
     * calls {@code equals} on the few entries in that bucket. If two equal keys hash differently,
     * they land in different buckets and the map never even compares them.
     *
     * <p>Rule of thumb: use exactly the fields {@code equals} uses — a field that is in
     * {@code hashCode} but not in {@code equals} breaks rule 2; a field in {@code equals} but not in
     * {@code hashCode} is merely a slower (but legal) hash.
     */
    static void contractDemo() {
        System.out.println("=== The contract ===");
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);
        System.out.println("equal objects         : " + a.equals(b));
        System.out.println("same hash code        : " + (a.hashCode() == b.hashCode()));
        System.out.println("(1,2) vs (2,1) hashes : " + new Point(1, 2).hashCode() + " vs " + new Point(2, 1).hashCode());
    }

    // ── 2. equals without hashCode ────────────────────────────────────────────

    /** BROKEN on purpose: overrides {@code equals}, inherits the identity {@code hashCode}. */
    @SuppressWarnings("overrides")   // intentional: this is the bug being demonstrated
    static final class EqualsOnly {
        final int id;

        EqualsOnly(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof EqualsOnly && ((EqualsOnly) o).id == id;
        }
    }

    /** Adds two EQUAL objects to a HashSet and returns its size (2 — the duplicate is not detected). */
    static int sizeWithoutHashCode() {
        Set<EqualsOnly> set = new HashSet<>();
        set.add(new EqualsOnly(1));
        set.add(new EqualsOnly(1));
        return set.size();
    }

    /** Looks up an object EQUAL to the stored one (false — it hashes to a different bucket). */
    static boolean containsWithoutHashCode() {
        Set<EqualsOnly> set = new HashSet<>();
        set.add(new EqualsOnly(1));
        return set.contains(new EqualsOnly(1));
    }

    /**
     * {@code EqualsOnly} says two instances with the same id are equal, but each instance keeps the
     * default identity hash code, so a {@code HashSet} files them in different buckets and never
     * calls {@code equals}. The set silently holds duplicates, and {@code contains} fails for an
     * object that is "obviously" there.
     *
     * <p>WHY this matters: this is the single most common {@code hashCode} bug. It shows up only
     * in hash-based collections ({@code HashMap}, {@code HashSet}, {@code Collectors.groupingBy},
     * {@code distinct()}); {@code ArrayList.contains} still works, which hides it in small tests.
     * (Strictly, the identity hashes could coincide — about one chance in two billion.)
     *
     * <p>Rule: override {@code equals} and {@code hashCode} TOGETHER, always.
     */
    static void missingHashCodeDemo() {
        System.out.println("=== equals without hashCode ===");
        System.out.println("set size after two equal adds : " + sizeWithoutHashCode());       // 2
        System.out.println("contains(equal object)        : " + containsWithoutHashCode());   // false
        List<EqualsOnly> list = new ArrayList<>();
        list.add(new EqualsOnly(1));
        System.out.println("ArrayList.contains (equals only): " + list.contains(new EqualsOnly(1)));   // true
    }

    // ── 3. Collisions: legal but slow ─────────────────────────────────────────

    /** Legal but terrible: every instance has the same hash code. */
    static final class ConstantHash {
        final int id;

        ConstantHash(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ConstantHash && ((ConstantHash) o).id == id;
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    /** A good spread: the id itself. */
    static final class SpreadHash {
        final int id;

        SpreadHash(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof SpreadHash && ((SpreadHash) o).id == id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    static int distinctHashes(Collection<?> items) {
        Set<Integer> hashes = new HashSet<>();
        for (Object item : items) {
            hashes.add(item.hashCode());
        }
        return hashes.size();
    }

    /** Puts {@code n} constant-hash objects into a HashSet and checks that every one is still found. */
    static boolean everyConstantHashFound(int n) {
        Set<ConstantHash> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            set.add(new ConstantHash(i));
        }
        for (int i = 0; i < n; i++) {
            if (!set.contains(new ConstantHash(i))) {
                return false;
            }
        }
        return set.size() == n;
    }

    /**
     * {@code hashCode() { return 42; }} satisfies the contract — every equal pair has equal hashes —
     * and every lookup still returns the right answer. What it destroys is PERFORMANCE: all entries
     * share one bucket, so {@code contains} degrades from O(1) to a scan of the whole bucket
     * (O(n), or O(log n) when the JDK converts a big bucket into a tree and the key is
     * {@code Comparable}).
     *
     * <p>WHY this matters: a bad hash is never a correctness bug, only a slowness bug — which is
     * why it survives code review. The goal is a good spread: different objects should usually
     * get different hash codes.
     *
     * <p>Common gotcha: hashing on a single low-variety field (a boolean flag, a "type" enum) —
     * thousands of objects, two buckets.
     */
    static void collisionsDemo() {
        System.out.println("=== Collisions: legal but slow ===");
        List<ConstantHash> constant = new ArrayList<>();
        List<SpreadHash> spread = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            constant.add(new ConstantHash(i));
            spread.add(new SpreadHash(i));
        }
        System.out.println("distinct hashes (constant) : " + distinctHashes(constant));   // 1
        System.out.println("distinct hashes (spread)   : " + distinctHashes(spread));     // 100
        System.out.println("constant-hash set still correct : " + everyConstantHashFound(500));
    }

    // ── 4. How to write one ───────────────────────────────────────────────────

    /** The classic formula, written out: {@code s[0]*31^(n-1) + … + s[n-1]}. */
    static int manualStringHash(String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = 31 * h + s.charAt(i);
        }
        return h;
    }

    /** Returns {@code {Objects.hash(1, 2), Arrays.hashCode(new int[]{1, 2}), List.of(1, 2).hashCode()}}. */
    static int[] sameRecipeThreeWays() {
        return new int[] {
                Objects.hash(1, 2),
                Arrays.hashCode(new int[] {1, 2}),
                List.of(1, 2).hashCode()
        };
    }

    /**
     * The recipe behind {@code String}, {@code List} and {@code Arrays.hashCode}: start from 1 (or 0)
     * and for every significant field do {@code result = 31 * result + fieldHash}. The prime 31 is
     * odd, so no information is lost by overflow, and {@code 31 * i == (i << 5) - i} is cheap.
     * The order of the fields matters, so {@code (1,2)} and {@code (2,1)} differ.
     *
     * <p>Per field type: {@code int} → itself; {@code long} → {@code Long.hashCode}; {@code double}
     * → {@code Double.hashCode} (matches {@code Double.compare} equality); {@code boolean} →
     * {@code Boolean.hashCode}; object → {@code Objects.hashCode(o)} ({@code 0} for {@code null});
     * array → {@code Arrays.hashCode} / {@code deepHashCode}.
     *
     * <p>In practice write {@code Objects.hash(f1, f2, ...)}. WHY not always: it boxes primitives
     * and allocates a varargs array on every call — fine for ordinary code, worth a hand-written
     * version only in a measured hot path.
     */
    static void recipesDemo() {
        System.out.println("=== How to write one ===");
        System.out.println("\"abc\".hashCode()          : " + "abc".hashCode());          // 96354
        System.out.println("manualStringHash(\"abc\")   : " + manualStringHash("abc"));   // 96354
        System.out.println("three recipes for (1, 2)  : " + Arrays.toString(sameRecipeThreeWays()));   // [994, 994, 994]
        System.out.println("Objects.hash()            : " + Objects.hash());              // 1
        System.out.println("Objects.hashCode(null)    : " + Objects.hashCode(null));      // 0
    }

    // ── 5. Mutable keys ───────────────────────────────────────────────────────

    static final class MutableKey {
        int id;

        MutableKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof MutableKey && ((MutableKey) o).id == id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    /** Returns {@code {map.get(key), map.containsKey(key), map.size(), map.containsValue("v")}} after the key mutates. */
    static Object[] lostKey() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "v");
        key.id = 2;                                      // changes the hash AFTER insertion
        return new Object[] {map.get(key), map.containsKey(key), map.size(), map.containsValue("v")};
    }

    /** The safe order: remove, mutate, re-insert. Returns what {@code get} finds afterwards. */
    static String removeMutateReinsert() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "v");
        map.remove(key);
        key.id = 2;
        map.put(key, "v");
        return map.get(key);
    }

    /**
     * A {@code HashMap} stores each entry together with the hash computed at insertion. If the key
     * mutates, {@code get(key)} computes a NEW hash, looks in a different bucket and finds nothing —
     * yet the entry is still counted in {@code size()} and still reachable through
     * {@code containsValue}. It is a memory leak that cannot be removed by key.
     *
     * <p>WHY this matters: no exception, no warning, just {@code null}. The same thing happens to
     * the elements of a {@code HashSet} and to the keys of a {@code TreeMap} whose ordering
     * changes.
     *
     * <p>Fix: make key classes immutable ({@code String}, {@code Integer}, records of immutable
     * parts) — or remove the entry BEFORE you mutate the key and re-insert it afterwards.
     */
    static void mutableKeyDemo() {
        System.out.println("=== Mutable keys ===");
        Object[] r = lostKey();
        System.out.println("get(key) after mutation       : " + r[0]);   // null
        System.out.println("containsKey                   : " + r[1]);   // false
        System.out.println("size                          : " + r[2]);   // 1 — the entry is still there
        System.out.println("containsValue(\"v\")            : " + r[3]);   // true
        System.out.println("remove → mutate → put → get   : " + removeMutateReinsert());   // v
    }

    // ── 6. Stability and caching ──────────────────────────────────────────────

    /** An immutable class that computes its hash once, like {@code String}. */
    static final class CachedHash {
        private final String text;
        private int hash;
        private boolean computed;
        private int computations;

        CachedHash(String text) {
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof CachedHash && text.equals(((CachedHash) o).text);
        }

        @Override
        public int hashCode() {
            if (!computed) {
                hash = text.hashCode();
                computed = true;
                computations++;
            }
            return hash;
        }

        int computations() {
            return computations;
        }
    }

    /**
     * A hash code must be stable within one run — but it need NOT be stable between runs. The
     * default identity hash code changes from run to run, so never persist it, send it over the
     * network or use it for ordering in a file.
     *
     * <p>WHY this matters: for an IMMUTABLE class the hash never changes, so it can be computed once
     * and cached ({@code String} does exactly that). Never cache the hash of a class with mutable
     * fields — the cached value would go stale and break the contract.
     *
     * <p>Common gotcha: iteration order of a {@code HashMap}/{@code HashSet} depends on hash codes —
     * it is unspecified and may differ between runs and JDK versions. Never rely on it.
     */
    static void stabilityDemo() {
        System.out.println("=== Stability and caching ===");
        CachedHash cached = new CachedHash("abc");
        cached.hashCode();
        cached.hashCode();
        cached.hashCode();
        System.out.println("computations after 3 calls : " + cached.computations());   // 1
        Object plain = new Object();
        System.out.println("identity hash stable       : " + (plain.hashCode() == plain.hashCode()));
    }
}
