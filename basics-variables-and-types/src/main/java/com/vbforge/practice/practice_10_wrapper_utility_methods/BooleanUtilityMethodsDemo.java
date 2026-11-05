package com.vbforge.practice.practice_10_wrapper_utility_methods;

/**
 * Boolean parsing (case-insensitive "true") and compare.
 */
public class BooleanUtilityMethodsDemo {

    public static void main(String[] args) {
        System.out.println("parseBoolean(\"true\"): " + Boolean.parseBoolean("true"));
        System.out.println("parseBoolean(\"TRUE\"): " + Boolean.parseBoolean("TRUE"));
        System.out.println("compare(true, false): " + Boolean.compare(true, false));
    }
}
