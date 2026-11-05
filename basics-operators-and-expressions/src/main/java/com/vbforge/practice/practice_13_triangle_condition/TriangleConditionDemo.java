package com.vbforge.practice.practice_13_triangle_condition;

/**
 * Three positive integers represent a triangle iff each side is
 * strictly less than the sum of the other two.
 *
 * Run with three integer arguments, or without arguments to use built-in samples.
 */
public class TriangleConditionDemo {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("3, 4, 5   -> " + isTriangle(3, 4, 5));   // true
            System.out.println("1, 2, 3   -> " + isTriangle(1, 2, 3));   // false (degenerate)
            System.out.println("5, 5, 10  -> " + isTriangle(5, 5, 10));  // false
            return;
        }
        if (args.length != 3) {
            System.out.println("Please provide exactly three positive integers.");
            return;
        }
        try {
            int a = Integer.parseInt(args[0]);
            int b = Integer.parseInt(args[1]);
            int c = Integer.parseInt(args[2]);
            System.out.println(isTriangle(a, b, c));
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid integers.");
        }
    }

    public static boolean isTriangle(int a, int b, int c) {
        return (a < b + c) && (b < a + c) && (c < a + b);
    }
}
