package com.vbforge.practice.practice_3_lambda_variable_capture;

import java.util.function.Predicate;

/**
 * A lambda captures the VALUE of a local variable, and the compiler enforces that the
 * variable is never reassigned (effectively final).
 *
 * <p>Why? A lambda may outlive the stack frame it was created in (for example when it
 * is passed to another thread). Allowing mutation of captured locals would create data races.
 */
public class EffectivelyFinalCaptureDemo {

    public static void main(String[] args) {
        int threshold = 42;                          // effectively final: never reassigned
        // threshold = 50;                           // <-- uncomment: compile error

        Predicate<Integer> above = n -> n > threshold;   // captures 'threshold' by value

        System.out.println("70 > 42? " + above.test(70));
        System.out.println("10 > 42? " + above.test(10));
    }
}
