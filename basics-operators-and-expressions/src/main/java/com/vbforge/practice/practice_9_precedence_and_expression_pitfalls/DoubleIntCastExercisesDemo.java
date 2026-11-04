package com.vbforge.practice.practice_9_precedence_and_expression_pitfalls;

/**
 * Sedgewick exercise: given `double a = 3.14159`, predict each output.
 *
 *   a              = 3.14159
 *   a + 1          = 4.14159
 *   8 / (int) a    = 8 / 3       = 2     (the cast binds tighter than `/`: integer division)
 *   8 / a          = 2.546...            (floating-point division)
 *   (int)(8 / a)   = 2                   (cast of the floating-point result)
 */
public class DoubleIntCastExercisesDemo {

    public static void main(String[] args) {
        double a = 3.14159;
        System.out.println("a             = " + a);
        System.out.println("a + 1         = " + (a + 1));
        System.out.println("8 / (int) a   = " + (8 / (int) a));
        System.out.println("8 / a         = " + (8 / a));
        System.out.println("(int)(8 / a)  = " + ((int) (8 / a)));
    }
}
