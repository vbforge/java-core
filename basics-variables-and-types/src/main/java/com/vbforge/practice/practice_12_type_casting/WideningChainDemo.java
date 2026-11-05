package com.vbforge.practice.practice_12_type_casting;

/**
 * Widening (implicit) is safe: byte -> int -> long -> double.
 * Narrowing back to a type that can hold the value loses nothing.
 */
public class WideningChainDemo {

    public static void main(String[] args) {
        byte   b = 7;
        int    i = b;
        long   l = i;
        double d = l;
        System.out.println("widening: " + b + " -> " + i + " -> " + l + " -> " + d);

        int original = 123;
        int back = (int) (long) original; // round-trip via long, still 123
        System.out.println("(int)(long)123 = " + back);
    }
}
