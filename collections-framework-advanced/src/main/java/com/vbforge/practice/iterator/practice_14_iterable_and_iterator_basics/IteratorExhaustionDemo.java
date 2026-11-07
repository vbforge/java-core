package com.vbforge.practice.iterator.practice_14_iterable_and_iterator_basics;

import java.util.*;

/**
 * Deep dive into Java iteration mechanisms.
 *
 * Three layers of iteration in Java:
 *
 * 1. Iterable<T>  — any class implementing this can be used in for-each
 *    └─ iterator() → Iterator<T>
 *
 * 2. Iterator<T>  — cursor-based traversal protocol
 *    ├─ hasNext() → boolean
 *    ├─ next()    → T       (advances cursor)
 *    └─ remove()  → void    (removes last element returned by next())
 *
 * 3. ListIterator<T>  — bidirectional; only for List
 *    ├─ hasPrevious() / previous()
 *    ├─ nextIndex() / previousIndex()
 *    ├─ add(e) / set(e)
 *    └─ remove()
 *
 * Fail-fast vs Fail-safe:
 *   - Fail-fast (ArrayList, HashMap, etc.): ConcurrentModificationException on
 *     structural modification during iteration. Uses modCount tracking.
 *   - Fail-safe (CopyOnWriteArrayList, ConcurrentHashMap): operate on a snapshot
 *     or tolerate concurrent modification; no CME.
 *
 * Enhanced for-each desugaring:
 *   for (T e : collection) { body }
 *   → Iterator<T> it = collection.iterator();
 *     while (it.hasNext()) { T e = it.next(); body }
 */
public class IteratorExhaustionDemo {

    // ─────────────────────────────────────────────────────────────
    // 1. Iterator fundamentals
    // ─────────────────────────────────────────────────────────────

    /**
     * Direct Iterator usage — necessary when:
     *   a) Removing during iteration (iterator.remove())
     *   b) Need to interleave other logic between next() calls
     */
    public static void iteratorBasics() {
        System.out.println("=== Iterator Basics ===");

        List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e"));
        Iterator<String> it = list.iterator();

        System.out.print("Explicit iteration: ");
        while (it.hasNext()) {
            String s = it.next();
            System.out.print(s + " ");
            if (s.equals("c")) {
                it.remove();  // safe removal via iterator
            }
        }
        System.out.println("\nAfter removing 'c': " + list);  // [a, b, d, e]

        // next() on exhausted iterator throws NoSuchElementException
        try {
            it.next();
        } catch (NoSuchElementException e) {
            System.out.println("NoSuchElementException after exhaustion — as expected");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. ListIterator — bidirectional iteration
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 3. Custom Iterable — implementing your own
    // ─────────────────────────────────────────────────────────────

    /**
     * Any class can be made for-each compatible by implementing Iterable<T>.
     * Must return an Iterator<T> from iterator().
     *
     * This is how Collections, arrays (via Arrays.asList), and Streams work.
     */
    static class NumberRange implements Iterable<Integer> {
        private final int start;
        private final int end;   // inclusive

        NumberRange(int start, int end) {
            this.start = start;
            this.end   = end;
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<>() {
                int current = start;

                @Override
                public boolean hasNext() { return current <= end; }

                @Override
                public Integer next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    return current++;
                }
            };
        }
    }


    // ─────────────────────────────────────────────────────────────
    // 4. Fail-fast vs Fail-safe
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 5. Map iteration patterns
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // main
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        iteratorBasics();
    }
}
