package com.vbforge.practice.practice_14_try_with_resources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * try-with-resources: every resource declared in the parentheses is closed
 * automatically (in reverse order), before the catch block runs. Resource types
 * must implement AutoCloseable (FileReader and BufferedReader do, via Closeable).
 *
 * Here the file does not exist, so the first resource fails to open: nothing
 * has to be closed and the IOException goes straight to the catch block.
 */
public class TryWithResourcesFileDemo {

    public static void main(String[] args) {
        try (FileReader reader = new FileReader("missing.txt");
             BufferedReader br = new BufferedReader(reader)) {
            System.out.println(br.readLine());
        } catch (IOException e) {
            System.out.println("  caught IOException: " + e.getMessage());
        }
    }
}
