package com.vbforge.equals_hashcode_and_object_contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * ToStringDemo — annotated reference for {@code Object.toString()}.
 *
 * <p>Read top-to-bottom. Each section covers one facet of {@code toString} with
 * concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>The default — {@code ClassName@hexHash}</li>
 *   <li>Overriding — a readable, stable format; implicit calls</li>
 *   <li>Null-safety — {@code String.valueOf}, {@code Objects.toString}</li>
 *   <li>Cycles — a {@code toString} that calls itself forever</li>
 *   <li>Sensitive data — what must never appear in a log line</li>
 *   <li>Arrays, collections and records</li>
 * </ol>
 */
public class ToStringDemo {

    public static void main(String[] args) {
        defaultFormatDemo();
        System.out.println("---");
        overrideDemo();
        System.out.println("---");
        nullSafetyDemo();
        System.out.println("---");
        cyclesDemo();
        System.out.println("---");
        sensitiveDataDemo();
        System.out.println("---");
        arraysCollectionsRecordsDemo();
    }

    // ── 1. The default ────────────────────────────────────────────────────────

    static class NoToString { }

    /** Re-implements what {@code Object.toString()} does. */
    static String defaultFormat(Object o) {
        return o.getClass().getName() + "@" + Integer.toHexString(o.hashCode());
    }

    /**
     * Without an override, {@code toString()} returns {@code getClass().getName() + "@" +
     * Integer.toHexString(hashCode())} — for example {@code ...ToStringDemo$NoToString@1b6d3586}.
     * It tells you the TYPE and nothing about the STATE.
     *
     * <p>WHY this matters: every log line, exception message, debugger view and
     * {@code assertEquals} failure message that prints your object shows this useless text.
     * Note that it uses the virtual {@code hashCode()} — an override of {@code hashCode} changes it.
     */
    static void defaultFormatDemo() {
        System.out.println("=== The default ===");
        NoToString plain = new NoToString();
        System.out.println("toString()          : " + plain);
        System.out.println("rebuilt by hand     : " + defaultFormat(plain));
    }

    // ── 2. Overriding ─────────────────────────────────────────────────────────

