package com.vbforge.practice.iterator.practice_16_modify_while_iterating;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Fail-fast vs fail-safe iterators — three scenes that make the difference
 * concrete:
 *
 *   Scene 1 (fail-fast, for-each): ArrayList iterator notices external
 *           structural change on the next next() → CME.
 *
 *   Scene 2 (fail-fast, iterator): the SAME list, but removing through
 *           iterator.remove() is legal — the iterator's own modCount is
 *           kept in sync, so no CME.
 *
 *   Scene 3 (fail-safe): CopyOnWriteArrayList captures a snapshot at
 *           iterator() time. Removing the live list during for-each does
 *           NOT trip CME, because the iterator is iterating the snapshot.
 */
public class FailFastAndFailSafeDemo {

    public static void main(String[] args) {
        failFastForEachScene();
        failFastIteratorScene();
        failSafeCopyOnWriteScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 — fail-fast, modified during for-each
    // ─────────────────────────────────────────────────────────────
    private static void failFastForEachScene() {
        System.out.println("=== Scene 1: fail-fast for-each ===");

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        System.out.println("Before remove: ");
        //iterating to see list values by iterator:
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer value = iterator.next();
            System.out.print(value + " ");
        }
        System.out.println();

        // a fail-fast case is occurring during for-each loop and modification: ConcurrentModificationException
        try {
            for (Integer i : list) {
                System.out.print(i + " ");
                if (i == 3) {
                    list.remove(i); // ConcurrentModificationException
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("\nCME as expected — for-each + list.remove()");
        }

        //never happen while an exception occurs
        System.out.println("After remove (unreachable in the CME path): " + list);
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2 — fail-fast, but safe removal via iterator
    // ─────────────────────────────────────────────────────────────
    private static void failFastIteratorScene() {
        System.out.println("\n=== Scene 2: fail-fast iterator (safe removal) ===");

        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");

        System.out.println("Before remove: " + list);

        Iterator<String> stringIterator = list.iterator();
        while (stringIterator.hasNext()) {
            String value = stringIterator.next();
            if (value.equals("c")) {
//                list.remove(value); // ConcurrentModificationException
                stringIterator.remove(); // to avoid the exception need to remove from iterator not form the collection
            }
        }

        System.out.println("After remove: " + list);
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 3 — fail-safe (CopyOnWriteArrayList)
    // ─────────────────────────────────────────────────────────────
    private static void failSafeCopyOnWriteScene() {
        System.out.println("\n=== Scene 3: fail-safe CopyOnWriteArrayList ===");

        // Initialize a thread-safe, fail-safe list
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");

        // Create iterator to traverse the list
        Iterator<String> stringIterator = list.iterator();

        // Iteration over the list during which the list is modified
        while (stringIterator.hasNext()) {
            String value = stringIterator.next();
            System.out.println(value);
        }

        System.out.println("-------------------------------");

        // Using enhanced for-loop (internally uses iterator)
        for (String item : list) {
            System.out.println(item);
            if ("c".equals(item)) {
                // Remove "c" during iteration
                list.remove("c");
            }
        }

        // Show the list after modification
        System.out.println("Final list: " + list);
    }
}
