package com.vbforge.generics_fundamentals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Demonstrates wildcards: invariance, {@code ? extends} (producers), {@code ? super}
 * (consumers), the PECS rule, unbounded {@code ?} with wildcard capture, and
 * {@code Comparable<? super T>}.
 */
public class WildcardsDemo {

    /** Small hierarchy used by several sections: Apple and Orange are Fruits, Fruits compare by weight. */
    public static class Fruit implements Comparable<Fruit> {
        private final String name;
        private final int grams;

        public Fruit(String name, int grams) {
            this.name = name;
            this.grams = grams;
        }

        public String getName() {
            return name;
        }

        public int getGrams() {
            return grams;
        }

        @Override
        public int compareTo(Fruit other) {
            return Integer.compare(grams, other.grams);
        }

        @Override
        public String toString() {
            return name + "(" + grams + "g)";
        }
    }

    public static class Apple extends Fruit {
        public Apple(int grams) {
            super("apple", grams);
        }
    }

    public static class Orange extends Fruit {
        public Orange(int grams) {
            super("orange", grams);
        }
    }


    // ── 1. Invariance — why wildcards exist ────────────────────────────────────
    /*
     * WHAT:  Generics are INVARIANT: List<Apple> is NOT a List<Fruit>, even though Apple is a Fruit.
     * WHY:   If it were, this would compile and break type safety:
     *            List<Apple> apples = new ArrayList<>();
     *            List<Fruit> fruits = apples;       // ← compile error (luckily)
     *            fruits.add(new Orange(150));       // an Orange inside a List<Apple>!
     * CONTRAST: Arrays ARE covariant (Apple[] is a Fruit[]), so the same mistake compiles and
     *        fails at runtime with ArrayStoreException. Generics catch it at compile time.
     * SOLUTION: Wildcards describe a FAMILY of types: List<? extends Fruit> accepts
     *        List<Apple>, List<Orange> and List<Fruit> — at the price of restricted operations.
     */
    public static void storeIntoCovariantArray() {
        Fruit[] fruits = new Apple[1];               // allowed: arrays are covariant
        fruits[0] = new Orange(150);                 // compiles, throws ArrayStoreException at runtime
    }

    static void invarianceDemo() {
        try {
            storeIntoCovariantArray();
        } catch (ArrayStoreException e) {
            System.out.println("arrays: ArrayStoreException at runtime");
        }
        List<Apple> apples = new ArrayList<>();
        // List<Fruit> fruits = apples;               // ← compile error: generics are invariant
        List<? extends Fruit> view = apples;           // ✓ a read-only view of "some kind of Fruit"
        System.out.println("generics: wildcard view of apples, size " + view.size());
    }


    // ── 2. Upper-bounded wildcard: ? extends T (producer) ──────────────────────
    /*
     * WHAT:  Collection<? extends Number> = a collection of SOME unknown subtype of Number.
     * WHY:   Lets one method accept List<Integer>, List<Double>, Set<BigDecimal>, …
     *        Reading is safe: whatever the element really is, it IS a Number.
     * RULE:  You can READ elements as the bound, but you cannot ADD (except null),
     *        because the compiler does not know which subtype the list really holds
     *        (it could be a List<Double> — adding an Integer would corrupt it).
     * GOTCHA: "? extends" is for things that PRODUCE values for you.
     */
    public static double sum(Collection<? extends Number> numbers) {
        double total = 0;
        for (Number n : numbers) {                   // reading as Number is always safe
            total += n.doubleValue();
        }
        return total;
    }

    /** Adding is rejected for everything except null — the only value valid for every subtype. */
    public static int addNullToExtendsList() {
        List<? extends Number> numbers = new ArrayList<Integer>();
        // numbers.add(1);                           // ← compile error: capture of ? extends Number
        numbers.add(null);                           // ✓ null belongs to every type
        return numbers.size();
    }

    static void extendsDemo() {
        List<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        List<Double> doubles = new ArrayList<>();
        doubles.add(0.5);
        System.out.println("sum of Integers: " + sum(ints));
        System.out.println("sum of Doubles:  " + sum(doubles));
        System.out.println("size after adding null: " + addNullToExtendsList());
    }


