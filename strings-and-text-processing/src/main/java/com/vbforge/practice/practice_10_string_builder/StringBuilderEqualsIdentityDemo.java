package com.vbforge.practice.practice_10_string_builder;

/** StringBuilder does not override equals(): two builders with the same text are not equal. */
public class StringBuilderEqualsIdentityDemo {

    public static void main(String[] args) {
        StringBuilder sb1 = new StringBuilder("I like Java.");
        StringBuilder sb2 = new StringBuilder(sb1);
        System.out.println("sb1.equals(sb2):                       " + sb1.equals(sb2)); // false
        System.out.println("sb1.toString().equals(sb2.toString()): " + sb1.toString().equals(sb2.toString())); // true
    }
}
