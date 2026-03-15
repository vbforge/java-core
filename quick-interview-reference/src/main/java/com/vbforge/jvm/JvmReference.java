package com.vbforge.jvm;

import java.lang.ref.*;
import java.util.*;

/**
 * ═══════════════════════════════════════════════════════════════
 *  JVM INTERVIEW REFERENCE
 *  Topics: memory areas · GC algorithms · reference types ·
 *          memory leaks · JIT · OutOfMemoryError types
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class JvmReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. JVM MEMORY AREAS
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  HEAP (shared across all threads):
     *  ├── Young Generation
     *  │   ├── Eden Space        ← new objects allocated here
     *  │   ├── Survivor S0       ← survived 1+ minor GC
     *  │   └── Survivor S1
     *  └── Old Generation (Tenured)  ← long-lived objects promoted from Young
     *
     *  NON-HEAP (JVM internals):
     *  ├── Metaspace (Java 8+)   ← class metadata, replaces PermGen
     *  ├── Code Cache            ← JIT-compiled native code
     *  └── Direct Memory         ← off-heap (NIO ByteBuffer.allocateDirect())
     *
     *  PER-THREAD:
     *  ├── Stack                 ← stack frames, local variables, operand stack
     *  ├── PC Register           ← current bytecode instruction
     *  └── Native Method Stack   ← for native (JNI) methods
     *
     *  WHAT LIVES WHERE:
     *  Stack: local primitives, local references (the pointer itself, not the object)
     *  Heap:  all objects (new Foo()), arrays, instance variables
     *  ⚠️ "Objects live on stack" = FALSE — only references live on stack
     */


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. GC ALGORITHMS — comparison
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Algorithm     │ STW Pauses   │ Throughput │ Latency  │ Best for
     *  ──────────────┼──────────────┼────────────┼──────────┼─────────────────────
     *  Serial        │ Full STW     │ Low        │ High     │ Single-core, small heap
     *  Parallel      │ Full STW     │ High       │ High     │ Batch/throughput apps
     *  G1 (default)  │ Short STW    │ Good       │ Good     │ General purpose (>4GB heap)
     *  ZGC (Java 15+)│ Sub-ms STW   │ Good       │ Very Low │ Large heaps, low-latency apps
     *  Shenandoah    │ Sub-ms STW   │ Good       │ Very Low │ Large heaps, low-latency
     *
     *  STW = Stop-The-World — application pauses completely
     *
     *  GC PROCESS (simplified):
     *  Minor GC: Young generation collected — fast, frequent
     *  Major GC: Old generation collected — slow, infrequent
     *  Full GC:  Entire heap — avoid! Usually means memory pressure
     *
     *  TUNING FLAGS (useful to know):
     *  -Xms512m         ← initial heap size
     *  -Xmx2g           ← max heap size
     *  -XX:+UseG1GC     ← use G1 collector
     *  -XX:+UseZGC      ← use ZGC
     *  -Xlog:gc*        ← enable GC logging
     */


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. REFERENCE TYPES — Strong, Soft, Weak, Phantom ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Type          │ GC'd when                      │ Use case
     *  ──────────────┼────────────────────────────────┼──────────────────────
     *  Strong        │ Never (while ref exists)        │ Normal objects
     *  Soft          │ Low memory pressure             │ Memory-sensitive caches
     *  Weak          │ Next GC cycle (regardless)      │ Canonicalizing maps, listeners
     *  Phantom       │ After finalization              │ Cleanup tracking, off-heap
     */
    static void referenceTypes() {
        Object obj = new Object();  // strong reference — never GC'd while in scope

        // Soft reference — GC collects when memory is low (before OutOfMemoryError)
        SoftReference<byte[]> soft = new SoftReference<>(new byte[1024 * 1024]);
        byte[] data = soft.get();  // may be null if GC collected it

        // Weak reference — GC collects at next GC cycle
        WeakReference<Object> weak = new WeakReference<>(new Object());
        Object weakObj = weak.get();  // likely null after next GC

        // WeakHashMap — keys held with weak references
        // Entry removed automatically when key is GC'd
        WeakHashMap<Object, String> weakMap = new WeakHashMap<>();

        // Phantom reference — get() ALWAYS returns null
        // Used with ReferenceQueue for post-GC cleanup
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        PhantomReference<Object> phantom = new PhantomReference<>(new Object(), queue);
        // phantom.get() == null always
        // queue.poll() returns the reference after object is finalized
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. MEMORY LEAKS — common causes ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  A memory leak in Java = objects reachable by GC roots but no longer needed.
     *  GC cannot collect them → heap grows → eventually OutOfMemoryError.
     *
     *  COMMON CAUSES:
     *
     *  1. Static collections holding references
     *     static Map<Key, Value> cache = new HashMap<>();
     *     → entries never removed → grows forever
     *
     *  2. ThreadLocal not removed
     *     ThreadLocal<HeavyObject> tl = new ThreadLocal<>();
     *     tl.set(new HeavyObject());
     *     // thread pool thread reuses same thread → HeavyObject never released
     *     // ✅ Always: tl.remove() in finally after use
     *
     *  3. Listeners/callbacks not deregistered
     *     eventBus.register(this);
     *     // object never GC'd because eventBus holds reference
     *     // ✅ Always deregister when done
     *
     *  4. Inner class holding outer class reference
     *     class Outer {
     *         class Inner {}  // Inner holds implicit reference to Outer!
     *         // If Inner outlives Outer → Outer cannot be GC'd
     *         // ✅ Use static nested class if outer ref not needed
     *     }
     *
     *  5. Unclosed resources (streams, connections)
     *     → use try-with-resources
     */


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 5. OutOfMemoryError TYPES ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Error message                              │ Cause
     *  ───────────────────────────────────────────┼──────────────────────────────
     *  Java heap space                            │ Heap full — memory leak or too small
     *  GC overhead limit exceeded                 │ GC running >98% of time, recovering <2% heap
     *  Metaspace                                  │ Class metadata area full (too many classes)
     *  Unable to create new native thread         │ OS thread limit reached
     *  Direct buffer memory                       │ Off-heap NIO direct buffers exhausted
     *  Requested array size exceeds VM limit      │ Allocating array larger than heap
     */


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 6. JIT COMPILATION
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  JVM starts interpreting bytecode → profiles "hot" code paths →
     *  JIT compiles hot code to native machine code → runs at near-native speed.
     *
     *  C1 (client) compiler: fast compilation, moderate optimization — good startup
     *  C2 (server) compiler: slow compilation, aggressive optimization — good throughput
     *  Tiered compilation (default): starts with C1, promotes to C2 for hot methods
     *
     *  OPTIMIZATIONS JIT applies:
     *  - Inlining: replaces method call with method body (avoids call overhead)
     *  - Escape analysis: stack-allocate objects that don't escape the method
     *  - Loop unrolling: reduces loop overhead for small, predictable loops
     *  - Dead code elimination: removes code that can never execute
     *
     *  ⚠️ Benchmarking without JVM warmup gives misleading results.
     *  Use JMH (Java Microbenchmark Harness) for accurate benchmarks.
     */

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Calling System.gc()                      → hint only, JVM may ignore; hurts performance
     *  ❌ Relying on finalize()                    → deprecated Java 9, removed Java 18
     *  ❌ Storing large objects in static fields    → memory leak
     *  ❌ Not calling ThreadLocal.remove()          → thread pool memory leak
     *  ❌ Non-static inner class held by long-lived ref → outer class memory leak
     *  ❌ Benchmarking without warmup               → JIT not yet optimized → wrong results
     */

    public static void main(String[] args) {
        referenceTypes();
        System.out.println("JVM reference — read the comments!");
    }
}
