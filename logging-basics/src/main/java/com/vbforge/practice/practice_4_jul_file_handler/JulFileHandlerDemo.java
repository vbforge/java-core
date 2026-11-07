package com.vbforge.practice.practice_4_jul_file_handler;

import com.vbforge.practice.practice_1_person_sample_data.PersonRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * java.util.logging: a FileHandler writing to a log file (here a temp file, read back and deleted at the end).
 */
public class JulFileHandlerDemo {

    private static final Logger logger = Logger.getLogger(JulFileHandlerDemo.class.getName());

    public static void main(String[] args) throws IOException {
        // Deterministic SimpleFormatter output: level and message only (the default format adds a timestamp)
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s%n");

        Path dir = Files.createTempDirectory("jul-file-handler");
        Path logFile = dir.resolve("application.log");
        FileHandler fileHandler = null;
        try {
            // Set up the file handler
            // (append mode 'true': next logs are added to the end of the file, so the whole history is kept)
            // (append mode 'false': the existing content is overwritten when the handler opens the file)
            fileHandler = new FileHandler(logFile.toString(), false);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            // Keep the records out of the console so the only output is the file content printed below
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL); // Set logging level to ALL

            // Log messages using the logger
            logger.info("Starting JulFileHandlerDemo");

            PersonRecord johnRecord = new PersonRecord("John", 30);
            logger.info("Created person: " + johnRecord.fullDescription());
            logger.info("John's adult status: " + johnRecord.isAdult());

            PersonRecord janeRecord = new PersonRecord("Jane", 17);
            logger.info("Created person: " + janeRecord.fullDescription());
            logger.info("Jane's adult status: " + janeRecord.isAdult());

            logger.info("Comparison between John and Jane: " + PersonRecord.compareAges(johnRecord, janeRecord));

            logger.info("Exiting JulFileHandlerDemo");
        } catch (IOException e) {
            logger.severe("Failed to set up file logger: " + e.getMessage());
        } finally {
            if (fileHandler != null) {
                logger.removeHandler(fileHandler);
                fileHandler.close(); // flushes and releases the file (and its .lck lock file)
            }
        }

        System.out.println("--- content of " + logFile.getFileName() + " ---");
        Files.readAllLines(logFile).forEach(System.out::println);

        try (var files = Files.list(dir)) {
            for (Path p : (Iterable<Path>) files::iterator) {
                Files.deleteIfExists(p);
            }
        }
        Files.deleteIfExists(dir);
    }
}
