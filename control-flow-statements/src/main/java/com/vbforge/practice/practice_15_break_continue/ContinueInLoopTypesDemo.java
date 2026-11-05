package com.vbforge.practice.practice_15_break_continue;

/**
 * continue in all three loop types.
 *
 * In a for, continue runs the update expression before re-checking the condition.
 * In a while / do-while, continue jumps straight to the condition - so the loop variable
 * must be updated BEFORE continue, or the loop never ends.
 */
public class ContinueInLoopTypesDemo {

    public static void main(String[] args) {
        System.out.println("--- for: continue skips evens ---");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) continue;
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.println("\n--- while: increment before continue ---");
        int i = 1;
        while (i <= 10) {
            if (i % 2 == 0) {
                i++;            // MUST happen before continue
                continue;
            }
            System.out.print(i + " ");
            i++;
        }
        System.out.println();

        System.out.println("\n--- do-while: same rule ---");
        i = 1;
        do {
            if (i % 2 == 0) {
                i++;
                continue;
            }
            System.out.print(i + " ");
            i++;
        } while (i <= 10);
        System.out.println();
    }
}
