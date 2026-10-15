package com.vbforge.enums_and_nested_classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Demonstrates anonymous classes: implementing an interface on the spot,
 * extending a class with constructor arguments, keeping state, how {@code this}
 * differs from a lambda, and the double-brace-initialisation trap.
 *
 * <p>Everything here is valid Java 8. Anonymous classes pre-date lambdas;
 * since Java 8 a lambda is the better choice for a single-method interface,
 * but anonymous classes remain the tool when you need state, several methods,
 * or a class (not interface) as the supertype.
 */
public class AnonymousClassDemo {

    // ══ 1. Implementing an interface inline ═════════════════════════════════
    /**
     * {@code new Interface() { ... }} declares AND instantiates a nameless class
     * in one expression.
     *
     * <p>Why it matters: before Java 8 this was the only way to pass behaviour
     * (comparators, listeners, callbacks) without writing a named class.
     *
     * <p>Complexity: each anonymous class compiles to its own {@code .class} file
     * ({@code AnonymousClassDemo$1.class}, {@code $2}, ...). Sorting below is
     * O(n log n) comparisons.
     *
     * <p>Gotcha: the anonymous body can read local variables of the enclosing
     * method only if they are effectively final. The <em>reference</em> is
     * frozen — the object it points to (the {@code events} list) can still change.
     */
    public interface Listener {
        void onStart(String task);

        void onFinish(String task);
    }

