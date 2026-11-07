package com.vbforge.practice.practice_8_memory_model;

/**
 * JVM Memory Areas — a guided tour of all runtime data regions.
 *
 * The JVM specification defines several distinct memory spaces.
 * Understanding them is critical for diagnosing OOM errors, tuning GC,
 * and reasoning about thread safety.
 *
 * ┌─────────────────────────────────────────────────────────┐
 * │                    JVM Memory Layout                    │
 * ├─────────────────────────────────────────────────────────┤
 * │  Per-Thread (not shared):                               │
 * │    • PC Register       — current bytecode instruction   │
 * │    • JVM Stack         — frames (locals, operands, ref) │
 * │    • Native Method Stack — for native (C/C++) calls     │
 * │                                                         │
 * │  Shared (all threads):                                  │
 * │    • Heap              — all object instances           │
 * │    • Method Area       — class metadata, static fields  │
 * │      └─ (String Pool lives here in Java 8+)            │
 * └─────────────────────────────────────────────────────────┘
 *
 * Key distinctions:
 *   - "new Object()" → always goes to Heap
 *   - local primitives → live in the Stack frame
 *   - static fields → live in the Method Area (Metaspace in Java 8+)
 *   - interned Strings → Heap (String Pool moved from PermGen in Java 7)
 *
 * Common OOM variants and their memory area:
 *   - OutOfMemoryError: Java heap space         → Heap exhausted
 *   - OutOfMemoryError: Metaspace               → Method Area exhausted
 *   - StackOverflowError                        → Stack exhausted (deep recursion)
 */
public class JvmMemoryAreasDemo {

    // --- Method Area: static fields (class-level, shared across all threads) ---
    private static final String APP_NAME = "JVM Memory Demo";
    private static int instanceCount = 0;

    // --- Heap: instance fields (per-object state) ---
    private final int id;
    private final String label;

    public JvmMemoryAreasDemo(String label) {
        this.id    = ++instanceCount;
        this.label = label;
    }

    /**
     * Demonstrates Stack frame allocation.
     *
     * Each method call creates a new Stack Frame containing:
     *   - Local variable table  (primitives stored by value, refs stored as pointers)
     *   - Operand stack         (working area for bytecode operations)
     *   - Frame data            (return address, constant pool reference)
     *
     * When the method returns, the frame is popped and locals are gone.
     */
    public int stackFrameExample(int a, int b) {
        // 'a', 'b', 'sum' live entirely on the Stack — no heap involved
        int sum = a + b;       // operand stack used internally by JVM
        int product = a * b;   // another local variable in this frame
        return sum + product;
    }

    /**
     * Demonstrates StackOverflowError via unbounded recursion.
     *
     * Each recursive call pushes a new frame; the Stack has limited size
     * (default ~512KB–1MB per thread). Infinite recursion → SOE.
     *
     * @param depth current recursion depth (for demonstration)
     */
    public static int recursiveStack(int depth) {
        // Base case intentionally omitted to force StackOverflowError
        return recursiveStack(depth + 1);
    }

    /**
     * Demonstrates String interning and the String Pool.
     *
     * String literals are automatically interned — they reference the same
     * object in the String Pool (part of the Heap since Java 7).
     * `new String(...)` bypasses the pool → always a new Heap object.
     * `.intern()` manually places a string into the pool.
     */
    public static void stringPoolDemo() {
        String literal1 = "hello";          // pool reference
        String literal2 = "hello";          // same pool reference
        String heapStr  = new String("hello"); // explicit heap allocation, bypasses pool
        String interned = heapStr.intern(); // intern() → returns pool reference

        System.out.println("=== String Pool Demo ===");
        System.out.println("literal1 == literal2 : " + (literal1 == literal2));   // true  — same pool object
        System.out.println("literal1 == heapStr  : " + (literal1 == heapStr));    // false — heap vs pool
        System.out.println("literal1 == interned : " + (literal1 == interned));   // true  — intern() returned pool ref
        System.out.println("heapStr.equals(literal1): " + heapStr.equals(literal1)); // true — content equal
    }

    /**
     * Demonstrates Metaspace (Method Area) behavior.
     *
     * Static fields are stored in the Method Area (Metaspace in Java 8+).
     * Class metadata is loaded once per ClassLoader and shared.
     * Metaspace grows dynamically — unlike old PermGen, no hard default cap.
     *
     * To limit: -XX:MaxMetaspaceSize=256m
     */
    public static void metaspaceDemo() {
        System.out.println("\n=== Metaspace / Method Area Demo ===");
        System.out.println("APP_NAME (static final in Method Area): " + APP_NAME);
        System.out.println("instanceCount (static mutable in Method Area): " + instanceCount);

        // Class objects themselves live in Metaspace
        Class<?> clazz = JvmMemoryAreasDemo.class;
        System.out.println("Class name from Metaspace: " + clazz.getName());
        System.out.println("Methods count: " + clazz.getDeclaredMethods().length);
    }

    /**
     * Demonstrates PC Register behavior conceptually.
     *
     * Each thread has its own PC (Program Counter) Register — a tiny memory cell
     * holding the address of the JVM instruction currently being executed.
     * It is NOT accessible from Java code directly; this is purely illustrative.
     *
     * For native methods, the PC is undefined (set to undefined value).
     */
    public static void pcRegisterNote() {
        System.out.println("\n=== PC Register (conceptual) ===");
        System.out.println("Each thread tracks its own instruction pointer.");
        System.out.println("Enables threads to resume correctly after context switches.");
        System.out.println("Thread: " + Thread.currentThread().getName() + " — executing now");
    }

    public String describe() {
        return "Instance #" + id + " [" + label + "]";
    }

    public static int getInstanceCount() { return instanceCount; }

    public static void main(String[] args) {
        System.out.println("=== JVM Memory Areas Demo ===\n");

        // Objects created on the Heap
        JvmMemoryAreasDemo obj1 = new JvmMemoryAreasDemo("Alpha");
        JvmMemoryAreasDemo obj2 = new JvmMemoryAreasDemo("Beta");
        System.out.println(obj1.describe());
        System.out.println(obj2.describe());

        // Stack frames
        System.out.println("\n=== Stack Frame Demo ===");
        int result = obj1.stackFrameExample(3, 4);
        System.out.println("stackFrameExample(3,4) = " + result); // (3+4) + (3*4) = 19

        stringPoolDemo();
        metaspaceDemo();
        pcRegisterNote();

        // StackOverflowError demo
        System.out.println("\n=== StackOverflowError Demo ===");
        try {
            recursiveStack(0);
        } catch (StackOverflowError e) {
            System.out.println("StackOverflowError caught — stack exhausted by deep recursion");
        }
    }
}
