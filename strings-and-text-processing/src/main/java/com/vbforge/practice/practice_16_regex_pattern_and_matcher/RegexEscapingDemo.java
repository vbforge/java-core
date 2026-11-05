package com.vbforge.practice.practice_16_regex_pattern_and_matcher;

/** A regex backslash must be doubled inside a Java String literal: the regex \w is written "\\w". */
public class RegexEscapingDemo {

    public static void main(String[] args) {
        String regex1 = "\\w";       // the regex text is: \w
        String regex3 = "When\\?";   // the regex text is: When\?
        System.out.println("regex1 = " + regex1);
        System.out.println("regex3 = " + regex3);
    }
}