    // ── 3. Lower-bounded wildcard: ? super T (consumer) ────────────────────────
    /*
     * WHAT:  List<? super Apple> = a list of SOME unknown SUPERTYPE of Apple
     *        (List<Apple>, List<Fruit>, List<Object>).
     * WHY:   A method that only WRITES Apples can accept every list that can hold them.
     * RULE:  You can ADD an Apple (and its subclasses) safely; what you READ back is only
     *        an Object, because the real element type could be anything above Apple.
     * GOTCHA: "? super" is for things that CONSUME values you hand them.
     */
    public static void addApples(List<? super Apple> basket, int count) {
        for (int i = 0; i < count; i++) {
            basket.add(new Apple(100 + i));          // safe: an Apple fits in every list above
        }
        // Apple first = basket.get(0);              // ← compile error: only Object comes out
        Object first = basket.isEmpty() ? null : basket.get(0);   // reading yields Object
        if (first != null) {
            first.toString();
        }
    }

    static void superDemo() {
        List<Fruit> fruits = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        addApples(fruits, 2);
        addApples(objects, 1);
        System.out.println("fruits: " + fruits);
        System.out.println("objects: " + objects);
    }


    // ── 4. PECS: Producer Extends, Consumer Super ──────────────────────────────
    /*
     * WHAT:  A mnemonic for choosing the wildcard: the SOURCE produces (extends),
     *        the DESTINATION consumes (super).
     * WHY:   copyInto() works for every compatible pair — Apple → Fruit, Integer → Number,
     *        Fruit → Object — which a plain copy(List<T>, List<T>) cannot do.
     *        java.util.Collections.copy has exactly this shape.
     * GOTCHA: If a parameter both reads and writes, use NO wildcard (List<T>).
     */
    public static <T> void copyInto(List<? super T> destination, List<? extends T> source) {
        for (T item : source) {                      // read from the producer
            destination.add(item);                   // write to the consumer
        }
    }

    static void pecsDemo() {
        List<Apple> apples = new ArrayList<>();
        apples.add(new Apple(120));
        List<Fruit> fruits = new ArrayList<>();
        fruits.add(new Orange(200));

        copyInto(fruits, apples);                    // T = Apple: List<Fruit> accepts Apples
        System.out.println("fruits after copy: " + fruits);
    }


    // ── 5. Unbounded wildcard ? and wildcard capture ───────────────────────────
    /*
     * WHAT:  List<?> = a list of something, I do not care what.
     * WHY:   For code that never needs the element type: size, isEmpty, printing, clear.
     *        It is NOT the same as List<Object>: List<Object> accepts only List<Object>,
     *        List<?> accepts a list of anything.
     * CAPTURE: To WRITE into a List<?> (swap, reverse, shuffle) delegate to a private generic
     *        helper. The compiler "captures" the unknown ? as a named type E for the call.
     * GOTCHA: Without the helper, list.set(0, list.get(1)) does not compile —
     *        the compiler cannot prove the two unknown types are the same.
     */
    public static String describe(List<?> list) {
        return list.size() + " element(s), first: " + (list.isEmpty() ? "-" : String.valueOf(list.get(0)));
    }

    public static void swapFirstAndLast(List<?> list) {
        swapFirstAndLastHelper(list);                // capture: ? becomes E
    }

    private static <E> void swapFirstAndLastHelper(List<E> list) {
        if (list.size() < 2) {
            return;
        }
        int last = list.size() - 1;
        E temp = list.get(0);
        list.set(0, list.get(last));
        list.set(last, temp);
    }

    static void unboundedDemo() {
        List<String> words = new ArrayList<>();
        words.add("a");
        words.add("b");
        words.add("c");
        System.out.println(describe(words));
        swapFirstAndLast(words);
        System.out.println("after swap: " + words);
    }


    // ── 6. Comparable<? super T> ───────────────────────────────────────────────
    /*
     * WHAT:  <T extends Comparable<? super T>> — "T can be compared with T OR with any of
     *        T's supertypes".
     * WHY:   Apple does not implement Comparable<Apple>; it inherits Comparable<Fruit>.
     *        BoundedTypesDemo.maxOf (T extends Comparable<T>) REJECTS Apples,
     *        while this version accepts them. This is the signature used by
     *        Collections.max, Collections.sort and List.sort.
     * GOTCHA: Prefer Comparable<? super T> over Comparable<T> in library-style code.
     */
    public static <T extends Comparable<? super T>> T maxOf(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    static void comparableSuperDemo() {
        Apple heavier = maxOf(new Apple(120), new Apple(180));
        // BoundedTypesDemo.maxOf(new Apple(120), new Apple(180));   // ← compile error: Apple is not Comparable<Apple>
        System.out.println("heavier apple: " + heavier);
    }


    // ── Main — runs every section ──────────────────────────────────────────────
    public static void main(String[] args) {
        invarianceDemo();
        System.out.println("---");
        extendsDemo();
        System.out.println("---");
        superDemo();
        System.out.println("---");
        pecsDemo();
        System.out.println("---");
        unboundedDemo();
        System.out.println("---");
        comparableSuperDemo();
    }
}
