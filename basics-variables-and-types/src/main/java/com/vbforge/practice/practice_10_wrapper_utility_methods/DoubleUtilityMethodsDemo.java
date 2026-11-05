package com.vbforge.practice.practice_10_wrapper_utility_methods;

/**
 * Double constants and helpers, including NaN and Infinity.
 */
public class DoubleUtilityMethodsDemo {

    public static void main(String[] args) {
        System.out.println("parseDouble(\"3.14\"):  " + Double.parseDouble("3.14"));
        System.out.println("isNaN(0.0/0.0):       " + Double.isNaN(0.0 / 0.0));
        System.out.println("isInfinite(1.0/0.0):  " + Double.isInfinite(1.0 / 0.0));
        System.out.println("MAX_VALUE:            " + Double.MAX_VALUE);
        System.out.println("MIN_VALUE:            " + Double.MIN_VALUE); // smallest POSITIVE value, not the most negative
        System.out.println("NaN == NaN:           " + (Double.NaN == Double.NaN) + "  <- use isNaN()");
        System.out.println("compare(1.0, 2.0):    " + Double.compare(1.0, 2.0));
    }
}
