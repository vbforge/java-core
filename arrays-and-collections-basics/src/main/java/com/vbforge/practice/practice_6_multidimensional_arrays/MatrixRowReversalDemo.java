package com.vbforge.practice.practice_6_multidimensional_arrays;

import java.util.Arrays;

/**
 * Reordering a 2D array: reverse the ROW order, reverse the elements INSIDE
 * each row, and the "snake" pattern (reverse every odd row only).
 */
public class MatrixRowReversalDemo {

    public static void main(String[] args) {
        System.out.println("--- reverse row iteration order ---");
        int[][] m = {{100, 200, 300}, {400, 500, 600}, {700, 800, 900}};
        for (int r = m.length - 1; r >= 0; r--) {
            System.out.println("  " + Arrays.toString(m[r]));
        }

        System.out.println("\n--- reverse elements within each row ---");
        for (int[] row : m) reverseRow(row);
        for (int[] row : m) System.out.println("  " + Arrays.toString(row));

        System.out.println("\n--- snake pattern (reverse odd rows only) ---");
        int[][] snake = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        snakeReverseOddRows(snake);
        for (int[] row : snake) System.out.println("  " + Arrays.toString(row));
    }

    /** Reverses odd rows only, producing a snake-like reading order. */
    static void snakeReverseOddRows(int[][] matrix) {
        for (int r = 1; r < matrix.length; r += 2) {
            reverseRow(matrix[r]);
        }
    }

    private static void reverseRow(int[] row) {
        for (int i = 0, j = row.length - 1; i < j; i++, j--) {
            int tmp = row[i];
            row[i] = row[j];
            row[j] = tmp;
        }
    }
}
