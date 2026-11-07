package com.vbforge.practice.iterator.practice_14_iterable_and_iterator_basics;

import java.util.*;

/**
 * A custom class that implements Iterable interface.
 * Iterable<T> is the root interface for all collections that can be iterated using the enhanced for-loop (for-each).
 * It provides only one method:
 * Iterator<T> iterator();
 *
 * Key Points
 * Implements Iterable<T>: Enables use in for-each loop.
 * Custom Iterator handles traversal.
 * Default remove() not supported unless explicitly implemented.
 */
public class SimpleIterableExample implements Iterable<String> {

    private final List<String> data = new ArrayList<>(Arrays.asList("apple", "banana", "cherry"));

    @Override
    public Iterator<String> iterator() {
        return new ArrayIterator();
    }

    /**
     * Basic iterator that does not support removal.
     */
    private class ArrayIterator implements Iterator<String> {
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < data.size();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return data.get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported in this example");
        }
    }

    /**
     * Removable iterator that supports removal during iteration.
     */
    private class RemovableIterator implements Iterator<String> {
        private int index = 0;
        private boolean canRemove = false;

        @Override
        public boolean hasNext() {
            return index < data.size();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            canRemove = true;
            return data.get(index++);
        }

        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException("Call next() before remove()");
            }
            data.remove(--index);
            canRemove = false;
            System.out.print("Removed successfully: ");
        }
    }

    public static void main(String[] args) {
        // Instantiate the class
        SimpleIterableExample iterable = new SimpleIterableExample();

        System.out.println("1. Iterate using for-each loop: ");
        // Iterate using for-each loop
        for (String fruit : iterable) {
            System.out.print(fruit + " ");
        }
        System.out.println();

        System.out.println("\n2. Use the custom ArrayIterator explicitly: ");
        // Use the custom ArrayIterator explicitly
        Iterator<String> it = iterable.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();

        // If needed, we can also create and use the RemovableIterator directly
        // For example purposes, here's how to use it:
        System.out.println("\n 3. Using RemovableIterator:");

        //creating like inner class, because we already in use iterator() method for ArrayIterator
        Iterator<String> removableIt = iterable.new RemovableIterator();
        while (removableIt.hasNext()) {
            String val = removableIt.next();
            System.out.println("This element is next: " + val);
            if (val.equals("banana")) {
                System.out.println("Chosen to remove: " + val);
                removableIt.remove(); // Remove 'banana'
                System.out.println(val);
            }
        }

        // Verify the list after removal
        System.out.println("\n 4. List after removal:");
        for (String fruit : iterable) {
            System.out.print(fruit + " ");
        }
    }
}
