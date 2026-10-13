package com.vbforge.oop_interfaces_and_abstraction;

import com.vbforge.oop_interfaces_and_abstraction.defaults.Bird;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Duck;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Gull;
import com.vbforge.oop_interfaces_and_abstraction.defaults.Logger;
import com.vbforge.oop_interfaces_and_abstraction.defaults.PlainLogger;
import com.vbforge.oop_interfaces_and_abstraction.defaults.ShoutingLogger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DefaultAndStaticMethodsDemo — annotated reference for default, static and private
 * methods in interfaces, and for the "diamond" rules of multiple inheritance.
 *
 * <p>The types live in the {@code defaults} sub-package, one file each: {@code Logger},
 * {@code PlainLogger}, {@code ShoutingLogger}, {@code Flyer}, {@code Swimmer}, {@code Duck},
 * {@code Animal}, {@code Bird}, {@code Seabird}, {@code Gull}.
 *
 * <p>Topics:
 * <ol>
 *   <li>Why default methods exist — evolving an interface without breaking implementers</li>
 *   <li>Overriding a default and reusing it with {@code Interface.super.method()}</li>
 *   <li>Static methods in interfaces</li>
 *   <li>Private interface methods (Java 9)</li>
 *   <li>Conflict rules: class wins, most specific interface wins</li>
 *   <li>The diamond: two unrelated defaults force an explicit choice</li>
 *   <li>Gotchas — what a default method cannot do</li>
 * </ol>
 */
public class DefaultAndStaticMethodsDemo {

    public static void main(String[] args) {
        evolutionDemo();
        System.out.println("---");
        overridingDefaultDemo();
        System.out.println("---");
        staticMethodsDemo();
        System.out.println("---");
        privateMethodsDemo();
        System.out.println("---");
        conflictRulesDemo();
        System.out.println("---");
        diamondDemo();
        System.out.println("---");
        gotchasDemo();
    }

    // ── 1. Why default methods exist ──────────────────────────────────────────

    /**
     * A {@code default} method is an interface method WITH a body. Implementing classes inherit it
     * and may override it.
     *
     * <p>WHY this matters: before Java 8, adding a method to a published interface broke every
     * class that implemented it. Default methods let the JDK add {@code Iterable.forEach},
     * {@code Collection.removeIf}, {@code List.sort} and {@code Map.getOrDefault} to interfaces that
     * millions of existing classes already implement.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code PlainLogger} writes only {@code log}; {@code warn} and {@code error} just work</li>
     *   <li>A default method calls the abstract methods of the same interface (here: {@code log})</li>
     *   <li>The JDK collections gained defaults: {@code removeIf}, {@code getOrDefault}</li>
     * </ul>
     *
     * <p>Common gotcha: defaults are for EVOLVING an interface and for convenience methods built from
     * the abstract ones — they are not a reason to turn every interface into an abstract class.
     */
    static void evolutionDemo() {
        System.out.println("=== Why default methods ===");

        System.out.println("PlainLogger lines     : " + plainLoggerLines());                 // [[WARN] disk, [ERROR] fire]
        System.out.println("removeIf (default)    : " + removeEvens(Arrays.asList(1, 2, 3, 4))); // [1, 3]
        System.out.println("getOrDefault (default): " + countOrZero("missing"));            // 0
    }

    static List<String> plainLoggerLines() {
        PlainLogger logger = new PlainLogger();
        logger.warn("disk");                                // inherited default — PlainLogger never wrote it
        logger.error("fire");
        return logger.lines();
    }

    static List<Integer> removeEvens(List<Integer> numbers) {
        List<Integer> copy = new ArrayList<>(numbers);
        copy.removeIf(n -> n % 2 == 0);                     // Collection.removeIf is a default method
        return copy;
    }

