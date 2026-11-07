package com.vbforge.practice.practice_15_formatter_to_file;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Formatter;

/**
 * Write formatted lines (java.util.Formatter over a PrintWriter) to a file, then read the file back.
 * Uses a temp file that is deleted at the end, so the demo leaves nothing behind.
 */
public class FormatterToFileDemo {

    public static void main(String[] args) {
        String[] words = "Order a1 has 3 items".split("\\s+");
        Path file = null;
        try {
            file = Files.createTempFile("formatter-demo", ".txt");
            try (PrintWriter writer = new PrintWriter(file.toFile());
                 Formatter formatter = new Formatter(writer)) {
                formatter.format("Total words: %d%n", words.length);
                for (String word : words) {
                    formatter.format("Word: %s, Length: %d%n", word, word.length());
                }
            }
            System.out.println("Formatted output written; file content:");
            System.out.print(Files.readString(file));
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        } finally {
            try {
                if (file != null) Files.deleteIfExists(file);
            } catch (IOException ignored) {
                // temp file cleanup is best effort
            }
        }
    }
}
