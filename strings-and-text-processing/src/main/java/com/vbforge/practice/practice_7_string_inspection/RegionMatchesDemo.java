package com.vbforge.practice.practice_7_string_inspection;

/** regionMatches(thisOffset, other, otherOffset, length): compare slices without creating substrings. */
public class RegionMatchesDemo {

    public static void main(String[] args) {
        // "Learn Java"[7..10) = "ava", "Cool avatar!"[5..8) = "ava"
        System.out.println("regionMatches: " + "Learn Java".regionMatches(7, "Cool avatar!", 5, 3)); // true
    }
}
