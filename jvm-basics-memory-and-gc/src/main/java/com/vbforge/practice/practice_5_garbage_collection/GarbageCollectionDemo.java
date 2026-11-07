package com.vbforge.practice.practice_5_garbage_collection;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Garbage Collection — how the JVM automatically reclaims unreachable objects.
 *
 * Core principle: an object is "live" if it is reachable from a GC root.
 *
 * GC Roots (always considered live):
 *   - Local variables on any thread's stack
 *   - Static fields
 *   - Active Java threads
 *   - JNI references
 *
 * Mark-and-Sweep algorithm (conceptual basis of most GC):
 *   Phase 1 — MARK:  walk from GC roots, mark all reachable objects
 *   Phase 2 — SWEEP: reclaim all unmarked (unreachable) objects
 *   Phase 3 — (some GCs) COMPACT: defragment heap to avoid fragmentation
 *
 * Generational Hypothesis:
 *   Most objects die young ("infant mortality").
 *   The JVM exploits this by splitting the heap:
 *
 *   ┌────────────────────────────────────────────────────────────┐
 *   │  YOUNG GENERATION         │  OLD GENERATION (Tenured)     │
 *   ├───────────────────────────┼────────────────────────────────┤
 *   │  Eden                     │  Objects that survived many    │
 *   │  Survivor S0 (from)       │  Minor GCs are promoted here  │
 *   │  Survivor S1 (to)         │  Major GC / Full GC runs here │
 *   └───────────────────────────┴────────────────────────────────┘
 *
 * GC Types (Java 11+):
 *   • Serial GC      (-XX:+UseSerialGC)       — single-threaded, stop-the-world
 *   • Parallel GC    (-XX:+UseParallelGC)      — throughput-focused, multi-threaded
 *   • G1 GC          (-XX:+UseG1GC)            — balanced, default since Java 9
 *   • ZGC            (-XX:+UseZGC)             — ultra-low-latency, Java 15+ production
 *   • Shenandoah     (-XX:+UseShenandoahGC)    — low-pause, OpenJDK
 *
 * Reference Types (strength matters for GC):
 *   Strong  → default; object never GC'd while strong ref exists
 *   Soft    → GC'd only when JVM is low on memory (good for caches)
 *   Weak    → GC'd on next collection regardless of memory pressure (WeakHashMap)
 *   Phantom → object already finalized; used for cleanup hooks
 *
 * Key JVM flags:
 *   -Xms512m          — initial heap size
 *   -Xmx2g            — maximum heap size
 *   -XX:NewRatio=3    — ratio of old:young generation sizes
 *   -verbose:gc       — print GC log to stdout
 *   -XX:+PrintGCDetails — detailed GC diagnostics
 */
public class GarbageCollectionDemo {

    private final String name;

    public GarbageCollectionDemo(String name) {
        this.name = name;
    }

    @Override
    protected void finalize() throws Throwable {
        // finalize() is called by GC before reclaiming the object.
        // WARNING: deprecated since Java 9, removed in Java 18.
        // Unreliable: timing is non-deterministic; may never be called at all.
        // Use java.lang.ref.Cleaner or try-with-resources instead.
        System.out.println("finalize() called for: " + name);
        super.finalize();
    }

    // -----------------------------------------------------------------------
    // Reference strength demonstrations
    // -----------------------------------------------------------------------

    /**
     * Demonstrates SoftReference — GC respects it until memory is needed.
     * Ideal for memory-sensitive caches: entries are dropped automatically
     * under memory pressure, preventing OOM while still improving performance.
     */
    public static void softReferenceDemo() {
        System.out.println("=== SoftReference Demo ===");

        SoftReference<String> softRef = new SoftReference<>("I am softly referenced");
        System.out.println("Before GC hint — softRef.get(): " + softRef.get()); // non-null

        System.gc(); // JVM may or may not collect — typically won't when memory is available
        System.out.println("After System.gc() — softRef.get(): " + softRef.get()); // likely still non-null
    }

