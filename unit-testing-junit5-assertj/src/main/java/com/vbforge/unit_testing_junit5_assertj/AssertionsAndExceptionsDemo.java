package com.vbforge.unit_testing_junit5_assertj;

import java.util.List;

/**
 * AssertionsAndExceptionsDemo — reference material for AssertJ's fluent API
 * and exception testing.
 *
 * <p>See {@code AssertionsAndExceptionsDemoTest} for the full set of examples:
 * chained assertions, {@code extracting}, {@code isInstanceOf},
 * {@code assertThatThrownBy}, {@code assertThatExceptionOfType},
 * {@code assertThatCode}.
 *
 * <p>WHY: JUnit 5's {@code assertEquals} gives one boolean per call. AssertJ
 * gives a fluent chain, rich failure messages ("expected list to contain
 * 'Bob' but only contained ..."), and first-class support for exceptions and
 * collections. Prefer AssertJ everywhere.
 */
public class AssertionsAndExceptionsDemo {

    /**
     * Integer division. Throws {@link ArithmeticException} when {@code b == 0}.
     */
    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("division by zero");
        }
        return a / b;
    }

    /**
     * Parses an age from a string. Throws {@link IllegalArgumentException}
     * if the string is null, not numeric, or outside {@code [0, 150]}.
     */
    public int parseAge(String s) {
        if (s == null) {
            throw new IllegalArgumentException("age is null");
        }
        int age;
        try {
            age = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("not a number: " + s);
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("age out of range: " + age);
        }
        return age;
    }

    /**
     * Returns the first element of a non-empty list. Throws
     * {@link java.util.NoSuchElementException} if the list is empty or null.
     */
    public String firstElement(List<String> list) {
        if (list == null || list.isEmpty()) {
            throw new java.util.NoSuchElementException("list is empty");
        }
        return list.get(0);
    }

    /**
     * Clamps {@code value} to the inclusive range {@code [min, max]}.
     * Throws {@link IllegalArgumentException} if {@code min > max}.
     */
    public int clamp(int value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min > max: " + min + " > " + max);
        }
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static void main(String[] args) {
        System.out.println("=== AssertJ + exception testing — see AssertionsAndExceptionsDemoTest ===");
        System.out.println();
        AssertionsAndExceptionsDemo d = new AssertionsAndExceptionsDemo();
        System.out.println("divide(10, 2)          = " + d.divide(10, 2));
        System.out.println("parseAge(\"42\")         = " + d.parseAge("42"));
        System.out.println("firstElement([a, b])   = " + d.firstElement(List.of("a", "b")));
        System.out.println("clamp(150, 0, 100)     = " + d.clamp(150, 0, 100));
    }
}