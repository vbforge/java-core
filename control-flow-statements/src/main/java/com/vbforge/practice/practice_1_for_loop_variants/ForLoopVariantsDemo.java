package com.vbforge.practice.practice_1_for_loop_variants;

/**
 * Ten valid shapes of the classic {@code for} loop:
 * {@code for (init; condition; update) { body }}.
 *
 * <p>Any of the three parts can be empty; several statements in init/update are
 * separated by commas; {@code for (;;)} without a {@code break} is an infinite loop.
 */
public class ForLoopVariantsDemo {

    public static void main(String[] args) {
        System.out.println("1. explicit index:");
        for (int i = 0; i < 5; i++) System.out.print(i + " ");
        System.out.println();

        System.out.println("\n2. custom update in body:");
        for (int i = 0; i < 10; ) { System.out.print(i + " "); i += 2; }
        System.out.println();

        System.out.println("\n3. forward iteration over array:");
        int[] arr = {10, 20, 30, 40};
        for (int i = 0; i < arr.length; i++) System.out.print(arr[i] + " ");
        System.out.println();

        System.out.println("\n4. reverse iteration:");
        for (int i = arr.length - 1; i >= 0; i--) System.out.print(arr[i] + " ");
        System.out.println();

        System.out.println("\n5. two variables (converging):");
        for (int i = 0, j = 5; i < j; i++, j--) System.out.println("  i=" + i + ", j=" + j);

        System.out.println("\n6. omitted parts:");
        int k = 0;
        for (; k < 3; ) { System.out.print(k + " "); k++; }
        System.out.println();

        System.out.println("\n7. infinite loop with break:");
        int m = 0;
        for (;;) { if (m >= 3) break; System.out.print(m + " "); m++; }
        System.out.println();

        System.out.println("\n8. modify array in place:");
        String[] names = {"alice", "bob", "carol"};
        for (int i = 0; i < names.length; i++) names[i] = names[i].toUpperCase();
        for (String n : names) System.out.print(n + " ");
        System.out.println();

        System.out.println("\n9. index in body:");
        for (int i = 0; i < 6; i++) if (i % 2 == 0) System.out.println("  even index: " + i);

        System.out.println("\n10. parameterised range:");
        int start = 1, end = 5, step = 1;
        for (int i = start; i <= end; i += step) System.out.println("  step " + i);
    }
}
