package com.vbforge.practice.practice_4_string_comparison;

/** Lexicographic ordering: compareTo (negative / zero / positive) and compareToIgnoreCase. */
public class CompareToDemo {

    public static void main(String[] args) {
        System.out.println("compareTo:           " + "apple".compareTo("banana"));        // negative
        System.out.println("compareToIgnoreCase: " + "Apple".compareToIgnoreCase("apple")); // 0
    }
}
