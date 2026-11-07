package com.vbforge.practice.practice_7_throw_and_throws;

/**
 * `throw`   — a statement inside a method that creates an exception
 * `throws`  — a method-signature declaration of which checked exceptions may escape
 *
 * Checked exceptions thrown with `throw` MUST also appear in the method's `throws`
 * or be caught in a `try`. Unchecked exceptions (RuntimeException) do NOT need a
 * `throws` declaration — the compiler doesn't enforce it.
 */
public class ThrowingAndThrowsDemo {

    public static void main(String[] args) {
        System.out.println("--- checked: must be caught or declared ---");
        try {
            throwChecked();
        } catch (Exception e) {
            System.out.println("  caught: " + e.getMessage());
        }

        System.out.println("\n--- unchecked: not declared, still catchable ---");
        try {
            throwUnchecked();
        } catch (RuntimeException e) {
            System.out.println("  caught: " + e.getMessage());
        }
    }

    // Checked: `throws Exception` required
    static void throwChecked() throws Exception {
        throw new Exception("checked test");
    }

    // Unchecked: no throws needed (adding `throws RuntimeException` is legal but noisy)
    static void throwUnchecked() {
        throw new RuntimeException("unchecked test");
    }
}
