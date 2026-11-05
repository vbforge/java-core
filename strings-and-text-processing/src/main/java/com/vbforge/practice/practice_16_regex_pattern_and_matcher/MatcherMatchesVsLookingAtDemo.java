package com.vbforge.practice.practice_16_regex_pattern_and_matcher;

import java.util.regex.Pattern;

/** matches() needs the whole input to match; lookingAt() only needs the beginning to match. */
public class MatcherMatchesVsLookingAtDemo {

    public static void main(String[] args) {
        Pattern word = Pattern.compile("\\w*");
        System.out.println("matches(\"wonderful\"):  " + word.matcher("wonderful").matches());  // true
        System.out.println("matches(\"wonderful!\"): " + word.matcher("wonderful!").matches()); // false
        System.out.println("lookingAt(\"Thanks!\"):  " + word.matcher("Thanks!").lookingAt());  // true
    }
}
