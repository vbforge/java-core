package com.vbforge.practice.practice_7_memory_optimization;

/**
 * Demonstrates common memory optimization techniques in Java.
 *
 * Memory cost mindset:
 *   - Every object allocation triggers GC pressure eventually
 *   - String concatenation in loops creates N intermediate String objects
 *   - Reusing objects (StringBuilder, pooling) reduces allocation rate
 *   - Primitives are cheaper than boxed types (no heap object overhead)
 *
 * Key techniques:
 *   1. StringBuilder vs String concatenation
 *   2. Primitive arrays vs boxed arrays
 *   3. Object reuse / avoid unnecessary allocation
 *   4. Lazy initialization
 *   5. String interning awareness
 *
 * JVM object overhead (approx, HotSpot 64-bit):
 *   - Object header: 16 bytes (mark word + class pointer)
 *   - Integer (boxed):  ~16 bytes object header + 4 bytes value = ~16 bytes (aligned to 16)
 *   - int (primitive):     4 bytes in array, 0 heap allocation
 */
public class MemoryOptimizationDemo {

    // -------------------------------------------------------------------------
    // 1. String concatenation — the classic memory trap
    // -------------------------------------------------------------------------

    /**
     * ANTI-PATTERN: each '+' in a loop creates a new String object.
     * N iterations → O(N²) bytes allocated.
     */
    public static String concatenateNaive(int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result += i;          // new String allocated every iteration
        }
        return result;
    }

    /**
     * OPTIMIZED: StringBuilder pre-allocates a buffer and appends in-place.
     * N iterations → O(N) bytes, single final toString() allocation.
     *
     * Pre-sizing the buffer (capacity hint) avoids internal array resizing.
     */
    public static String concatenateOptimized(int n) {
        StringBuilder sb = new StringBuilder(n * 3); // capacity hint
        for (int i = 0; i < n; i++) {
            sb.append(i);
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // 2. Primitive arrays vs boxed arrays
    // -------------------------------------------------------------------------

    /**
     * int[] — stores raw 4-byte values contiguously.
     * No boxing, no heap objects per element, cache-friendly.
     */
    public static long sumPrimitive(int[] values) {
        long sum = 0;
        for (int v : values) {
            sum += v;             // no unboxing, straight arithmetic
        }
        return sum;
    }

    /**
     * Integer[] — each element is a heap object (reference + Integer instance).
     * Each access dereferences a pointer → cache miss risk for large arrays.
     * Each arithmetic operation causes implicit unboxing.
     */
    public static long sumBoxed(Integer[] values) {
        long sum = 0;
        for (Integer v : values) {
            sum += v;             // unboxing: Integer → int on each iteration
        }
        return sum;
    }

    // -------------------------------------------------------------------------
    // 3. Lazy initialization — defer allocation until actually needed
    // -------------------------------------------------------------------------

    /**
     * Expensive resource only created on first access.
     * Pattern: field starts null, initialized on first call (not thread-safe here —
     * see double-checked locking in _12_concurrency for thread-safe variant).
     */
    private ExpensiveResource resource; // null until needed

    public ExpensiveResource getResource() {
        if (resource == null) {
            resource = new ExpensiveResource(); // allocated only when requested
        }
        return resource;
    }

    /**
     * Placeholder simulating an expensive-to-construct object.
     */
    public static class ExpensiveResource {
        private final byte[] buffer = new byte[1024]; // simulate memory cost
        public int size() { return buffer.length; }
    }

    // -------------------------------------------------------------------------
    // 4. Avoid unnecessary autoboxing in hot paths
    // -------------------------------------------------------------------------

    /**
     * Returns a boxed Long — forces heap allocation per call.
     * Avoid in tight loops or frequently-called utility methods.
     */
    public static Long countBoxed(int limit) {
        Long count = 0L;           // boxed counter — new Long on every '+='
        for (int i = 0; i < limit; i++) {
            count++;               // unbox → add → rebox
        }
        return count;
    }

    /**
     * Returns a primitive long — zero heap allocations for the counter.
     */
    public static long countPrimitive(int limit) {
        long count = 0L;           // stack-allocated primitive
        for (int i = 0; i < limit; i++) {
            count++;
        }
        return count;
    }

    // -------------------------------------------------------------------------
    // 5. String interning awareness
    // -------------------------------------------------------------------------

    /**
     * String literals are interned by default (String Pool).
     * 'new String(...)' always creates a heap object outside the pool.
     * intern() explicitly puts a string into the pool — useful when the same
     * string content appears many times (e.g., parsing a CSV file with
     * repeated category values).
     *
     * Trade-off: intern() reduces heap pressure but the pool itself
     * lives in the JVM metaspace / heap (since Java 7+) and can overflow
     * if too many unique strings are interned.
     */
    public static String demonstrateInterning() {
        String a = "hello";                    // from pool
        String b = "hello";                    // same pool entry
        String c = new String("hello");        // new heap object
        String d = new String("hello").intern(); // put back into pool

        System.out.println("a == b (literals): " + (a == b));      // true
        System.out.println("a == c (new):      " + (a == c));      // false
        System.out.println("a == d (interned): " + (a == d));      // true
        return d;
    }

    // -------------------------------------------------------------------------
    // main — illustrative output
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        System.out.println("=== Memory Optimization Demo ===\n");

        // StringBuilder vs naive
        System.out.println("-- String building (n=1000) --");
        String naive     = concatenateNaive(1000);
        String optimized = concatenateOptimized(1000);
        System.out.println("naive length:     " + naive.length());
        System.out.println("optimized length: " + optimized.length());
        System.out.println("results equal:    " + naive.equals(optimized));

        // Primitive vs boxed sum
        System.out.println("\n-- Primitive vs boxed array sum --");
        int[]     prim  = {1, 2, 3, 4, 5};
        Integer[] boxed = {1, 2, 3, 4, 5};
        System.out.println("primitive sum: " + sumPrimitive(prim));
        System.out.println("boxed sum:     " + sumBoxed(boxed));

        // Lazy init
        System.out.println("\n-- Lazy initialization --");
        MemoryOptimizationDemo demo = new MemoryOptimizationDemo();
        System.out.println("resource before first access: " + demo.resource); // null
        System.out.println("resource size: " + demo.getResource().size());
        System.out.println("resource after first access: " + (demo.resource != null)); // allocated

        // Autoboxing trap
        System.out.println("\n-- Autoboxing overhead (count to 10_000) --");
        System.out.println("boxed result:     " + countBoxed(10_000));
        System.out.println("primitive result: " + countPrimitive(10_000));

        // String interning
        System.out.println("\n-- String interning --");
        demonstrateInterning();
    }
}
