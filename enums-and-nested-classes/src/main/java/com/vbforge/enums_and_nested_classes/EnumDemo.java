package com.vbforge.enums_and_nested_classes;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Demonstrates Java enums: constants, fields + constructors, methods,
 * constant-specific class bodies, interfaces, EnumMap/EnumSet and the
 * enum-singleton idiom.
 *
 * <p>Everything here is valid Java 8. Each {@code ...Demo()} method prints what
 * the section proves; the matching {@code @Nested} class in
 * {@code EnumDemoTest} asserts the same claims.
 */
public class EnumDemo {

    // ══ 1. Enum basics ═══════════════════════════════════════════════════════
    /**
     * An enum is a class with a FIXED set of instances created by the JVM.
     *
     * <p>Why it matters: it replaces {@code static final int WINTER = 0} style
     * constants, which are not type-safe (any int compiles) and print as numbers.
     *
     * <p>Complexity: {@code values()} is O(n) (it returns a fresh copy);
     * {@code valueOf} is O(1) (internal name map); {@code ==} is O(1).
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code valueOf} is case-sensitive and throws {@link IllegalArgumentException}</li>
     *   <li>{@code switch} on a {@code null} enum throws {@link NullPointerException}</li>
     *   <li>{@code values()} returns a NEW array each call — don't call it in a hot loop</li>
     *   <li>{@code name()} is final; {@code toString()} may be overridden — don't parse toString()</li>
     * </ul>
     */
    public enum Season { WINTER, SPRING, SUMMER, AUTUMN }

    /** Classic (Java 8) switch over an enum — case labels use the bare constant name. */
    public static String activity(Season season) {
        switch (season) {
            case WINTER: return "skiing";
            case SPRING: return "hiking";
            case SUMMER: return "swimming";
            case AUTUMN: return "mushroom picking";
            default: throw new IllegalStateException("Unhandled season: " + season);
        }
    }

    public static void basicsDemo() {
        System.out.println("values()          = " + Arrays.toString(Season.values()));
        System.out.println("SUMMER.name()     = " + Season.SUMMER.name());
        System.out.println("SUMMER.ordinal()  = " + Season.SUMMER.ordinal());
        System.out.println("valueOf(\"SPRING\") == SPRING ? " + (Season.valueOf("SPRING") == Season.SPRING));
        System.out.println("WINTER.compareTo(AUTUMN) = " + Season.WINTER.compareTo(Season.AUTUMN)
                + "  (ordinal difference)");
        System.out.println("activity(SUMMER)  = " + activity(Season.SUMMER));
        try {
            Season.valueOf("summer");   // wrong case
        } catch (IllegalArgumentException e) {
            System.out.println("valueOf(\"summer\") -> IllegalArgumentException: " + e.getMessage());
        }
    }


    // ══ 2. Fields, constructors and methods ═════════════════════════════════
    /**
     * An enum can carry state and behaviour like any class.
     *
     * <p>Rules worth knowing:
     * <ul>
     *   <li>The constructor is implicitly {@code private} — nobody can create new constants</li>
     *   <li>Constants are created FIRST, before the enum's static fields are initialised,
     *       so a constructor cannot read a non-constant static field (compile error).
     *       That is why the lookup map below is filled in a {@code static} block.</li>
     *   <li>Fields should be {@code final} — a mutable enum is shared global state</li>
     * </ul>
     *
     * <p>Complexity: {@link #lookup(String)} is O(1) thanks to the static map;
     * a loop over {@code values()} would be O(n) and allocate an array every call.
     */
    public enum Planet {
        MERCURY(3.303e+23, 2.4397e6),
        EARTH(5.976e+24, 6.37814e6),
        JUPITER(1.9e+27, 7.1492e7);

        private static final double G = 6.67300E-11;   // compile-time constant: legal everywhere
        private static final Map<String, Planet> BY_NAME = new HashMap<>();

        static {
            for (Planet planet : values()) {
                BY_NAME.put(planet.name().toLowerCase(Locale.ROOT), planet);
            }
        }

        private final double mass;     // kg
        private final double radius;   // metres

        Planet(double mass, double radius) {
            this.mass = mass;
            this.radius = radius;
        }

        public double mass()   { return mass; }
        public double radius() { return radius; }

        /** Surface gravity in m/s². */
        public double surfaceGravity() {
            return G * mass / (radius * radius);
        }

        /** Weight (in newtons) of an object with the given mass on this planet. */
        public double surfaceWeight(double otherMass) {
            return otherMass * surfaceGravity();
        }

