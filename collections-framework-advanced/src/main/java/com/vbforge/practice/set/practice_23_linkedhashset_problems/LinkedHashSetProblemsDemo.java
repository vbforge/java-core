package com.vbforge.practice.set.practice_23_linkedhashset_problems;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class LinkedHashSetProblemsDemo {
    public static void main(String[] args) {

        // Problem 1: Remove duplicates from array, preserve order
        Integer[] input = {3, 5, 2, 3, 7, 5, 2};
        Integer[] result = removeDuplicatesPreserveOrder(input);
        System.out.println("1. Remove duplicates and preserve order: " + Arrays.toString(result));

        // Problem 2: First non-repeating character in a string
        String word = "aabbcddexx";
        Character firstUnique = firstNonRepeatingChar(word);
        System.out.println("2. First non-repeating character: " + firstUnique);

        // Problem 3: Check if a collection contains duplicates
        Integer[] checkDup = {1, 2, 3, 4, 2};
        boolean hasDup = containsDuplicate(checkDup);
        System.out.println("3. Contains duplicate? " + hasDup);

        // Problem 4: Print elements in insertion order (natural behavior of LinkedHashSet)
        System.out.println("4. Insertion order using LinkedHashSet:");
        printInOrder(new String[]{"red", "green", "blue", "green", "yellow"});
    }

    // Problem 1: Remove duplicates from an array, preserve order
    public static Integer[] removeDuplicatesPreserveOrder(Integer[] array) {
        Set<Integer> set = new LinkedHashSet<>(Arrays.asList(array));
        return set.toArray(new Integer[0]);
    }

    // Problem 2: First non-repeating character
    public static Character firstNonRepeatingChar(String str) {
        LinkedHashSet<Character> set = new LinkedHashSet<>();
        int[] freq = new int[256];

        //add chars to set and incrementing current char
        for (char c : str.toCharArray()) {
            set.add(c);
            freq[c]++;
        }

        //check if its amount more than one, return it
        for (char c : set) {
            if (freq[c] == 1) return c;
        }

        return null; // all characters repeat
    }

    // Problem 3: Check if there are duplicates
    public static boolean containsDuplicate(Integer[] array) {
        Set<Integer> set = new LinkedHashSet<>();
        for (Integer num : array) {
            if (!set.add(num)) return true;
        }
        return false;
    }

    // Problem 4: Print elements preserving insertion order
    public static void printInOrder(String[] words) {
        Set<String> orderedSet = new LinkedHashSet<>(Arrays.asList(words));
        orderedSet.forEach(System.out::println);
    }


}
