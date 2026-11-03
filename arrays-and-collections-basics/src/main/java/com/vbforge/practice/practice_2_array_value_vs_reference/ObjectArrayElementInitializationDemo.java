package com.vbforge.practice.practice_2_array_value_vs_reference;

/**
 * Arrays of objects hold references: every element must be constructed
 * individually (unlike primitive arrays, where each slot is zero-initialised).
 *
 * <p>Here each object computes a Fibonacci-like sequence in its constructor.
 */
public class ObjectArrayElementInitializationDemo {

    public static void main(String[] args) {
        Fibonacci[] sequences = new Fibonacci[5];         // 5 null slots, no objects yet
        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = new Fibonacci(3 * i + 4);      // build each element separately
        }
        for (Fibonacci sequence : sequences) sequence.show();
    }

    private static final class Fibonacci {
        private final int[] numbers;

        Fibonacci(int n) {
            numbers = new int[n];
            if (n > 0) numbers[0] = 1;
            if (n > 1) numbers[1] = 1;
            for (int i = 2; i < n; i++) numbers[i] = numbers[i - 1] + numbers[i - 2];
        }

        void show() {
            for (int v : numbers) System.out.print(v + " ");
            System.out.println();
        }
    }
}
