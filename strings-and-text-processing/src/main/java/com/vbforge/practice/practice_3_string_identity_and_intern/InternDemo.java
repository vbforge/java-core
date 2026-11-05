package com.vbforge.practice.practice_3_string_identity_and_intern;

/** {@code intern()} returns the pooled instance, so it is == to the literal. */
public class InternDemo {

    public static void main(String[] args) {
        String s1 = "java";
        String s2 = "java";
        String s3 = new String("java");
        String s4 = s3.intern();
        System.out.println("s1 == s2 (both literals):     " + (s1 == s2)); // true
        System.out.println("s1 == s3 (literal vs new):    " + (s1 == s3)); // false
        System.out.println("s1 == s4 (literal vs intern): " + (s1 == s4)); // true
        System.out.println("s1.equals(s3):                " + s1.equals(s3)); // true
    }
}
