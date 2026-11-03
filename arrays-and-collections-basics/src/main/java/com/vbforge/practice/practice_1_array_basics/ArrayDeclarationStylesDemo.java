package com.vbforge.practice.practice_1_array_basics;

/**
 * Ways to declare and initialise a one-dimensional array.
 *
 * <p>Arrays are fixed-size, zero-indexed, type-safe and heap-allocated.
 * {@code length} is a field (no parentheses), unlike {@code String.length()}.
 */
public class ArrayDeclarationStylesDemo {

    public static void main(String[] args) {
        int[] sized = new int[5];                 // size only: slots get default values
        int[] primes = {2, 3, 5, 7, 11};          // initializer shorthand (declaration statement only)
        int[] explicit = new int[]{2, 3, 5, 7, 11}; // explicit form, usable anywhere (e.g. as a method argument)
        int legacy[] = new int[3];                // C-style position of [] - legal, discouraged

        System.out.println("sized.length    = " + sized.length);
        System.out.println("primes[4]       = " + primes[4]);
        System.out.println("explicit.length = " + explicit.length);
        System.out.println("legacy.length   = " + legacy.length);
    }
}
