package com.vbforge.practice.practice_6_bitwise_operators;

import java.util.Random;

/**
 * Prints full 32-bit / 64-bit binary views of every bitwise operator's result.
 * Useful because `System.out.println` on an int hides the actual bit pattern.
 *
 * Sign bit reminder: leftmost bit is 0 for positive, 1 for negative.
 * A fixed seed keeps the "random" operands (and so the output) reproducible.
 */
public class BitManipulationBinaryPrinterDemo {

    public static void main(String[] args) {
        Random rand = new Random(42);

        int i = rand.nextInt();
        int j = rand.nextInt();

        printInt("-1", -1);
        printInt("+1", +1);
        printInt("Integer.MAX_VALUE", Integer.MAX_VALUE);
        printInt("Integer.MIN_VALUE", Integer.MIN_VALUE);
        printInt("i", i);
        printInt("~i", ~i);
        printInt("-i", -i);
        printInt("j", j);
        printInt("i & j", i & j);
        printInt("i | j", i | j);
        printInt("i ^ j", i ^ j);
        printInt("i << 5", i << 5);
        printInt("i >> 5", i >> 5);
        printInt("(~i) >> 5", (~i) >> 5);
        printInt("i >>> 5", i >>> 5);
        printInt("(~i) >>> 5", (~i) >>> 5);

        long l = rand.nextLong();
        long m = rand.nextLong();

        printLong("-1L", -1L);
        printLong("+1L", +1L);
        printLong("Long.MAX_VALUE", Long.MAX_VALUE);
        printLong("Long.MIN_VALUE", Long.MIN_VALUE);
        printLong("l", l);
        printLong("~l", ~l);
        printLong("-l", -l);
        printLong("m", m);
        printLong("l & m", l & m);
        printLong("l | m", l | m);
        printLong("l ^ m", l ^ m);
        printLong("l << 5", l << 5);
        printLong("l >> 5", l >> 5);
        printLong("(~l) >> 5", (~l) >> 5);
        printLong("l >>> 5", l >>> 5);
        printLong("(~l) >>> 5", (~l) >>> 5);
    }

    private static void printInt(String label, int v) {
        System.out.printf("%-16s int: %-12d bin: %s%n",
                label, v, toBinary(v));
    }

    private static void printLong(String label, long v) {
        System.out.printf("%-16s long: %-22d bin: %s%n",
                label, v, toBinary(v));
    }

    private static String toBinary(int v) {
        return String.format("%32s", Integer.toBinaryString(v)).replace(' ', '0');
    }

    private static String toBinary(long v) {
        return String.format("%64s", Long.toBinaryString(v)).replace(' ', '0');
    }
}
