package com.vbforge.design_patterns.singleton_pattern;

/**
 * SingletonDemo — five ways to guarantee exactly one instance.
 *
 * <p>Read top-to-bottom. Each section shows a variation with the trade-off
 * that makes it worth knowing. Prefer the enum version in real code.
 *
 * <p>Topics:
 * <ol>
 *   <li>Eager — instance created at class-load time</li>
 *   <li>Lazy (unsafe) — created on first use, races under concurrency</li>
 *   <li>Double-checked locking — lazy + thread-safe, needs {@code volatile}</li>
 *   <li>Holder idiom — lazy + thread-safe, no synchronisation</li>
 *   <li>Enum — the safest singleton, serialisation/reflection-proof</li>
 * </ol>
 */
public class SingletonDemo {

    public static void main(String[] args) {
        eagerDemo();
        System.out.println("---");
        lazyUnsafeDemo();
        System.out.println("---");
        doubleCheckedDemo();
        System.out.println("---");
        holderDemo();
        System.out.println("---");
        enumDemo();
    }

    /**
     * Eager initialisation: instance built when the class is loaded.
     *
     * <p><b>Why:</b> simplest, no lock, guaranteed thread-safe by the
     * JVM's class-initialisation contract. If construction is expensive
     * and the singleton may never be used, this wastes resources.
     *
     * <p><b>Gotcha:</b> if the constructor throws, the class fails to load
     * with {@code ExceptionInInitializerError}.
     */
    static void eagerDemo() {
        System.out.println("=== Eager ===");
        Eager a = Eager.getInstance();
        Eager b = Eager.getInstance();
        System.out.println("same instance: " + (a == b));
        System.out.println("value        : " + a.value());
    }

    static class Eager {
        private static final Eager INSTANCE = new Eager();
        private final int value = 42;
        private Eager() {}
        static Eager getInstance() { return INSTANCE; }
        int value() { return value; }
    }

    /**
     * Lazy init — the instance is only created on first call.
     *
     * <p><b>Why:</b> no waste if the singleton is never touched.
     *
     * <p><b>Gotcha:</b> this is <em>not</em> thread-safe. Two threads can
     * both observe {@code instance == null} and construct two objects.
     * Demonstrating the race reliably needs a thread test — we only show
     * the happy path here.
     */
    static void lazyUnsafeDemo() {
        System.out.println("=== Lazy (unsafe) ===");
        Lazy a = Lazy.getInstance();
        Lazy b = Lazy.getInstance();
        System.out.println("same instance: " + (a == b));
    }

    static class Lazy {
        private static Lazy instance;
        private Lazy() {}
        static Lazy getInstance() {
            if (instance == null) instance = new Lazy();  // race
            return instance;
        }
    }

    /**
     * Double-checked locking — lazy + thread-safe, but only with {@code volatile}.
     *
     * <p><b>Why:</b> avoids the lock on the happy path (post-construction).
     *
     * <p><b>Gotcha:</b> without {@code volatile}, the write to {@code instance}
     * can be seen <em>before</em> the constructor finishes — another thread
     * then reads a half-constructed object.
     */
    static void doubleCheckedDemo() {
        System.out.println("=== Double-checked locking ===");
        Dcl a = Dcl.getInstance();
        Dcl b = Dcl.getInstance();
        System.out.println("same instance: " + (a == b));
    }

    static class Dcl {
        private static volatile Dcl instance;
        private Dcl() {}
        static Dcl getInstance() {
            if (instance == null) {
                synchronized (Dcl.class) {
                    if (instance == null) instance = new Dcl();
                }
            }
            return instance;
        }
    }

    /**
     * Holder idiom — lazy and thread-safe with no synchronisation.
     *
     * <p><b>Why:</b> relies on the JVM's guarantee that a class is
     * initialised exactly once, on first access, under a monitor that
     * also establishes happens-before for the static final field.
     *
     * <p><b>Gotcha:</b> the holder class is initialised only when
     * {@code getInstance()} is called — perfect laziness.
     */
    static void holderDemo() {
        System.out.println("=== Holder idiom ===");
        Holder h1 = Holder.getInstance();
        Holder h2 = Holder.getInstance();
        System.out.println("same instance: " + (h1 == h2));
    }

    static class Holder {
        private Holder() {}
        private static class Instance {
            static final Holder INSTANCE = new Holder();
        }
        static Holder getInstance() { return Instance.INSTANCE; }
    }

    /**
     * Enum singleton — recommended by Effective Java.
     *
     * <p><b>Why:</b> the JVM guarantees exactly one instance per enum
     * constant. It is serialisation-safe by default (no readResolve needed)
     * and resistant to reflection-based attacks ({@code Constructor.newInstance}
     * throws for enums).
     *
     * <p><b>Gotcha:</b> you cannot extend an enum — the singleton type is fixed.
     */
    static void enumDemo() {
        System.out.println("=== Enum singleton ===");
        EnumS a = EnumS.INSTANCE;
        EnumS b = EnumS.INSTANCE;
        System.out.println("same instance: " + (a == b));
        System.out.println("value        : " + a.value());
    }

    enum EnumS {
        INSTANCE;
        private final int value = 7;
        int value() { return value; }
    }
}