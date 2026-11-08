package com.vbforge.practice.practice_14_initialization_failure;

/**
 * A failing static initialiser is reported once as ExceptionInInitializerError (its cause is
 * the real bug). Every later use of that class throws NoClassDefFoundError
 * "Could not initialize class ...": the class stays unusable for the life of its loader.
 * When you read a log, look for the FIRST error.
 */
public class InitializationFailureDemo {

    static class Broken {
        static final int VALUE = explode();

        static int explode() {
            throw new IllegalStateException("boom in the static initializer");
        }
    }

    static class Healthy {
        static int value = 1;
    }

    public static void main(String[] args) {
        try {
            System.out.println(Broken.VALUE);
        } catch (ExceptionInInitializerError e) {
            System.out.println("1st use: ExceptionInInitializerError, cause = "
                    + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
        }

        try {
            System.out.println(Broken.VALUE);
        } catch (NoClassDefFoundError e) {
            System.out.println("2nd use: NoClassDefFoundError, message starts with 'Could not initialize class': "
                    + e.getMessage().startsWith("Could not initialize class"));
        }

        System.out.println("other classes are unaffected: Healthy.value = " + Healthy.value);
    }
}
