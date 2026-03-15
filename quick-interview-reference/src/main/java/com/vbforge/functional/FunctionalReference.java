package com.vbforge.functional;

import java.util.*;
import java.util.function.*;

/**
 * ═══════════════════════════════════════════════════════════════
 *  FUNCTIONAL PROGRAMMING INTERVIEW REFERENCE
 *  Topics: functional interfaces · lambda rules · method refs ·
 *          Optional · common built-in interfaces · gotchas
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class FunctionalReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. BUILT-IN FUNCTIONAL INTERFACES — cheat sheet
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Interface          │ Signature           │ Use case
     *  ───────────────────┼─────────────────────┼──────────────────────
     *  Predicate<T>       │ T → boolean         │ filter, test conditions
     *  Function<T,R>      │ T → R               │ map, transform
     *  Consumer<T>        │ T → void            │ forEach, side effects
     *  Supplier<T>        │ () → T              │ lazy evaluation, factories
     *  BiFunction<T,U,R>  │ T,U → R             │ combine two values
     *  BiPredicate<T,U>   │ T,U → boolean       │ test two values
     *  BiConsumer<T,U>    │ T,U → void          │ Map.forEach
     *  UnaryOperator<T>   │ T → T               │ map same type (extend Function)
     *  BinaryOperator<T>  │ T,T → T             │ reduce (extend BiFunction)
     *
     *  Primitive variants (avoid autoboxing):
     *  IntPredicate, IntFunction<R>, IntConsumer, IntSupplier
     *  LongPredicate, LongFunction<R>, DoubleFunction<R>, ...
     */
    static void builtInInterfaces() {
        Predicate<String>      notEmpty  = s -> !s.isEmpty();
        Function<String, Integer> length = String::length;
        Consumer<String>       printer   = System.out::println;
        Supplier<List<String>> newList   = ArrayList::new;

        // Composing functions
        Function<Integer, Integer> times2  = x -> x * 2;
        Function<Integer, Integer> plus10  = x -> x + 10;
        Function<Integer, Integer> both    = times2.andThen(plus10);  // first times2, then plus10
        Function<Integer, Integer> reverse = times2.compose(plus10);  // first plus10, then times2

        // Composing predicates
        Predicate<String> longEnough = s -> s.length() > 3;
        Predicate<String> startsWithA = s -> s.startsWith("A");
        Predicate<String> combined = longEnough.and(startsWithA);  // both must be true
        Predicate<String> either   = longEnough.or(startsWithA);   // at least one
        Predicate<String> negated  = longEnough.negate();
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. METHOD REFERENCE TYPES — all four forms
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Type                       │ Syntax                 │ Lambda equivalent
     *  ───────────────────────────┼────────────────────────┼──────────────────────
     *  Static method              │ ClassName::staticMethod│ x -> ClassName.staticMethod(x)
     *  Instance method (specific) │ instance::method       │ x -> instance.method(x)
     *  Instance method (arbitrary)│ ClassName::method      │ x -> x.method()
     *  Constructor                │ ClassName::new         │ x -> new ClassName(x)
     */
    static void methodReferences() {
        // Static method reference
        Function<String, Integer> parse1 = Integer::parseInt;          // x -> Integer.parseInt(x)
        Function<String, Integer> parse2 = s -> Integer.parseInt(s);   // same

        // Instance method on specific object
        String prefix = "Hello";
        Predicate<String> starts1 = prefix::startsWith;                // s -> prefix.startsWith(s)

        // Instance method on arbitrary object (first param is the receiver)
        Function<String, String> upper1 = String::toUpperCase;         // s -> s.toUpperCase()
        Predicate<String> empty1 = String::isEmpty;                    // s -> s.isEmpty()

        // Constructor reference
        Supplier<ArrayList<String>> newList = ArrayList::new;          // () -> new ArrayList<>()
        Function<Integer, int[]>    newArr  = int[]::new;              // n -> new int[n]
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. LAMBDA GOTCHAS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void lambdaGotchas() {
        // ⚠️ Lambda captures only EFFECTIVELY FINAL variables
        int x = 10;
        Runnable r = () -> System.out.println(x);
        // x = 20;  // ← COMPILE ERROR: makes x non-effectively-final

        // ⚠️ Workaround: use array or AtomicInteger for mutable capture
        int[] counter = {0};
        Runnable inc = () -> counter[0]++;   // array itself is effectively final
        // But this is NOT thread-safe — use AtomicInteger for concurrent use

        // ⚠️ this in lambda = enclosing class instance
        // ⚠️ this in anonymous class = the anonymous class instance
        // Lambdas do NOT create a new scope for 'this'

        // ⚠️ Lambda vs anonymous class — key differences:
        // Lambda: no new scope, no 'this' binding, no state, one abstract method only
        // Anonymous class: new scope, own 'this', can have state, can implement multiple methods
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. OPTIONAL — correct usage ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Optional<T> = a container that may or may not hold a non-null value.
     *  PURPOSE: return type for methods that may have no result.
     *           Force callers to handle the absent case explicitly.
     *
     *  ✅ USE Optional FOR: method return types that may have no result
     *  ❌ DON'T USE Optional FOR:
     *     - Method parameters (use overloading or null with @Nullable)
     *     - Fields (serialization issues, overhead)
     *     - Collection elements (use empty collection instead)
     */
    static void optionalUsage() {
        // Creation
        Optional<String> present = Optional.of("hello");       // throws NPE if null!
        Optional<String> nullable = Optional.ofNullable(null); // safe
        Optional<String> empty    = Optional.empty();

        // ✅ Preferred patterns
        String val1 = present.orElse("default");               // value or default
        String val2 = present.orElseGet(() -> computeValue()); // lazy default
        String val3 = present.orElseThrow(() -> new IllegalStateException("missing"));

        present.ifPresent(v -> System.out.println(v));         // only if present
        present.ifPresentOrElse(
            v -> System.out.println("got: " + v),
            () -> System.out.println("absent")                 // Java 9+
        );

        Optional<Integer> length = present.map(String::length);        // Optional<Integer>
        Optional<String>  upper  = present.map(String::toUpperCase);

        // flatMap for Optional-returning methods
        Optional<String> result = present.flatMap(s -> findSomething(s)); // avoids Optional<Optional<>>

        // filter
        Optional<String> longEnough = present.filter(s -> s.length() > 3);

        // ⚠️ ANTI-PATTERN: Optional.get() without check
        // present.get();          // safe here, but fragile — use orElse* instead
        // empty.get();            // NoSuchElementException!

        // ⚠️ ANTI-PATTERN: treating Optional like a null check
        if (present.isPresent()) {
            present.get();  // ❌ ugly — use map/orElse instead
        }
    }

    static Optional<String> findSomething(String s) { return Optional.of(s.toUpperCase()); }
    static String computeValue() { return "computed"; }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Optional as method parameter              → use overloading or @Nullable
     *  ❌ Optional as field                         → serialization issues
     *  ❌ Optional.get() without isPresent()        → NoSuchElementException
     *  ❌ Optional.of(value) with potentially null  → use ofNullable()
     *  ❌ Mutating captured variables in lambda     → effectively final rule violation
     *  ❌ Long lambda bodies                        → extract to named method
     */

    public static void main(String[] args) {
        builtInInterfaces();
        methodReferences();
        lambdaGotchas();
        optionalUsage();
    }
}
