package com.vbforge.practice.practice_8_context_class_loader;

import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;
import com.vbforge.practice.practice_5_child_first_loader.ChildFirstClassLoader;

/**
 * A new thread copies the creator's context loader at construction time.
 * Threads created while the swap is active keep it, even after you restore the original.
 */
public class ContextLoaderInheritedByThreadDemo {

    public static void main(String[] args) throws Exception {
        Thread main = Thread.currentThread();
        ClassLoader original = main.getContextClassLoader();
        ClassLoader plugin = new ChildFirstClassLoader(original, Greeter.class.getName());

        main.setContextClassLoader(plugin);
        try {
            report("thread created while the plugin loader is the context loader", plugin);
        } finally {
            main.setContextClassLoader(original);
        }
        report("thread created after restoring the original", original);
    }

    private static void report(String label, ClassLoader expected) throws InterruptedException {
        ClassLoader[] seen = new ClassLoader[1];
        Thread child = new Thread(() -> seen[0] = Thread.currentThread().getContextClassLoader());
        child.start();
        child.join();
        System.out.println(label + ": child inherited it = " + (seen[0] == expected));
    }
}
