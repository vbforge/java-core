package com.vbforge.practice.practice_8_context_class_loader;

import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;
import com.vbforge.practice.practice_5_child_first_loader.ChildFirstClassLoader;

/**
 * A "framework" method cannot know which loader its caller wants, so it asks the thread's
 * context class loader. Swap it for the call, and always restore it in finally.
 */
public class ContextClassLoaderSwapDemo {

    public static void main(String[] args) throws Exception {
        Thread current = Thread.currentThread();
        ClassLoader original = current.getContextClassLoader();
        System.out.println("default context loader is the application loader: "
                + (original == ClassLoader.getSystemClassLoader()));

        ClassLoader plugin = new ChildFirstClassLoader(original, Greeter.class.getName());

        System.out.println("framework before the swap returns the app's Greeter: " + (frameworkLoad() == Greeter.class));

        current.setContextClassLoader(plugin);
        try {
            Class<?> viaContext = frameworkLoad();
            System.out.println("framework during the swap uses the plugin loader:   " + (viaContext.getClassLoader() == plugin));
        } finally {
            current.setContextClassLoader(original);
        }

        System.out.println("framework after the restore returns the app's Greeter: " + (frameworkLoad() == Greeter.class));
    }

    /** What libraries (ServiceLoader, JNDI, JDBC drivers, ...) do: no loader from the caller, use the context one. */
    static Class<?> frameworkLoad() throws ClassNotFoundException {
        return Class.forName(Greeter.class.getName(), false, Thread.currentThread().getContextClassLoader());
    }
}
