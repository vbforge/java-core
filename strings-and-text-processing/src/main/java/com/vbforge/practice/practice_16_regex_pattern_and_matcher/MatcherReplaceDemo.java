package com.vbforge.practice.practice_16_regex_pattern_and_matcher;

import java.util.regex.Pattern;

/** Matcher.replaceFirst, replaceAll and replaceAll with a $1 back-reference (which needs a capturing group). */
public class MatcherReplaceDemo {

    static final String TEXT = MatcherFindDemo.TEXT;

    public static void main(String[] args) {
        Pattern twoLetterWord = Pattern.compile("\\b[\\w]{2}\\b");
        System.out.println("-- replaceFirst --");
        System.out.println(twoLetterWord.matcher(TEXT).replaceFirst("lab2"));
        System.out.println("\n-- replaceAll --");
        System.out.println(twoLetterWord.matcher(TEXT).replaceAll("lab2"));
        System.out.println("\n-- replaceAll with $1 --");
        System.out.println(Pattern.compile("(\\b[\\w]{2}\\b)").matcher(TEXT).replaceAll("$1_lab2"));
    }
}
