package com.vbforge.practice.practice_16_regex_pattern_and_matcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Matcher.find() loop: group() for the matched text, start()/end() for its position. */
public class MatcherFindDemo {

    static final String TEXT = "This is my second java 45 project.\n"
            + "It is wonderful to learn polysemantics and arrays.\n"
            + "The weather is cold like it should be in winter, but we are all looking forward to spring.";

    public static void main(String[] args) {
        findDigits();
        findTwoLetterWords();
    }

    static void findDigits() {
        System.out.println("-- find digits --");
        Matcher matcher = Pattern.compile("\\d+").matcher("There 123 apples");
        while (matcher.find()) {
            System.out.println("Found: " + matcher.group()); // 123
        }
    }

    static void findTwoLetterWords() {
        System.out.println("-- find two-letter words with positions --");
        Matcher m = Pattern.compile("\\b[\\w]{2}\\b").matcher(TEXT);
        System.out.println("m = " + m);
        while (m.find()) {
            System.out.printf("Found match: '%s' from %d to %d positions%n",
                    TEXT.substring(m.start(), m.end()), m.start(), m.end() - 1);
        }
    }
}
