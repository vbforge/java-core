package com.vbforge.practice.practice_13_best_practices;

/**
 * Anti-pattern: swallowing an exception.
 *
 * `unsafeParse` catches the NumberFormatException and returns -1. The caller
 * cannot tell a failure from a real result — the failure is silent.
 * Better: let the exception propagate, or report the failure explicitly.
 */
public class SwallowedExceptionDemo {

    public static void main(String[] args) {
        int r = unsafeParse("not-a-number");
        System.out.println("  unsafeParse = " + r + "  ← silent failure, caller has no idea");
    }

    static int unsafeParse(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
