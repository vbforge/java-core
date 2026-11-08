package com.vbforge.practice.practice_3_parent_first_delegation;

import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;

/**
 * loadClass is parent-first: findLoadedClass, then parent.loadClass, and only if the
 * parent fails does findClass run. A class the parent can supply is defined by the
 * parent, so the loader you asked is not its defining loader.
 */
public class ParentFirstDelegationDemo {

    public static void main(String[] args) throws Exception {
        ClassLoader system = ClassLoader.getSystemClassLoader();
        TracingClassLoader tracing = new TracingClassLoader(system);

        System.out.println("1. tracing.loadClass(Greeter)  (the application loader can see it)");
        Class<?> greeter = tracing.loadClass(Greeter.class.getName());
        printTrace(tracing);
        System.out.println("   defined by the tracing loader:      " + (greeter.getClassLoader() == tracing));
        System.out.println("   defined by the application loader:  " + (greeter.getClassLoader() == system));
        System.out.println("   same Class object as Greeter.class: " + (greeter == Greeter.class));

        System.out.println("2. tracing.loadClass(java.lang.String)");
        Class<?> string = tracing.loadClass("java.lang.String");
        printTrace(tracing);
        System.out.println("   defined by the bootstrap loader (null): " + (string.getClassLoader() == null));

        System.out.println("3. tracing.loadClass(no.such.Plugin)  (nobody can supply it)");
        try {
            tracing.loadClass("no.such.Plugin");
        } catch (ClassNotFoundException e) {
            printTrace(tracing);
            System.out.println("   ClassNotFoundException: " + e.getMessage());
        }
    }

    private static void printTrace(TracingClassLoader loader) {
        for (String line : loader.drainTrace()) {
            System.out.println("   [trace] " + line);
        }
    }
}
