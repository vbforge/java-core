package com.vbforge.practice.practice_11_for_loop_accumulators;

/**
 * Accumulating into a StringBuilder inside a for loop: each iteration appends one more
 * number and prints the whole string built so far.
 */
public class GrowingStringLoopDemo {

    public static void main(String[] args) {
        growingString(9);
    }

    static void growingString(int n) {
        StringBuilder text = new StringBuilder("Ruler");
        for (int i = 0; i <= n; i++) {
            text.append(' ').append(i);
            System.out.println(text);
        }
    }
}
