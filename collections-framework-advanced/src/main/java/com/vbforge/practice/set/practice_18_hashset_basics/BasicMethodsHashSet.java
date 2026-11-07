package com.vbforge.practice.set.practice_18_hashset_basics;

import java.util.HashSet;
import java.util.Set;

public class BasicMethodsHashSet {
    public static void main(String[] args) {
        /*add(E e)	Adds element if not already present	hashSet.add("apple");
        remove(Object o)	Removes specified element if present	hashSet.remove("apple");
        contains(Object o)	Checks if element exists	hashSet.contains("apple");
        size()	Returns number of elements	hashSet.size();
        clear()	Removes all elements	hashSet.clear();
        isEmpty()	Checks if set is empty	hashSet.isEmpty();*/

        Set<String> hashSet = new HashSet<>();

        // Add elements to the set
        hashSet.add("apple");
        hashSet.add("banana");
        hashSet.add("cherry");
        System.out.println("After adding elements: " + hashSet);

        // Check if "apple" exists in the set
        boolean containsApple = hashSet.contains("apple");
        System.out.println("Contains 'apple'? " + containsApple);

        // Remove "banana" from the set
        hashSet.remove("banana");
        System.out.println("After removing 'banana': " + hashSet);

        // Get the size of the set
        int size = hashSet.size();
        System.out.println("Size of set: " + size);

        // Check if the set is empty
        boolean isEmpty = hashSet.isEmpty();
        System.out.println("Is the set empty? " + isEmpty);

        // Clear the set
        hashSet.clear();
        System.out.println("After clearing: " + hashSet);

        // Check again if the set is empty
        System.out.println("Is the set empty after clear? " + hashSet.isEmpty());

    }
}
