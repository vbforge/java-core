package com.vbforge.practice.map.practice_29_hashmap_classic_problems;

import java.util.*;

/**
 * Problem:
 * Given a list of strings, group them into lists of anagrams.
 * Use of HashMap:
 * Use a sorted string as key, and group words in a List<String> as value.
 * */
public class GroupAnagram {
    public static void main(String[] args) {

        String[] strs = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> result = groupAnagrams(strs);
        System.out.println(result);


    }

    private static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(map.values());
    }

}
