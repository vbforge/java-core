package com.vbforge.practice.practice_11_custom_exceptions;

import com.vbforge.practice.practice_11_custom_exceptions.FileMissingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Runs FileProcessor: a checked custom exception the caller must handle, and a
 * standard unchecked exception for a null argument. Uses a temp file, so the
 * output does not depend on the working directory.
 */
public class FileProcessorDemo {

    public static void main(String[] args) throws IOException {
        FileProcessor processor = new FileProcessor();
        Path file = Files.createTempFile("practice-lines", ".txt");
        try {
            Files.write(file, Arrays.asList("one", "two", "three"));

            System.out.println("--- existing file ---");
            System.out.println("  lines = " + processor.countLines(file.toString()));

            Files.delete(file);

            System.out.println("\n--- deleted file: checked FileMissingException ---");
            System.out.println("  lines = " + processor.countLines(file.toString()));
        } catch (FileMissingException e) {
            System.out.println("  caught FileMissingException (message starts with: "
                    + e.getMessage().substring(0, "File not found".length()) + ")");
        } finally {
            Files.deleteIfExists(file);
        }

        System.out.println("\n--- null input: unchecked NullPointerException ---");
        try {
            processor.riskyOperation(null);
        } catch (NullPointerException e) {
            System.out.println("  caught: " + e.getMessage());
        }
    }
}
