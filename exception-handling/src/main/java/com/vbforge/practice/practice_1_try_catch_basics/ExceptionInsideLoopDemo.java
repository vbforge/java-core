package com.vbforge.practice.practice_1_try_catch_basics;

/**
 * An exception thrown inside a loop leaves the whole loop (and the rest of the
 * try block); the catch runs once, and the program continues after it.
 */
public class ExceptionInsideLoopDemo {

    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5, 6};
        int sum = 0;
        try {
            for (int i = 0; i <= 6; i += 2) {          // i = 0,2,4,6 -> 6 is out of bounds
                sum += numbers[i];
            }
            System.out.println("sum = " + sum);          // skipped: the exception leaves the try
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  program error! partial sum (" + sum + ") abandoned");
        }
        System.out.println("  program continues after catch");
    }
}
