package com.vbforge.class_loading_and_jvm_architecture;

/**
 * Practice tasks for the class lifecycle. Read {@link ClassLifecycleDemo} first.
 */
public class ClassLifecycleTasks {

    /**
     * Load a class by name through the given loader WITHOUT running its static initialisers.
     *
     * <p>Examples:
     * <pre>
     * loadWithoutInitializing("java.lang.String", null)   → String.class
     * loadWithoutInitializing("com.acme.HasStaticBlock", loader)
     *                                                     → the Class; the static block has NOT run
     * loadWithoutInitializing("no.such.Type", loader)     → throws ClassNotFoundException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Class.forName(String)} initialises; look for the overload with three parameters.</li>
     *   <li>A {@code null} loader means the bootstrap loader.</li>
     * </ul>
     *
     * @param className fully-qualified binary name
     * @param loader    loader to use, {@code null} for bootstrap
     * @return the loaded, uninitialised class
     * @throws ClassNotFoundException if the class cannot be found
     */
    public Class<?> loadWithoutInitializing(String className, ClassLoader loader) throws ClassNotFoundException {
        throw new UnsupportedOperationException("TODO: implement loadWithoutInitializing");
    }

    /**
     * Force initialisation of a class and report the error its static initialiser caused.
     * Return {@code null} when initialisation succeeds.
     *
     * <p>Examples:
     * <pre>
     * captureInitializationFailure("java.lang.Integer", null)      → null
     * captureInitializationFailure("com.acme.BadStatic", loader)   → ExceptionInInitializerError
     *                                                                (cause: the original exception)
     * captureInitializationFailure("no.such.Type", loader)         → throws ClassNotFoundException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You need the overload of {@code Class.forName} that lets you ask for initialisation.</li>
     *   <li>ExceptionInInitializerError and NoClassDefFoundError share a common superclass.</li>
     * </ul>
     *
     * @param className fully-qualified binary name
     * @param loader    loader to use, {@code null} for bootstrap
     * @return the {@link LinkageError} raised during initialisation, or {@code null}
     * @throws ClassNotFoundException if the class cannot be found
     */
    public LinkageError captureInitializationFailure(String className, ClassLoader loader)
            throws ClassNotFoundException {
        throw new UnsupportedOperationException("TODO: implement captureInitializationFailure");
    }

    /**
     * Define a class from raw class-file bytes inside a fresh loader whose parent is {@code parent}.
     * Each call must create its own loader, so two calls give two distinct Class objects.
     * Invalid bytes must surface as the JVM's {@link ClassFormatError} (do not catch it).
     *
     * <p>Examples:
     * <pre>
     * defineFromBytes(parent, "com.acme.Foo", fooBytes)   → a Class named "com.acme.Foo",
     *                                                       != Foo.class loaded elsewhere
     * defineFromBytes(parent, "com.acme.Foo", badBytes)   → throws ClassFormatError
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ClassLoader.defineClass} is protected: you need a subclass.</li>
     *   <li>An anonymous subclass can be enough.</li>
     * </ul>
     *
     * @param parent     parent of the new loader
     * @param className  binary name stored in the bytes
     * @param classBytes content of a .class file
     * @return the defined class
     */
    public Class<?> defineFromBytes(ClassLoader parent, String className, byte[] classBytes) {
        throw new UnsupportedOperationException("TODO: implement defineFromBytes");
    }
}
