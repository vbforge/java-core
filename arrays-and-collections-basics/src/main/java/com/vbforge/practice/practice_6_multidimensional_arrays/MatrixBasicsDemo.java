package com.vbforge.practice.practice_6_multidimensional_arrays;

import java.util.Arrays;

/**
 * 2D arrays are "arrays of arrays", not one contiguous matrix.
 * Creating, indexing, initialising in a loop and summing a rectangular matrix.
 */
public class MatrixBasicsDemo {

    public static void main(String[] args) {
        System.out.println("--- rectangular matrix ---");
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println("matrix[1][2] = " + matrix[1][2]);   // 6
        for (int[] row : matrix) System.out.println("  " + Arrays.toString(row));

        System.out.println("\n--- in-line 2D initialisation ---");
        char[][] board = {
                {'O', '-', '-'},
                {'O', '-', '-'},
                {'O', '-', '-'}
        };
        System.out.println(Arrays.deepToString(board));

        System.out.println("\n--- loop initialisation + sum ---");
        int[][] table = new int[3][5];
        for (int r = 0; r < table.length; r++)
            for (int c = 0; c < table[r].length; c++)
                table[r][c] = c + 1;
        for (int[] row : table) System.out.println("  " + Arrays.toString(row));

        int sum = 0;
        for (int[] row : table)
            for (int v : row)
                sum += v;
        System.out.println("sum = " + sum);   // 45
    }
}
