package com.vbforge.practice.practice_7_instanceof_patterns;

/**
 * obj instanceof String s combines the type test and the cast: if the test is true,
 * s is already a String. A null reference never matches.
 */
public class InstanceofPatternBasicsDemo {

    static String oldStyle(Object o) {
        if (o instanceof String) {
            String s = (String) o;           // explicit cast
            return "string of length " + s.length();
        }
        return "not a string";
    }

    static String patternStyle(Object o) {
        if (o instanceof String s) {         // test + cast + binding in one step
            return "string of length " + s.length();
        }
        return "not a string";
    }

    public static void main(String[] args) {
        Object[] values = {"hello", 42, null};
        for (Object v : values) {
            System.out.println(v + " -> " + oldStyle(v) + " | " + patternStyle(v));
        }
    }
}
