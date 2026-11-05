package com.vbforge.practice.practice_12_nested_loops;

/**
 * Nested for loops - the outer loop picks the table, the inner loop prints its rows.
 * The inner loop runs completely for every single iteration of the outer loop.
 */
public class NestedLoopDemo {

    public static void main(String[] args) {
        for (int i = 2; i <= 9; i++) {
            System.out.println("Table of " + i + ":");
            for (int j = 1; j <= 10; j++) {
                System.out.println("  " + i + " * " + j + " = " + (i * j));
            }
        }
    }
}
