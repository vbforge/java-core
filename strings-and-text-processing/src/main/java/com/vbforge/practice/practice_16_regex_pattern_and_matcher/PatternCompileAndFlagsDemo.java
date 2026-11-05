package com.vbforge.practice.practice_16_regex_pattern_and_matcher;

import java.util.regex.Pattern;

/** Pattern.compile with and without flags, flags() values, and the one-shot Pattern.matches (whole-string match). */
public class PatternCompileAndFlagsDemo {

    public static void main(String[] args) {
        Pattern p1 = Pattern.compile("java", Pattern.CASE_INSENSITIVE);
        System.out.println("p1 = " + p1);
        Pattern p2 = Pattern.compile("java");
        System.out.println("p2 = " + p2);
        System.out.println("Pattern.matches(\"J.+a\",\"Java\")            = " + Pattern.matches("J.+a", "Java"));            // true
        System.out.println("Pattern.matches(\"J.+a\",\"Java JavaScript\") = " + Pattern.matches("J.+a", "Java JavaScript")); // false
        System.out.println("flags (no flag):          " + Pattern.compile("abc").flags());                            // 0
        System.out.println("flags (CASE_INSENSITIVE): " + Pattern.compile("abc", Pattern.CASE_INSENSITIVE).flags()); // 2
    }
}
