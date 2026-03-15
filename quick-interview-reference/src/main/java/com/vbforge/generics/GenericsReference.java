package com.vbforge.generics;

import java.util.*;
import java.util.function.*;

/**
 * ═══════════════════════════════════════════════════════════════
 *  GENERICS INTERVIEW REFERENCE
 *  Topics: type erasure · wildcards · bounded types ·
 *          PECS rule · common gotchas
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class GenericsReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. TYPE ERASURE ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Generic type parameters are ERASED at compile time → replaced with Object (or bound).
     *  At runtime, List<String> and List<Integer> are BOTH just List.
     *
     *  CONSEQUENCES:
     *  - Cannot use instanceof with parameterized types:  obj instanceof List<String>  ← COMPILE ERROR
     *  - Cannot create generic arrays:  new T[]  ← COMPILE ERROR
     *  - Cannot use primitives as type params:  List<int>  ← COMPILE ERROR (use Integer)
     *  - Overloading based on generic type alone is not possible (same erasure)
     *
     *  ⚠️ Heap pollution: unchecked cast warning = potential ClassCastException at runtime
     */
    static void typeErasureGotchas() {
        List<String> strings = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();

        // Both are just List at runtime
        System.out.println(strings.getClass() == integers.getClass()); // true!

        // ❌ Cannot check parameterized type
        Object obj = List.of("a","b");
        // if (obj instanceof List<String>) {}   // COMPILE ERROR

        // ✅ Can check raw type only
        if (obj instanceof List<?> list) {       // wildcard — safe
            System.out.println("is a list");
        }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. WILDCARDS & PECS RULE ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  PECS = Producer Extends, Consumer Super
     *
     *  ? extends T (upper bound):
     *  ✅ READ from it (producer — provides T values)
     *  ❌ WRITE to it (unknown exact subtype — unsafe)
     *  USE WHEN: method reads from collection (copy FROM)
     *
     *  ? super T (lower bound):
     *  ✅ WRITE T to it (consumer — accepts T or supertype)
     *  ❌ READ precisely (returns Object)
     *  USE WHEN: method writes to collection (copy TO)
     *
     *  ? (unbounded):
     *  Only safe operations: read as Object, check size, clear
     */

    // Producer Extends: reads Numbers FROM list
    static double sum(List<? extends Number> list) {
        double total = 0;
        for (Number n : list) { total += n.doubleValue(); }
        // list.add(1);  // ❌ Cannot add — don't know exact subtype
        return total;
    }

    // Consumer Super: writes Numbers INTO list
    static void fillWithZeros(List<? super Integer> list, int count) {
        for (int i = 0; i < count; i++) { list.add(0); }
        // Integer n = list.get(0);  // ❌ Returns Object, not Integer
    }

    static void wildcardUsage() {
        List<Integer> ints    = List.of(1, 2, 3);
        List<Double>  doubles = List.of(1.1, 2.2);

        sum(ints);     // ✅ Integer extends Number
        sum(doubles);  // ✅ Double extends Number

        List<Number> numbers = new ArrayList<>();
        fillWithZeros(numbers, 3);  // ✅ Number super Integer

        List<Object> objects = new ArrayList<>();
        fillWithZeros(objects, 3);  // ✅ Object super Integer
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. GENERIC METHODS & BOUNDED TYPES
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // T extends Comparable<T> means T must implement Comparable
    static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    // Multiple bounds — T must extend Base AND implement Serializable
    static <T extends Number & java.io.Serializable> void process(T value) { }

    // Generic class with type bound
    static class Pair<A, B> {
        final A first; final B second;
        Pair(A first, B second) { this.first = first; this.second = second; }
        @Override public String toString() { return "(" + first + ", " + second + ")"; }

        // Static factory — type inferred from arguments
        static <X, Y> Pair<X, Y> of(X x, Y y) { return new Pair<>(x, y); }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Using raw types (List instead of List<T>)    → heap pollution, unchecked warnings
     *  ❌ Unchecked casts (List<String>)(Object)list  → ClassCastException at runtime
     *  ❌ extends when you need to write               → can't add to ? extends T
     *  ❌ super when you need to read precisely        → only gets Object back
     *  ❌ Generic array creation new T[]               → compile error — use List<T>
     */

    public static void main(String[] args) {
        typeErasureGotchas();
        wildcardUsage();
        System.out.println(max(3, 7));                  // 7
        System.out.println(Pair.of("hello", 42));       // (hello, 42)
    }
}
