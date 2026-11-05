package com.vbforge.practice.practice_14_do_while_loop;

import java.util.Scanner;

/**
 * A two-operand calculator that repeats until the user answers something other than 'y'.
 * Uses a switch expression on a char; an unknown operator throws.
 * main feeds scripted input: "+ 2 3", again, "* 4 5", stop.
 */
public class DoWhileCalculatorDemo {

    public static void main(String[] args) {
        calculator(new Scanner("+ 2 3 y * 4 5 n"));
    }

    static void calculator(Scanner scanner) {
        char again;
        do {
            System.out.print("Operator (+, -, *, /): ");
            char op = scanner.next().charAt(0);
            System.out.print("First:  "); double a = scanner.nextDouble();
            System.out.print("Second: "); double b = scanner.nextDouble();
            double result = switch (op) {
                case '+' -> a + b;
                case '-' -> a - b;
                case '*' -> a * b;
                case '/' -> a / b;
                default  -> throw new IllegalArgumentException("bad operator: " + op);
            };
            System.out.println("= " + result);
            System.out.print("Another? (y/n): ");
            again = scanner.next().charAt(0);
            System.out.println(again);
        } while (again == 'y');
    }
}
