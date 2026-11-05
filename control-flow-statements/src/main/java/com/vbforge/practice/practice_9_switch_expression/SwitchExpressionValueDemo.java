package com.vbforge.practice.practice_9_switch_expression;

/**
 * Switch EXPRESSION: the switch itself produces a value that is returned or assigned.
 * Several labels can share one arrow (case 2, 3, 5, 7 -> ...). A default is required
 * here because int has more values than the listed labels.
 */
public class SwitchExpressionValueDemo {

    public static void main(String[] args) {
        System.out.println("--- returning a value ---");
        for (int n = 1; n <= 5; n++) {
            System.out.println(n + " -> " + numberName(n));
        }

        System.out.println("\n--- multi-label cases ---");
        for (int n : new int[]{1, 3, 5, 7, 9, 2, 4, 6, 8}) {
            System.out.println(n + " -> " + classify(n));
        }
    }

    static String numberName(int n) {
        return switch (n) {
            case 1 -> "First";
            case 2 -> "Second";
            case 3 -> "Third";
            case 4 -> "Fourth";
            case 5 -> "Fifth";
            default -> "No value";
        };
    }

    static String classify(int n) {
        return switch (n) {
            case 1, 9       -> "odd, not prime";
            case 2, 3, 5, 7 -> "prime";
            case 4, 8       -> "power of two";
            case 6          -> "perfect number";
            default         -> "unclassified";
        };
    }
}
