package com.vbforge.practice.practice_1_floating_point_error;

/**
 * float keeps only ~7 significant decimal digits, so different-looking values collapse
 * into the same float and == gives surprising "true" results.
 */
public class FloatLiteralEqualityTrapDemo {

    public static void main(String[] args) {
        // both literals are rounded to the same float (1.0f)
        System.out.println("1.00000001f == 1.00000002f : " + (1.00000001f == 1.00000002f)); // true

        // 1f / 3 is rounded, but multiplying by 3 rounds back to exactly 1.0f
        System.out.println("1 == 1f / 3 * 3             : " + (1 == 1f / 3 * 3));           // true
    }
}
