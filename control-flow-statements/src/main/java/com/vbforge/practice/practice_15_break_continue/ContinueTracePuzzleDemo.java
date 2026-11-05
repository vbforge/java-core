package com.vbforge.practice.practice_15_break_continue;

/**
 * Tracing puzzle: how many times is '!' printed?
 * i goes 9, 8, ..., 3 (one decrement per pass); at i == 2 the loop subtracts 2 and
 * continues with i == 0, which ends the loop. That is 7 passes + 1 = 8 prints.
 */
public class ContinueTracePuzzleDemo {

    public static void main(String[] args) {
        int count = 0;
        int i = 9;
        while (i > 0) {
            System.out.print("!");
            count++;
            if (i == 2) { i -= 2; continue; }
            i--;
        }
        System.out.println();
        System.out.println("printed " + count + " times");
    }
}
