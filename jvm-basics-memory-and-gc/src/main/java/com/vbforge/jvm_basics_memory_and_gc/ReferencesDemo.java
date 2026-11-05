package com.vbforge.jvm_basics_memory_and_gc;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * Strong, soft, weak and phantom references.
 *
 * <p>GC timing is up to the JVM, so the demos that need a collection call
 * {@link #gcUntilCleared(Reference, int)}, which retries {@code System.gc()} a bounded
 * number of times. Where a result must be deterministic we use {@link Reference#clear()},
 * which is exactly what the GC does to a reference it decides to reclaim.
 */
public class ReferencesDemo {

    public static void main(String[] args) throws InterruptedException {
        strongReferenceDemo();
        System.out.println("---");
        weakReferenceDemo();
        System.out.println("---");
        softReferenceCacheDemo();
        System.out.println("---");
        phantomReferenceDemo();
        System.out.println("---");
        weakHashMapDemo();
    }

    /**
     * Asks for a GC up to {@code attempts} times (sleeping 10 ms between tries) until the
     * reference is cleared. Returns {@code true} if it was cleared.
     */
    public static boolean gcUntilCleared(Reference<?> ref, int attempts) {
        for (int i = 0; i < attempts && ref.get() != null; i++) {
            System.gc();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return ref.get() == null;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Strong references
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: the ordinary `Object o = new Object();` reference.
     *
     * Why it matters: an object with at least one strong path from a GC root is NEVER
     *   collected — even if the JVM is about to throw OutOfMemoryError. The other three
     *   reference types exist to let you hold an object WITHOUT forcing it to stay.
     *
     * Common gotcha: a weak reference to an object that is also strongly referenced
     *   elsewhere is as good as a strong one — it will not be cleared.
     */
    public static void strongReferenceDemo() {
        System.out.println("weak ref survives while a strong ref exists: " + weakSurvivesWhileStronglyHeld());
    }

    /** Creates a weak reference, keeps the strong one, runs the GC and reports whether the referent survived. */
    public static boolean weakSurvivesWhileStronglyHeld() {
        Object strong = new Object();
        WeakReference<Object> weak = new WeakReference<>(strong);
        System.gc();
        boolean alive = weak.get() != null;
        Objects.requireNonNull(strong); // keeps `strong` reachable until here
        return alive;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. Weak references
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: WeakReference<T> does not keep its referent alive. At the next GC, if the
     *       object has no strong or soft path, the JVM clears the reference
     *       (get() starts returning null).
     *
     * Why it matters: canonical use is metadata ABOUT an object that must not extend
     *       its life — listener registries, canonicalising maps, ThreadLocal keys.
     *
     * Complexity: get() is O(1). Clearing happens during GC, not on a timer.
     *
     * Common gotcha: `if (ref.get() != null) ref.get().use()` is a race — the GC can
     *   clear it between the two calls. Read once into a local, then null-check it.
     */
    public static void weakReferenceDemo() {
        System.out.println("weak ref cleared after GC once unreferenced: " + weakClearedAfterGc());
        System.out.println("read-once pattern returns: " + readOnce(new WeakReference<>("value")));
    }

    /** Drops the only strong reference and returns whether a GC cleared the weak one. */
    public static boolean weakClearedAfterGc() {
        Object strong = new Object();
        WeakReference<Object> weak = new WeakReference<>(strong);
        strong = null;
        return gcUntilCleared(weak, 20);
    }

    /** The safe idiom: one {@code get()}, stored in a local. Returns {@code "gone"} when cleared. */
    public static String readOnce(WeakReference<String> ref) {
        String value = ref.get();
        return value != null ? value : "gone";
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Soft references
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: like weak, but the JVM keeps soft referents as long as there is no memory
     *       pressure. HotSpot guarantees every soft reference is cleared BEFORE it throws
     *       OutOfMemoryError. A ref is also cleared if it has not been read for
     *       roughly (free heap in MB × -XX:SoftRefLRUPolicyMSPerMB, default 1000 ms).
     *
     * Why it matters: ideal for memory-sensitive caches — you keep the data while there
     *       is room and automatically give it back when the heap is tight.
     *
     * Common gotcha: soft caches make GC behaviour hard to predict and keep the heap
     *       full, so they can lengthen GC pauses. For real caches prefer a bounded one
     *       (size-limited LRU) and use soft references only as a safety valve.
     */
    public static void softReferenceCacheDemo() {
        SoftCache<String, String> cache = new SoftCache<>();
        Function<String, String> loader = key -> key.toUpperCase();
        System.out.println(cache.get("a", loader) + " loads=" + cache.loads());
        System.out.println(cache.get("a", loader) + " loads=" + cache.loads() + "  (cache hit)");
        cache.simulateMemoryPressure();
        System.out.println(cache.get("a", loader) + " loads=" + cache.loads() + "  (cleared, reloaded)");
    }

    /** A cache whose values may be reclaimed by the GC. Reloads transparently when cleared. */
    public static class SoftCache<K, V> {
        private final Map<K, SoftReference<V>> map = new HashMap<>();
        private int loads;

        public V get(K key, Function<K, V> loader) {
            SoftReference<V> ref = map.get(key);
            V value = ref == null ? null : ref.get();
            if (value == null) {
                value = loader.apply(key);
                loads++;
                map.put(key, new SoftReference<>(value));
            }
            return value;
        }

        public int loads() {
            return loads;
        }

        /** Clears every reference — what the GC does to soft references before an OutOfMemoryError. */
        public void simulateMemoryPressure() {
            for (SoftReference<V> ref : map.values()) {
                ref.clear();
            }
        }
    }

    /** A normal GC with plenty of free heap does not reclaim a freshly created soft reference. */
    public static boolean softSurvivesNormalGc() {
        byte[] payload = new byte[1024];
        SoftReference<byte[]> soft = new SoftReference<>(payload);
        payload = null;
        System.gc();
        return soft.get() != null;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. Phantom references and ReferenceQueue
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: PhantomReference.get() ALWAYS returns null — you can never resurrect the
     *       object. Its only job is notification: once the object is unreachable, the
     *       reference is added to its ReferenceQueue so you can run cleanup afterwards.
     *
     * Why it matters: it is the safe replacement for finalize() (deprecated for
     *       removal since Java 18). java.lang.ref.Cleaner is built on this mechanism.
     *       finalize() can resurrect objects and delays reclamation by an extra GC cycle.
     *
     * Common gotcha: the PhantomReference object itself must stay strongly reachable,
     *       or it is collected before it can be enqueued.
     */
    public static void phantomReferenceDemo() throws InterruptedException {
        System.out.println("phantom get() always null: " + phantomGetAlwaysNull());
        System.out.println("phantom enqueued after GC: " + phantomEnqueuedAfterGc());
    }

    public static boolean phantomGetAlwaysNull() {
        Object referent = new Object();
        PhantomReference<Object> phantom = new PhantomReference<>(referent, new ReferenceQueue<>());
        boolean isNull = phantom.get() == null;
        Objects.requireNonNull(referent);
        return isNull;
    }

    /** Drops the referent, runs the GC and waits for the phantom to appear on its queue. */
    public static boolean phantomEnqueuedAfterGc() throws InterruptedException {
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        Object referent = new Object();
        PhantomReference<Object> phantom = new PhantomReference<>(referent, queue);
        referent = null;
        Reference<?> polled = null;
        for (int i = 0; i < 20 && polled == null; i++) {
            System.gc();
            polled = queue.remove(50);
        }
        return polled == phantom;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. WeakHashMap
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: a Map whose KEYS are held by weak references. When a key is no longer
     *       strongly reachable elsewhere, its entry silently disappears.
     *
     * Why it matters: attach extra data to objects you do not own without leaking
     *       them (e.g. per-object caches).
     *
     * Common gotchas:
     *   - a value that strongly references its own key keeps the entry alive forever
     *   - interned strings / literals / cached boxed Integers are strongly reachable
     *     from the JVM, so such keys are never evicted
     */
    public static void weakHashMapDemo() {
        int[] sizes = weakHashMapEvictsUnreferencedKey();
        System.out.println("size before/after dropping key: " + sizes[0] + " / " + sizes[1]);
        System.out.println("literal key stays after GC    : " + weakHashMapLiteralKeyStays());
    }

    /** Returns {@code {sizeBefore, sizeAfter}} after dropping the only strong reference to the key. */
    public static int[] weakHashMapEvictsUnreferencedKey() {
        Map<Object, String> map = new WeakHashMap<>();
        Object key = new Object();
        map.put(key, "value");
        int before = map.size();
        key = null;
        for (int i = 0; i < 20 && !map.isEmpty(); i++) {
            System.gc();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return new int[]{before, map.size()};
    }

    /** A string literal is interned (held by the JVM), so its WeakHashMap entry never disappears. */
    public static boolean weakHashMapLiteralKeyStays() {
        Map<String, String> map = new WeakHashMap<>();
        map.put("literal-key", "value");
        for (int i = 0; i < 3; i++) {
            System.gc();
        }
        return map.size() == 1;
    }
}
