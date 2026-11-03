package com.vbforge.practice.practice_1_array_basics;

import java.util.Arrays;

/**
 * Default values of freshly allocated arrays.
 *
 * <ul>
 *   <li>primitives: {@code 0}, {@code 0.0}, {@code false}, the NUL character</li>
 *   <li>objects (including wrappers such as {@code Integer}): {@code null}</li>
 * </ul>
 *
 * Any object slot can also be set back to {@code null} later - a common source of NPE.
 */
public class ArrayDefaultValuesDemo {

    public static void main(String[] args) {
        System.out.println("--- primitive arrays: zero-values ---");
        System.out.println("int[]     : " + Arrays.toString(new int[3]));       // [0, 0, 0]
        System.out.println("double[]  : " + Arrays.toString(new double[3]));    // [0.0, 0.0, 0.0]
        System.out.println("boolean[] : " + Arrays.toString(new boolean[3]));   // [false, false, false]
        char[] chars = new char[3];
        System.out.println("char[]    : code of chars[0] = " + (int) chars[0]); // 0 = the NUL character

        System.out.println("\n--- object arrays: null per slot ---");
        System.out.println("String[]  : " + Arrays.toString(new String[3]));    // [null, null, null]

        Integer[] integers = new Integer[3];
        System.out.println("Integer[] : " + Arrays.toString(integers));         // [null, null, null]
        integers[0] = 5;
        System.out.println("after set : " + Arrays.toString(integers));         // [5, null, null]

        Foo[] foos = {new Foo(1), new Foo(2), new Foo(3)};
        System.out.println("Foo[]     : " + Arrays.toString(foos));
        foos[1] = null;                                                         // any slot can become null again
        System.out.println("after null: " + Arrays.toString(foos));
    }

    private static final class Foo {
        private final int id;

        Foo(int id) { this.id = id; }

        @Override
        public String toString() { return "Foo#" + id; }
    }
}
