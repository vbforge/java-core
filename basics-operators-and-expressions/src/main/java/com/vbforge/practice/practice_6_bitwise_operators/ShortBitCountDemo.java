package com.vbforge.practice.practice_6_bitwise_operators;

/**
 * Counting set bits in a `short`.
 *
 * WARNING: this implementation (with `s >>>= 1`) is written for NON-NEGATIVE
 * shorts. `s >>>= 1` shifts the promoted 32-bit int and casts the result back to
 * short. For a negative short the upper bits are all 1s, so the shift pulls a 0
 * into bit 31 of the int, but the cast keeps only the low 16 bits, which are
 * still all 1s: the sign bit comes back every time and `s` never reaches 0
 * (for s = -1 it stays -1), so the loop never ends.
 * Use `Integer.bitCount(s & 0xFFFF)` in real code.
 */
public class ShortBitCountDemo {

    public static void main(String[] args) {
        System.out.println("bitCount((short)3)  = " + bitCount((short) 3));
        System.out.println("bitCount((short)7)  = " + bitCount((short) 7));
        System.out.println("bitCount((short)15) = " + bitCount((short) 15));

        // bitCount((short) -1); // infinite loop — see class javadoc

        System.out.println("\n--- correct alternative ---");
        System.out.println("Integer.bitCount(-1 & 0xFFFF) = " + Integer.bitCount(-1 & 0xFFFF));
    }

    static short bitCount(short s) {
        short bits = 0;
        while (s != 0) {
            bits += s & 1;   // inspect the low bit
            s >>>= 1;        // then shift right
        }
        return bits;
    }
}
