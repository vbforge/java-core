package com.vbforge.practice.practice_10_string_builder;

/**
 * A StringBuilder passed to a method is mutated through the shared reference, so the caller sees the change.
 * (The String-parameter contrast is covered in basics-variables-and-types/practice_18_pass_by_value_reference.)
 */
public class StringBuilderPassedToMethodDemo {

    public static void main(String[] args) {
        StringBuilder sbU = new StringBuilder("Learning");
        updateString(sbU);
        System.out.println("StringBuilder after update: " + sbU); // Learningjava!
    }

    static void updateString(StringBuilder string) {
        string.append("java!");
    }
}
