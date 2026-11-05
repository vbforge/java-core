package com.vbforge.jvm_diagnostic_tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Finding a memory leak — the classic causes and the method.
 *
 * <p>In Java a "leak" is not lost memory; it is memory that is still
 * <b>reachable</b> but will never be used again. The garbage collector is
 * working correctly — you are holding a reference you forgot about.
 *
 * <p>The method, in order:
 * <pre>
 *   1. Confirm: heap used AFTER a full GC keeps climbing (staircase, not sawtooth)
 *   2. Locate:  two class histograms minutes apart — which class only grows?
 *   3. Explain: heap dump → "path to GC roots" for a sample instance
 *   4. Fix, then repeat step 1 to prove it
 * </pre>
 */
public class LeakHuntingDemo {

    public static void main(String[] args) {
        staticCollectionDemo();
        System.out.println("---");
        missingEqualsDemo();
        System.out.println("---");
        lapsedListenerDemo();
        System.out.println("---");
        retentionDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Leak #1 — the static collection that only grows
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   a static (or long-lived) Map/List used as a cache with no eviction.
    // Why:    it is the most common leak in real code. Every entry is
    //         reachable from a GC root (the static field), so none is ever freed.
    // Cost:   unbounded cache O(n) memory for n distinct keys;
    //         bounded LRU cache O(maxEntries) — put/get stay O(1).
    // Gotcha: a "cache" with no size limit is a leak with a friendly name.
    //         Fix: bound it (LinkedHashMap.removeEldestEntry, Caffeine, Guava)
    //         or hold values through SoftReference/WeakReference.

    /** A cache with no eviction. Looks harmless, grows forever. */
    public static class UnboundedCache<K, V> {
        private final Map<K, V> entries = new HashMap<>();

        public void put(K key, V value) {
            entries.put(key, value);
        }

        public int size() {
            return entries.size();
        }
    }

    /** LRU cache: the least recently used entry is dropped once {@code maxEntries} is exceeded. */
    public static class BoundedCache<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;
        private final int maxEntries;

        public BoundedCache(int maxEntries) {
            super(16, 0.75f, true);
            this.maxEntries = maxEntries;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxEntries;
        }
    }

    /** Puts {@code n} distinct keys into both caches; returns {unboundedSize, boundedSize}. */
    public static int[] fillCaches(int n, int boundedLimit) {
        UnboundedCache<Integer, String> unbounded = new UnboundedCache<>();
        BoundedCache<Integer, String> bounded = new BoundedCache<>(boundedLimit);
        for (int i = 0; i < n; i++) {
            unbounded.put(i, "value-" + i);
            bounded.put(i, "value-" + i);
        }
        return new int[]{unbounded.size(), bounded.size()};
    }

