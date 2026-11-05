package com.vbforge.practice.practice_9_switch_expression;

/**
 * A switch expression is an expression, so it can be passed directly as a method argument
 * (here to println) without a temporary variable.
 */
public class SwitchExpressionAsArgumentDemo {

    public static void main(String[] args) {
        printDescription(2);
        printDescription(777);
        printDescription(0);
        printDescription(42);
    }

    static void printDescription(int value) {
        System.out.println(switch (value) {
            case 1, 2, 3, 4 -> "1, 2, 3 or 4";
            case 777        -> "Range: " + value;
            case 0          -> "zero";
            default         -> "default";
        });
    }
}
