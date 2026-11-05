package com.vbforge.practice.practice_15_break_continue;

/** break inside a for-each: stop scanning as soon as a condition is met and count the steps taken. */
public class BreakInForEachDemo {

    public static void main(String[] args) {
        int[] scores = {100, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int passmark = 12;
        int steps = 0;
        for (int score : scores) {
            steps++;
            if (score >= passmark) break;
        }
        System.out.println("steps to find a pass: " + steps);
    }
}
