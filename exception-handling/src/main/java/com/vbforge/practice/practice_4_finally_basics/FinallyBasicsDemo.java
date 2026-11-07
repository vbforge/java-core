package com.vbforge.practice.practice_4_finally_basics;

/**
 * try-catch-finally.
 *
 * KEY RULE: the `finally` block runs whatever happens in try/catch — normal
 * completion, a caught exception, a `return`, or an exception thrown by the catch.
 * (The one exception to the rule is System.exit(), see practice_5_finally_gotchas.)
 */
public class FinallyBasicsDemo {

    public static void main(String[] args) {
        System.out.println("--- try-catch-finally ---");
        tryCatchFinally();

        System.out.println("\n--- try-finally (no catch) — valid ---");
        tryFinally();
    }

    static void tryCatchFinally() {
        try {
            int a = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("  caught: " + e.getMessage());
        } finally {
            System.out.println("  finally: runs regardless of what happened");
        }
    }

    static void tryFinally() {
        // No catch — but finally still runs.
        try {
            System.out.println("  inside try");
        } finally {
            System.out.println("  inside finally");
        }
    }
}
