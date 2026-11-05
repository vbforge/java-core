package com.vbforge.practice.practice_8_string_transformation;

/** toLowerCase / toUpperCase return new Strings. */
public class CaseConversionDemo {

    public static void main(String[] args) {
        String text = "  Hello, World!  ";
        System.out.println("toLowerCase(): \"" + text.strip().toLowerCase() + "\"");
        System.out.println("toUpperCase(): \"" + text.strip().toUpperCase() + "\"");
    }
}
