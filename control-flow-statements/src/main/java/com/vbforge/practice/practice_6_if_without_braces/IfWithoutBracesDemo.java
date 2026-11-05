package com.vbforge.practice.practice_6_if_without_braces;

/**
 * An if without braces guards only the FIRST statement that follows it.
 *
 * demonstrate(10): the condition is false, so only the guarded line is skipped;
 * the second println is a separate statement and still runs.
 * demonstrate(11): both lines print, which hides the bug.
 *
 * Rule: always use braces for if.
 */
public class IfWithoutBracesDemo {

    public static void main(String[] args) {
        demonstrate(10);
        System.out.println();
        demonstrate(11);
    }

    static void demonstrate(int number) {
        System.out.println("number = " + number);
        if (number > 10)
            System.out.println("  greater than 10");                  // guarded by the if
            System.out.println("  you should enter a smaller number"); // NOT guarded - always runs
    }
}
