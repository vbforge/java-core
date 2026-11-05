package com.vbforge.practice.practice_10_string_builder;

/** Reverse a String with StringBuilder.reverse(). */
public class ReverseWithStringBuilderDemo {

    public static void main(String[] args) {
        System.out.println("reverseString: " + reverseString("Hello my friend!"));
    }

    static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
