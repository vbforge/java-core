package com.vbforge.jvm_basics_memory_and_gc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * OutOfMemoryError: what the messages mean and which code patterns cause them.
 *
 * <p>Real exhaustion would make a demo (and CI) unstable, so most sections reproduce the
 * <em>mechanism</em> of a leak deterministically with counters, and only the cheap
 * "array too large" case really throws.
 */
public class OomCausesDemo {

    /** The OutOfMemoryError families and what to do about each. */
    public enum OomKind {
        HEAP("Java heap space",
                "Live set exceeds -Xmx: find the leak in a heap dump, or raise -Xmx if the load is genuine"),
        GC_OVERHEAD("GC overhead limit exceeded",
                "JVM spends >98 % of time in GC and frees <2 %: same family as HEAP - a leak or a heap that is far too small"),
        METASPACE("Metaspace",
                "Too many classes: usually a class-loader leak (redeploys, dynamic proxies); cap with -XX:MaxMetaspaceSize while debugging"),
        NATIVE_THREAD("unable to create native thread",
                "Too many threads or an OS limit (ulimit -u, container pids): bound pools, shrink -Xss"),
        ARRAY_LIMIT("Requested array size exceeds VM limit",
                "A single array is larger than the VM can allocate: a size/overflow bug in the code"),
        DIRECT_BUFFER("Direct buffer memory",
                "Off-heap ByteBuffers exhausted: release buffers, tune -XX:MaxDirectMemorySize"),
        UNKNOWN("", "Read the full message and the stack trace");

        private final String marker;
        private final String advice;

        OomKind(String marker, String advice) {
            this.marker = marker;
            this.advice = advice;
        }

        public String marker() {
            return marker;
        }

        public String advice() {
            return advice;
        }
    }

