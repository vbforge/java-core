package com.vbforge.practice.practice_3_char_as_int_value;

/**
 * `char` is a numeric type holding a Unicode code unit: '1' is 49, not 1.
 * Passing a char to an int parameter widens it to its numeric value.
 */
public class CharAsIntValueDemo {

    public static void main(String[] args) {
        char c = '1';
        System.out.println("c       = " + c);          // 1
        System.out.println("(int) c = " + (int) c);    // 49
        System.out.println("c + 1   = " + (c + 1));    // 50 (int arithmetic)
        System.out.println("widened = " + describe(c)); // 49
    }

    private static int describe(int value) {
        return value;
    }
}
