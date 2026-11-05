package com.vbforge.class_loading_and_jvm_architecture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class loaders and the parent-delegation model.
 *
 * <p>Sections:
 * <ol>
 *   <li>The loader hierarchy (and the {@code null} that means "bootstrap")</li>
 *   <li>Parent delegation, made visible with a tracing loader</li>
 *   <li>Same class name + different loader = different class</li>
 *   <li>The thread context class loader</li>
 * </ol>
 */
public class ClassLoaderDemo {

    /** Label used for the bootstrap loader, which has no {@code ClassLoader} object. */
    public static final String BOOTSTRAP = "<bootstrap>";

    /** Shared interface: always loaded by the parent, so every copy of Greeter agrees on it. */
    public interface Greeting {
        String greet();
    }

    /** A tiny class we load twice (through different loaders) in section 3. */
    public static class Greeter implements Greeting {
        @Override
        public String greet() {
            return "hello";
        }
    }

    public static void main(String[] args) throws Exception {
        loaderHierarchyDemo();
        System.out.println("---");
        delegationDemo();
        System.out.println("---");
        sameNameDifferentLoaderDemo();
        System.out.println("---");
        contextClassLoaderDemo();
    }

    // =========================================================================
    // 1. The loader hierarchy
    // =========================================================================

    /*
     * WHAT: Every class is defined by exactly one class loader. The usual chain is
     *       application loader -> (platform | extension) loader -> bootstrap loader.
     *
     * WHY:  The chain is what makes delegation work. Core classes (java.lang.String, ...)
     *       come from the bootstrap loader, so application code cannot replace them.
     *
     * GOTCHA: The bootstrap loader is native code, not a Java object. Class.getClassLoader()
     *       returns null for it - and so does getParent() of the top-most Java loader.
     *       Calling a method on that null is a classic NullPointerException.
     *       Primitives and arrays of bootstrap classes also report null.
     *       (Exact loader names differ between Java 8 and 9+, so we never hard-code them.)
     */

    /** Names of the loader classes from the defining loader up to the bootstrap, which is always last. */
    public static List<String> loaderChainNames(Class<?> type) {
        List<String> names = new ArrayList<>();
        ClassLoader loader = type.getClassLoader();
        while (loader != null) {
            names.add(loader.getClass().getName());
            loader = loader.getParent();
        }
        names.add(BOOTSTRAP);
        return names;
    }

    /** True when the class was defined by the bootstrap loader (getClassLoader() == null). */
    public static boolean isBootstrapClass(Class<?> type) {
        return type.getClassLoader() == null;
    }

    public static void loaderHierarchyDemo() {
        System.out.println("String        -> " + loaderChainNames(String.class));
        System.out.println("int           -> " + loaderChainNames(int.class));
        System.out.println("this class    -> " + loaderChainNames(ClassLoaderDemo.class));
    }

    // =========================================================================
    // 2. Parent delegation
    // =========================================================================

    /*
     * WHAT: ClassLoader.loadClass(name) first asks the PARENT. Only if the parent cannot
     *       find the class does the loader call its own findClass(name).
     *
     * WHY:  Guarantees one definition of each core class and stops code from impersonating
     *       java.* types. It is also why you override findClass (not loadClass) to add a source.
     *
     * Below, TracingClassLoader records every name it is asked for, then delegates. The class
     * that comes back is defined by the parent, NOT by the tracer: the tracer never defines it.
     */

    /** A pass-through loader that remembers which class names were requested from it. */
    public static class TracingClassLoader extends ClassLoader {
        private final List<String> requests = new ArrayList<>();

        public TracingClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            requests.add(name);
            return super.loadClass(name, resolve); // parent first
        }

