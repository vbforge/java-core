package com.vbforge.practice.practice_1_arithmetic_operators;

/**
 * Integer division by zero throws ArithmeticException.
 * Floating-point division by zero follows IEEE-754 and yields:
 *   +Infinity, -Infinity, or NaN  — no exception.
 */
public class FloatingPointDivisionByZeroDemo {

    public static void main(String[] args) {
        System.out.println("--- double division by zero ---");
        System.out.println("-3.0 / 0   = " + (-3.0 / 0));    // -Infinity
        System.out.println(" 3.0 / 0   = " + ( 3.0 / 0));    //  Infinity
        System.out.println(" 0   / 4.0 = " + ( 0   / 4.0));  //  0.0
        System.out.println(" 0   / 0.0 = " + ( 0   / 0.0));  //  NaN

        System.out.println("\n--- NaN propagates through arithmetic ---");
        System.out.println("NaN + Infinity       = " + (Double.NaN + Double.POSITIVE_INFINITY));                       // NaN
        System.out.println("Infinity + 0.0       = " + (Double.POSITIVE_INFINITY + 0.0));                              // Infinity
        System.out.println("-Infinity * Infinity = " + (Double.NEGATIVE_INFINITY * Double.POSITIVE_INFINITY));         // -Infinity

        System.out.println("\n--- but integer division by zero throws ---");
        int zero = 0;
        try {
            int bad = 1 / zero;
            System.out.println(bad);
        } catch (ArithmeticException e) {
            System.out.println("ArithmeticException: " + e.getMessage());
        }
    }
}
