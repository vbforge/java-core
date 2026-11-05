package com.vbforge.practice.practice_3_if_else_chain;

/**
 * The condition of an if must be a boolean: a boolean variable or a boolean-returning
 * method can be used directly, no "== true" needed.
 *
 * Note: the second branch below, {@code else if (!isEven)}, is redundant - when the first
 * condition is false, a plain {@code else} already means "odd". It is kept to show that
 * negating a boolean variable with ! is a valid condition.
 */
public class BooleanConditionDemo {

    public static void main(String[] args) {
        int n = 127;
        boolean isEven = isEven(n);
        if (isEven) System.out.println(n + " is even");
        else if (!isEven) System.out.println(n + " is odd");
    }

    static boolean isEven(int n) {
        return n % 2 == 0;
    }
}
