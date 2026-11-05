package com.vbforge.practice.practice_7_biginteger;

import java.math.BigInteger;

/**
 * BigInteger.isProbablePrime(certainty): probabilistic primality test.
 * certainty 100 means the error chance is below 2^-100.
 */
public class BigIntegerProbablePrimeDemo {

    public static void main(String[] args) {
        System.out.println("997 is probable prime: " + BigInteger.valueOf(997).isProbablePrime(100));   // true
        System.out.println("999 is probable prime: " + BigInteger.valueOf(999).isProbablePrime(100));   // false
    }
}
