package com.vbforge.practice.practice_13_best_practices;

/**
 * Generalizing everything to `Exception` vs keeping exceptions specific.
 *
 * Bad:
 *   - `throws Exception` on a method that only fails with two specific exceptions
 *   - the caller must dig through getCause() with instanceof to find out what happened
 *
 * Good:
 *   - leave the exceptions specific; the caller catches exactly what it can handle
 *   - when wrapping, always pass the original as the cause
 *
 * Both inputs are tried so that both branches run: "notANumber" raises
 * NumberFormatException, "abc" raises IllegalArgumentException.
 */
public class GeneralizedThrowsDemo {

    public static void main(String[] args) {
        String[] inputs = {"notANumber", "abc"};

        System.out.println("--- bad: generalize everything to Exception ---");
        for (String input : inputs) {
            runPoorApproach(input);
        }

        System.out.println("\n--- good: specific exceptions from doSomething ---");
        for (String input : inputs) {
            runSpecificApproach(input);
        }
    }

    static void runPoorApproach(String input) {
        try {
            poorApproach(input);
        } catch (Exception e) {
            if (e.getCause() instanceof NumberFormatException) {
                System.out.println("  [BAD] hiding NumberFormatException inside Exception");
            } else if (e.getCause() instanceof IllegalArgumentException) {
                System.out.println("  [BAD] hiding IllegalArgumentException inside Exception");
            } else {
                System.out.println("  [BAD] unknown error: " + e.getMessage());
            }
        }
    }

    static void poorApproach(String input) throws Exception {
        try {
            doSomething(input);
        } catch (NumberFormatException e) {
            throw new Exception("Caught NumberFormatException: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new Exception("Caught IllegalArgumentException: " + e.getMessage(), e);
        }
    }

    static void runSpecificApproach(String input) {
        try {
            doSomething(input);
        } catch (NumberFormatException e) {
            System.out.println("  [GOOD] NumberFormatException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("  [GOOD] IllegalArgumentException: " + e.getMessage());
        }
    }

    static void doSomething(String input) {
        if (input.equals("notANumber"))
            throw new NumberFormatException("Input is not a valid number");
        if (input.length() < 5)
            throw new IllegalArgumentException("Input must be at least 5 characters");
    }
}
