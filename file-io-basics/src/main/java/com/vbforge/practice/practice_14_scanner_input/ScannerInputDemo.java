package com.vbforge.practice.practice_14_scanner_input;

import java.util.Scanner;

/**
 * Scanner basics — reading typed tokens (String, int, byte, float).
 *
 * The input comes from a fixed String so the demo is deterministic. To type the
 * values yourself, replace `new Scanner(INPUT)` with `new Scanner(System.in)`
 * and print a prompt before each read.
 */
public class ScannerInputDemo {

    private static final String INPUT = "Alice 30 100 7 2.5";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(INPUT)) {
            String name = scanner.next();
            System.out.println("Hello, " + name);

            int age = scanner.nextInt();
            System.out.println("Next year you'll be " + (age + 1));

            byte a = scanner.nextByte();
            byte b = scanner.nextByte();

            System.out.println("sum   = " + (a + b));
            System.out.println("diff  = " + (a - b));
            System.out.println("prod  = " + (a * b));
            System.out.println("div   = " + ((float) a / b));
            System.out.println("rem   = " + (a % b));

            float f = scanner.nextFloat();
            System.out.println("float * 2 = " + (f * 2));
        }
    }
}
