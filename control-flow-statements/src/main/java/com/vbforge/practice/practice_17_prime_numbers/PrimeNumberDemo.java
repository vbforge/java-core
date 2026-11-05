package com.vbforge.practice.practice_17_prime_numbers;

/**
 * Classic algorithm: all primes up to N by trial division.
 *
 * The outer while walks the candidates; the inner for tests each one, and break leaves the
 * inner loop as soon as a divisor is found.
 */
public class PrimeNumberDemo {

    public static void main(String[] args) {
        System.out.println("primes up to 100:");
        System.out.println(primesUpTo(100));
    }

    public static String primesUpTo(int limit) {
        StringBuilder out = new StringBuilder();
        int num = 2;
        while (num <= limit) {
            boolean isPrime = true;
            for (int i = 2; i < num; i++) {
                if (num % i == 0) { isPrime = false; break; }
            }
            if (isPrime) out.append(num).append(' ');
            num++;
        }
        return out.toString().trim();
    }
}
