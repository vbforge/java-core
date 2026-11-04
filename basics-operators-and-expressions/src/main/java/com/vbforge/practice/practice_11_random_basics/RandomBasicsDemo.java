package com.vbforge.practice.practice_11_random_basics;

import java.util.Random;

/**
 * java.util.Random basics.
 * Not cryptographically secure — use java.security.SecureRandom for that.
 * Since Java 17 Random implements RandomGenerator, and `RandomGeneratorFactory`
 * is available if you need pluggable algorithms.
 *
 * A fixed seed makes every value below reproducible (same seed → same sequence).
 */
public class RandomBasicsDemo {

    public static void main(String[] args) {
        Random rng = new Random(2024);

        System.out.println("nextInt()         = " + rng.nextInt());
        System.out.println("nextInt(100)      = " + rng.nextInt(100));      // [0, 100)
        System.out.println("nextInt() % 100   = " + (rng.nextInt() % 100)); // can be negative!
        System.out.println("nextLong()        = " + rng.nextLong());
        System.out.println("nextDouble()      = " + rng.nextDouble());      // [0.0, 1.0)
        System.out.println("nextBoolean()     = " + rng.nextBoolean());

        System.out.println("\n--- same seed, same sequence ---");
        Random seededA = new Random(42);
        Random seededB = new Random(42);
        System.out.println("seededA.nextInt(100) = " + seededA.nextInt(100));
        System.out.println("seededB.nextInt(100) = " + seededB.nextInt(100));

        System.out.println("\n--- bounded helper (Java 17+) ---");
        System.out.println("nextInt(5, 10) = " + rng.nextInt(5, 10));       // [5, 10)
    }
}
