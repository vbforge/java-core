package com.vbforge.oop_interfaces_and_abstraction;

import com.vbforge.oop_interfaces_and_abstraction.basics.Countdown;
import com.vbforge.oop_interfaces_and_abstraction.basics.EmailNotifier;
import com.vbforge.oop_interfaces_and_abstraction.basics.MemoryStore;
import com.vbforge.oop_interfaces_and_abstraction.basics.Notifier;
import com.vbforge.oop_interfaces_and_abstraction.basics.ReadWriteStore;
import com.vbforge.oop_interfaces_and_abstraction.basics.ReadableStore;
import com.vbforge.oop_interfaces_and_abstraction.basics.SmsNotifier;
import com.vbforge.oop_interfaces_and_abstraction.basics.Version;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;
import java.util.TreeSet;

/**
 * InterfaceBasicsDemo — annotated reference for Java interfaces.
 *
 * <p>The types used here live in the {@code basics} sub-package, one file each, so you can open
 * them side by side with this class: {@code Notifier}, {@code EmailNotifier}, {@code SmsNotifier},
 * {@code ReadableStore}, {@code WritableStore}, {@code ReadWriteStore}, {@code MemoryStore},
 * {@code Version} and {@code Countdown}.
 *
 * <p>Topics:
 * <ol>
 *   <li>An interface is a contract — and a type you can program against</li>
 *   <li>What the compiler adds for you: implicit modifiers and constants</li>
 *   <li>Interface inheritance and multiple inheritance of TYPE</li>
 *   <li>Contracts built into the JDK: {@link Comparable}, {@link Iterable}</li>
 *   <li>Marker interfaces — an interface with no methods</li>
 *   <li>Anonymous classes and lambdas as quick implementations</li>
 * </ol>
 */
public class InterfaceBasicsDemo {

    public static void main(String[] args) {
        contractDemo();
        System.out.println("---");
        implicitModifiersDemo();
        System.out.println("---");
        interfaceInheritanceDemo();
        System.out.println("---");
        jdkContractsDemo();
        System.out.println("---");
        markerInterfacesDemo();
        System.out.println("---");
        anonymousAndLambdaDemo();
    }

    // ── 1. An interface is a contract ─────────────────────────────────────────

    /**
     * An {@code interface} lists WHAT a type can do without saying HOW. Any class that
     * {@code implements} it promises to supply every abstract method.
     *
     * <p>WHY this matters: callers depend on the contract, not on a concrete class, so you can
     * add or swap implementations without touching them. It is the basis of polymorphism across
     * unrelated class hierarchies — {@code EmailNotifier} and {@code SmsNotifier} share no parent
     * class except {@link Object}.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>A variable may have an interface type: {@code Notifier n = new SmsNotifier()}</li>
     *   <li>{@code new Notifier()} does not compile — an interface cannot be instantiated</li>
     *   <li>{@code n.send(...)} runs the implementation of the OBJECT's class (dynamic dispatch)</li>
     *   <li>A class may implement an interface without extending anything</li>
     * </ul>
     *
     * <p>Common gotcha: the interface type limits what you can call. Through a {@code Notifier}
     * variable only {@code send} (and {@link Object} methods) are visible.
     */
    static void contractDemo() {
        System.out.println("=== Interface as a contract ===");

        // Notifier n = new Notifier();                     // ← compile error: Notifier is abstract; cannot be instantiated
        for (String receipt : broadcast(sampleNotifiers(), "Build finished")) {
            System.out.println(receipt);                    // EMAIL: Build finished / SMS: Build finished
        }
    }

    static List<Notifier> sampleNotifiers() {
        return Arrays.<Notifier>asList(new EmailNotifier(), new SmsNotifier());
    }

    /** Works for ANY Notifier, including ones that do not exist yet. */
    static List<String> broadcast(List<Notifier> notifiers, String message) {
        List<String> receipts = new ArrayList<>();
        for (Notifier notifier : notifiers) {
            receipts.add(notifier.send(message));
        }
        return receipts;
    }

    // ── 2. Implicit modifiers and constants ───────────────────────────────────

