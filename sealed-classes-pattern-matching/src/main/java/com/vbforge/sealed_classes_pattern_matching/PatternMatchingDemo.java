package com.vbforge.sealed_classes_pattern_matching;

/**
 * Pattern matching for {@code instanceof} (Java 16+) and for {@code switch} (Java 21).
 *
 * <p>A <b>type pattern</b> tests a type AND declares a typed variable in one step,
 * replacing the "check, then cast" idiom.
 *
 * <p>Sections:
 * <ol>
 *   <li>{@code instanceof} patterns and binding scope</li>
 *   <li>{@code switch} with type patterns</li>
 *   <li>Guards with {@code when} and case ordering</li>
 *   <li>{@code null} handling in {@code switch}</li>
 * </ol>
 *
 * <p>Complexity: not relevant — these are syntax features; a pattern costs the same
 * as the instanceof + cast it replaces.
 */
public class PatternMatchingDemo {

    // ── 1. instanceof patterns ─────────────────────────────────────────────────
    /**
     * What: {@code obj instanceof String s} — if true, {@code s} is a ready-to-use String.
     *
     * <p>Why: it removes the redundant cast, which was a classic source of
     * ClassCastException when the check and the cast drifted apart.
     *
     * <p>Gotcha: the binding is only in scope where the compiler can prove the match
     * succeeded — inside the {@code if} body, to the right of {@code &&}, or after
     * a negated check that exits early.
     */
    public static String describeWithInstanceof(Object obj) {
        if (obj instanceof String s) {
            return "String of length " + s.length();
        }
        if (obj instanceof Integer i && i > 100) {   // binding usable right of &&
            return "big Integer " + i;
        }
        if (obj instanceof Integer i) {
            return "Integer " + i;
        }
        return "other";
    }

    /** Negated check + early return: the binding is in scope AFTER the if. */
    public static int lengthOrMinusOne(Object obj) {
        if (!(obj instanceof String s)) {
            return -1;
        }
        return s.length();
    }

    /** The classic real-world use: a compact, null-safe {@code equals}. */
    public static final class Version {
        private final int major;
        private final int minor;

        public Version(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }

        @Override
        public boolean equals(Object obj) {
            // instanceof is false for null, so no separate null check is needed
            return obj instanceof Version v && major == v.major && minor == v.minor;
        }

        @Override
        public int hashCode() {
            return 31 * major + minor;
        }

        @Override
        public String toString() {
            return major + "." + minor;
        }
    }

    public static void instanceofDemo() {
        System.out.println(describeWithInstanceof("hello"));
        System.out.println(describeWithInstanceof(500));
        System.out.println(lengthOrMinusOne(42));
        System.out.println(new Version(1, 2).equals(new Version(1, 2)));
    }


    // ── 2. switch with type patterns ──────────────────────────────────────────
    /**
     * What: {@code case Type name ->} branches on the runtime type of the selector.
     *
     * <p>Why: replaces long {@code if / else if (x instanceof ...)} chains with one
     * expression that returns a value.
     *
     * <p>Gotcha: a pattern switch over {@code Object} must be exhaustive, so it needs a
     * {@code default} (only sealed hierarchies can be exhaustive without one).
     */
    public static String typeSwitch(Object obj) {
        return switch (obj) {
            case Integer i -> "Integer " + i;
            case Long l -> "Long " + l;
            case String s -> "String '" + s + "'";
            case int[] arr -> "int[] of " + arr.length;
            default -> "other " + obj.getClass().getSimpleName();
        };
    }

    public static void typeSwitchDemo() {
        System.out.println(typeSwitch(7));
        System.out.println(typeSwitch(7L));
        System.out.println(typeSwitch(new int[]{1, 2}));
        System.out.println(typeSwitch(2.5));
    }


    // ── 3. Guards and case ordering ───────────────────────────────────────────
    /**
     * What: {@code case Type t when condition ->} adds a boolean guard to a pattern.
     *
     * <p>Why: you can split one type into several cases without nested ifs.
     *
     * <p>Gotcha: cases are tested top to bottom, and the compiler rejects a case that
     * can never be reached. Put guarded / specific cases BEFORE the general one:
     * <pre>
     *   case Integer i          -> ...
     *   case Integer i when i<0 -> ...   // ❌ does not compile: dominated by the case above
     * </pre>
     */
    public static String classify(Object obj) {
        return switch (obj) {
            case Integer i when i < 0 -> "negative";
            case Integer i when i == 0 -> "zero";
            case Integer i -> "positive";
            case String s when s.isBlank() -> "blank text";
            case String s -> "text";
            default -> "unsupported";
        };
    }

    public static void guardsDemo() {
        System.out.println(classify(-5));
        System.out.println(classify(0));
        System.out.println(classify(9));
        System.out.println(classify("  "));
        System.out.println(classify("hi"));
    }


    // ── 4. null handling ──────────────────────────────────────────────────────
    /**
     * What: a {@code switch} on {@code null} throws {@link NullPointerException} — unless
     * there is a {@code case null}.
     *
     * <p>Why: {@code case null} lets you handle "no value" in the same expression
     * instead of guarding it before the switch.
     *
     * <p>Gotcha: {@code default} does NOT match null. Use {@code case null, default}
     * to cover both.
     */
    public static String withoutNullCase(Object obj) {
        return switch (obj) {
            case String s -> "string";
            default -> "not a string";
        };
    }

    public static String withNullCase(Object obj) {
        return switch (obj) {
            case null -> "null!";
            case String s -> "string";
            default -> "not a string";
        };
    }

    public static String nullAndDefaultTogether(Object obj) {
        return switch (obj) {
            case String s -> "string";
            case null, default -> "null or other";
        };
    }

    public static void nullHandlingDemo() {
        System.out.println(withNullCase(null));
        System.out.println(nullAndDefaultTogether(null));
        try {
            withoutNullCase(null);
        } catch (NullPointerException e) {
            System.out.println("no case null -> NullPointerException");
        }
    }


    public static void main(String[] args) {
        instanceofDemo();
        System.out.println("---");
        typeSwitchDemo();
        System.out.println("---");
        guardsDemo();
        System.out.println("---");
        nullHandlingDemo();
    }
}
