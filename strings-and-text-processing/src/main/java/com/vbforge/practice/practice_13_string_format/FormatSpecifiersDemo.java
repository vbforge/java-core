package com.vbforge.practice.practice_13_string_format;

/** String.format with the common specifiers: %f double, %s String, %d integer, %c char, %b boolean, %% percent sign. */
public class FormatSpecifiersDemo {

    public static void main(String[] args) {
        String formatString = "We are printing double variable (%f), string (\"%s\") and integer variable (%d).";
        System.out.println(String.format(formatString, 0.7, "Java", 10));

        // addition: the other specifiers listed in the original comments, actually used
        System.out.println(String.format("char (%c), boolean (%b), percent sign (%%)", 'J', true));
    }
}
