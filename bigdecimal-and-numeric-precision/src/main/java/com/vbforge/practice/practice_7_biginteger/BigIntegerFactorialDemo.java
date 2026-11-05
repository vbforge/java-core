package com.vbforge.practice.practice_7_biginteger;

import java.math.BigInteger;

/**
 * Factorial with BigInteger: 20! is the largest factorial that still fits into long,
 * BigInteger keeps going.
 */
public class BigIntegerFactorialDemo {

    public static void main(String[] args) {
        BigInteger fact = BigInteger.ONE;
        for (int i = 1; i <= 20; i++) {
            fact = fact.multiply(BigInteger.valueOf(i));
        }
        System.out.println("20! = " + fact);   // 2432902008176640000
    }
}
