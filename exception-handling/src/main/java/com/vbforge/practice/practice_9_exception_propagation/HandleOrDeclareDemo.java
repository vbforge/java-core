package com.vbforge.practice.practice_9_exception_propagation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A checked exception can be dealt with in three ways:
 *
 *   1. Declare it with `throws` — the CALLER must handle (or declare) it
 *   2. Handle it inside the same method (try/catch)
 *   3. Nobody handles it — the JVM prints the stack trace and ends the thread
 */
public class HandleOrDeclareDemo {

    // (1) declare — the CALLER must handle it
    static void readFileDeclaration(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        scanner.close();
    }

    // (2) handle — inside the same method
    static void readFileHandle(String path) {
        try {
            Scanner scanner = new Scanner(new File(path));
            scanner.close();
            System.out.println("  " + path + " opened");
        } catch (FileNotFoundException e) {
            System.out.println("  " + path + " not found");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- (1) declared with throws, handled in the caller ---");
        try {
            readFileDeclaration("missing.txt");
        } catch (FileNotFoundException e) {
            System.out.println("  caught in main: " + e.getMessage());
        }

        System.out.println("\n--- (2) handled inside the method ---");
        readFileHandle("missing.txt");
        readFileHandle("also-missing.txt");

        System.out.println("\n--- (3) never handled: JVM prints stack trace and exits ---");
        // To see it: declare `main(...) throws FileNotFoundException` and call
        // readFileDeclaration("missing.txt") without a try/catch — the program ends here.
    }
}
