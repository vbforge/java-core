package com.vbforge.practice.practice_5_logical_operators_and_shortcircuit;

/**
 * Logical operators on boolean values: && (and), || (or), ! (not), ^ (xor).
 * Fixed inputs keep the output deterministic.
 */
public class LogicalOperatorsDemo {

    public static void main(String[] args) {
        int i = 7;
        int j = 42;

        System.out.println("i = " + i);
        System.out.println("j = " + j);
        System.out.println("(i < 10) && (j < 10) = " + ((i < 10) && (j < 10))); // true && false → false
        System.out.println("(i < 10) || (j < 10) = " + ((i < 10) || (j < 10))); // true || false → true
        System.out.println("!(i < 10)            = " + !(i < 10));              // false
        System.out.println("(i < 10) ^ (j < 10)  = " + ((i < 10) ^ (j < 10)));  // true ^ false → true
        System.out.println("(i < 10) ^ (j > 10)  = " + ((i < 10) ^ (j > 10)));  // true ^ true → false
    }
}
