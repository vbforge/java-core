package com.vbforge.practice.practice_9_precedence_and_expression_pitfalls;

/**
 * `+` is left-associative: once the left side becomes a String, everything
 * after it is concatenated. `"x = " + 2 + 2` → "x = 22".
 * Parenthesise the arithmetic to get the sum.
 */
public class StringConcatLeftAssociativityDemo {

    public static void main(String[] args) {
        System.out.println("2 + 2 = " + 2 + 2);       // "2 + 2 = 22"
        System.out.println("2 + 2 = " + (2 + 2));     // "2 + 2 = 4"   ← parenthesise
        System.out.println((2 + 2) + " = 2 + 2");     // "4 = 2 + 2"
        System.out.println(2 + 2 + " = 2 + 2");       // "4 = 2 + 2"   (arithmetic first, then the String appears)
    }
}
