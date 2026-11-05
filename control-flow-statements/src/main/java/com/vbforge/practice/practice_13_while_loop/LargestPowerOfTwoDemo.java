package com.vbforge.practice.practice_13_while_loop;

/**
 * while that keeps doubling: the largest power of two that does not exceed n.
 * Valid for n >= 1 (for n < 1 the method returns 1).
 */
public class LargestPowerOfTwoDemo {

    public static void main(String[] args) {
        System.out.println("largestPowerOf2(64)  = " + largestPowerOf2(64));
        System.out.println("largestPowerOf2(100) = " + largestPowerOf2(100));
    }

    static int largestPowerOf2(int n) {
        int power = 1;
        while (power <= n / 2) {
            power = 2 * power;
        }
        return power;
    }
}
