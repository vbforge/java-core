package com.vbforge.practice.practice_3_increment_decrement;

/**
 * Unary `-` and `+`, plus the self-assignment forms of `++`.
 *
 * Unary plus is a no-op (apart from promoting byte/short/char to int).
 * Unary minus negates; it binds tighter than binary `*`, so `b * -a` is `b * (-a)`.
 *
 * `x = ++x` increments, but `x = x++` is a trap: the old value is assigned back
 * and the increment is lost.
 */
public class UnaryPlusMinusDemo {

    public static void main(String[] args) {
        System.out.println("--- unary minus ---");
        int a = 4;
        int x = -a;
        System.out.println("-a         = " + x);         // -4

        int b = 2;
        System.out.println("b * -a     = " + (b * -a)); // -8
        System.out.println("b * (-a)   = " + (b * (-a))); // -8 (same)

        System.out.println("\n--- unary plus is a no-op ---");
        byte bv = 5;
        int promoted = +bv;                             // promotion to int happens anyway
        System.out.println("+bv        = " + promoted);

        System.out.println("\n--- x = ++x vs x = x++ ---");
        int k = 1;
        k = ++k;
        System.out.println("k = ++k → " + k);           // 2

        int r = 1;
        r = r++;
        System.out.println("r = r++ → " + r);           // 1 — the increment result is discarded
    }
}
