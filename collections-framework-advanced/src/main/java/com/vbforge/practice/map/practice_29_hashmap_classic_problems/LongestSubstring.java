package com.vbforge.practice.map.practice_29_hashmap_classic_problems;

import java.util.HashMap;
import java.util.Map;

/**
 * Problem:
 * Find the length of the longest substring without repeating characters.
 * Use of HashMap:
 * Track the last seen index of each character with a HashMap<Character, Integer>.
 * */
public class LongestSubstring {
    public static void main(String[] args) {

        String str = "pwwkew"; //3
        int longestSubstring = lengthOfLongestSubstring(str);
        System.out.println(longestSubstring);

    }

    private static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();

        int maxLength = 0;
        int start = 0;

        for(int end = 0; end < s.length(); end++){
            char c = s.charAt(end);
            if(map.containsKey(c)){
                start = Math.max(start, map.get(c) + 1);
            }
            map.put(c, end);
            maxLength = Math.max(end - start + 1, maxLength);
        }
        return maxLength;
    }

}
