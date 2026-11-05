package com.vbforge.practice.practice_8_classic_switch;

/**
 * Fall-through: without break, execution continues into the next case labels
 * (and into default) until a break or the end of the switch.
 */
public class SwitchFallThroughDemo {

    public static void main(String[] args) {
        System.out.println("--- no break: withoutBreak(2) ---");
        withoutBreak(2);

        System.out.println("\n--- with break: withBreak(2) ---");
        withBreak(2);
    }

    static void withoutBreak(int n) {
        switch (n) {
            case 1:
                System.out.println("  one");
            case 2:
                System.out.println("  two");
            case 3:
                System.out.println("  three");
            default:
                System.out.println("  default");
        }
    }

    static void withBreak(int n) {
        switch (n) {
            case 1:
                System.out.println("  one");
                break;
            case 2:
                System.out.println("  two");
                break;
            case 3:
                System.out.println("  three");
                break;
            default:
                System.out.println("  default");
        }
    }
}
