package com.vbforge.practice.practice_7_string_inspection;

/** contains / startsWith / endsWith yes-no checks. */
public class ContainsStartsEndsDemo {

    public static void main(String[] args) {
        String text = "  Hello, World!  ";
        System.out.println("contains(\"World\"): " + text.contains("World"));
        System.out.println("startsWith(\"  H\"): " + text.startsWith("  H"));
        System.out.println("endsWith(\"!  \"):   " + text.endsWith("!  "));
        System.out.println("\"Hello\".contains(\"ell\"): " + "Hello".contains("ell"));
    }
}
