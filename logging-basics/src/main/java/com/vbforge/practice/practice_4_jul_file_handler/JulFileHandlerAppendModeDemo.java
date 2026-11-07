package com.vbforge.practice.practice_4_jul_file_handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * FileHandler append flag: two "runs" write to the same file, once with append=false (the second run
 * overwrites the first) and once with append=true (the second run adds to the first).
 */
public class JulFileHandlerAppendModeDemo {

    private static final Logger logger = Logger.getLogger(JulFileHandlerAppendModeDemo.class.getName());

    public static void main(String[] args) throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s%n");
        logger.setUseParentHandlers(false);

        Path dir = Files.createTempDirectory("jul-append-mode");
        try {
            Path overwriteFile = dir.resolve("overwrite.log");
            runTwice(overwriteFile, false);
            System.out.println("append=false -> " + Files.readAllLines(overwriteFile));

            Path appendFile = dir.resolve("append.log");
            runTwice(appendFile, true);
            System.out.println("append=true  -> " + Files.readAllLines(appendFile));
        } finally {
            try (var files = Files.list(dir)) {
                for (Path p : (Iterable<Path>) files::iterator) {
                    Files.deleteIfExists(p);
                }
            }
            Files.deleteIfExists(dir);
        }
    }

    private static void runTwice(Path file, boolean append) throws IOException {
        for (int run = 1; run <= 2; run++) {
            FileHandler handler = new FileHandler(file.toString(), append);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            try {
                logger.info("message from run " + run);
            } finally {
                logger.removeHandler(handler);
                handler.close();
            }
        }
    }
}
