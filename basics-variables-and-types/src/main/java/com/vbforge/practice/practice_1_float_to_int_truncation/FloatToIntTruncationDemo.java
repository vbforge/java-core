package com.vbforge.practice.practice_1_float_to_int_truncation;

/**
 * Casting float/double to int/char TRUNCATES toward zero — it does not round.
 * `(int) 3.9 → 3`, `(int) -3.9 → -3`, `(char) ('a' + 0.9) → 'a'` (not 'b').
 */
public class FloatToIntTruncationDemo {

    public static void main(String[] args) {
        double above = 0.7, below = 0.4;
        System.out.println("above = " + above);
        System.out.println("below = " + below);
        System.out.println("(int) above           = " + (int) above);           // 0
        System.out.println("(int) below           = " + (int) below);           // 0
        System.out.println("(int) 3.9             = " + (int) 3.9);             // 3
        System.out.println("(int) -3.9            = " + (int) -3.9);            // -3 (toward zero)
        System.out.println("(char)('a' + above)   = " + (char) ('a' + above)); // a
        System.out.println("(char)('a' + below)   = " + (char) ('a' + below)); // a

        System.out.println("\n--- for real rounding use Math.round ---");
        System.out.println("Math.round(0.7) = " + Math.round(0.7));           // 1
        System.out.println("Math.round(0.4) = " + Math.round(0.4));           // 0
    }
}
