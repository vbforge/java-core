package com.vbforge.practice.practice_14_do_while_loop;

import java.util.Scanner;

/**
 * "Validate until valid": ask once, then repeat while the answer is wrong.
 * main feeds scripted input (1, 9, 5) so the output is deterministic.
 */
public class DoWhileValidateInputDemo {

    public static void main(String[] args) {
        promptForFive(new Scanner("1 9 5"));
    }

    static void promptForFive(Scanner scanner) {
        int value;
        do {
            System.out.print("Press 5: ");
            value = scanner.nextInt();
            System.out.println(value);
        } while (value != 5);
        System.out.println("Perfect!");
    }
}
