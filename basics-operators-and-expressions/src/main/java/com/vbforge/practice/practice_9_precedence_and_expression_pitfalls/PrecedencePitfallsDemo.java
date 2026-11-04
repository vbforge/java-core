package com.vbforge.practice.practice_9_precedence_and_expression_pitfalls;

/**
 * Precedence pitfalls between operators that people often mix up:
 * shift vs addition, bitwise AND vs equality, multiplication vs addition.
 */
public class PrecedencePitfallsDemo {

    public static void main(String[] args) {
        System.out.println("--- precedence: add / multiply / shift ---");
        System.out.println("1 + 2 << 3   = " + (1 + 2 << 3));     // (1+2) << 3 = 24
        System.out.println("1 + (2 << 3) = " + (1 + (2 << 3)));   // 1 + 16 = 17

        System.out.println("\n--- precedence: bitwise AND vs equality ---");
        System.out.println("(1 & 3) == 1 : " + ((1 & 3) == 1));   // true
        // `1 & 3 == 1` is parsed as `1 & (3 == 1)` → int & boolean → compile error;
        // the parentheses above are mandatory.

        System.out.println("\n--- when in doubt, parenthesise ---");
        int a = 5, b = 10, c = 3;
        System.out.println("a * b + c   = " + (a * b + c));    // 53
        System.out.println("a * (b + c) = " + (a * (b + c)));  // 65
    }
}
