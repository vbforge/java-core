package com.vbforge.practice.practice_15_break_continue;

/**
 * break exits the innermost loop; continue skips to its next iteration.
 * The same "search" written two ways: with break, and with return from a helper
 * (often cleaner than break, because the method ends as soon as the answer is known).
 */
public class BreakVersusReturnDemo {

    public static void main(String[] args) {
        System.out.println("--- break: stop when value reaches 10 ---");
        printUntilTenWithBreak(1);

        System.out.println("\n--- return from a helper: same logic ---");
        System.out.println(findFirstAtOrAbove(1, 10));
    }

    static void printUntilTenWithBreak(int start) {
        int value = start;
        while (value >= 0) {
            if (value < 10) {
                value++;
                continue;
            }
            System.out.println(value);
            System.out.println("Done");
            break;
        }
    }

    static String findFirstAtOrAbove(int start, int threshold) {
        int value = start;
        while (value >= 0) {
            if (value < threshold) {
                value++;
                continue;
            }
            return "Found " + value;
        }
        return value + " is negative";
    }
}
