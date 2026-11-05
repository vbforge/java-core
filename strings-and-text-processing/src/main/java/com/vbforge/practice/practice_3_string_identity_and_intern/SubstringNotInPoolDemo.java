package com.vbforge.practice.practice_3_string_identity_and_intern;

/** A String computed at runtime (here by substring) is not taken from the pool: == is false, equals is true. */
public class SubstringNotInPoolDemo {

    public static void main(String[] args) {
        String hello = "Hello";
        String welcome = "Hello World!";
        String ss = welcome.substring(0, welcome.indexOf(" "));
        System.out.println(hello + " == " + ss + " -> " + (hello == ss));         // false
        System.out.println(hello + " equals " + ss + " -> " + hello.equals(ss));  // true
    }
}
