package com.vbforge.practice.practice_8_string_transformation;

/** substring(begin, endExclusive) and substring(begin). */
public class SubstringDemo {

    public static void main(String[] args) {
        String text = "  Hello, World!  ".strip(); // "Hello, World!"
        System.out.println("substring(7, 12): \"" + text.substring(7, 12) + "\""); // World
        System.out.println("substring(7):     \"" + text.substring(7) + "\"");     // World!
    }
}
