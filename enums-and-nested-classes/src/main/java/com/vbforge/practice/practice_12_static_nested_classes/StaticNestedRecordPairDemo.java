package com.vbforge.practice.practice_12_static_nested_classes;

import java.util.Arrays;
import java.util.Random;

/**
 * A static nested record (Pair) used to return two values (min and max) from one method.
 * The "random" array uses a fixed seed so the output is reproducible.
 */
public class StaticNestedRecordPairDemo {

    public static void main(String[] args) {
        double[] arrayRandom = new double[20];
        double[] arrayCustom = {10, 20, 60, 5, -3, 0, 14};
        Random random = new Random(42);
        // a for-each loop cannot modify the array elements, so fill it with an indexed loop
        for (int i = 0; i < arrayRandom.length; i++) {
            double randomVal = 100 * random.nextDouble();
            arrayRandom[i] = Math.round(randomVal * 100) / 100.0;   // keep two digits after the point (25.94)
        }
        System.out.println("Random array: " + Arrays.toString(arrayRandom));
        Pair pairRand = Pair.searchMinMax(arrayRandom);
        System.out.println("Min: " + pairRand.min);   // record field access (same top-level class)
        System.out.println("Max: " + pairRand.max);

        System.out.println("Custom array: " + Arrays.toString(arrayCustom));
        Pair pairCustom = Pair.searchMinMax(arrayCustom);
        System.out.println("Min: " + pairCustom.min());   // accessor
        System.out.println("Max: " + pairCustom.max());
    }

    public record Pair(double min, double max) {
        public static Pair searchMinMax(double[] array) {
            double min = array[0];
            double max = array[0];
            for (double v : array) {
                if (min > v) {
                    min = v;
                }
                if (max < v) {
                    max = v;
                }
            }
            return new Pair(min, max);
        }
    }
}
