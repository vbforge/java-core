package com.vbforge.practice.practice_11_overload_resolution_and_widening;

/**
 * Primitive widening: byte -> short -> int -> long -> float -> double.
 * A smaller primitive can be passed to a method that takes a wider one (no cast needed);
 * the opposite direction is narrowing and does not compile.
 * The commented-out lines are compile-time errors - kept here as documentation.
 */
public class PrimitiveWideningDemo {

    public static void main(String[] args) {
        byte b = 1;
        printByte(b);    // exact match
        printShort(b);   // widening
        printInt(b);     // widening
        printLong(b);    // widening

        short s = 450;
        // printByte(s); // compile error: short -> byte is narrowing
        printShort(s);
        printInt(s);
        printLong(s);

        int i = 450;
        // printByte(i);  // compile error: int -> byte is narrowing
        // printShort(i); // compile error: int -> short is narrowing
        printInt(i);
        printLong(i);

        long l = 450L;
        // printByte(l);  // compile error
        // printShort(l); // compile error
        // printInt(l);   // compile error
        printLong(l);
    }

    static void printByte(byte v)   { System.out.println("printByte  : " + v); }
    static void printShort(short v) { System.out.println("printShort : " + v); }
    static void printInt(int v)     { System.out.println("printInt   : " + v); }
    static void printLong(long v)   { System.out.println("printLong  : " + v); }
}
