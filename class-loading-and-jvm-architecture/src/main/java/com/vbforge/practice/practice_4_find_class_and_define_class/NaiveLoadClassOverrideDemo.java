package com.vbforge.practice.practice_4_find_class_and_define_class;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;

/**
 * The trap: override loadClass to "add a source" and forget the parent.
 * Defining Greeter needs java.lang.Object, the naive loader tries to define that
 * too, and the JVM refuses: java.* may only come from the bootstrap loader.
 * Override findClass instead and delegation stays intact.
 */
public class NaiveLoadClassOverrideDemo {

    static class NaiveLoader extends ClassLoader {

        NaiveLoader() {
            super(null);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            byte[] bytes = ClassBytes.read(name);
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            new NaiveLoader().loadClass(Greeter.class.getName());
            System.out.println("loaded without a problem");
        } catch (SecurityException e) {
            System.out.println("SecurityException: " + e.getMessage());
        }
    }
}
