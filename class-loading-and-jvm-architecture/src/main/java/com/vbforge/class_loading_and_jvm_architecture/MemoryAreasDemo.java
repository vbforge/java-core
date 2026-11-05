package com.vbforge.class_loading_and_jvm_architecture;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * JVM runtime data areas: thread stacks, the heap, and class metadata (Metaspace).
 *
 * <p>Sections:
 * <ol>
 *   <li>Stacks and frames</li>
 *   <li>Stack vs heap: what "pass by value" really copies</li>
 *   <li>Heap limits, the String pool and OutOfMemoryError</li>
 *   <li>Class metadata and statics, observed through JMX beans</li>
 * </ol>
 *
 * <p>Garbage-collection details live in the {@code jvm-basics-memory-and-gc} module.
 */
public class MemoryAreasDemo {

    private static volatile Object sink;
    private static int recursionCounter;

    public static void main(String[] args) {
        stacksDemo();
        System.out.println("---");
        stackVersusHeapDemo();
        System.out.println("---");
        heapDemo();
        System.out.println("---");
        classMetadataDemo();
    }

    // =========================================================================
    // 1. Stacks and frames
    // =========================================================================

    /*
     * WHAT: Every thread has its own stack made of frames - one frame per method call.
     *       A frame holds the local variables, the operand stack and a return address.
     *       (Each thread also has its own program counter; native methods use a native stack.)
     *
     * WHY:  Frames explain stack traces, recursion limits and why local variables are thread-safe.
     *
     * COMPLEXITY: allocating/freeing a frame is O(1); memory is released automatically on return.
     *
     * GOTCHA: The stack is small (typically around 512 KB - 1 MB, set with -Xss). Unbounded
     *         recursion ends in StackOverflowError, which you CAN catch - but should fix instead.
     */

    /** Method names of the current call stack, innermost first (levelThree, levelTwo, ...). */
    public static List<String> callStackNames() {
        return levelOne();
    }

    private static List<String> levelOne() {
        return levelTwo();
    }

    private static List<String> levelTwo() {
        return levelThree();
    }

    private static List<String> levelThree() {
        List<String> names = new ArrayList<>();
        for (StackTraceElement frame : new Throwable().getStackTrace()) {
            names.add(frame.getMethodName());
        }
        return names;
    }

    /** Recurses without end and returns how many frames fit before StackOverflowError. */
    public static int stackOverflowDepth() {
        recursionCounter = 0;
        try {
            recurseForever();
        } catch (StackOverflowError expected) {
            return recursionCounter;
        }
        return recursionCounter;
    }

    private static void recurseForever() {
        recursionCounter++;
        recurseForever();
    }

    public static void stacksDemo() {
        System.out.println("call stack (innermost first): " + callStackNames().subList(0, 4));
        System.out.println("frames before StackOverflowError: " + stackOverflowDepth());
    }

    // =========================================================================
    // 2. Stack vs heap
    // =========================================================================

    /*
     * WHAT: Local variables live in the frame. Objects live on the heap. A reference variable
     *       is a small value in the frame that points at the heap object.
     *
     * WHY:  Java always passes arguments BY VALUE - but for objects the value is the reference.
     *       So a method can mutate the caller's object, yet it can never re-point the caller's variable.
     *
     * GOTCHA: `sb = new StringBuilder(...)` inside a method only changes the method's own copy
     *         of the reference.
     */

    public static int incrementPrimitive(int value) {
        value++;
        return value;
    }

    public static void appendWorld(StringBuilder builder) {
        builder.append(" world");
    }

    public static void reassignParameter(StringBuilder builder) {
        builder = new StringBuilder("replaced");
        builder.append("!");
    }

    public static void stackVersusHeapDemo() {
        int number = 1;
        incrementPrimitive(number);
        StringBuilder text = new StringBuilder("hello");
        appendWorld(text);
        reassignParameter(text);
        System.out.println("primitive after call: " + number);
        System.out.println("builder after calls : " + text);
    }

    // =========================================================================
    // 3. Heap
    // =========================================================================

