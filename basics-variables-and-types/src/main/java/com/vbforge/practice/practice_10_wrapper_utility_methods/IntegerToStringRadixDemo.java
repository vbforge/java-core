package com.vbforge.practice.practice_10_wrapper_utility_methods;

/**
 * Formatting integers to different radices (2..36).
 * Negative numbers keep the sign in toString(value, radix); toUnsignedString does not.
 */
public class IntegerToStringRadixDemo {

    public static void main(String[] args) {
        int value = 255;
        System.out.println("decimal   = " + Integer.toString(value));
        System.out.println("hex       = " + Integer.toString(value, 16));
        System.out.println("octal     = " + Integer.toString(value, 8));
        System.out.println("binary    = " + Integer.toString(value, 2));

        int negative = -255;
        System.out.println("\nsigned negative:");
        System.out.println("decimal   = " + Integer.toString(negative));
        System.out.println("hex       = " + Integer.toString(negative, 16)); // -ff
        System.out.println("octal     = " + Integer.toString(negative, 8));  // -377
        System.out.println("binary    = " + Integer.toString(negative, 2));  // -11111111

        System.out.println("\nunsigned negative (two's complement view):");
        System.out.println("decimal   = " + Integer.toUnsignedString(negative));
        System.out.println("hex       = " + Integer.toUnsignedString(negative, 16));
        System.out.println("octal     = " + Integer.toUnsignedString(negative, 8));
        System.out.println("binary    = " + Integer.toUnsignedString(negative, 2));

        System.out.println("\nconvenience methods (same as toUnsignedString(.., radix)):");
        System.out.println("hex       = " + Integer.toHexString(negative));
        System.out.println("octal     = " + Integer.toOctalString(negative));
        System.out.println("binary    = " + Integer.toBinaryString(negative));
    }
}
