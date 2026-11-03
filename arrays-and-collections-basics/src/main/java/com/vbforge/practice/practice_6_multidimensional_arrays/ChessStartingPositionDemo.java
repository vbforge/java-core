package com.vbforge.practice.practice_6_multidimensional_arrays;

/**
 * Builds the 8x8 chess starting position as a {@code String[][]} - a "real"
 * 2D array example beyond the plain numeric matrix.
 * Black is on rows 0-1, white on rows 6-7; the queen stands on column 3, the king on column 4.
 */
public class ChessStartingPositionDemo {

    private static final int ROWS = 8;
    private static final int COLS = 8;

    private static final String[] BLACK_BACK_RANK = {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"};
    private static final String[] WHITE_BACK_RANK = {"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"};
    private static final String BLACK_PAWN = "♟";
    private static final String WHITE_PAWN = "♙";
    private static final String EMPTY = "_";

    public static void main(String[] args) {
        printBoard(buildBoard());
    }

    private static String[][] buildBoard() {
        String[][] board = new String[ROWS][COLS];

        for (int c = 0; c < COLS; c++) {
            board[0][c] = BLACK_BACK_RANK[c];
            board[1][c] = BLACK_PAWN;
            board[6][c] = WHITE_PAWN;
            board[7][c] = WHITE_BACK_RANK[c];
        }

        for (int r = 2; r < 6; r++)
            for (int c = 0; c < COLS; c++)
                board[r][c] = EMPTY;

        return board;
    }

    private static void printBoard(String[][] board) {
        for (String[] row : board) {
            for (String cell : row) System.out.print(cell + " ");
            System.out.println();
        }
    }
}
