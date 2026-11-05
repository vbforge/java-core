package com.vbforge.practice.practice_12_boolean_algebra;

/**
 * (!(a < b) && !(a > b)) is equivalent to (a == b).
 */
public class ComparisonNegationSimplificationDemo {

    public static void main(String[] args) {
        int a = 5;
        int b = 5;

        boolean original   = !(a < b) && !(a > b);
        boolean simplified = (a == b);

        System.out.println("original   = " + original);
        System.out.println("simplified = " + simplified);
        System.out.println("equivalent = " + (original == simplified));
    }
}
