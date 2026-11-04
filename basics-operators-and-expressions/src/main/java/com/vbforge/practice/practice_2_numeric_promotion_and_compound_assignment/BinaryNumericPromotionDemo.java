package com.vbforge.practice.practice_2_numeric_promotion_and_compound_assignment;

/**
 * Binary numeric promotion — the rules Java applies before most arithmetic.
 *
 *   1. If either operand is double  → the other becomes double
 *   2. Else if either is float      → the other becomes float
 *   3. Else if either is long       → the other becomes long
 *   4. Else                          → both become int
 *
 * byte/short/char → int is the reason `byte + byte` needs a cast to be stored
 * back into a byte. (Compound assignment hides that cast: see CompoundAssignmentDemo.)
 */
public class BinaryNumericPromotionDemo {

    public static void main(String[] args) {
        System.out.println("--- byte + byte is promoted to int ---");
        byte a1 = 1, b1 = 2;
        // byte x1 = a1 + b1;         // compile error: int → byte needs a cast
        byte x1 = (byte) (a1 + b1);
        System.out.println("(byte)(a1 + b1) = " + x1);

        System.out.println("\n--- final byte + final byte = compile-time constant, no cast ---");
        final byte a = 1;
        final byte b = 2;
        byte x = a + b;               // OK: constant expression fits in byte
        System.out.println("byte x = a + b = " + x);

        System.out.println("\n--- (byte) 129 + 3 → int -124, NOT a byte ---");
        int aa = 129;
        byte bb = 3;
        Object ab = (byte) aa + bb;   // (byte)129 = -127, then promoted to int, + 3 = -124
        System.out.println(ab.getClass().getName() + " value: " + ab); // java.lang.Integer -124

        System.out.println("\n--- narrowing cast can truncate ---");
        int big = 5_000_000;
        short truncated = (short) big;
        System.out.println("(short) 5_000_000 = " + truncated);

        System.out.println("\n--- int + float → float ---");
        int    ia = 9;
        float  fb = 7;
        System.out.println("9 + 7f   = " + (ia + fb));       // float
        System.out.println("9 / 7f   = " + (ia / fb));       // float division, not int
        System.out.printf("9 / 7f   ≈ %.4f%n", (ia / fb));

        System.out.println("\n--- long + int → long (no int overflow) ---");
        long la = 2_147_483_647_000L;
        int  ib = 1;
        long sum = la + ib;           // computed as long: 2_147_483_647_001
        System.out.println("2_147_483_647_000L + 1 = " + sum);
    }
}
