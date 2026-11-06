package com.vbforge.practice.practice_6_default_method_conflicts_and_interface_super;

/**
 * Two unrelated interfaces declare the same default log(String).
 * A class implementing both MUST override it, otherwise the compiler reports:
 *   "class ClassImpl inherits unrelated defaults for log(String) from types ConsoleLogging and FileLogging"
 */
public class DefaultMethodDiamondConflictDemo {

    interface ConsoleLogging {
        default void log(String str) {
            System.out.println("ConsoleLogging default. Logging: " + str);
        }
    }

    interface FileLogging {
        default void log(String str) {
            System.out.println("FileLogging default. Logging: " + str);
        }
    }

    static class ClassImpl implements ConsoleLogging, FileLogging {
        // Mandatory override: resolves the ambiguity between the two defaults
        @Override
        public void log(String str) {
            System.out.println("ClassImpl. Logging : " + str);
        }
    }

    public static void main(String[] args) {
        ClassImpl obj = new ClassImpl();
        obj.log("hello");          // the class override wins
    }
}
