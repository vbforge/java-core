package com.vbforge.practice.practice_23_static_members;

/**
 * Utility class pattern: static fields + static methods, no instance needed.
 * (A private constructor would make "do not instantiate" explicit, see practice_22.)
 */
public final class UtilityClassDemo {

    private static double percentage = 0.25;

    public static double add(int a, int b)      { return (a + b) * percentage; }
    public static double multiply(int a, int b) { return (a * b) * percentage; }

    public static void main(String[] args) {
        System.out.println("add(10, 90)      = " + add(10, 90));
        System.out.println("multiply(10, 10) = " + multiply(10, 10));
    }
}
