package com.vbforge.practice.practice_13_while_loop;

import java.util.Random;

/**
 * while for an unknown number of iterations: loop until a value reaches a threshold.
 * The Random is seeded so the attempt count is the same on every run.
 */
public class WhileUntilConditionMetDemo {

    public static void main(String[] args) {
        Random random = new Random(42);
        double r = 0;
        int attempts = 0;
        while (r < 0.99) {
            r = random.nextDouble();
            attempts++;
        }
        System.out.println("took " + attempts + " attempts; last value = " + r);
    }
}
