package com.vbforge.practice.practice_12_type_casting;

/**
 * Narrowing to byte keeps the low 8 bits and reinterprets them as signed.
 */
public class ByteTruncationDemo {

    public static void main(String[] args) {
        System.out.println("(byte) 128 = " + (byte) 128); // -128
        System.out.println("(byte) 129 = " + (byte) 129); // -127
        System.out.println("(byte) 130 = " + (byte) 130); // -126
        System.out.println("(byte) 256 = " + (byte) 256); // 0
        System.out.println("(byte) 257 = " + (byte) 257); // 1

        int negative = -130;
        int overflow = 300;
        System.out.println("(byte) -130 = " + (byte) negative); // 126
        System.out.println("(byte)  300 = " + (byte) overflow);  // 44

        int intValue = 34567;
        short shortValue = (short) intValue;
        System.out.println("(short) 34567 = " + shortValue);    // -30969
    }
}