    /*
     * WHAT: The heap holds all objects and arrays and is shared by all threads. Its size is bounded
     *       by -Xmx (Runtime.maxMemory()); the currently committed part is Runtime.totalMemory().
     *
     * WHY:  When an allocation cannot be satisfied (even after GC) the JVM throws OutOfMemoryError.
     *       Array length is also capped by the VM itself: new byte[Integer.MAX_VALUE] fails
     *       no matter how large -Xmx is.
     *
     * String literals are kept in the String pool (on the heap since Java 7): equal literals are the
     * same object, `new String(...)` is a different object, intern() returns the pooled one.
     */

    /** {@code totalMemory() <= maxMemory()}: committed heap never exceeds the limit. */
    public static boolean heapLimitsAreConsistent() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() <= runtime.maxMemory();
    }

    /** Tries an allocation no JVM can satisfy and reports whether OutOfMemoryError was thrown. */
    public static boolean oversizedArrayThrowsOutOfMemory() {
        try {
            sink = new byte[Integer.MAX_VALUE];
            return false;
        } catch (OutOfMemoryError expected) {
            sink = null;
            return true;
        }
    }

    /** {literal == new String, literal.equals(new String), new String.intern() == literal}. */
    public static boolean[] stringPoolIdentity() {
        String literal = "jvm";
        String created = new String("jvm");
        return new boolean[] {literal == created, literal.equals(created), created.intern() == literal};
    }

    public static void heapDemo() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("max heap (MB)   : " + runtime.maxMemory() / (1024 * 1024));
        System.out.println("limits consistent: " + heapLimitsAreConsistent());
        System.out.println("huge array -> OOM : " + oversizedArrayThrowsOutOfMemory());
        boolean[] pool = stringPoolIdentity();
        System.out.println("pool: ==" + pool[0] + ", equals=" + pool[1] + ", intern==" + pool[2]);
    }

    // =========================================================================
    // 4. Class metadata and statics
    // =========================================================================

    /*
     * WHAT: For every loaded class the JVM keeps metadata (field/method layout, constant pool,
     *       bytecode). Since Java 8 this lives in native memory called Metaspace
     *       (before: PermGen). It is NOT part of the heap.
     *
     * WHY:  Static fields belong to the class, not to instances: one copy per class (per loader).
     *       Defining classes dynamically grows class metadata - a source of "leaks" in plugin and
     *       redeploy scenarios, because a class is only unloaded together with its loader.
     *
     * Observation: ClassLoadingMXBean counts every class ever defined; MemoryMXBean reports used
     *              heap and non-heap bytes (Metaspace and code cache are non-heap).
     */

    public static class Slot {
        public static int shared;
        public int own;

        public void bump() {
            shared++;
            own++;
        }
    }

    /** {a.own, b.own, Slot.shared} after a.bump() twice and b.bump() once → {2, 1, 3}. */
    public static int[] staticVersusInstanceFields() {
        Slot.shared = 0;
        Slot a = new Slot();
        Slot b = new Slot();
        a.bump();
        a.bump();
        b.bump();
        return new int[] {a.own, b.own, Slot.shared};
    }

    /** How many more classes the JVM reports as loaded after defining one new class. */
    public static long classesLoadedByDefiningOneClass() throws ClassNotFoundException {
        ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
        long before = bean.getTotalLoadedClassCount();
        ClassLoaderDemo.isolatedGreeterClass();
        return bean.getTotalLoadedClassCount() - before;
    }

    /** {used heap bytes, used non-heap bytes}. */
    public static long[] heapAndNonHeapUsedBytes() {
        return new long[] {
            ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed(),
            ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed()
        };
    }

    public static void classMetadataDemo() {
        try {
            int[] counts = staticVersusInstanceFields();
            System.out.println("a.own=" + counts[0] + ", b.own=" + counts[1] + ", shared=" + counts[2]);
            System.out.println("classes added by one define: " + classesLoadedByDefiningOneClass());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        long[] used = heapAndNonHeapUsedBytes();
        System.out.println("heap used > 0: " + (used[0] > 0) + ", non-heap used > 0: " + (used[1] > 0));
    }
}
