package com.vbforge.practice.practice_8_autoboxing_unboxing;

/**
 * Unboxing in its simplest forms:
 * - assigning a wrapper to a primitive variable
 * - unboxing inside a method call that takes a primitive
 * - unboxing in arithmetic that mixes wrapper and primitive
 */
public class UnboxingBasicsDemo {

    public static void main(String[] args) {
        Integer iOb = 7;
        Double dOb = 7.0;
        Character cOb = 'a';
        Boolean bOb = true;

        int i = iOb;         // unboxing
        double d = dOb;      // unboxing
        char c = cOb;        // unboxing
        boolean b = bOb;     // unboxing

        System.out.println(i + ", " + d + ", " + c + ", " + b);

        Integer f = 5;
        f = f + 10;          // unbox -> add -> rebox
        System.out.println("f = " + f); // 15

        printPrimitive(iOb); // unboxing at method call
    }

    private static void printPrimitive(int value) {
        System.out.println("primitive int = " + value);
    }
}
