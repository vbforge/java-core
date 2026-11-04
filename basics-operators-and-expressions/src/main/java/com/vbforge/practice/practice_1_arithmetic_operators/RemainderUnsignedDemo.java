package com.vbforge.practice.practice_1_arithmetic_operators;

/**
 * Unsigned remainder. `%` is signed; Java 8+ adds Integer/Long.remainderUnsigned,
 * which treats the bits of a negative number as a large positive value.
 */
public class RemainderUnsignedDemo {

    public static void main(String[] args) {
        System.out.println("signed   -2 % 3                  = " + (-2 % 3));  // -2 (sign of dividend)

        System.out.println("\n--- unsigned remainder ---");
        System.out.println("Integer.remainderUnsigned(-2, 3) = " + Integer.remainderUnsigned(-2, 3)); // 2
        System.out.println("Long.remainderUnsigned(-2, 3)    = " + Long.remainderUnsigned(-2, 3));    // 2

        System.out.println("\n--- unsigned interpretation of -2 ---");
        System.out.println("-2 as unsigned int = " + Integer.toUnsignedString(-2));   // 4294967294
        System.out.println("4294967294 % 3     = " + (4294967294L % 3));              // 2 — matches remainderUnsigned
    }
}
