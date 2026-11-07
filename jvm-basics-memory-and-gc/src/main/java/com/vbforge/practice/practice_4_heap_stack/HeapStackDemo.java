package com.vbforge.practice.practice_4_heap_stack;

/**
 * Heap vs Stack — the two most important memory areas for Java developers.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │  STACK (per-thread)            │  HEAP (shared)             │
 * ├────────────────────────────────┼────────────────────────────┤
 * │  Method call frames            │  All object instances      │
 * │  Local primitive variables     │  Instance fields           │
 * │  Local reference variables     │  Arrays                    │
 * │  Return addresses              │  Static field VALUES*      │
 * │  LIFO structure                │  GC-managed                │
 * │  Fast allocation (move pointer)│  Slower (GC overhead)      │
 * │  Thread-safe by design         │  Requires synchronization  │
 * │  Limited size (~1MB default)   │  Large (configured by -Xmx)│
 * └────────────────────────────────┴────────────────────────────┘
 *
 * *Static field REFERENCES live in Metaspace; the objects they point to
 *  live on the Heap.
 *
 * Key insight — what goes WHERE:
 *   int x = 5;                 → 'x' is on the Stack (primitive)
 *   Person p = new Person();   → 'p' reference on Stack, Person object on Heap
 *   p.name = "Alice";          → 'name' field inside Person object → Heap
 *
 * Pass-by-value in Java:
 *   - Primitives: copy of the value is passed — callee cannot affect caller's variable
 *   - References: copy of the reference is passed — callee CAN mutate the object,
 *                 but reassigning the reference does NOT affect the caller
 *
 * Escape Analysis (JVM optimization):
 *   - JVM can detect that an object never "escapes" a method
 *   - Such objects may be allocated on the Stack (or registers) instead of Heap
 *   - Result: zero GC pressure for short-lived local objects
 *   - Enabled by default: -XX:+DoEscapeAnalysis
 */
public class HeapStackDemo {

    // Heap: instance fields
    private String name;
    private int    value;

    public HeapStackDemo(String name, int value) {
        this.name  = name;
        this.value = value;
    }

    // -----------------------------------------------------------------------
    // Pass-by-value: primitives
    // -----------------------------------------------------------------------

    /**
     * Java is ALWAYS pass-by-value.
     * For primitives: a copy of the int is passed — no way to change the original.
     *
     * @param x copy of the caller's variable — changes here are invisible outside
     */
    public static void tryToModifyPrimitive(int x) {
        x = x * 100; // modifies only the local copy — caller's variable unchanged
    }

    /**
     * For object references: a copy of the REFERENCE is passed.
     * The copy points to the same Heap object → field mutations ARE visible.
     * But reassigning the reference itself does NOT affect the caller.
     *
     * @param obj copy of the reference — mutations to obj.value are visible,
     *            but assigning obj = new ... is invisible to caller
     */
    public static void tryToModifyObject(HeapStackDemo obj) {
        obj.value = 999;          // mutates the Heap object — caller WILL see this
        obj = new HeapStackDemo("Ghost", -1); // reassigns local copy — caller unaffected
    }

    /**
     * Same principle with String (immutable):
     * Reassigning a String inside a method does NOT affect the caller's reference.
     */
    public static void tryToModifyString(String s) {
        s = "modified"; // local reassignment — caller's reference unchanged
    }

    // -----------------------------------------------------------------------
    // Stack depth and frame lifecycle
    // -----------------------------------------------------------------------

    /**
     * Each call to this method pushes a new stack frame.
     * The frame is popped when the method returns — locals vanish.
     *
     * @param n recursion depth (bounded to avoid SOE)
     * @return depth reached
     */
    public static int countFrames(int n) {
        if (n <= 0) return 0;
        // At this point: two frames on stack (main + countFrames)
        // Each level adds another frame with its own copy of 'n'
        return 1 + countFrames(n - 1);
    }

