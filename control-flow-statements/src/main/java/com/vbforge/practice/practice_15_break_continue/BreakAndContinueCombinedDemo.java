package com.vbforge.practice.practice_15_break_continue;

/**
 * break and continue together, in a for loop and in an infinite while loop.
 * break ends the loop; continue skips the rest of the current iteration.
 */
public class BreakAndContinueCombinedDemo {

    public static void main(String[] args) {
        System.out.println("--- for: break at 74, continue on non-multiples of 9 ---");
        for (int i = 0; i < 100; i++) {
            if (i == 74) break;
            if (i % 9 != 0) continue;
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.println("\n--- infinite while: break when i * 27 == 1269, print multiples of 10 ---");
        int i = 0;
        while (true) {
            i++;
            int j = i * 27;
            if (j == 1269) break;
            if (i % 10 != 0) continue;
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
