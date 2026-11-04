package com.vbforge.practice.practice_6_bitwise_operators;

/**
 * `>>>=` on narrow types is a footgun.
 *
 *   int  i = -1; i >>>= 10;  → 4194303  (upper 10 bits zero-filled)
 *   long l = -1; l >>>= 10;  → huge positive
 *   short s = -1; s >>>= 10; → -1       (BUG: shift happens on int, then truncates
 *                                        back to short where the low 16 bits are all 1s)
 *   byte  b = -1; b >>>= 10; → -1       (same problem, worse)
 *
 * To see the "real" result, cast to int BEFORE shifting (mask off the sign extension).
 */
public class UnsignedRightShiftOnWidenedTypesDemo {

    public static void main(String[] args) {
        int i = -1;
        i >>>= 10;
        System.out.println("int  -1 >>>= 10 : " + i);       // 4194303

        long l = -1;
        l >>>= 10;
        System.out.println("long -1 >>>= 10 : " + l);       // 18014398509481983

        short s = -1;
        s >>>= 10;
        System.out.println("short -1 >>>= 10: " + s);       // -1  ← surprise

        byte b = -1;
        b >>>= 10;
        System.out.println("byte  -1 >>>= 10: " + b);       // -1  ← surprise

        System.out.println("\n--- print the shift result without reassigning ---");
        b = -1;
        System.out.println("byte -1 >>> 10   : " + (b >>> 10)); // 4194303 (int view)

        System.out.println("\n--- correct narrow shift: mask first ---");
        short s2 = -1;
        s2 = (short) ((s2 & 0xFFFF) >>> 10);
        System.out.println("(short)((s2 & 0xFFFF) >>> 10) = " + s2); // 63
    }
}
