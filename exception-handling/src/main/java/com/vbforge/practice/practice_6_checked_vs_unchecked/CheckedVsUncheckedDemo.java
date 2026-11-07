package com.vbforge.practice.practice_6_checked_vs_unchecked;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Checked vs unchecked exceptions — the structural difference.
 *
 *   CHECKED   — extends Exception (not RuntimeException)
 *               MUST be caught or declared in `throws`
 *               signals a recoverable condition the caller must handle
 *               examples: IOException, SQLException, ClassNotFoundException
 *
 *   UNCHECKED — extends RuntimeException
 *               no forced handling; the compiler doesn't care
 *               signals a programming error (bug) the caller should fix
 *               examples: NullPointerException, ArithmeticException,
 *                         ArrayIndexOutOfBoundsException, ClassCastException
 *
 *   ERROR     — extends Error; JVM-level failure; do NOT catch
 *               examples: OutOfMemoryError, StackOverflowError
 */
public class CheckedVsUncheckedDemo {

    public static void main(String[] args) {
        System.out.println("--- checked: FileNotFoundException ---");
        checkedExample();

        System.out.println("\n--- unchecked: ArithmeticException ---");
        uncheckedArithmetic();

        System.out.println("\n--- unchecked: NullPointerException ---");
        uncheckedNullPointer();

        System.out.println("\n--- unchecked: ArrayIndexOutOfBoundsException ---");
        uncheckedOutOfBounds();
    }

    /** A checked exception must be handled (caught or re-thrown). */
    static void checkedExample() {
        File file = new File("this-does-not-exist.txt");
        try {
            Scanner scanner = new Scanner(file);
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("  caught FileNotFoundException — recoverable");
        }
    }

    static void uncheckedArithmetic() {
        try {
            int a = 1 / 0;
            System.out.println(a);
        } catch (ArithmeticException e) {
            System.out.println("  caught ArithmeticException: " + e.getMessage());
        }
    }

    static void uncheckedNullPointer() {
        try {
            String name = null;
            name.hashCode();
        } catch (NullPointerException e) {
            System.out.println("  caught NullPointerException");
        }
    }

    static void uncheckedOutOfBounds() {
        try {
            int[] arr = new int[2];
            System.out.println(arr[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  caught ArrayIndexOutOfBoundsException");
        }
    }
}
