package com.vbforge.generics_fundamentals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Demonstrates generic classes and interfaces: type parameters, multiple type
 * parameters, generic interfaces, the generic-array gotcha, type erasure and raw types.
 *
 * <p>Each section isolates one idea so it can be read (and tested) on its own.
 * The supporting types ({@link Box}, {@link Pair}, {@link SimpleStack}, {@link Converter})
 * are public nested classes so that {@link GenericClassesTasks} and the tests can reuse them.
 */
public class GenericClassesDemo {

    // ── 1. Why generics? ───────────────────────────────────────────────────────
    /*
     * WHAT:  Generics let a class or method take a *type* as a parameter, written <T>.
     * WHY:   Before Java 5 collections stored Object. The compiler could not stop you from
     *        putting a String into a "list of Integers", and the error showed up later as a
     *        ClassCastException at the point of READING — far from the bug.
     *        Generics move that error to COMPILE time and remove the manual casts.
     * COST:  Zero at runtime — generics are erased by the compiler (see section 5).
     * GOTCHA: A generic type is only as safe as its weakest caller; raw types switch the
     *         protection off again.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String readSecondAsStringFromRawList() {
        List untyped = new ArrayList();
        untyped.add("text");
        untyped.add(42);                           // compiles — nothing stops us
        return (String) untyped.get(1);            // ClassCastException at runtime: the bug the compiler could not see
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void withoutGenericsDemo() {
        List untyped = new ArrayList();            // raw type: elements are just Object
        untyped.add("text");
        untyped.add(42);                           // compiles — nothing stops us

        String first = (String) untyped.get(0);    // manual cast required
        System.out.println("first (cast by hand): " + first);
        try {
            readSecondAsStringFromRawList();
        } catch (ClassCastException e) {
            System.out.println("raw list: wrong cast only fails at runtime");
        }

        List<String> typed = new ArrayList<>();    // diamond <>: type arguments inferred (Java 7+)
        typed.add("text");
        // typed.add(42);                          // ← compile error: that is the whole point
        String safe = typed.get(0);                // no cast needed
        System.out.println("first (compiler-checked): " + safe);
    }


    // ── 2. Generic classes: Box<T> and Pair<A, B> ──────────────────────────────
    /*
     * WHAT:  A class with one or more type parameters: class Box<T>, class Pair<A, B>.
     * WHY:   One implementation works for every type, and each use site gets full type safety
     *        (Box<String> and Box<Integer> are different types to the compiler).
     * NAMING: T = Type, E = Element, K/V = Key/Value, A/B = generic pairs, R = Result.
     * GOTCHA: Type parameters exist only for INSTANCE members. A static field or static
     *         method cannot use the class's T — a static generic method declares its own <T>
     *         (see GenericMethodsDemo).
     */

    /** A container that holds at most one non-null value. */
    public static class Box<T> {
        private final T value;

        private Box(T value) {
            this.value = value;
        }

        /** Static factory — declares its own {@code <T>} because statics cannot see the class's T. */
        public static <T> Box<T> of(T value) {
            return new Box<>(Objects.requireNonNull(value, "value must not be null"));
        }

        public static <T> Box<T> empty() {
            return new Box<>(null);
        }

        public boolean isPresent() {
            return value != null;
        }

