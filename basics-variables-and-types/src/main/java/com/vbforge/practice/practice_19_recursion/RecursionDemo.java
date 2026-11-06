package com.vbforge.practice.practice_19_recursion;

/**
 * Recursion: a method that calls itself until a base case stops it.
 *
 *   factorial(n)  - n * factorial(n - 1), base case n <= 1
 *   fibonacci(n)  - fibonacci(n - 1) + fibonacci(n - 2), base case n <= 1
 */
public class RecursionDemo {

    static long factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    static int fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static void main(String[] args) {
        System.out.println("factorial(5):  " + factorial(5) + "  = 5x4x3x2x1");
        System.out.println("factorial(10): " + factorial(10));
        System.out.print("fibonacci(0..9): ");
        for (int i = 0; i < 10; i++) System.out.print(fibonacci(i) + " ");
        System.out.println();
    }
}
