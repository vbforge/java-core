package com.vbforge.practice.practice_1_array_basics;

/**
 * The three classic runtime failures when working with arrays.
 */
public class ArrayExceptionsDemo {

    public static void main(String[] args) {
        try {
            int[] arr = new int[2];
            System.out.println(arr[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("index outside 0..length-1 -> ArrayIndexOutOfBoundsException");
        }

        try {
            int[] arr = new int[-1];
            System.out.println(arr.length);
        } catch (NegativeArraySizeException e) {
            System.out.println("negative size             -> NegativeArraySizeException");
        }

        try {
            int[] arr = null;
            System.out.println(arr.length);
        } catch (NullPointerException e) {
            System.out.println("null array reference      -> NullPointerException");
        }
    }
}
