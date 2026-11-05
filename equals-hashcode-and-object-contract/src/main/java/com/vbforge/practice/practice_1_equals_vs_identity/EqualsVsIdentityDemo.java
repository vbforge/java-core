package com.vbforge.practice.practice_1_equals_vs_identity;

/**
 * {@code ==} compares references (identity); {@code equals} compares state - once the
 * class overrides it. Equal objects must also have equal hash codes.
 */
public class EqualsVsIdentityDemo {

    public static void main(String[] args) {
        Room london = new Room("London", "Presentation hall", 200, 500.0);
        Room same   = new Room("London", "Presentation hall", 200, 500.0);

        System.out.println("london.equals(same) : " + london.equals(same));                         // true
        System.out.println("london == same      : " + (london == same));                            // false
        System.out.println("same hashCode       : " + (london.hashCode() == same.hashCode()));      // true
    }
}