    static void staticCollectionDemo() {
        int[] sizes = fillCaches(10_000, 100);
        System.out.println("unbounded cache entries: " + sizes[0]);
        System.out.println("bounded cache entries  : " + sizes[1]);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. Leak #2 — keys without equals()/hashCode()
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   a HashMap key class that inherits identity-based equals/hashCode.
    // Why:    every lookup with a fresh-but-equal key MISSES, so code of the
    //         form "if (!map.containsKey(k)) map.put(k, ...)" adds a new
    //         entry every time. The map grows, no exception is thrown.
    // Cost:   O(requests) memory instead of O(distinct keys).
    // Gotcha: in a histogram this shows up as millions of instances of YOUR
    //         key class — a very recognisable signature.
    //         (See the equals-hashcode-and-object-contract module.)

    /** Key WITHOUT equals/hashCode: two instances with the same id are different keys. */
    public static final class BadKey {
        final String id;

        public BadKey(String id) {
            this.id = id;
        }
    }

    /** Key WITH equals/hashCode: equal ids are the same key. */
    public static final class GoodKey {
        final String id;

        public GoodKey(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof GoodKey && Objects.equals(id, ((GoodKey) o).id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    /** Registers the same logical key {@code repeats} times; returns {badKeyMapSize, goodKeyMapSize}. */
    public static int[] countEntriesForRepeatedKey(int repeats) {
        Map<BadKey, Integer> bad = new HashMap<>();
        Map<GoodKey, Integer> good = new HashMap<>();
        for (int i = 0; i < repeats; i++) {
            bad.put(new BadKey("user-1"), i);
            good.put(new GoodKey("user-1"), i);
        }
        return new int[]{bad.size(), good.size()};
    }

    static void missingEqualsDemo() {
        int[] sizes = countEntriesForRepeatedKey(1_000);
        System.out.println("same key 1000x, no equals  : " + sizes[0] + " entries");
        System.out.println("same key 1000x, with equals: " + sizes[1] + " entry");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Leak #3 — the lapsed listener
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   an object registers a callback with a long-lived publisher and
    //         never unregisters it.
    // Why:    the publisher's list holds the callback; the callback (a lambda
    //         or inner class) holds its enclosing object — and everything THAT
    //         references. One forgotten unregister can pin a whole screen,
    //         request context or session.
    // Cost:   O(registrations) memory; every event also calls dead listeners (CPU).
    // Gotcha: lambdas that touch an instance field capture "this". The leak is
    //         invisible in the source — only the heap dump's path to GC roots shows it.
    //         Fix: unregister in close()/finally, or keep listeners as WeakReferences.

    /** A long-lived publisher. */
    public static class ListenerRegistry {
        private final List<Runnable> listeners = new ArrayList<>();

        public void register(Runnable listener) {
            listeners.add(listener);
        }

        public boolean unregister(Runnable listener) {
            return listeners.remove(listener);
        }

        public int size() {
            return listeners.size();
        }

        public void fire() {
            for (Runnable listener : new ArrayList<>(listeners)) {
                listener.run();
            }
        }
    }

    static void lapsedListenerDemo() {
        ListenerRegistry registry = new ListenerRegistry();
        Runnable first = () -> { };
        Runnable second = () -> { };
        registry.register(first);
        registry.register(second);
        System.out.println("registered         : " + registry.size());
        registry.unregister(first);
        System.out.println("after unregister   : " + registry.size());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. Measuring retention — "used after GC"
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   force a GC, THEN read used memory. What remains is reachable.
    // Why:    "used" without a GC mixes live data and garbage. Used-after-GC
    //         is the only number that can prove or disprove a leak.
    // Cost:   System.gc() is a full, stop-the-world collection — fine for a
    //         diagnostic, never in a request path.
    // Gotcha: System.gc() is a request, not a command; -XX:+DisableExplicitGC
    //         turns it into a no-op. In production prefer
    //         "jcmd <pid> GC.run" or read the old-generation usage AFTER the
    //         JVM's own major GCs (visible in GC logs).

    private static volatile byte[][] retained;

    /** Heap bytes in use after (a best effort of) three full collections. */
    public static long usedAfterGc() {
        for (int i = 0; i < 3; i++) {
            System.gc();
        }
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static void holdMegabytes(int megabytes) {
        byte[][] data = new byte[megabytes][];
        for (int i = 0; i < megabytes; i++) {
            data[i] = new byte[1_000_000];
        }
        retained = data;
    }

    /** Returns {usedBeforeAllocation, usedWhileHeld, usedAfterRelease} in bytes. */
    public static long[] measureRetention(int megabytes) {
        long before = usedAfterGc();
        holdMegabytes(megabytes);
        long whileHeld = usedAfterGc();
        retained = null;
        long afterRelease = usedAfterGc();
        return new long[]{before, whileHeld, afterRelease};
    }

    static void retentionDemo() {
        long[] samples = measureRetention(10);
        System.out.println("before   : " + samples[0]);
        System.out.println("held 10MB: " + samples[1]);
        System.out.println("released : " + samples[2]);
    }
}
