package com.vbforge.practice.iterator.practice_16_modify_while_iterating;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Removal pitfalls during iteration — six scenes, from safe to explosive:
 *
 *   Scene 1: basic iterator + safe removal (IteratorExample)
 *   Scene 2: ListIterator.remove() — safe (ListModificationExamples.run1)
 *   Scene 3: Iterator + list.remove()   → CME (run2)
 *   Scene 4: for-each + list.remove()   → CME (run4)
 *   Scene 5: for-each CME + the two solutions — iterator.remove / removeIf
 *   Scene 6: iterator.remove() twice in a row → IllegalStateException
 */
public class RemovalPitfallsDemo {

    public static void main(String[] args) {
        basicIteratorSafeRemovalScene();
        listIteratorSafeRemovalScene();
        iteratorExternalRemoveCmeScene();
        forEachExternalRemoveCmeScene();
        forEachCmeWithSolutionsScene();
        doubleRemoveIllegalStateScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 — Iterator basics + safe removal
    // (was: IteratorExample)
    // ─────────────────────────────────────────────────────────────
    private static void basicIteratorSafeRemovalScene() {
        System.out.println("=== Scene 1: iterator basics + safe removal ===");

        List<String> fruits = new ArrayList<>();
        fruits.add("apple");
        fruits.add("banana");
        fruits.add("cherry");

        Iterator<String> iterator = fruits.iterator();

        System.out.println("Original list:");
        while (iterator.hasNext()) {
            String fruit = iterator.next();
            System.out.println(fruit);
        }

        // Remove elements that start with 'b'
        iterator = fruits.iterator(); // Reset iterator
        while (iterator.hasNext()) {
            String fruit = iterator.next();
            if (fruit.startsWith("b")) {
                iterator.remove(); // safe remove
            }
        }

        System.out.println("After removal by using iterator:");
        for (String fruit : fruits) {
            System.out.println(fruit);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2 — ListIterator.remove() — safe
    // ─────────────────────────────────────────────────────────────
    private static void listIteratorSafeRemovalScene() {
        System.out.println("\n=== Scene 2: ListIterator.remove() (safe) ===");

        List<String> list = new ArrayList<>(List.of("a", "b", "c", "d", "e"));

        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            String item = listIterator.next();
            if (item.equals("c")) {
                listIterator.remove(); // safe removal during iteration
            }
            System.out.print(item + " ");
        }
        System.out.println("\nModified list: " + list);
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 3 — Iterator + external list.remove() → CME
    // ─────────────────────────────────────────────────────────────
    private static void iteratorExternalRemoveCmeScene() {
        System.out.println("\n=== Scene 3: Iterator + list.remove() (CME) ===");

        List<String> list = new ArrayList<>(List.of("a", "b", "c", "d", "e"));

        try {
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                String item = iterator.next();
                if (item.equals("c")) {
                    list.remove(2); // direct removal, causes exception
                }
                System.out.print(item + " ");
            }
            System.out.println("\nModified list (unreachable): " + list);
        } catch (ConcurrentModificationException e) {
            System.out.println("\nCaught CME — external list.remove() during iterator traversal");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 4 — for-each + external list.remove() → CME
    // ─────────────────────────────────────────────────────────────
    private static void forEachExternalRemoveCmeScene() {
        System.out.println("\n=== Scene 4: for-each + list.remove() (CME) ===");

        List<String> list = new ArrayList<>(List.of("a", "b", "c", "d", "e"));

        try {
            for (String item : list) {
                System.out.println(item);
                if ("b".equals(item)) {
                    list.remove(1); // modification during for-each
                }
            }
            System.out.println("List (unreachable): " + list);
        } catch (ConcurrentModificationException e) {
            System.out.println("Caught CME — for-each is an Iterator underneath");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 5 — for-each CME + the two canonical solutions
    // (was: PitfallsWhileModifyingDuringIteration)
    // ─────────────────────────────────────────────────────────────
    private static void forEachCmeWithSolutionsScene() {
        System.out.println("\n=== Scene 5: for-each CME + solutions ===");

        List<String> fruits = new ArrayList<>();
        fruits.add("apple");
        fruits.add("banana");
        fruits.add("cherry");
        fruits.add("orange");
        fruits.add("pineapple");
        fruits.add("watermelon");

        System.out.println("Before removing: " + fruits);

        System.out.println("\nExpected Pitfall: ConcurrentModificationException");
        System.out.println("(uncomment to see the exception)");
        /*for (String fruit : fruits) {
            if(fruit.equals("orange")){
                fruits.remove(fruit);  // ConcurrentModificationException
            }
        }*/

        System.out.println("\nSolution 1: Correct way to remove while using iterator");
        Iterator<String> iterator = fruits.iterator();
        while (iterator.hasNext()) {
            String fruit = iterator.next();
            if (fruit.equals("orange")) {
                iterator.remove();
            }
        }

        System.out.println("\nSolution 2: Collection.removeIf is used");
        fruits.removeIf(fruit -> fruit.equals("pineapple"));

        System.out.println("\nAfter removing: " + fruits);
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 6 — remove() twice without next() → IllegalStateException
    // (was: PitfallsWhileRemovingByIterator)
    // ─────────────────────────────────────────────────────────────
    private static void doubleRemoveIllegalStateScene() {
        System.out.println("\n=== Scene 6: remove() twice → IllegalStateException ===");

        //Using remove() twice in a row without calling next():
        //iterator.remove(); // only once is correct for current next value
        //iterator.remove(); // AGAIN → BOOM(!!!): IllegalStateException

        List<Integer> integerList = new LinkedList<>();
        integerList.add(1);
        integerList.add(2);
        integerList.add(3);
        integerList.add(4);
        integerList.add(5);

        System.out.println("Before removing: " + integerList);

        try {
            Iterator<Integer> integerIterator = integerList.iterator();
            while (integerIterator.hasNext()) {
                Integer element1 = integerIterator.next();
                if (element1.equals(2)) {
                    integerIterator.remove();
                    integerIterator.remove(); // IllegalStateException
                }
            }
            System.out.println("After removing (unreachable): " + integerList);
        } catch (IllegalStateException e) {
            System.out.println("Caught IllegalStateException — remove() called twice");
        }
    }
}
