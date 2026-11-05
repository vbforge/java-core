package com.vbforge.practice.practice_17_varargs;

/**
 * Varargs (`int...`) rules:
 *   - accepted as zero or more arguments
 *   - at the call site it's just syntactic sugar for an array
 *   - must be the last parameter in the signature (fixed parameters come before it)
 *   - an actual int[] can be passed directly
 */
public class VarargsDemo {

    public static void main(String[] args) {
        System.out.println("--- varargs as counter ---");
        count();                          // 0
        count(1);                         // 1
        count(2, 3);                      // 2
        count(new int[]{1, 2, 3, 4, 5});  // 5
        count(new int[0]);                // 0

        System.out.println("\n--- varargs as sum ---");
        System.out.println(sum());                    // 0
        System.out.println(sum(3));                   // 3
        System.out.println(sum(55, 66));              // 121
        System.out.println(sum(77, 55, 33, 11, 99));  // 275

        System.out.println("\n--- fixed parameter + varargs ---");
        System.out.println("describe(\"nums\", 1, 2, 3) = " + describe("nums", 1, 2, 3));
        System.out.println("describe(\"none\")           = " + describe("none"));
    }

    private static void count(int... values) {
        System.out.println("count = " + values.length);
    }

    private static int sum(int... values) {
        int total = 0;
        for (int v : values) {
            total += v;
        }
        return total;
    }

    private static String describe(String label, int... values) {
        return label + " -> count=" + values.length;
    }
}
