package com.vbforge.practice.practice_6_multidimensional_arrays;

/**
 * Creating a 2D array in two phases:
 * <ol>
 *   <li>{@code new int[5][]} - allocates only the outer array of row references (all null)</li>
 *   <li>{@code x[i] = new int[...]} - allocate each row separately</li>
 * </ol>
 * This is what makes jagged arrays possible (rows of different lengths).
 */
public class JaggedArrayCreationDemo {

    public static void main(String[] args) {
        System.out.println("--- phase 1: rows are null ---");
        int[][] jagged = new int[5][];
        for (int i = 0; i < jagged.length; i++) {
            System.out.println("jagged[" + i + "] = " + jagged[i]);   // null
        }

        System.out.println("\n--- phase 2: allocate rows of different length ---");
        for (int i = 0; i < jagged.length; i++) {
            jagged[i] = new int[i + 1];                               // row i has i + 1 elements
            System.out.println("jagged[" + i + "] length = " + jagged[i].length);
        }

        System.out.println("\n--- populated jagged array (default zeros) ---");
        for (int[] row : jagged) {
            for (int v : row) System.out.print(v + " ");
            System.out.println();
        }
    }
}
