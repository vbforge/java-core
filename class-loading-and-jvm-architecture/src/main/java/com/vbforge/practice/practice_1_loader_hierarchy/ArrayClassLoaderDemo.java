package com.vbforge.practice.practice_1_loader_hierarchy;

/**
 * An array class has no class file of its own: it reports the loader of its
 * element type. Arrays of primitives and of JDK classes therefore report null.
 */
public class ArrayClassLoaderDemo {

    static class Local {
    }

    public static void main(String[] args) {
        ClassLoader localLoader = Local.class.getClassLoader();

        System.out.println("Local[]   has the loader of Local: " + (Local[].class.getClassLoader() == localLoader));
        System.out.println("Local[][] has the loader of Local: " + (Local[][].class.getClassLoader() == localLoader));
        System.out.println("String[]  loader is null (bootstrap): " + (String[].class.getClassLoader() == null));
        System.out.println("int[]     loader is null (bootstrap): " + (int[].class.getClassLoader() == null));
    }
}
