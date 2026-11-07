package com.vbforge.practice.practice_9_memory_behavior;

/**
 * Demonstrates Java memory areas — Stack and Heap.
 *
 * STACK:
 *   - Per-thread, LIFO structure
 *   - Stores: local variables, method parameters, return addresses, primitive values
 *   - Automatically managed: frame pushed on call, popped on return
 *   - Fast allocation/deallocation
 *   - Limited size → StackOverflowError on deep/infinite recursion
 *
 * HEAP:
 *   - Shared across all threads
 *   - Stores: all objects and arrays (created via 'new')
 *   - Managed by Garbage Collector
 *   - Larger but slower than stack
 *   - OutOfMemoryError when GC can't free enough space
 *
 * String Pool:
 *   - Special area in the heap (Metaspace in Java 8+)
 *   - String literals are interned — same literal shares one instance
 *   - 'new String(...)' bypasses the pool — creates a new heap object
 */
public class MemoryBehaviorDemo {

    // Static field — lives on heap (in class metadata / Metaspace)
    static int staticCounter = 0;

    // Instance field — lives on heap (inside the object)
    int instanceId;

    public MemoryBehaviorDemo(int id) {
        this.instanceId = id;
    }

    // Demonstrates stack frame creation and local variable lifecycle
    static int computeSum(int a, int b) {
        // 'a', 'b', 'result' are ALL on the stack — local to this frame
        int result = a + b;
        return result;
        // stack frame popped here — a, b, result gone
    }

    // Demonstrates StackOverflowError
    static int infiniteRecursion(int n) {
        return infiniteRecursion(n + 1);   // no base case → stack fills up
    }

    public static void main(String[] args) {
        System.out.println("=== Stack vs Heap Memory Demo ===\n");

        // --- Stack: local primitives ---
        System.out.println("-- Stack: local primitives and method frames --");
        int x = 10;       // x lives on the stack
        int y = 20;       // y lives on the stack
        int sum = computeSum(x, y);  // new stack frame created, then popped
        System.out.println("computeSum(10, 20) = " + sum);

        // --- Heap: objects ---
        System.out.println("\n-- Heap: objects live here --");
        MemoryBehaviorDemo obj1 = new MemoryBehaviorDemo(1);  // object on heap
        MemoryBehaviorDemo obj2 = new MemoryBehaviorDemo(2);  // another object on heap
        // obj1 and obj2 are references (addresses) — they live on the stack
        // the actual MemoryBehaviorDemo objects live on the heap
        System.out.println("obj1.instanceId = " + obj1.instanceId + "  (on heap)");
        System.out.println("obj2.instanceId = " + obj2.instanceId + "  (on heap)");

        // --- String pool ---
        System.out.println("\n-- String pool --");
        String s1 = "hello";             // interned in string pool
        String s2 = "hello";             // same pool entry
        String s3 = new String("hello"); // new heap object, bypasses pool
        String s4 = s3.intern();         // force interning — returns pool reference

        System.out.println("s1 == s2:         " + (s1 == s2) + "  ← same pool reference");
        System.out.println("s1 == s3:         " + (s1 == s3) + "  ← different: s3 is on heap");
        System.out.println("s1 == s4:         " + (s1 == s4) + "  ← intern() returns pool ref");
        System.out.println("s1.equals(s3):    " + s1.equals(s3) + "  ← always use equals for value");

        // --- GC eligibility ---
        System.out.println("\n-- GC eligibility --");
        MemoryBehaviorDemo temp = new MemoryBehaviorDemo(99);
        System.out.println("temp created: id=" + temp.instanceId);
        temp = null;   // original object now has no strong references → GC eligible
        System.out.println("temp = null → object eligible for garbage collection");

        // --- StackOverflowError ---
        System.out.println("\n-- StackOverflowError (infinite recursion) --");
        try {
            infiniteRecursion(0);
        } catch (StackOverflowError e) {
            System.out.println("StackOverflowError caught! Stack exhausted by infinite recursion.");
        }
    }
}
