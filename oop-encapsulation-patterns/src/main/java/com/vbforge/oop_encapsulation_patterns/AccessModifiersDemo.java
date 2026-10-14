package com.vbforge.oop_encapsulation_patterns;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * AccessModifiersDemo — annotated reference for Java access control.
 *
 * <p>Read top-to-bottom. Each section covers one facet of access modifiers with
 * concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>The four levels — {@code private}, package-private, {@code protected}, {@code public}</li>
 *   <li>Access on classes — what a top-level class and a nested class may be</li>
 *   <li>{@code private} is per class, not per object</li>
 *   <li>{@code protected} — more than package-private, and widening in overrides</li>
 *   <li>A small public surface — expose one method, hide the steps</li>
 *   <li>Gotchas — interface members, private constructors, reflection</li>
 * </ol>
 */
public class AccessModifiersDemo {

    public static void main(String[] args) {
        fourLevelsDemo();
        System.out.println("---");
        classLevelDemo();
        System.out.println("---");
        privatePerClassDemo();
        System.out.println("---");
        protectedDemo();
        System.out.println("---");
        smallSurfaceDemo();
        System.out.println("---");
        gotchasDemo();
    }

    // ── Shared helper ─────────────────────────────────────────────────────────

    /**
     * Translates the bit-mask from {@code getModifiers()} into a readable access level.
     * Package-private has NO keyword — it is simply "none of the three".
     */
    static String levelOf(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return "public";
        }
        if (Modifier.isProtected(modifiers)) {
            return "protected";
        }
        if (Modifier.isPrivate(modifiers)) {
            return "private";
        }
        return "package-private";
    }

    // ── 1. The four levels ────────────────────────────────────────────────────

    /** One member of each access level, so reflection can show them side by side. */
    static class Visibility {
        private int secret = 1;
        int packageLevel = 2;                   // no keyword = package-private
        protected int shared = 3;
        public int open = 4;

        private void privateMethod() { }
        void packageMethod() { }
        protected void protectedMethod() { }
        public void publicMethod() { }
    }

    /**
     * Java has exactly four access levels, from most to least restrictive:
     *
     * <pre>
     *   modifier         same class   same package   subclass (other pkg)   everywhere
     *   private              ✅            ❌               ❌                  ❌
     *   (package-private)    ✅            ✅               ❌                  ❌
     *   protected            ✅            ✅               ✅                  ❌
     *   public               ✅            ✅               ✅                  ✅
     * </pre>
     *
     * <p>WHY this matters: access control is how a class decides which parts are its
     * <em>contract</em> (visible to others) and which are its <em>implementation</em> (free to
     * change tomorrow without breaking anybody). The narrower the access, the fewer callers you
     * can break by changing it.
     *
     * <p>Rule of thumb: start with {@code private} and widen only when a real caller needs it.
     *
     * <p>Common gotcha: package-private is the DEFAULT when you write no modifier. It is not
     * spelled {@code default} — that keyword means something else (interface default methods,
     * {@code switch}).
     */
    static void fourLevelsDemo() {
        System.out.println("=== The four levels ===");
        try {
            for (String name : new String[] {"secret", "packageLevel", "shared", "open"}) {
                Field field = Visibility.class.getDeclaredField(name);
                System.out.println("field  " + name + " : " + levelOf(field.getModifiers()));
            }
            for (String name : new String[] {"privateMethod", "packageMethod", "protectedMethod", "publicMethod"}) {
                Method method = Visibility.class.getDeclaredMethod(name);
                System.out.println("method " + name + " : " + levelOf(method.getModifiers()));
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    // ── 2. Access on classes ──────────────────────────────────────────────────

    private static class HiddenHelper { }
    static class PackageNested { }
    protected static class ProtectedNested { }
    public static class PublicNested { }

    static Class<?> hiddenHelperType() {
        return HiddenHelper.class;
    }

    /**
     * A TOP-LEVEL class can be only {@code public} or package-private. {@code private} and
     * {@code protected} do not make sense there: there is no enclosing class to hide it from or
     * to inherit from it. A NESTED class is a member of its outer class, so it can use all four
     * levels.
     *
     * <p>WHY this matters: a {@code private static class} is the cleanest way to keep a helper
     * type completely invisible — nobody outside the file can even name it.
     *
     * <p>Common gotcha: a {@code public} nested class inside a package-private outer class is
     * still only reachable by code that can see the outer class.
     */
    static void classLevelDemo() {
        System.out.println("=== Access on classes ===");
        System.out.println("top-level AccessModifiersDemo : " + levelOf(AccessModifiersDemo.class.getModifiers()));
        System.out.println("nested HiddenHelper           : " + levelOf(HiddenHelper.class.getModifiers()));
        System.out.println("nested PackageNested          : " + levelOf(PackageNested.class.getModifiers()));
        System.out.println("nested ProtectedNested        : " + levelOf(ProtectedNested.class.getModifiers()));
        System.out.println("nested PublicNested           : " + levelOf(PublicNested.class.getModifiers()));
    }

    // ── 3. private is per class ───────────────────────────────────────────────

    static class Meter {
        private int reading;

        Meter(int reading) {
            this.reading = reading;
        }

        /** {@code other.reading} is legal: the rule is "same CLASS", not "same object". */
        boolean sameReading(Meter other) {
            return this.reading == other.reading;
        }

        int reading() {
            return reading;
        }
    }

    /** Reads {@code meter.reading} directly — legal only because Meter is nested in THIS class. */
    static int directPeek(Meter meter) {
        return meter.reading;
    }

    /**
     * {@code private} means "visible inside the body of the class that declares it" — not "only
     * visible to {@code this} object". Two instances of the same class can read each other's
     * private fields, which is exactly what {@code equals}, {@code compareTo} and copy
     * constructors rely on.
     *
     * <p>WHY this matters: you never need a getter just so a class can compare two of its own
     * objects — keep the field private and read it directly.
     *
     * <p>Common gotcha: the rule is really "inside the same TOP-LEVEL class body", so an outer
     * class can read the private members of its nested classes ({@code directPeek} above) and
     * vice versa. A separate top-level class cannot.
     */
    static void privatePerClassDemo() {
        System.out.println("=== private is per class ===");
        Meter a = new Meter(10);
        Meter b = new Meter(10);
        Meter c = new Meter(99);
        System.out.println("a.sameReading(b) : " + a.sameReading(b));   // true  — reads b.reading
        System.out.println("a.sameReading(c) : " + a.sameReading(c));   // false
        System.out.println("directPeek(c)    : " + directPeek(c));      // 99 — nested-class privilege
    }

    // ── 4. protected ──────────────────────────────────────────────────────────

    static class Base {
        protected String id() {
            return "base";
        }

        String label() {
            return "[" + id() + "]";
        }
    }

    static class Derived extends Base {
        @Override
        public String id() {                // protected → public: widening is allowed
            return "derived";
        }
    }

    /**
     * {@code protected} = package-private PLUS subclasses in other packages. So it is never
     * stricter than package-private — it is a widening of it.
     *
     * <p>An override may keep or WIDEN the access of the method it overrides, never narrow it:
     * {@code protected → public} compiles, {@code protected → private} does not. That keeps the
     * substitution rule intact — anything you could call on a {@code Base}, you can call on a
     * {@code Derived}.
     *
     * <p>WHY this matters: {@code protected} is part of the class's public contract for
     * subclass authors. Every protected member is something you can never freely change again,
     * so prefer {@code private} plus a small protected hook method.
     *
     * <p>Common gotcha: code in the SAME package can call a protected method on any instance,
     * subclass or not ({@code new Base().id()} below).
     */
    static void protectedDemo() {
        System.out.println("=== protected ===");
        Base base = new Base();
        Base derived = new Derived();
        System.out.println("same-package call   : " + base.id());       // base
        System.out.println("override widened    : " + derived.id());    // derived
        System.out.println("label() uses id()   : " + derived.label()); // [derived]
    }

    // ── 5. A small public surface ─────────────────────────────────────────────

    /** Public API: one method. Everything else is an implementation detail. */
    static class Slugger {
        public String slug(String title) {
            return dashes(normalize(title));
        }

        private String normalize(String text) {
            return text.trim().toLowerCase(Locale.ROOT);
        }

        private String dashes(String text) {
            return text.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        }
    }

    static List<String> publicMethodNames(Class<?> type) {
        List<String> names = new ArrayList<>();
        for (Method method : type.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && !method.isSynthetic()) {
                names.add(method.getName());
            }
        }
        Collections.sort(names);
        return names;
    }

    /**
     * Expose the smallest API that does the job. {@code Slugger} has two helper steps, but
     * callers only ever see {@code slug(...)}.
     *
     * <p>WHY this matters: every public method is a promise. If {@code normalize} were public,
     * somebody would call it, and you could no longer rename it, merge it or change what it
     * returns. A small surface keeps the class free to improve inside.
     *
     * <p>Common gotcha: making a method public "just for the test" widens the contract for
     * everyone. Test through the public method, or leave the helper package-private so tests in
     * the same package can reach it.
     */
    static void smallSurfaceDemo() {
        System.out.println("=== A small public surface ===");
        Slugger slugger = new Slugger();
        System.out.println("slug(\"  Hello, World!  \") : " + slugger.slug("  Hello, World!  "));   // hello-world
        System.out.println("public API of Slugger     : " + publicMethodNames(Slugger.class));      // [slug]
    }

    // ── 6. Gotchas ────────────────────────────────────────────────────────────

    interface Greeter {
        String hi();                         // implicitly public abstract
    }

    static final class MathUtil {
        private MathUtil() { }               // no instances: the class is only a namespace

        static int twice(int x) {
            return 2 * x;
        }
    }

    /** Reads a private field with reflection — proof that access modifiers are not security. */
    static int peekWithReflection(Meter meter) {
        try {
            Field field = Meter.class.getDeclaredField("reading");
            field.setAccessible(true);
            return field.getInt(meter);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Three things that surprise people:
     *
     * <ul>
     *   <li>Interface members are implicitly {@code public}. A class implementing
     *       {@code Greeter} must write {@code public String hi()} — package-private would be a
     *       compile error (weaker access).</li>
     *   <li>A {@code private} constructor is how you forbid {@code new}: utility classes
     *       ({@code MathUtil}), singletons and classes that only expose static factories.</li>
     *   <li>Reflection can bypass {@code private} ({@code setAccessible(true)}). Access
     *       modifiers are a design tool that the compiler enforces, not a security boundary — never
     *       store secrets in a private field and call it protected.</li>
     * </ul>
     */
    static void gotchasDemo() {
        System.out.println("=== Gotchas ===");
        try {
            Method hi = Greeter.class.getMethod("hi");
            System.out.println("interface method hi()      : " + levelOf(hi.getModifiers()));         // public
            System.out.println("MathUtil constructor       : "
                    + levelOf(MathUtil.class.getDeclaredConstructors()[0].getModifiers()));          // private
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        System.out.println("MathUtil.twice(21)         : " + MathUtil.twice(21));                   // 42
        System.out.println("private read by reflection : " + peekWithReflection(new Meter(7)));     // 7
    }
}
