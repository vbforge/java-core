package com.vbforge.practice.practice_5_primitive_types_and_literals;

/**
 * One example of each of the eight primitive types, with the literal forms they accept
 * (hex, suffixes L / F, exponent notation, char escapes).
 */
public class PrimitiveTypesDemo {

    public static void main(String[] args) {
        System.out.println("--- byte, short, int, long ---");
        int x = 0;
        long i = 0;
        byte a1 = (byte) 0xF1;      // 0xF1 is an int (241) -> the cast keeps the low 8 bits
        byte a2 = 0x07;
        byte byteVal = 100;         // -128..127
        short shortVal = 3266;
        int intValue = 557;
        long longVal = 23_232_323L; // L suffix
        System.out.println("x  = " + x);
        System.out.println("i  = " + i);
        System.out.println("a1 = " + a1);
        System.out.println("a2 = " + a2);
        System.out.println("byteVal  = " + byteVal);
        System.out.println("shortVal = " + shortVal);
        System.out.println("intValue = " + intValue);
        System.out.println("longVal  = " + longVal);

        System.out.println("--- float, double ---");
        float x1 = 3.5f, x2 = 3.7E6f, x3 = -1.8E-7f;
        float floatVal = 235.6F;    // F suffix: without it 235.6 is a double
        double z = 1.0;
        double doubleVal = 235.32;
        System.out.println("x1 = " + x1);
        System.out.println("x2 = " + x2);
        System.out.println("x3 = " + x3);
        System.out.println("floatVal  = " + floatVal);
        System.out.println("z  = " + z);
        System.out.println("doubleVal = " + doubleVal);

        System.out.println("--- char ---");
        char symbol = 'f';
        System.out.println("symbol = " + symbol);
        char bigB = '\u0042';
        System.out.println("bigB = " + bigB);
        char backspace = '\u0008';
        System.out.println("aaa" + backspace);
        char tab = '\u0009';
        System.out.println("a" + tab + "b" + tab + "c" + tab + "d");
        char newline = '\n';
        System.out.println("a, b, c, d;" + newline + "1, 2, 3, 4;");
        char quote = '\u0022';
        System.out.println(quote + "double quoted" + quote);
        char singleQuote = '\'';
        System.out.println(singleQuote + "single quoted" + singleQuote);
        char backslash = '\\';
        System.out.println("a" + backslash);

        System.out.println("--- boolean ---");
        boolean b1 = true;
        boolean b2 = false;
        System.out.println("b1 = " + b1);
        System.out.println("b2 = " + b2);
    }
}
