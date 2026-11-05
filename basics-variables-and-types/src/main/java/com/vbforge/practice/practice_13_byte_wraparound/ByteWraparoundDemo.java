package com.vbforge.practice.practice_13_byte_wraparound;

/**
 * byte wraps silently: MAX_VALUE + 1 == MIN_VALUE.
 */
public class ByteWraparoundDemo {

    public static void main(String[] args) {
        byte b = Byte.MAX_VALUE;
        b++;
        System.out.println("Byte.MAX_VALUE + 1 = " + b); // -128

        byte a = 0;
        while (a >= 0) {
            a++;
        }
        System.out.println("loop exits at a = " + a); // -128
    }
}