        public List<String> requests() {
            return Collections.unmodifiableList(requests);
        }
    }

    public static void delegationDemo() throws ClassNotFoundException {
        TracingClassLoader tracer = new TracingClassLoader(ClassLoaderDemo.class.getClassLoader());
        Class<?> list = tracer.loadClass("java.util.ArrayList");
        Class<?> self = tracer.loadClass(ClassLoaderDemo.class.getName());
        System.out.println("requests seen by tracer : " + tracer.requests());
        System.out.println("ArrayList same as parent's: " + (list == java.util.ArrayList.class));
        System.out.println("this class defined by tracer? " + (self.getClassLoader() == tracer));
    }

    // =========================================================================
    // 3. Same name, different loader
    // =========================================================================

    /*
     * WHAT: A class's runtime identity is (binary name, defining loader). Two loaders that
     *       each define "Greeter" produce two unrelated Class objects.
     *
     * WHY:  This is how application servers, plugin systems and test frameworks isolate
     *       code, and why you can see "X cannot be cast to X" errors.
     *
     * IsolatingClassLoader breaks delegation on purpose for names with a given prefix
     * (child-first): it reads the .class bytes itself and calls defineClass.
     * Everything else (like the Greeting interface) still goes to the parent.
     *
     * GOTCHA: Casting to the class from the other loader throws ClassCastException,
     *         even though the names are identical. Casting to the shared interface works.
     */

    /** Child-first loader for classes whose name starts with {@code isolatedPrefix}. */
    public static class IsolatingClassLoader extends ClassLoader {
        private final String isolatedPrefix;

        public IsolatingClassLoader(ClassLoader parent, String isolatedPrefix) {
            super(parent);
            this.isolatedPrefix = isolatedPrefix;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                if (!name.startsWith(isolatedPrefix)) {
                    return super.loadClass(name, resolve); // normal parent-first delegation
                }
                Class<?> loaded = findLoadedClass(name);
                if (loaded == null) {
                    loaded = findClass(name); // child-first: define our own copy
                }
                if (resolve) {
                    resolveClass(loaded);
                }
                return loaded;
            }
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            String path = name.replace('.', '/') + ".class";
            ClassLoader parent = getParent();
            try (InputStream in = parent != null
                    ? parent.getResourceAsStream(path)
                    : ClassLoader.getSystemResourceAsStream(path)) {
                if (in == null) {
                    throw new ClassNotFoundException(name);
                }
                byte[] bytes = readFully(in);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }
    }

    /** Defines a brand-new copy of {@link Greeter} in a brand-new {@link IsolatingClassLoader}. */
    public static Class<?> isolatedGreeterClass() throws ClassNotFoundException {
        ClassLoader loader = new IsolatingClassLoader(
                ClassLoaderDemo.class.getClassLoader(), Greeter.class.getName());
        return loader.loadClass(Greeter.class.getName());
    }

    public static Object newIsolatedGreeter() throws ReflectiveOperationException {
        return isolatedGreeterClass().getDeclaredConstructor().newInstance();
    }

    /** The cast that fails when the object's class came from another loader. */
    public static Greeter castToGreeter(Object candidate) {
        return (Greeter) candidate;
    }

    public static void sameNameDifferentLoaderDemo() throws ReflectiveOperationException {
        Object isolated = newIsolatedGreeter();
        System.out.println("same name?            " + isolated.getClass().getName().equals(Greeter.class.getName()));
        System.out.println("same Class object?    " + (isolated.getClass() == Greeter.class));
        System.out.println("instanceof Greeting?  " + (isolated instanceof Greeting));
        try {
            castToGreeter(isolated);
        } catch (ClassCastException e) {
            System.out.println("cast to Greeter fails: ClassCastException");
        }
    }

    // =========================================================================
    // 4. Thread context class loader
    // =========================================================================

    /*
     * WHAT: Every thread carries a "context class loader" that library code can use to look up
     *       classes/resources that its own loader cannot see (JNDI, ServiceLoader, JDBC ...).
     *
     * WHY:  It is the escape hatch from strict parent delegation: a framework loaded by a
     *       high-level loader can still reach classes owned by a lower-level (app) loader.
     *
     * New threads inherit the context loader of the thread that created them.
     * Always restore the original in a finally block.
     */

    /** Sets the context loader to {@code temporary}, reads it back, then restores the original. */
    public static ClassLoader contextClassLoaderDuring(ClassLoader temporary) {
        Thread thread = Thread.currentThread();
        ClassLoader original = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(temporary);
            return thread.getContextClassLoader();
        } finally {
            thread.setContextClassLoader(original);
        }
    }

    /** True when a freshly created thread starts with its creator's context class loader. */
    public static boolean newThreadInheritsContextLoader() {
        ClassLoader mine = Thread.currentThread().getContextClassLoader();
        return new Thread().getContextClassLoader() == mine;
    }

    public static void contextClassLoaderDemo() {
        ClassLoader tracer = new TracingClassLoader(ClassLoaderDemo.class.getClassLoader());
        System.out.println("during swap  : " + (contextClassLoaderDuring(tracer) == tracer));
        System.out.println("new thread inherits: " + newThreadInheritsContextLoader());
    }

    // -------------------------------------------------------------------------
    // Shared byte helpers (also used by the other demos in this module)
    // -------------------------------------------------------------------------

    /** Raw bytes of the .class file for {@code type}, read through the class's own loader. */
    public static byte[] readClassFile(Class<?> type) throws IOException {
        String resource = "/" + type.getName().replace('.', '/') + ".class";
        try (InputStream in = type.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException("No class file found for " + type.getName());
            }
            return readFully(in);
        }
    }

    private static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }
}
