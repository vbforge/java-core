package com.vbforge.practice.practice_6_checked_vs_unchecked;

/**
 * Errors are NOT exceptions.
 * Both extend Throwable, but Errors represent JVM-level failures
 * that application code should not try to catch.
 *
 *   StackOverflowError — infinite recursion exhausts the call stack
 *   OutOfMemoryError   — the JVM cannot allocate more heap
 *
 * The right response is to fix the code (add a base case, reduce memory usage),
 * not to catch these. The catch below exists only to show that the language
 * allows it.
 *
 * Note: `catch (Exception e)` would NOT catch a StackOverflowError - to catch it
 * you must name Error, StackOverflowError or Throwable.
 */
public class ErrorsVsExceptionsDemo {

    public static void main(String[] args) {
        System.out.println("--- finite recursion: OK ---");
        System.out.println("factorial(5) = " + factorial(5));

        System.out.println("\n--- infinite recursion: StackOverflowError ---");
        try {
            recurseForever();
        } catch (StackOverflowError e) {
            System.out.println("  caught StackOverflowError (a Throwable, not an Exception)");
        }
    }

    static int factorial(int n) {
        if (n == 0) return 1;                  // base case → prevents the error
        return n * factorial(n - 1);
    }

    static void recurseForever() {
        recurseForever();                       // no base case → stack overflow
    }
}
