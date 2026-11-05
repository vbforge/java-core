package com.vbforge.practice.practice_14_do_while_loop;

import java.util.Scanner;

/**
 * do-while + switch is the natural shape of a menu: always show it once, then repeat until
 * the exit choice. The Scanner is a parameter so any input source can drive the menu;
 * main feeds a scripted String (deterministic). Use new Scanner(System.in) for interactive use.
 */
public class DoWhileMenuDemo {

    public static void main(String[] args) {
        // choices: 1 (greet), 2 (sum 3 + 4), 7 (invalid), 3 (exit)
        simpleMenu(new Scanner("1\n2\n3 4\n7\n3\n"));
    }

    static void simpleMenu(Scanner scanner) {
        int choice;
        do {
            System.out.println("\nMenu:");
            System.out.println("1. Greet");
            System.out.println("2. Sum");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> System.out.println("Hello!");
                case 2 -> {
                    System.out.print("a: "); int a = scanner.nextInt();
                    System.out.print("b: "); int b = scanner.nextInt();
                    System.out.println("sum = " + (a + b));
                }
                case 3 -> System.out.println("Bye");
                default -> System.out.println("Unknown choice");
            }
        } while (choice != 3);
    }
}