    /**
     * The compiler fills in modifiers you leave out: interface FIELDS are
     * {@code public static final} (constants), interface METHODS are {@code public abstract}.
     *
     * <p>WHY this matters: an interface can never carry per-object state, and its contract is always
     * public. A constant like {@code Notifier.MAX_LENGTH} is shared by every implementer.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code int MAX_LENGTH = 20;} really means {@code public static final int MAX_LENGTH = 20;}</li>
     *   <li>{@code String send(String)} really means {@code public abstract String send(String)}</li>
     *   <li>An implementing class sees the constant by its simple name ({@code SmsNotifier} uses it)</li>
     * </ul>
     *
     * <p>Common gotcha: because interface methods are public, the implementing method MUST be
     * {@code public} too. Leaving it package-private is a compile error ("attempting to assign
     * weaker access privileges") — the same widen-never-narrow rule as for overriding.
     */
    static void implicitModifiersDemo() {
        System.out.println("=== Implicit modifiers ===");

        System.out.println("MAX_LENGTH modifiers  : " + constantModifiers());              // public static final
        System.out.println("send() modifiers      : " + sendModifiers());                  // public abstract
        System.out.println("Notifier.MAX_LENGTH   : " + Notifier.MAX_LENGTH);              // 20
        System.out.println("long SMS is cut       : " + new SmsNotifier().send("This message is far too long for one SMS"));
    }

