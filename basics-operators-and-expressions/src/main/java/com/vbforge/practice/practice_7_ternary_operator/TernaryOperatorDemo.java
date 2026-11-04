package com.vbforge.practice.practice_7_ternary_operator;

/**
 * Ternary is an EXPRESSION `cond ? a : b`, not a statement.
 * The two branches must produce a common type (widening may apply).
 */
public class TernaryOperatorDemo {

    public static void main(String[] args) {
        System.out.println("--- max of two ints ---");
        int a = 5, b = 10;
        System.out.println("max = " + ((a > b) ? a : b));

        System.out.println("\n--- accept / learn more ---");
        int experience = 5, requirements = 10;
        System.out.println((experience > requirements) ? "Accept" : "Learn more");

        System.out.println("\n--- even / odd ---");
        int number = 10;
        System.out.println(number + " is " + ((number % 2 == 0) ? "even" : "odd"));

        System.out.println("\n--- if/else vs ternary ---");
        int purchaseItem = 5, discount = 10;
        final int LIMIT = 3;
        int bonusIfElse;
        if (purchaseItem > LIMIT) bonusIfElse = discount;
        else                      bonusIfElse = 0;
        int bonusTernary = purchaseItem > LIMIT ? discount : 0;
        System.out.println("if/else = " + bonusIfElse + ", ternary = " + bonusTernary);

        System.out.println("\n--- type of the result: short operands become int ---");
        short x = 3, y = 12;
        System.out.println("x < y ? (x+y) : (x-y) = " + (x < y ? (x + y) : (x - y)));

        System.out.println("\n--- nested ternary: grade ---");
        int score = 87;
        char grade = score >= 90 ? 'A'
                   : score >= 80 ? 'B'
                   : score >= 70 ? 'C'
                   : 'F';
        System.out.println("score " + score + " → grade " + grade);

        System.out.println("\n--- recursive factorial via ternary ---");
        System.out.println("factorial(5) = " + factorial(5));
    }

    static int factorial(int n) {
        return (n == 0 || n == 1) ? 1 : n * factorial(n - 1);
    }
}
