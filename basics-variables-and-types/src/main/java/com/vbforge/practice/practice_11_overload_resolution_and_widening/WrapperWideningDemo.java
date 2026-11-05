package com.vbforge.practice.practice_11_overload_resolution_and_widening;

/**
 * Widening does NOT apply between wrapper types.
 * `Byte` will not auto-widen to `Short` / `Integer` / `Long` even though the
 * underlying primitive would.
 *
 * In practice: a wrapper argument needs a parameter of exactly that wrapper type
 * (or a primitive parameter, where unboxing then widening is allowed).
 */
public class WrapperWideningDemo {

    public static void main(String[] args) {
        Byte b = 1;
        printByte(b);    // exact match
        // printShort(b);   // compile error - Byte does not widen to Short
        // printInt(b);     // compile error - Byte does not widen to Integer
        // printLong(b);    // compile error - Byte does not widen to Long

        Short s = 450;
        // printByte(s);  // compile error
        printShort(s);
        // printInt(s);   // compile error
        // printLong(s);  // compile error

        Integer i = 450;
        // printByte(i);  // compile error
        // printShort(i); // compile error
        printInt(i);
        // printLong(i);  // compile error

        Long l = 450L;
        // printByte(l);  // compile error
        // printShort(l); // compile error
        // printInt(l);   // compile error
        printLong(l);
    }

    static void printByte(Byte v)    { System.out.println("printByte  : " + v); }
    static void printShort(Short v)  { System.out.println("printShort : " + v); }
    static void printInt(Integer v)  { System.out.println("printInt   : " + v); }
    static void printLong(Long v)    { System.out.println("printLong  : " + v); }
}
