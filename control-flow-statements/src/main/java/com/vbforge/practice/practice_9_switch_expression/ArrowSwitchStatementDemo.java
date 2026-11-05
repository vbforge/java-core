package com.vbforge.practice.practice_9_switch_expression;

/** Arrow-form switch used as a statement (Java 14+): no fall-through, no break needed. */
public class ArrowSwitchStatementDemo {

    public static void main(String[] args) {
        printNumber(3);
        printNumber(4);
        printNumber(9);
    }

    static void printNumber(int n) {
        switch (n) {
            case 1 -> System.out.println("one");
            case 2 -> System.out.println("two");
            case 3 -> System.out.println("three");
            case 4 -> System.out.println("four");
            default -> System.out.println("many");
        }
    }
}
