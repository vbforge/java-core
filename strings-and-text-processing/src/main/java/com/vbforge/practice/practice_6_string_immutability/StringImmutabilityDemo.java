package com.vbforge.practice.practice_6_string_immutability;

/** Every "modification" returns a new String; the original object is never changed. */
public class StringImmutabilityDemo {

    public static void main(String[] args) {
        String original = "hello";
        String upper = original.toUpperCase();
        System.out.println("original: " + original + "  (unchanged)");
        System.out.println("upper:    " + upper);
        System.out.println("Same ref? " + (original == upper)); // false
    }
}
