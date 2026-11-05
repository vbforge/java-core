package com.vbforge.practice.practice_3_string_identity_and_intern;

/**
 * Two {@code new String(...)} objects are always different references, even with the same content
 * (equal hashCode, equal by equals, different by ==). Two equal literals are the same pooled object.
 */
public class NewStringVsNewStringDemo {

    public static void main(String[] args) {
        String str1 = new String("My name is John Smith");
        String str2 = new String("My name is John Smith");
        System.out.println("hash.str1 = " + str1.hashCode());
        System.out.println("hash.str2 = " + str2.hashCode());
        System.out.println("equals: " + str1.equals(str2)); // true
        System.out.println("==    : " + (str1 == str2));    // false

        System.out.println("-----");
        String str3 = "My name is Jane Andrews";
        String str4 = "My name is Jane Andrews";
        System.out.println("hash.str3 = " + str3.hashCode());
        System.out.println("hash.str4 = " + str4.hashCode());
        System.out.println("equals: " + str3.equals(str4)); // true
        System.out.println("==    : " + (str3 == str4));    // true (same pooled literal)
    }
}
