package com.vbforge.practice.practice_15_anonymous_subclass_initializer_block;

/**
 * An anonymous subclass has no constructor of its own, so its instance initializer
 * block is the place to set inherited fields right after creation
 * (the "double brace" shape: new X() { { ... } }).
 */
public class AnonymousSubclassInitializerBlockDemo {

    static class SquarePrinter {
        private static final int[] square = new int[10];

        {
            for (int i = 0; i < 10; i++)
                square[i] = i * i;
        }

        String userName;
        int i;

        void print() {
            System.out.println("Hi " + userName + ", " + i + " squared is " + square[i] + '.');
        }
    }

    static SquarePrinter createAnonymousSquarePrinter() {
        return new SquarePrinter() {
            {
                userName = "Ed";
                i = 3;
            }
        };
    }

    public static void main(String[] args) {
        createAnonymousSquarePrinter().print();
    }
}
