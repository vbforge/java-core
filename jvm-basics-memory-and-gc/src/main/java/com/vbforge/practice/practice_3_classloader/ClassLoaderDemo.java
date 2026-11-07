package com.vbforge.practice.practice_3_classloader;

/**
 * ClassLoader — how the JVM finds, loads, links, and initializes classes.
 *
 * Class loading is a LAZY process: a class is loaded only when first needed
 * (first access to a static member, first instantiation, etc.).
 *
 * The class loading pipeline has three phases:
 *
 *   1. LOADING
 *      - Find the class file (classpath, JAR, network, generated...)
 *      - Read the .class bytes
 *      - Create a java.lang.Class object in the Method Area
 *
 *   2. LINKING
 *      a) Verification  — bytecode is structurally valid and safe
 *      b) Preparation   — static fields allocated, set to default values (0, null, false)
 *      c) Resolution    — symbolic references (e.g., "java/lang/String") replaced with
 *                         direct memory references (optional / lazy)
 *
 *   3. INITIALIZATION
 *      - Static initializer blocks (<clinit>) run — exactly once, thread-safe
 *      - Static fields assigned their declared initial values
 *
 * ClassLoader Hierarchy (Parent Delegation Model):
 *
 *   Bootstrap ClassLoader  (C/C++ code, loads java.* — rt.jar / jmods)
 *         ↑ parent
 *   Platform ClassLoader   (Java 9+; loads javax.*, java.sql, etc.)
 *   (was Extension CL in Java 8)
 *         ↑ parent
 *   Application ClassLoader (loads your classpath / module-path classes)
 *         ↑ parent (optional)
 *   Custom ClassLoader      (plugin systems, hot-reload, isolation, OSGi)
 *
 * Parent Delegation:
 *   A class loader ALWAYS delegates to its parent FIRST before trying itself.
 *   Benefit: core classes (String, Object) can never be replaced by user code.
 *   Breaking it: override findClass() (not loadClass()) in custom ClassLoader.
 *
 * When is a class loaded?
 *   - First `new MyClass()`
 *   - First access to a static field (that is not a compile-time constant)
 *   - First call to a static method
 *   - Class.forName("com.example.Foo")
 *   - Reflective operations on the class
 *
 * Class identity = (ClassLoader, binary name)
 *   Two Class objects loaded by different ClassLoaders are DIFFERENT types.
 *   This is how OSGi, Tomcat webapps, etc. achieve class isolation.
 */
public class ClassLoaderDemo {

    // -----------------------------------------------------------------------
    // ClassLoader hierarchy inspection
    // -----------------------------------------------------------------------

    /**
     * Walks the ClassLoader chain from a given class up to Bootstrap.
     * Bootstrap CL is implemented natively and returns null in Java code.
     *
     * @param clazz the class to inspect
     */
    public static void printClassLoaderChain(Class<?> clazz) {
        System.out.println("ClassLoader chain for: " + clazz.getName());
        ClassLoader cl = clazz.getClassLoader();
        int level = 0;
        while (cl != null) {
            System.out.println("  Level " + level + ": " + cl.getClass().getName() + " → " + cl);
            cl = cl.getParent();
            level++;
        }
        System.out.println("  Level " + level + ": Bootstrap ClassLoader (null — native, loads java.*)");
    }

    /**
     * Bootstrap ClassLoader loads all java.* classes.
     * Its parent is null (it IS the root).
     */
    public static void bootstrapLoaderDemo() {
        System.out.println("\n=== Bootstrap ClassLoader ===");
        // java.lang.String is loaded by Bootstrap CL
        System.out.println("String's ClassLoader: " + String.class.getClassLoader()); // null = Bootstrap
        System.out.println("Object's ClassLoader: " + Object.class.getClassLoader()); // null = Bootstrap
    }

    /**
     * Application ClassLoader loads user-defined classes (this class itself).
     */
    public static void applicationLoaderDemo() {
        System.out.println("\n=== Application ClassLoader ===");
        ClassLoader appCL = ClassLoaderDemo.class.getClassLoader();
        System.out.println("ClassLoaderDemo loaded by: " + appCL);
        System.out.println("App CL parent: " + appCL.getParent()); // Platform CL
    }

    // -----------------------------------------------------------------------
    // Class.forName — dynamic loading
    // -----------------------------------------------------------------------