    /** Returns a sorted COPY (shortest first); equal lengths keep their original order (stable sort). */
    public static List<String> sortByLength(List<String> words) {
        List<String> copy = new ArrayList<>(words);
        Collections.sort(copy, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Integer.compare(a.length(), b.length());
            }
        });
        return copy;
    }

    /** Runs a "task" and returns what the listener recorded. */
    public static List<String> runWithListener(String task) {
        final List<String> events = new ArrayList<>();     // 'final' is optional since Java 8 (effectively final)
        Listener listener = new Listener() {                // two methods → cannot be a lambda
            @Override
            public void onStart(String t) {
                events.add("start:" + t);
            }

            @Override
            public void onFinish(String t) {
                events.add("finish:" + t);
            }
        };
        listener.onStart(task);
        listener.onFinish(task);
        return events;
    }

    public static void interfaceDemo() {
        System.out.println("sortByLength  = " + sortByLength(Arrays.asList("pear", "fig", "banana", "kiwi")));
        System.out.println("listener log  = " + runWithListener("backup"));
    }


    // ══ 2. Extending a class (with constructor arguments) ═══════════════════
    /**
     * An anonymous class can extend a concrete or abstract CLASS. Arguments in the
     * parentheses go to the superclass constructor.
     *
     * <p>Why it matters: lambdas can only target interfaces. For an abstract
     * class with state, an anonymous subclass is the one-off option.
     *
     * <p>Gotcha: an anonymous class has no constructor of its own — you cannot
     * declare one. Use an instance-initializer block or fields if you need setup.
     */
    public abstract static class Greeter {
        private final String name;

        protected Greeter(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

        public abstract String greet();
    }

    public static Greeter casualGreeter(String name) {
        return new Greeter(name) {                          // "name" goes to Greeter's constructor
            @Override
            public String greet() {
                return "Hey " + name() + "!";               // name() reads the state stored in the superclass
            }
        };
    }

    public static void abstractClassDemo() {
        Greeter greeter = casualGreeter("Ann");
        System.out.println(greeter.greet());
        System.out.println("is anonymous? " + greeter.getClass().isAnonymousClass()
                + ", superclass = " + greeter.getClass().getSuperclass().getSimpleName());
    }


    // ══ 3. Own state and captured values ════════════════════════════════════
    /**
     * An anonymous class is a real class, so it can have mutable FIELDS.
     *
     * <p>Why it matters: a captured local cannot be changed (effectively final),
     * but a field of the anonymous class can — that is the standard workaround
     * when a callback needs a counter or accumulator.
     *
     * <p>Complexity: O(1) per call, O(1) extra space per instance.
     *
     * <p>Gotcha: each call to {@code counterFrom} creates an independent object
     * with its own field — two suppliers never share their counter.
     */
    public static Supplier<Integer> counterFrom(int start, int step) {
        return new Supplier<Integer>() {
            private int next = start;                       // captured parameter initialises a mutable field

            @Override
            public Integer get() {
                int value = next;
                next += step;
                return value;
            }
        };
    }

    public static void stateDemo() {
        Supplier<Integer> evens = counterFrom(0, 2);
        Supplier<Integer> tens = counterFrom(10, 10);
        System.out.println("evens: " + evens.get() + ", " + evens.get() + ", " + evens.get());
        System.out.println("tens : " + tens.get() + ", " + tens.get());
    }


    // ══ 4. "this" — anonymous class vs lambda ═══════════════════════════════
    /**
     * Inside an anonymous class, {@code this} is the ANONYMOUS INSTANCE.
     * Inside a lambda, {@code this} is the ENCLOSING instance — a lambda
     * does not introduce a new scope or a new class.
     *
     * <p>Why it matters: it is the most common behavioural difference when
     * refactoring an anonymous class into a lambda (or back) — code that calls
     * {@code this.something()} silently changes meaning.
     *
     * <p>Gotcha: an anonymous class has an EMPTY simple name
     * ({@code getSimpleName()} returns {@code ""}). A lambda object's class is
     * generated at runtime by the JVM and is not reported as anonymous at all.
     */
    public static class Probe {
        public String anonymousThis() {
            Supplier<String> supplier = new Supplier<String>() {
                @Override
                public String get() {
                    return this.getClass().getSimpleName();   // "this" = the anonymous Supplier
                }
            };
            return supplier.get();
        }

        public String lambdaThis() {
            Supplier<String> supplier = () -> this.getClass().getSimpleName();   // "this" = the Probe
            return supplier.get();
        }
    }

    public static Supplier<String> anonymousSupplier() {
        return new Supplier<String>() {
            @Override
            public String get() {
                return "anonymous";
            }
        };
    }

    public static Supplier<String> lambdaSupplier() {
        return () -> "lambda";
    }

    public static void thisDemo() {
        Probe probe = new Probe();
        System.out.println("anonymous this -> simple name \"" + probe.anonymousThis() + "\"");
        System.out.println("lambda this    -> simple name \"" + probe.lambdaThis() + "\"");
        System.out.println("anonymousSupplier is anonymous class? "
                + anonymousSupplier().getClass().isAnonymousClass());
        System.out.println("lambdaSupplier    is anonymous class? "
                + lambdaSupplier().getClass().isAnonymousClass());
    }


    // ══ 5. The double-brace initialisation trap ═════════════════════════════
    /**
     * {@code new ArrayList<String>() {{ add("a"); }}} is NOT special syntax: the
     * outer braces declare an anonymous SUBCLASS of {@code ArrayList}, the inner
     * braces are its instance initializer.
     *
     * <p>Why it is tempting: it looks like a collection literal.
     *
     * <p>Why to avoid it:
     * <ul>
     *   <li>it creates an extra class per use-site (class-loading cost)</li>
     *   <li>in an instance method the object also keeps the enclosing instance alive (leak)</li>
     *   <li>its runtime class is no longer {@code ArrayList}, which breaks
     *       {@code getClass()}-based {@code equals} implementations and serialization</li>
     * </ul>
     * Use {@code new ArrayList<>(Arrays.asList(...))} (Java 8) instead.
     */
    @SuppressWarnings("serial")
    public static List<String> doubleBraceList() {
        return new ArrayList<String>() {{
            add("a");
            add("b");
        }};
    }

    public static List<String> plainList() {
        return new ArrayList<>(Arrays.asList("a", "b"));
    }

    public static void doubleBraceDemo() {
        List<String> trap = doubleBraceList();
        List<String> plain = plainList();
        System.out.println("equal as lists?     " + trap.equals(plain));
        System.out.println("same runtime class? " + (trap.getClass() == plain.getClass()));
        System.out.println("trap class is anonymous? " + trap.getClass().isAnonymousClass());
    }


    // ── Main — quick smoke run ───────────────────────────────────────────────
    public static void main(String[] args) {
        interfaceDemo();
        System.out.println("---");
        abstractClassDemo();
        System.out.println("---");
        stateDemo();
        System.out.println("---");
        thisDemo();
        System.out.println("---");
        doubleBraceDemo();
    }
}
