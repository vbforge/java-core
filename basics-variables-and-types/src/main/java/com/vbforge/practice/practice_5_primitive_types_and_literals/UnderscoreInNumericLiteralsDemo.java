package com.vbforge.practice.practice_5_primitive_types_and_literals;

/**
 * Underscores in numeric literals (Java 7+): legal only BETWEEN digits.
 *
 * Compile-time errors are documented inline - uncomment to reproduce.
 */
public class UnderscoreInNumericLiteralsDemo {

    public static void main(String[] args) {
        double m1 = 5_000_000.75;
        int    m4 = 5_000_000;
        // long m2 = _5_000_000;   // compile error: cannot start with _
        // int  m6 = 5_000_000_;   // compile error: cannot end with _
        // int  m  = 0b_1010;      // compile error: no _ between the prefix and the digits

        System.out.println("m1 = " + m1);
        System.out.println("m4 = " + m4);
    }
}
