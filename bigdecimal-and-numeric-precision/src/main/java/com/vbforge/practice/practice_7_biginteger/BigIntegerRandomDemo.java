package com.vbforge.practice.practice_7_biginteger;

import java.math.BigInteger;
import java.util.Random;

/**
 * new BigInteger(numBits, Random): random value in the range 0 .. 2^numBits - 1.
 * A seeded Random makes the output reproducible.
 */
public class BigIntegerRandomDemo {

    public static void main(String[] args) {
        BigInteger random = new BigInteger(100, new Random(42));
        System.out.println("random(100 bits, seed 42) = " + random);
        System.out.println("bit length <= 100         : " + (random.bitLength() <= 100));
    }
}
