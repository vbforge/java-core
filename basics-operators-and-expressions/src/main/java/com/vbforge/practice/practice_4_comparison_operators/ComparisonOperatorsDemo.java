package com.vbforge.practice.practice_4_comparison_operators;

/**
 * Comparison operators on primitives. All return boolean.
 *   ==  !=  <  <=  >  >=
 * (Object identity via ==, and String / wrapper identity, are separate topics
 * covered in the OOP and Strings modules — this file stays on primitives.)
 */
public class ComparisonOperatorsDemo {

    public static void main(String[] args) {
        int a = 10, b = 20;
        System.out.println("a == b : " + (a == b));
        System.out.println("a != b : " + (a != b));
        System.out.println("a >  b : " + (a > b));
        System.out.println("a <  b : " + (a < b));
        System.out.println("b >= a : " + (b >= a));
        System.out.println("b <= a : " + (b <= a));

        System.out.println("\n--- edge cases with a fixed value ---");
        int count = 1;
        System.out.println("count <  1 : " + (count <  1));
        System.out.println("count >  0 : " + (count >  0));
        System.out.println("count <= 0 : " + (count <= 0));
        System.out.println("count >= -1: " + (count >= -1));
        System.out.println("count == 0 : " + (count == 0));
        System.out.println("count != 1 : " + (count != 1));

        System.out.println("\n--- beware: floating point == is unreliable ---");
        System.out.println("0.1 + 0.2 == 0.3 : " + (0.1 + 0.2 == 0.3)); // false
        // For floats, use Math.abs(x - y) < epsilon, or BigDecimal for money.
    }
}