    static int countOrZero(String key) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("known", 5);
        return counts.getOrDefault(key, 0);                 // Map.getOrDefault is a default method
    }

    // ── 2. Overriding a default ───────────────────────────────────────────────

    /**
     * An implementing class may override a default method like any other. To REUSE the interface's
     * version, call {@code InterfaceName.super.method()}.
     *
     * <p>WHY this matters: you can specialise one default and still delegate to the original instead of
     * copying its logic.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code ShoutingLogger} overrides {@code warn} but not {@code error}</li>
     *   <li>{@code Logger.super.warn(...)} is legal only because {@code Logger} is a DIRECT
     *       superinterface of the class</li>
     * </ul>
     *
     * <p>Common gotcha: {@code Logger.super.warn(...)} is not the same as {@code super.warn(...)};
     * the latter would look for the method in the superCLASS.
     */
    static void overridingDefaultDemo() {
        System.out.println("=== Overriding a default ===");

        System.out.println("ShoutingLogger        : " + shoutingLoggerLines());             // [[WARN] DISK, [ERROR] disk]
    }

    static List<String> shoutingLoggerLines() {
        ShoutingLogger logger = new ShoutingLogger();
        logger.warn("disk");                                // overridden: upper-cased, then delegated
        logger.error("disk");                               // inherited default untouched
        return logger.lines();
    }

    // ── 3. Static methods in interfaces ───────────────────────────────────────

    /**
     * An interface may declare {@code static} methods with a body. They are called on the interface name:
     * {@code Logger.prefix("INFO")}.
     *
     * <p>WHY this matters: factory and helper methods can live next to the type they belong to, instead
     * of in a separate {@code Collections}-style utility class. The JDK does this with
     * {@code Comparator.naturalOrder()}, {@code Comparator.comparing(...)}, {@code List.of(...)} and more.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code Logger.silent()} returns a ready-made {@code Logger}</li>
     *   <li>Static interface methods are NOT inherited: {@code PlainLogger} does not have {@code prefix}</li>
     *   <li>{@code Comparator.reverseOrder()} and {@code Comparator.comparing(...)} are static factories</li>
     * </ul>
     *
     * <p>Common gotcha: {@code new PlainLogger().prefix("x")} does not compile — you must write
     * {@code Logger.prefix("x")}.
     */
    static void staticMethodsDemo() {
        System.out.println("=== Static methods ===");

        System.out.println("Logger.prefix         : " + Logger.prefix("INFO") + "...");      // [INFO] ...
        Logger.silent().log("nobody hears this");
        System.out.println("methods of PlainLogger: " + hasMethod(PlainLogger.class, "prefix"));   // false
        System.out.println("sorted descending     : " + sortedDescending(Arrays.asList(3, 1, 2)));  // [3, 2, 1]
        System.out.println("sorted by length      : " + sortedByLength(Arrays.asList("ccc", "a", "bb"))); // [a, bb, ccc]
    }

    static boolean hasMethod(Class<?> type, String name) {
        for (Method method : type.getMethods()) {           // public members, including inherited ones
            if (method.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    static List<Integer> sortedDescending(List<Integer> numbers) {
        List<Integer> copy = new ArrayList<>(numbers);
        copy.sort(Comparator.<Integer>reverseOrder());      // static factory on Comparator; List.sort is a default method
        return copy;
    }

    static List<String> sortedByLength(List<String> words) {
        List<String> copy = new ArrayList<>(words);
        Collections.sort(copy, Comparator.comparing(String::length));   // static factory taking a key extractor
        return copy;
    }

    // ── 4. Private interface methods (Java 9) ─────────────────────────────────

    /**
     * Since Java 9 an interface can have {@code private} methods. They hold code shared by several
     * default methods without becoming part of the public contract.
     *
     * <p>WHY this matters: without them you would duplicate the code in every default method, or
     * expose a helper that every implementer could call or override.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code Logger.decorate} is {@code private}: {@code warn} and {@code error} use it</li>
     *   <li>It does not show up in {@code Logger.class.getMethods()} (public members only)</li>
     *   <li>Implementers cannot call or override it</li>
     * </ul>
     *
     * <p>Common gotcha: this needs Java 9. In Java 8 source the same code does not compile.
     */
    static void privateMethodsDemo() {
        System.out.println("=== Private interface methods ===");

        System.out.println("private methods       : " + privateMethodNames(Logger.class));  // [decorate]
        System.out.println("decorate is public?   : " + hasMethod(Logger.class, "decorate")); // false
    }

    /** Names of the private, non-compiler-generated methods declared by {@code type}. */
    static List<String> privateMethodNames(Class<?> type) {
        List<String> names = new ArrayList<>();
        for (Method method : type.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers()) && !method.isSynthetic()) {   // skip lambda$silent$0 etc.
                names.add(method.getName());
            }
        }
        Collections.sort(names);
        return names;
    }

    // ── 5. Conflict rules ─────────────────────────────────────────────────────

    /**
     * What happens when a class inherits the same method from several places? Java decides with
     * three rules, applied in this order:
     * <ol>
     *   <li><b>Class wins</b> — a method from a class (or superclass) beats any interface default</li>
     *   <li><b>Most specific interface wins</b> — if one interface extends another and overrides the method,
     *       its version beats the more general one</li>
     *   <li>Otherwise it is a conflict and the class must override explicitly (next section)</li>
     * </ol>
     *
     * <p>WHY this matters: these rules make adding a default method to an interface safe for classes
     * that already have a method with the same signature — their own method keeps winning.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code Bird extends Animal implements Flyer}: {@code move()} is Animal's → "walk", not "fly"</li>
     *   <li>{@code Gull implements Flyer, Seabird}: Seabird is more specific → "glide"</li>
     * </ul>
     *
     * <p>Common gotcha: a rule-1 victory is silent — a default you expected to inherit may be hidden by
     * a superclass method you forgot about.
     */
    static void conflictRulesDemo() {
        System.out.println("=== Conflict rules ===");

        System.out.println("Bird (class wins)     : " + new Bird().move());                // walk
        System.out.println("Gull (specific wins)  : " + new Gull().move());                // glide
    }

    // ── 6. The diamond ────────────────────────────────────────────────────────

    /**
     * If a class inherits the same default method from two UNRELATED interfaces, neither rule can pick
     * a winner. The compiler refuses to guess: the class MUST override the method itself.
     *
     * <p>WHY this matters: this is Java's safe form of multiple inheritance. You may inherit several
     * TYPES and several default BEHAVIOURS, but ambiguity is always resolved explicitly by the
     * programmer — never silently by the language.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code Duck implements Flyer, Swimmer} — both define {@code move()}</li>
     *   <li>{@code Duck} overrides {@code move()} and chooses: {@code Flyer.super.move()} and
     *       {@code Swimmer.super.move()}</li>
     *   <li>It could also ignore both and write its own body</li>
     * </ul>
     *
     * <p>Common gotcha: if {@code Duck} did not override {@code move()}, the code would not compile
     * ("inherits unrelated defaults for move()").
     */
    static void diamondDemo() {
        System.out.println("=== The diamond ===");

        System.out.println("Duck                  : " + new Duck().move());                 // fly & swim
    }

    // ── 7. Gotchas ────────────────────────────────────────────────────────────

    /**
     * Limits of default methods.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>A default method is always {@code public}</li>
     *   <li>{@code Method.isDefault()} distinguishes defaults from abstract and static methods</li>
     *   <li>A default method cannot override {@code equals}, {@code hashCode} or {@code toString} of
     *       {@link Object} — {@code default String toString() { ... }} is a compile error, because a class
     *       method (rule 1: class wins) always beats it</li>
     *   <li>An interface still cannot hold per-object state: it declares no instance fields</li>
     * </ul>
     *
     * <p>Common gotcha: if you need state or {@code Object} methods, you need an abstract class.
     */
    static void gotchasDemo() {
        System.out.println("=== Gotchas ===");

        System.out.println("warn is default      : " + isDefault(Logger.class, "warn", String.class));    // true
        System.out.println("log is default       : " + isDefault(Logger.class, "log", String.class));     // false
        System.out.println("warn is public       : " + isPublic(Logger.class, "warn", String.class));     // true
        System.out.println("instance fields      : " + Logger.class.getDeclaredFields().length);          // 0
    }

    static boolean isDefault(Class<?> iface, String name, Class<?>... parameterTypes) {
        try {
            return iface.getMethod(name, parameterTypes).isDefault();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    static boolean isPublic(Class<?> iface, String name, Class<?>... parameterTypes) {
        try {
            return Modifier.isPublic(iface.getMethod(name, parameterTypes).getModifiers());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
