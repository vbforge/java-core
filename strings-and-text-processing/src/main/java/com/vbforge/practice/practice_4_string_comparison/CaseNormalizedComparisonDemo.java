package com.vbforge.practice.practice_4_string_comparison;

/** Normalise with toLowerCase, then compare; comparing the result with the source tells whether it was already lowercase. */
public class CaseNormalizedComparisonDemo {

    public static void main(String[] args) {
        String line1 = "This A";
        String line2 = "this a";
        String l1 = line1.toLowerCase();
        String l2 = line2.toLowerCase();
        if (l1.equalsIgnoreCase(l2)) {
            System.out.println("line1 and line2 are case-insensitive equal; lowercased forms equal: " + l1.equals(l2));
        }
        System.out.println("line1 lowercase? " + l1.equals(line1)); // false
        System.out.println("line2 lowercase? " + l2.equals(line2)); // true
    }
}
