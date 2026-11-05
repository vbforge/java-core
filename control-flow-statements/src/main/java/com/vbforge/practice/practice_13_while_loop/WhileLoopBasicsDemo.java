package com.vbforge.practice.practice_13_while_loop;

/**
 * while-loop basics:
 *   - the condition is checked BEFORE each iteration, so the body may run 0 times
 *   - the loop variable must be updated inside the body
 */
public class WhileLoopBasicsDemo {

    public static void main(String[] args) {
        System.out.println("--- count 1..10 ---");
        int i = 1;
        while (i <= 10) { System.out.print(i++ + " "); }
        System.out.println();

        System.out.println("\n--- decrement 5..1 ---");
        int j = 5;
        while (j >= 1) { System.out.print(j-- + " "); }
        System.out.println();

        System.out.println("\n--- halve until 10 or below ---");
        int k = 100;
        while (k > 10) {
            System.out.print(k + " ");
            k /= 2;
        }
        System.out.println();
    }
}