        /** Case-insensitive, null-safe lookup — the non-throwing alternative to {@code valueOf}. */
        public static Optional<Planet> lookup(String name) {
            return name == null
                    ? Optional.<Planet>empty()
                    : Optional.ofNullable(BY_NAME.get(name.trim().toLowerCase(Locale.ROOT)));
        }
    }

    public static void fieldsAndMethodsDemo() {
        for (Planet p : Planet.values()) {
            System.out.printf(Locale.ROOT, "%-8s gravity = %.2f m/s2%n", p, p.surfaceGravity());
        }
        System.out.println("lookup(\" earth \") = " + Planet.lookup(" earth "));
        System.out.println("lookup(\"pluto\")   = " + Planet.lookup("pluto"));
    }


    // ══ 3. Constant-specific class bodies ═══════════════════════════════════
    /**
     * Each constant may supply its own method body. The enum declares an
     * {@code abstract} method and the compiler forces EVERY constant to implement it.
     *
     * <p>Why it matters: it replaces {@code switch (this)} chains inside the enum.
     * Add a new constant and the compiler tells you what's missing — a forgotten
     * {@code case} would only fail at runtime.
     *
     * <p>Complexity: a virtual call, O(1).
     *
     * <p>Gotcha: a constant with a body is an instance of an ANONYMOUS SUBCLASS, so
     * {@code ADD.getClass() != Operation.class}. Use {@code getDeclaringClass()}
     * when you need the enum type itself.
     */
    public enum Operation {
        ADD("+") {
            @Override public int apply(int a, int b) { return a + b; }
        },
        SUBTRACT("-") {
            @Override public int apply(int a, int b) { return a - b; }
        },
        MULTIPLY("*") {
            @Override public int apply(int a, int b) { return a * b; }
        };

        private final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() { return symbol; }

        public abstract int apply(int a, int b);
    }

    /**
     * An enum as a tiny STATE MACHINE: every constant knows its successor.
     * Constant bodies may reference other constants — the method runs later,
     * after all constants exist.
     */
    public enum TrafficLight {
        RED(30)    { @Override public TrafficLight next() { return GREEN; } },
        GREEN(25)  { @Override public TrafficLight next() { return YELLOW; } },
        YELLOW(5)  { @Override public TrafficLight next() { return RED; } };

        private final int seconds;

        TrafficLight(int seconds) {
            this.seconds = seconds;
        }

        public int seconds() { return seconds; }

        public abstract TrafficLight next();
    }

    public static void constantSpecificBodiesDemo() {
        for (Operation op : Operation.values()) {
            System.out.println("6 " + op.symbol() + " 3 = " + op.apply(6, 3));
        }
        System.out.println("ADD.getClass()          = " + Operation.ADD.getClass().getName());
        System.out.println("ADD.getDeclaringClass() = " + Operation.ADD.getDeclaringClass().getName());

        TrafficLight light = TrafficLight.RED;
        StringBuilder cycle = new StringBuilder(light.name());
        for (int i = 0; i < 3; i++) {
            light = light.next();
            cycle.append(" -> ").append(light);
        }
        System.out.println("state machine: " + cycle);
    }


    // ══ 4. Enums implementing interfaces ════════════════════════════════════
    /**
     * Enums cannot EXTEND a class (they already extend {@link Enum}) but they can
     * IMPLEMENT interfaces. That lets callers depend on the interface while the
     * set of implementations stays closed and enumerable.
     *
     * <p>Why it matters: a strategy-per-constant without a factory or registry.
     *
     * <p>Gotcha: an enum can't be subclassed either — "extending" an enum's
     * behaviour means putting a shared interface in front of it.
     */
    public interface Discount {
        double applyTo(double price);
    }

    public enum CustomerTier implements Discount {
        REGULAR(0), SILVER(5), GOLD(10);

        private final int percentOff;

        CustomerTier(int percentOff) {
            this.percentOff = percentOff;
        }

        public int percentOff() { return percentOff; }

        @Override
        public double applyTo(double price) {
            return price * (100 - percentOff) / 100.0;
        }
    }

    public static void enumImplementsInterfaceDemo() {
        Discount discount = CustomerTier.GOLD;   // callers only see the interface
        System.out.println("GOLD discount on 80.0 = " + discount.applyTo(80.0));
        for (CustomerTier tier : CustomerTier.values()) {
            System.out.println(tier + " pays " + tier.applyTo(100.0) + " for a 100.0 item");
        }
    }