    public static void main(String[] args) throws Exception {
        classificationDemo();
        System.out.println("---");
        hugeArrayDemo();
        System.out.println("---");
        staticCollectionLeakDemo();
        System.out.println("---");
        mutableKeyLeakDemo();
        System.out.println("---");
        threadLocalLeakDemo();
        System.out.println("---");
        diagnosticFlagsDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Reading the message
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: the text after "java.lang.OutOfMemoryError:" says WHICH resource ran out.
     *
     * Why it matters: "OOM" is not one problem. Heap, metaspace, native threads and
     *       direct memory need completely different fixes; raising -Xmx helps only one.
     *
     * Common gotcha: raising -Xmx on a genuine leak only delays the crash.
     */
    public static void classificationDemo() {
        String[] messages = {
                "Java heap space",
                "GC overhead limit exceeded",
                "Metaspace",
                "unable to create native thread: possibly out of memory or process/resource limits reached",
                "Requested array size exceeds VM limit",
                "Direct buffer memory",
                "something else"};
        for (String message : messages) {
            OomKind kind = classify(message);
            System.out.println(kind + " <- \"" + message + "\"");
            System.out.println("    " + kind.advice());
        }
    }

    /** Maps an OutOfMemoryError message to its family; {@code null} or unmatched gives UNKNOWN. */
    public static OomKind classify(String message) {
        if (message == null) {
            return OomKind.UNKNOWN;
        }
        for (OomKind kind : OomKind.values()) {
            if (kind != OomKind.UNKNOWN && message.contains(kind.marker())) {
                return kind;
            }
        }
        return OomKind.UNKNOWN;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. An allocation that can never succeed
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: asking for an array near Integer.MAX_VALUE elements fails immediately —
     *       the JVM cannot satisfy it, whatever the free heap is.
     *
     * Why it matters: the cheap, deterministic way to see a real OutOfMemoryError.
     *       In production the same thing comes from trusting an untrusted length field.
     *
     * Common gotcha: OutOfMemoryError is an Error — catching it is for demos and for
     *       last-ditch logging, not for recovery. State may be inconsistent.
     */
    private static volatile Object sink;

    public static void hugeArrayDemo() {
        System.out.println("message: " + provokeOutOfMemory());
    }

    /** Tries to allocate {@code long[Integer.MAX_VALUE]} and returns the error message. */
    public static String provokeOutOfMemory() {
        try {
            sink = new long[Integer.MAX_VALUE];
            return "no error";
        } catch (OutOfMemoryError expected) {
            return expected.getMessage();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Leak: unbounded static collection
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: a static field is a GC root. Anything added to a static collection and never
     *       removed is reachable for the life of the class loader — the number-one
     *       Java "memory leak" (reachable but no longer needed).
     *
     * Why it matters: every request adds a bit; the heap fills slowly, then Full GCs
     *       become back-to-back, then Java heap space.
     *
     * Complexity: leaking = O(requests) retained memory. A bounded LRU cache keeps
     *       O(maxEntries).
     *
     * Common gotcha: the leak is not "in" the collection — it is the missing eviction.
     */
    private static final List<byte[]> LEAKY_REGISTRY = new ArrayList<>();

    public static void staticCollectionLeakDemo() {
        clearLeakyRegistry();
        System.out.println("leaky registry after 1000 requests   : " + leakyRegistrySizeAfter(1000));
        System.out.println("bounded cache (max 100) after 1000   : " + boundedCacheSizeAfter(1000, 100));
        clearLeakyRegistry();
    }

    /** Simulates {@code requests} calls that each register a payload in a static list. */
    public static int leakyRegistrySizeAfter(int requests) {
        for (int i = 0; i < requests; i++) {
            LEAKY_REGISTRY.add(new byte[16]);
        }
        return LEAKY_REGISTRY.size();
    }

    public static void clearLeakyRegistry() {
        LEAKY_REGISTRY.clear();
    }

    /** The same workload against an access-ordered LRU map that evicts beyond {@code maxEntries}. */
    public static int boundedCacheSizeAfter(int requests, int maxEntries) {
        Map<Integer, byte[]> cache = new LinkedHashMap<Integer, byte[]>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, byte[]> eldest) {
                return size() > maxEntries;
            }
        };
        for (int i = 0; i < requests; i++) {
            cache.put(i, new byte[16]);
        }
        return cache.size();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. Leak: mutated hash key
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: if a key's hashCode() depends on a field you later change, the entry sits in
     *       the wrong bucket: get() and remove() cannot find it, but the map still
     *       holds it strongly.
     *
     * Why it matters: you can no longer remove the entry — it is a perfect leak.
     *
     * Common gotcha: use immutable keys (String, Integer, records of immutables).
     */
    static final class MutableKey {
        int id;

        MutableKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof MutableKey && ((MutableKey) other).id == id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    public static void mutableKeyLeakDemo() {
        int[] result = mutableKeyResult();
        System.out.println("found before mutation : " + (result[0] == 1));
        System.out.println("found after mutation  : " + (result[1] == 1));
        System.out.println("map size after remove : " + result[2] + "  (entry is stuck)");
    }

    /** Returns {@code {foundBefore, foundAfterMutation, sizeAfterFailedRemove}} (found as 1 or 0). */
    public static int[] mutableKeyResult() {
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "payload");
        int foundBefore = map.containsKey(key) ? 1 : 0;
        key.id = 2;                              // hash changes while the key is inside the map
        int foundAfter = map.containsKey(key) ? 1 : 0;
        map.remove(key);                         // looks in the wrong bucket → no effect
        return new int[]{foundBefore, foundAfter, map.size()};
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. Leak: ThreadLocal in a thread pool
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: a ThreadLocal value lives as long as its thread. Pool threads are reused
     *       forever, so a value set and never removed stays reachable — and may be
     *       seen by the NEXT task that runs on that thread.
     *
     * Why it matters: heavy values (buffers, user context, class-loader references)
     *       pin memory; stale values are also a correctness and security bug.
     *
     * Common gotcha: always call remove() in a finally block.
     */
    public static void threadLocalLeakDemo() throws Exception {
        String[] result = threadLocalStaleValue();
        System.out.println("next task sees (no remove) : " + result[0]);
        System.out.println("next task sees (remove())  : " + result[1]);
    }

    /** Returns {@code {valueSeenWithoutRemove, valueSeenAfterRemove}} measured on a single reused thread. */
    public static String[] threadLocalStaleValue() throws Exception {
        ThreadLocal<String> context = new ThreadLocal<>();
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            pool.submit(() -> context.set("user-1")).get();
            String stale = pool.submit(context::get).get();

            pool.submit(() -> {
                try {
                    context.set("user-2");
                } finally {
                    context.remove();
                }
            }).get();
            String clean = pool.submit(context::get).get();
            return new String[]{stale, clean};
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 6. Evidence for the post-mortem
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: flags that capture evidence automatically at the moment of failure.
     *       -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=<file> writes an .hprof
     *       -XX:+ExitOnOutOfMemoryError     exits so an orchestrator can restart cleanly
     *
     * Why it matters: an OOM you cannot reproduce is only debuggable from the dump.
     *       Exiting is usually better than limping on in an undefined state.
     *
     * Common gotcha: the dump is as large as the live heap — make sure the volume has room.
     */
    public static void diagnosticFlagsDemo() {
        System.out.println(oomDiagnosticFlags("/var/dumps/app.hprof"));
    }

    public static String oomDiagnosticFlags(String dumpPath) {
        return "-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=" + dumpPath + " -XX:+ExitOnOutOfMemoryError";
    }
}
