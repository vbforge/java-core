package com.vbforge.practice.practice_1_floating_point_error;

/**
 * Why double/float cannot be trusted for exact decimal arithmetic.
 * Binary floating point (IEEE 754) cannot represent most decimal fractions exactly,
 * so tiny errors show up in plain subtraction and addition.
 */
public class FloatingPointArithmeticErrorDemo {

    public static void main(String[] args) {
        double d1 = 0.3;
        double d2 = 0.1;
        System.out.println("0.3 - 0.1   = " + (d1 - d2));      // 0.19999999999999998
        System.out.println("0.1 + 0.2   = " + (0.1 + 0.2));    // 0.30000000000000004

        float f1 = 0.4f;
        float f2 = 0.3f;
        System.out.println("0.4f - 0.3f = " + (f1 - f2));      // 0.099999994
    }
}
