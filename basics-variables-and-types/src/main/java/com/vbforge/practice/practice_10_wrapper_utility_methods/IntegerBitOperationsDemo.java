package com.vbforge.practice.practice_10_wrapper_utility_methods;

/**
 * Bit-level utility methods on Integer. 500 = 0b111110100.
 */
public class IntegerBitOperationsDemo {

    public static void main(String[] args) {
        int a = 500;

        System.out.println("value           = " + a);
        System.out.println("highestOneBit   = " + Integer.highestOneBit(a));        // 256
        System.out.println("lowestOneBit    = " + Integer.lowestOneBit(a));         // 4
        System.out.println("bitCount        = " + Integer.bitCount(a));             // 6
        System.out.println("leadingZeros    = " + Integer.numberOfLeadingZeros(a)); // 23
        System.out.println("padded binary   = "
                + "0".repeat(Integer.numberOfLeadingZeros(a)) + Integer.toBinaryString(a));
        System.out.println("reverse(1)      = " + Integer.reverse(1));              // -2147483648
    }
}
