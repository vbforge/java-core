package com.vbforge.practice.practice_24_static_initializer_block;

/** A static block fills the lookup table once, when the class is loaded. */
public class StaticBlockSquareTableDemo {

    private static final int LIMIT = 10;
    private static final int[] square = new int[LIMIT];

    static {
        for (int i = 0; i < LIMIT; i++) {
            square[i] = i * i;
        }
    }

    public static int getSquare(int i) {
        return square[i];
    }

    public static void main(String[] args) {
        int num = 5;
        System.out.println(num + " squared is: " + getSquare(num));
    }
}
