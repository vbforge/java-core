package com.vbforge.practice.practice_2_loader_sample_classes;

/**
 * Shared fixture (not a concept): a class with static state and no dependencies
 * besides Object, so it can be defined in an isolated loader on its own.
 */
public class Counter {

    private static int count;

    public static int next() {
        return ++count;
    }
}
