package com.vbforge.oop_interfaces_and_abstraction.defaults;

/**
 * One abstract method ({@link #log(String)}), two default methods built on top of it, two static
 * helpers and one private helper (Java 9+).
 */
public interface Logger {

    /** The only method an implementer MUST write. */
    void log(String message);

    /** Default method: implementers get it for free and may override it. */
    default void warn(String message) {
        log(decorate("WARN", message));
    }

    default void error(String message) {
        log(decorate("ERROR", message));
    }

    /** Static factory: belongs to the interface itself and is NOT inherited by implementers. */
    static Logger silent() {
        return message -> { };
    }

    /** Static helper, called as {@code Logger.prefix("INFO")}. */
    static String prefix(String level) {
        return "[" + level + "] ";
    }

    /** Private interface method (Java 9): shared code for the defaults, invisible to implementers. */
    private String decorate(String level, String message) {
        return prefix(level) + message;
    }
}
