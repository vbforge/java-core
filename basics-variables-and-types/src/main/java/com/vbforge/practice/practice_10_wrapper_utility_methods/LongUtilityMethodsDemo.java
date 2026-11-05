package com.vbforge.practice.practice_10_wrapper_utility_methods;

/**
 * Long constants and helpers.
 */
public class LongUtilityMethodsDemo {

    public static void main(String[] args) {
        System.out.println("MAX_VALUE:          " + Long.MAX_VALUE);
        System.out.println("parseLong(\"123\"):   " + Long.parseLong("123"));
        System.out.println("signum(-5 / 0 / 5): "
                + Long.signum(-5L) + " / " + Long.signum(0L) + " / " + Long.signum(5L));
    }
}
