package com.vbforge.practice.practice_8_classic_switch;

/**
 * Several case labels sharing one body: stacked labels, or labels followed by a block
 * (a block scopes local variables declared inside that group).
 */
public class SwitchGroupedCasesDemo {

    public static void main(String[] args) {
        System.out.println("--- stacked labels (weekdays) ---");
        for (int day : new int[]{1, 3, 6, 7, 9}) {
            System.out.println(day + " -> " + dayKind(day));
        }

        System.out.println("\n--- grouped labels with a block ---");
        for (int n : new int[]{8, 3, 42}) {
            System.out.println(n + " -> " + rangeOf(n));
        }
    }

    static String dayKind(int day) {
        switch (day) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return "weekday";
            case 6:
            case 7:
                return "weekend";
            default:
                return "invalid";
        }
    }

    static String rangeOf(int n) {
        switch (n) {
            case 1: case 2: case 3: case 4: case 5: {
                return "1..5";
            }
            case 6: case 7: case 8: case 9: case 10: {
                return "6..10";
            }
            default: {
                return "> 10 or <= 0";
            }
        }
    }
}
