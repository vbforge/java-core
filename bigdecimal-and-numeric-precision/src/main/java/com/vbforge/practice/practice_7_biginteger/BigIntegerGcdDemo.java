package com.vbforge.practice.practice_7_biginteger;

import java.math.BigInteger;

/**
 * BigInteger.gcd: greatest common divisor of two huge numbers.
 */
public class BigIntegerGcdDemo {

    public static void main(String[] args) {
        BigInteger a = new BigInteger("123456789012345678901234567890");
        BigInteger b = new BigInteger("987654321098765432109876543210");

        System.out.println("gcd = " + a.gcd(b));   // 9000000000900000000090
    }
}
