package com.vbforge.practice.practice_16_regex_pattern_and_matcher;

import java.util.regex.Pattern;

/** Pattern.split(input) and split(input, limit). */
public class PatternSplitDemo {

    public static void main(String[] args) {
        String str = "One two,three!four;five six.seven";
        Pattern p = Pattern.compile("[,!;.]");
        System.out.println("-- split --");
        for (String s : p.split(str)) System.out.println("Lexeme: " + s);
        System.out.println("-- split with limit 2 --");
        for (String s : p.split(str, 2)) System.out.println("Lexeme: " + s);
    }
}
