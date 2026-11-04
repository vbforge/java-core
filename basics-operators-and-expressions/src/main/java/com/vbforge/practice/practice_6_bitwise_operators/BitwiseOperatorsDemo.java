package com.vbforge.practice.practice_6_bitwise_operators;

/**
 * Bitwise and shift operators on ints.
 *   &  AND      |  OR      ^  XOR      ~  NOT
 *   << left    >> arithmetic right    >>> logical right
 */
public class BitwiseOperatorsDemo {

    public static void main(String[] args) {
        System.out.println("--- int literals ---");
        int a = 60;   // 0b0011_1100
        int b = 13;   // 0b0000_1101
        System.out.println("a & b   = " + (a & b));   // 12
        System.out.println("a | b   = " + (a | b));   // 61
        System.out.println("a ^ b   = " + (a ^ b));   // 49
        System.out.println("~a      = " + (~a));      // -61
        System.out.println("a << 2  = " + (a << 2));  // 240
        System.out.println("a >> 2  = " + (a >> 2));  // 15
        System.out.println("a >>> 2 = " + (a >>> 2)); // 15

        System.out.println("\n--- same operations with binary literals (clearer) ---");
        int b1 = 0b1011;   // 11
        int b2 = 0b0101;   //  5
        System.out.println("b1 | b2 = " + (b1 | b2)); // 15
        System.out.println("b1 & b2 = " + (b1 & b2)); //  1
        System.out.println("b1 ^ b2 = " + (b1 ^ b2)); // 14
        System.out.println("~b2     = " + (~b2));     // -6

        System.out.println("\n--- >> vs >>> on a negative int ---");
        int negative = -8;
        System.out.println("-8 >>  1 = " + (negative >>  1)); // -4  (sign-extending)
        System.out.println("-8 >>> 1 = " + (negative >>> 1)); // 2147483644 (zero-filled)
    }
}
