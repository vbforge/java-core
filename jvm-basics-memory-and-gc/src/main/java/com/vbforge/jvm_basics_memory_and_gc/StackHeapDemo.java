package com.vbforge.jvm_basics_memory_and_gc;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stack vs Heap — where do variables and objects actually live?
 *
 * <p>Run {@link #main(String[])} to see every section. Each section has a printing
 * {@code ...Demo()} method plus small static helpers that return values, so the
 * claims made in the comments can be verified by {@code StackHeapDemoTest}.
 */
public class StackHeapDemo {

    public static void main(String[] args) throws InterruptedException {
        passByValueDemo();
        System.out.println("---");
        referenceAliasingDemo();
        System.out.println("---");
        stackDepthDemo();
        System.out.println("---");
        heapStatsDemo();
        System.out.println("---");
        threadStacksDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Java is always pass-by-value
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: every method call pushes a *stack frame* holding the parameters and local
     *       variables. Primitives live in the frame itself. For objects and arrays the
     *       frame holds only a *reference* (a pointer-sized value); the object body
     *       lives on the heap.
     *
     * Why it matters: "pass-by-value" means the callee gets a COPY of that value.
     *   - copy of a primitive → callee cannot change the caller's variable
     *   - copy of a reference → callee CAN mutate the shared heap object, but
     *     re-pointing its own copy does not affect the caller
     *
     * Complexity:
     *   - pushing/popping a frame: O(1), no GC involved — stack memory is reclaimed
     *     the instant the method returns
     *   - heap allocation: O(1) amortised, reclaimed later by the GC
     *
     * Common gotcha: "I reassigned the parameter but the caller's array didn't change".
     */
    public static void passByValueDemo() {
        int number = 1;
        int[] numbers = {1, 2, 3};
        int[] other = {7, 8, 9};

        int returned = incrementPrimitive(number);
        mutateArray(numbers);
        int[] replaced = reassignArray(other);

        System.out.println("number after incrementPrimitive : " + number + " (method returned " + returned + ")");
        System.out.println("numbers after mutateArray       : " + Arrays.toString(numbers));
        System.out.println("other after reassignArray       : " + Arrays.toString(other)
                + " (method returned " + Arrays.toString(replaced) + ")");
    }

    /** Increments its own copy of the value and returns it; the caller's variable is untouched. */
    public static int incrementPrimitive(int value) {
        value++;
        return value;
    }

    /** Writes through the copied reference — the caller sees the change because the array is shared. */
    public static void mutateArray(int[] array) {
        array[0] = 99;
    }

    /** Re-points only the local copy of the reference — the caller's reference is unaffected. */
    public static int[] reassignArray(int[] array) {
        array = new int[]{-1};
        return array;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. References, aliasing and identity
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: two reference variables can point to the SAME heap object (aliases), or to
     *       two different objects that happen to be equal.
     *
     * Why it matters: `==` compares references (identity); `equals` compares content.
     *   An alias sees every mutation made through the other name.
     *
     * Common gotcha: `new String("jvm") == new String("jvm")` is false — two heap objects.
     */
    public static void referenceAliasingDemo() {
        System.out.println("alias append result  : " + aliasAppend());
        boolean[] checks = identityChecks();
        System.out.println("a == b               : " + checks[0]);
        System.out.println("a.equals(b)          : " + checks[1]);
        System.out.println("a == c (alias)       : " + checks[2]);
    }

    /** Appends through one name and reads through the alias. */
    public static String aliasAppend() {
        StringBuilder original = new StringBuilder("x");
        StringBuilder alias = original;
        alias.append("y");
        return original.toString();
    }

    /** Returns {@code {a == b, a.equals(b), a == c}} for two equal strings and an alias. */
    public static boolean[] identityChecks() {
        String a = new String("jvm");
        String b = new String("jvm");
        String c = a;
        return new boolean[]{a == b, a.equals(b), a == c};
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Stack depth and StackOverflowError
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: each thread has a fixed-size stack (default 1 MB on most 64-bit platforms,
     *       tunable with -Xss). Every active method call consumes part of it.
     *
     * Why it matters: unbounded or very deep recursion exhausts the stack long before
     *   the heap is touched, and the JVM throws StackOverflowError.
     *
     * Complexity:
     *   - recursive sum: O(n) time AND O(n) stack space
     *   - iterative sum: O(n) time, O(1) stack space
     *
     * Common gotcha: Java does not perform tail-call optimisation — even a "tail
     *   recursive" method grows the stack. Convert deep recursion to a loop or an
     *   explicit Deque on the heap.
     */
    public static void stackDepthDemo() {
        System.out.println("recursion depth before StackOverflowError ~ " + measureStackDepth());
        System.out.println("sumRecursive(1_000)   = " + sumRecursive(1_000));
        System.out.println("sumIterative(10_000_000) = " + sumIterative(10_000_000));
        try {
            sumRecursive(10_000_000);
        } catch (StackOverflowError e) {
            System.out.println("sumRecursive(10_000_000) -> " + e.getClass().getSimpleName());
        }
    }

    private static int depth;

    /** Recurses until the stack is exhausted and returns how many frames fit (varies by machine). */
    public static int measureStackDepth() {
        depth = 0;
        try {
            dive();
        } catch (StackOverflowError expected) {
            // the stack is unwound; we only wanted the counter
        }
        return depth;
    }

    private static void dive() {
        depth++;
        dive();
    }

    /** Sum of 1..n using one stack frame per step. */
    public static long sumRecursive(int n) {
        return n == 0 ? 0 : n + sumRecursive(n - 1);
    }

    /** Sum of 1..n using a loop — constant stack usage. */
    public static long sumIterative(int n) {
        long sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += i;
        }
        return sum;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. The heap is bounded: Runtime memory figures
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: the heap is the shared area where all objects and arrays live.
     *   Runtime exposes three numbers:
     *     maxMemory()   — the ceiling the heap may grow to (-Xmx)
     *     totalMemory() — heap currently committed from the OS
     *     freeMemory()  — unused part of the committed heap
     *   used = total - free; headroom = max - used.
     *
     * Why it matters: OutOfMemoryError happens when the *max* is reached and the GC
     *   cannot free enough — not when `total` is full (the JVM can still grow).
     *
     * Common gotcha: freeMemory() alone is misleading; it ignores room to grow up to max.
     */
    public static void heapStatsDemo() {
        long[] stats = heapStats();
        System.out.println("max   = " + stats[0] / (1024 * 1024) + " MB");
        System.out.println("total = " + stats[1] / (1024 * 1024) + " MB");
        System.out.println("free  = " + stats[2] / (1024 * 1024) + " MB");
        System.out.println("used  = " + (stats[1] - stats[2]) / (1024 * 1024) + " MB");
    }

    /** Returns {@code {maxMemory, totalMemory, freeMemory}} in bytes. */
    public static long[] heapStats() {
        Runtime runtime = Runtime.getRuntime();
        return new long[]{runtime.maxMemory(), runtime.totalMemory(), runtime.freeMemory()};
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. One stack per thread, one heap for all
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: locals live on the owning thread's stack, so no other thread can see them.
     *       Heap objects are shared between all threads that hold a reference.
     *
     * Why it matters: local variables are inherently thread-safe; shared heap state is
     *   where all concurrency bugs live (see the concurrency phase).
     *
     * Common gotcha: a local *reference* is private, but the object it points to may not be.
     */
    public static void threadStacksDemo() throws InterruptedException {
        int[] result = countInTwoThreads();
        System.out.println("shared counter (heap)   = " + result[0]);
        System.out.println("local counter, thread 1 = " + result[1]);
        System.out.println("local counter, thread 2 = " + result[2]);
    }

    /**
     * Two threads each loop 1000 times. Returns {@code {sharedCounter, local1, local2}}:
     * the shared heap counter reaches 2000, each private local counter reaches 1000.
     */
    public static int[] countInTwoThreads() throws InterruptedException {
        final AtomicInteger shared = new AtomicInteger();
        final int[] locals = new int[2];

        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                int local = 0;                    // lives on THIS thread's stack
                for (int n = 0; n < 1000; n++) {
                    local++;
                    shared.incrementAndGet();     // lives on the shared heap
                }
                locals[index] = local;
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        return new int[]{shared.get(), locals[0], locals[1]};
    }
}
