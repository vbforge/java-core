package com.vbforge.practice.practice_3_multi_catch;

import java.util.Arrays;

/**
 * Multi-catch (Java 7+): `catch (A | B e)` — one handler for unrelated types.
 * The alternatives must not be subclasses of one another.
 */
public class MultiCatchDemo {

    public static void main(String[] args) {
        System.out.println("--- multi-catch: two types, one handler ---");
        twoTypes();

        System.out.println("\n--- multi-catch with three unrelated types ---");
        String[][] inputs = {
                {"10", "2"},      // no exception
                {"10", "0"},      // ArithmeticException
                {"ten", "2"},     // NumberFormatException
                {"10"}            // ArrayIndexOutOfBoundsException
        };
        for (String[] input : inputs) {
            divideFirstBySecond(input);
        }
    }

    static void twoTypes() {
        int[] arr = {1, 2, 3};
        try {
            System.out.println(arr[10] / 0);
        } catch (ArrayIndexOutOfBoundsException | ArithmeticException e) {
            System.out.println("  one of: " + e.getClass().getSimpleName());
        }
    }

    static void divideFirstBySecond(String[] arr) {
        System.out.print("  " + Arrays.toString(arr) + " -> ");
        try {
            int a = Integer.parseInt(arr[0]);
            int b = Integer.parseInt(arr[1]);
            System.out.println("res = " + (a / b));
        } catch (ArithmeticException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("caught: " + e.getClass().getSimpleName());
        }
    }
}
