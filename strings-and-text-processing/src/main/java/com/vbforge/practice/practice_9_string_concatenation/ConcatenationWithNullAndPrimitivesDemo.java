package com.vbforge.practice.practice_9_string_concatenation;

/**
 * How + converts non-String operands: null becomes "null", booleans and chars become text.
 * (Left-to-right evaluation order itself is covered in basics-operators-and-expressions.)
 */
public class ConcatenationWithNullAndPrimitivesDemo {

    public static void main(String[] args) {
        String s = new String("Java");
        String t = new String();
        String r = null;
        r = s + t + r;            // "Javanull": null is converted to the text "null"
        System.out.println("s + t + r   = " + r);
        r = s + t + 'r';
        System.out.println("s + t + 'r' = " + r);
        // r = s & t & r;   // compile error: & is not defined for Strings
        // r = s && t && r; // compile error: && needs booleans

        String mixed = 1 + Integer.parseInt("2") + " java" + true + false + null;
        System.out.println("mixed concat: " + mixed); // 3 javatruefalsenull
    }
}
