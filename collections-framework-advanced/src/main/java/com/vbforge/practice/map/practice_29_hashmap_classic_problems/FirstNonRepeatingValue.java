package com.vbforge.practice.map.practice_29_hashmap_classic_problems;

import java.util.HashMap;
import java.util.Map;

/**
 * Problem:
 * Given a string, find the first character that does not repeat.
 * Use of HashMap:
 * Use HashMap<Character, Integer> to count frequency of characters.
 * In a second pass, return the first character with frequency 1.
 * */
public class FirstNonRepeatingValue {
    public static void main(String[] args) {

//        String str = "Hello World"; // H
        String str = "abbac";         // c
        char result = firstNonRepeatingValue(str);
        System.out.println(result);

    }

    private static char firstNonRepeatingValue(String str) {

        Map<Character, Integer> map = new HashMap<>();

        for (char c : str.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        for (char c : str.toCharArray()) {
            if (map.get(c) == 1) {
                return c;
            }
        }

        return '_'; //no non-repeating character
    }
}
