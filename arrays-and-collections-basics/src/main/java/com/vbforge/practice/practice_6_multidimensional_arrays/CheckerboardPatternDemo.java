package com.vbforge.practice.practice_6_multidimensional_arrays;

/**
 * Fills an 8x8 {@code int[][]} with 0 / 1 using {@code (row + col) % 2}:
 * the "colour" of a cell is derived from its coordinates.
 */
public class CheckerboardPatternDemo {

    private static final int ROWS = 8;
    private static final int COLS = 8;

    public static void main(String[] args) {
        printBoard(buildBoard());
    }

    private static int[][] buildBoard() {
        int[][] board = new int[ROWS][COLS];
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                board[r][c] = (r + c) % 2 == 0 ? 0 : 1;
        return board;
    }

    private static void printBoard(int[][] board) {
        for (int[] row : board) {
            for (int cell : row) System.out.print(cell + " ");
            System.out.println();
        }
    }
}