        public T get() {
            if (value == null) {
                throw new NoSuchElementException("Box is empty");
            }
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Box)) return false;
            return Objects.equals(value, ((Box<?>) o).value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return isPresent() ? "Box[" + value + "]" : "Box[empty]";
        }
    }

    /** An immutable pair of two values whose types may differ. */
    public static final class Pair<A, B> {
        private final A first;
        private final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false;
            Pair<?, ?> other = (Pair<?, ?>) o;
            return Objects.equals(first, other.first) && Objects.equals(second, other.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }

    static void boxAndPairDemo() {
        Box<String> name = Box.of("Vlad");
        String value = name.get();                     // no cast: the compiler knows it is a String
        System.out.println("box: " + name + " -> " + value);

        Box<Integer> none = Box.empty();               // target typing infers T = Integer
        System.out.println("empty box present? " + none.isPresent());

        Pair<String, Integer> entry = new Pair<>("age", 30);   // two different types in one object
        System.out.println("pair: " + entry);
        // Pair<String, Integer> wrong = new Pair<>(30, "age");  // ← compile error
    }


    // ── 3. A generic container with an internal array ──────────────────────────
    /*
     * WHAT:  SimpleStack<T> — a LIFO stack backed by an array that grows when full.
     * WHY:   Shows the one place where generics and arrays collide.
     * GOTCHA: "new T[n]" does not compile, because T does not exist at runtime (erasure).
     *         The usual workaround is to store Object[] internally and cast on the way OUT,
     *         keeping the unchecked cast private so callers can never corrupt the stack.
     * COMPLEXITY:
     *   - push: amortised O(1) (occasional O(n) copy when the array doubles)
     *   - pop / peek / size: O(1)
     *   - space: O(n)
     */
    public static class SimpleStack<T> {
        private Object[] elements = new Object[2];   // ← new T[2] would NOT compile
        private int size;

        public void push(T item) {
            if (size == elements.length) {
                elements = Arrays.copyOf(elements, size * 2);
            }
            elements[size++] = item;
        }

        @SuppressWarnings("unchecked")                // safe: only push(T) can put things in
        public T pop() {
            if (size == 0) {
                throw new NoSuchElementException("stack is empty");
            }
            T item = (T) elements[--size];
            elements[size] = null;                    // do not keep a reference alive (memory leak)
            return item;
        }

        @SuppressWarnings("unchecked")
        public T peek() {
            if (size == 0) {
                throw new NoSuchElementException("stack is empty");
            }
            return (T) elements[size - 1];
        }

        public int size() {
            return size;
        }

        public boolean isEmpty() {
            return size == 0;
        }
    }

    static void stackDemo() {
        SimpleStack<String> stack = new SimpleStack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");                              // triggers an internal resize (capacity 2 -> 4)
        System.out.println("peek: " + stack.peek() + ", size: " + stack.size());
        System.out.println("pop: " + stack.pop() + ", " + stack.pop() + ", " + stack.pop());
        System.out.println("empty now? " + stack.isEmpty());
    }


    // ── 4. Generic interfaces ──────────────────────────────────────────────────
    /*
     * WHAT:  An interface with type parameters: Converter<F, T>.
     * WHY:   Describes a contract once; implementers either FIX the type arguments
     *        (class StringLength implements Converter<String, Integer>) or stay generic
     *        themselves. This is exactly how Comparable<T>, Comparator<T>, Function<T, R>
     *        and Supplier<T> are defined in the JDK.
     * GOTCHA: A class cannot implement the same generic interface twice with different
     *         arguments (Comparable<A> and Comparable<B>) — erasure would make them clash.
     */
    public interface Converter<F, T> {
        T convert(F from);
    }

    /** Fixes both type arguments: converts String → Integer. */
    public static class StringLength implements Converter<String, Integer> {
        @Override
        public Integer convert(String from) {
            return from.length();
        }
    }

    static void genericInterfaceDemo() {
        Converter<String, Integer> byClass = new StringLength();
        Converter<String, Integer> byLambda = s -> s.length() * 2;   // a single-method interface fits a lambda (Java 8)
        System.out.println("class:  " + byClass.convert("generics"));
        System.out.println("lambda: " + byLambda.convert("generics"));
    }


    // ── 5. Type erasure and raw types ──────────────────────────────────────────
    /*
     * WHAT:  The compiler checks generics, then ERASES them: List<String> and List<Integer>
     *        become plain List in bytecode, T becomes its bound (Object if unbounded),
     *        and casts are inserted where values are read.
     * WHY:   Backward compatibility — generic code can run on pre-Java-5 libraries.
     * CONSEQUENCES (what you cannot do):
     *   - new T(), new T[n], T.class
     *   - x instanceof List<String>  (only instanceof List<?> is legal)
     *   - overload m(List<String>) and m(List<Integer>) — same erasure
     * GOTCHA: A raw type can smuggle the wrong type into a typed list ("heap pollution").
     *         Nothing fails at the insertion; the ClassCastException appears later, when
     *         the compiler-inserted cast runs on READ.
     */
    public static boolean sameRuntimeClass() {
        List<String> strings = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();
        return strings.getClass() == numbers.getClass();    // true: both are just ArrayList
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addThroughRawType(List list, Object item) {
        list.add(item);                                      // unchecked: compiler cannot protect us here
    }

    /** Returns the first element of a {@code List<String>} that was polluted with an Integer. */
    public static String readFromPollutedList() {
        List<String> strings = new ArrayList<>();
        addThroughRawType(strings, 42);                      // insertion succeeds silently…
        return strings.get(0);                               // …the inserted cast to String fails HERE
    }

    static void typeErasureDemo() {
        System.out.println("List<String> and List<Integer> share a class? " + sameRuntimeClass());
        try {
            readFromPollutedList();
        } catch (ClassCastException e) {
            System.out.println("heap pollution detected on read: ClassCastException");
        }
    }


    // ── Main — runs every section ──────────────────────────────────────────────
    public static void main(String[] args) {
        withoutGenericsDemo();
        System.out.println("---");
        boxAndPairDemo();
        System.out.println("---");
        stackDemo();
        System.out.println("---");
        genericInterfaceDemo();
        System.out.println("---");
        typeErasureDemo();
    }
}
