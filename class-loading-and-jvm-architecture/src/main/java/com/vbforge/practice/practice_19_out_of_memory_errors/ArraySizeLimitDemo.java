package com.vbforge.practice.practice_19_out_of_memory_errors;

/**
 * An array of Integer.MAX_VALUE elements is above what HotSpot allows (the header needs a few
 * slots), so it fails at once with a different OutOfMemoryError message, whatever -Xmx is.
 * OutOfMemoryError is an Error, not an Exception.
 */
public class ArraySizeLimitDemo {

    public static void main(String[] args) {
        try {
            int[] impossible = new int[Integer.MAX_VALUE];
            System.out.println("allocated " + impossible.length);
        } catch (OutOfMemoryError e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println("is an Error: " + (((Object) e) instanceof Error));
        }
    }
}
