package com.vbforge.practice.practice_5_string_creation_and_null;

/** Literal, concatenation, equals, length and a null reference check. */
public class StringLiteralsDemo {

    public static void main(String[] args) {
        String nullRef = null;
        String literal = "java";
        String concatenated = literal + " programming";
        boolean sameContent = literal.equals("java");
        int length = literal.length();

        System.out.println("literal       = " + literal);
        System.out.println("concatenated  = " + concatenated);
        System.out.println("sameContent   = " + sameContent);
        System.out.println("length        = " + length);

        if (nullRef == null) {
            System.out.println("nullRef is null");
        }
    }
}
