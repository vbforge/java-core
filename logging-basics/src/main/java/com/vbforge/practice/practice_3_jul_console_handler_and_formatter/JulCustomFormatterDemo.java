package com.vbforge.practice.practice_3_jul_console_handler_and_formatter;

import com.vbforge.practice.practice_1_person_sample_data.PersonRecord;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * java.util.logging: a ConsoleHandler with a custom Formatter.
 * A message is printed only if it passes TWO level checks: the logger's level AND the handler's level.
 */
public class JulCustomFormatterDemo {

    private static final Logger logger = Logger.getLogger(JulCustomFormatterDemo.class.getName());

    public static class CustomLogFormatter extends SimpleFormatter {
        @Override
        public String format(LogRecord record) {
            return String.format("[%s]: %s%n",
                    record.getLevel(),
                    record.getMessage());
        }
    }

    public static void main(String[] args) {
        // Set up the console handler with the custom formatter
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new CustomLogFormatter());
        // ConsoleHandler's own level defaults to INFO, so without this line every FINE message would be dropped
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        // Without this, each record would ALSO go to the root logger's default ConsoleHandler (printed twice, in two formats)
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL); // Logger level ALL: the logger itself lets every record through to its handlers

        // Log messages using the logger
        logger.fine("Starting JulCustomFormatterDemo");

        PersonRecord johnRecord = new PersonRecord("John", 30);
        logger.info("Created person: " + johnRecord.fullDescription());
        logger.fine("John's adult status: " + johnRecord.isAdult());

        PersonRecord janeRecord = new PersonRecord("Jane", 17);
        logger.info("Created person: " + janeRecord.fullDescription());
        logger.fine("Jane's adult status: " + janeRecord.isAdult());

        logger.fine("Comparison between John and Jane: " + PersonRecord.compareAges(johnRecord, janeRecord));

        logger.fine("Exiting JulCustomFormatterDemo");
    }
}
