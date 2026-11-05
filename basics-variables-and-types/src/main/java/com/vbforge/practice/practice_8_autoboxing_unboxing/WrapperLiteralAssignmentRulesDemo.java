package com.vbforge.practice.practice_8_autoboxing_unboxing;

/**
 * Which literals can be assigned to a wrapper variable.
 * Boxing never combines with widening: a float or long literal does not become a Double.
 *
 * Compile-time errors are documented inline - uncomment to reproduce.
 */
public class WrapperLiteralAssignmentRulesDemo {

    public static void main(String[] args) {
        double  d1 = 0.0f;          // float literal widens to a primitive double
        Double  d5 = 0.d;           // legal: trailing-d decimal
        Integer i2 = 85;            // int literal boxes to Integer

        // Double d2 = new Double(.0f);   // compiles but deprecated for removal
        // Double d3 = 0.0f;              // compile error: float -> Double needs widening + boxing
        // Double d4 = new Double("1L");  // NumberFormatException at runtime
        // Double d6 = 1L;                // compile error: long literal -> Double
        // int i3 = Integer.decode('12'); // compile error: char, not String
        // int i5 = Integer.valueOf(null);// NumberFormatException at runtime (null String)
        // int i6 = new Integer("15");    // deprecated for removal

        System.out.println("d1 = " + d1 + ", d5 = " + d5 + ", i2 = " + i2);
    }
}
