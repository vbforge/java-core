package com.vbforge.practice.practice_7_private_interface_methods;

/**
 * Private interface methods (Java 9+): shared code for default methods that implementers cannot see.
 * Loggable also has a static factory that returns a lambda, since Loggable has one abstract method.
 */
public class PrivateInterfaceMethodDemo {

    interface Loggable {
        String getLogTag();

        default void logInfo(String message)  { System.out.println(formatLog("INFO",  message)); }
        default void logError(String message) { System.out.println(formatLog("ERROR", message)); }

        private String formatLog(String level, String message) {
            return "[" + level + "] [" + getLogTag() + "] " + message;
        }

        static Loggable of(String tag) {
            return () -> tag;
        }
    }

    static class Document implements Loggable {
        private final String id;

        Document(String id) { this.id = id; }

        @Override
        public String getLogTag() { return "Document#" + id; }
        // formatLog(...) is not visible here
    }

    public static void main(String[] args) {
        Loggable docLog = new Document("DOC-001");
        docLog.logInfo("Document loaded");
        docLog.logError("Something went wrong");

        Loggable serviceLog = Loggable.of("OrderService");
        serviceLog.logInfo("Order created");
    }
}
