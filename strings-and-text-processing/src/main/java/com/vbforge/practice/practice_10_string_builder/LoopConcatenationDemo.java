package com.vbforge.practice.practice_10_string_builder;

/**
 * {@code str += i} in a loop builds a new String (and copies everything so far) on every pass;
 * one StringBuilder appends into a single buffer. The result is the same, the work is not.
 * Timings are left out on purpose so the output is deterministic.
 */
public class LoopConcatenationDemo {

    public static void main(String[] args) {
        int n = 10_000;

        String str = "";
        for (int i = 0; i < n; i++) str += i;   // n intermediate String objects

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(i); // single mutable buffer

        System.out.println("n = " + n);
        System.out.println("String +=      length: " + str.length());
        System.out.println("StringBuilder  length: " + sb.length());
        System.out.println("same result:           " + str.equals(sb.toString()));
    }
}
