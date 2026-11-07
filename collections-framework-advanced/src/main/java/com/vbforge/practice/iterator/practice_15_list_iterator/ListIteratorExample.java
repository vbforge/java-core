package com.vbforge.practice.iterator.practice_15_list_iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Demonstrates ListIterator features including bidirectional traversal, add, set, and remove.
 *
 * Overview
 * ListIterator<E> is a subinterface of Iterator<E> and is used to iterate bidirectionally (forward and backward) through a List.
 * Can also add, replace, and remove elements during iteration.
 * Available only for List-based collections (like ArrayList, LinkedList), not for Set or Queue.
 * */
public class ListIteratorExample {
    public static void main(String[] args) {

        /**
         * Methods:
         *  - boolean hasNext();
         *  - E next();
         *  - boolean hasPrevious();
         *  - E previous();
         *  - int nextIndex();
         *  - int previousIndex();
         *  - void remove();
         *  - void set(E e);     // replaces last element returned
         *  - void add(E e);     // inserts before next()
         * */
        /*Key Operations
        Operation	Description
        set(E e)	Replaces the last element returned by next() or previous()
        add(E e)	Adds element before the next element to be returned
        remove()	Removes last element returned by next() or previous()*/

        List<String> animals = new ArrayList<>();
        animals.add("dog");
        animals.add("cat");
        animals.add("mouse");

        ListIterator<String> iterator = animals.listIterator();

        System.out.println("Forward iteration:");
        while (iterator.hasNext()) {
            System.out.println("[" + iterator.nextIndex() + "] " + iterator.next());
        }

        System.out.println("\nBackward iteration:");
        while (iterator.hasPrevious()) {
            System.out.println("[" + iterator.previousIndex() + "] " + iterator.previous());
        }

        System.out.println("\nModify during iteration:");
        while (iterator.hasNext()) {
            String current = iterator.next();
            if (current.equals("cat")) {
                iterator.set("lion"); // replaces "cat" with "lion"
                System.out.println(" * replaces 'cat' with 'lion'");
            } else if (current.equals("mouse")) {
                iterator.add("elephant"); // inserts after "mouse"
                System.out.println(" * if 'mouse exist' inserts 'elephant' after 'mouse'");
            }
        }

        System.out.println("\nFinal list:");
        for (String animal : animals) {
            System.out.println(animal);
        }



    }
}
