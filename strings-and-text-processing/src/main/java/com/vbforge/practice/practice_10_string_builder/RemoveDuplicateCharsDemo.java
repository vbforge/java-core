package com.vbforge.practice.practice_10_string_builder;

import java.util.HashSet;
import java.util.Set;

/** Keep the first occurrence of every character, building the result in a StringBuilder. */
public class RemoveDuplicateCharsDemo {

    public static void main(String[] args) {
        System.out.println("removeDuplicates: " + removeDuplicates("Hello"));
        System.out.println("non-ASCII:        " + removeDuplicates("жжaжa")); // would crash with boolean[256]
    }

    static String removeDuplicates(String str) {
        StringBuilder sb = new StringBuilder();
        Set<Character> seen = new HashSet<>();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (seen.add(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
