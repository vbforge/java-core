package com.vbforge.practice.practice_22_class_file_sample_classes;

/**
 * Shared fixture (not a concept): small methods with predictable bytecode, read back from
 * its own .class file by practice_23..27. Do not add locals or branches: the demos expect
 * max_locals to equal the number of parameter slots.
 */
public class SampleMath {

    public int add(int a, int b) {
        return a + b;
    }

    public static int square(int x) {
        return x * x;
    }

    public static long twice(long x) {
        return x + x;
    }

    public double mix(double a, int b) {
        return a + b;
    }

    public String banner() {
        return "hello bytecode";
    }
}
