package com.vbforge.practice.practice_9_bounded_types;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Method-level bound {@code <T extends Comparable<T>>}: findMax and clamp.
 * <p>
 * Bounded type parameter:
 *   - T is a specific type known at compile time
 *   - you can use T as a type (T result = ...) and return it
 *   - required when you need to refer to T later in the method
 * <p>
 * Contrast with the wildcard {@code <? extends Comparable<?>>}:
 *   - the type is unknown and cannot be named
 *   - use it when you only need the constraint, not the name
 */
public class ComparableBoundedMethodsDemo {

    public static void main(String[] args) {

        // ================== Scene 1: findMax ==================
        List<Integer> ints = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
        System.out.println("findMax (integers): " + findMax(ints));  // 9

        List<String> words = Arrays.asList("banana", "apple", "cherry");
        System.out.println("findMax (strings): " + findMax(words));  // cherry

        // ================== Scene 2: clamp ==================
        System.out.println("\n=== Bounded Type Parameters: clamp ===");
        System.out.println("clamp(5, 1, 10):   " + clamp(5, 1, 10));     // 5
        System.out.println("clamp(-5, 1, 10):  " + clamp(-5, 1, 10));    // 1
        System.out.println("clamp(99, 1, 10):  " + clamp(99, 1, 10));    // 10

        System.out.println("clamp('m','a','z'): " + clamp('m', 'a', 'z')); // m
        System.out.println("clamp('A','a','z'): " + clamp('A', 'a', 'z')); // a (A < a in Unicode)
    }

    // Find max in a list - T must be Comparable (bounded type parameter)
    public static <T extends Comparable<T>> T findMax(List<T> list) {
        if (list == null || list.isEmpty()) throw new NoSuchElementException("Empty list");
        T max = list.get(0);
        for (T item : list) {
            if (item.compareTo(max) > 0) max = item;
        }
        return max;
    }

    // Needs T to return a value of that type
    public static <T extends Comparable<T>> T clamp(T val, T min, T max) {
        if (val.compareTo(min) < 0) return min;
        if (val.compareTo(max) > 0) return max;
        return val;
    }
}
