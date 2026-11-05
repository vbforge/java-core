package com.vbforge.practice.practice_7_string_inspection;

/** length() and charAt(): first, last and an inner character. */
public class LengthAndCharAtDemo {

    public static void main(String[] args) {
        String walk = "Hello";
        System.out.println("length():        " + walk.length());
        System.out.println("charAt(0):       " + walk.charAt(0));
        System.out.println("charAt(len-1):   " + walk.charAt(walk.length() - 1));

        String text = "  Hello, World!  ";
        System.out.println("padded length(): " + text.length());
        System.out.println("padded charAt(7):" + text.charAt(7)); // ','  (the two leading spaces count)

        String w = "TEST";
        System.out.println("How many letters in this word: " + w);
        System.out.println("Letters = " + w.length());
    }
}
