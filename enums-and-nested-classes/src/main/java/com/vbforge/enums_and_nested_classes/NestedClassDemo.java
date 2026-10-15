package com.vbforge.enums_and_nested_classes;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Demonstrates the four kinds of nested classes: static nested, inner
 * (non-static member), local, and how to tell them apart at runtime.
 * (Anonymous classes have their own pair: {@code AnonymousClassDemo}.)
 *
 * <p>Everything here is valid Java 8.
 *
 * <p>Cheat sheet:
 * <pre>
 *   kind           declared                        holds outer instance?   typical use
 *   static nested  static class inside a class     no                      Builder, Node, Entry
 *   inner          class inside a class            YES                     Iterator over outer state
 *   local          class inside a method           yes (if method is non-static)   one-off helper
 *   anonymous      new X() { ... } expression      yes (if non-static context)    callbacks
 * </pre>
 */
public class NestedClassDemo {

    // ══ 1. Static nested class ══════════════════════════════════════════════
    /**
     * A static nested class is an ordinary class that is merely NAMESPACED inside
     * another one. It has no hidden reference to an outer instance.
     *
     * <p>Why it matters: the classic use is a Builder. The nested class and its
     * outer class can read each other's {@code private} members, so
     * {@code Pizza} can keep its constructor private while {@code Builder}
     * still creates it — callers are forced through the builder.
     *
     * <p>Complexity: creating one costs the same as any object; no extra memory
     * for an outer reference.
     *
     * <p>Gotcha: prefer {@code static} by default. Only drop it when the nested
     * class genuinely needs the outer instance.
     */
    public static class Pizza {
        private final String size;
        private final List<String> toppings;

        private Pizza(Builder builder) {                       // private: only Builder may call it
            this.size = builder.size;                          // ...and Pizza may read Builder's privates
            this.toppings = Collections.unmodifiableList(new ArrayList<>(builder.toppings));
        }

        public String size() { return size; }

        public List<String> toppings() { return toppings; }

        public static class Builder {
            private String size = "medium";
            private final List<String> toppings = new ArrayList<>();

            public Builder size(String size) {
                this.size = size;
                return this;
            }

            public Builder topping(String topping) {
                toppings.add(topping);
                return this;
            }

            public Pizza build() {
                return new Pizza(this);
            }
        }
    }

    public static void staticNestedDemo() {
        Pizza pizza = new Pizza.Builder().size("large").topping("ham").topping("olives").build();
        System.out.println("pizza size     = " + pizza.size());
        System.out.println("pizza toppings = " + pizza.toppings());
        System.out.println("Builder class  = " + Pizza.Builder.class.getName());
    }


    // ══ 2. Inner (non-static member) class ══════════════════════════════════
    /**
     * An inner class instance is BOUND to one instance of its outer class and can
     * use that instance's fields directly — even private ones.
     *
     * <p>Why it matters: it is the natural way to write an {@link Iterator} for a
     * collection-like class. The iterator needs the collection's state and has no
     * meaning without the collection.
     *
     * <p>Complexity: each inner instance stores one extra reference (to the outer).
     *
     * <p>Gotcha: that hidden reference keeps the outer object alive as long as the
     * inner one is reachable. Handing an inner-class instance to a long-lived
     * cache is a classic memory-leak pattern. If the inner class does not need
     * the outer instance, make it {@code static}.
     */
    public static class NumberRange implements Iterable<Integer> {
        private final int start;
        private final int endExclusive;

        public NumberRange(int start, int endExclusive) {
            this.start = start;
            this.endExclusive = endExclusive;
        }

        @Override
        public Iterator<Integer> iterator() {
            return new RangeIterator();            // implicit "this" is passed as the outer instance
        }

        private class RangeIterator implements Iterator<Integer> {
            private int current = start;           // reads the OUTER instance's private field

            @Override
            public boolean hasNext() {
                return current < endExclusive;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return current++;
            }
        }
    }

    public static void innerClassDemo() {
        StringBuilder out = new StringBuilder();
        for (int n : new NumberRange(3, 7)) {
            out.append(n).append(' ');
        }
        System.out.println("NumberRange(3, 7)    = " + out.toString().trim());
        Class<?> iteratorClass = new NumberRange(0, 1).iterator().getClass();
        System.out.println("iterator is static?  " + Modifier.isStatic(iteratorClass.getModifiers()));
    }


