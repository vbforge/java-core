package com.vbforge.practice.set.practice_27_treeset_problems;

import java.util.TreeSet;

public class TreeSetProblemsDemo {
    public static void main(String[] args) {

        // Problem 1: Remove duplicates and sort automatically
        int[] arr = {5, 2, 9, 1, 5, 6, 2};
        TreeSet<Integer> sortedUnique = removeDuplicatesAndSort(arr);
        System.out.println("1. Sorted unique elements: " + sortedUnique);

        // Problem 2: Find the floor and ceiling
        TreeSet<Integer> nums = new TreeSet<>();
        nums.add(10);
        nums.add(20);
        nums.add(30);
        nums.add(40);

        int key = 25;
        System.out.println("2. Floor of " + key + ": " + nums.floor(key));     // 20
        System.out.println("   Ceiling of " + key + ": " + nums.ceiling(key)); // 30

        // Problem 3: Get all elements within a range
        System.out.println("3. Elements between 15 and 35: " + nums.subSet(15, 36)); // 20, 30

        // Problem 4: Automatically sort custom strings (e.g., city names)
        TreeSet<String> cities = new TreeSet<>();
        cities.add("Zurich");
        cities.add("Amsterdam");
        cities.add("Berlin");
        cities.add("Paris");
        System.out.println("4. Cities (sorted): " + cities);

        // Problem 5: Detect the first missing positive number
        int[] data = {1, 2, 3, 5, 6, 8};
        System.out.println("5. First missing number: " + firstMissingPositive(data)); // 4
    }

    // Problem 1: Remove duplicates and sort
    public static TreeSet<Integer> removeDuplicatesAndSort(int[] arr) {
        TreeSet<Integer> set = new TreeSet<>();
        for (int num : arr) set.add(num);
        return set;
    }

    // Problem 5: Find the first missing number in a sequence
    public static int firstMissingPositive(int[] arr) {
        TreeSet<Integer> set = new TreeSet<>();
        for (int num : arr) {
            if (num > 0) set.add(num);
        }
        int expected = 1;
        for (int num : set) {
            if (num != expected) return expected;
            expected++;
        }
        return expected;
    }


}
