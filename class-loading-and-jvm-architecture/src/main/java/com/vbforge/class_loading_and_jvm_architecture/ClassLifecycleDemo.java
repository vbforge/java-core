package com.vbforge.class_loading_and_jvm_architecture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class lifecycle: load → link (verify, prepare, resolve) → initialise.
 *
 * <p>Sections:
 * <ol>
 *   <li>What triggers initialisation (and what does not)</li>
 *   <li>Preparation (default values) vs initialisation (assigned values)</li>
 *   <li>A failing static initialiser</li>
 *   <li>Class-file checks during loading</li>
 * </ol>
 *
 * <p>Class initialisation happens once per loader, so the "Iso*" classes below are always
 * used through a fresh {@link ClassLoaderDemo.IsolatingClassLoader}. That gives every scenario
 * its own un-initialised copy and keeps the results repeatable.
 */
public class ClassLifecycleDemo {

    /** Binary-name prefix of every class that is re-defined per scenario. */
    private static final String ISO_PREFIX = ClassLifecycleDemo.class.getName() + "$Iso";

    // =========================================================================
    // 1. What triggers initialisation
    // =========================================================================

    /*
     * WHAT: The JVM works in three phases per class:
     *   1. Loading        - find the bytes, create the Class object.
     *   2. Linking        - verification (is the bytecode safe?), preparation (static fields get
     *                       DEFAULT values: 0 / null / false), resolution (symbolic refs → direct refs).
     *   3. Initialisation - run static initialisers and static field assignments, in text order.
     *
     * WHY:  Initialisation is lazy. Static blocks run at the first *active use*, not when the class
     *       is loaded, so their side effects can appear "late" or never.
     *
     * Initialises the class:      new, static method call, read/write of a NON-constant static field,
     *                             Class.forName(name) / forName(name, true, loader).
     * Does NOT initialise it:     Class.forName(name, false, loader), a class literal (Foo.class),
     *                             creating an array of it, reading a compile-time constant
     *                             (static final primitive/String with a constant initialiser - inlined by javac).
     * Superclass first:           initialising a subclass initialises its superclass beforehand.
     * Field via subclass:         reading a static field through the subclass only initialises the class
     *                             that DECLARES the field.
     *
     * GOTCHA: "Parent init, then child init, then constructors" - see instantiateChild().
     */

    /** Records events from static initialisers; deliberately NOT an "Iso" class, so it is shared. */
    public static class InitLog {
        private static final List<String> EVENTS = new ArrayList<>();

        public static synchronized void add(String event) {
            EVENTS.add(event);
        }

        public static synchronized void clear() {
            EVENTS.clear();
        }

        public static synchronized List<String> snapshot() {
            return new ArrayList<>(EVENTS);
        }
    }

    public static class IsoParent {
        public static int sharedCounter = 1;

        static {
            InitLog.add("IsoParent static init");
        }

        public IsoParent() {
            InitLog.add("IsoParent constructor");
        }
    }

    public static class IsoChild extends IsoParent {
        static {
            InitLog.add("IsoChild static init");
        }

        public IsoChild() {
            super();
            InitLog.add("IsoChild constructor");
        }
    }

    public static class IsoHolder {
        /** Compile-time constant: javac copies the value into callers. */
        public static final int COMPILE_TIME_CONSTANT = 42;
        /** NOT a constant: needs code to run, so reading it requires initialisation. */
        public static final int RUNTIME_VALUE = Integer.parseInt("43");

        static {
            InitLog.add("IsoHolder static init");
        }
    }

    /** Each method touches the classes above in one specific way. Called reflectively. */
    public static class IsoReader {
        public static int readCompileTimeConstant() {
            return IsoHolder.COMPILE_TIME_CONSTANT;
        }

        public static int readRuntimeStaticField() {
            return IsoHolder.RUNTIME_VALUE;
        }

        public static int createArrayOfChild() {
            return new IsoChild[3].length;
        }

        public static Class<?> classLiteral() {
            return IsoChild.class;
        }

        public static int readParentFieldViaChild() {
            return IsoChild.sharedCounter;
        }
    }

    private static ClassLoader newIsolatedLoader() {
        return new ClassLoaderDemo.IsolatingClassLoader(ClassLifecycleDemo.class.getClassLoader(), ISO_PREFIX);
    }

