package com.vbforge.practice.practice_12_console_input;

import java.io.*;
import java.util.Scanner;

/**
 * Demonstrates reading from the console / standard input (stdin).
 *
 * Three approaches, from low to high level:
 *
 *   1. System.in  (InputStream)
 *      — raw byte stream; rarely used directly for text
 *
 *   2. BufferedReader wrapping InputStreamReader(System.in)
 *      — classic, efficient, line-by-line reading
 *
 *   3. Scanner(System.in)
 *      — high-level: parses tokens, numbers, lines
 *      — WARNING: mixing nextInt() and nextLine() leaves '\n' in buffer
 *
 *   4. Console (System.console())
 *      — terminal-only; returns null in IDE/piped runs
 *      — readPassword() suppresses echo
 */
public class ConsoleInputDemo {

    // =========================================================================
    // 1. BufferedReader — line-by-line, efficient
    // =========================================================================

    static String readLineViaBufferedReader(Reader in) throws IOException {
        BufferedReader br = new BufferedReader(in);
        return br.readLine();
    }

    static java.util.List<String> readAllLinesViaBufferedReader(Reader in) throws IOException {
        BufferedReader br = new BufferedReader(in);
        java.util.List<String> lines = new java.util.ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    // =========================================================================
    // 2. Scanner — token-based parsing
    // =========================================================================

    static int readIntViaScanner(InputStream in) {
        Scanner sc = new Scanner(in);
        int value = sc.nextInt();
        return value;
    }

    static java.util.List<String> readTokensViaScanner(InputStream in) {
        Scanner sc = new Scanner(in);
        java.util.List<String> tokens = new java.util.ArrayList<>();
        while (sc.hasNext()) {
            tokens.add(sc.next());
        }
        return tokens;
    }

    static String[] scannerNextIntThenNextLine(InputStream in) {
        Scanner sc = new Scanner(in);
        int num  = sc.nextInt();
        sc.nextLine();
        String rest = sc.nextLine();
        return new String[]{ String.valueOf(num), rest };
    }

    // =========================================================================
    // 3. Console — terminal-only, with readPassword()
    // =========================================================================

    static void demonstrateConsoleUsage() {
        System.out.println("\n--- 3. System.console() ---");

        Console console = System.console();
        if (console == null) {
            System.out.println("console == null (running in IDE or piped input)");
            System.out.println("System.console() is only non-null in a real terminal.");
            return;
        }

        String line = console.readLine("Enter your name: ");
        console.printf("Hello, %s!%n", line);

        char[] password = console.readPassword("Enter password (hidden): ");
        console.printf("Password length: %d%n", password.length);
        java.util.Arrays.fill(password, '\0');
    }

    // =========================================================================
    // 4. Reading numbers + mixed input (Scanner pattern)
    // =========================================================================

    static int sumFromInput(InputStream in) {
        Scanner sc = new Scanner(in);
        int n   = sc.nextInt();
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += sc.nextInt();
        }
        return sum;
    }

    // =========================================================================
    // main — interactive demo
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("=== Console Input Demo ===");

        System.out.println("\n--- 1. BufferedReader ---");
        System.out.print("Enter a line: ");
        String line = readLineViaBufferedReader(
            new InputStreamReader(System.in, java.nio.charset.StandardCharsets.UTF_8));
        System.out.println("You entered: " + line);

        System.out.println("\n--- 2. Scanner ---");
        System.out.print("Enter an integer: ");
        Scanner sc = new Scanner(System.in);
        if (sc.hasNextInt()) {
            System.out.println("Parsed int: " + sc.nextInt());
        }

        demonstrateConsoleUsage();
    }
}