    static final class Point {
        private final int x;
        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point(x=" + x + ", y=" + y + ")";
        }
    }

    /** Four implicit routes to {@code toString()}; all give the same text. */
    static String[] implicitConversions(Point p) {
        return new String[] {
                "" + p,
                String.valueOf(p),
                String.format(Locale.ROOT, "%s", p),
                new StringBuilder().append(p).toString()
        };
    }

    /**
     * A good {@code toString} lists the class name and every field a developer needs to see, in a
     * stable, readable format: {@code Point(x=1, y=2)}. You almost never call it explicitly — string
     * concatenation, {@code String.valueOf}, {@code %s}, {@code StringBuilder.append},
     * {@code System.out.println} and every logger do it for you.
     *
     * <p>WHY this matters: {@code toString} is a debugging and logging contract. Do not parse it,
     * do not use it for business logic or equality, and do not promise its exact format in public
     * API unless you document it ({@code Integer.toString} is documented; most others are not).
     *
     * <p>Common gotcha: {@code toString} must not throw and should not have side effects — it runs
     * inside debuggers and exception handlers, where a second exception hides the first.
     */
    static void overrideDemo() {
        System.out.println("=== Overriding ===");
        Point p = new Point(1, 2);
        for (String s : implicitConversions(p)) {
            System.out.println("implicit conversion : " + s);
        }
    }

    // ── 3. Null-safety ────────────────────────────────────────────────────────

    /** Returns {@code {"" + null, String.valueOf(null object), Objects.toString(null, "n/a"), Objects.toString(null)}}. */
    static String[] nullHandling() {
        Object nothing = null;
        return new String[] {
                "" + nothing,
                String.valueOf(nothing),
                Objects.toString(nothing, "n/a"),
                Objects.toString(nothing)
        };
    }

    /**
     * Calling {@code obj.toString()} on a {@code null} reference throws a
     * {@code NullPointerException}. String concatenation, {@code String.valueOf(Object)} and
     * {@code Objects.toString(o)} turn {@code null} into the text {@code "null"} instead, and
     * {@code Objects.toString(o, default)} lets you pick the replacement.
     *
     * <p>WHY this matters: inside your own {@code toString}, a field may be {@code null}. Building
     * the text with {@code +} or {@code String.valueOf} survives it; {@code name.toString()} does not.
     *
     * <p>Common gotcha: {@code String.valueOf((char[]) null)} picks the {@code char[]} overload
     * and throws — pass a variable typed as {@code Object}.
     */
    static void nullSafetyDemo() {
        System.out.println("=== Null-safety ===");
        String[] r = nullHandling();
        System.out.println("\"\" + null                        : " + r[0]);
        System.out.println("String.valueOf(null object)      : " + r[1]);
        System.out.println("Objects.toString(null, \"n/a\")    : " + r[2]);
        System.out.println("Objects.toString(null)           : " + r[3]);
    }

    // ── 4. Cycles ─────────────────────────────────────────────────────────────

    /** BROKEN on purpose: a parent prints its children, each child prints its parent… */
    static final class BadParent {
        private final String name;
        private final List<BadChild> children = new ArrayList<>();

        BadParent(String name) {
            this.name = name;
        }

        void add(BadChild child) {
            children.add(child);
        }

        @Override
        public String toString() {
            return "Parent(" + name + ", children=" + children + ")";
        }
    }

    static final class BadChild {
        private final BadParent parent;

        BadChild(BadParent parent) {
            this.parent = parent;
            parent.add(this);
        }

        @Override
        public String toString() {
            return "Child(" + parent + ")";                 // → BadParent.toString → BadChild.toString → …
        }
    }

    static final class Dept {
        private final String name;
        private final List<Emp> staff = new ArrayList<>();

        Dept(String name) {
            this.name = name;
        }

        void add(Emp emp) {
            staff.add(emp);
        }

        @Override
        public String toString() {
            return "Dept(" + name + ", staff=" + staff + ")";
        }
    }

    static final class Emp {
        private final String name;
        private final Dept dept;

        Emp(String name, Dept dept) {
            this.name = name;
            this.dept = dept;
            dept.add(this);
        }

        @Override
        public String toString() {
            return "Emp(" + name + ", dept=" + dept.name + ")";    // the dept's NAME only — breaks the cycle
        }
    }

    static boolean badCycleOverflows() {
        BadParent parent = new BadParent("p");
        new BadChild(parent);
        try {
            parent.toString();
            return false;
        } catch (StackOverflowError e) {
            return true;
        }
    }

    static String safeCycle() {
        Dept dept = new Dept("Sales");
        new Emp("Ann", dept);
        return dept.toString();
    }

    /**
     * Two objects that point at each other make a naive {@code toString} recurse until the stack is
     * exhausted ({@code StackOverflowError}). The same trap hides in IDE-generated {@code toString},
     * Lombok's {@code @ToString}, JPA bidirectional relationships and {@code hashCode}/{@code equals}.
     *
     * <p>Fix: print the OTHER side by a short identifier (its name or id), never by its own
     * {@code toString}.
     *
     * <p>WHY this matters: the error appears only when the object graph is cyclic and the text is
     * requested — often inside a logger, far from the cause.
     */
    static void cyclesDemo() {
        System.out.println("=== Cycles ===");
        System.out.println("naive version overflows the stack : " + badCycleOverflows());   // true
        System.out.println("safe version                      : " + safeCycle());            // Dept(Sales, staff=[Emp(Ann, dept=Sales)])
    }

    // ── 5. Sensitive data ─────────────────────────────────────────────────────

    static final class User {
        private final String name;
        private final String password;

        User(String name, String password) {
            this.name = name;
            this.password = password;
        }

        @Override
        public String toString() {
            return "User(name=" + name + ")";               // the password is deliberately left out
        }
    }

    /** Shows only the last four characters; anything of four characters or fewer is fully hidden. */
    static String mask(String secret) {
        if (secret == null || secret.length() <= 4) {
            return "****";
        }
        return "****" + secret.substring(secret.length() - 4);
    }

    /**
     * {@code toString} ends up in log files, error trackers, screenshots and bug reports. Passwords,
     * tokens, API keys, full card numbers and personal data must NOT appear in it — leave the field
     * out, or print a masked form ({@code ****1234}).
     *
     * <p>WHY this matters: "we log the user object for debugging" is how credentials leak into
     * log aggregators that many people can read. Treat {@code toString} as public output.
     */
    static void sensitiveDataDemo() {
        System.out.println("=== Sensitive data ===");
        System.out.println(new User("ann", "s3cret"));
        System.out.println("mask(\"DE8937040044\") : " + mask("DE8937040044"));   // ****0044
        System.out.println("mask(\"abc\")          : " + mask("abc"));            // ****
    }

    // ── 6. Arrays, collections and records ────────────────────────────────────

    record Pair(String left, int right) { }

    static Map<String, Integer> sampleMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        return map;
    }

    /**
     * An array is NOT a collection: {@code int[].toString()} prints {@code [I@1b6d3586} (type code
     * plus hash). Use {@code Arrays.toString(array)} for one dimension and
     * {@code Arrays.deepToString(array)} for nested arrays. Collections and maps print their
     * elements through THEIR {@code toString}: {@code [1, 2]} and {@code {a=1, b=2}}. A record gets
     * a generated {@code toString} in the form {@code Pair[left=x, right=1]}. {@code Optional}
     * prints {@code Optional[x]} or {@code Optional.empty}.
     *
     * <p>WHY this matters: printing an array field inside your own {@code toString} with {@code +}
     * gives the useless {@code [I@…} — wrap it in {@code Arrays.toString}.
     */
    static void arraysCollectionsRecordsDemo() {
        System.out.println("=== Arrays, collections and records ===");
        int[] numbers = {1, 2};
        System.out.println("int[].toString()          : " + numbers.toString().startsWith("[I@"));
        System.out.println("Arrays.toString           : " + Arrays.toString(numbers));
        System.out.println("Arrays.deepToString       : " + Arrays.deepToString(new int[][] {{1, 2}, {3}}));
        System.out.println("List                      : " + Arrays.asList("x", "y"));
        System.out.println("Map                       : " + sampleMap());
        System.out.println("record                    : " + new Pair("x", 1));
        System.out.println("Optional                  : " + Optional.of("x") + " / " + Optional.empty());
    }
}