    // -----------------------------------------------------------------------
    // Heap allocation patterns
    // -----------------------------------------------------------------------

    /**
     * Demonstrates that arrays are always on the Heap in Java.
     * The local variable 'arr' (the reference) lives on the Stack;
     * the array data itself lives on the Heap.
     *
     * @param size number of elements
     * @return sum — computed from heap-allocated array
     */
    public static long heapArray(int size) {
        int[] arr = new int[size]; // array body on Heap
        for (int i = 0; i < size; i++) {
            arr[i] = i + 1;
        }
        long sum = 0;
        for (int v : arr) sum += v;
        return sum; // frame (including 'arr' reference) popped; array eligible for GC
    }

    /**
     * Escape analysis example:
     * The JVM may notice that 'temp' never escapes this method and
     * allocate it on the Stack (or eliminate it entirely via scalar replacement).
     * Observable behavior is identical — this is a transparent optimization.
     *
     * @param a first operand
     * @param b second operand
     * @return sum stored via a "temporary object" (which JVM may elide)
     */
    public static int escapeAnalysisExample(int a, int b) {
        // In a naive JVM: Point allocated on Heap
        // With escape analysis: JVM may allocate on Stack or inline fields directly
        Point temp = new Point(a, b);
        return temp.x + temp.y;
    }

    /** Simple value holder — intentionally package-private for escape analysis demo. */
    static class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }

    // -----------------------------------------------------------------------
    // Object lifecycle on the Heap
    // -----------------------------------------------------------------------

    /**
     * Objects become GC-eligible when no live references point to them.
     * This method creates objects, lets them go out of scope, and notes
     * that GC may (or may not) collect them immediately — GC timing is JVM-controlled.
     */
    public static void objectLifecycleDemo() {
        System.out.println("\n=== Object Lifecycle on Heap ===");

        HeapStackDemo live = new HeapStackDemo("Live", 1);
        System.out.println("Object created: " + live.name);

        // Nulling the reference makes the object GC-eligible
        live = null;
        System.out.println("Reference nulled — object eligible for GC (no live reference)");

        // Suggest GC — does NOT guarantee immediate collection
        System.gc();
        System.out.println("System.gc() called — collection is up to the JVM");
    }

    // Getters
    public String getName()  { return name; }
    public int    getValue() { return value; }

    public static void main(String[] args) {
        System.out.println("=== Heap vs Stack Demo ===\n");

        // --- Primitive pass-by-value ---
        System.out.println("--- Primitive pass-by-value ---");
        int x = 42;
        tryToModifyPrimitive(x);
        System.out.println("x after tryToModifyPrimitive(x): " + x); // still 42

        // --- Reference pass-by-value ---
        System.out.println("\n--- Reference pass-by-value ---");
        HeapStackDemo obj = new HeapStackDemo("Original", 10);
        System.out.println("Before: name=" + obj.name + ", value=" + obj.value);
        tryToModifyObject(obj);
        System.out.println("After:  name=" + obj.name + ", value=" + obj.value);
        // value changed to 999 (heap mutation visible), name still "Original" (reassign invisible)

        // --- String pass-by-value ---
        System.out.println("\n--- String pass-by-value ---");
        String s = "original";
        tryToModifyString(s);
        System.out.println("s after tryToModifyString: " + s); // still "original"

        // --- Stack frames ---
        System.out.println("\n--- Stack frames (bounded recursion) ---");
        int depth = countFrames(5);
        System.out.println("Frames counted: " + depth);

        // --- Heap array ---
        System.out.println("\n--- Heap array ---");
        long sum = heapArray(10);
        System.out.println("Sum of 1..10 (heap array): " + sum);

        // --- Escape analysis ---
        System.out.println("\n--- Escape analysis ---");
        int result = escapeAnalysisExample(7, 8);
        System.out.println("escapeAnalysisExample(7,8) = " + result);

        objectLifecycleDemo();
    }
}
