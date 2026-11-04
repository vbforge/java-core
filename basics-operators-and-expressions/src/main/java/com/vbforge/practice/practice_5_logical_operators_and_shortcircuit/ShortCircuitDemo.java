package com.vbforge.practice.practice_5_logical_operators_and_shortcircuit;

/**
 * && and || short-circuit: the right operand is not evaluated
 * if the result is already determined by the left operand.
 * The non-short-circuit forms & and | always evaluate both sides.
 */
public class ShortCircuitDemo {

    public static void main(String[] args) {
        System.out.println("--- && stops at the first false ---");
        if (test1(0) && test2(2) && test3(2)) {   // test3 is never called
            System.out.println("expression is true");
        } else {
            System.out.println("expression is false");
        }

        System.out.println("\n--- & evaluates every operand ---");
        if (test1(0) & test2(2) & test3(2)) {     // all three are called
            System.out.println("expression is true");
        } else {
            System.out.println("expression is false");
        }
    }

    static boolean test1(int v) { System.out.println("test1(" + v + ")"); return v < 1; }
    static boolean test2(int v) { System.out.println("test2(" + v + ")"); return v < 2; }
    static boolean test3(int v) { System.out.println("test3(" + v + ")"); return v < 3; }
}
