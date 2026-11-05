package com.vbforge.practice.practice_12_boolean_algebra;

/**
 * Show that
 *   (!(a && b) && (a || b)) || ((a && b) || !(a || b))
 * evaluates to true for every combination of a and b (a tautology).
 */
public class TautologyTruthTableDemo {

    public static void main(String[] args) {
        boolean[] values = {true, false};
        for (boolean a : values) {
            for (boolean b : values) {
                boolean result = (!(a && b) && (a || b)) || ((a && b) || !(a || b));
                System.out.println("a=" + a + ", b=" + b + " => " + result);
            }
        }
    }
}
