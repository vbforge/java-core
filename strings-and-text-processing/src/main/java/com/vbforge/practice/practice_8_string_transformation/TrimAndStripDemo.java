package com.vbforge.practice.practice_8_string_transformation;

/** trim() (ASCII <= ' ') vs strip() (Unicode-aware, Java 11+), plus one-sided stripping. */
public class TrimAndStripDemo {

    public static void main(String[] args) {
        String text = "  Hello, World!  ";
        System.out.println("original:        \"" + text + "\"");
        System.out.println("trim():          \"" + text.trim() + "\"");
        System.out.println("strip():         \"" + text.strip() + "\"");
        System.out.println("stripLeading():  \"" + "  hi  ".stripLeading() + "\"");
        System.out.println("stripTrailing(): \"" + "  hi  ".stripTrailing() + "\"");
    }
}
