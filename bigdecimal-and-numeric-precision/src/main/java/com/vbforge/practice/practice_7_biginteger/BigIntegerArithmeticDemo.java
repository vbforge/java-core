package com.vbforge.practice.practice_7_biginteger;

import java.math.BigInteger;

/**
 * BigInteger: arbitrary-precision integers for values that do not fit into long.
 * Operations are method calls (add, multiply), not operators.
 */
public class BigIntegerArithmeticDemo {

    public static void main(String[] args) {
        BigInteger n = new BigInteger("123456789012345678901234567890");
        BigInteger m = new BigInteger("123456789012345678901234567891");

        System.out.println("n     = " + n);
        System.out.println("n + m = " + n.add(m));
        System.out.println("n * m = " + n.multiply(m));
    }
}