    /**
     * Class.forName() dynamically loads and INITIALIZES a class at runtime.
     * This triggers static initializer blocks.
     *
     * Two signatures:
     *   Class.forName(String name)                — uses calling class's CL, initializes
     *   Class.forName(String name, boolean init, ClassLoader loader)  — full control
     *
     * Common use: JDBC driver registration (DriverManager pattern).
     *
     * @param className fully-qualified class name
     * @return the loaded Class object, or null on failure
     */
    public static Class<?> dynamicLoad(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            System.out.println("Dynamically loaded: " + clazz.getName()
                    + " via " + clazz.getClassLoader());
            return clazz;
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + className);
            return null;
        }
    }

    // -----------------------------------------------------------------------
    // Static initialization order — Linking (Preparation) vs Initialization
    // -----------------------------------------------------------------------

    /**
     * Demonstrates that class initialization is lazy.
     *
     * EagerInit's static block runs when the class is first referenced.
     * Until that moment, no initialization happens.
     */
    static class EagerInit {
        static final String TOKEN;

        static {
            System.out.println("  [EagerInit] static initializer running");
            TOKEN = "INITIALIZED_" + System.currentTimeMillis();
        }

        static String getToken() { return TOKEN; }
    }

    /**
     * Demonstrates Initialization-on-Demand Holder — a thread-safe, lazy singleton
     * that relies entirely on class loading semantics.
     *
     * - The inner class Holder is not loaded until getInstance() is first called.
     * - JVM guarantees <clinit> is executed exactly once, with thread-safety.
     * - No synchronized keyword needed — class loading is inherently thread-safe.
     */
    public static class LazyHolder {
        private LazyHolder() {} // prevent instantiation

        private static class Holder {
            static final LazyHolder INSTANCE = new LazyHolder();
            static { System.out.println("  [Holder] loaded — singleton created"); }
        }

        public static LazyHolder getInstance() {
            return Holder.INSTANCE; // triggers Holder class loading on first call
        }
    }

    // -----------------------------------------------------------------------
    // Custom ClassLoader skeleton
    // -----------------------------------------------------------------------

    /**
     * Minimal custom ClassLoader demonstrating the override pattern.
     *
     * Rule: override findClass(), NOT loadClass().
     * loadClass() implements parent delegation; findClass() is called only if parent fails.
     *
     * Real-world uses:
     *   - Hot-reload (reload classes from disk without restarting JVM)
     *   - Plugin isolation (each plugin gets its own ClassLoader)
     *   - Bytecode transformation (ASM / ByteBuddy integration)
     *   - Encrypted class files (decrypt in findClass before defineClass)
     */
    static class MinimalCustomClassLoader extends ClassLoader {

        MinimalCustomClassLoader() {
            super(ClassLoader.getSystemClassLoader()); // explicit parent
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            // In a real implementation: read bytes from custom source,
            // then call: return defineClass(name, bytes, 0, bytes.length);
            System.out.println("  [CustomCL] findClass called for: " + name + " — delegating to parent");
            return super.findClass(name); // fall back to parent for this demo
        }
    }

    public static void main(String[] args) {
        System.out.println("=== ClassLoader Demo ===\n");

        bootstrapLoaderDemo();
        applicationLoaderDemo();

        System.out.println("\n=== ClassLoader Chain ===");
        printClassLoaderChain(ClassLoaderDemo.class);

        System.out.println("\n=== Dynamic Loading (Class.forName) ===");
        dynamicLoad("java.util.ArrayList");
        dynamicLoad("com.nonexistent.Ghost");

        System.out.println("\n=== Lazy Static Initialization ===");
        System.out.println("EagerInit class referenced — initializer should run now:");
        System.out.println("TOKEN = " + EagerInit.getToken());

        System.out.println("\n=== Initialization-on-Demand Holder (lazy singleton) ===");
        System.out.println("Calling getInstance() for the first time:");
        LazyHolder singleton = LazyHolder.getInstance();
        System.out.println("Got singleton: " + singleton);
        System.out.println("Same instance on second call: " + (LazyHolder.getInstance() == singleton));

        System.out.println("\n=== Custom ClassLoader ===");
        MinimalCustomClassLoader custom = new MinimalCustomClassLoader();
        System.out.println("Custom CL created: " + custom);
        System.out.println("Custom CL parent:  " + custom.getParent());
    }
}
