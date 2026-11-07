package com.vbforge.practice.practice_12_common_exceptions_catalog;

import java.util.List;

/**
 * A catalog of the most common unchecked exceptions, each paired with
 * the precondition check that raises it early with a clear message
 * instead of letting the JVM fail somewhere deeper.
 *
 * Philosophy: don't catch these — prevent them. A NullPointerException
 * caught and silently ignored is a bug deferred, not a bug fixed.
 * (The catches in main() exist only to print what each guard rejected.)
 */
public class CommonExceptionsCatalogDemo {

    public static void main(String[] args) {
        System.out.println("--- NullPointerException (prevention) ---");
        try {
            greetUser(null);
        } catch (NullPointerException e) {
            System.out.println("  rejected: " + e.getMessage());
        }

        System.out.println("\n--- ArrayIndexOutOfBoundsException (prevention) ---");
        try {
            getThirdElement(new int[]{1, 2});
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  rejected: " + e.getMessage());
        }

        System.out.println("\n--- ClassCastException (prevention) ---");
        try {
            getStringFromList(List.of(42));
        } catch (ClassCastException e) {
            System.out.println("  rejected: " + e.getMessage());
        }

        System.out.println("\n--- ArithmeticException (prevention) ---");
        try {
            calculateAverage(10, 0);
        } catch (ArithmeticException e) {
            System.out.println("  rejected: " + e.getMessage());
        }

        System.out.println("\n--- NumberFormatException (wrap into IllegalArgumentException) ---");
        try {
            parseStringToInt("not-a-number");
        } catch (IllegalArgumentException e) {
            System.out.println("  rejected: " + e.getMessage());
        }

        System.out.println("\n--- exception raised by the JVM itself: getClass() and getMessage() ---");
        printExceptionClass();

    }

    static String greetUser(String name) {
        if (name == null) throw new NullPointerException("name cannot be null");
        return "Hello, " + name + "!";
    }

    static int getThirdElement(int[] numbers) {
        if (numbers == null || numbers.length < 3)
            throw new ArrayIndexOutOfBoundsException("array needs at least 3 elements");
        return numbers[2];
    }

    static String getStringFromList(List<?> list) {
        if (list.isEmpty()) throw new IndexOutOfBoundsException("list is empty");
        Object obj = list.get(0);
        if (!(obj instanceof String))
            throw new ClassCastException("expected String, got " + obj.getClass().getSimpleName());
        return (String) obj;
    }

    static double calculateAverage(int numerator, int denominator) {
        if (denominator == 0) throw new ArithmeticException("cannot divide by zero");
        return (double) numerator / denominator;
    }

    static int parseStringToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid number format: " + value, e);
        }
    }

    static void printExceptionClass() {
        try {
            int[] data = new int[2];
            System.out.println(data[5]);
        } catch (RuntimeException e) {
            System.out.println("  caught class: " + e.getClass().getSimpleName());
            System.out.println("  message:      " + e.getMessage());
        }
    }

}