    static String constantModifiers() {
        try {
            return Modifier.toString(Notifier.class.getField("MAX_LENGTH").getModifiers());
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    static String sendModifiers() {
        try {
            return Modifier.toString(Notifier.class.getMethod("send", String.class).getModifiers());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    // ── 3. Interface inheritance ──────────────────────────────────────────────

    /**
     * An interface can {@code extends} one or MORE interfaces. A class can {@code implements} as many
     * interfaces as it likes (while extending only ONE class).
     *
     * <p>WHY this matters: Java forbids inheriting from several CLASSES (state and constructors
     * would clash), but it allows inheriting several TYPES. This is "multiple inheritance of type":
     * one object can be viewed through many narrow contracts.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code ReadWriteStore extends ReadableStore, WritableStore} — it adds no methods, it combines</li>
     *   <li>{@code MemoryStore} is-a {@code ReadWriteStore}, is-a {@code ReadableStore}, is-a {@code WritableStore}</li>
     *   <li>A method that accepts only {@code ReadableStore} cannot write — least privilege</li>
     *   <li>{@link Class#getInterfaces()} returns only the DIRECTLY declared interfaces</li>
     *   <li>The JDK does the same: {@code ArrayList implements List, RandomAccess, Cloneable, Serializable}</li>
     * </ul>
     *
     * <p>Common gotcha: {@code instanceof} works with interfaces, but an interface type never tells
     * you the concrete class — use it to ask "can you do X?", not "who are you?".
     */
    static void interfaceInheritanceDemo() {
        System.out.println("=== Interface inheritance ===");

        MemoryStore store = new MemoryStore();
        store.write("lang", "Java");
        System.out.println("read through ReadableStore: " + lookup(store, "lang"));       // Java
        System.out.println("MemoryStore is-a Writable : " + (store instanceof ReadableStore));   // true
        System.out.println("ReadWriteStore extends    : " + interfaceNames(ReadWriteStore.class));  // [ReadableStore, WritableStore]
        System.out.println("ArrayList implements      : " + interfaceNames(ArrayList.class));       // [List, RandomAccess, Cloneable, Serializable]
    }

    /** The parameter type says "I only read" — the compiler enforces it. */
    static String lookup(ReadableStore store, String key) {
        return store.read(key);                             // store.write(...) would not compile here
    }

    static List<String> interfaceNames(Class<?> type) {
        List<String> names = new ArrayList<>();
        for (Class<?> iface : type.getInterfaces()) {
            names.add(iface.getSimpleName());
        }
        return names;
    }

    // ── 4. Contracts built into the JDK ───────────────────────────────────────

    /**
     * Many JDK features are interfaces you implement to plug into them.
     * <ul>
     *   <li>{@link Comparable} — "I have a natural order": {@code Collections.sort}, {@code TreeSet} and
     *       {@code TreeMap} use it</li>
     *   <li>{@link Iterable} — "I can be looped over": the enhanced {@code for} works on any Iterable</li>
     * </ul>
     *
     * <p>WHY this matters: implementing one small interface gives your class access to a whole
     * library of algorithms.
     *
     * <p>Complexity: sorting with {@code Collections.sort} is O(n log n) comparisons.
     *
     * <p>Common gotcha: keep {@code compareTo} CONSISTENT with {@code equals}. {@link BigDecimal}
     * breaks the rule on purpose: {@code 1.0} and {@code 1.00} are {@code equals}-different but
     * {@code compareTo}-equal, so a {@link HashSet} keeps both while a {@link TreeSet} keeps one.
     */
    static void jdkContractsDemo() {
        System.out.println("=== JDK contracts: Comparable and Iterable ===");

        List<Version> versions = Arrays.asList(new Version(1, 10), new Version(1, 2), new Version(0, 9));
        System.out.println("natural order         : " + sortVersions(versions));            // [0.9, 1.2, 1.10]
        System.out.println("newest first          : " + newestFirst(versions));             // [1.10, 1.2, 0.9]
        System.out.println("for-each over Countdown: " + countdown(3));                    // [3, 2, 1]
        int[] sizes = bigDecimalSetSizes();
        System.out.println("HashSet vs TreeSet    : " + sizes[0] + " vs " + sizes[1]);     // 2 vs 1
    }

    static List<Version> sortVersions(List<Version> versions) {
        List<Version> copy = new ArrayList<>(versions);
        Collections.sort(copy);                             // needs Comparable — Version provides compareTo
        return copy;
    }

    static List<Version> newestFirst(List<Version> versions) {
        List<Version> copy = new ArrayList<>(versions);
        Collections.sort(copy, Collections.<Version>reverseOrder());
        return copy;
    }

    static List<Integer> countdown(int from) {
        List<Integer> values = new ArrayList<>();
        for (int n : new Countdown(from)) {                 // legal because Countdown implements Iterable<Integer>
            values.add(n);
        }
        return values;
    }

    /** Returns {HashSet size, TreeSet size} for the values 1.0 and 1.00. */
    static int[] bigDecimalSetSizes() {
        BigDecimal a = new BigDecimal("1.0");
        BigDecimal b = new BigDecimal("1.00");
        Set<BigDecimal> hash = new HashSet<>(Arrays.asList(a, b));
        Set<BigDecimal> tree = new TreeSet<>(Arrays.asList(a, b));
        return new int[]{hash.size(), tree.size()};
    }

    // ── 5. Marker interfaces ──────────────────────────────────────────────────

    /**
     * A MARKER interface has no methods at all; implementing it simply tags a class with a
     * capability that other code checks with {@code instanceof}.
     *
     * <p>WHY this matters: {@link RandomAccess} tells algorithms "{@code get(i)} is cheap on me", so
     * they can pick an index-based loop instead of an iterator. {@code Serializable} and
     * {@code Cloneable} work the same way.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code ArrayList} is a {@code RandomAccess}; {@code LinkedList} is not</li>
     *   <li>{@code RandomAccess} declares zero methods</li>
     * </ul>
     *
     * <p>Common gotcha: a marker carries no compiler-checked behaviour — nothing stops you from
     * tagging a class that does not honour the promise. Modern code often uses annotations instead.
     */
    static void markerInterfacesDemo() {
        System.out.println("=== Marker interfaces ===");

        System.out.println("ArrayList  RandomAccess: " + isRandomAccess(new ArrayList<String>()));    // true
        System.out.println("LinkedList RandomAccess: " + isRandomAccess(new LinkedList<String>()));   // false
        System.out.println("methods in RandomAccess: " + RandomAccess.class.getMethods().length);     // 0
    }

    static boolean isRandomAccess(List<?> list) {
        return list instanceof RandomAccess;
    }

    // ── 6. Anonymous classes and lambdas ──────────────────────────────────────

    /**
     * You do not always need a named class. An ANONYMOUS class implements an interface inline;
     * a LAMBDA does the same in one line when the interface has exactly ONE abstract method
     * (a "functional interface" — covered fully in the {@code functional-interfaces} module).
     *
     * <p>WHY this matters: tiny one-off implementations (comparators, callbacks, tests) stay next to
     * the code that uses them.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code new Notifier() { ... }} — anonymous class, may implement any interface</li>
     *   <li>{@code message -> "LAMBDA: " + message} — lambda, only for single-abstract-method interfaces</li>
     *   <li>{@code Notifier} has one abstract method, so it is lambda-friendly;
     *       {@code ReadWriteStore} has two (both inherited), so it is not</li>
     * </ul>
     *
     * <p>Common gotcha: adding a SECOND abstract method to an interface silently breaks every lambda
     * that implements it.
     */
    static void anonymousAndLambdaDemo() {
        System.out.println("=== Anonymous class and lambda ===");

        System.out.println("anonymous             : " + anonymousNotifier().send("hi"));   // ANON: hi
        System.out.println("lambda                : " + lambdaNotifier().send("hi"));      // LAMBDA: hi
        System.out.println("abstract methods in Notifier       : " + abstractMethodCount(Notifier.class));        // 1
        System.out.println("abstract methods in ReadWriteStore : " + abstractMethodCount(ReadWriteStore.class)); // 2
    }

    static Notifier anonymousNotifier() {
        return new Notifier() {
            @Override
            public String send(String message) {
                return "ANON: " + message;
            }
        };
    }

    static Notifier lambdaNotifier() {
        return message -> "LAMBDA: " + message;
    }

    /** Counts abstract methods visible on an interface, inherited ones included. */
    static int abstractMethodCount(Class<?> iface) {
        int count = 0;
        for (Method method : iface.getMethods()) {
            if (Modifier.isAbstract(method.getModifiers())) {
                count++;
            }
        }
        return count;
    }
}
