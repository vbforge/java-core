package com.vbforge.practice.practice_13_string_format;

/** printf: %s, %d, left (-) and right alignment by width, and precision of %f. */
public class PrintfWidthAndPrecisionDemo {

    public static void main(String[] args) {
        System.out.printf("String, %s%n", "Object.class");
        System.out.printf("Number, %d%n", 100);

        int[] numbers = {123456, 12, 156, 1000023456, 12346};
        System.out.println("Left-aligned width 10:");
        for (int n : numbers) System.out.printf("Number: %-10d|%n", n);
        System.out.println("Right-aligned width 10:");
        for (int n : numbers) System.out.printf("Number: %10d|%n", n);

        System.out.println("Floating point precision:");
        System.out.printf("Number: %.3f%n", 12.3456);
        System.out.printf("Number: %.2f%n", 12.3333);
        System.out.printf("Number: %.1f%n", 1.56);
        System.out.printf("Number: %.5f%n", 10.00023456);
        System.out.printf("Number: %.4f%n", 1.332346);
    }
}
