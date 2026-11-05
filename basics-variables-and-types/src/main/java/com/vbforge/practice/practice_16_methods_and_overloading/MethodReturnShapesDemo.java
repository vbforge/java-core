package com.vbforge.practice.practice_16_methods_and_overloading;

/**
 * Two common return shapes:
 *   - value-returning methods (pure: same input, same output, no side effects)
 *   - void methods that print (side effect only)
 */
public class MethodReturnShapesDemo {

    public static void main(String[] args) {
        System.out.println("--- pure, value-returning ---");
        System.out.println("add(3, 4) = " + add(3, 4));
        System.out.println("square(5) = " + square(5));
        System.out.println("max(3, 8) = " + max(3, 8));

        System.out.println("\n--- void, prints for side-effect ---");
        printGreeting("Bob");
    }

    static int add(int a, int b)  { return a + b; }
    static int square(int a)      { return a * a; }
    static int max(int a, int b)  { return Math.max(a, b); }

    static void printGreeting(String name) {
        System.out.println("Hello, " + name + "!");
    }
}
