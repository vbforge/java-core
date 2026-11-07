package com.vbforge.practice.list.practice_12_linkedlist_standard_api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Iterating a standard {@link LinkedList} — five idiom variants plus the
 * bidirectional {@link ListIterator} demo on a Person domain.
 */
public class LinkedListIterationDemo {

    public static void main(String[] args) {
        iterationStylesScene();
        bidirectionalPersonScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1: five iteration styles
    // ─────────────────────────────────────────────────────────────
    private static void iterationStylesScene() {
        System.out.println("=== Scene 1: iteration styles ===");

        LinkedList<String> list = new LinkedList<>(List.of("A", "B", "C", "D"));

        // For loop
        for (int i = 0; i < list.size(); i++) {
            System.out.println("Index " + i + ": " + list.get(i));
        }

        // Enhanced for
        for (String item : list) {
            System.out.println("Enhanced for: " + item);
        }

        // Iterator
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println("Iterator: " + iterator.next());
        }

        // ListIterator (bi-directional)
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            System.out.println("ListIterator forward: " + listIterator.next());
        }
        while (listIterator.hasPrevious()) {
            System.out.println("ListIterator backward: " + listIterator.previous());
        }

        // ForEach method (Java 8+)
        list.forEach(item -> System.out.println("ForEach lambda: " + item));
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2: bidirectional ListIterator on a Person domain
    // ─────────────────────────────────────────────────────────────
    private static void bidirectionalPersonScene() {
        System.out.println("\n=== Scene 2: bidirectional ListIterator (Person) ===");

        LinkedList<Person> persons = new LinkedList<>();

        persons.add(new Person("John", 35));
        persons.add(new Person("Gustav", 31));
        persons.addFirst(new Person("Adam", 24));
        persons.addLast(new Person("Tom", 21));

        fromFirstToLast(persons);

        System.out.println("=====");

        fromLastToFirst(persons);
    }

    private static void fromFirstToLast(List<Person> persons) {
        System.out.println("---iterating from first to last---");

        ListIterator<Person> personsListIterator = persons.listIterator();

        while (personsListIterator.hasNext()) {
            System.out.println(personsListIterator.next());
        }
    }

    private static void fromLastToFirst(List<Person> persons) {
        System.out.println("---iterating from last to first---");

        ListIterator<Person> personsListIterator = persons.listIterator(persons.size());

        while (personsListIterator.hasPrevious()) {
            System.out.println(personsListIterator.previous());
        }
    }
}