    // ══ 5. EnumMap and EnumSet ══════════════════════════════════════════════
    /**
     * Specialised collections for enum keys.
     *
     * <p>Why it matters: {@code EnumMap} is backed by an array indexed by
     * {@code ordinal()}, {@code EnumSet} by a bit mask (one {@code long} for up
     * to 64 constants). Both iterate in DECLARATION order — a {@code HashMap}
     * gives you no order at all.
     *
     * <p>Complexity:
     * <ul>
     *   <li>EnumMap get/put/containsKey: O(1), no hashing, no collisions</li>
     *   <li>EnumSet contains/add/remove: O(1) bit operations</li>
     *   <li>EnumSet union/intersection ({@code addAll}/{@code retainAll}): O(1) for small enums</li>
     *   <li>Space: O(number of constants), not O(entries)</li>
     * </ul>
     *
     * <p>Gotcha: neither accepts {@code null} keys/elements ({@link NullPointerException}).
     */
    public static EnumMap<Season, String> scrambledCalendar() {
        EnumMap<Season, String> months = new EnumMap<>(Season.class);
        months.put(Season.AUTUMN, "Sep-Nov");   // inserted out of order on purpose
        months.put(Season.WINTER, "Dec-Feb");
        months.put(Season.SUMMER, "Jun-Aug");
        months.put(Season.SPRING, "Mar-May");
        return months;
    }

    public static Set<Season> warmSeasons() {
        return EnumSet.range(Season.SPRING, Season.SUMMER);        // inclusive range by ordinal
    }

    public static Set<Season> coldSeasons() {
        return EnumSet.complementOf(EnumSet.range(Season.SPRING, Season.SUMMER));
    }

    public static void enumCollectionsDemo() {
        System.out.println("EnumMap (inserted AUTUMN, WINTER, SUMMER, SPRING) = " + scrambledCalendar());
        System.out.println("warmSeasons = " + warmSeasons());
        System.out.println("coldSeasons = " + coldSeasons());
        System.out.println("allOf       = " + EnumSet.allOf(Season.class));
        System.out.println("noneOf      = " + EnumSet.noneOf(Season.class));
    }


    // ══ 6. Enum singleton ═══════════════════════════════════════════════════
    /**
     * The enum singleton (Effective Java, Item 3).
     *
     * <p>Why it matters: the JVM guarantees exactly one instance per constant,
     * thread-safe class initialisation, and — unlike a hand-written singleton —
     * it survives serialization and reflection attacks.
     *
     * <p>Gotcha: this is shared mutable state. It is convenient, but it makes
     * tests order-dependent unless you clear it — prefer injecting dependencies
     * in real applications.
     */
    public enum AppRegistry {
        INSTANCE;

        private final Map<String, String> settings = new HashMap<>();

        public void put(String key, String value) { settings.put(key, value); }

        public String get(String key) { return settings.get(key); }

        public void clear() { settings.clear(); }
    }

    /**
     * Tries to create a second {@code AppRegistry} through reflection.
     *
     * @return the exception the JVM throws (an {@link IllegalArgumentException}),
     *         or {@code null} if — wrongly — creation succeeded
     */
    public static Throwable reflectiveCreationFailure() {
        try {
            // Enum constructors get two hidden leading parameters: name and ordinal.
            Constructor<AppRegistry> constructor =
                    AppRegistry.class.getDeclaredConstructor(String.class, int.class);
            constructor.setAccessible(true);
            constructor.newInstance("COPY", 1);
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    public static void singletonDemo() {
        AppRegistry.INSTANCE.put("mode", "demo");
        System.out.println("same instance?   " + (AppRegistry.INSTANCE == AppRegistry.valueOf("INSTANCE")));
        System.out.println("shared state:    mode = " + AppRegistry.INSTANCE.get("mode"));
        System.out.println("reflection copy: " + reflectiveCreationFailure());
        AppRegistry.INSTANCE.clear();
    }


    // ── Main — quick smoke run ───────────────────────────────────────────────
    public static void main(String[] args) {
        basicsDemo();
        System.out.println("---");
        fieldsAndMethodsDemo();
        System.out.println("---");
        constantSpecificBodiesDemo();
        System.out.println("---");
        enumImplementsInterfaceDemo();
        System.out.println("---");
        enumCollectionsDemo();
        System.out.println("---");
        singletonDemo();
    }
}
