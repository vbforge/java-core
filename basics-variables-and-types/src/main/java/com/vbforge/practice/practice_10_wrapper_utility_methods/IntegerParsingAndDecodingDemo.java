package com.vbforge.practice.practice_10_wrapper_utility_methods;

/**
 * Three ways to parse an Integer from a String:
 *   - Integer.parseInt(s)          -> only base-10
 *   - Integer.decode(s)            -> auto-detects 0x / 0 prefixes
 *   - Integer.parseInt(s, radix)   -> explicit radix 2..36
 */
public class IntegerParsingAndDecodingDemo {

    public static void main(String[] args) {
        System.out.println("--- parseInt: signs ---");
        System.out.println(Integer.parseInt("-123"));
        System.out.println(Integer.parseInt("123"));
        System.out.println(Integer.parseInt("+123"));
        // Integer.parseInt("123_45");   // NumberFormatException - underscores not allowed in String input
        // Integer.parseInt("12ABCD");   // NumberFormatException

        System.out.println("\n--- decode: auto-detects 0x / 0 prefixes ---");
        System.out.println("decode(\"0xff\")  = " + Integer.decode("0xff"));   // 255
        System.out.println("decode(\"0377\")  = " + Integer.decode("0377"));   // 255 (octal)
        System.out.println("decode(\"255\")   = " + Integer.decode("255"));    // 255 (decimal)

        System.out.println("\n--- parseInt with explicit radix ---");
        System.out.println("parseInt(\"11111111\", 2) = " + Integer.parseInt("11111111", 2)); // 255
        System.out.println("parseInt(\"3333\", 4)     = " + Integer.parseInt("3333", 4));     // 255
        System.out.println("parseInt(\"FF\", 16)      = " + Integer.parseInt("FF", 16));      // 255

        // Integer.parseInt("0xff");   // NumberFormatException - parseInt does NOT auto-detect a prefix
    }
}
