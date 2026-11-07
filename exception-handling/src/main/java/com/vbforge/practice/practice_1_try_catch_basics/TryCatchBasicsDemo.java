package com.vbforge.practice.practice_1_try_catch_basics;

/**
 * try / catch basics:
 *   - the try block runs until an exception is thrown
 *   - the rest of the try block is skipped
 *   - control jumps to the matching catch block
 *   - execution continues AFTER the try-catch (unless the catch re-throws)
 */
public class TryCatchBasicsDemo {

    public static void main(String[] args) {
        System.out.println("--- normal flow: no exception ---");
        System.out.println("result = " + divide(10, 2));

        System.out.println("\n--- exception flow: caught ---");
        System.out.println("result = " + divide(10, 0));

        System.out.println("\n--- execution continues after catch ---");
        int a = 10;
        String name = null;
        try {
            a = name.length();       // throws NullPointerException, a is not assigned
            a++;                     // skipped
        } catch (RuntimeException e) {
            a++;                     // recovery path
        }
        System.out.println("a = " + a);   // 11 (10 -> NPE -> a++ in catch)
    }

    static int divide(int a, int b) {
        try {
            return a / b;
        } catch (ArithmeticException e) {
            System.out.println("  caught: " + e.getMessage());
            return 0;
        }
    }
}
