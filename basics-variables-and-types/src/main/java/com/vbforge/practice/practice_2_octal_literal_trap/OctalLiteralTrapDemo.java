package com.vbforge.practice.practice_2_octal_literal_trap;

/**
 * An integer literal with a leading zero is OCTAL: `010` is decimal 8, not ten.
 * (Binary literals start with 0b, hex with 0x.)
 */
public class OctalLiteralTrapDemo {

    public static void main(String[] args) {
        System.out.println("010   = " + 010);        // 8
        System.out.println("0b1010 = " + 0b1010);    // 10
        System.out.println("0xA    = " + 0xA);       // 10
        System.out.println("010 | 4 = " + (010 | 4)); // (8 | 4) = 12
    }
}
