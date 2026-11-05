package com.vbforge.practice.practice_19_regex_words_with_digits;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find words that contain a digit, split a sentence into words and format a line per word.
 * The sentence is a built-in sample (or args joined by space) instead of a Scanner prompt.
 */
public class WordsWithDigitsDemo {

    public static void main(String[] args) {
        String inputString = args.length > 0 ? String.join(" ", args) : "Order a1 has 3 items, abc12def and x9 shipped";

        Matcher matcher = Pattern.compile("\\b\\w*\\d\\w*\\b").matcher(inputString);
        System.out.println("Words with digits:");
        while (matcher.find()) {
            System.out.println(matcher.group());
        }

        String[] words = inputString.split("\\s+");
        System.out.println("Words split by spaces:");
        for (String word : words) {
            System.out.println(word);
        }

        System.out.println(String.format("Total words: %d", words.length));
        for (String word : words) {
            System.out.println(String.format("Word: %s, Length: %d", word, word.length()));
        }
    }
}