    /**
     * Demonstrates WeakReference — eligible for GC at any collection cycle.
     * The object is reclaimed even when memory is plentiful.
     * Used by WeakHashMap, thread-local caches, canonicalization tables.
     */
    public static String weakReferenceDemo() {
        System.out.println("\n=== WeakReference Demo ===");

        String strong = new String("weakly held"); // heap string (not interned)
        WeakReference<String> weakRef = new WeakReference<>(strong);

        System.out.println("Before null — weakRef.get(): " + weakRef.get()); // non-null

        strong = null; // remove only strong reference — object now weakly reachable
        System.gc();   // suggest GC — weakly reachable objects should be collected

        // After GC: weakRef.get() may be null (collected) or non-null (GC hasn't run yet)
        String afterGC = weakRef.get();
        System.out.println("After null + GC — weakRef.get(): " + afterGC);
        return afterGC; // null in most JVM runs
    }

    /**
     * Demonstrates PhantomReference — the object has already been finalized.
     * PhantomReference.get() always returns null; used purely for cleanup notification.
     * The ReferenceQueue is notified when the referent is about to be reclaimed.
     *
     * Modern alternative: java.lang.ref.Cleaner (Java 9+)
     */
    public static void phantomReferenceDemo() {
        System.out.println("\n=== PhantomReference Demo ===");

        ReferenceQueue<Object> queue    = new ReferenceQueue<>();
        Object                 referent = new Object();
        PhantomReference<Object> phantom = new PhantomReference<>(referent, queue);

        System.out.println("phantom.get() always null: " + phantom.get()); // always null by spec

        referent = null;
        System.gc();

        // In production: poll the ReferenceQueue on a dedicated thread
        // to perform cleanup (close resources, log, etc.)
        System.out.println("PhantomReference enqueued: " + phantom.isEnqueued());
    }

    // -----------------------------------------------------------------------
    // Generational GC simulation (conceptual — we can't truly control GC)
    // -----------------------------------------------------------------------

    /**
     * Simulates short-lived object creation (Eden → collected quickly).
     * In practice, these objects would be collected in Minor GC without
     * ever reaching the Old Generation.
     *
     * @param count number of short-lived objects to create
     */
    public static void shortLivedObjectsDemo(int count) {
        System.out.println("\n=== Short-lived Objects (Young Gen) ===");
        long totalLength = 0;
        for (int i = 0; i < count; i++) {
            String temp = "temp_" + i; // dies at end of each loop iteration
            totalLength += temp.length();
        }
        System.out.println("Created " + count + " short-lived strings; total length: " + totalLength);
        // After this method: all 'temp' strings are GC-eligible
    }

    /**
     * Creates a list of objects that survive multiple GC cycles
     * and would eventually be promoted to Old Generation.
     *
     * In real JVM: objects surviving -XX:MaxTenuringThreshold (default 15) Minor GCs
     * are promoted to Old Gen.
     *
     * @param count objects to retain
     * @return live list (prevents GC of elements while returned reference is alive)
     */
    public static List<GarbageCollectionDemo> longLivedObjectsDemo(int count) {
        System.out.println("\n=== Long-lived Objects (Old Gen candidates) ===");
        List<GarbageCollectionDemo> survivors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            survivors.add(new GarbageCollectionDemo("Survivor-" + i));
        }
        System.out.println("Retained " + survivors.size() + " long-lived objects");
        return survivors; // returned → still reachable → not GC-eligible
    }

    public String getName() { return name; }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Garbage Collection Demo ===\n");

        softReferenceDemo();
        weakReferenceDemo();
        phantomReferenceDemo();
        shortLivedObjectsDemo(100);

        List<GarbageCollectionDemo> longLived = longLivedObjectsDemo(5);
        System.out.println("Still alive: " + longLived.get(0).getName());

        // Let go — now GC-eligible
        longLived = null;
        System.gc();
        Thread.sleep(100); // give GC a moment (not guaranteed)
        System.out.println("\nLong-lived list released — objects eligible for GC");
    }
}
