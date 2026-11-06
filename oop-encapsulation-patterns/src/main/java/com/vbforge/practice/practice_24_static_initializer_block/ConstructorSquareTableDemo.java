package com.vbforge.practice.practice_24_static_initializer_block;

/**
 * BAD: static data filled by a constructor. The table stays empty until somebody
 * creates an instance, and it is refilled on every 'new'.
 */
public class ConstructorSquareTableDemo {

    private static final int LIMIT = 10;
    private static final int[] square = new int[LIMIT];

    public ConstructorSquareTableDemo() {
        for (int i = 0; i < LIMIT; i++) {
            square[i] = i * i;
        }
    }

    public static int getSquare(int i) {
        return square[i];
    }

    public static void main(String[] args) {
        System.out.println("3 squared before any instance: " + getSquare(3));   // 0
        new ConstructorSquareTableDemo();
        System.out.println("3 squared is: " + getSquare(3));                    // 9
    }
}
