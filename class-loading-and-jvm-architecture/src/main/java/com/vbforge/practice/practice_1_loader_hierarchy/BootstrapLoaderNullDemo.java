package com.vbforge.practice.practice_1_loader_hierarchy;

/**
 * The bootstrap loader is not a Java object, so getClassLoader() returns null
 * for JDK core classes, for primitives and for arrays of them.
 * Always test for null before you call getParent() or getClass() on a loader.
 */
public class BootstrapLoaderNullDemo {

    public static void main(String[] args) {
        Class<?>[] types = {
                String.class,
                java.util.ArrayList.class,
                int.class,
                String[].class,
                BootstrapLoaderNullDemo.class
        };
        for (Class<?> type : types) {
            System.out.printf("%-26s -> %s%n", type.getName(), describe(type.getClassLoader()));
        }

        try {
            String.class.getClassLoader().getParent();
        } catch (NullPointerException e) {
            System.out.println("String.class.getClassLoader().getParent() -> NullPointerException");
        }
    }

    static String describe(ClassLoader loader) {
        if (loader == null) {
            return "<bootstrap> (null)";
        }
        if (loader == ClassLoader.getSystemClassLoader()) {
            return "application loader";
        }
        return loader.getClass().getName();
    }
}
