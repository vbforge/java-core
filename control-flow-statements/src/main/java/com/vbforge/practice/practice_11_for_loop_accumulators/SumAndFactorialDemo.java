package com.vbforge.practice.practice_11_for_loop_accumulators;

/**
 * Accumulator pattern with a for loop: start from the neutral element
 * (0 for a sum, 1 for a product) and fold each i into it.
 *
 * Note: the product uses int, which overflows for n > 12.
 */
public class SumAndFactorialDemo {

    public static void main(String[] args) {
        System.out.println("sum 1..6 = " + finiteSum(6));
        System.out.println("6!       = " + finiteProduct(6));
    }

    /** Sum 1..n. */
    static int finiteSum(int n) {
        int sum = 0;
        for (int i = 1; i <= n; i++) sum += i;
        return sum;
    }

    /** Product 1..n (factorial). */
    static int finiteProduct(int n) {
        int product = 1;
        for (int i = 1; i <= n; i++) product *= i;
        return product;
    }
}
