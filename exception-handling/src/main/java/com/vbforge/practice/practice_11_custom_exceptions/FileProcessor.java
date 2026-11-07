package com.vbforge.practice.practice_11_custom_exceptions;

import com.vbforge.practice.practice_11_custom_exceptions.FileMissingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A custom checked exception next to a standard unchecked one:
 *   - countLines throws the CHECKED custom FileMissingException — caller must handle
 *   - riskyOperation throws the standard UNCHECKED NullPointerException (not a custom type)
 */
public class FileProcessor {

    public int countLines(String filePath) throws FileMissingException {
        try {
            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                throw new FileMissingException("File not found: " + filePath);
            }
            List<String> lines = Files.readAllLines(path);
            return lines.size();
        } catch (IOException e) {
            throw new FileMissingException("Error reading file: " + e.getMessage(), e);
        }
    }

    public int riskyOperation(String input) {
        if (input == null) {
            throw new NullPointerException("Input cannot be null");
        }
        return input.length();
    }
}
