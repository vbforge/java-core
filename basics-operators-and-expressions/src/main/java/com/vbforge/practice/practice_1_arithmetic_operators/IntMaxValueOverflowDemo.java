package com.vbforge.practice.practice_1_arithmetic_operators;

/**
 * Sedgewick exercise: given `int a = Integer.MAX_VALUE`, what do these print?
 *
 *   a         → 2147483647   (MAX_VALUE)
 *   a + 1     → -2147483648  (wraps to MIN_VALUE)
 *   2 - a     → -2147483645  (fits: no overflow)
 *   -2 - a    → 2147483647   (true result -2147483649 does NOT fit, wraps to MAX_VALUE)
 *   2 * a     → -2           (wraps)
 *   4 * a     → -4           (wraps)
 *
 * Integer arithmetic in Java wraps silently — no exception, no warning.
 */
public class IntMaxValueOverflowDemo {

    public static void main(String[] args) {
        int a = Integer.MAX_VALUE;

        System.out.println("a       = " + a);
        System.out.println("a + 1   = " + (a + 1));
        System.out.println("2 - a   = " + (2 - a));
        System.out.println("-2 - a  = " + (-2 - a));
        System.out.println("2 * a   = " + (2 * a));
        System.out.println("4 * a   = " + (4 * a));

        System.out.println("\n--- use Math.addExact for overflow checking ---");
        try {
            int checked = Math.addExact(a, 1);
            System.out.println(checked);
        } catch (ArithmeticException e) {
            System.out.println("ArithmeticException caught as expected");
        }
    }
}
