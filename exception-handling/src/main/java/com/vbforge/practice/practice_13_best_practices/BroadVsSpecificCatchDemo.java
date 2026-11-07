package com.vbforge.practice.practice_13_best_practices;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Broad catch vs specific catch.
 *
 * Bad:  catch (Exception e) { generic message }   → all specific error info is lost
 * Good: catch the specific exception types the caller can actually act on
 */
public class BroadVsSpecificCatchDemo {

    public static void main(String[] args) {
        System.out.println("--- bad: broad catch swallows detail ---");
        badReadFile("missing.txt");

        System.out.println("\n--- good: specific catch, specific response ---");
        goodReadFile("missing.txt");
    }

    static void badReadFile(String path) {
        try {
            String content = Files.readString(Path.of(path));
            System.out.println(content);
        } catch (Exception e) {
            System.out.println("  [BAD] something went wrong");
        }
    }

    static void goodReadFile(String path) {
        try {
            String content = Files.readString(Path.of(path));
            System.out.println(content);
        } catch (NoSuchFileException e) {
            System.out.println("  [GOOD] file not found: " + path);
        } catch (IOException e) {
            System.out.println("  [GOOD] I/O error: " + e.getMessage());
        }
    }
}
