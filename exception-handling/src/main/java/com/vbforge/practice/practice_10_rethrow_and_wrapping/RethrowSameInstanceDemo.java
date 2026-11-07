package com.vbforge.practice.practice_10_rethrow_and_wrapping;

/**
 * Rethrowing the SAME exception instance preserves its stack trace.
 * `throw e;` — e is the original; the trace was filled in where it was created.
 *
 * Compare with `throw new RuntimeException(e);` or `throw new X(e.getMessage())`:
 *   - the first keeps the original reachable through getCause()
 *   - the second DISCARDS the original and its trace (only the message survives)
 *
 * Standard pattern: log at intermediate layers, rethrow unchanged, handle at the top.
 * Code after the call that rethrows is never reached.
 */
public class RethrowSameInstanceDemo {

    private static ClassCastException original;

    public static void main(String[] args) {
        try {
            logAndRethrow();
            System.out.println("this never prints");   // never reached
        } catch (ClassCastException e) {
            System.out.println("main: caught " + e.getClass().getSimpleName());
            System.out.println("main: same instance as the original? " + (e == original));
        }
    }

    static void logAndRethrow() {
        try {
            original = new ClassCastException("Demo");
            throw original;
        } catch (ClassCastException exp) {
            System.out.println("logAndRethrow: log — exception in method");
            throw exp;                                  // same instance rethrown
        }
    }
}
