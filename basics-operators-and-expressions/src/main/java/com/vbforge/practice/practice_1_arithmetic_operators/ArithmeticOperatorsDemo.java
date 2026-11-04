package com.vbforge.practice.practice_1_arithmetic_operators;

/**
 * Basic arithmetic: + - * / %.
 * Integer division truncates; `%` on negatives keeps the sign of the dividend.
 */
public class ArithmeticOperatorsDemo {

    public static void main(String[] args) {
        int a = 10, b = 20, c = 25;

        System.out.println("a + b = " + (a + b));   // 30
        System.out.println("a - b = " + (a - b));   // -10
        System.out.println("a * b = " + (a * b));   // 200
        System.out.println("b / a = " + (b / a));   // 2   (integer division)
        System.out.println("b % a = " + (b % a));   // 0
        System.out.println("c % a = " + (c % a));   // 5

        System.out.println("\n--- integer division truncates ---");
        System.out.println("7 / 2   = " + (7 / 2));     // 3
        System.out.println("7 / 2.0 = " + (7 / 2.0));   // 3.5 (double division)
        System.out.println("7 % 2   = " + (7 % 2));     // 1

        System.out.println("\n--- negative modulo keeps the sign of the dividend ---");
        System.out.println("-7 % 3  = " + (-7 % 3));    // -1
        System.out.println(" 7 % -3 = " + (7 % -3));    //  1
        System.out.println("-7 % -3 = " + (-7 % -3));   // -1
    }
}
