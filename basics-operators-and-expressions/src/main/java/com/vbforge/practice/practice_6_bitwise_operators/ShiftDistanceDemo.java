package com.vbforge.practice.practice_6_bitwise_operators;

/**
 * Shift-distance rules.
 *   - for `int`  shifts, only the low 5 bits of the distance are used (mod 32)
 *   - for `long` shifts, only the low 6 bits are used (mod 64)
 *   - the distance operand may be byte / short / char / int / long
 *     (a long distance is legal; only its low bits matter)
 *   - double / float / String as a distance → compile error
 */
public class ShiftDistanceDemo {

    public static void main(String[] args) {
        int i = 1;
        int j = 3;
        long l = 3L;

        System.out.println("--- legal shift distances ---");
        System.out.println("i << j  = " + (i << j));  // 8
        System.out.println("i << l  = " + (i << l));  // 8  — long distance is allowed

        // i << 3.0;   // compile error: double as shift distance
        // i << "3";   // compile error: String as shift distance

        System.out.println("\n--- masking: only low 5 bits of distance used for int ---");
        System.out.println("1 << 32 = " + (1 << 32));   // 1  (32 % 32 == 0)
        System.out.println("1 << 33 = " + (1 << 33));   // 2  (33 % 32 == 1)

        System.out.println("\n--- masking: only low 6 bits of distance used for long ---");
        System.out.println("1L << 64 = " + (1L << 64)); // 1  (64 % 64 == 0)
        System.out.println("1L << 65 = " + (1L << 65)); // 2  (65 % 64 == 1)
    }
}