    private static List<String> runReader(String readerMethod) {
        InitLog.clear();
        try {
            Class<?> reader = Class.forName(ISO_PREFIX + "Reader", true, newIsolatedLoader());
            reader.getMethod(readerMethod).invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return InitLog.snapshot();
    }

    /** Class.forName(name, false, loader) loads and links lazily: no static init runs. */
    public static List<String> loadWithoutInitializing() {
        InitLog.clear();
        try {
            Class.forName(ISO_PREFIX + "Child", false, newIsolatedLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
        return InitLog.snapshot();
    }

    /** {@code new IsoChild()}: parent static init, child static init, parent ctor, child ctor. */
    public static List<String> instantiateChild() {
        InitLog.clear();
        try {
            Class<?> child = Class.forName(ISO_PREFIX + "Child", false, newIsolatedLoader());
            child.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return InitLog.snapshot();
    }

    public static List<String> readCompileTimeConstant() {
        return runReader("readCompileTimeConstant");
    }

    public static List<String> readRuntimeStaticField() {
        return runReader("readRuntimeStaticField");
    }

    public static List<String> createArrayOfChild() {
        return runReader("createArrayOfChild");
    }

    public static List<String> classLiteral() {
        return runReader("classLiteral");
    }

    public static List<String> readParentFieldViaChild() {
        return runReader("readParentFieldViaChild");
    }

    public static void initializationTriggersDemo() {
        System.out.println("load only            : " + loadWithoutInitializing());
        System.out.println("new IsoChild()       : " + instantiateChild());
        System.out.println("compile-time constant: " + readCompileTimeConstant());
        System.out.println("runtime static field : " + readRuntimeStaticField());
        System.out.println("array of child       : " + createArrayOfChild());
        System.out.println("class literal        : " + classLiteral());
        System.out.println("parent field via child: " + readParentFieldViaChild());
    }

    // =========================================================================
    // 2. Preparation vs initialisation
    // =========================================================================

    /*
     * WHAT: During preparation every static field gets its type's default (0, null, false).
     *       Initialisation then assigns real values in the order the code is written.
     *
     * WHY:  Explains forward-reference surprises: code that runs during initialisation can
     *       observe a field that has been prepared but not yet assigned.
     *
     * GOTCHA: first = peek() reads `second` before `second = 5` has run, so first is 0.
     */

    public static class PreparationProbe {
        public static int first = peek();
        public static int second = 5;

        private static int peek() {
            return second;
        }
    }

    /** {first, second} → {0, 5}. */
    public static int[] preparationVersusInitialization() {
        return new int[] {PreparationProbe.first, PreparationProbe.second};
    }

    // =========================================================================
    // 3. A failing static initialiser
    // =========================================================================

    /*
     * WHAT: If a static initialiser throws, the first use fails with ExceptionInInitializerError
     *       (the original exception is its cause). The class is then marked unusable:
     *       every later use fails with NoClassDefFoundError.
     *
     * WHY:  The second error has no stack trace pointing at the real problem, so
     *       always look for the FIRST ExceptionInInitializerError in the logs.
     */

    public static class IsoBroken {
        public static int value = Integer.parseInt("not-a-number");
    }

    /** {firstAttempt, secondAttempt}: the errors raised when forcing initialisation twice. */
    public static Throwable[] initializeBrokenClassTwice() {
        ClassLoader loader = newIsolatedLoader();
        return new Throwable[] {attemptInitialization(loader), attemptInitialization(loader)};
    }

    private static Throwable attemptInitialization(ClassLoader loader) {
        try {
            Class.forName(ISO_PREFIX + "Broken", true, loader);
            return null;
        } catch (LinkageError | ClassNotFoundException e) {
            return e;
        }
    }

    // =========================================================================
    // 4. Class-file checks during loading
    // =========================================================================

    /*
     * WHAT: defineClass() validates the bytes before it creates a Class:
     *       a wrong magic number → ClassFormatError;
     *       a class-file version newer than the JVM supports → UnsupportedClassVersionError
     *       (a subclass of ClassFormatError).
     *
     * WHY:  These are the errors you see when running code compiled for a newer Java on an older JVM.
     */

    /** Loader whose only job is to expose the protected defineClass. */
    public static class BytesClassLoader extends ClassLoader {
        public BytesClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    public static byte[] greeterClassFile() throws IOException {
        return ClassLoaderDemo.readClassFile(ClassLoaderDemo.Greeter.class);
    }

    /** Copy of {@code original} whose first byte (part of 0xCAFEBABE) is zeroed. */
    public static byte[] withBrokenMagic(byte[] original) {
        byte[] copy = original.clone();
        copy[0] = 0;
        return copy;
    }

    /** Copy of {@code original} with the major-version field (bytes 6-7) set to {@code major}. */
    public static byte[] withMajorVersion(byte[] original, int major) {
        byte[] copy = original.clone();
        copy[6] = (byte) (major >> 8);
        copy[7] = (byte) major;
        return copy;
    }

    /** Defines {@link ClassLoaderDemo.Greeter} from {@code bytes} in a throwaway loader. */
    public static Class<?> defineGreeterFrom(byte[] bytes) {
        return new BytesClassLoader(ClassLifecycleDemo.class.getClassLoader())
                .define(ClassLoaderDemo.Greeter.class.getName(), bytes);
    }

    public static void classFileChecksDemo() throws IOException {
        byte[] good = greeterClassFile();
        System.out.println("good bytes define a class: " + defineGreeterFrom(good).getName());
        try {
            defineGreeterFrom(withBrokenMagic(good));
        } catch (ClassFormatError e) {
            System.out.println("broken magic   -> " + e.getClass().getSimpleName());
        }
        try {
            defineGreeterFrom(withMajorVersion(good, 99));
        } catch (ClassFormatError e) {
            System.out.println("version 99     -> " + e.getClass().getSimpleName());
        }
    }

    public static void main(String[] args) throws IOException {
        initializationTriggersDemo();
        System.out.println("---");
        int[] values = preparationVersusInitialization();
        System.out.println("first=" + values[0] + ", second=" + values[1]);
        System.out.println("---");
        Throwable[] errors = initializeBrokenClassTwice();
        System.out.println("1st: " + errors[0] + " / 2nd: " + errors[1]);
        System.out.println("---");
        classFileChecksDemo();
    }
}
