package com.vbforge.class_loading_and_jvm_architecture;

import java.util.List;

/**
 * Practice tasks for JVM memory areas. Read {@link MemoryAreasDemo} first.
 */
public class MemoryAreasTasks {

    /**
     * Return the method names currently on this thread's call stack, innermost first,
     * starting with the method that CALLED this one. Neither {@code currentCallStack}
     * itself nor any JDK method used to read the stack may appear in the result.
     *
     * <p>Examples:
     * <pre>
     * void outer() { inner(); }
     * void inner() { currentCallStack(); }
     *
     * called from outer() → ["inner", "outer", ...frames of whoever called outer]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A {@code Throwable} knows the stack where it was created.</li>
     *   <li>Look at what sits at the very top of the stack-trace array.</li>
     * </ul>
     *
     * @return method names, innermost caller first
     */
    public List<String> currentCallStack() {
        throw new UnsupportedOperationException("TODO: implement currentCallStack");
    }

    /**
     * Recurse (one call per level) until either {@code limit} levels have been reached
     * or the JVM throws {@link StackOverflowError}; return how many levels were reached.
     * The error must be handled inside this method.
     *
     * <p>Examples:
     * <pre>
     * recursionDepth(0)                 → 0
     * recursionDepth(10)                → 10
     * recursionDepth(500)               → 500
     * recursionDepth(Integer.MAX_VALUE) → some number below Integer.MAX_VALUE
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A loop will never overflow the stack; you need real method calls.</li>
     *   <li>Keep the counter somewhere that survives the unwinding.</li>
     * </ul>
     *
     * @param limit maximum number of levels to reach, zero or more
     * @return levels reached
     */
    public int recursionDepth(int limit) {
        throw new UnsupportedOperationException("TODO: implement recursionDepth");
    }

    /**
     * Swap the text held by two builders. The caller keeps its own references, so you cannot
     * swap the variables - change what the two objects contain.
     *
     * <p>Examples:
     * <pre>
     * a = "left",  b = "right"   → a = "right", b = "left"
     * a = "",      b = "abc"     → a = "abc",   b = ""
     * a = "same",  b = "same"    → unchanged
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Reassigning a parameter only changes your local copy of the reference.</li>
     *   <li>Remember the old text of one builder before overwriting it.</li>
     * </ul>
     *
     * @param a first builder, mutated in place
     * @param b second builder, mutated in place
     */
    public void swapContents(StringBuilder a, StringBuilder b) {
        throw new UnsupportedOperationException("TODO: implement swapContents");
    }
}
