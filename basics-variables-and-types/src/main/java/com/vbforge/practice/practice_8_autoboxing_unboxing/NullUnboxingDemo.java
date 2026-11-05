package com.vbforge.practice.practice_8_autoboxing_unboxing;

/**
 * Unboxing a null wrapper throws NullPointerException.
 * A wrapper field declared but not initialized defaults to null - unlike a primitive.
 */
public class NullUnboxingDemo {

    private static Integer initialized   = 0;   // auto-boxed
    private static Integer uninitialized;       // null

    public static void main(String[] args) {
        printWithPrimitive(initialized);        // ok, 0 is unboxed

        // printWithPrimitive(uninitialized);   // NPE - unboxing null
        if (uninitialized != null) {
            printWithPrimitive(uninitialized);
        } else {
            System.out.println("uninitialized is null - skipped");
        }

        try {
            printWithPrimitive(uninitialized);
        } catch (NullPointerException e) {
            System.out.println("NPE as expected");
        }
    }

    private static void printWithPrimitive(int value) {
        System.out.println("value = " + value);
    }
}