    // ══ 3. Outer.this and outer.new Inner() ═════════════════════════════════
    /**
     * Two pieces of syntax that only exist for inner classes.
     *
     * <ul>
     *   <li>{@code Outer.this.field} — reach the outer instance when a name is shadowed</li>
     *   <li>{@code outerInstance.new Inner()} — create an inner object from OUTSIDE the outer class</li>
     * </ul>
     *
     * <p>Why it matters: without {@code Outer.this}, a field in the inner class
     * with the same name silently hides the outer one.
     *
     * <p>Gotcha: {@code new Engine()} inside a {@code static} method does not
     * compile — there is no outer instance to bind to.
     */
    public static class Car {
        private final String label;
        private int km;

        public Car(String label) {
            this.label = label;
        }

        public void drive(int distance) {
            km += distance;
        }

        public Engine newEngine(String label) {
            return new Engine(label);              // inside Car: implicit this
        }

        public class Engine {
            private final String label;            // shadows Car.label

            public Engine(String label) {
                this.label = label;
            }

            public String describe() {
                return label + " engine of " + Car.this.label;   // Car.this = the enclosing Car
            }

            public int kmOnCar() {
                return km;                         // live view of the enclosing Car's state
            }
        }
    }

    public static void outerThisDemo() {
        Car tesla = new Car("Tesla");
        Car.Engine engine = tesla.new Engine("electric");     // outer.new Inner()
        tesla.drive(120);
        System.out.println(engine.describe());
        System.out.println("km seen by engine = " + engine.kmOnCar());
    }


    // ══ 4. Local class ══════════════════════════════════════════════════════
    /**
     * A class declared inside a method body, visible only there.
     *
     * <p>Why it matters: it names a one-off helper without polluting the file.
     * It can read the method's parameters and locals, but only if they are
     * effectively final (Java 8) — the class works on COPIES of them, so a later
     * reassignment would make the copy and the original disagree. The compiler
     * rejects that situation outright.
     *
     * <p>Gotcha: ❌ {@code prefix = "x";} anywhere in the method after the local
     * class captures {@code prefix} → compile error
     * "local variables referenced from an inner class must be final or effectively final".
     */
    public static List<String> greetAll(String prefix, List<String> names) {
        class Greeter {
            String greet(String name) {
                return prefix + ", " + name + "!";     // captures the parameter 'prefix'
            }
        }

        Greeter greeter = new Greeter();
        List<String> greetings = new ArrayList<>();
        for (String name : names) {
            greetings.add(greeter.greet(name));
        }
        return greetings;
    }

    public static void localClassDemo() {
        System.out.println(greetAll("Hello", Arrays.asList("Ann", "Bob")));
    }


    // ══ 5. Telling the kinds apart at runtime ═══════════════════════════════
    /**
     * Reflection can classify any class. Note the order of the checks:
     * anonymous and local classes also report an enclosing class, so they must be
     * tested BEFORE {@code isMemberClass()}.
     *
     * <p>Gotcha: nested enums and interfaces are implicitly {@code static}, so they
     * count as "static nested".
     */
    public static String kindOf(Class<?> type) {
        if (type.isAnonymousClass()) {
            return "anonymous";
        }
        if (type.isLocalClass()) {
            return "local";
        }
        if (type.isMemberClass()) {
            return Modifier.isStatic(type.getModifiers()) ? "static nested" : "inner";
        }
        return "top-level";
    }

    /** Returns the {@code Class} of a local class declared inside this method. */
    public static Class<?> localClassSample() {
        class Local { }
        return Local.class;
    }

    /** Returns the {@code Class} of an anonymous class created inside this method. */
    public static Class<?> anonymousClassSample() {
        return new Object() { }.getClass();
    }

    public static void kindsDemo() {
        System.out.println("NestedClassDemo         -> " + kindOf(NestedClassDemo.class));
        System.out.println("Pizza.Builder           -> " + kindOf(Pizza.Builder.class));
        System.out.println("Car.Engine              -> " + kindOf(Car.Engine.class));
        System.out.println("local class             -> " + kindOf(localClassSample()));
        System.out.println("anonymous class         -> " + kindOf(anonymousClassSample()));
        System.out.println("Car.Engine binary name  = " + Car.Engine.class.getName());
    }


    // ── Main — quick smoke run ───────────────────────────────────────────────
    public static void main(String[] args) {
        staticNestedDemo();
        System.out.println("---");
        innerClassDemo();
        System.out.println("---");
        outerThisDemo();
        System.out.println("---");
        localClassDemo();
        System.out.println("---");
        kindsDemo();
    }
}
