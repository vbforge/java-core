package com.vbforge.practice.practice_15_class_file_errors;

/**
 * ClassNotFoundException is a checked Exception: a name you asked for explicitly was not found.
 * The class-file problems (ClassFormatError, UnsupportedClassVersionError, NoClassDefFoundError,
 * ExceptionInInitializerError) are Errors under LinkageError.
 */
public class ClassNotFoundVersusLinkageErrorDemo {

    public static void main(String[] args) {
        try {
            Class.forName("com.acme.DoesNotExist");
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException is an Exception: " + (e instanceof Exception)
                    + ", an Error: " + ((Object) e instanceof Error));
        }

        System.out.println("superclass chain of UnsupportedClassVersionError:");
        for (Class<?> c = UnsupportedClassVersionError.class; c != null; c = c.getSuperclass()) {
            System.out.println("   " + c.getName());
        }

        System.out.println("NoClassDefFoundError is a LinkageError:        "
                + LinkageError.class.isAssignableFrom(NoClassDefFoundError.class));
        System.out.println("ExceptionInInitializerError is a LinkageError: "
                + LinkageError.class.isAssignableFrom(ExceptionInInitializerError.class));
    }
}
