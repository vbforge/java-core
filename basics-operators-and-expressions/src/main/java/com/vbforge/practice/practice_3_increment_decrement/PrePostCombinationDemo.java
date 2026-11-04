package com.vbforge.practice.practice_3_increment_decrement;

/**
 * `++` / `--` inside larger expressions. Evaluation is left-to-right, so the
 * value produced by each prefix/postfix operator depends on where it sits.
 *
 * Do NOT rely on these combinations in real code — write the step out explicitly.
 */
public class PrePostCombinationDemo {

    public static void main(String[] args) {
        System.out.println("--- ++k + ++k ---");
        int k = 5;
        int kSum = ++k + ++k;         // (6) + (7) = 13, k = 7
        System.out.println("k = " + k + ", kSum = " + kSum);

        System.out.println("\n--- 2 * ++t ---");
        int t = 100;
        int v = 2 * ++t;              // t = 101, v = 202
        System.out.println("t = " + t + ", v = " + v);

        System.out.println("\n--- 2 * u++ ---");
        int u = 100;
        int i = 2 * u++;              // i = 200, u = 101
        System.out.println("u = " + u + ", i = " + i);
    }
}
